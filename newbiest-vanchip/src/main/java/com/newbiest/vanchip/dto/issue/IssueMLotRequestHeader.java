package com.newbiest.vanchip.dto.issue;

import com.newbiest.base.threadlocal.ThreadLocalContext;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
public class IssueMLotRequestHeader implements Serializable {

    public static final String DEFAULT_ORG_NAME = "ZhiXing";

    public  static final String ISSUE_MATERIAL_MESSAGE_NAME = "IssueMaterialRequestMes" ;
    public  static final String ISSUE_MLOT_MESSAGE_NAME = "IssueMLotRequestMes" ;

    private String messageName;

    private String transactionId;

    private String orgRrn;

    private String orgName;

    private String username;

    private String language;

    public static IssueMLotRequestHeader buildDefaultRequestHeader(String messageName) {
        return new IssueMLotRequestHeader(messageName, UUID.randomUUID().toString(), "1", DEFAULT_ORG_NAME,
                "WMS", ThreadLocalContext.getLanguage());
    }


}
