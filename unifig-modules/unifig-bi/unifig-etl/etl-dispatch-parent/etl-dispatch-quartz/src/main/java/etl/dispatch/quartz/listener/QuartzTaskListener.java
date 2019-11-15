package etl.dispatch.quartz.listener;

import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.alibaba.fastjson.JSON;
import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.config.api.IEtlConfigApiService;
import etl.dispatch.config.service.IConfRelyTasksService;
import etl.dispatch.script.constant.CommonConstants;
import etl.dispatch.quartz.holder.QuartzMangerHolder;
import etl.dispatch.quartz.jobs.ScheduledJob;
import etl.dispatch.util.StringUtil;

/**
 * 定时任务监听器，容器启动时会执行onApplicationEvent方法，销毁时会执行onApplicationDestroy方法。
 *
 *
 */
public class QuartzTaskListener implements ApplicationListener<ContextRefreshedEvent> {
	private static final Logger logger = LoggerFactory.getLogger(QuartzTaskListener.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("::::::::::::::::::::: 开始加载调度定时任务！:::::::::::::::::::::");
		initQuartzListener();
		logger.info("::::::::::::::::::::: 调度定时任务加载结束！:::::::::::::::::::::");
	}

	/**
	 * 容器关闭时会自动执行该方法，定时任务也会随之停止。<br/>
	 * 详细描述：容器关闭时自动执行该方法，该方法中实现了定时任务的停止操作。<br/>
	 * 使用方式：容器关闭会自动被调用。
	 */
	@PreDestroy
	public void onApplicationDestroy() {
		QuartzMangerHolder.getInstance().stopScheduler();
		logger.info("容器关闭，同时也关闭所有的定时任务！");
	}

	/**
	 * 容器启动时需要加载的定时任务。
	 */
	private void initQuartzListener() {
		QuartzMangerHolder quartzTaskManger = QuartzMangerHolder.getInstance();
		// 初始化Quartz调度任务
		quartzTaskManger.initQuartz();
		// 查询Etl调度任务配置
		IEtlConfigApiService etlConfigApiService = SpringContextHolder.getBean("iEtlConfigApiService", IEtlConfigApiService.class);
		List<Map<String, Object>> etlConfigMapList = etlConfigApiService.getEtlConfigQuartzData();
		if (null != etlConfigMapList && !etlConfigMapList.isEmpty()) {
			for (Map<String, Object> etlConfigMap : etlConfigMapList) {
				String jobName     = String.valueOf(etlConfigMap.get(CommonConstants.PROP_QUARTZ_JOBNAME));
				String jobGroup    = StringUtil.isNullOrEmpty(etlConfigMap.get(CommonConstants.PROP_QUARTZ_JOBGROUP)) ? Scheduler.DEFAULT_GROUP : String.valueOf(etlConfigMap.get(CommonConstants.PROP_QUARTZ_JOBGROUP));
				String schedExpres = String.valueOf(etlConfigMap.get(CommonConstants.PROP_QUARTZ_SCHEDEXPRES));
				String jobDesc     = String.valueOf(etlConfigMap.get(CommonConstants.PROP_QUARTZ_JOBDESC));
				JobDataMap dataMap = (JobDataMap) etlConfigMap.get(CommonConstants.PROP_QUARTZ_JOBDATAMAP);
				boolean validExpre = org.quartz.CronExpression.isValidExpression(schedExpres);
				if (!validExpre) {
					logger.error("Quartz schedExpress is not correct; etlConfig:"+ JSON.toJSONString(etlConfigMap)+"; please contact the administrator!");
					continue;
				}
				quartzTaskManger.createQuartz(jobName, jobGroup, ScheduledJob.class, schedExpres, jobDesc, dataMap);
				logger.info("定时任务" + jobGroup + "配置成功！");
			}
		}
		// 执行 Quartz调度任务
		quartzTaskManger.startQuartz();
	}

}
