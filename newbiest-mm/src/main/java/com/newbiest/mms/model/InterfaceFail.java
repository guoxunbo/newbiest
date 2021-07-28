package com.newbiest.mms.model;

import com.newbiest.base.model.NBHis;
import com.newbiest.security.model.NBOrg;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 接口失败的实例。需要进行重发
 *  此处暂时为格科客制化 后面需要整合到interface-monitor模块
 * @author guoxunbo
 * @date 2021/5/13 2:39 下午
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

    public InterfaceFail(InterfaceHistory interfaceHistory) {
        this.result = interfaceHistory.getResult();
        this.requestType = interfaceHistory.getRequestType();
        this.destination = interfaceHistory.getDestination();
        this.requestTxt = interfaceHistory.getRequestTxt();
        this.responseTxt = interfaceHistory.getResponseTxt();
        this.transType = interfaceHistory.getTransType();
        this.actionCode = interfaceHistory.getActionCode();
    }

    protected void prePersist() {
        if (this.created == null) {
            created = new Date();
        }
        updated = new Date();
        this.orgRrn = NBOrg.GLOBAL_ORG_RRN;
    }
}
