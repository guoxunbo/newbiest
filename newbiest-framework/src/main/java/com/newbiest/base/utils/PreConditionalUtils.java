package com.newbiest.base.utils;

import com.google.common.base.Preconditions;
import com.newbiest.base.exception.ExceptionManager;

import java.util.Collection;

/**
 * 先决条件检查类
 * Created by guoxunbo on 2018/1/19.
 */
public class PreConditionalUtils {

    public static final String NULL_MESSAGE = "%s is null";
    public static final String LESS_THAN_MESSAGE = "%s is not less than %s";
    public static final String MORE_THEN_MESSAGE = "%s is not more than %s";
    public static final String BETWEEN_END_MESSAGE = "%s is not between %s end %s";

    /**
     * 检查下标是否在集合之中
     * @param index 下标
     * @param collection 集合
     */
    public static void checkIndexInCollection(int index, Collection collection) {
        try {
            Preconditions.checkElementIndex(index, collection.size());
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 检查当前值是否在最小值最大值之间 包含最小值最大值即[min, max]
     * @param source 当前值
     * @param min 最小值
     * @param max 最大值
     */
    public static void checkBetweenEnd(int source, int min, int max) {
        try {
            boolean expression = source >= min && source <= max;
            Preconditions.checkArgument(expression, BETWEEN_END_MESSAGE, source, min, max);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 检查当前值是否小于目标值
     * @param source
     * @param dest
     */
    public static void checkLessThan(int source, int dest) {
        try {
            Preconditions.checkArgument(source < dest, LESS_THAN_MESSAGE, source, dest);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 检查当前值是否大于目标值
     * @param source
     * @param dest
     */
    public static void checkMoreThan(int source, int dest) {
        try {
            Preconditions.checkArgument(source > dest, MORE_THEN_MESSAGE, source, dest);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 检查对象是否是空
     * @param object 检查对象
     * @param objectType 检查对象所属类型当前类型指业务类型非JAVA基础类型
     */
    public static void checkNotNull(Object object, String objectType) {
        try {
            if (StringUtils.isNullOrEmpty(objectType)) {
                Preconditions.checkNotNull(object);
            } else {
                Preconditions.checkNotNull(object, NULL_MESSAGE, objectType);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }



}
