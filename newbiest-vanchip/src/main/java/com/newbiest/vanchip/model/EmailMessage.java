package com.newbiest.vanchip.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 邮件信息
 */
@Entity
@Table(name="VC_EMAIL_MESSAGE")
@Data
public class EmailMessage extends NBBase {

    public static final String EMAIL_TYPE_RELEASE = "Release";
    public static final String EMAIL_TYPE_HOLD = "Hold";

    /**
     * 发送者
     */
    @Column(name = "FROM_USER")
    private String fromUser;

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

    /**
     *发送日期
     */
    @Column(name = "SEND_DATE")
    private Date sendDate;

    public void setEmailManager(EmailManager emailManager){
        this.setToUser(emailManager.getToUser());
        this.setSubject(emailManager.getSubject());
        this.setEmailType(emailManager.getEmailType());
    }
}