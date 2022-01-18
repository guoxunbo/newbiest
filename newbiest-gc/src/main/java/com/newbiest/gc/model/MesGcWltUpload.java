package com.newbiest.gc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zhoutao
 * @title: GC_WLT_UPLOAD
 * @description：
 * @date 2022/1/17
 */
@Data
@Table(name = "MES_GC_WLT_UPLOAD")
@Entity
public class MesGcWltUpload implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String WLT_UN_TEST = "WLT未测";

    public static final String FLAG_NONE = "None";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="OBJECT_RRN")
    private Long objectRrn;

    @Column(name="WAFER_ID")
    private String waferId;

    @Column(name="DEVICE")
    private String device;

    @Column(name="BIN1")
    private Long bin1;

    @Column(name="BIN2")
    private Long bin2;

    @Column(name="BIN3")
    private Long bin3;

    @Column(name="BIN4")
    private Long bin4;

    @Column(name="STATUS")
    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = NBUpdatable.GMT_PE, pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    @Column(name="ACTION_TIME")
    private Date actionTime;

    @Column(name="FLAG")
    private String flag;

    @Column(name="PROGRAM_BIT")
    private String programBit;

    @Column(name="BACKCODE_CHECK")
    private String backcodeCheck;
}
