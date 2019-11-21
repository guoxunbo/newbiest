package com.newbiest.ams.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 警报任务
 * Created by guoxunbo on 2019-11-18 17:31
 */
@Data
@Entity
@Table(name="AMS_JOB")
public class AlarmJob extends NBUpdatable {

    public static final String DEFAULT_TITLE_PATTERN = "jobId:objectId:alarmId";

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    /**
     * 警报大类，一般用来做系统之间的区分比如RTM,MES,SPC等
     */
    @Column(name = "CATEGORY")
    private String category;

    /**
     * 警报类型
     */
    @Column(name = "TYPE")
    private String type;

    /**
     * 标题
     */
    @Column(name = "TITLE_PATTERN")
    private String titlePattern = DEFAULT_TITLE_PATTERN;

    /**
     * 默认的严重级别
     */
    @Column(name = "SEVERITY_LEVEL")
    private String severityLevel;

    /**
     * 默认的优先级
     */
    @Column(name = "PRIORITY")
    private Long priority;

    /**
     * 默认的责任人
     */
    @Column(name = "OWNER")
    private String owner;

    /**
     * 对象过滤器。支持正则
     */
    @Column(name = "OBJECT_ID_REGEX")
    private String objectIdRegex;

    /**
     * alarmId过滤。支持正则
     */
    @Column(name = "ALARM_Name_REGEX")
    private String alarmNameRegex;


}
