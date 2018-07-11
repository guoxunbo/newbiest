package com.newbiest.context.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by guoxunbo on 2018/7/6.
 */
@Entity
@Table(name="COM_CONTEXT_VALUE")
@Data
@NoArgsConstructor
public class ContextValue extends NBBase{

    @Column(name = "CONTEXT_RRN")
    private Long contextRrn;

    @Column(name="STATUS")
    private String status;

    @Column(name = "FIELD_VALUE1")
    private String fieldValue1;

    @Column(name = "FIELD_VALUE2")
    private String fieldValue2;

    @Column(name = "FIELD_VALUE3")
    private String fieldValue3;

    @Column(name = "FIELD_VALUE4")
    private String fieldValue4;

    @Column(name = "FIELD_VALUE5")
    private String fieldValue5;

    @Column(name = "FIELD_VALUE6")
    private String fieldValue6;

    @Column(name = "FIELD_VALUE7")
    private String fieldValue7;

    @Column(name = "FIELD_VALUE8")
    private String fieldValue8;

    @Column(name = "FIELD_VALUE9")
    private String fieldValue9;

    @Column(name = "FIELD_VALUE10")
    private String fieldValue10;

    @Column(name = "RESULT_VALUE1")
    private String resultValue1;

    @Column(name = "RESULT_VALUE2")
    private String resultValue2;

    @Column(name = "RESULT_VALUE3")
    private String resultValue3;

    @Column(name = "RESULT_VALUE4")
    private String resultValue4;

    @Column(name = "RESULT_VALUE5")
    private String resultValue5;

    @Column(name = "RESULT_VALUE6")
    private String resultValue6;

    @Column(name = "RESULT_VALUE7")
    private String resultValue7;

    @Column(name = "RESULT_VALUE8")
    private String resultValue8;

    @Column(name = "RESULT_VALUE9")
    private String resultValue9;

    @Column(name = "RESULT_VALUE10")
    private String resultValue10;

}
