package org.java.utils;

import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Created by msamoylych on 26.05.2017.
 */
public final class BeanUtils {

    public static <T> Collection<T> beansOfType(ApplicationContext applicationContext, Class<T> cls) {
        return applicationContext.getBeansOfType(cls).values();
    }

    public static <T> void forEachBeanOfType(ApplicationContext applicationContext, Class<T> cls, Consumer<? super T> action) {
        beansOfType(applicationContext, cls).forEach(action);
    }
}
