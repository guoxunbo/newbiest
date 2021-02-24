package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue(Material.CLASS_PRODUCT)
public class Product extends Material {

    /**
     * 默认OQC出货检验记录
     */
    public static final String OQC_SHEET_RRN = "8395";

}
