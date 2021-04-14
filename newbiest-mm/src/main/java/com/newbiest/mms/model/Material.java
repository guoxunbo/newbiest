package com.newbiest.mms.model;

import com.newbiest.base.model.NBVersionControl;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by guoxunbo on 2019/1/3.
 */
@Table(name="MMS_MATERIAL")
@Entity
@Data
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CLASS", discriminatorType = DiscriminatorType.STRING, length = 32)
public class Material extends NBVersionControl {

    private static final long serialVersionUID = -8075936261995774501L;

    /**
     * 原材料
     */
    public static final String CLASS_RAW = "RAW";

    /**
     * 产品型号
     */
    public static final String CLASS_PRODUCT = "PRODUCT";

    /**
     * 备件型号
     */
    public static final String CLASS_PARTS = "PARTS";

    /**
     * 默认状态模型
     */
    public static final String DEFAULT_STATUS_MODEL = "Normal";

    /**
     * FiFo = F 先进先出
     */
    public static final String DELIVERY_POLICY_FIFO = "FIFO";

    /**
     * LiFo = L 后进先出
     */
    public static final String DELIVERY_POLICY_LIFO = "LIFO";

    /**
     * RESIDUAL = R 余量优先
     */
    public static final String DELIVERY_POLICY_RESIDUAL = "RESIDUAL";

    /**
     * 先余量优先, 再先进先出
     */
    public static final String DELIVERY_POLICY_RESIDUAL_FIFO = "RF";

    /**
     * 先余量优先, 再后进先出
     */
    public static final String DELIVERY_POLICY_RESIDUAL_LIFO = "RL";

    /**
     * 物料类别
     */
    public static final String TYPE_PRODUCT = "Product";
    public static final String TYPE_WAFER = "Wafer";
    public static final String TYPE_MATERIAL = "Material";

    public static final String MATERIAL_TYPE = "Wafer";
    public static final String MATERIAL_TYPE_WIRE = "WIRE";
    public static final String MATERIAL_TYPE_IR = "IR";

    //RW辅料类型
    public static final String MATERIAL_TYPE_TAPE = "TAPE";
    public static final String MATERIAL_TYPE_BLADE = "BLADE";
    public static final String MATERIAL_SHIPPER_NAME = "Lintec";
    public static final String MATERIAL_DISCO = "DISCO";
    public static final String MATERIAL_PO_EMPTY = "NO PO";

    public static final String MATERIAL_TYPE_IRA = "IRA";
    public static final String MATERIAL_TYPE_GOLD = "金线";
    public static final String MATERIAL_TYPE_GLUE = "胶水";

    public static final String QUERY_WAFERTYPEINFO = "queryWaferType";
    public static final String QUERY_PRODUCTINFO = "GETPRODUCTINFO";
    public static final String QUERY_PRODUCT_SUBCODE = "queryProductSubcode";
    public static final String QUERY_PRODUCT_MODEL_CONVERSION = "queryProductModelConversion";
    public static final String QUERY_WAREHOUSE_PRODUCT_MODEL = "queryWareHouseProductModel";
    public static final String QUERY_PRODUCT_PRINT_MODELID = "queryProductPrintModelId";
    public static final String QUERY_MATERIAL_MODEL = "queryMaterialModelId";
    public static final String QUERY_GLUE_TYPE = "queryGlueType";

    @Column(name="CLASS",insertable = false, updatable = false)
    private String clazz;

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
    @Transient
    private String spareSpecs;

    /**
     * 备件型号
     */
    @Transient
    private String spareModel;

    /**
     * 备件线别
     */
    @Transient
    private String sparePartsLine;

    public void setParts (Parts parts) {
        this.setSpareModel(parts.getSpareModel());
        this.setSparePartsLine(parts.getSparePartsLine());
        this.setSpareSpecs(parts.getSpareSpecs());
    }
}
