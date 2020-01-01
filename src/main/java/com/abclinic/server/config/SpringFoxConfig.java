package com.abclinic.server.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

/**
 * @author tmduc
 * @package com.abclinic.server.config
 * @created 12/27/2019 3:22 PM
 */
@Configuration
@EnableSwagger2
public class SpringFoxConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .globalOperationParameters(
                        Arrays.asList(new ParameterBuilder()
                        .name("Authorization")
                        .description("UID của người dùng (bắt buộc cho tất cả API ngoại trừ đăng kí hoặc đăng nhập)")
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .required(false)
                        .build())
                )
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();
    }
}
