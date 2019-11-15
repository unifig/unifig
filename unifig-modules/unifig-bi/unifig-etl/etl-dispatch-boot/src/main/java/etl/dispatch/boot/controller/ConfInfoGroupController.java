package etl.dispatch.boot.controller;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;

import etl.dispatch.boot.abstracts.ServiceAbstract;
import etl.dispatch.boot.entity.ConfInfoGroup;
import etl.dispatch.boot.entity.ConfRelyGroup;
import etl.dispatch.boot.enums.StatusEnum;
import etl.dispatch.boot.response.ResponseCommand;
import etl.dispatch.boot.response.ResultPage;
import etl.dispatch.boot.response.VisitsResult;
import etl.dispatch.boot.service.IConfInfoGroup;
import etl.dispatch.boot.service.IConfRelyGroup;
import etl.dispatch.config.holder.EtlConfInfoGroupCacheHolder;
import etl.dispatch.config.holder.EtlConfRelyGroupCacheHolder;
import etl.dispatch.config.holder.EtlConfRelyTasksCacheHolder;
import etl.dispatch.quartz.holder.QuartzMangerHolder;
import etl.dispatch.util.NewMapUtil;
import etl.dispatch.util.convert.MapConvertUtil;

/**
 * <p>
 * 存储任务分组配置
 * </p>
 *
 *
 * @since 2017-08-14
 */
@RestController
@RequestMapping("group")
public class ConfInfoGroupController extends ServiceAbstract {
	@Autowired
	private IConfInfoGroup iConfInfoGroup;
	
	@Autowired
	private IConfRelyGroup iConfRelyGroup;
	
	/**
	 * (OK)
	 * @author: ylc
	 */
	@GetMapping("selectRely")
	public Object selectRely(@RequestParam(value = "classifyId", required = true) Integer classifyId) {
		if (classifyId == null || classifyId <= 0)
			classifyId = 1;
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(iConfInfoGroup.selectRely(classifyId)));
	}
	/**
	 * (OK)
	 * @author: ylc
	 */
	@GetMapping(value="rerun")
	public Object rerun(@RequestParam(value = "groupId", required = true) Integer groupId) {
		if (groupId == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "groupId不能为空").get()));
		}
		ConfInfoGroup confInfoGroup = iConfInfoGroup.selectById(groupId);
		if (confInfoGroup == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "任务不存在！").get()));
		}
		iConfInfoGroup.restart(confInfoGroup);
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, true);
	}
	
	/**
	 * (OK)
	 * @Title: handle 
	 * @Description: 修改任务组状态，停用，启用
	 * @param groupId
	 * @param handle 1:启用任务 0:停用任务
	 * @return
	 * @return: Object
	 */
	@GetMapping(value="handle")
	public Object handle(@RequestParam(value = "groupId", required = true) Integer groupId, @RequestParam(value = "handle", required = true) Integer handle) {
		if (groupId == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "groupId不能为空").get()));
		}
		if (handle == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "handle不能为空").get()));
		}
		ConfInfoGroup confInfoGroup = iConfInfoGroup.selectById(groupId);
		if (confInfoGroup == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "任务不存在！").get()));
		}
		iConfInfoGroup.handle(confInfoGroup, handle);
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, true);
	}
	/**
	 * 查询任务组列表
	 * (OK)
	 * @param id
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	public Object selectList(@RequestParam(value = "id", required = false) Integer id) {
		List<ConfInfoGroup> list = null;
		if (id == null) {
			list = iConfInfoGroup.selectList(new EntityWrapper<ConfInfoGroup>().where("status!={0}", StatusEnum.DELETED.getCode()).groupBy("pk_id desc"));
		} else {
			list = iConfInfoGroup.selectList(new EntityWrapper<ConfInfoGroup>().where("status!={0}", StatusEnum.DELETED.getCode()).and("pk_id={0}", id).groupBy("pk_id desc"));
		}
		List<Map<String, Object>> ls = new ArrayList<>();
		list.forEach(l -> {
			Map<String, Object> map = null;
			try {
				map = MapConvertUtil.convertBeanToMap(l, true);
			} catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
				e.printStackTrace();
			}
			map.put("runningState", 1);
			map.put("runningMessage", "运行成功");
			ls.add(map);
		});
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(ls));
	}
	
	/**
	 * 添加
	 * 
	 * @param entity
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public Object insert(@ModelAttribute ConfInfoGroup entity,@RequestParam(value="fatherId",required = true)String fatherId, HttpServletRequest request) {
		try {
			boolean config=iConfInfoGroup.insertConfInfoGroup(entity, super.getUser(request).getName(),fatherId);
			if(config){
				return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(entity));
			}
		} catch (Exception e) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "添加失败").get()));
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "添加失败").get()));
	}




	/**
	 * 修改
	 * (OK)
	 * @param entity
	 * @param request
	 * @return
	 */
	@PutMapping
	public Object update(@ModelAttribute ConfInfoGroup entity, HttpServletRequest request) {
		try {
			boolean bool = iConfInfoGroup.updateConfInfoGroup(entity, super.getUser(request).getName());
			if (bool) {
				return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(entity));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "修改失败").get()));
	}
	
		/**
		 * (OK)
		 * @author: ylc
		 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}

	
	@GetMapping("Quartz")
	public Object Quartz(@RequestParam Integer id) {
		Boolean quartz = iConfInfoGroup.Quartz(id);
		if (quartz) {
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(quartz));
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "操作失败！").get()));
	}
}
