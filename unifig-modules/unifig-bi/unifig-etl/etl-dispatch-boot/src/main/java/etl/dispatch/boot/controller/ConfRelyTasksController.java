package etl.dispatch.boot.controller;

import java.io.IOException;
import java.util.ArrayList;
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
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import etl.dispatch.boot.abstracts.ServiceAbstract;
import etl.dispatch.boot.entity.ConfRelyTasks;
import etl.dispatch.boot.response.ResponseCommand;
import etl.dispatch.boot.response.VisitsResult;
import etl.dispatch.boot.service.IConfRelyTasks;
import etl.dispatch.config.holder.EtlConfRelyTasksCacheHolder;
import etl.dispatch.util.NewMapUtil;

@RestController
@RequestMapping("confRelyTasks")
public class ConfRelyTasksController extends ServiceAbstract {

	@Autowired
	private IConfRelyTasks iConfRelyTasks;

	@GetMapping
	public Object selectList(@RequestParam(value = "id", required = false) Integer id) {
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS,
				new VisitsResult(iConfRelyTasks.selectConfRelyTasks(id)));
	}
/**
 * (OK)
 * @author: ylc
 */
	@PostMapping
	public Object insert(@RequestParam(value="entityList",required = true)String entityList,@RequestParam(value="fatherId",required = true)String fatherId, HttpServletRequest request)
			throws JsonParseException, JsonMappingException, IOException {
		List<ConfRelyTasks> list = JSONArray.parseArray(entityList, ConfRelyTasks.class);
		if (list.isEmpty()) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR,
					new VisitsResult(new NewMapUtil().set("message", "添加为空！").get()));
		}
		String[] reid=fatherId.split(",");
		List<ConfRelyTasks> ls=new ArrayList<>();
		for (int i=0;i<reid.length ;i++) {
			for (ConfRelyTasks confRelyTasks : list) {
				String relytaskId=reid[i];
				ConfRelyTasks c=new ConfRelyTasks();
				c.setGroupId(	confRelyTasks.getGroupId());
				c.setTasksId(confRelyTasks.getTasksId());
				c.setRelytasksId(relytaskId==null||relytaskId.trim().equals("")?-1:Integer.valueOf(reid[i]));
				ls.add(c);
			}
		}
		try {
			Object object=iConfRelyTasks.updateConfRelyTasks(ls, super.getUser(request).getName());
			EtlConfRelyTasksCacheHolder.refreshCache();
			return object;
		} catch (Exception e) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR,
					new VisitsResult(new NewMapUtil().set("message", e.getMessage()).get()));
		}
	}


	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping
	public Object delete(@RequestParam(required=true) Integer id) {
//		ConfRelyTasks entity = new ConfRelyTasks();
//		entity.setPkId(id);
//		entity.setStatus(StatusEnum.DELETED.getCode());
		List<ConfRelyTasks> list = iConfRelyTasks.isNext(id);
		if (!list.isEmpty()) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR,
					new VisitsResult(new NewMapUtil().set("message", "有子级不能删除！").get()));
		}
		int bool = iConfRelyTasks.updatebyTasksId(id);
		if (bool>0) {
			EtlConfRelyTasksCacheHolder.refreshCache();
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(bool));
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR,
				new VisitsResult(new NewMapUtil().set("message", "删除失败").get()));
	}

}
