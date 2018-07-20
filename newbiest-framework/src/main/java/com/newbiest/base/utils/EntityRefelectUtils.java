package com.newbiest.base.utils;

import com.newbiest.base.exception.ClientException;

import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 反射工具类
 * Created by guoxunbo on 2018/7/19.
 */
public class EntityRefelectUtils {

    public static boolean checkFieldPersist(Field field) {
        if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers()) || field.getAnnotation(Transient.class) != null) {
            return false;
        }
        return true;
    }
}
