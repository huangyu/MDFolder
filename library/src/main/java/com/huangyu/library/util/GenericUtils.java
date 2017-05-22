package com.huangyu.library.util;

import java.lang.reflect.ParameterizedType;

/**
 * 泛型工具类
 * Created by huangyu on 2017-4-10.
 */
public class GenericUtils {

    private GenericUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 获取反射类实例
     *
     * @param classObject  类对象
     * @param typePosition 泛型参数序号
     * @param <T>          任意泛型
     * @return 泛型参数实例
     */
    public static <T> T getT(Object classObject, int typePosition) {
        try {
            ParameterizedType parameterizedType = (ParameterizedType) classObject.getClass().getGenericSuperclass();
            Class<?> clazz = (Class) parameterizedType.getActualTypeArguments()[typePosition];
            return (T) clazz.newInstance();
        } catch (Exception e) {
            // 非使用MVP模式的的Activity会抛出此异常
        }
        return null;
    }

}
