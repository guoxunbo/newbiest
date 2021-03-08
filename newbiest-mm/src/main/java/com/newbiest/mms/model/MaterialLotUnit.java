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
     * PO NO
     */
    @Column(name="RESERVED1")
    private String reserved1;

    /**
     * D/C
     */
    @Column(name="RESERVED2")
    private String reserved2;



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
