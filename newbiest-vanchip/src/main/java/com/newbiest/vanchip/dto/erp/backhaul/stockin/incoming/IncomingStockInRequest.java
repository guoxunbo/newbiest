package com.newbiest.vanchip.dto.erp.backhaul.stockin.incoming;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.vanchip.dto.erp.ErpRequest;
import lombok.Data;

/**
 * 客供料/待测品来料入库
 */
@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class IncomingStockInRequest extends ErpRequest {

    private IncomingStockInRequestHeader Header;

}
