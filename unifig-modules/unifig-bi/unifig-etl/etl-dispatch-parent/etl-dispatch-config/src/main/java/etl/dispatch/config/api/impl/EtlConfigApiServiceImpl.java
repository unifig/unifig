package etl.dispatch.config.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import etl.dispatch.config.api.IEtlConfigApiService;
import etl.dispatch.config.entity.ConfInfoGroupEntity;
import etl.dispatch.config.entity.ConfInfoJavaScriptEntity;
import etl.dispatch.config.entity.ConfInfoPythonScriptEntity;
import etl.dispatch.config.entity.ConfInfoTasksEntity;
import etl.dispatch.config.entity.ConfRelyGroupEntity;
import etl.dispatch.config.entity.ConfRelyTasksEntity;
import etl.dispatch.config.entity.SignInfoTasksEntity;
import etl.dispatch.config.enums.ScriptTypeEnum;
import etl.dispatch.config.holder.EtlConfInfoGroupCacheHolder;
import etl.dispatch.config.holder.EtlConfInfoJavaScriptCacheHolder;
import etl.dispatch.config.holder.EtlConfInfoPythonScriptCacheHolder;
import etl.dispatch.config.holder.EtlConfInfoTasksCacheHolder;
import etl.dispatch.config.holder.EtlConfRelyGroupCacheHolder;
import etl.dispatch.config.holder.EtlConfRelyTasksCacheHolder;
import etl.dispatch.config.service.ISignInfoTasksService;
import etl.dispatch.script.constant.CommonConstants;

/**
 * 依赖关系说明：
 * 
 * 任务组依赖 ：一个任务组不可以同时配置依赖2个
 * 父级都是-1的存在（即，依赖的group不允许重复依赖）；一个任务组可以依赖多个任务组，一个任务又可以是多个任务组的父级
 * 组内任务依赖：一个任务不可以同时配置依赖2个
 * 父级都是-1的存在（即，依赖的task不允许重复依赖）；一个任务可以依赖多个任务，一个任务又可以是多个任务的父级，任务组内，不允许出现重复的任务配置，例如
 * 首节点-1的任务组A，被A1，A2依赖，A1被A11，A12依赖，则A11依赖A1，也可同时依赖A2， 场景一：
 * 一个任务在多个任务组内出现（不允许，会出现重复调用，除非2个任务组有紧依赖关系，不然平行的任务组；
 * 不允许同一时间存在2个相同的任务，可以将共同的依赖尽量抽取单独的组，然后一个任务组同一时间只能允许一次，不允许重复运行，会出现数据问题）
 * 任务组依赖后，任务组内的任务不需要依赖
 * 
 *
 *
 */
@Service("etlConfigApiService")
public class EtlConfigApiServiceImpl implements IEtlConfigApiService {
	private static Logger logger = LoggerFactory.getLogger(EtlConfigApiServiceImpl.class);

	@Autowired
	private ISignInfoTasksService signInfoTasksService;

	@Override
	public void saveSignInfoTasks(SignInfoTasksEntity signInfoTasks) {
		this.signInfoTasksService.saveSignInfoTasks(signInfoTasks);
	}

	@Override
	public void deleteSignInfoTasks(SignInfoTasksEntity signInfoTasks) {
		this.signInfoTasksService.deleteSignInfoTasks(signInfoTasks);
	}

	@Override
	public List<SignInfoTasksEntity> findSignInfoTasks(SignInfoTasksEntity signInfoTasks) {
		return this.signInfoTasksService.findSignInfoTasks(signInfoTasks);
	}

