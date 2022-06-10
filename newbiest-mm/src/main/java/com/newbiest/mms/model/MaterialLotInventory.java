package com.newbiest.mms.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * 物料库存表
 *      支持一个物料在多个库存中
 * Created by guoxunbo on 2019/2/28.
 */
@Data
@Entity
@Table(name="MMS_MATERIAL_LOT_INVENTORY")
public class MaterialLotInventory extends NBUpdatable {

    private static final long serialVersionUID = 677152248659968811L;

    public static final String SH_DEFAULT_STORAGE_ID = "HJ AZ5000";
    public static final String ZSH_DEFAULT_STORAGE_ID = "ZHJ AZ6000";

    @Column(name = "MATERIAL_LOT_RRN")
    private Long materialLotRrn;

    @Column(name = "MATERIAL_LOT_ID")
    private String materialLotId;

    @Column(name = "MATERIAL_NAME")
    private String materialName;

    @Column(name = "MATERIAL_DESC")
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

    @Column(name = "WAREHOUSE_RRN")
    private Long warehouseRrn;

    @Column(name = "WAREHOUSE_ID")
    private String warehouseId;

    @Column(name = "STORAGE_RRN")
    private Long storageRrn;

    /**
     * 库位类型
     */
    @Column(name="STORAGE_TYPE")
    private String storageType;

    /**
     * 库位号
     */
    @Column(name="STORAGE_ID")
    private String storageId;

    /**
     * 载具号aliasId
     */
    @Column(name="LOT_ID")
    private String lotId;

    /**
     * 库存数量
     */
    @Column(name = "STOCK_QTY")
    private BigDecimal stockQty = BigDecimal.ZERO;

    /**
     * die数量
     */
    @Column(name = "CURRENT_SUB_QTY")
    private BigDecimal currentSubQty = BigDecimal.ZERO;

    /**
     * 备件规格
     */
    @Column(name="RESERVED1")
    private String reserved1;

    /**
     * 备件型号
     */
    @Column(name="RESERVED2")
    private String reserved2;

    /**
     * 备件线别
     */
    @Column(name="RESERVED3")
    private String reserved3;

    @Column(name="RESERVED4")
    private String reserved4;

    @Column(name="RESERVED5")
    private String reserved5;

    public MaterialLotInventory setWarehouse(Warehouse warehouse) {
        this.setWarehouseRrn(warehouse.getObjectRrn());
        this.setWarehouseId(warehouse.getName());
        return this;
    }

    public MaterialLotInventory setMaterialLot(MaterialLot materialLot) {
        this.setMaterialLotRrn(materialLot.getObjectRrn());
        this.setMaterialLotId(materialLot.getMaterialLotId());
        this.setMaterialName(materialLot.getMaterialName());
        this.setMaterialDesc(materialLot.getMaterialDesc());
        this.setMaterialType(materialLot.getMaterialType());
        this.setMaterialCategory(materialLot.getMaterialCategory());
        this.setLotId(materialLot.getLotId());
        this.setCurrentSubQty(materialLot.getCurrentSubQty());
        this.setReserved1(materialLot.getReserved58());
        this.setReserved2(materialLot.getReserved59());
        this.setReserved3(materialLot.getReserved60());
        return this;
    }

    public MaterialLotInventory setStorage(Storage storage) {
        this.setStorageRrn(storage.getObjectRrn());
        this.setStorageId(storage.getName());
        this.setStorageType(storage.getStorageType());
        return this;
    }


}
