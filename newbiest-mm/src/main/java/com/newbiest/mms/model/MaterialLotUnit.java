package com.newbiest.mms.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name="MMS_MATERIAL_LOT_UNIT")
@Data
public class MaterialLotUnit extends NBUpdatable {

    /**
     * RA生产订单
     */
    public static final String WORK_ORDER_TYPE_RA = "RA";

    /**
     * DETAPE生产订单
     */
    private static final String WORK_ORDER_TYPE_DETAPE = "DETAPE";

    /**
     * FT生产订单
     */
    private static final String WORK_ORDER_TYPE_FT = "FT";

    /**
     * REELABEL生产订单
     */
    private static final String WORK_ORDER_TYPE_REELABEL = "REELABEL";

    @Column(name="UNIT_ID")
    private String unitId;

    /**
     * 主物料批次主键
     */
    @Column(name="MATERIAL_LOT_RRN")
    private String materialLotRrn;

    /**
     * 主物料批次号
     */
    @Column(name="MATERIAL_LOT_ID")
    private String materialLotId;

    /**
     * 数量
     */
    @Column(name="QTY")
    private BigDecimal Qty = BigDecimal.ZERO;

    /**
     * 工单
     */
    @Column(name = "WORK_ORDER_ID")
    private String workOrderId;

    /**
     * 物料主键
     */
    @Column(name="MATERIAL_RRN")
    private String materialRrn;

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

    @Column(name = "GRADE")
    private String grade;

    /**
     * 行号
     */
    @Column(name="ITEM_ID")
    private String itemId;

    /**
     * 工单类型
     */
    @Column(name="WORK_ORDER_TYPE")
    private String workOrderType;

    /**
     * 唯捷生产订单号（唯捷测试订单号）
     */
    @Column(name="RESERVED1")
    private String reserved1;

    /**
     * D/C
     */
    @Column(name="RESERVED2")
    private String reserved2;

    /**
     * Part Number
     */
    @Column(name="RESERVED3")
    private String reserved3;

    /**
     * Control Lot
     */
    @Column(name="RESERVED4")
    private String reserved4;

    /**
     * MRB1 (原料自带的MRB)客户MRB
     */
    @Column(name="RESERVED5")
    private String reserved5;

    /**
     * MRB2 (生产产生的MRB)精测MRB
     */
    @Column(name="RESERVED6")
    private String reserved6;

    /**
     * 测试机台
     */
    @Column(name="RESERVED7")
    private String reserved7;

    /**
     * 测试程序
     */
    @Column(name="RESERVED8")
    private String reserved8;

    /**
     * Handler型号
     */
    @Column(name="RESERVED9")
    private String reserved9;

    /**
     * 来料工厂
     */
    @Column(name="RESERVED10")
    private String reserved10;

    /**
     * 客户
     */
    @Column(name="RESERVED11")
    private String reserved11;

    /**
     * 客户销售订单
     */
    @Column(name="RESERVED12")
    private String reserved12;

    /**
     * version
     */
    @Column(name="RESERVED13")
    private String reserved13;

    /**
     * contromer lot no
     */
    @Column(name="RESERVED14")
    private String reserved14;

    /**
     * Marking
     */
    @Column(name="RESERVED15")
    private String reserved15;

    /**
     * 客户RMA号
     */
    @Column(name="RESERVED16")
    private String reserved16;

    /**
     * 精测RMA号
     */
    @Column(name="RESERVED17")
    private String reserved17;

    /**
     * 客户订单编码(来料封装订单号)
     */
    @Column(name="RESERVED18")
    private String reserved18;

    /**
     * remark
     */
    @Column(name="RESERVED19")
    private String reserved19;

    /**
     * 保税手册号
     */
    @Column(name="RESERVED20")
    private String reserved20;

    /**
     * 销售订单号(内部销售订单号)
     */
    @Column(name="RESERVED21")
    private String reserved21;

    /**
     * 销售订单行号
     */
    @Column(name="RESERVED22")
    private String reserved22;

//    /**
//     * 来料批次号
//     */
//    @Column(name="INCOMING_MLOT_ID")
//    private String incomingMLotId;

    public void setMaterialLot(MaterialLot materialLot){
        this.setMaterialLotRrn(materialLot.getObjectRrn());
        this.setMaterialLotId(materialLot.getMaterialLotId());
    }

    public void setMaterial(Material material){
        this.setMaterialRrn(material.getObjectRrn());
        this.setMaterialName(material.getName());
        this.setMaterialDesc(material.getDescription());
        this.setMaterialVersion(material.getVersion());
        this.setMaterialCategory(material.getMaterialCategory());
        this.setMaterialType(material.getMaterialType());
        this.setStoreUom(material.getStoreUom());
    }
}
