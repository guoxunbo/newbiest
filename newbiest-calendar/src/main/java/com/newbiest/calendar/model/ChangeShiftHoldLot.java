package com.newbiest.calendar.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by guoxunbo on 2019/4/28.
 */
@Entity
@Table(name="DMS_CHANGE_SHIFT_HOLD_LOT")
@Data
public class ChangeShiftHoldLot extends NBUpdatable{


    @Column(name="CHANGE_SHIFT_RRN")
    private Long changeShiftRrn;

    @Column(name="LOT_ID")
    private String lotId;

    @Column(name="HOLD_STEP")
    private String holdStep;

    @Column(name="HOLD_REASON")
    private String holdReason;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    @Column(name="HOLD_TIME")
    private Date holdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    @Column(name="RELEASE_TIME")
    private Date releaseTime;

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
