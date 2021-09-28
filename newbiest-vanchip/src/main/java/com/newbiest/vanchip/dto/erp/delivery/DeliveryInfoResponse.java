package com.newbiest.vanchip.dto.erp.delivery;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.vanchip.dto.erp.ErpResponse;
import lombok.Data;

import java.util.List;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class DeliveryInfoResponse extends ErpResponse {

    private List<DeliveryInfoResponseData> data;

}
