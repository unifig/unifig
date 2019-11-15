package etl.dispatch.boot.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;

import etl.dispatch.boot.abstracts.ServiceAbstract;
import etl.dispatch.boot.entity.ConfInfoTasks;
import etl.dispatch.boot.entity.ConfRelyTasks;
import etl.dispatch.boot.enums.StatusEnum;
import etl.dispatch.boot.response.ResponseCommand;
import etl.dispatch.boot.response.ResultPage;
import etl.dispatch.boot.response.VisitsResult;
import etl.dispatch.boot.service.IConfInfoTasks;
import etl.dispatch.boot.service.IConfRelyTasks;
import etl.dispatch.config.enums.ScriptTypeEnum;
import etl.dispatch.config.holder.EtlConfInfoTasksCacheHolder;
import etl.dispatch.util.NewMapUtil;

/**
 * <p>
 * 存储各个任务配置
 * </p>
 * 
 *
 * @since 2017-08-14
 */
@RestController
@RequestMapping("tasks")
public class ConfInfoTasksController extends ServiceAbstract {
	@Autowired
	private IConfInfoTasks iConfInfoTasks;
	@Autowired
	private IConfRelyTasks iConfRelyTasks;
	/**
	 * (OK)
	 * @author: ylc
	 */
	@GetMapping("unselected")
	public Object selectLists() {
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(iConfInfoTasks.selectUnselected()));
	}

	private boolean verificScriptType(Integer scriptType) {
		boolean bool = false;
		for (ScriptTypeEnum ScriptTypeEnum : ScriptTypeEnum.values()) {
			if (ScriptTypeEnum.getCode() == scriptType) {
				bool = true;
			}
		}
		return bool;
	}

	/**
	 * (OK)
	 * @author: ylc
	 */
	@PostMapping
	public Object insert(ConfInfoTasks entity, HttpServletRequest request) {
		if (entity.getScriptType() == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "脚本类型不能为空").get()));
		}
		if (!verificScriptType(entity.getScriptType())) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "Please pass in the correct scriptType").get()));
		}

		if (entity.getScriptId() == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "执行脚本id不能为空").get()));
		}
		
		List<ConfInfoTasks> list = iConfInfoTasks.selectList(new EntityWrapper<ConfInfoTasks>().where("script_id={0} and script_type={1}", entity.getScriptId(), entity.getScriptType()));
		if (list.size() > 0) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "已有任务请重新选择！").get()));
		}
		entity.setCreateUser(super.getUser(request).getName());
		entity.setCreateTime(new Date());
		boolean bool = entity.insert();
		if (bool) {
			EtlConfInfoTasksCacheHolder.refreshCache();
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(entity));
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "添加失败").get()));
	}

	/**
	 * 分页(OK)
	 * 
	 * @param current
	 * @param size
	 * @return
	 */
	@PostMapping("page")
	public Object selectPage(@RequestParam(required = true) Integer current, @RequestParam(required = true) Integer size, ConfInfoTasks entity) {
		if (current == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "current must preach").get()));
		}
		if (size == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "size must preach").get()));
		}
		entity.setStatus(null);
		Page page = new Page<>(current, size);
		iConfInfoTasks.page(page,entity);
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new ResultPage(page));
	}

	/**
	 * 修改(OK)
	 * 
	 * @param entity
	 * @param request
	 * @return
	 */
	@PutMapping
	public Object update(ConfInfoTasks entity, HttpServletRequest request) {
		if (entity.getScriptType() == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "脚本类型不能为空").get()));
		}
		if (!verificScriptType(entity.getScriptType())) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "Please pass in the correct scriptType").get()));
		}
		if (entity.getScriptId() == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "执行脚本id不能为空").get()));
		}
		entity.setUpdateUser(super.getUser(request).getName());
		entity.setUpdateTime(new Date());
		boolean bool = entity.updateById();
		if (bool) {
			EtlConfInfoTasksCacheHolder.refreshCache();
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(entity));
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "修改失败").get()));
	}

	/**
	 * 删除(OK)
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping
	public Object delete(@RequestParam Integer id) {
		List<ConfRelyTasks> selectList = iConfRelyTasks.selectList(new EntityWrapper<ConfRelyTasks>().where("status =1 and tasks_id={0}", id));
		if (selectList.size() > 0) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "必须先删除任务组才能删除任务!!!").get()));
		}
		ConfInfoTasks entity = new ConfInfoTasks();
		entity.setPkId(id);
		entity.setStatus(StatusEnum.DELETED.getCode());
		boolean bool = iConfInfoTasks.updateById(entity);
		if (bool) {
			EtlConfInfoTasksCacheHolder.refreshCache();
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(bool));
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "删除失败").get()));
	}

}
