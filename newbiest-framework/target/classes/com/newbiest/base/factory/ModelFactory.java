package com.newbiest.base.factory;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guoxunbo on 2017/10/13.
 */
public class ModelFactory {

    /**
     * 实体名称和对象的关系
     */
    public static Map<String, ClassLoader> modelClassLoaders = Maps.newConcurrentMap();

    /**
     * 实体名称和其历史对象的classLoader所属关系
     */
    public static Map<String, ClassLoader> historyModelClassLoaders = Maps.newConcurrentMap();

    /**
     * 实体名称和其历史对象名称所属关系
     */
    public static Map<String, String> historyModelClassName = Maps.newConcurrentMap();

    public static void registerModelClassLoader(String model, ClassLoader loader) {
        modelClassLoaders.put(model, loader);
    }

    public static ClassLoader getModelClassLoader(String model){
        return modelClassLoaders.get(model);
    }

    public static void registerHistoryModelClassLoader(String model, ClassLoader loader) {
        historyModelClassLoaders.put(model, loader);
    }

    public static ClassLoader getHistoryModelClass(String model){
        return historyModelClassLoaders.get(model);
    }

    public static void registerHistoryClassName(String model, String historyClassName) {
        historyModelClassName.put(model, historyClassName);
    }

    public static String getHistoryModelClassName(String model){
        return historyModelClassName.get(model);
    }

}
