package com.abclinic.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = { "com.abclinic.server" })
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    final static Logger log = LoggerFactory.getLogger(WebMvcConfiguration.class);

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().defaultViewInclusion(true).build();
        converters.add(new MappingJackson2HttpMessageConverter(mapper));
        log.info("Jackson Configured");
        log.info(converters.toString());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("http://127.0.0.1:5500")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}