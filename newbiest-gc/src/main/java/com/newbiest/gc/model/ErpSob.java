package com.newbiest.gc.model;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 其他发货单据
 * 发货单 Type为SO
 */
@Data
@Entity
@Table(name="ERP_SOB")
public class ErpSob implements Serializable {

    public static final String SOURCE_TABLE_NAME = "ERP_SOB";

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

    /**
     * WMS合单之后
     */
    public static final String SYNC_STATUS_MERGE = "9";

    @Column(name="SEQ")
    @Id
    private Long seq;

    /**
     * ERP单号
     */
    @Column(name="CCODE")
    private String ccode;

    /**
     * 创建日期
     */
    @Column(name="DDATE")
    private String ddate;

    /**
     * 产品型号
     */
    @Column(name="CINVCODE")
    private String cinvcode;

    /**
     * 创建人
     */
    @Column(name="USER_ID")
    private String userId;

    /**
     * 数量
     */
    @Column(name="IQUANTITY")
    private BigDecimal iquantity;

    /**
     * 未发货数量
     */
    @Column(name="LEFT_NUM")
    private BigDecimal leftNum;


    /**
     * 二级代码
     */
    @Column(name="SECONDCODE")
    private String secondcode;

    /**
     * 等级
     */
    @Column(name="GRADE")
    private String grade;

    /**
     * 主计量
     */
    @Column(name="CFREE3")
    private String cfree3;

    /**
     * 制单人
     */
    @Column(name="CMAKER")
    private String cmaker;

    /**
     * 审核人
     */
    @Column(name="CHANDLER")
    private String chandler;

    /**
     * 物流属性
     */
    @Column(name="OTHER1")
    private String other1;

    /**
     * ERP发货账套
     */
    @Column(name="OTHER5")
    private String other5;

    @Column(name="CFREE4")
    private String cfree4;

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

    @Column(name="CVOUCHTYPE")
    private String cvouchtype;

    @Column(name="CVOUCHNAME")
    private String cvouchname;

    @Column(name="CBUSTYPE")
    private String cbustype;

    @Column(name="CSOURCE")
    private String csource;

    @Column(name="CWHCODE")
    private String cwhcode;

    @Column(name="CWHNAME")
    private String cwhname;

    @Column(name="OTHER2")
    private String other2;

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

    @Column(name="OTHER14")
    private String other14;

    @Column(name="OTHER15")
    private String other15;

    @Column(name="CREATE_SEQ")
    private String createSeq;

    @Column(name="CMEMO")
    private String cmemo;

    @Column(name="CRDCODE")
    private String crdcode;

    @Column(name="CRDNAME")
    private String crdname;

    @Column(name="OTHER16")
    private String other16;

}
