package com.newbiest.ams.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 具体的alarm信息
 * Created by guoxunbo on 2019-11-19 11:37
 */
@Data
@Entity
@Table(name="AMS_DATA")
public class AlarmData extends NBBase {

    public static final String STATUS_OPEN = "Open";
    public static final String STATUS_ACK = "Ack";
    public static final String STATUS_CLOSE = "Close";

    @Column(name="JOB_RRN")
    private String jobRrn;

    @Column(name="JOB_ID")
    private String jobId;

    @Column(name="JOB_DESC")
    private String jobDesc;

    /**
     * alarm的名字
     */
    @Column(name="NAME")
    private String name;

    /**
     * title根据Job上的生成
     */
    @Column(name="TITLE")
    private String title;

    /**
     * 内容
     */
    @Column(name="TEXT")
    private String text;

    /**
     * 触发事件
     */
    @Column(name="TRIGGER_TIME")
    private Date triggerTime;

    /**
     * 触发对象的ID比如设备Id，LotId等
     */
    @Column(name="OBJECT_ID")
    private String objectId;

    /**
     * 触发对象的类型，比如LOT/EQP
     */
    @Column(name="OBJECT_TYPE")
    private String objectType;

    /**
     * 触发对象的责任人。
     */
    @Column(name="OBJECT_OWNER")
    private String objectOwner;

    /**
     * 责任人
     */
    @Column(name="OWNER")
    private String owner;

    /**
     * 状态
     */
    @Column(name="STATUS")
    private String status = STATUS_OPEN;

    /**
     * 备注
     */
    @Column(name="COMMENTS")
    private String comments;

    /**
     * 严重级别，
     */
    @Column(name="SEVERITY_LEVEL")
    private String severityLevel;

    /**
     * 优先级
     */
    @Column(name="PRIORITY")
    private Long priority;

    /**
     * 确认者
     */
    @Column(name="ACK_USER")
    private String ackUser;

    /**
     * 确认时间
     */
    @Column(name="ACK_TIME")
    private Date ackTime;

    /**
     * 关闭者
     */
    @Column(name="CLOSE_USER")
    private String closeUser;

    /**
     * 关闭时间
     */
    @Column(name="CLOSE_TIME")
    private Date closeTime;

}
