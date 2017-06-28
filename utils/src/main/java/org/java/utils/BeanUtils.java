package org.java.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Created by msamoylych on 26.05.2017.
 */
@Component
public class BeanUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static <T> T bean(Class<T> cls) {
        return applicationContext.getBean(cls);
    }

    public static <T> Collection<T> beansOfType(Class<T> cls) {
        return applicationContext.getBeansOfType(cls).values();
    }

    public static <T> void forEachBeanOfType(Class<T> cls, Consumer<? super T> action) {
        beansOfType(cls).forEach(action);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanUtils.applicationContext = applicationContext;
    }
}
