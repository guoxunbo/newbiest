package com.newbiest.mms.print;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author guoxunbo
 * @date 5/22/21
 */

public interface IPrintStrategy {

    void print(PrintContext printContext);

    /**
     * 构建client打印参数。
     * @param printContext
     * @return Map
     */
    default Map<String, Object> buildParameters(PrintContext printContext) {
        return Maps.newHashMap();
    }
}

