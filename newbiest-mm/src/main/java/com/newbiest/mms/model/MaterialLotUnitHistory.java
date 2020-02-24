package com.newbiest.mms.model;

import com.newbiest.base.model.NBHis;
import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 物料批次的单元数据。比如接收以单元的方式进行。组装个一个materiLot。
 *
 * Created by guoxunbo on 2020-01-17 10:53
 */
@Entity
@Table(name="MMS_MATERIAL_LOT_UNIT_HIS")
@Data
public class MaterialLotUnitHistory extends NBHis {

    public static final String TRANS_TYPE_IN = "In";

    @Column(name="UNIT_ID")
    private String unitId;

    @Column(name="MATERIAL_LOT_RRN")
    private Long materialLotRrn;

    @Column(name="MATERIAL_LOT_ID")
    private String materialLotId;

    @Column(name="STATE")
    private String state;

    @Column(name="RECEIVE_QTY")
    private BigDecimal receiveQty;

    @Column(name="CURRENT_QTY")
    private BigDecimal currentQty;

    @Column(name="TRANS_QTY")
    private BigDecimal transQty;

    @Column(name="GRADE")
    private String grade;

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
     * 二级代码
     */
    @Column(name="RESERVED1")
    private String reserved1;

    /**
     * 载具号
     */
    @Column(name="RESERVED2")
    private String reserved2;

    /**
     * 载具晶圆数量
     */
    @Column(name="RESERVED3")
    private BigDecimal reserved3;

    /**
     * 供应商
     */
    @Column(name="RESERVED4")
    private String reserved4;

    /**
     * 出货厂商
     */
    @Column(name="RESERVED5")
    private String reserved5;

    /**
     *生产订单
     */
    @Column(name="RESERVED6")
    private String reserved6;

    /**
     * 发票号
     */
    @Column(name="RESERVED7")
    private String reserved7;

    /**
     * 包装箱号
     */
    @Column(name="RESERVED8")
    private String reserved8;

    /**
     *备注
     */
    @Column(name="RESERVED9")
    private String reserved9;

    /**
     * 硅厚
     */
    @Column(name="RESERVED10")
    private BigDecimal reserved10;

    /**
     * 晶圆等级
     */
    @Column(name="RESERVED11")
    private String reserved11;

    /**
     * 保税属性
     */
    @Column(name="RESERVED12")
    private String reserved12;

    /**
     * 供货商出货时间
     */
    @Column(name="RESERVED13")
    private String reserved13;

    /**
     * 委外订单
     */
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
}
