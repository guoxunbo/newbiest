package com.newbiest.gc.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * created by Youqing Huang on 2021-12-28
 */
@Data
@Entity
@Table(name = "COM_THROW_WAFER_TAB")
public class ComThrowWaferTab implements Serializable {

    /**
     * wafer号
     */
    @Id
    @Column(name = "WAFERID")
    private String waferId;

    /**
     * 型号
     */
    @Column(name = "PDTID")
    private String pdtId;

    /**
     * 二级代码
     */
    @Column(name = "SECOND_CODE")
    private String secondCode;

    /**
     * 物流属性
     */
    @Column(name = "PROPERTY")
    private String property;

    /**
     * 发货时间
     */
    @Column(name = "TIMESTR")
    private String timeStr;

    /**
     * 发货单号
     */
    @Column(name = "BILLNUM")
    private String billNum;

}
