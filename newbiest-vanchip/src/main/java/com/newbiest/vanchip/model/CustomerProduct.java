package com.newbiest.vanchip.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.*;

/**
 * 客户产品信息。
 */
@Entity
@Data
@Table(name="VC_CUSTOMER_PRODUCT")
public class CustomerProduct extends NBBase {

    public static final String CUSTOMER_NAME_OPPO = "OPPO";

    /**
     * 产品型号
     */
    @Column(name = "PART_NUMBER")
    private String partNumber;

    /**
     * 供应商代码
     */
    @Column(name = "VENDOR_CODE")
    private String vendorCode;

    /**
     * 客户物料代码
     */
    @Column(name = "MATERIAL_CODE")
    private String materialCode;

    /**
     *客户物料描述
     */
    @Column(name = "MATERIAL_DESCRIPTION")
    private String materialDescription;

    /**
     * 客户名称
     */
    @Column(name = "CUSTOMER_NAME")
    private String customerName;

}