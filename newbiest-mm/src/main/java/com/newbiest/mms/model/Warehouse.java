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

    public static final String HOLD_WAREHOUSE_DESCRIPTION = "Hold仓";

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
