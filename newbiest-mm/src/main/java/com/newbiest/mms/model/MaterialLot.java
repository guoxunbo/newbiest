package com.newbiest.mms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.commom.sm.model.StatusLifeCycle;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.state.model.MaterialStatusModel;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by guoxunbo on 2019/2/26.
 */
@Entity
@Table(name="MMS_MATERIAL_LOT")
@Data
public class MaterialLot extends NBUpdatable implements StatusLifeCycle{

    /**
     * 生成物料批次号的规则
     */
    public static final String GENERATOR_MATERIAL_LOT_ID_RULE = "CreateMLot";

    /**
     * 生成
     */
    public static final String GENERATOR_SUB_MATERIAL_LOT_ID_RULE = "CreateSubMLot";

    public static final String HOLD_STATE_ON = "On";
    public static final String HOLD_STATE_OFF = "Off";

    /**
     * 物料批次号
     */
    @Column(name="MATERIAL_LOT_ID")
    private String materialLotId;

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
    private String holdState = HOLD_STATE_OFF;

    /**
     * 批次接收数量
     * 只做记录使用不做任何运算
     */
    @Column(name="RECEIVE_QTY")
    private BigDecimal receiveQty = BigDecimal.ZERO;

    /**
     * 当前数量
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
    @Column(name="PARENT_MLOT_ID")
    private String parentMLotId;

    /**
     * 如果是子批，对应的母批的主键
     */
    @Column(name="PARENT_MLOT_RRN")
    private String parentMLotRrn;

    /**
     * 是否是子批的标记
     */
    @Column(name="SUB_MLOT_FLAG")
    private String subMLotFlag;

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
     * 验证物料批次是否在有效期内
     */
    public void validationEffective() {
        if (effectiveLife != null && !StringUtils.isNullOrEmpty(effectiveUnit)) {
            Date effectiveDate = DateUtils.plus(receiveDate, effectiveLife.intValue(), effectiveUnit);
            if (!effectiveDate.after(new Date())) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_HAS_EXPIRED);
            }
        }
    }

    @Override
    public String getSubStatus() {
        return null;
    }

    @Override
    public String getPreSubStatus() {
        return null;
    }

    @Override
    public void setSubStatus(String subState) {

    }

    @Override
    public void setPreSubStatus(String subStatus) {

    }

    /**
     * 验证了批次是否被Hold 如果被Hold则抛出异常
     * @throws ClientException
     */
    public MaterialLot validateMLotHold() throws ClientException{
        if (HOLD_STATE_ON.equals(holdState)) {
            throw new ClientException(MmsException.MM_MATERIAL_LOT_ALREADY_HOLD);
        }
        return this;
    }

}
