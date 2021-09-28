package com.newbiest.vanchip.dto.erp.delivery;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.vanchip.dto.erp.ErpRequest;
import lombok.Data;

import java.util.UUID;

/**
 * 采购到货通知单（待测品）/退货通知
 * 发货通知单，不良品发货通知单/RMA退货通知单
 */
@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class DeliveryInfoRequest extends ErpRequest {

    private String guid;

    private DeliveryInfoRequestHeader header;

    public DeliveryInfoRequest(){
        this.guid = "WMS" + UUID.randomUUID().toString();
    }
}
