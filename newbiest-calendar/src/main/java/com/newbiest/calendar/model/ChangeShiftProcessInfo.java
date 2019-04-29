package com.newbiest.calendar.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * ProcessInfo
 * Created by guoxunbo on 2019/4/28.
 */
@Entity
@Table(name="DMS_CHANGE_SHIFT_PRO_INFO")
@Data
public class ChangeShiftProcessInfo extends NBUpdatable{


    @Column(name="CHANGE_SHIFT_RRN")
    private Long changeShiftRrn;

    @Column(name="LOT_ID")
    private String lotId;

    @Column(name="STEP_ID")
    private String stepId;

    @Column(name="EQUIPMENT_ID")
    private String equipmentId;

    @Column(name="EXCEPTION_DESC")
    private String exceptionDesc;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    @Column(name="HANDLE_TIME")
    private Date handleTime;

    @Column(name="OWNER")
    private String owner;

    @Column(name="REMARK")
    private String remark;

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
