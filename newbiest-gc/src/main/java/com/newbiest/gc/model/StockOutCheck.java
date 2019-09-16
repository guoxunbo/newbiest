package com.newbiest.gc.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 检查项的DTO
 * Created by guoxunbo on 2019-08-28 17:54
 */
@Data
public class StockOutCheck implements Serializable {

    /**
     * 装箱检要求是PASS/NG而非是OK/NG
     */
    public static final String RESULT_PASS = "PASS";

    public static final String RESULT_OK = "OK";
    public static final String RESULT_NG = "NG";

    private String name;

    private String result;

}
