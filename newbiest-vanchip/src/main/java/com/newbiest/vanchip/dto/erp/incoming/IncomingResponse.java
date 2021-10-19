package com.newbiest.vanchip.dto.erp.incoming;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.vanchip.dto.erp.ErpResponse;
import lombok.Data;

import java.util.List;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class IncomingResponse extends ErpResponse {

    private List<IncomingResponseHeader> Header;

    private List<IncomingResponseItem> Item;

    private String Status;

}
