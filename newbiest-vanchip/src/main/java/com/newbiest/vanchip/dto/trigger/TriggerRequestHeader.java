package com.newbiest.vanchip.dto.trigger;

import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.StringUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class TriggerRequestHeader implements Serializable {

    public static final String DEFAULT_ORG_NAME = "1";
    public static final String DEFAULT_MESSAGE_NAME = "Trigger";
    public static final String DEFAULT_USER_NAME = StringUtils.SYSTEM_USER;

    private String messageName;

    private String transactionId;

    private String orgRrn;

    private String orgName;

    private String username;

    private String language;

    public TriggerRequestHeader() {
        this.messageName = DEFAULT_MESSAGE_NAME;
        this.orgRrn = DEFAULT_ORG_NAME;
        this.username = DEFAULT_USER_NAME;
        this.transactionId = UUID.randomUUID().toString();
        this.language = ThreadLocalContext.getLanguage();
    }

}
