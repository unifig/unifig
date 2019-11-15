package etl.dispatch.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * <p>
 * 存储各个任务任务组执行完成标记
 * </p>
 *
 *
 * @since 2017-08-14
 */

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;

import etl.dispatch.boot.entity.SignInfoTasks;
import etl.dispatch.boot.enums.StatusEnum;
import etl.dispatch.boot.response.ResponseCommand;
import etl.dispatch.boot.response.ResultPage;
import etl.dispatch.boot.response.VisitsResult;
import etl.dispatch.boot.service.ISignInfoTasks;
import etl.dispatch.util.NewMapUtil;

@RestController
@RequestMapping("signInfoTasks")
public class SignInfoTasksController {
	@Autowired
	private ISignInfoTasks iSignInfoTasks;

	/**
	 * 分页
	 * @param current
	 * @param size
	 * @return
	 */
	@PostMapping("page")
	public Object selectPage(@RequestParam(required = true) Integer current, @RequestParam(required = true) Integer size, SignInfoTasks entity) {
		if (current == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "current must preach").get()));
		}
		if (size == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "size must preach").get()));
		}
		Page<SignInfoTasks> page = new Page<>(current, size);
		
		if(entity.getTaskName()==null || "".equals(entity.getTaskName())) {
			entity.setTaskName(null);
			iSignInfoTasks.selectPage(page, new EntityWrapper<SignInfoTasks>(entity).orderBy("start_time", false));
		}else {
			String taskName=entity.getTaskName();
			entity.setTaskName(null);
			iSignInfoTasks.selectPage(page, new EntityWrapper<SignInfoTasks>(entity).where("task_name like {0}","%"+ taskName+"%").orderBy("start_time", false));
		}
	
	//.where("status!={0}", StatusEnum.DELETED.getCode()))
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new ResultPage(page));
	}
	
	

}
