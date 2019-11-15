package etl.dispatch.boot.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import etl.dispatch.boot.bean.GroupBean;
import etl.dispatch.boot.dao.ConfRelyGroupMapper;
import etl.dispatch.boot.entity.ConfInfoGroup;
import etl.dispatch.boot.entity.ConfRelyGroup;
import etl.dispatch.boot.entity.ConfRelyTasks;
import etl.dispatch.boot.enums.StatusEnum;
import etl.dispatch.boot.response.ResponseCommand;
import etl.dispatch.boot.response.VisitsResult;
import etl.dispatch.boot.service.IConfRelyGroup;
import etl.dispatch.config.entity.ConfInfoGroupEntity;
import etl.dispatch.config.holder.EtlConfInfoGroupCacheHolder;
import etl.dispatch.config.holder.EtlConfRelyGroupCacheHolder;
import etl.dispatch.quartz.holder.QuartzMangerHolder;
import etl.dispatch.util.NewMapUtil;

/**
 * <p>
 * 存储各个任务组之间依赖配置 服务实现类
 * </p>
 *
 *
 * @since 2017-08-14
 */
@Service
public class ConfRelyGroupService extends ServiceImpl<ConfRelyGroupMapper, ConfRelyGroup> implements IConfRelyGroup {
	@Autowired
	private ConfRelyGroupMapper confRelyGroupMapper;
	@Autowired
	private ConfRelyTasksService confRelyTasksService;
	@Autowired
	private ConfInfoGroupService confInfoGroupService;

	@Override
	public Object selectGroup(Integer classifyId) {
		if(classifyId == null || classifyId <= 0){
			classifyId = 1;
		}
		List<GroupBean> list = confRelyGroupMapper.selectGroup(classifyId);
		return list;
	}

	public List<Map<String, Object>> show(GroupBean bean, List<GroupBean> list) {
		List<Map<String, Object>> ls = new ArrayList<>();
		if (bean == null) {
			return ls;
		}
		List<GroupBean> s = list.stream().filter(l -> l.getRelygroupId() == bean.getPkId()).collect(Collectors.toList());
		s.forEach(l -> {
			Map<String, Object> m = new TreeMap<>();
			List<Map<String, Object>> children = show(l, list);
			if (!children.isEmpty()) {
				m.put("children", children);
				m.put("expand", true);
			}
			m.put("pkId", l.getPkId());
			m.put("title", l.getGroupName());
			m.put("groupName", l.getGroupName());
			m.put("relygroupId", l.getRelygroupId());
			m.put("relygroupId1", l.getRelygroupId1());
			m.put("relygroupName", l.getRelygroupName());
			ls.add(m);
		});
		return ls;
	}

	@Override
	public List<ConfRelyGroup> isNext(Integer id) {
		return confRelyGroupMapper.isNext(id);
	}

	@Override
	@Transactional
	public Object createConfRelyGroup(List<ConfRelyGroup> entityList, String createUser) {
		for (ConfRelyGroup entity : entityList) {
			if (entity.getGroupId() == null) {
				return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "任务分组id不能为空").get()));
			}
			if (entity.getRelygroupId() == null) {
				return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "依赖任务组id不能为空").get()));
			}
			List<ConfRelyGroup> list = confRelyGroupMapper.selectList(new EntityWrapper<ConfRelyGroup>().where("group_id={0} and relygroup_id={1} and status!=-1 ", entity.getGroupId(), entity.getRelygroupId()));
			if (list.size() > 0) {
				return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "已有配置请重新选择！").get()));
			}
			entity.setCreateUser(createUser);
			entity.setCreateTime(new Date());
			boolean bool = entity.insert();
			if (!bool) {
				throw new RuntimeException("添加失败");
			}
		}
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(entityList));
	}

	/**
	 * 删除 同时删除关系
	 */
	@Override
	public boolean deleteConfRelyGroup(Integer groupId) throws Exception {
		ConfInfoGroupEntity confInfoGroup = EtlConfInfoGroupCacheHolder.getEtlGroupInfo("pkId", String.valueOf(groupId));
		boolean boolgroup = deleteConfRelyGroups(groupId);
		if (boolgroup) {
			EtlConfRelyGroupCacheHolder.refreshCache();
		}
		QuartzMangerHolder.getInstance().deleteQuartz(String.valueOf(confInfoGroup.getPkId()), confInfoGroup.getGroupName());
		return true;
	}
	
	@Transactional
	private boolean deleteConfRelyGroups(Integer groupId) {
		Integer relyGroup = confRelyGroupMapper.updatebyGroupId(groupId);
		Integer relyTasks = confRelyTasksService.updatebyGroupId(groupId);
		ConfInfoGroup group = new ConfInfoGroup();
		group.setPkId(groupId);
		group.setStatus(StatusEnum.DELETED.getCode());
		boolean boolgroup = confInfoGroupService.updateById(group);
		return boolgroup;
	}

}
