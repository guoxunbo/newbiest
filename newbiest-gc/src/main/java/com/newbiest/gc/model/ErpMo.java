package com.newbiest.gc.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 接收的时候给ERP写数据。
 * 一个VBOX一行
 */
@Data
@Entity
@Table(name="ERP_MO")
public class ErpMo implements Serializable {

    public static final String C_VOUCH_TYPE_WO = "生产订单";

    public static final String DEFAULT_WO_ID = "ZJ0000000000";

    public static final String C_MAKER_MES = "MES";

    @Column(name="SEQ")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long seq;

    /**
     * 类型 写死生产订单
     */
    @Column(name="CVOUCHTYPE")
    private String cVouchType = C_VOUCH_TYPE_WO;

    /**
     * 单据编号。对应mesPackedLotLot里的shipSerialNumber
     */
    @Column(name="CCODE")
    private String cCode;

    /**
     * 入库时间
     */
    @Column(name="DDATE")
    private Date dDate;

    /**
     * 生产订单号 对应mesPackedLot的workorderId
     * workOrderId为空的时候，给默认值ZJ0000000000
     */
    @Column(name="CMOCODE")
    private String cmoCode;

    @Column(name="CRDCODE")
    private String cRDCode = "产成品入库";

    @Column(name="CWHCODE")
    private String cWHCode;

    @Column(name="CMOROW")
    private Integer CMOROW = 1;

    /**
     * 创建人 MES
     */
    @Column(name="CMAKER")
    private String cMaker = C_MAKER_MES;

    /**
     * 创建时间
     */
    @Column(name="DMAKEDATE")
    private Date dMakeDate = new Date();

    /**
     * 产品编码 对应mesPackedLot的productId
     */
    @Column(name="CINVCODE")
    private String cinVCode;

    /**
     * 产品数量 对应mesPackedLot的quantity
     */
    @Column(name="FQTY")
    private Integer fQty;

    /**
     * 产品等级 对应mesPackedLot的grade
     */
    @Column(name="CGRAD")
    private String cGrade;

    /**
     * 保税属性 对应mesPackedLot的bondedProperty
     */
    @Column(name="BBONDED")
    private String bonded;

    /**
     * 保税属性 对应mesPackedLot的secondcode
     */
    @Column(name="SECONDCODE")
    private String secondcode;


}
