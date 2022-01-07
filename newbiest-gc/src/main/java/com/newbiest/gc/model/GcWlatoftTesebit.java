package com.newbiest.gc.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by wangxinqi
 */
@Entity
@Table(name="GC_WLATOFT_TESTBIT")
@Data
public class GcWlatoftTesebit extends NBUpdatable{
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

    /**
     * 测试码
     */
    @Column(name="WLA_PROGRAM_BIT")
    private String wlaProgramBit;
}
