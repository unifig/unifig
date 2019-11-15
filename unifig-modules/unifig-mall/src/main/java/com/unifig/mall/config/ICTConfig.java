//package com.unifig.mall.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
///**
// * 拦截器管理器
// *    on 2017/12/11.
// */
//@Configuration
//public class ICTConfig extends WebMvcConfigurerAdapter {
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        //添加拦截器
//        // registry.addInterceptor(new UserLoginInterceptor()).addPathPatterns("/**")
//        //         .excludePathPatterns("/login", "/service/editor/**", "/service/model/**", "/user/loginout", "/public/**", "/employee/insertOrUpdateEmployee", "/orgUser/entryPre/**", "/finance/html/***", "/finance/uploadMonofile", "/employee/getEmployeeInfo", "/employee/delectEmployeeInfoData", "/orgUser/updateOrgUserPwd");
//       // registry.addInterceptor(new UserAdminInterceptor()).addPathPatterns("/supply/insertOrUpdateSupplyOrder", "/circulation/logisticsSuppList", "/trucker/**");
//        super.addInterceptors(registry);
//    }
//
//
//}
