package com.newbiest.gc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 发料信息记录表
 * Created by guozhangLuo on 2021-01-18
 */
@Data
@Entity
@Table(name="ERP_MATERIAL_IN")
public class ErpMaterialIn implements Serializable {

    public static final String DEFAULT_CMAKER = "MES";

    @Column(name="SEQ")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long seq;

    /**
     * 采购/委外订单号
     */
    @Column(name="CCODE")
    private String ccode;

    /**
     * 单据日期
     */
    @Column(name="DDATE")
    @JsonFormat(timezone = "GMT+8",pattern = DateUtils.DEFAULT_DATE_PATTERN)
    @Temporal(TemporalType.TIMESTAMP)
    private Date ddate = new Date();

    /**
     * MES供应商简称
     */
    @Column(name="SUBNAME")
    private String subname;

    /**
     * 二级代码
     */
    @Column(name="GCVERSION")
    private String gcversion;

    /**
     * 存货编码
     */
    @Column(name="CINVCODE")
    private String cinvcode;

    /**
     * 产品分类
     */
    @Column(name="PRODCATE")
    private String prodcate;

    /**
     * 数量
     */
    @Column(name="IQUANTITY")
    private String iquantity;

    /**
     * die数
     */
    @Column(name="DIEQTY")
    private Double dieqty = 0.0;

    /**
     * 等级
     */
    @Column(name="CGRAD")
    private String cgrad;

    /**
     * 物流
     */
    @Column(name="BBONDED")
    private String bbonded;

    /**
     * 备注
     */
    @Column(name="CMEMO")
    private String cmemo;

    /**
     * 仓库
     */
    @Column(name="WAREHOUSE")
    private String warehouse;

    /**
     * 创建人
     */
    @Column(name="CMAKER")
    private String cmaker;

    /**
     * 创建日期
     */
    @Column(name="DMAKEDATE")
    private Date dmakerdate = new Date();

    /**
     * U8导入标识
     */
    @Column(name="CFLAG")
    private String cflag;

    /**
     * 出错原因
     */
    @Column(name="CERROR")
    private String cerror;

    /**
     * 修改人
     */
    @Column(name="CMODIFY")
    private String cmodify;

    /**
     * 修改日期
     */
    @Column(name="DMODIFYDATE")
    private Date dmodifydate;

    /**
     * 导入类型
     */
    @Column(name="PLTYPE")
    private String pltype;

    public void setMaterialLot(MaterialLot materialLot){
        this.setCcode(materialLot.getReserved27());
        this.setSubname(materialLot.getReserved22());
        this.setGcversion(materialLot.getReserved1());
        this.setCinvcode(materialLot.getMaterialName());
        this.setProdcate(materialLot.getProductType());
        this.setIquantity(materialLot.getCurrentQty().toString());
        this.setCgrad(materialLot.getGrade());
        this.setBbonded(materialLot.getReserved6());
        this.setCmemo(materialLot.getReserved41());
        this.setPltype(materialLot.getReserved49());
        this.setCmaker(DEFAULT_CMAKER);
        if(MaterialLot.ZJ_WAREHOUSE.equals(materialLot.getReserved13())){
            this.setWarehouse(MaterialLot.ZJ_STOCK);
        } else if(MaterialLot.HK_WAREHOUSE.equals(materialLot.getReserved13())){
            this.setWarehouse(MaterialLot.SH_STOCK);
        } else if(MaterialLot.SH_WAREHOUSE.equals(materialLot.getReserved13())){
            this.setWarehouse(MaterialLot.HK_STOCK);
        }

    }
}
