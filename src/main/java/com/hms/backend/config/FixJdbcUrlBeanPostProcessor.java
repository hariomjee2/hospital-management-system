package com.hms.backend.config;

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
            if (url != null && (url.startsWith("postgresql://") || url.startsWith("postgres://"))) {
                properties.setUrl("jdbc:" + url);
            }
        }
        return bean;
    }
}
