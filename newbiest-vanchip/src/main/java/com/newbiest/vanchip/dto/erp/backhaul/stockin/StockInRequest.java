package com.newbiest.vanchip.dto.erp.backhaul.stockin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.vanchip.dto.erp.ErpRequest;
import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class StockInRequest extends ErpRequest {

    private StockInRequestHeader HEADER;

}
