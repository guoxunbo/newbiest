package com.newbiest.mms.model;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBVersionControlHis;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by guoxunbo on 2019/1/3.
 */
@Table(name="MMS_MATERIAL_HIS")
@Entity
@Data
public class MaterialHistory extends NBVersionControlHis {

    private static final long serialVersionUID = -8075936261995774501L;

    @Column(name="MATERIAL_RRN")
    private String materialRrn;

    @Column(name="STATUS_MODEL_RRN")
    private String statusModelRrn;

    /**
     * 物料类别
     *
     */
    @Column(name="MATERIAL_CATEGORY")
    private String materialCategory;

    /**
     * 物料类型
     */
    @Column(name="MATERIAL_TYPE")
    private String materialType;

    /**
     * 库存单位
     */
    @Column(name="STORE_UOM")
    private String storeUom;

    /**
     * 安全库存量
     */
    @Column(name="SAFETY_STORE_QTY")
    private BigDecimal safetyStoreQty;

    /**
     * 最大库存量
     */
    @Column(name="MAX_STORE_QTY")
    private BigDecimal maxStoreQty;

    /**
     * 默认仓库
     */
    @Column(name="WAREHOUSE_RRN")
    private String warehouseRrn;

    /**
     * 有效时长
     */
    @Column(name="EFFECTIVE_LIFE")
    private Long effectiveLife;

    /**
     * 警告时长
     * 当达到此时长的时候触发警告
     */
    @Column(name="WARNING_LIFE")
    private Long warningLife;

    /**
     * 有效时长单位
     */
    @Column(name="EFFECTIVE_UNIT")
    private String effectiveUnit;

    /**
     * 物料出库策略
     * 给与提醒，不参与直接决策
     */
    @Column(name="DELIVERY_POLICY")
    private String deliveryPolicy;

    /**
     * IQC 表单
     */
    @Column(name="IQC_SHEET_RRN")
    private String iqcSheetRrn;

    /**
     * OQC 表单
     */
    @Column(name="OQC_SHEET_RRN")
    private String oqcSheetRrn;

    /**
     * 分类
     */
    @Column(name="RESERVED1")
    private String reserved1;

    /**
     * 产品类别
     */
    @Column(name="RESERVED2")
    private String reserved2;

    /**
     * 物料类别
     */
    @Column(name="RESERVED3")
    private String reserved3;

    /**
     * 客户产品
     */
    @Column(name="RESERVED4")
    private String reserved4;

    /**
     *客户版本1
     */
    @Column(name="RESERVED5")
    private String reserved5;

    /**
     *规格型号
     */
    @Column(name="RESERVED6")
    private String reserved6;

    /**
     *产品系列
     */
    @Column(name="RESERVED7")
    private String reserved7;

    /**
     *品牌
     */
    @Column(name="RESERVED8")
    private String reserved8;

    /**
     *有效期
     */
    @Column(name="RESERVED9")
    private String reserved9;

    /**
     *是否保税
     */
    @Column(name="RESERVED10")
    private String reserved10;

    /**
     *二级分类
     */
    @Column(name="RESERVED11")
    private String reserved11;

    /**
     *供应商代码
     */
    @Column(name="RESERVED12")
    private String reserved12;

    /**
     *产品尺寸
     */
    @Column(name="RESERVED13")
    private String reserved13;

    /**
     *客户代码
     */
    @Column(name="RESERVED14")
    private String reserved14;

    /**
     *客户简称
     */
    @Column(name="RESERVED15")
    private String reserved15;

    /**
     *客户全称
     */
    @Column(name="RESERVED16")
    private String reserved16;

    @Column(name="RESERVED17")
    private String reserved17;

    @Column(name="RESERVED18")
    private String reserved18;

    @Column(name="RESERVED19")
    private String reserved19;

    @Column(name="RESERVED20")
    private String reserved20;

    @Column(name="RESERVED21")
    private String reserved21;

    @Column(name="RESERVED22")
    private String reserved22;

    @Column(name="RESERVED23")
    private String reserved23;

    @Override
    public void setNbBase(NBBase base) {
        super.setNbBase(base);
        this.setMaterialRrn(base.getObjectRrn());
    }
}
