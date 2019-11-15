package etl.dispatch.base;

import java.util.Collection;

import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import etl.dispatch.base.holder.PropertiesHolder;
import etl.dispatch.base.holder.SpringContextHolder;
import etl.dispatch.base.scheduled.ScheduledService;
@DependsOn({"springContextHolder" })
@Component
public class SckeduledTasks {
	private Collection<ScheduledService> scheduledServices;
	/**
	 * 表示每隔1000*60*10ms，Spring scheduling会调用一次该方法，不论该方法的执行时间是多少，定时load自定义properties
	 * @throws InterruptedException
	 */
	@Scheduled(fixedRate = 1000*60*10)
	public void loadProperties() throws InterruptedException {
		try {
			//PropertiesHolder.loadProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 表示当方法执行完毕60000ms后，Spring scheduling会再次调用该方法,定时load刷新数据
	 * @throws InterruptedException
	 */
	@Scheduled(fixedRate = 60000)
	public void reportCurrentTimeAfterSleep() throws InterruptedException {
		this.scheduledServices = SpringContextHolder.getBeansOfType(ScheduledService.class).values();
		if (this.scheduledServices == null) {
			return;
		}
		for (ScheduledService scheduledService : scheduledServices) {
			scheduledService.schedule();
		}
	}

	/**
	 * 提供了一种通用的定时任务表达式，这里表示每隔5秒执行一次，更加详细的信息可以参考cron表达式。
	 * @throws InterruptedException
	 */
	@Scheduled(cron = "0 0 1 * * *")
	public void reportCurrentTimeCron() throws InterruptedException {

	}
}
