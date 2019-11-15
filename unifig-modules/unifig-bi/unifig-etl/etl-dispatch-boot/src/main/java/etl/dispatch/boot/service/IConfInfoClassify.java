package etl.dispatch.boot.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import etl.dispatch.boot.entity.ConfInfoClassify;

public interface IConfInfoClassify extends IService<ConfInfoClassify> {

	void page(Page page);

}
