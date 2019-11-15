//package com.unifig.organ.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.context.request.async.DeferredResult;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
///**
// * Swagger2API文档的配置
// *    on 2018/4/26.
// */
//@Configuration
//@EnableSwagger2
//public class Swagger2Config {
//    /*@Bean
//    public Docket createRestApi(){
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.unifig.mall.controller"))
//                .paths(PathSelectors.any())
//                .build();
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("mall")
//                .description("mall")
//                .contact("Z")
//                .version("1.0")
//                .build();
//    }*/
//    @Bean
//    public Docket ProductApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .genericModelSubstitutes(DeferredResult.class)
//                .useDefaultResponseMessages(false)
//                .forCodeGeneration(false)
//                .pathMapping("/")
//                .select()
//                .build()
//                .apiInfo(productApiInfo());
//    }
//
//    private ApiInfo productApiInfo() {
//        ApiInfo apiInfo = new ApiInfo("organ 接口文档",
//                "organ接口",
//                "1.0.0",
//                "API TERMS URL",
//                " @ .com",
//                "license",
//                "license url");
//        return apiInfo;
//    }
//}
