package com.newbiest.mms.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 物料批次的具体单元数据。由一系列的单元组成一个物料批次。可以针对单元做操作。
 *  比如接收以单元的方式进行。组装个一个materiLot。
 *
 * Created by guoxunbo on 2020-01-17 10:53
 */
@Entity
@Table(name="MMS_MATERIAL_LOT_UNIT")
@Data
public class MaterialLotUnit extends NBUpdatable {

    public static final String STATE_CREATE = "Create";

    @Column(name="UNIT_ID")
    private String unitId;

    /**
     * 主物料批次主键
     */
    @Column(name="MATERIAL_LOT_RRN")
    private Long materialLotRrn;

    /**
     * 主物料批次号
     */
    @Column(name="MATERIAL_LOT_ID")
    private String materialLotId;

    /**
     * 状态
     */
    @Column(name="STATE")
    private String state = STATE_CREATE;

    /**
     * 导入时候的数量
     */
    @Column(name="RECEIVE_QTY")
    private BigDecimal receiveQty;

    /**
     * 当前数量
     */
    @Column(name="CURRENT_QTY")
    private BigDecimal currentQty;

    /**
     * 等级
     */
    @Column(name="GRADE")
    private String grade;

    /**
     * 指定工单号
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

}
