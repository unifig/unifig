package etl.dispatch.boot.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.boot.dao.ConfInfoGroupMapper;
import etl.dispatch.boot.entity.ConfInfoGroup;
import etl.dispatch.boot.entity.ConfRelyGroup;
import etl.dispatch.boot.service.IConfInfoGroup;
import etl.dispatch.config.api.IEtlConfigApiService;
import etl.dispatch.config.entity.ConfInfoGroupEntity;
import etl.dispatch.config.entity.ConfInfoJavaScriptEntity;
import etl.dispatch.config.entity.ConfInfoPythonScriptEntity;
import etl.dispatch.config.entity.ConfInfoTasksEntity;
import etl.dispatch.config.entity.ConfRelyGroupEntity;
import etl.dispatch.config.entity.ConfRelyTasksEntity;
import etl.dispatch.config.entity.SignInfoTasksEntity;
import etl.dispatch.config.enums.ClassifyEnum;
import etl.dispatch.config.enums.IsSuccessEnum;
import etl.dispatch.config.enums.ScriptTypeEnum;
import etl.dispatch.config.holder.EtlConfInfoGroupCacheHolder;
import etl.dispatch.config.holder.EtlConfRelyGroupCacheHolder;
import etl.dispatch.config.holder.EtlConfInfoJavaScriptCacheHolder;
import etl.dispatch.config.holder.EtlConfInfoPythonScriptCacheHolder;
import etl.dispatch.config.holder.EtlConfInfoTasksCacheHolder;
import etl.dispatch.config.holder.EtlConfRelyTasksCacheHolder;
import etl.dispatch.quartz.holder.QuartzMangerHolder;
import etl.dispatch.quartz.jobs.ScheduledJob;
import etl.dispatch.quartz.notice.QuartzNotice;
import etl.dispatch.script.constant.CommonConstants;
import etl.dispatch.util.DateUtil;
import etl.dispatch.util.Exceptions;
import etl.dispatch.util.StringUtil;
import etl.dispatch.util.helper.ObjectClassHelper;

/**
 * <p>
 * 存储任务分组配置 服务实现类
 * </p>
 *
 *
 * @since 2017-08-14
 */
@Service
public class ConfInfoGroupService extends ServiceImpl<ConfInfoGroupMapper, ConfInfoGroup> implements IConfInfoGroup {
	private static Logger logger = LoggerFactory.getLogger(ConfInfoGroupService.class);
	@Autowired
	private ConfInfoGroupMapper confInfoGroupMapper;
	@Autowired
	private ScheduledJob scheduledJob;
	
