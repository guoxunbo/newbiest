package com.newbiest.mms.utils;

import java.math.BigDecimal;

/**
 * Created by guoxunbo on 2020-01-17 15:36
 */
@FunctionalInterface
public interface ToBigDecimalFunction<T> {
    BigDecimal applyAsBigDecimal(T value);
}
