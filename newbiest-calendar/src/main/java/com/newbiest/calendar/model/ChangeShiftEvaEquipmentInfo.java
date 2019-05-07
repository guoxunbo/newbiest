package com.newbiest.calendar.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 实验信息
 * Created by guoxunbo on 2019/4/28.
 */
@Entity
@Table(name="DMS_CHANGE_SHIFT_EVA_EQP_INFO")
@Data
public class ChangeShiftEvaEquipmentInfo extends NBUpdatable{


    @Column(name="CHANGE_SHIFT_RRN")
    private Long changeShiftRrn;

    @Column(name="EQUIPMENT_ID")
    private String equipmentId;

    @Column(name="CHAMBER")
    private String chamber;

    @Column(name="VACUUM")
    private Integer vacuum;

    @Column(name="MASK")
    private String mask;

    @Column(name="MASK_OFFSET")
    private String maskOffset;

    @Column(name="SOURCE")
    private String source;

    @Column(name="MATERIAL")
    private String material;

    @Column(name="NOZZLE_TEMP")
    private Integer nozzleTemp;

    @Column(name="TOP_TEMP")
    private String topTemp;

    @Column(name="RATE")
    private String rate;

    @Column(name="TOOLING")
    private String tooling;

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
