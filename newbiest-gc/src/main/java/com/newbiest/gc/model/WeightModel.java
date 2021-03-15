package com.newbiest.gc.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class WeightModel implements Serializable  {

    private String materialLotId;

    private String weight;

    private  String boxsWeightFlag;

    private  String scanSeq;

    private  Long boxsScanSeq;

}
