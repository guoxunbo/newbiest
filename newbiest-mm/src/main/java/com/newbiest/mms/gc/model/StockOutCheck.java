package com.newbiest.mms.gc.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by guoxunbo on 2019-08-28 17:54
 */
@Data
public class StockOutCheck implements Serializable {

    private String name;

    private String result;

}
