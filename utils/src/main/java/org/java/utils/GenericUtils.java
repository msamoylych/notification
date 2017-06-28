package org.java.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by msamoylych on 27.06.2017.
 */
public class GenericUtils {

    public static Type getGenericType(Object o) {
        return getGenericType(o.getClass());
    }

    public static Type getGenericType(Class<?> cls) {
        return ((ParameterizedType) (cls).getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public static Type getGenericType(ParameterizedType type) {
        return type.getActualTypeArguments()[0];
    }
}
