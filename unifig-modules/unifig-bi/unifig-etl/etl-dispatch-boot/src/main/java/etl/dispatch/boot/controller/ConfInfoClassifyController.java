package etl.dispatch.boot.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;

import etl.dispatch.boot.abstracts.ServiceAbstract;
import etl.dispatch.boot.entity.ConfInfoClassify;
import etl.dispatch.boot.entity.ConfInfoGroup;
import etl.dispatch.boot.enums.StatusEnum;
import etl.dispatch.boot.response.ResponseCommand;
import etl.dispatch.boot.response.ResultPage;
import etl.dispatch.boot.response.VisitsResult;
import etl.dispatch.boot.service.IConfInfoClassify;
import etl.dispatch.boot.service.IConfInfoGroup;
import etl.dispatch.util.NewMapUtil;
import etl.dispatch.util.StringUtil;

@RestController
@RequestMapping("/classify")
public class ConfInfoClassifyController extends ServiceAbstract {

	@Autowired
	private IConfInfoClassify confInfoClassify;
	@Autowired
	private IConfInfoGroup confInfoGroup;

	/**
	 * (OK)
	 * @Title: page
	 * @Description: 分页
	 * @param current
	 * @param size
	 * @param entity
	 * @return
	 * @return: Object
	 */
	@PostMapping("page")
	public Object page(@RequestParam(required = true) Integer current, @RequestParam(required = true) Integer size, ConfInfoClassify entity) {
		if (current == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "current must preach").get()));
		}
		if (size == null) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "size must preach").get()));
		}
		entity.setStatus(null);
		Page<ConfInfoClassify> page = new Page<ConfInfoClassify>(current, size);
		confInfoClassify.selectPage(page, new EntityWrapper<ConfInfoClassify>().where("status!={0}", StatusEnum.DELETED.getCode()).like("classify_name", entity.getClassifyName()).groupBy("pk_id desc"));
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new ResultPage(page));
	}
	/**
	 * (OK)
	 * @author: ylc
	 */
	@GetMapping("selectList")
	public Object selectList(){
		return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(confInfoClassify.selectList(new EntityWrapper<ConfInfoClassify>().where("status!={0}", StatusEnum.DELETED.getCode()).groupBy("pk_id desc"))));
	}
	
	/**
	 * (OK)
	 * @Title: insert
	 * @Description: 添加
	 * @param entity
	 * @param request
	 * @return
	 * @return: Object
	 */
	@RequestMapping(method = RequestMethod.POST)
	public Object insert(@ModelAttribute ConfInfoClassify entity, HttpServletRequest request) {
		if (StringUtil.isNullOrEmpty(entity.getClassifyName())) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "任务组分类名称不能为空")));
		}
		List<ConfInfoClassify> list = confInfoClassify.selectList(new EntityWrapper<ConfInfoClassify>().where("classify_name = {0}", entity.getClassifyName()));
		if (list.size() > 0) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "任务分组已存在，请勿重复添加")));
		}
		entity.setCreateUser(super.getUser(request).getName());
		entity.setCreateTime(new Date());
		boolean bool = entity.insert();
		if (bool) {
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(entity));
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "添加失败").get()));
	}

	/**
	 * (OK)
	 * @Title: delete
	 * @Description: 删除
	 * @param id
	 * @return
	 * @return: Object
	 */
	@DeleteMapping
	public Object delete(@RequestParam Integer id) {
		List<ConfInfoGroup> list = confInfoGroup.selectList(new EntityWrapper<ConfInfoGroup>().where("classify_id = {0} and status = {1}", id, 1));
		if (list.size() > 0) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "该分类下有正在运行的任务组，删除失败")));
		}
		ConfInfoClassify entity = new ConfInfoClassify();
		entity.setPkId(id);
		entity.setStatus(StatusEnum.DELETED.getCode());
		boolean bool = confInfoClassify.updateById(entity);
		if (bool) {
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(bool));
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "删除失败").get()));
	}

	/**
	 * (OK)
	 * @Title: update
	 * @Description: 修改
	 * @param entity
	 * @param request
	 * @return
	 * @return: Object
	 */
	@PutMapping
	public Object update(@ModelAttribute ConfInfoClassify entity, HttpServletRequest request) {
		if (StringUtil.isNullOrEmpty(entity.getClassifyName())) {
			return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil("message", "分类名不能为空").get()));
		}
		entity.setUpdateUser(super.getUser(request).getName());
		entity.setUpdateTime(new Date());
		boolean bool = entity.updateById();
		if (bool) {
			return new ResponseCommand(ResponseCommand.STATUS_SUCCESS, new VisitsResult(entity));
		}
		return new ResponseCommand(ResponseCommand.STATUS_ERROR, new VisitsResult(new NewMapUtil().set("message", "修改失败").get()));
	}

}
