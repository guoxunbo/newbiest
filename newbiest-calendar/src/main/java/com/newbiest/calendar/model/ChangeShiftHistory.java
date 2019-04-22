package com.newbiest.calendar.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBHis;
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
@Table(name="DMS_CHANGE_SHIFT_HIS")
@Data
public class ChangeShiftHistory extends NBHis {

    public static final String TRANS_TYPE_CLOSE = "Close";
    public static final String TRANS_TYPE_OPEN = "Open";

    @Column(name="NAME")
    private String name;

    @Column(name="SHIFT_TIME")
    @Temporal(TemporalType.DATE)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATE_PATTERN)
    private Date shiftTime;

    @Column(name="SHIFT")
    private String shift;

    @Column(name="CATEGORY")
    private String category;

    @Column(name="STATUS")
    private String status;

    @Column(name="ATTENDANCE")
    private Integer attendance;

    @Column(name="ACTUAL_ATTENDANCE")
    private Integer actualAttendance;

    @Column(name="ASK_FOR_LEAVE")
    private Integer askForLeave;

    @Column(name="LATE_ARRIVAL")
    private Integer lateArrival;

    @Column(name="EARLY_LEAVE")
    private Integer earlyLeave;

    @Column(name="ABSENTEEISM")
    private Integer absenteeism;

    @Column(name="LEAVE_POST")
    private Integer leavePost;

    @Column(name="SUCCESSOR")
    private String successor;

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
