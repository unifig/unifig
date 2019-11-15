package etl.dispatch.base.initialize;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


/**
 * 容器启动的初始化类，会根据注解自动执行initialize方法，该方法中会调用所有实现IWebInitializable接口的类。
 *
 *
 */
@Configuration
public class ApplicationInitialization implements ApplicationContextAware {
	private static Logger logger = LoggerFactory.getLogger(ApplicationInitialization.class);
    private ApplicationContext applicationContext = null;
  

    /**
     * 根据注解@PostConstruct会调用该方法，实现初始化操作。<br/>
     * 详细描述：根据该注解在服务器加载时会被执行一次，该方法中会调用所有实现IWebInitializable接口的方法，实现初始化操作。<br/>
     * 使用方式：无需调用，根据注解启动时会自动执行。
     */
    @PostConstruct
    public void initialize() {
        IWebInitializable[] initializableAry = this.getIWebInitializables();
        if (initializableAry != null) {
            logger.info("::::::::::::::::::::: 开始系统初始化 :::::::::::::::::::::");
            int i = 0;
            for (IWebInitializable initableObj : initializableAry) {
                i++ ;
                initableObj.initialize();
                logger.info(" ({}) :::::: {} ::::::[{}]", new Object[] {i, initableObj.initLog(),
                    initableObj.getClass().getCanonicalName()});
            }
            logger.info("::::::::::::::::::::: 系统初始化完成 :::::::::::::::::::::");
        }
    }

    private IWebInitializable[] getIWebInitializables() {
        Map<String, IWebInitializable> initableMap = this.applicationContext.getBeansOfType(IWebInitializable.class);
        if (CollectionUtils.isEmpty(initableMap)) {
            return null;
        }
        Collection<IWebInitializable> list = initableMap.values();
        IWebInitializable[] initializableAry = list.toArray(new IWebInitializable[list.size()]);
        Arrays.sort(initializableAry, new Comparator<IWebInitializable>() {

            public int compare(IWebInitializable initializable1, IWebInitializable initializable2) {
                Class<? extends IWebInitializable> clazz1 = initializable1.setInitDepend();
                Class<? extends IWebInitializable> clazz2 = initializable2.setInitDepend();

                if (clazz1 == IWebInitializable.class) {
                    return -1;
                } else if (clazz2 == IWebInitializable.class) {
                    return 1;
                } else if (initializable1.getClass() == clazz2) {
                    return -1;
                } else if (initializable2.getClass() == clazz1) {
                    return 1;
                }
                return 0;
            }
        });
        return initializableAry;
    }

    @PreDestroy
    public void destory() {
        IWebInitializable[] initializableAry = this.getIWebInitializables();
        if (initializableAry != null) {
            logger.info("::::::::::::::::::::: 系统即将关闭,开始调用销毁方法:::::::::::::::::::::");
            int i = 0;
            for (IWebInitializable initableObj : initializableAry) {
                i++ ;
                initableObj.destroy();
                logger.info(" ({}) :::::: {} ::::::[{}]", new Object[] {i, initableObj.destroyLog(),
                    initableObj.getClass().getCanonicalName()});
            }
            logger.info("::::::::::::::::::::: 系统关闭 :::::::::::::::::::::");
        }
    }

    /**
     * 容器启动会把spring的应用上下文赋给变量applicationContext。<br/>
     * 详细描述：当容器启动时，spring会自动调用setApplicationContext方法，并把应用上下文作为参数装入该方法。<br/>
     * 使用方式：spring会自动调用该方法。
     * 
     * @param applicationContext spring的应用上下文。
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}