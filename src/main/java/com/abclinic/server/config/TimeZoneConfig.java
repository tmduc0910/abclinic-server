package com.abclinic.server.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @author tmduc
 * @package com.abclinic.server.config
 * @created 7/8/2020 9:49 AM
 */
@Configuration
public class TimeZoneConfig {
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    }
}
