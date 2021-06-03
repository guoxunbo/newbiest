package com.newbiest.vanchip.dto.print.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class PKListMLotPrintInfo implements Serializable {

    private String storageId;
    private String material_lot_id;
    private String unit_id;
    private String control_lot;
    private String qty;
    private String dc;

}
