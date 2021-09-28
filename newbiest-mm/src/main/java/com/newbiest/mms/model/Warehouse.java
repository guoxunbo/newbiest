package com.newbiest.mms.model;

import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.StringUtils;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by guoxunbo on 2019/2/27.
 */
@Data
@Entity
@Table(name="MMS_WAREHOUSE")
public class Warehouse extends NBUpdatable{

    private static final long serialVersionUID = -4573637477819603368L;

    //hold仓库类型
    public static final String WAREHOUSE_TYPE_HOLD = "Hold";
    //原材料仓库类型
    public static final String WAREHOUSE_TYPE_RAW_MATERIAL = "RawMaterial";
    //完成品仓库类型
    public static final String WAREHOUSE_TYPE_PRODUCT = "Product";
    //实验室仓库类型
    public static final String WAREHOUSE_TYPE_LABORATORY = "Laboratory";
    //不良品仓库类型
    public static final String WAREHOUSE_TYPE_REJECTS= "Rejects";
    //包材仓库类型
    public static final String WAREHOUSE_TYPE_PACKING_MATERIAL = "PackingMaterial";
    //备件仓库类型
    public static final String WAREHOUSE_TYPE_PARTS = "Parts";
    //RA仓库类型
    public static final String WAREHOUSE_TYPE_RA = "RA";
    //RMA来料仓库类型
    public static final String WAREHOUSE_TYPE_RMA = "RMA";

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="WAREHOUSE_TYPE")
    private String warehouseType;

    @Column(name="WAREHOUSE_GROUP")
    private String warehouseGroup;

    @Column(name="VIRTUAL_FLAG")
    private String virtualFlag;

    /**
     * 是否发送数据给其他系统 比如WMS。ERP等等
     */
    @Column(name="TRANSFER_DATA_FLAG")
    private String transferDataFlag = StringUtils.NO;

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

    public Boolean getVirtualFlag() {
        return StringUtils.YES.equalsIgnoreCase(virtualFlag);
    }

    public void setVirtualFlag(Boolean virtualFlag) {
        this.virtualFlag = virtualFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getTransferDataFlag() {
        return StringUtils.YES.equalsIgnoreCase(transferDataFlag);
    }

    public void setTransferDataFlag(Boolean transferDataFlag) {
        this.transferDataFlag = transferDataFlag ? StringUtils.YES : StringUtils.NO;
    }
}
