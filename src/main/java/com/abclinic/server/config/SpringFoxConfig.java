package com.abclinic.server.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Tag;
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
                .tags(
                        new Tag("Xác thực", "Quản lý API xác thực, bao gồm đăng kí, đăng nhập và đăng xuất cho người dùng"),
                        new Tag("Nhân viên phòng khám", "Quản lý API dùng chung cho các bác sĩ và điều phồi viên"),
                        new Tag("Điều phối viên", "Quản lý API dành riêng cho điều phối viên"),
                        new Tag("Đa khoa", "Quản lý API dành riêng cho bác sĩ đa khoa"),
                        new Tag("Chuyên khoa", "Quản lý API dành riêng cho bác sĩ chuyên khoa"),
                        new Tag("Dinh dưỡng", "Quản lý API dành riêng cho bác sĩ dinh dưỡng"),
                        new Tag("Bệnh nhân", "Quản lý API dành riêng cho bệnh nhân"),
                        new Tag("Album và ảnh", "Quản lý API dùng cho việc upload và lấy dữ liệu ảnh"),
                        new Tag("Khác", "Các API khác dùng để test, không đưa vào hệ thống"),
                        new Tag("Thông tin cá nhân", "Quản lý API về thông tin tài khoản cá nhân")
                )
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();
    }
}