	@Override
	public List<Map<String, Object>> getEtlConfigQuartzData() {
		List<ConfInfoGroupEntity> confInfoGroupList = EtlConfInfoGroupCacheHolder.getAllEtlGroupInfos();
		if (null == confInfoGroupList || confInfoGroupList.isEmpty()) {
			return null;
		}
		List<Map<String, Object>> etlConfigMapList = new ArrayList<Map<String, Object>>();
		for (ConfInfoGroupEntity confInfoGroup : confInfoGroupList) {
			Map<String, Object> etlConfigMap = new HashMap<String, Object>();
			etlConfigMap.put(CommonConstants.PROP_QUARTZ_JOBNAME, confInfoGroup.getPkId());
			etlConfigMap.put(CommonConstants.PROP_QUARTZ_JOBGROUP, confInfoGroup.getGroupName());
			etlConfigMap.put(CommonConstants.PROP_QUARTZ_SCHEDEXPRES, confInfoGroup.getTasksCron());
			etlConfigMap.put(CommonConstants.PROP_QUARTZ_JOBDESC, confInfoGroup.getRemark());
			// 查询当前任务组依赖的任务组
			Set<Integer> allRelyGroupId = this.getAllRelyGroupIds(confInfoGroup.getPkId());
			// 查询哪些任务组依赖当前任务组
			Set<Integer> allChildGroupId = this.getAllChildGroupIds(confInfoGroup.getPkId());
			// 查询任务组下任务依赖关系
			List<ConfRelyTasksEntity> confRelyTasksList = EtlConfRelyTasksCacheHolder.getEtlTasksRelys("groupId", String.valueOf(confInfoGroup.getPkId()));
			List<Map<String, Object>> relyTasksMapList = new ArrayList<Map<String, Object>>();
			if (null != confRelyTasksList && !confRelyTasksList.isEmpty()) {
				for (ConfRelyTasksEntity confRelyTasks : confRelyTasksList) {
					// 解读任务调度依赖
					Map<String, Object> relyTasksMap = this.getRelyTasksMap(confInfoGroup.getPkId(), confRelyTasks, allRelyGroupId, allChildGroupId);
					if (null != relyTasksMap && !relyTasksMap.isEmpty()) {
						relyTasksMapList.add(relyTasksMap);
					} else {
						logger.error("! warning Etl config is error; Etl Task No rely task configuration; " + confRelyTasks.toString());
					}
				}
			} else {
				logger.error("! warning Etl config is error; Etl Task Group have No task configuration; " + confInfoGroup.toString());
			}
			// 设置 JobDataMap参数
			JobDataMap dataMap = new JobDataMap();
			dataMap.put(CommonConstants.PROP_QUARTZ_CONTEXT, relyTasksMapList);
			dataMap.put(CommonConstants.PROP_GROUP_GROUPID, confInfoGroup.getPkId());
			dataMap.put(CommonConstants.PROP_GROUP_GROUPNAME, confInfoGroup.getGroupName());
			dataMap.put(CommonConstants.PROP_GROUP_REPTNOTICE, confInfoGroup.getReportNotice());
			etlConfigMap.put(CommonConstants.PROP_QUARTZ_JOBDATAMAP, dataMap);
			etlConfigMapList.add(etlConfigMap);
		}
		return etlConfigMapList;
	}
	
	@Override
	public Map<String, Object> getConfigQuartzData(ConfInfoGroupEntity confInfoGroup) {
		Map<String, Object> etlConfigMap = new HashMap<String, Object>();
		etlConfigMap.put(CommonConstants.PROP_QUARTZ_JOBNAME, confInfoGroup.getPkId());
		etlConfigMap.put(CommonConstants.PROP_QUARTZ_JOBGROUP, confInfoGroup.getGroupName());
		etlConfigMap.put(CommonConstants.PROP_QUARTZ_SCHEDEXPRES, confInfoGroup.getTasksCron());
		etlConfigMap.put(CommonConstants.PROP_QUARTZ_JOBDESC, confInfoGroup.getRemark());
		// 查询当前任务组依赖的任务组
		Set<Integer> allRelyGroupId = this.getAllRelyGroupIds(confInfoGroup.getPkId());
		// 查询哪些任务组依赖当前任务组
		Set<Integer> allChildGroupId = this.getAllChildGroupIds(confInfoGroup.getPkId());
		// 查询任务组下任务依赖关系
		List<ConfRelyTasksEntity> confRelyTasksList = EtlConfRelyTasksCacheHolder.getEtlTasksRelys("groupId", String.valueOf(confInfoGroup.getPkId()));
		List<Map<String, Object>> relyTasksMapList = new ArrayList<Map<String, Object>>();
		if (null != confRelyTasksList && !confRelyTasksList.isEmpty()) {
			for (ConfRelyTasksEntity confRelyTasks : confRelyTasksList) {
				// 解读任务调度依赖
				Map<String, Object> relyTasksMap = this.getRelyTasksMap(confInfoGroup.getPkId(), confRelyTasks, allRelyGroupId, allChildGroupId);
				if (null != relyTasksMap && !relyTasksMap.isEmpty()) {
					relyTasksMapList.add(relyTasksMap);
				} else {
					logger.error("! warning Etl config is error; Etl Task No rely task configuration; " + confRelyTasks.toString());
				}
			}
		} else {
			logger.error("! warning Etl config is error; Etl Task Group have No task configuration; " + confInfoGroup.toString());
		}
		// 设置 JobDataMap参数
		JobDataMap dataMap = new JobDataMap();
		dataMap.put(CommonConstants.PROP_QUARTZ_CONTEXT, relyTasksMapList);
		dataMap.put(CommonConstants.PROP_GROUP_GROUPID, confInfoGroup.getPkId());
		dataMap.put(CommonConstants.PROP_GROUP_GROUPNAME, confInfoGroup.getGroupName());
		dataMap.put(CommonConstants.PROP_GROUP_REPTNOTICE, confInfoGroup.getReportNotice());
		etlConfigMap.put(CommonConstants.PROP_QUARTZ_JOBDATAMAP, dataMap);
		return etlConfigMap;
	}
	
	
	/**
	 * 查询当前任务组依赖的任务组
	 * 
	 * @param groupId
	 * @return
	 */
	private Set<Integer> getAllRelyGroupIds(int groupId) {
		List<ConfRelyGroupEntity> allRelyGroupList = EtlConfRelyGroupCacheHolder.getEtlGroupRelys("groupId", String.valueOf(groupId));
		if (null != allRelyGroupList && !allRelyGroupList.isEmpty()) {
			Set<Integer> allRelyGroupId = new HashSet<Integer>();
			for (ConfRelyGroupEntity allRelyGroup : allRelyGroupList) {
				allRelyGroupId.add(allRelyGroup.getRelyGroupId());
			}
			return allRelyGroupId;
		}
		return null;
	}

