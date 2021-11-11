package com.newbiest.mms.model;

import com.newbiest.base.model.NBHis;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 接口历史
 */
@Table(name="COM_IF_HISTORY")
@Entity
@Data
public class InterfaceHistory extends NBHis {

    public static final String SYSTEM_NAME_ERP_SAP = "ERP_SAP";
    public static final String SYSTEM_NAME_VIVO_VLM = "VIVO_VLM";

    public static final Integer ACTION_CODE_MAX_LENGTH = 256;
    public static final String RESULT_SUCCESS = "Success";
    public static final String RESULT_FAIL = "Fail";

    public static final String TRANS_TYPE_NORMAL = "Normal";
    public static final String TRANS_TYPE_RETRY = "Retry";

    public static final String REQUEST_TYPE_HTTP = "Http";

    @Column(name="RESULT")
    private String result = RESULT_SUCCESS;

    @Column(name="REQUEST_TYPE")
    private String requestType = REQUEST_TYPE_HTTP;

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
}
