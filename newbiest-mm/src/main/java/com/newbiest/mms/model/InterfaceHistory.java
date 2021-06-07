package com.newbiest.mms.model;

import com.newbiest.base.model.NBHis;
import com.newbiest.security.model.NBOrg;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 接口历史
 *  此处暂时为格科客制化 后面需要整合到interface-monitor模块
 * @author guoxunbo
 * @date 2021/5/13 2:39 下午
 */
@Table(name="COM_IF_HISTORY")
@Entity
@Data
public class InterfaceHistory extends NBHis {

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

    protected void prePersist() {
        if (this.created == null) {
            created = new Date();
        }
        updated = new Date();
        this.orgRrn = NBOrg.GLOBAL_ORG_RRN;
    }

}