	/**
	 * 查询哪些任务组依赖当前任务组
	 * 
	 * @param groupId
	 * @return
	 */
	private Set<Integer> getAllChildGroupIds(int groupId) {
		List<ConfRelyGroupEntity> allChildGroupList = EtlConfRelyGroupCacheHolder.getEtlGroupRelys("relyGroupId", String.valueOf(groupId));
		if (null != allChildGroupList && !allChildGroupList.isEmpty()) {
			Set<Integer> allChildGroupId = new HashSet<Integer>();
			for (ConfRelyGroupEntity allChildGroup : allChildGroupList) {
				allChildGroupId.add(allChildGroup.getGroupId());
			}
			return allChildGroupId;
		}
		return null;
	}

	/**
	 * 查询当前任务依赖的任务
	 * 
	 * @param tasksId
	 * @return
	 */
	private Set<Integer> getAllRelyTasksId(int groupId, int tasksId) {
		List<ConfRelyTasksEntity> allRelyTasksList = EtlConfRelyTasksCacheHolder.getEtlTasksRelys("tasksId", String.valueOf(tasksId));
		if (null != allRelyTasksList && !allRelyTasksList.isEmpty()) {
			Set<Integer> allRelyTasksId = new HashSet<Integer>();
			for (ConfRelyTasksEntity allRelyTasks : allRelyTasksList) {
				if (allRelyTasks.getGroupId() == groupId) {
					allRelyTasksId.add(allRelyTasks.getRelytasksId());
				}
			}
			return allRelyTasksId;
		}
		return null;
	}

	/**
	 * 查询哪些任务赖当前任务
	 * 
	 * @param tasksId
	 * @return
	 */
	private Set<Integer> getAllChildTasksId(int groupId, int tasksId) {
		List<ConfRelyTasksEntity> allChildTasksList = EtlConfRelyTasksCacheHolder.getEtlTasksRelys("relytasksId", String.valueOf(tasksId));
		if (null != allChildTasksList && !allChildTasksList.isEmpty()) {
			Set<Integer> allChildTasksId = new HashSet<Integer>();
			for (ConfRelyTasksEntity allChildTasks : allChildTasksList) {
				if (allChildTasks.getGroupId() == groupId) {
					allChildTasksId.add(allChildTasks.getTasksId());
				}
			}
			return allChildTasksId;
		}
		return null;
	}

