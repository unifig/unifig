package etl.dispatch.boot.dao;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;

import etl.dispatch.base.datasource.annotation.BaseRepository;
import etl.dispatch.boot.entity.ConfInfoClassify;

/**
 * 
 * @ClassName: ConfInfoClassifyMapper 
 * @Description: 存储各个任务组的分类配置
 * @date: 2017年11月6日 下午4:19:17
 */
@BaseRepository
public interface ConfInfoClassifyMapper extends BaseMapper<ConfInfoClassify> {

	List page(Page page);
}