	@Override
	public void restart(ConfInfoGroup confInfoGroup) {
		try {
			Date startTime = DateUtil.getDate();
			// 查询Etl调度任务配置
			IEtlConfigApiService etlConfigApiService = SpringContextHolder.getBean("iEtlConfigApiService", IEtlConfigApiService.class);
			ConfInfoGroupEntity confInfoGroupEntity = new ConfInfoGroupEntity();
			confInfoGroupEntity.setCreateTime(confInfoGroup.getCreateTime());
			confInfoGroupEntity.setCreateUser(confInfoGroup.getCreateUser());
			confInfoGroupEntity.setEffectiveEnd(confInfoGroup.getEffectiveEnd());
			confInfoGroupEntity.setEffectiveStart(confInfoGroup.getEffectiveStart());
			confInfoGroupEntity.setGroupName(confInfoGroup.getGroupName());
			confInfoGroupEntity.setPkId(confInfoGroup.getPkId());
			confInfoGroupEntity.setRemark(confInfoGroup.getRemark());
			confInfoGroupEntity.setReportNotice(confInfoGroup.getReportNotice());
			confInfoGroupEntity.setStatus(confInfoGroup.getStatus());
			confInfoGroupEntity.setTasksCron(confInfoGroup.getTasksCron());
			confInfoGroupEntity.setUpdateTime(confInfoGroup.getUpdateTime());
			confInfoGroupEntity.setUpdateUser(confInfoGroup.getUpdateUser());
			Map<String, Object> etlConfigMap = etlConfigApiService.getConfigQuartzData(confInfoGroupEntity);
			if (null != etlConfigMap && !etlConfigMap.isEmpty()) {
				String jobName = String.valueOf(etlConfigMap.get(CommonConstants.PROP_QUARTZ_JOBNAME));
				String jobGroup = StringUtil.isNullOrEmpty(etlConfigMap.get(CommonConstants.PROP_QUARTZ_JOBGROUP)) ? Scheduler.DEFAULT_GROUP : String.valueOf(etlConfigMap.get(CommonConstants.PROP_QUARTZ_JOBGROUP));
				String schedExpres = String.valueOf(etlConfigMap.get(CommonConstants.PROP_QUARTZ_SCHEDEXPRES));
				String jobDesc = String.valueOf(etlConfigMap.get(CommonConstants.PROP_QUARTZ_JOBDESC));
				JobDataMap dataMap = (JobDataMap) etlConfigMap.get(CommonConstants.PROP_QUARTZ_JOBDATAMAP);
				boolean validExpre = org.quartz.CronExpression.isValidExpression(schedExpres);
				if (!validExpre) {
					logger.error("Quartz schedExpress is not correct; etlConfig:" + JSON.toJSONString(etlConfigMap) + "; please contact the administrator!");
				}
				List<Map<String, Object>> relyTasksMapListTmp = (List<Map<String, Object>>) dataMap.get(CommonConstants.PROP_QUARTZ_CONTEXT);
				if (null == relyTasksMapListTmp || relyTasksMapListTmp.isEmpty()) {
					return;
				}
				List<Map<String, Object>> relyTasksMapList = new ArrayList<>();
				relyTasksMapListTmp.forEach(l -> {
					List<Map<String, Object>> rr = relyTasksMapList.stream().filter(ls -> {
						return ls.get("taskId").toString().equals(l.get("taskId").toString());
					}).collect(Collectors.toList());
					if (rr == null || rr.size() <= 0) {
						relyTasksMapList.add(l);
					}
				});
				if (null == relyTasksMapList || relyTasksMapList.isEmpty()) {
					return;
				}
				//删除历史记录
				delHistoryIdValue(String.valueOf(confInfoGroup.getPkId()), ClassifyEnum.GROUP);
				for (Map<String, Object> tasksMap : relyTasksMapList) {
					String groupId = String.valueOf(tasksMap.get(CommonConstants.PROP_TASKS_GROUPID));
					String tasksId = String.valueOf(tasksMap.get(CommonConstants.PROP_TASKS_TASKID));
					delHistoryIdValue(groupId + "_" + tasksId, ClassifyEnum.TASK);
				}
				CountDownLatch countDownLatch = new CountDownLatch(relyTasksMapList.size());
				// 任务结果状态
				List<Boolean> taskStatusList = new ArrayList<>();
				// 线程执行返回
				List<Future<Boolean>> taskFutureList = new ArrayList<>();
				// 线程池对象
                ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("restart-%d").build();
                ThreadPoolExecutor taskScheduledJob = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2,
                                                                                    16,
                                                                                    0L,
                                                                                    TimeUnit.MILLISECONDS,
                                                                                    new LinkedBlockingDeque<>(),
                                                                                    threadFactory,
                                                                                    new ThreadPoolExecutor.AbortPolicy());

                for (Map<String, Object> tasksMap : relyTasksMapList) {
                	Future<Boolean> future = scheduledJob.doTask(taskScheduledJob, tasksMap, countDownLatch);
                	if(future == null) {
                		System.out.print("taskScheduledJob");
                	}
					taskFutureList.add(future);
				}
				for (Future<Boolean> future : taskFutureList) {
					if(future == null) {
						System.out.print("");
					}else {
						taskStatusList.add(future.get());
					}
				}
				countDownLatch.await();
				String message = "Etl调度任务组 执行完毕 ; job Group Config [groupId:" + confInfoGroup.getPkId() + " , groupName:" + confInfoGroup.getGroupName() + "], 涵盖" + relyTasksMapList.size() + "个调度任务";
				if (null != etlConfigApiService && !taskStatusList.contains(false)) {
					SignInfoTasksEntity signInfoTasks = new SignInfoTasksEntity();
					ObjectClassHelper.setFieldValue(signInfoTasks, "classify", ClassifyEnum.GROUP.getCode());
					ObjectClassHelper.setFieldValue(signInfoTasks, "taskId", confInfoGroup.getPkId());
					ObjectClassHelper.setFieldValue(signInfoTasks, "tasksName", confInfoGroup.getGroupName());
					ObjectClassHelper.setFieldValue(signInfoTasks, "timeSign", DateUtil.getSysStrCurrentDate("yyyy-MM-dd"));
					ObjectClassHelper.setFieldValue(signInfoTasks, "startTime", startTime);
					ObjectClassHelper.setFieldValue(signInfoTasks, "endTime", DateUtil.getDate());
					ObjectClassHelper.setFieldValue(signInfoTasks, "isSuccess", IsSuccessEnum.SUCCESS.getCode());
					ObjectClassHelper.setFieldValue(signInfoTasks, "message", message);
					
					etlConfigApiService.saveSignInfoTasks(signInfoTasks);
				} else {
					QuartzNotice.doAlarm(true, confInfoGroup.getReportNotice(), null, message, confInfoGroup.getGroupName());
					SignInfoTasksEntity signInfoTasks = new SignInfoTasksEntity();
					ObjectClassHelper.setFieldValue(signInfoTasks, "classify", ClassifyEnum.GROUP.getCode());
					ObjectClassHelper.setFieldValue(signInfoTasks, "taskId", confInfoGroup.getPkId());
					ObjectClassHelper.setFieldValue(signInfoTasks, "tasksName", confInfoGroup.getGroupName());
					ObjectClassHelper.setFieldValue(signInfoTasks, "timeSign", DateUtil.getSysStrCurrentDate("yyyy-MM-dd"));
					ObjectClassHelper.setFieldValue(signInfoTasks, "startTime", startTime);
					ObjectClassHelper.setFieldValue(signInfoTasks, "endTime", DateUtil.getDate());
					ObjectClassHelper.setFieldValue(signInfoTasks, "isSuccess", IsSuccessEnum.FAIL.getCode());
					ObjectClassHelper.setFieldValue(signInfoTasks, "message", message);
					etlConfigApiService.saveSignInfoTasks(signInfoTasks);
				}
			}
		} catch (Exception e) {
			logger.error("reason for failure:" + Exceptions.getStackTraceAsString(e));
		}
	}
	/**
	 * (OK)
	 */
	@Override
	public void handle(ConfInfoGroup entity, Integer handle) {
		try{
			entity.setStatus(handle);
			boolean config = entity.updateById();
			if (config) {
				EtlConfInfoGroupCacheHolder.refreshCache();
				EtlConfRelyGroupCacheHolder.refreshCache();
				EtlConfRelyTasksCacheHolder.refreshCache();
				String jobName = String.valueOf(entity.getPkId());
				String jobGroup = entity.getGroupName();
				// 启用任务
				if(handle == 1){
					QuartzMangerHolder.getInstance().rescheduleJob(jobName, jobGroup);
				}
				// 停用任务
				if(handle == 0){
					QuartzMangerHolder.getInstance().pauseQuartz(jobName, jobGroup);
				}
			}
		}catch (Exception e) {
			// 创建任务
			this.createQuartz(entity);
		}
	}
	
	@Override
	public List<Map<String,Object>> selectRely(Integer classifyId) {
	return confInfoGroupMapper.selectRely(classifyId);
	}
	
	@Override
	public boolean insertConfInfoGroup(ConfInfoGroup entity, String createUser, String fatherId) throws Exception {
		boolean config = insertConfiginfoGroups(entity, createUser, fatherId);
		if (config) {
			try {
				EtlConfInfoGroupCacheHolder.refreshCache();
				EtlConfRelyGroupCacheHolder.refreshCache();
				EtlConfRelyTasksCacheHolder.refreshCache();
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new RuntimeException("添加失败");
		}
		return true;
	}
	@Transactional
	public boolean insertConfiginfoGroups(ConfInfoGroup entity, String createUser, String fatherId) throws Exception {
		String[] str = fatherId.split(",");
		entity.setPkId(null);
		entity.setCreateUser(createUser);
		entity.setCreateTime(new Date());
		boolean config = entity.insert();
		for (String string : str) {
			ConfRelyGroup confRelyGroup = new ConfRelyGroup();
			confRelyGroup.setGroupId(entity.getPkId());
			confRelyGroup.setRelygroupId(string == null || string.trim().equals("") ? -1 : Integer.valueOf(string));
			confRelyGroup.setStatus(1);
			confRelyGroup.setCreateUser(createUser);
			confRelyGroup.setCreateTime(new Date());
			confRelyGroup.insert();
		}
		return config;
	}
	
	@Override
	public boolean updateConfInfoGroup(ConfInfoGroup entity,String createUser) throws Exception{
		boolean config = updateConfInfoGroups(entity, createUser);
		if (config) {
			EtlConfInfoGroupCacheHolder.refreshCache();
			EtlConfRelyGroupCacheHolder.refreshCache();
			EtlConfRelyTasksCacheHolder.refreshCache();
			return true;
		}
		throw new RuntimeException("修改失败");
	}
	
	@Transactional
	public boolean updateConfInfoGroups(ConfInfoGroup entity, String createUser) {
		ConfInfoGroup tmpEntity=this.selectById(entity.getPkId());
		entity.setUpdateUser(createUser);
		entity.setUpdateTime(new Date());
		boolean config = entity.updateById();
		String jobName = String.valueOf(entity.getPkId());
		QuartzMangerHolder.getInstance().deleteQuartz(jobName, tmpEntity.getGroupName());
		return config;
	}

	/**
	 * 创建定时任务
	 */
	@Override
	public boolean createQuartz(ConfInfoGroup entity) {
		// 查询Etl调度任务配置
		IEtlConfigApiService etlConfigApiService = SpringContextHolder.getBean("iEtlConfigApiService", IEtlConfigApiService.class);
		ConfInfoGroupEntity confInfoGroupEntity = EtlConfInfoGroupCacheHolder.getEtlGroupInfo("pkId", String.valueOf(entity.getPkId()));
		if (null != confInfoGroupEntity) {
			Map<String, Object> etlConfigMap = etlConfigApiService.getConfigQuartzData(confInfoGroupEntity);
			String jobName = String.valueOf(etlConfigMap.get(CommonConstants.PROP_QUARTZ_JOBNAME));
			String jobGroup = StringUtil.isNullOrEmpty(etlConfigMap.get(CommonConstants.PROP_QUARTZ_JOBGROUP)) ? Scheduler.DEFAULT_GROUP : String.valueOf(etlConfigMap.get(CommonConstants.PROP_QUARTZ_JOBGROUP));
			String schedExpres = String.valueOf(etlConfigMap.get(CommonConstants.PROP_QUARTZ_SCHEDEXPRES));
			String jobDesc = String.valueOf(etlConfigMap.get(CommonConstants.PROP_QUARTZ_JOBDESC));
			JobDataMap dataMap = (JobDataMap) etlConfigMap.get(CommonConstants.PROP_QUARTZ_JOBDATAMAP);
			QuartzMangerHolder.getInstance().deleteQuartz(jobName, jobGroup);
			boolean validExpre = org.quartz.CronExpression.isValidExpression(schedExpres);
			if (!validExpre) {
				logger.error("Quartz schedExpress is not correct; etlConfig:" + JSON.toJSONString(etlConfigMap) + "; please contact the administrator!");
				return false;
			}
			QuartzMangerHolder.getInstance().createQuartz(jobName, jobGroup, ScheduledJob.class, schedExpres, jobDesc, dataMap);
			logger.info("定时任务" + jobGroup + "配置成功！");
		}
		return true;
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
	 * 删除历史运行记录
	 * 
	 * @return
	 */
	public static boolean delHistoryIdValue(String groupOrTaskId, ClassifyEnum classifyEnum) {
		IEtlConfigApiService etlConfigApiService = SpringContextHolder.getBean("iEtlConfigApiService", IEtlConfigApiService.class);
		if (null != etlConfigApiService) {
			SignInfoTasksEntity signInfoTasks = new SignInfoTasksEntity();
			ObjectClassHelper.setFieldValue(signInfoTasks, "classify", classifyEnum.getCode());
			ObjectClassHelper.setFieldValue(signInfoTasks, "taskId", groupOrTaskId);
			ObjectClassHelper.setFieldValue(signInfoTasks, "timeSign", DateUtil.getSysStrCurrentDate("yyyy-MM-dd"));
			etlConfigApiService.deleteSignInfoTasks(signInfoTasks);
		}
		return true;
	}

	
	@Override
	public Boolean Quartz(Integer id) {
		ConfInfoGroup selectById = confInfoGroupMapper.selectById(id);
		 return this.createQuartz(selectById);
	}
}
