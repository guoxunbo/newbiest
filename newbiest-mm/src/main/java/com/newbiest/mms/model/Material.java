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
     * 成品
     */
    public static final String CLASS_PRODUCT = "PRODUCT";

    /**
     * 实验室材料
     */
    public static final String CLASS_LAB = "LAB";

    /**
     * 默认状态模型
     */
    public static final String DEFAULT_STATUS_MODEL = "Normal";

    /**
     * FiFo = 先进先出
     */
    public static final String DELIVERY_POLICY_FIFO = "FIFO";

    /**
     * LiFo = 后进先出
     */
    public static final String DELIVERY_POLICY_LIFO = "LIFO";

    /**
     * RESIDUAL = 余量优先
     */
    public static final String DELIVERY_POLICY_RESIDUAL = "RESIDUAL";

    public static final String OQC_SHEET_NAME = "OQC出货检验记录";

    public static final String TYPE_PRODUCT = "Product";

    //MainMaterial PackingMaterial material Laboratory

    //成品类型
    public static final String MATERIAL_CATEGORY_PRODUCT = "Product";
    //主材类型
    public static final String MATERIAL_CATEGORY_MAIN_MATERIAL = "MainMaterial";
    //包材类型
    public static final String MATERIAL_CATEGORY_PACKING_MATERIAL = "PackingMaterial";
    //辅材类型
    public static final String MATERIAL_CATEGORY_MATERIAL = "Material";
    //实验室类型
    public static final String MATERIAL_CATEGORY_LABORATORY = "Laboratory";



    @Column(name="CLASS",insertable = false, updatable = false)
    private String clazz;

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
     * 默认仓库主键
     */
    @Column(name="WAREHOUSE_RRN")
    private String warehouseRrn;

    /**
     * 默认仓库
     */
    @Column(name="WAREHOUSE_NAME")
    private String warehouseName;

    /**
     * 有效时长
     */
    @Column(name="EFFECTIVE_LIFE")
    private Double effectiveLife;

    /**
     * 警告时长
     * 当达到此时长的时候触发警告
     */
    @Column(name="WARNING_LIFE")
    private Double warningLife;

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
     * IQC检查清单名称
     */
    @Column(name="IQC_SHEET_NAME")
    private String iqcSheetName;

    /**
     * OQC检查清单名称
     */
    @Column(name="OQC_SHEET_NAME")
    private String oqcSheetName;

    /**
     * 全称
     */
    @Column(name="FULL_NAME")
    private String fullName;

    /**
     * 大类描述
     */
    @Column(name="RESERVED1")
    private String reserved1;

    /**
     * 中类描述
     */
    @Column(name="RESERVED2")
    private String reserved2;

    /**
     * 小类/物料组
     */
    @Column(name="RESERVED3")
    private String reserved3;

    /**
     * 小类描述
     */
    @Column(name="RESERVED4")
    private String reserved4;

    /**
     *客户产品型号
     */
    @Column(name="RESERVED5")
    private String reserved5;

    /**
     *客户产品版本
     */
    @Column(name="RESERVED6")
    private String reserved6;

    /**
     *客户物料代码
     */
    @Column(name="RESERVED7")
    private String reserved7;

    /**
     *封装厂
     */
    @Column(name="RESERVED8")
    private String reserved8;

    /**
     *是否保税
     */
    @Column(name="RESERVED9")
    private String reserved9;

    /**
     *HS 编码
     */
    @Column(name="RESERVED10")
    private String reserved10;

    /**
     *产品尺寸
     */
    @Column(name="RESERVED11")
    private String reserved11;

    /**
     *是否IQC
     */
    @Column(name="RESERVED12")
    private String reserved12;

    /**
     *客户代码
     */
    @Column(name="RESERVED13")
    private String reserved13;

    /**
     *客户简称
     */
    @Column(name="RESERVED14")
    private String reserved14;

    /**
     *客户全称
     */
    @Column(name="RESERVED15")
    private String reserved15;

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
}
