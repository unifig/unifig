package etl.dispatch.base.holder;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * SpringContextHolder是Spring容器持有工具类，用于在代码中获取Spring容器管理的Bean。
 * 
 *
 *
 */
@Service("springContextHolder")
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
    private static Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);
    private static ApplicationContext applicationContext;

    /**
     * 初始化ApplicationContext对象。<br/>
     * 
     * @see ApplicationContextAware#setApplicationContext(ApplicationContext context)
     */
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
        logger.info("初始化SpringContextHolder ：" + context.getDisplayName());
    }

    /**
     * 获取ApplicationContext对象。<br/>
     * 
     * @return ApplicationContext对象。
     */
    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null)
            throw new IllegalStateException("applicationContext is null,SpringContextHolder未成功初始化");
        return applicationContext;
    }

    /**
     * 根据Bean的名称从Spring容器中获取Bean对象。<br/> 详细描述：根据Bean的名称从Spring容器中获取Bean对象。<br/> 使用方式：java代码中可直接调用此静态方法。
     * 
     * @param name bean名称。
     * @return T 返回相应的对象。
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T)getApplicationContext().getBean(name);
    }

    /**
     * 根据Class从Spring容器中获取Bean对象。<br/> 详细描述：根据Class从Spring容器中获取Bean。<br/> 使用方式：java代码中可直接调用此静态方法。
     * 
     * @param clazz Class类实例。
     * @return T 返回相应的bean对象。
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return getApplicationContext().getBeansOfType(clazz);
    }

    /**
     * 根据Bean名称、Class从Spring容器中获取Bean对象。<br/> 详细描述：根据Bean名称和Class从Spring容器中获取Bean对象。<br/> 使用方式：java代码中可直接调用此静态方法。
     * 
     * @param name bean名称。
     * @param clazz Class实例。
     * @return T 返回相应的bean对象。
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        try {
            return getBean(name);
        } catch (Exception e) {
            try {
                return getBean(clazz);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    /**
     * 获取Spring容器中的BeanDefinition Map。<br/> 详细描述：获取Spring容器中的BeanDefinition Map。<br/> 使用方式：java代码中可直接调用此方法。
     * 
     * @return Map<String, BeanDefinition> 返回设定好的类型。
     */
    public static Map<String, BeanDefinition> getApplicationBeanDefinitions() {
        Map<String, BeanDefinition> map = new HashMap<String, BeanDefinition>();
        XmlWebApplicationContext context = (XmlWebApplicationContext)applicationContext;
        ConfigurableListableBeanFactory factory = context.getBeanFactory();
        String[] names = factory.getBeanDefinitionNames();
        for (String name : names) {
            map.put(name, factory.getBeanDefinition(name));
        }
        return map;
    }

    /**
     * 扩展destory方法，清除spring容器持有的bean。
     */
    @Override
    public void destroy() throws Exception {
        SpringContextHolder.cleanHolder();
    }

    /**
     * 清除Spring容器持有的bean。
     */
    public static void cleanHolder() {
        applicationContext = null;
    }
}