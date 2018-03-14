package com.jfhealthcare.conf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile({"dev", "test"})
public class Swagger2Config {

    @Bean
    public Docket createRestCApi() {
    	ParameterBuilder aParameterBuilder = new ParameterBuilder();
    	aParameterBuilder.name("token").defaultValue("123456789")
    	.description("token测试用").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
    	Parameter parameter = aParameterBuilder.build();
    	 List<Parameter> aParameters = new ArrayList<Parameter>();
    	 aParameters.add(parameter);
        return new Docket(DocumentationType.SWAGGER_2).groupName("web")
                .apiInfo(apiInfo())
                .globalOperationParameters(aParameters)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jfhealthcare.modules.system.controller"))
                .paths(PathSelectors.any()) //过滤的接口
                .build();
    }
    
//    @Bean
//    public Docket createRestPCApi() {
//        return new Docket(DocumentationType.SWAGGER_2).groupName("pcapi")
//                .apiInfo(apiInfo())
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.jfhealthcare.controller.business"))
//                .paths(PathSelectors.any()) //过滤的接口
//                .build();
//    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("JF RESTful API/九峰开发接口文档") //大标题
                .description("HTTP状态码大全：http://tool.oschina.net/commons?type=5") //详细描述
//                .termsOfServiceUrl("http://tool.oschina.net/commons?type=5")
                .contact("老蒋") //作者
                .version("1.0")
                .build();
    }

}