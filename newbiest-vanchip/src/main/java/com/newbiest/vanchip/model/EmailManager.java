package com.newbiest.vanchip.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 邮件管理
 */
@Entity
@Table(name="VC_EMAIL_MANAGER")
@Data
public class EmailManager extends NBBase {

    /**
     * 邮件名称
     */
    @Column(name = "EMAIL_NAME")
    private String emailName;

    /**
     *接收者
     */
    @Column(name = "TO_USER")
    private String toUser;

    /**
     *主题
     */
    @Column(name = "SUBJECT")
    private String subject;

    /**
     *内容
     */
    @Column(name = "CONTENT")
    private String content;

    /**
     *邮件类型
     */
    @Column(name = "EMAIL_TYPE")
    private String emailType;

}