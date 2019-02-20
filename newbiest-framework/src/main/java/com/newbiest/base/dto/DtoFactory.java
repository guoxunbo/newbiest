package com.newbiest.base.dto;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

/**
 * Dto注册工厂
 * Created by guoxunbo on 2019/1/22.
 */
public class DtoFactory implements Serializable {

    private static Map<String, IDto> context = Maps.newConcurrentMap();

    public static void registerDto(String className, IDto dto) {
        context.put(className, dto);
    }

    public static IDto getDto(String className) {
        return context.get(className);
    }

}
