package com.newbiest.kms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 问题记录历史
 * Created by guoxunbo on 2019-07-25 15:17
 */
@Data
@Entity
@Table(name="KMS_QUESTION_HIS")
public class QuestionHistory extends NBHis {

    public static final String TRANS_TYPE_CLOSE = "Close";

    /**
     * 名称
     */
    @Column(name="NAME")
    private String name;

    /**
     * 主题
     */
    @Column(name="THEME")
    private String theme;

    /**
     * 发生问题描述
     */
    @Column(name="DESCRIPTION")
    private String description;

    /**
     * 标记 可以允许多个，用分号隔开
     * 类似Issue, Enhance, FirstIssues
     */
    @Column(name="TAGS")
    private String tags;

    /**
     * 开始时间
     */
    @Column(name="START_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date startTime;

    /**
     * 结束时间
     */
    @Column(name="END_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date endTime;

    @Column(name="STATUS")
    private String status;

    /**
     * 责任部门
     */
    @Column(name="ASSIGN_TO")
    private String assignTo;

    /**
     * 现象描述
     */
    @Column(name="PHENOMENON_DESC")
    private String phenomenonDesc;

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
