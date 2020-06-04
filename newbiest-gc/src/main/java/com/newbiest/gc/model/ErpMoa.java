package com.newbiest.gc.model;

import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 产线入库的时候给ERP写数据。
 * 一箱一行（WLT\CP\FT）
 */
@Data
@Entity
@Table(name="ERP_MOA")
public class ErpMoa implements Serializable {

    public static final String C_MAKER_MES = "MES";

    @Column(name="SEQ")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long seq;

    /**
     * 产线
     */
    @Column(name="PRODUCT_CLASSIFY")
    private String productClassify;

    /**
     * 生产订单号
     */
    @Column(name="CMOCODE")
    private String cMOCode;

    /**
     * 生产编码
     */
    @Column(name="CINVCODE")
    private String cInvCode;

    /**
     * 产品数量
     */
    @Column(name="FQTY")
    private Integer fQty;

    /**
     * 产品等级
     */
    @Column(name="CGRAD")
    private String cGrad;

    /**
     * 保税属性
     */
    @Column(name="BBONDED")
    private String bBonded;

    /**
     * 二级代码
     */
    @Column(name="GCVERSION")
    private String gcVersion;

    /**
     * 物料编码
     */
    @Column(name="MATERIALCODE")
    private String materialCode;

    /**
     * 物料数量
     */
    @Column(name="MATERIALQTY")
    private Integer materialQty;

    /**
     * 物料二级代码
     */
    @Column(name="MATERIALVERSION")
    private String materialVersion;

    /**
     * 物料保税属性
     */
    @Column(name="MATERIALBONDED")
    private String materialBonded;

    /**
     * 物料等级
     */
    @Column(name="MATERIALGRADE")
    private String materialGrade;

    /**
     * 备注
     */
    @Column(name="CMEMO")
    private String cMemo;

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
     * 导入仓库
     */
    @Column(name="WAREHOUSE")
    private String warehouse;

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
     * 修改时间
     */
    @Column(name="UPDATE_TIME")
    private Date updateTime;


    public void setMesPackedLot(MesPackedLot mesPackedLot){
        this.setCGrad(mesPackedLot.getGrade());
        this.setBBonded(mesPackedLot.getBondedProperty());
        this.setGcVersion(mesPackedLot.getLevelTwoCode());
        this.setProductClassify(mesPackedLot.getProductCategory());
        this.setCMOCode(mesPackedLot.getShipSerialNumber());
        this.setCInvCode(mesPackedLot.getProductId());
    }

}
