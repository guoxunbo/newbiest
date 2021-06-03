package com.newbiest.vanchip.dto.print.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShippingListBoxPrintInfo implements Serializable {

    private String ctn_idx;

    private String part_Number;
    private String box_material_lot;
    private String qty;



}
