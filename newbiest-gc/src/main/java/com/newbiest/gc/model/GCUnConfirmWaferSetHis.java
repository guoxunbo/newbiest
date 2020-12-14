package com.newbiest.gc.model;

import com.newbiest.base.model.NBHis;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by guozhangLuo
 */
@Entity
@Table(name="GC_UNCONFIRM_WAFER_SET_HIS")
@Data
public class GCUnConfirmWaferSetHis extends NBHis {

    /**
     * 序号
     */
    @Column(name="SERIAL_NUMBER")
    private String serialNumber;

    /**
     * LOT_ID
     */
    @Column(name="LOT_ID")
    private String lotId;

    /**
     * 晶圆号
     */
    @Column(name="WAFER_ID")
    private String waferId;

    /**
     * 测试站点
     */
    @Column(name="TEST_SITE")
    private String modelCategory;

    /**
     * 型号
     */
    @Column(name="MODEL_ID")
    private String modelId;

    /**
     * 异常分类
     */
    @Column(name="EXCEPTION_CLASSIFY")
    private String exceptionClassify;

    /**
     * 风险等级
     */
    @Column(name="RISK_GRADE")
    private String riskGrade;

}
