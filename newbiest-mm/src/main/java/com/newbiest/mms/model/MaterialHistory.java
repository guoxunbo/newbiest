package com.newbiest.mms.model;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBVersionControlHis;
import com.newbiest.base.utils.SessionContext;
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
    private Long materialRrn;

    @Column(name="STATUS_MODEL_RRN")
    private Long statusModelRrn;

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
    private Long warehouseRrn;

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
     * 产品绑定的真空包包标准数量
     */
    @Column(name="RESERVED1")
    private String reserved1;

    @Column(name="RESERVED2")
    private String reserved2;

    @Column(name="RESERVED3")
    private String reserved3;

    @Column(name="RESERVED4")
    private String reserved4;

    @Column(name="RESERVED5")
    private String reserved5;

    @Column(name="RESERVED6")
    private String reserved6;

    @Column(name="RESERVED7")
    private String reserved7;

    @Column(name="RESERVED8")
    private String reserved8;

    @Column(name="RESERVED9")
    private String reserved9;

    @Column(name="RESERVED10")
    private String reserved10;

    /**
     * 备件规格
     */
    @Column(name="SPARE_SPECS")
    private String spareSpecs;

    /**
     * 备件型号
     */
    @Column(name="SPARE_MODEL")
    private String spareModel;

    /**
     * 备件线别
     */
    @Column(name="SPARE_PARTS_LINE")
    private String sparePartsLine;

    @Override
    public void setNbBase(NBBase base) {
        super.setNbBase(base);
        this.setMaterialRrn(base.getObjectRrn());
    }
}
