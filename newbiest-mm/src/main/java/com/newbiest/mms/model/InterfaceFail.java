package com.newbiest.mms.model;

import com.newbiest.base.model.NBHis;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 接口失败记录,需重发
 */
@Table(name="COM_IF_FAIL")
@Entity
@Data
@NoArgsConstructor
public class InterfaceFail extends NBHis {

    @Column(name="RESULT")
    private String result;

    @Column(name="REQUEST_TYPE")
    private String requestType;

    @Column(name="DESTINATION")
    private String destination;

    @Column(name="REQUEST_TXT")
    private String requestTxt;

    @Column(name="RESPONSE_TXT")
    private String responseTxt;

    /**
     * 接口系统名称/系统类型
     */
    @Column(name="SYSTEM_NAME")
    private String systemName;

    public InterfaceFail(InterfaceHistory interfaceHistory) {
        this.result = interfaceHistory.getResult();
        this.requestType = interfaceHistory.getRequestType();
        this.destination = interfaceHistory.getDestination();
        this.requestTxt = interfaceHistory.getRequestTxt();
        this.responseTxt = interfaceHistory.getResponseTxt();
        this.transType = interfaceHistory.getTransType();
        this.actionCode = interfaceHistory.getActionCode();
        this.systemName = interfaceHistory.getSystemName();
    }
}
