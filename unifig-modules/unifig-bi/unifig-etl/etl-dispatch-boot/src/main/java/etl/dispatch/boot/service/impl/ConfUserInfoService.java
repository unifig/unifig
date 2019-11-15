package etl.dispatch.boot.service.impl;

import etl.dispatch.boot.dao.ConfUserInfoMapper;
import etl.dispatch.boot.entity.ConfUserInfo;
import etl.dispatch.boot.service.IConfUserInfo;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 组织机构(部门+岗位) 服务实现类
 * </p>
 *
 *
 * @since 2017-08-14
 */
@Service
public class ConfUserInfoService extends ServiceImpl<ConfUserInfoMapper, ConfUserInfo> implements IConfUserInfo {
	
}
