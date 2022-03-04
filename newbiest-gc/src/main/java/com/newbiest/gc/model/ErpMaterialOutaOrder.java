package com.newbiest.gc.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 其他线发料单
 * Type为TV
 * Created by guozhangLuo on 2020-06-16 15:01
 */
@Data
@Entity
@Table(name="ERP_MATERIAL_OUTA_ORDER")
public class ErpMaterialOutaOrder implements Serializable {

    public static final String CATEGORY_WAFER_ISSUEA = "WaferIssue";

    public static final String SOURCE_TABLE_NAME = "ERP_MATERIAL_OUTA_ORDER";

    /**
     * 其他线晶圆发料单
     */
    public static final String TYPE_TV = "TV";

    /**
     * 原材料发料单
     */
    public static final String TYPE_MV = "MV";

    /**
     * FT重测发料单
     */
    public static final String TYPE_RT = "RT";

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
     * 未发数量
     */
    @Column(name="LEFT_NUM")
    private BigDecimal leftNum;

    /**
     * 创建人
     */
    @Column(name="USER_ID")
    private String userId;

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
     * 已发货数量
     */
    @Column(name="DELIVERED_NUM")
    private String deliveredNum;

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

    @Column(name="TYPE")
    private String type;

    @Column(name="CREATE_SEQ")
    private String createSeq;

    @Column(name="CFREE4")
    private String cfree4;

    @Column(name="CFREE3")
    private String cfree3;

    @Column(name="CUSNAME")
    private String cusname;

    @Column(name="G_CODE")
    private String gCode;

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

    @Column(name="G_NAME")
    private String gName;

    @Column(name="OTHER8")
    private String other8;

    @Column(name="OTHER18")
    private String other18;

    @Column(name="OTHER3")
    private String other3;

    @Column(name="OTHER9")
    private String other9;

    @Column(name="OTHER10")
    private String other10;

    @Column(name="OTHER7")
    private String other7;

    @Column(name="OTHER4")
    private String other4;

    @Column(name="OTHER5")
    private String other5;

    @Column(name="CUSCODE")
    private String cuscode;

    @Column(name="OTHER15")
    private String other15;

    /**
     * 备注
     */
    @Column(name="CMEMO")
    private String cmemo;

    @Column(name="CRDCODE")
    private String crdcode;

    @Column(name="CRDNAME")
    private String crdname;

    @Column(name="OTHER16")
    private String other16;

}
