package com.newbiest.kms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.validate.IDataAuthorityValidation;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 问题记录
 * Created by guoxunbo on 2019-07-25 15:17
 */
@Data
@Entity
@Table(name="KMS_QUESTION")
public class Question extends NBUpdatable implements IDataAuthorityValidation {

    public static final String CREATE_QUESTION_GENERATOR_NAME = "CreateQuestion";

    public static final String STATUS_DOING = "Doing";
    public static final String STATUS_WATCHING = "Watching";
    public static final String STATUS_CLOSE = "Close";

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
     * 指派给谁
     */
    @Column(name="ASSIGN_TO")
    private String assignTo;

    /**
     * 现象描述
     */
    @Column(name="PHENOMENON_DESC")
    private String phenomenonDesc;

    /**
     * 创建用户所属部门
     */
    @Column(name="CREATED_USER_DEPT")
    private String createdUserDept;

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

    /**
     * 上传者的userRrn
     */
    @Column(name="RESERVED7")
    private String reserved7;

    /**
     * 上传者的username
     */
    @Column(name="RESERVED8")
    private String reserved8;

    /**
     * 修改用户的真实姓名
     */
    @Column(name="RESERVED9")
    private String reserved9;

    /**
     * 创建用户的真实姓名
     */
    @Column(name="RESERVED10")
    private String reserved10;
}
