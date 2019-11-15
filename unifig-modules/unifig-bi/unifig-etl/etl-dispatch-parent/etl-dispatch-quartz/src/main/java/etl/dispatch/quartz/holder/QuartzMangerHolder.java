package etl.dispatch.quartz.holder;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.Assert;

import etl.dispatch.base.holder.PropertiesHolder;
import etl.dispatch.quartz.bean.QuartzBean;
import etl.dispatch.quartz.exception.ServiceException;
import etl.dispatch.util.Exceptions;
import etl.dispatch.util.OsUtils;
 /**
 * QuartzTaskManger类是定时任务管理类。容器启动时会通过定时任务监听类QuartzTaskListener，进而调用本类中的相关方法。
 * 
 *
 *
 */
public class QuartzMangerHolder {
    private static final Logger logger = LoggerFactory.getLogger(QuartzMangerHolder.class);
    private static SchedulerFactory schedulerfactory = null;
    private static QuartzMangerHolder quartzTaskManger = null;

    /** 
     * 实例化定时任务。<br/>
     * 详细描述：用于定时任务的初始化工作。<br/>
     * 使用方式：容器启动时会通过QuartzTaskListener监听调用。
     * @return 返回QuartzTaskManger实例。
     */ 
    public synchronized static QuartzMangerHolder getInstance() {
        if (quartzTaskManger == null) {
            quartzTaskManger = new QuartzMangerHolder();
        }
        return quartzTaskManger;
    }

    /** 
     * 创建触发器。<br/>
     * 详细描述：定时任务触发器的创建。<br/>
     * @param triggerName 触发器名称。
     * @param express 定时任务的表达式。
     * @return cronTrigger CronTrigger。
     */ 
    public CronTrigger createTrigger(String triggerName, String express) {
        CronTriggerImpl cronTrigger = null;
        try {
            cronTrigger = new CronTriggerImpl();
            cronTrigger.setName(triggerName);
            cronTrigger.setGroup(Scheduler.DEFAULT_GROUP);
            cronTrigger.setCronExpression(express);
        } catch (ParseException e) {
            logger.error("taskManager class (createTrigger method) ParseException：" + e);
        }
        return cronTrigger;
    }

    /** 
     * 创建触发任务执行的实现类。<br/>
     * 详细描述：设置需要执行的job任务内容，要执行的class。
     * 使用方式：该方法会在startQuartz方法中被调用到。
     * @param jobName 任务名称。
     * @param obj 要执行任务class类。
     * @return jobDetail实例。
     */ 
    @SuppressWarnings("unchecked")
    public JobDetail createJobDetail(String jobName, Object obj) {
        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setName(jobName);
        jobDetail.setGroup(Scheduler.DEFAULT_GROUP);
        jobDetail.setJobClass((Class<? extends Job>)obj);
        return jobDetail;
    }
    
