package com.newbiest.gc.model;

import com.newbiest.mms.model.MaterialLot;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 发料信息记录表
 * Created by guozhangLuo on 2021-01-18
 */
@Data
@Entity
@Table(name="ERP_MATERIAL_OUT")
public class ErpMaterialOut implements Serializable {

    public static final String DEFAULT_CMAKER = "MES";
    public static final String DEFAULT_FQTY = "1";

    @Column(name="SEQ")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long seq;

    /**
     * 产品分类
     */
    @Column(name="PRODUCT_CLASSIFY")
    private String productClassify;

    /**
     * 单据编号
     */
    @Column(name="CMOCODE")
    private String cmocode;

    /**
     * 产品型号
     */
    @Column(name="CINVCODE")
    private String cinvcode;

    /**
     * 片数
     */
    @Column(name="FQTY")
    private String fqty;

    /**
     * 等级
     */
    @Column(name="CGRAD")
    private String cgrad;

    /**
     * 保税属性
     */
    @Column(name="BBONDED")
    private String bbonded;

    /**
     * 二级代码
     */
    @Column(name="GCVERSION")
    private String gcversion;

    /**
     * 颗数
     */
    @Column(name="MATERIALQTY")
    private String materialQty;

    /**
     * 备注
     */
    @Column(name="CMEMO")
    private String cmemo;

    /**
     * 创建人
     */
    @Column(name="CMAKER")
    private String cmaker;

    /**
     * 创建时间
     */
    @Column(name="DMAKEDATE")
    private String dmakerdate;

    /**
     * 仓库
     */
    @Column(name="WAREHOUSE")
    private String warehouse;

    @Column(name="CFLAG")
    private String cflag;

    @Column(name="UPDATE_TIME")
    private String updateTime;

    @Column(name="CERROR")
    private String cerroe;

    @Column(name="PRODCATE")
    private String prodcate;

    public void setMaterialLot(MaterialLot materialLot){
        this.setProductClassify(materialLot.getReserved49());
        this.setCinvcode(materialLot.getMaterialName());
        this.setCgrad(materialLot.getGrade());
        this.setBbonded(materialLot.getReserved6());
        this.setGcversion(materialLot.getReserved1());
        this.setProdcate(materialLot.getProductType());
        this.setCmaker(DEFAULT_CMAKER);
        if(MaterialLot.ZJ_WAREHOUSE.equals(materialLot.getReserved13())){
            this.setWarehouse(MaterialLot.ZJ_STOCK);
        } else if(MaterialLot.SH_WAREHOUSE.equals(materialLot.getReserved13())){
            this.setWarehouse(MaterialLot.SH_STOCK);
        } else if(MaterialLot.HK_WAREHOUSE.equals(materialLot.getReserved13())){
            this.setWarehouse(MaterialLot.HK_STOCK);
        }
    }
}
