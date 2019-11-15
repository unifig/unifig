package etl.dispatch.boot.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import etl.dispatch.boot.dao.ConfInfoClassifyMapper;
import etl.dispatch.boot.entity.ConfInfoClassify;
import etl.dispatch.boot.service.IConfInfoClassify;

@Service
public class ConfInfoClassifyService extends ServiceImpl<ConfInfoClassifyMapper, ConfInfoClassify> implements IConfInfoClassify {

	@Autowired
	private ConfInfoClassifyMapper confInfoClassMapper;
	
	@Override
	public void page(Page page) {
		page.setRecords(confInfoClassMapper.page(page));
	}
}
