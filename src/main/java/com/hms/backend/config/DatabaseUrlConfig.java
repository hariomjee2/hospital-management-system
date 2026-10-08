package com.hms.backend.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DatabaseUrlConfig {

    @Bean
    @Primary
    public DataSourceProperties dataSourceProperties(DataSourceProperties properties) {
        String url = properties.getUrl();
        if (url != null && url.startsWith("postgresql://")) {
            properties.setUrl("jdbc:" + url);
        }
        return properties;
    }
}
