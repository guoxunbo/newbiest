package com.newbiest.vanchip.dto.mes;

import com.newbiest.base.threadlocal.ThreadLocalContext;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class MesRequestHeader implements Serializable {

    public static final String DEFAULT_USER_NAME = "WMS";

    private String messageName;

    private String transactionId;

    private String orgRrn;

    private String orgName;

    private String username;

    private String language;

    public MesRequestHeader(String messageName) {
        this.messageName = messageName;
        this.transactionId = UUID.randomUUID().toString();
        this.orgRrn = ThreadLocalContext.getOrgRrn();
        this.username = DEFAULT_USER_NAME;
        this.language = ThreadLocalContext.getLanguage();
    }

}
