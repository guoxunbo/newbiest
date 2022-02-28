package com.newbiest.gc.model;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * GlaxyCore的ERP的单据。
 *              发货单 Type为SO
 *              晶圆接收单 TYPE为TV
 */
@Data
@Entity
@Table(name="ERP_SO")
public class ErpSo implements Serializable {

    public static final String SOURCE_TABLE_NAME = "ERP_SO";

    /**
     * 发货单
     */
    public static final String TYPE_SO = "SO";

    /**
     * 晶圆接收单
     */
    public static final String TYPE_TV = "TV";

    /**
     * 晶圆接收单
     */
    public static final String TYPE_COG = "COG";

    /**
     * 原材料其他出库单
     */
    public static final String TYPE_MO = "MO";

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
     * 入库备注
     */
    @Column(name="OTHER3")
    private String other3;

    /**
     * 出库备注
     */
    @Column(name="OTHER4")
    private String other4;

    /**
     * ERP发货账套
     */
    @Column(name="OTHER5")
    private String other5;

    /**
     * ODM
     */
    @Column(name="OTHER7")
    private String other7;

    /**
     * 客户简称
     */
    @Column(name="OTHER8")
    private String other8;

    /**
     * 创建人
     */
    @Column(name="USER_ID")
    private String userId;

    /**
     * 客户联系人
     */
    @Column(name="OTHER9")
    private String other9;

    /**
     * 客户联系电话
     */
    @Column(name="OTHER10")
    private String other10;

    /**
     * 客户联系电话
     */
    @Column(name="OTHER16")
    private String other16;

    /**
     * 发货地址
     */
    @Column(name="OTHER18")
    private String other18;

    /**
     * 快递单号
     */
    @Column(name="OTHER19")
    private String other19;

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

    @Column(name="TYPE")
    private String type;

    @Column(name="G_CODE")
    private String gCode;

    @Column(name="G_NAME")
    private String gName;

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

    @Column(name="CREATE_SEQ")
    private String createSeq;

    @Column(name="CFREE4")
    private String cfree4;

    @Column(name="CUSCODE")
    private String cuscode;

    @Column(name="OTHER15")
    private String other15;

    @Column(name="CRDCODE")
    private String crdcode;

    @Column(name="CRDNAME")
    private String crdname;

    /**
     * 备注
     */
    @Column(name="CMEMO")
    private String cmemo;

}
