package com.lee.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringTool implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(SpringTool.class);

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    public static String getProperty(String key) {
        return context.getEnvironment().getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return context.getEnvironment().getProperty(key, defaultValue);
    }

    public static <T> T getProperty(String key, Class<T> targetType) {
        return context.getEnvironment().getProperty(key, targetType);
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return context.getEnvironment().getProperty(key, targetType, defaultValue);
    }

    public static void autowireBean(Object existingBean) {
        context.getAutowireCapableBeanFactory().autowireBean(existingBean);
    }


}
