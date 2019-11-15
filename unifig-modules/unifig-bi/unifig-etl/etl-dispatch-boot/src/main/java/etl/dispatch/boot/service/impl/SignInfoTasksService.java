package etl.dispatch.boot.service.impl;

import etl.dispatch.boot.dao.SignInfoTasksMapper;
import etl.dispatch.boot.entity.SignInfoTasks;
import etl.dispatch.boot.service.ISignInfoTasks;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 存储各个任务任务组执行完成标记 服务实现类
 * </p>
 *
 *
 * @since 2017-08-14
 */
@Service
public class SignInfoTasksService extends ServiceImpl<SignInfoTasksMapper, SignInfoTasks> implements ISignInfoTasks {
	
}
