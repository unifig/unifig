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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;

import etl.dispatch.boot.abstracts.ServiceAbstract;
import etl.dispatch.boot.entity.ConfInfoShellScript;
import etl.dispatch.boot.entity.ConfInfoTasks;
import etl.dispatch.boot.enums.ScriptType;
import etl.dispatch.boot.enums.StatusEnum;
import etl.dispatch.boot.response.ResponseCommand;
import etl.dispatch.boot.response.ResultPage;
import etl.dispatch.boot.response.VisitsResult;
import etl.dispatch.boot.service.IConfInfoShellScript;
import etl.dispatch.boot.service.IConfInfoTasks;
import etl.dispatch.config.enums.ScriptTypeEnum;
import etl.dispatch.config.holder.EtlConfInfoPythonScriptCacheHolder;
import etl.dispatch.util.NewMapUtil;

/**
 * <p>
 * 存储 shell汇总实现脚本
 * </p>
 *
 * @since 2017-08-14
 */
@RestController
@RequestMapping("shellScript")
public class ConfInfoShellScriptController extends ServiceAbstract {
	@Autowired
	private IConfInfoShellScript iConfInfoShellScript;
	@Autowired
	private IConfInfoTasks iConfInfoTasks;
	@GetMapping
	public Object selectList(@RequestParam(value = "id", required = false) Integer id) {
		List<ConfInfoShellScript> list = null;
		if (id == null) {
			list = iConfInfoShellScript.selectList(new EntityWrapper<ConfInfoShellScript>().where("status!={0}", StatusEnum.DELETED.getCode()).groupBy("pk_id desc"));
		} else {
			list = iConfInfoShellScript.selectList(new EntityWrapper<ConfInfoShellScript>().where("status!={0}", StatusEnum.DELETED.getCode()).and("pk_id={0}", id).groupBy("pk_id desc"));
		}
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(list));
	}

	/**
	 * 分页
	 * @param current
	 * @param size
	 * @return
	 */
	@PostMapping("page")
	public Object selectPage(@RequestParam(required = true) Integer current, @RequestParam(required = true) Integer size, ConfInfoShellScript entity) {
		if (current == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "current must preach").get()));
		}
		if (size == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "size must preach").get()));
		}
		Page<ConfInfoShellScript> page = new Page<>(current, size);
		iConfInfoShellScript.selectPage(page, new EntityWrapper<ConfInfoShellScript>().where("status!={0}", StatusEnum.DELETED.getCode()).like("script_name", entity.getScriptName()).like("script_path", entity.getScriptPath()).like("personal", entity.getPersonal()).groupBy("pk_id desc"));
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new ResultPage(page));
	}

	/**
	 * 添加
	 * 
	 * @param entity
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public Object insert(ConfInfoShellScript entity, HttpServletRequest request) {
		entity.setCreateUser(super.getUser(request).getName());
		entity.setCreateTime(new Date());
		boolean bool = entity.insert();
		if (bool) {
//			EtlPythonScriptInfoCacheHolder.refreshCache();
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(entity));
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "添加失败").get()));
	}

	/**
	 * 修改
	 * 
	 * @param entity
	 * @param request
	 * @return
	 */
	@PutMapping
	public Object update(ConfInfoShellScript entity, HttpServletRequest request) {
		entity.setUpdateUser(super.getUser(request).getName());
		entity.setUpdateTime(new Date());
		boolean bool = entity.updateById();
		if (bool) {
//			EtlPythonScriptInfoCacheHolder.refreshCache();
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(entity));
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "修改失败").get()));
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping
	public Object delete(@RequestParam Integer id) {
		ConfInfoShellScript entity = new ConfInfoShellScript();
		entity.setPkId(id);
		entity.setStatus(StatusEnum.DELETED.getCode());
		List<ConfInfoTasks> list = iConfInfoTasks.selectList(new EntityWrapper<ConfInfoTasks>().where("script_id={0} and script_type={1}", id,ScriptTypeEnum.SHELL.getCode()));
		if (list.size() > 0) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "任务以配置不能删除！！").get()));
		}
		boolean bool = entity.updateById();
		if (bool) {
			EtlConfInfoPythonScriptCacheHolder.refreshCache();
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(bool));
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "删除失败").get()));
	}

}
