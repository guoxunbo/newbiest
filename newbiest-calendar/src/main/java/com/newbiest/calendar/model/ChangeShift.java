package com.newbiest.calendar.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 交接班定义
 * Created by guoxunbo on 2019/4/18.
 */
@Entity
@Table(name="DMS_CHANGE_SHIFT")
@Data
public class ChangeShift extends NBUpdatable {

    public static final String STATUS_OPEN = "Open";
    public static final String STATUS_CLOSE = "Close";
    public static final String STATUS_CONFIRM = "Confirm";

    public static final String GENERATOR_NAME_RULE = "CreateChangeShift";

    public static final String CATEGORY_TEC_NPI = "Npi";
    public static final String CATEGORY_TEC_PES = "Pes";
    public static final String CATEGORY_TEC_FA = "Fas";
    public static final String CATEGORY_TEC_EVA = "Eva";


    @Column(name="NAME")
    private String name;

    /**
     * 时间
     */
    @Column(name="SHIFT_TIME")
    @Temporal(TemporalType.DATE)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATE_PATTERN)
    private Date shiftTime;

    /**
     * 班别
     */
    @Column(name="SHIFT")
    private String shift;

    /**
     * 类别
     */
    @Column(name="CATEGORY")
    private String category;

    @Column(name="DEPARTMENT")
    private String department;

    @Column(name="STATUS")
    private String status;

    /**
     * 应出勤人数
     */
    @Column(name="ATTENDANCE")
    private Integer attendance;

    /**
     * 实际出勤人数
     */
    @Column(name="ACTUAL_ATTENDANCE")
    private Integer actualAttendance;

    /**
     * 请假人数
     */
    @Column(name="ASK_FOR_LEAVE")
    private Integer askForLeave;

    /**
     * 迟到人数
     */
    @Column(name="LATE_ARRIVAL")
    private Integer lateArrival;

    /**
     * 早退人数
     */
    @Column(name="EARLY_LEAVE")
    private Integer earlyLeave;

    /**
     * 旷工人数
     */
    @Column(name="ABSENTEEISM")
    private Integer absenteeism;

    /**
     * 脱岗人数
     */
    @Column(name="LEAVE_POST")
    private Integer leavePost;

    /**
     * 接班人
     */
    @Column(name="SUCCESSOR")
    private String successor;

    /**
     * 确认时间
     */
    @Column(name="CONFIRM_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date confirmTime;

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
