package com.newbiest.gc.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by guoxunbo on 2019-08-28 17:54
 */
@Data
public class StockOutCheck implements Serializable {

    public static final String RESULT_OK = "OK";
    public static final String RESULT_NG = "NG";

    private String name;

    private String result;

}
