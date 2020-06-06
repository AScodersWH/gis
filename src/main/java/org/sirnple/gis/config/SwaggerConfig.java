package org.sirnple.gis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by: sirnple
 * Created in: 2020-06-06
 * Description:
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket userApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().paths(PathSelectors.regex("/.*")).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("GIS")
                .description("GIS web")
                .license("Apache License Version 2.0")
                .licenseUrl("http//localhost:8080")
                .version("2.0")
                .build();
    }
}
