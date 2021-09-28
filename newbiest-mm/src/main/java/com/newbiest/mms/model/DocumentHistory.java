package com.newbiest.mms.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name="MMS_DOCUMENT_HIS")
public class DocumentHistory extends NBUpdatable {

    public static final String TRANS_TYPE_APPROVE = "Approve";
    public static final String TRANS_TYPE_ISSUE = "Issue";
    public static final String TRANS_TYPE_RETURN = "Return";
    public static final String TRANS_TYPE_STOCK_OUT = "StockOut";
    public static final String TRANS_TYPE_CHECK = "Check";
    public static final String TRANS_TYPE_SCRAP= "Scrap";

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name = "OWNER")
    private String owner;

    @Column(name="STATUS")
    private String status;

    @Column(name="CATEGORY")
    private String category;

    /**
     * 单据总数量。
     *  由lines上统计。无法私自改数量
     */
    @Column(name="QTY")
    private BigDecimal qty;

    /**
     * 已经处理的数量
     *  由lines上统计。无法私自改数量
     */
    @Column(name="HANDLED_QTY")
    private BigDecimal handledQty = BigDecimal.ZERO;

    /**
     * 未处理的数量
     *  由lines上统计。无法私自改数量
     */
    @Column(name="UN_HANDLE_QTY")
    private BigDecimal unHandledQty = BigDecimal.ZERO;

    /**
     * 客户或者供应商名称
     */
    @Column(name="SUPPLIER_NAME")
    private String supplierName;

    /**
     * 责任日期
     */
    @Column(name="DUE_DATE")
    private Date dueDate;

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

}
