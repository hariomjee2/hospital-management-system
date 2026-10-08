package com.hms.backend.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String dbUrl = environment.getProperty("SPRING_DATASOURCE_URL");
        if (dbUrl == null || dbUrl.isEmpty()) {
            dbUrl = environment.getProperty("DATABASE_URL");
        }

        if (dbUrl != null && dbUrl.startsWith("postgresql://")) {
            String fixedUrl = "jdbc:" + dbUrl;
            Map<String, Object> map = new HashMap<>();
            map.put("spring.datasource.url", fixedUrl);
            environment.getPropertySources().addFirst(new MapPropertySource("fixedDatabaseUrlProperties", map));
        }
    }
}
