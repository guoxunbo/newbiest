package com.newbiest.gc.model;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 其他线
 */
@Data
@Entity
@Table(name="ERP_SOA")
public class ErpSoa implements Serializable {

    public static final String SOURCE_TABLE_NAME = "ERP_SOA";

    /**
     * 未同步
     */
    public static final String SYNC_STATUS_NOT_ASYNC = "0";

    /**
     * 同步成功但还未操作
     */
    public static final String SYNC_STATUS_SYNC_SUCCESS = "1";

    /**
     * 同步失败。
     */
    public static final String SYNC_STATUS_SYNC_ERROR = "2";

    /**
     * 已经操作过。此状态下不管ERP是否修改过数据，都不同步数据
     */
    public static final String SYNC_STATUS_OPERATION = "3";

    /**
     * ERP修改过后
     */
    public static final String SYNC_STATUS_CHANGED = "4";

    @Column(name="SEQ")
    @Id
    private Long seq;

    /**
     * ERP单号
     */
    @Column(name="SOCODE")
    private String socode;

    /**
     * 创建日期
     */
    @Column(name="ORDER_DATE")
    private String orderDate;

    /**
     * 产品型号
     */
    @Column(name="INVCODE")
    private String invcode;

    /**
     * 数量
     */
    @Column(name="QUANTITY")
    private BigDecimal quantity;

    /**
     * 二级代码
     */
    @Column(name="BATCH")
    private String batch;

    /**
     * 等级
     */
    @Column(name="FREE2")
    private String free2;

    /**
     * 主计量
     */
    @Column(name="FREE3")
    private String free3;

    /**
     * 制单人
     */
    @Column(name="SHIP_MAKER")
    private String shipMaker;

    /**
     * 审核人
     */
    @Column(name="SHIP_VERIFIER")
    private String shipVerifier;

    /**
     * 客户名称
     */
    @Column(name="CUSNAME")
    private String cusname;

    /**
     * 同步状态
     */
    @Column(name="SYN_STATUS")
    private String synStatus;

    /**
     * 错误码
     */
    @Column(name="ERROR_MEMO")
    private String errorMemo;

    /**
     * 已发货数量
     */
    @Column(name="DELIVERED_NUM")
    private String deliveredNum;

    @Column(name="OTHER16")
    private String other16;

    @Column(name="OTHER17")
    private String other17;

    @Column(name="CUSABBNAME")
    private String cusabbName;

    @Column(name="MEMO")
    private String memo;

    @Column(name="ITEMCODE")
    private String itemcode;

    @Column(name="SHIPADDRESS")
    private String shipAddress;

    /**
     * 客户联系人
     */
    @Column(name="CUSPERSON")
    private String cusperson;

    /**
     * 创建人
     */
    @Column(name="USER_ID")
    private String userId;

    /**
     * 未发货数量
     */
    @Column(name="LEFT_NUM")
    private BigDecimal leftNum;

    /**
     * 客户联系电话
     */
    @Column(name="CUSPHONE")
    private String cusphone;

    /**
     * odm
     */
    @Column(name="ODM")
    private String odm;

    /**
     * 已处理数量
     */
    @Column(name="FHQUANTITY")
    private String fhquantity;

    /**
     * 关联ERP grade
     */
    @Column(name="GRADE")
    private String grade;



    @Column(name="OTHER3")
    private String other3;

    /**
     * 快递单号
     */
    @Column(name="OTHER4")
    private String other4;

    @Column(name="OTHER6")
    private String other6;

    @Column(name="OTHER8")
    private String other8;

    @Column(name="OTHER9")
    private String other9;

    @Column(name="OTHER10")
    private String other10;

    @Column(name="OTHER11")
    private String other11;

    @Column(name="OTHER12")
    private String other12;

    @Column(name="OTHER13")
    private String other13;

    /**
     * ERP发货地
     */
    @Column(name="OTHER18")
    private String Other18;

    /**
     * 出库备注
     */
    @Column(name="OTHER19")
    private String Other19;

    @Column(name="CREATE_SEQ")
    private String createSeq;

}