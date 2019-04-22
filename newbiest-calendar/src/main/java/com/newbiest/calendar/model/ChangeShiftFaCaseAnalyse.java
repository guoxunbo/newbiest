package com.newbiest.calendar.model;

import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.StringUtils;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 交接班->工厂案件分析
 * Created by guoxunbo on 2019/4/19.
 */
@Entity
@Table(name="DMS_CHANGE_SHIFT_FA_CASE_ANALYSE")
@Data
public class ChangeShiftFaCaseAnalyse extends NBUpdatable {

    @Column(name="NAME")
    private String name;

    @Column(name="CHANGE_SHIFT_RRN")
    private Long changeShiftRrn;

    @Column(name="CASE_ID")
    private String caseId;

    @Column(name="PRODUCT_ID")
    private String productId;

    /**
     * 分析目的
     */
    @Column(name="ANALYSE_PURPOSE")
    private String analysePurpose;

    /**
     * 分析进度
     */
    @Column(name="ANALYSE_PROGRESS")
    private Integer analyseProgress;

    @Column(name="OWNER")
    private String owner;

    @Column(name="COMMENT")
    private String comment;

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
