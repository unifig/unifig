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

import etl.dispatch.boot.bean.ConfRelyTasksBean;
import etl.dispatch.boot.dao.ConfRelyTasksMapper;
import etl.dispatch.boot.entity.ConfRelyTasks;
import etl.dispatch.boot.response.ResponseCommand;
import etl.dispatch.boot.response.VisitsResult;
import etl.dispatch.boot.service.IConfRelyTasks;
import etl.dispatch.util.NewMapUtil;

/**
 * <p>
 * 存储各个任务之间依赖配置 服务实现类
 * </p>
 *
 *
 * @since 2017-08-14
 */
@Service
public class ConfRelyTasksService extends ServiceImpl<ConfRelyTasksMapper, ConfRelyTasks> implements IConfRelyTasks {
	@Autowired
	private ConfRelyTasksMapper confRelyTasksMapper;

	@Override
	public Object selectConfRelyTasks(Integer id) {
	
		List<Map<String, Object>> list = confRelyTasksMapper.selectTasks(new NewMapUtil().set("id", id).get());
		return list;
	}
	
	
	
	public List<Map<String, Object>> show(ConfRelyTasksBean bean,List<ConfRelyTasksBean> list ){
		List<Map<String, Object>> ls=new ArrayList<>();
		if(bean==null){
			return ls;
		}
		List<ConfRelyTasksBean> s= list.stream().filter(l->l.getRelytasksId()==bean.getTasksId()).collect(Collectors.toList());
		s.forEach(l->{
			Map<String, Object> m=new TreeMap<>();
			List<Map<String, Object>> children= show(l, list);
			if(!children.isEmpty()){
				m.put("children",children);
				m.put("expand",true);
			}
			m.put("title", l.getGroupName());
			m.put("tasksId", l.getTasksId());
			m.put("tasksName", l.getTasksName());
			m.put("relytasksId", l.getRelytasksId());
			m.put("groupId", l.getGroupId());
			m.put("groupName", l.getGroupName());
			ls.add(m);
		});
		return ls;
	}

	@Override
	@Transactional
	public Object updateConfRelyTasks(List<ConfRelyTasks> entityList,String createUser){
		for (ConfRelyTasks entity : entityList) {
			if (entity.getGroupId() == null) {
				  throw new RuntimeException("任务分组id不能为空"); 
			}
			if (entity.getTasksId() == null) {
				  throw new RuntimeException("调度任务id不能为空"); 
			}
			if (entity.getRelytasksId() == null) {
				  throw new RuntimeException("依赖任务id不能为空"); 
			}
			List<ConfRelyTasks> list = confRelyTasksMapper.selectList(
					new EntityWrapper<ConfRelyTasks>().where("group_id={0} and tasks_id={1} and relytasks_id={2} and status!=-1 ",
							entity.getGroupId(), entity.getTasksId(), entity.getRelytasksId()));
			if (list.size() > 0) {
				  throw new RuntimeException("已有配置请重新选择！"); 
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
	@Override
	public List<ConfRelyTasks> isNext( Integer id){
		return confRelyTasksMapper.isNext(id);
	}
	@Override
	public Integer updatebyGroupId(Integer groupId){
		return confRelyTasksMapper.updatebyGroupId(groupId);
	}
	@Override
	public Integer updatebyTasksId(Integer tasksId){
		return confRelyTasksMapper.updatebyTasksId(tasksId);
	}
}
