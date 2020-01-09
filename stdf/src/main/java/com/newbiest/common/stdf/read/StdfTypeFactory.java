package com.newbiest.common.stdf.read;

import com.google.common.collect.Maps;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.common.stdf.datarecord.FAR;

import java.util.Map;

/**
 * Stdf的TYPE是由2种组合，head上有type+subType决定了一种stdfType
 * Created by guoxunbo on 2020-01-08 18:04
 */
public class StdfTypeFactory {

    public static final String UNKNOWN_TYPE_KEY = "UnknownTypeKey";
    public static byte[][] types = new byte[51][95];

    public static Map<String, String> typeMap = Maps.newConcurrentMap();

    static {
        putType(FAR.HEAD_TYPE, FAR.HEAD_SUB_TYPE, FAR.RECORD_TYPE);
    }

    public static void putType(short type, short subType, String typeName) {
        String key = generatorTypeKey(type, subType);
        typeMap.put(key, typeName);

    }

    public static String getType(short type, short subType) {
        String key = generatorTypeKey(type, subType);
        if (!typeMap.containsKey(key)) {
            return UNKNOWN_TYPE_KEY;
        }
        return typeMap.get(key);
    }

    private static String generatorTypeKey(short type, short subType) {
        return type + StringUtils.UNDERLINE_CODE + subType;
    }
}
