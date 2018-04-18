package com.newbiest.core.utils;

import java.util.Collection;

/**
 * 集合的工具类
 * Created by guoxunbo on 2018/1/25.
 */
public class CollectionUtils {

    public static boolean isNotEmpty(Collection collection) {
        return collection != null && collection.size() > 0;
    }
}
