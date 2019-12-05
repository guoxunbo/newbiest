package com.newbiest.mms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.dto.MaterialLotAction;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by guoxunbo on 2019/2/26.
 */
@Entity
@Table(name="MMS_MATERIAL_LOT_HIS")
@Data
public class MaterialLotHistory extends NBHis {

    public static final String TRANS_TYPE_RECEIVE = "Receive";
    public static final String TRANS_TYPE_STOCK_IN = "StockIn";
    public static final String TRANS_TYPE_STOCK_OUT = "StockOut";
    public static final String TRANS_TYPE_SHIP = "Ship";
    public static final String TRANS_TYPE_RESERVED = "Reserved";
    public static final String TRANS_TYPE_UN_RESERVED = "UnReserved";

    public static final String TRANS_TYPE_PICK = "Pick";
    public static final String TRANS_TYPE_TRANSFER = "Transfer";
    public static final String TRANS_TYPE_CHECK = "Check";
    public static final String TRANS_TYPE_CONSUME = "Consume";

    public static final String TRANS_TYPE_PACKAGE = "Package";
    public static final String TRANS_TYPE_UN_PACKAGE = "UnPackage";

    /**
     * 因为包装产生的批次
     */
    public static final String TRANS_TYPE_CREATE_PACKAGE = "CreatePackage";
    public static final String TRANS_TYPE_ADDITIONAL_PACKAGE = "AdditionalPackage";

    /**
     * 物料批次号
     */
    @Column(name="MATERIAL_LOT_ID")
    private String materialLotId;

    /**
     * 等级
     */
    @Column(name="GRADE")
    private String grade;

    /**
     * 状态模型主键
     * 从物料上携带
     */
    @Column(name="STATUS_MODEL_RRN")
    private Long statusModelRrn;

    /**
     * 状态大类
     */
    @Column(name="STATUS_CATEGORY")
    private String statusCategory;

    /**
     * 状态
     */
    @Column(name="STATUS")
    private String status;

    /**
     * 前置状态大类
     */
    @Column(name="PRE_STATUS_CATEGORY")
    private String preStatusCategory;

    /**
     * 前置状态
     */
    @Column(name="PRE_STATUS")
    private String preStatus;

    /**
     * Hold状态
     */
    @Column(name="HOLD_STATE")
    private String holdState;

    /**
     * 批次接收数量
     * 只做记录使用不做任何运算
     */
    @Column(name="RECEIVE_QTY")
    private BigDecimal receiveQty = BigDecimal.ZERO;

    /**
     * 主数量
     */
    @Column(name="CURRENT_QTY")
    private BigDecimal currentQty = BigDecimal.ZERO;

    /**
     * 预留数量
     */
    @Column(name="RESERVED_QTY")
    private BigDecimal reservedQty = BigDecimal.ZERO;

    /**
     * 如果是子批，对应的母批的批号
     */
    @Column(name="PARENT_MATERIAL_LOT_ID")
    private String parentMaterialLotId;

    /**
     * 如果是子批，对应的母批的主键
     */
    @Column(name="PARENT_MATERIAL_LOT_RRN")
    private Long parentMaterialLotRrn;

    /**
     * 是否是子批的标记
     */
    @Column(name="SUB_MATERIAL_LOT_FLAG")
    private String subMaterialLotFlag;

    /**
     * 物料批次类别，用来区分是普通物料批次还是包装物料批次
     */
    @Column(name="CATEGORY")
    private String category;

    /**
     * 包装类型 用于追加包装的时候进行验证
     */
    @Column(name="PACKAGE_TYPE")
    private String packageType;


    /**
     * 工单号。
     * 有些情况下工单会直接指定消耗的物料批次
     */
    @Column(name="WORK_ORDER_ID")
    private String workOrderId;

    /**
     * 物料主键
     */
    @Column(name="MATERIAL_RRN")
    private Long materialRrn;

    /**
     * 物料名称
     */
    @Column(name="MATERIAL_NAME")
    private String materialName;

    /**
     * 物料版本
     */
    @Column(name="MATERIAL_VERSION")
    private Long materialVersion;

    /**
     * 物料描述
     */
    @Column(name="MATERIAL_DESC")
    private String materialDesc;

    /**
     * 物料类别
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
     * 接收日期
     */
    @Column(name="RECEIVE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date receiveDate;

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
     * 操作数量
     */
    @Column(name="TRANS_QTY")
    private BigDecimal transQty;

    /**
     * 操作仓库
     */
    @Column(name="TRANS_WAREHOUSE_ID")
    private String transWarehouseId;

    /**
     * 操作库位类型
     */
    @Column(name="TRANS_STORAGE_TYPE")
    private String transStorageType;

    /**
     * 操作库位号
     */
    @Column(name="TRANS_STORAGE_ID")
    private String transStorageId;

    /**
     * 目标仓库
     */
    @Column(name="TARGET_WAREHOUSE_ID")
    private String targetWarehouseId;

    /**
     * 目标库位类型
     */
    @Column(name="TARGET_STORAGE_TYPE")
    private String targetStorageType;

    /**
     * 目标库位号
     */
    @Column(name="TARGET_STORAGE_ID")
    private String targetStorageId;

    /**
     * GlaxyCore MES完成品的levelTwoCode
     */
    @Column(name="RESERVED1")
    private String reserved1;

    /**
     * GlaxyCore MES完成品的waferId
     */
    @Column(name="RESERVED2")
    private String reserved2;

    /**
     * GlaxyCore MES完成品的salesNote
     */
    @Column(name="RESERVED3")
    private String reserved3;

    /**
     * GlaxyCore MES完成品的treasuryNote
     */
    @Column(name="RESERVED4")
    private String reserved4;

    /**
     * GlaxyCore MES完成品的productionNote
     */
    @Column(name="RESERVED5")
    private String reserved5;

    /**
     * GlaxyCore MES完成品的bondedProperty
     */
    @Column(name="RESERVED6")
    private String reserved6;

    /**
     * GlaxyCore MES完成品的productCategory
     */
    @Column(name="RESERVED7")
    private String reserved7;

    /**
     * GlaxyCore 中转箱号
     */
    @Column(name="RESERVED8")
    private String reserved8;

    /**
     * GlaxyCore 装箱检验判定等级
     */
    @Column(name="RESERVED9")
    private String reserved9;

    /**
     * GlaxyCore 装箱检验判定码
     */
    @Column(name="RESERVED10")
    private String reserved10;

    /**
     * GlaxyCore 重测之后的工单号
     */
    @Column(name="RESERVED11")
    private String reserved11;

    /**
     * GlaxyCore 单据号
     */
    @Column(name="RESERVED12")
    private String reserved12;

    @Column(name="RESERVED13")
    private String reserved13;

    @Column(name="RESERVED14")
    private String reserved14;

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

    public void setSubMaterialLotFlag(Boolean subMaterialLotFlag) {
        this.subMaterialLotFlag = subMaterialLotFlag ? StringUtils.YES : StringUtils.NO;
    }

    public boolean getSubMaterialLotFlag() {
        return StringUtils.YES.equalsIgnoreCase(this.subMaterialLotFlag);
    }

    public void buildByMaterialLotAction(MaterialLotAction materialLotAction) {
        this.setTransQty(materialLotAction.getTransQty());
        this.setActionCode(materialLotAction.getActionCode());
        this.setActionReason(materialLotAction.getActionReason());
        this.setActionComment(materialLotAction.getActionComment());
    }
}
