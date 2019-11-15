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

import com.alibaba.fastjson.JSONArray;

import etl.dispatch.boot.abstracts.ServiceAbstract;
import etl.dispatch.boot.entity.ConfRelyGroup;
import etl.dispatch.boot.response.ResponseCommand;
import etl.dispatch.boot.response.VisitsResult;
import etl.dispatch.boot.service.IConfRelyGroup;
import etl.dispatch.config.holder.EtlConfRelyGroupCacheHolder;
import etl.dispatch.util.NewMapUtil;

/**
 * <p>
 * 存储各个任务组之间依赖配置
 * </p>
 *
 *
 * @since 2017-08-14
 */
@RestController
@RequestMapping("confRelyGroup")
public class ConfRelyGroupController extends ServiceAbstract {
	@Autowired
	private IConfRelyGroup iConfRelyGroup;

	@GetMapping
	public Object selectList(@RequestParam(value = "classifyId", required = true) Integer classifyId) {
		if(classifyId == null || classifyId<=0)classifyId = 1;
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(iConfRelyGroup.selectGroup(classifyId)));
	}

	@PostMapping
	public Object insert(String entityList, HttpServletRequest request) {
		if (entityList == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "添加为空！").get()));
		}
		List<ConfRelyGroup> list = JSONArray.parseArray(entityList, ConfRelyGroup.class);
		if (list.isEmpty()) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "添加为空！").get()));
		}
		try {
			EtlConfRelyGroupCacheHolder.refreshCache();
			return iConfRelyGroup.createConfRelyGroup(list, super.getUser(request).getName());
		} catch (Exception e) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", e.getMessage()).get()));
		}

	}

	/**
	 * 修改
	 * 
	 * @param entity
	 * @param request
	 * @return
	 */
	@PutMapping
	public Object update(ConfRelyGroup entity, HttpServletRequest request) {
		if (entity.getGroupId() == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "任务分组id不能为空").get()));
		}
		if (entity.getRelygroupId() == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "依赖任务组id不能为空").get()));
		}
		entity.setUpdateUser(super.getUser(request).getName());
		entity.setUpdateTime(new Date());
		boolean bool = entity.updateById();
		if (bool) {
			EtlConfRelyGroupCacheHolder.refreshCache();
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
		if(id==null||id==0){
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "请传入正确的ID").get()));
		}
		List<ConfRelyGroup> list = iConfRelyGroup.isNext(id);
		if (!list.isEmpty()) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "有子级不能删除！").get()));
		}
		boolean bool = false;
		try {
			bool = iConfRelyGroup.deleteConfRelyGroup(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bool) {
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(bool));
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "删除失败").get()));
	}

}
