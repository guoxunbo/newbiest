package com.newbiest.gc.model;

import lombok.Data;

import java.io.Serializable;

/**
 * GC StockIn对象
 * Created by guoxunbo on 2019-09-23 18:10
 */
@Data
public class StockInModel implements Serializable {

    private String materialLotId;

    private String relaxBoxId;

    private String storageId;

}
