package com.newbiest.calendar.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 交接班->设备状况
 * Created by guoxunbo on 2019/4/19.
 */
@Entity
@Table(name="DMS_CHANGE_SHIFT_EQ_STATUS")
@Data
public class ChangeShiftEqpStatus extends NBUpdatable {

    @Column(name="CHANGE_SHIFT_RRN")
    private Long changeShiftRrn;

    @Column(name="EQUIPMENT_ID")
    private String equipmentId;

    @Column(name="EXCEPTION_FLAG")
    private String exceptionFlag;

    @Column(name="TOOL_HOMING_FLAG")
    private String toolHomingFlag;

    @Column(name="WRITE_CHECK_TABLE_FLAG")
    private String writeCheckTableFlag;

    @Column(name="COMMENT")
    private String comment;

    /**
     * 接班人
     */
    @Column(name="SUCCESSOR")
    private String successor;

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
