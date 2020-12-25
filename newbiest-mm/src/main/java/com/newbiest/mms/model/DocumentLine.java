package com.newbiest.mms.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 单据对应的详细信息
 * Created by guoxunbo on 2019-08-29 17:51
 */
@Data
@Table(name = "MMS_DOCUMENT_LINE")
@Entity
public class DocumentLine extends NBUpdatable {

    @Column(name="DOC_RRN")
    private String docRrn;

    @Column(name="DOC_ID")
    private String docId;

    @Column(name="MATERIAL_RRN")
    private String materialRrn;

    @Column(name="MATERIAL_NAME")
    private String materialName;

    @Column(name="QTY")
    private BigDecimal qty;

    @Column(name="HANDLED_QTY")
    private BigDecimal handledQty = BigDecimal.ZERO;

    @Column(name="UN_HANDLE_QTY")
    private BigDecimal unHandledQty = BigDecimal.ZERO;

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
