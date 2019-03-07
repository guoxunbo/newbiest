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

    @Column(name = "WAREHOUSE_RRN")
    private Long warehouseRrn;

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


}
