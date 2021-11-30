package com.newbiest.gc.model;

import com.newbiest.base.model.NBHis;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by wangxinqi
 */
@Entity
@Table(name="GC_WLATOFT_TESTBIT_HIS")
@Data
public class GcWlatoftTesebitHis  extends NBHis {
    /**
     * waferId
     */
    @Column(name = "WAFER_ID")
    private String waferId;

    /**
     * 测试码
     */
    @Column(name="WLA_TEST_BIT")
    private String wlaTestBit;
}
