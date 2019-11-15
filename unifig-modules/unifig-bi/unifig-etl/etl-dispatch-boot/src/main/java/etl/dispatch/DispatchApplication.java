package etl.dispatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import etl.dispatch.boot.filter.PermissionsAuthFilter;
import etl.dispatch.quartz.listener.QuartzTaskListener;
import etl.dispatch.register.listener.AppRegisterListener;

// 排除扫描MongoAutoConfiguration.class, MongoDataAutoConfiguration.class 避免启动报错
@SpringBootApplication(exclude = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
@EnableScheduling
public class DispatchApplication {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(DispatchApplication.class);
		springApplication.addListeners(new QuartzTaskListener());
		springApplication.addListeners(new AppRegisterListener());
		springApplication.run(args);
	}

	/**
	 * 访问请求权限过滤
	 * @return
	 */
	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		PermissionsAuthFilter httpBasicFilter = new PermissionsAuthFilter();
		registrationBean.setFilter(httpBasicFilter);
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/*");
		registrationBean.setUrlPatterns(urlPatterns);
		return registrationBean;
	}

	@InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
}
