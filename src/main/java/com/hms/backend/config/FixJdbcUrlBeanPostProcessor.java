package com.hms.backend.config;

import com.hms.backend.BackendApplication;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

@Component
public class FixJdbcUrlBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSourceProperties properties) {
            String url = properties.getUrl();
            if (url != null && (url.contains("@") || url.startsWith("postgresql://") || url.startsWith("postgres://"))) {
                BackendApplication.parseAndSetJdbcUrl(url);
                String systemFixedUrl = System.getProperty("spring.datasource.url");
                if (systemFixedUrl != null) {
                    properties.setUrl(systemFixedUrl);
                }
                String user = System.getProperty("spring.datasource.username");
                if (user != null) {
                    properties.setUsername(user);
                }
                String pass = System.getProperty("spring.datasource.password");
                if (pass != null) {
                    properties.setPassword(pass);
                }
            }
        }
        return bean;
    }
}
