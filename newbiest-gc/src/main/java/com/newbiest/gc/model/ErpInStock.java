package com.newbiest.gc.model;

import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 接收的时候给ERP写数据。
 * 一箱一行
 */
@Data
@Entity
@Table(name="ERP_IN_STOCK")
public class ErpInStock implements Serializable {

    public static final String C_MAKER_MES = "MES";

    @Column(name="SEQ")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long seq;

    /**
     * 采购/委外订单号
     */
    @Column(name="CCODE")
    private String cCode;

    /**
     * 入库时间
     */
    @Column(name="DDATE")
    private Date dDate = new Date();

    /**
     * MES供应商简称
     */
    @Column(name="SUBNAME")
    private String subName;

    /**
     * 二级代码
     */
    @Column(name="GCVERSION")
    private String gcVersion;

    /**
     * 存货编码
     */
    @Column(name="CINVCODE")
    private String cInvCode;

    /**
     * 产品分类
     */
    @Column(name="PRODCATE")
    private String prodCate;

    /**
     * wafer片数
     */
    @Column(name="WAFERQTY")
    private Integer waferQty;

    /**
     * 晶圆颗数（die数）
     */
    @Column(name="DIEQTY")
    private Integer dieQty;

    /**
     * 产品等级 对应mesPackedLot的grade
     */
    @Column(name="CGRAD")
    private String cGrad;

    /**
     * 保税属性 对应materialLot的reserved6
     */
    @Column(name="BBONDED")
    private String bBonded;

    /**
     * 备注 对应materialLot的reserved41
     */
    @Column(name="CMEMO")
    private String cMemo;

    /**
     * 导入仓库
     */
    @Column(name="WAREHOUSE")
    private String warehouse;

    /**
     * 创建人
     */
    @Column(name="CMAKER")
    private String cMaker = C_MAKER_MES;

    /**
     * 创建日期
     */
    @Column(name="DMAKEDATE")
    private Date dMakeDate = new Date();

    /**
     * 导入标识 Y:成功 ，N:失败（初始为空）
     */
    @Column(name="CFLAG")
    private String cFlag;

    /**
     * 出错原因
     */
    @Column(name="CERROR")
    private String cError;

    /**
     * 修改人
     */
    @Column(name="CMODIFY")
    private String cModify;

    /**
     * 修改时间
     */
    @Column(name="DMODIFYDATE")
    private Date dModifyDate;


    public void setMaterialLot(MaterialLot materialLot){
        this.setCCode(materialLot.getReserved27());
        this.setSubName(materialLot.getReserved22());
        this.setGcVersion(materialLot.getReserved1());
        this.setCInvCode(materialLot.getMaterialName());
        this.setWaferQty(materialLot.getCurrentSubQty().intValue());
        this.setDieQty(materialLot.getCurrentQty().intValue());
        this.setCGrad(materialLot.getGrade());
        this.setBBonded(materialLot.getReserved6());
        this.setCMemo(materialLot.getReserved41());
    }

}
