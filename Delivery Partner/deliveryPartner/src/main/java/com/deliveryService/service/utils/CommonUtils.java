package com.deliveryService.service.utils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public final class CommonUtils {

    private CommonUtils() {}

    //  Null checks
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    //  String checks
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    //  Collection checks
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    //  Map checks
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    //  Require non-null (fail fast)
    public static <T> T requireNonNull(T obj, String message) {
        return Objects.requireNonNull(obj, message);
    }


    public static boolean isNullOrEmptyObject(Object obj) {
        if (obj == null) return true;

        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (value != null) {
                    if (value instanceof String) {
                        if (!((String) value).trim().isEmpty()) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error checking object fields", e);
        }

        return true; // all fields null/empty
    }
}