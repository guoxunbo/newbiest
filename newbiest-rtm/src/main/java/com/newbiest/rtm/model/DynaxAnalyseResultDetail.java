package com.newbiest.rtm.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by guoxunbo on 2019/5/27.
 */
@Data
@Entity
@Table(name="DYNAX_ANALYSE_RESULT_DETAIL")
public class DynaxAnalyseResultDetail extends AnalyseResultDetail {

    @Column(name="STEP_NAME")
    private String stepName;

    @Column(name="TEST")
    private String test;

    @Column(name="RESULT")
    private String result;

    @Column(name="TEST_NAME")
    private String testName;

    @Column(name="LOWER_LIMIT")
    private String lowerLimit;

    @Column(name="CURRENT_VALUE")
    private String currentValue;

    @Column(name="UPPER_LIMIT")
    private String upperLimit;

    @Column(name="UNITS")
    private String units;

}
