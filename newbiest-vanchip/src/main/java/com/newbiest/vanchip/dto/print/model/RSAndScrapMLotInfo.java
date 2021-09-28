package com.newbiest.vanchip.dto.print.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RSAndScrapMLotInfo implements Serializable {

    private String storageId;
    private String materialLotId;
    private String materialName;
    private String materialDesc;
    private String qty;
    private String storeUom;
    private String id;

}
