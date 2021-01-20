package com.newbiest.vanchip.dto.returnlot;

import com.newbiest.base.threadlocal.ThreadLocalContext;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
public class ReturnMLotRequestHeader implements Serializable {

    public static final String DEFAULT_ORG_NAME = "ZhiXing";

    public  static final String RETURN_MLOT_MESSAGE_NAME = "ReturnMLotRequestMes" ;

    private String messageName;

    private String transactionId;

    private String orgRrn;

    private String orgName;

    private String username;

    private String language;

    public static ReturnMLotRequestHeader buildDefaultRequestHeader(String messageName) {
        return new ReturnMLotRequestHeader(messageName, UUID.randomUUID().toString(), "1", DEFAULT_ORG_NAME,
                "WMS", ThreadLocalContext.getLanguage());
    }


}
