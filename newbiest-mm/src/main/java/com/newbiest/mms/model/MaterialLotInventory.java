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
     * 库存数量
     */
    @Column(name = "STOCK_QTY")
    private BigDecimal stockQty = BigDecimal.ZERO;

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
        return this;
    }

    public MaterialLotInventory setStorage(Storage storage) {
        this.setStorageRrn(storage.getObjectRrn());
        this.setStorageId(storage.getName());
        this.setStorageType(storage.getStorageType());
        return this;
    }


}
