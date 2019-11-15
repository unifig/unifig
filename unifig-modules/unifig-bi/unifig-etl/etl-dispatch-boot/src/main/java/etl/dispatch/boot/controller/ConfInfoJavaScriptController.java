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
import etl.dispatch.boot.entity.ConfInfoJavaScript;
import etl.dispatch.boot.entity.ConfInfoTasks;
import etl.dispatch.boot.enums.StatusEnum;
import etl.dispatch.boot.response.ResponseCommand;
import etl.dispatch.boot.response.ResultPage;
import etl.dispatch.boot.response.VisitsResult;
import etl.dispatch.boot.service.IConfInfoJavaScript;
import etl.dispatch.boot.service.IConfInfoTasks;
import etl.dispatch.config.enums.ScriptTypeEnum;
import etl.dispatch.config.holder.EtlConfInfoJavaScriptCacheHolder;
import etl.dispatch.util.NewMapUtil;

/**
 * <p>
 * 存储 Java汇总实现脚本
 * </p>
 *
 *
 * @since 2017-08-14
 */
@RestController
@RequestMapping(value = "/javaScript")
public class ConfInfoJavaScriptController extends ServiceAbstract {
	@Autowired
	private IConfInfoJavaScript iConfInfoJavaScript;
	@Autowired
	private IConfInfoTasks iConfInfoTasks;
	@GetMapping
	public Object selectList(@RequestParam(value = "id", required = false) Integer id) {
		List<ConfInfoJavaScript> list = null;
		if (id == null) {
			list = iConfInfoJavaScript.selectList(new EntityWrapper<ConfInfoJavaScript>().where("status!={0}", StatusEnum.DELETED.getCode()).groupBy("pk_id desc"));
		} else {
			list = iConfInfoJavaScript.selectList(new EntityWrapper<ConfInfoJavaScript>().where("status!={0}", StatusEnum.DELETED.getCode()).and("pk_id={0}", id).groupBy("pk_id desc"));
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
	public Object selectPage(@RequestParam(required = true) Integer current, @RequestParam(required = true) Integer size, ConfInfoJavaScript entity) {
		if (current == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "current must preach").get()));
		}
		if (size == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "size must preach").get()));
		}
		Page<ConfInfoJavaScript> page = new Page<>(current, size);
		iConfInfoJavaScript.selectPage(page, new EntityWrapper<ConfInfoJavaScript>().where("status!={0}", StatusEnum.DELETED.getCode()).like("script_name", entity.getScriptName()).like("script_path", entity.getScriptPath()).like("personal", entity.getPersonal()).groupBy("pk_id desc"));
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new ResultPage(page));
	}


	@RequestMapping(method = RequestMethod.POST)
	public Object insert(ConfInfoJavaScript confInfoJavaScript, HttpServletRequest request) {
		if(confInfoJavaScript.getScriptName()==null){
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "脚本名称不能为空").get()));
		}
		confInfoJavaScript.setCreateUser(super.getUser(request).getName());
		confInfoJavaScript.setCreateTime(new Date());
		boolean bool = confInfoJavaScript.insert();
		if (bool) {
			EtlConfInfoJavaScriptCacheHolder.refreshCache();
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(confInfoJavaScript));
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "添加失败").get()));
	}

	@PutMapping
	public Object update(ConfInfoJavaScript confInfoJavaScript, HttpServletRequest request) {
		confInfoJavaScript.setUpdateUser(super.getUser(request).getName());
		confInfoJavaScript.setUpdateTime(new Date());
		boolean bool = confInfoJavaScript.updateById();
		if (bool) {
			EtlConfInfoJavaScriptCacheHolder.refreshCache();
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(confInfoJavaScript));
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "修改失败").get()));
	}

	@DeleteMapping
	public Object delete(@RequestParam Integer id) {
		ConfInfoJavaScript entity = new ConfInfoJavaScript();
		entity.setPkId(id);
		entity.setStatus(StatusEnum.DELETED.getCode());
		List<ConfInfoTasks> list = iConfInfoTasks.selectList(new EntityWrapper<ConfInfoTasks>().where("script_id={0} and script_type={1}", id,ScriptTypeEnum.JAVA.getCode()));
		if (list.size() > 0) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "任务以配置不能删除！！").get()));
		}
		boolean bool = iConfInfoJavaScript.updateById(entity);
		
		if (bool) {
			EtlConfInfoJavaScriptCacheHolder.refreshCache();
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(bool));
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "删除失败").get()));
	}

}
