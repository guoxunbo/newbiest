package com.newbiest.base.utils;

import net.sf.cglib.core.Converter;

import java.util.List;

/**
 * Copy历史的时候不复制List
 * Created by guoxunbo on 2017/10/7.
 */
public class HistoryBeanConverter implements Converter{

    @Override
    public Object convert(Object value, Class target, Object context) {
        if (value instanceof List) {
            return null;
        }
        return value;
    }

}
