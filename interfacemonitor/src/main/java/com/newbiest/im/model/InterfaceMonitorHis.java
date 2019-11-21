package com.newbiest.im.model;

import com.newbiest.base.model.NBHis;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 接口调用的历史
 */
@Data
@Entity
@Table(name = "COM_IM_HIS")
public class InterfaceMonitorHis extends NBHis {

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="BEAN_NAME")
    private String beanName;

    @Column(name="TYPE")
    private String type;

    @Column(name="REQUEST_MESSAGE")
    private String requestMessage;

    @Column(name="RESPONSE_MESSAGE")
    private String responseMessage;

    @Column(name="RESULT_CODE")
    private String resultCode;

}
