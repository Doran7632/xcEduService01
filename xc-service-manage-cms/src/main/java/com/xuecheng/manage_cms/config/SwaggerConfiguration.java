/*******************************************************************************
 * Package: com.xuecheng.manage_cms.config
 * Type:    SwaggerConfiguration
 * Date:    2020/1/15 16:01
 *
 * Copyright (c) 2020 HUANENG GUICHENG TRUST CORP.,LTD All Rights Reserved.
 *
 * You may not use this file except in compliance with the License.
 *******************************************************************************/
package com.xuecheng.manage_cms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.bycdao.cloud"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("学成在线CMS接口文档")
                .description("学成在线cms接口文档描述")
                .termsOfServiceUrl("http://localhost:8999/")
                .contact("developer@mail.com")
                .version("1.0")
                .build();
    }
}
