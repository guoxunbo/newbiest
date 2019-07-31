package com.newbiest.kms.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 问题处理详情
 * Created by guoxunbo on 2019-07-25 15:53
 */
@Data
@Entity
@Table(name="KMS_QUESTION_LINE")
public class QuestionLine extends NBUpdatable {

    @Column(name="QUESTION_RRN")
    private Long questionRrn;

    /**
     * 怀疑内容
     */
    @Column(name="SUSPECT_CONTENT")
    private String suspectContent;

    /**
     * 处理内容
     */
    @Column(name="PROCESS_CONTENT")
    private String processContent;

    /**
     * 后续内容
     */
    @Column(name="FOLLOW_UP_CONTENT")
    private String followUpContent;

    /**
     * 附件名称
     */
    @Column(name="FILE_NAME")
    private String fileName;

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
