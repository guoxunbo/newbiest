package com.newbiest.gc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLotUnit;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 格科盘点历史表
 * Created by guoxunbo on 2019/2/26.
 */
@Entity
@Table(name="GC_CHECK_HISTORY")
@Data
public class CheckHistory extends NBHis {

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
     * 操作数量
     */
    @Column(name="TRANS_QTY")
    private BigDecimal transQty;

    @Column(name="ERROR_FLAG")
    private String errorFlag;

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
     * GlaxyCore 晶圆号
     */
    @Column(name="UNIT_ID")
    private String unitId;

    /**
     * 载具号aliasId
     */
    @Column(name="LOT_ID")
    private String lotId;

    public Boolean getErrorFlag() {
        return StringUtils.YES.equalsIgnoreCase(errorFlag);
    }

    public void setErrorFlag(Boolean errorFlag) {
        this.errorFlag = errorFlag ? StringUtils.YES : StringUtils.NO;
    }

    public void setSubMaterialLotFlag(Boolean subMaterialLotFlag) {
        this.subMaterialLotFlag = subMaterialLotFlag ? StringUtils.YES : StringUtils.NO;
    }

    public boolean getSubMaterialLotFlag() {
        return StringUtils.YES.equalsIgnoreCase(this.subMaterialLotFlag);
    }

    public void setMaterialLotUnit(MaterialLotUnit materialLotUnit){
        this.setUnitId(materialLotUnit.getUnitId());
        this.setMaterialLotId(materialLotUnit.getMaterialLotId());
        this.setCurrentQty(materialLotUnit.getCurrentQty());
        this.setGrade(materialLotUnit.getGrade());
        this.setReceiveQty(materialLotUnit.getReceiveQty());
        this.setMaterialName(materialLotUnit.getMaterialName());
        this.setStatus(materialLotUnit.getState());
        this.setMaterialCategory(materialLotUnit.getMaterialCategory());
        this.setMaterialDesc(materialLotUnit.getMaterialDesc());
        this.setMaterialRrn(materialLotUnit.getMaterialRrn());
        this.setMaterialType(materialLotUnit.getMaterialType());
        this.setMaterialVersion(materialLotUnit.getMaterialVersion());
        this.setReserved1(materialLotUnit.getReserved1());
        this.setReserved6(materialLotUnit.getReserved4());
        this.setWorkOrderId(materialLotUnit.getWorkOrderId());
        this.setStoreUom(materialLotUnit.getStoreUom());
        this.setLotId(materialLotUnit.getLotId());
    }
}
