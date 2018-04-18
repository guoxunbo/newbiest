package com.newbiest.base.factory;

import com.google.common.collect.Maps;
import com.newbiest.msg.trans.ITransHandler;

import java.util.Map;

/**
 * 注册消息句柄的工厂
 * Created by guoxunbo on 2017/9/29.
 */
public class TransHandlerFactory {

    public static Map<String, ITransHandler> handlers = Maps.newConcurrentMap();

    public static void registerTransHandler(String type, ITransHandler handler) {
        handlers.put(type, handler);
    }

    public static ITransHandler getTransHandler(String type){
        return handlers.get(type);
    }

}
