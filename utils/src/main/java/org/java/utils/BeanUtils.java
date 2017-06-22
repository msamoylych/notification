package org.java.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Created by msamoylych on 26.05.2017.
 */
@Component
public class BeanUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static <T> Collection<T> beansOfType(Class<T> cls) {
        return applicationContext.getBeansOfType(cls).values();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanUtils.applicationContext = applicationContext;
    }
}