    /** 
     * 初始化定时任务。<br/>
     * 详细描述：根据触发器，触发任务的实现类，创建定时任务。
     * @param jobDetail 触发任务执行的实现类。
     * @param cronTrigger 触发器。
     */ 
	public void initQuartz() {
		Resource resource = null;
		boolean develop =PropertiesHolder.getBooleanProperty("webapp.service.develop");
		//非开发环境 且 操作系统非 Windows，使用正式库配置
		if(!develop && OsUtils.isShellModel()){
			resource = getResource("classpath*:quartz-cluster-pro.properties");
		}else{
			resource = getResource("classpath*:quartz-cluster-dev.properties");
		}
		if (null != resource) {
			try {
				schedulerfactory = new StdSchedulerFactory(PropertiesLoaderUtils.loadProperties(resource));
			} catch (SchedulerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取properties文件
	 * @param configLocation
	 * @return
	 */
    private Resource getResource(String configLocation) {
		PathMatchingResourcePatternResolver resolover = new PathMatchingResourcePatternResolver();
		Assert.notNull(configLocation, configLocation + " path is null");
		Resource[] resources = null;
		try {
			resources = resolover.getResources(configLocation);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (null != resources && resources.length > 0) {
			return resources[0];
		}
		return null;
	}


    /** 
     * 创建定时任务。<br/>
     * 详细描述：根据触发器，触发任务的实现类，创建定时任务。
     * @param jobDetail 触发任务执行的实现类。
     * @param cronTrigger 触发器。
     */ 
	@SuppressWarnings("unchecked")
	public void createQuartz(String jobName, String jobGroup, Object obj, String express, String jobDesc, JobDataMap dataMap) {
		try {
			String dataTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
			if (checkExists(jobName, jobGroup)) {
				return;
			}
			JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
			JobDetail jobDetail = (JobDetailImpl) JobBuilder.newJob((Class<? extends Job>)obj).withIdentity(jobKey).withDescription(jobDesc).usingJobData(dataMap).requestRecovery().build();
			
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
			CronScheduleBuilder schedBuilder = CronScheduleBuilder.cronSchedule(express).withMisfireHandlingInstructionDoNothing();
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withDescription(dataTime).withSchedule(schedBuilder).build();

			schedulerfactory.getScheduler().scheduleJob(jobDetail, trigger);
		} catch (SchedulerException  ex) {
			throw new ServiceException("类名不存在或执行表达式错误"+Exceptions.getStackTraceAsString(ex));
		}
	}

    /** 
     * 启动定时任务。<br/>
     * 详细描述：启动定时任务。
     * @param jobName 任务名称。
     * @param obj 触发任务的对象。
     * @param triggerName 触发器名称。
     * @param express 定时任务表达式。
     */ 
    public void startQuartz() {
    	try {
            schedulerfactory.getScheduler().start();
        } catch (SchedulerException e) {
            logger.error("taskManager class (createQuartz method) SchedulerException：" + e);
        }
    }

    /** 
     * 关闭所有定时任务。<br/>
     * 详细描述：关闭所有调度定时任务。
     */ 
    public void stopScheduler() {
        try {
            schedulerfactory.getScheduler().shutdown();
        } catch (SchedulerException e) {
            logger.error("taskManager class (stopScheduler method) SchedulerException：" + e);
        }
    }
    
    /**
     * 查询定时任务列表
     * @return
     * @throws SchedulerException
     */
	public List<QuartzBean> listQuartz() throws SchedulerException {
		List<QuartzBean> quartzBeanList = new ArrayList<>();
		Scheduler scheduler = schedulerfactory.getScheduler();
		for (String groupJob : scheduler.getJobGroupNames()) {
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.<JobKey> groupEquals(groupJob))) {
				List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
				for (Trigger trigger : triggers) {
					Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
					JobDetail jobDetail = scheduler.getJobDetail(jobKey);
					String cronExpression = null, createTime = null;
					if (trigger instanceof CronTrigger) {
						CronTrigger cronTrigger = (CronTrigger) trigger;
						cronExpression = cronTrigger.getCronExpression();
						createTime = cronTrigger.getDescription();
					}
					QuartzBean info = new QuartzBean();
					info.setJobName(jobKey.getName());
					info.setJobGroup(jobKey.getGroup());
					info.setJobDescription(jobDetail.getDescription());
					info.setJobStatus(triggerState.name());
					info.setCronExpression(cronExpression);
					info.setCreateTime(createTime);
					quartzBeanList.add(info);
				}
			}
		}
		return quartzBeanList;
	}
    
    /**
     * 修改 定时任务
     * @param jobName  任务名称
     * @param jobGroup 任务组名称
     * @param obj
     * @param express
     * @param jobDescription
     */
	public void editQuartz(String jobName, String jobGroup, Object obj, String express, String jobDescription) {
		try {
			String dataTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
			if (!checkExists(jobName, jobGroup)) {
				throw new ServiceException(String.format("Job不存在, jobName:{%s},jobGroup:{%s}", jobName, jobGroup));
			}
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
			JobKey jobKey = new JobKey(jobName, jobGroup);
			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(express).withMisfireHandlingInstructionDoNothing();
			CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withDescription(dataTime).withSchedule(cronScheduleBuilder).build();

			JobDetail jobDetail = schedulerfactory.getScheduler().getJobDetail(jobKey);
			jobDetail.getJobBuilder().withDescription(jobDescription);
			HashSet<Trigger> triggerSet = new HashSet<>();
			triggerSet.add(cronTrigger);

			schedulerfactory.getScheduler().scheduleJob(jobDetail, triggerSet, true);
		} catch (SchedulerException e) {
			throw new ServiceException("类名不存在或执行表达式错误");
		}
	}
    
    /**
	 * 删除定时任务
	 * @param jobName 任务名称
	 * @param jobGroup 任务组名称
	 */
	public void deleteQuartz(String jobName, String jobGroup) {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
		try {
			if (checkExists(jobName, jobGroup)) {
				//停止使用相关的触发器 
				schedulerfactory.getScheduler().pauseTrigger(triggerKey);
				//停止调度Job任务 
				schedulerfactory.getScheduler().unscheduleJob(triggerKey);
				logger.info("===> delete, triggerKey:{}", triggerKey);
			}
		} catch (SchedulerException e) {
			throw new ServiceException(e.getMessage());
		}
	}
    
    /**
     * 暂停定时任务
     * @param jobName
     * @param jobGroup
     */
	public void pauseQuartz(String jobName, String jobGroup) {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
		try {
			if (checkExists(jobName, jobGroup)) {
				schedulerfactory.getScheduler().pauseTrigger(triggerKey);
				logger.info("===> Pause success, triggerKey:{}", triggerKey);
			}
		} catch (SchedulerException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * 重新启动任务
	 * @param jobName
	 * @param jobGroup
	 */
	public void rescheduleJob(String jobName, String jobGroup) {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
		try {
			if (checkExists(jobName, jobGroup)) {
				Trigger newTrigger = null;
				try {
					newTrigger = schedulerfactory.getScheduler().getTrigger(triggerKey);
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
				schedulerfactory.getScheduler().rescheduleJob(triggerKey, newTrigger);
				logger.info("===> rescheduleJob success, triggerKey:{}", triggerKey);
			}
		} catch (SchedulerException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	

	/**
	 * 验证是否存在
	 * @param jobName 任务名称。
	 * @param jobGroup 任务组名称
	 * @return
	 * @throws SchedulerException
	 */
	public boolean checkExists(String jobName, String jobGroup) throws SchedulerException{
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
		return schedulerfactory.getScheduler().checkExists(triggerKey);
	}
}