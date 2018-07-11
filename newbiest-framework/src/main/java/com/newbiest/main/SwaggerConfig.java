package com.newbiest.main;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger配置类
 * Created by guoxunbo on 2017/11/13.
 */
@Configuration
@EnableSwagger2
@ConfigurationProperties(prefix = "newbiest.api")
@PropertySource(value = "classpath:newbiest.yml", factory = YmlPropertyLoaderFactory.class)
@Data
public class SwaggerConfig {

    private String title;

    private String description;

    private String version;

    private String termsOfServiceUrl;

    private String license;

    private String licenseUrl;

    private String contactName;

    private String contactUrl;

    private String contactEmail;

    @Bean
    public Docket createDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .termsOfServiceUrl(termsOfServiceUrl)
                .version(version)
                .contact(new Contact(contactName, contactUrl, contactEmail))
                .build();
    }

}