	/**
	 * 解读任务调度依赖
	 * 
	 * @param groupId
	 * @param confRelyTasks
	 * @return
	 */
	private Map<String, Object> getRelyTasksMap(int groupId, ConfRelyTasksEntity confRelyTasks, Set<Integer> allRelyGroupId, Set<Integer> allChildGroupId) {
		Map<String, Object> relyTasksMap = new HashMap<String, Object>();
		relyTasksMap.put(CommonConstants.PROP_TASKS_GROUPID, confRelyTasks.getGroupId());
		relyTasksMap.put(CommonConstants.PROP_TASKS_TASKID, confRelyTasks.getTasksId());
		relyTasksMap.put(CommonConstants.PROP_TASKS_RELYTASKID, confRelyTasks.getRelytasksId());
		// 查询当前任务组依赖的任务组
		if (null != allRelyGroupId && !allRelyGroupId.isEmpty()) {
			relyTasksMap.put(CommonConstants.PROP_GROUP_AllRELYGROUPID, StringUtils.join(allRelyGroupId.toArray(), ","));
		}
		// 查询哪些任务组依赖当前任务组
		if (null != allChildGroupId && !allChildGroupId.isEmpty()) {
			relyTasksMap.put(CommonConstants.PROP_GROUP_AllCHILDGROUPID, StringUtils.join(allChildGroupId.toArray(), ","));
		}
		// 查询当前任务依赖的任务
		Set<Integer> allRelyTasksId = this.getAllRelyTasksId(groupId, confRelyTasks.getTasksId());
		if (null != allRelyTasksId && !allRelyTasksId.isEmpty()) {
			relyTasksMap.put(CommonConstants.PROP_TASKS_AllRELYTASKID, StringUtils.join(allRelyTasksId.toArray(), ","));
		}
		// 查询哪些任务赖当前任务
		Set<Integer> allChildTasksId = this.getAllChildTasksId(groupId, confRelyTasks.getTasksId());
		if (null != allChildTasksId && !allChildTasksId.isEmpty()) {
			relyTasksMap.put(CommonConstants.PROP_TASKS_AllCHILDTASKID, StringUtils.join(allChildTasksId.toArray(), ","));
		}
		// 查询调度任务配置
		ConfInfoTasksEntity confInfoTasks = EtlConfInfoTasksCacheHolder.getEtlTasksInfo("pkId", String.valueOf(confRelyTasks.getTasksId()));
		if (null != confInfoTasks) {
			relyTasksMap.put(CommonConstants.PROP_TASKS_TASKNAME, confInfoTasks.getTasksName());
			relyTasksMap.put(CommonConstants.PROP_TASKS_TAKEEVAL, confInfoTasks.getTakeEval());
			relyTasksMap.put(CommonConstants.PROP_TASKS_ALARMNOTICE, confInfoTasks.getAlarmNotice());

			if (ScriptTypeEnum.PYTHON.getCode() == confInfoTasks.getScriptType()) {
				// 查询任务Python脚本
				ConfInfoPythonScriptEntity confInfoPythonScript = EtlConfInfoPythonScriptCacheHolder.getEtlPythonScriptInfo("pkId", String.valueOf(confInfoTasks.getScriptId()));
				if (null != confInfoPythonScript) {
					relyTasksMap.put(CommonConstants.PROP_SCRIPT_SCRIPTTYPE, ScriptTypeEnum.PYTHON.getCode());
					relyTasksMap.put(CommonConstants.PROP_SCRIPT_SCRIPTID, confInfoPythonScript.getPkId());
					relyTasksMap.put(CommonConstants.PROP_SCRIPT_SCRIPTNAME, confInfoPythonScript.getScriptName());
					relyTasksMap.put(CommonConstants.PROP_SCRIPT_SCRIPTPATH, confInfoPythonScript.getScriptPath());
					relyTasksMap.put(CommonConstants.PROP_SCRIPT_PRESETPARAM, confInfoPythonScript.getPresetParam());
					relyTasksMap.put(CommonConstants.PROP_SCRIPT_PERSONAL, confInfoPythonScript.getPersonal());
				}
			} else if (ScriptTypeEnum.JAVA.getCode() == confInfoTasks.getScriptType()) {
				// 查询任务Java脚本
				ConfInfoJavaScriptEntity confInfoJavaScript = EtlConfInfoJavaScriptCacheHolder.getEtlJavaScriptInfo("pkId", String.valueOf(confInfoTasks.getScriptId()));
				if (null != confInfoJavaScript) {
					relyTasksMap.put(CommonConstants.PROP_SCRIPT_SCRIPTTYPE, ScriptTypeEnum.JAVA.getCode());
					relyTasksMap.put(CommonConstants.PROP_SCRIPT_SCRIPTID, confInfoJavaScript.getPkId());
					relyTasksMap.put(CommonConstants.PROP_SCRIPT_SCRIPTNAME, confInfoJavaScript.getScriptName());
					relyTasksMap.put(CommonConstants.PROP_SCRIPT_SCRIPTKEY, confInfoJavaScript.getScriptKey());
					relyTasksMap.put(CommonConstants.PROP_SCRIPT_SCRIPTPATH, confInfoJavaScript.getScriptPath());
					relyTasksMap.put(CommonConstants.PROP_SCRIPT_PRESETPARAM, confInfoJavaScript.getPresetParam());
					relyTasksMap.put(CommonConstants.PROP_SCRIPT_PERSONAL, confInfoJavaScript.getPersonal());
				}
			}
		}
		return relyTasksMap;
	}
}
