package com.newbiest.mms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 单据
 * Created by guoxunbo on 2019-08-29 17:19
 */
@Data
@Entity
@Table(name="MMS_DOCUMENT")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CATEGORY", discriminatorType = DiscriminatorType.STRING, length = 32)
public class Document extends NBUpdatable {

    public static final String CATEGORY_DELIVERY = "Delivery";
    public static final String CATEGORY_RECEIVE = "Receive";

    public static final String STATUS_OPEN = "OPEN";


    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name = "OWNER")
    private String owner;

    @Column(name="STATUS")
    private String status;

    @Column(name="CATEGORY", updatable = false, insertable = false)
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

    @Column(name="ERP_CREATED", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATE_PATTERN)
    @Getter
    protected Date erpCreated;

    @Column(name="RESERVED_QTY")
    private BigDecimal reservedQty = BigDecimal.ZERO;

    @Column(name="UN_RESERVED_QTY")
    private BigDecimal unReservedQty = BigDecimal.ZERO;

    @OneToMany(fetch= FetchType.LAZY, cascade={CascadeType.ALL})
    @JoinColumn(name = "DOC_RRN", referencedColumnName = "OBJECT_RRN")
    private List<DocumentLine> documentLines;

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
