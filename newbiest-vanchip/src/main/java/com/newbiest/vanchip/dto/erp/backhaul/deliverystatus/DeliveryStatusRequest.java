package com.newbiest.vanchip.dto.erp.backhaul.deliverystatus;

import com.newbiest.vanchip.dto.erp.ErpRequest;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class DeliveryStatusRequest extends ErpRequest {

    private String guid;

    private List<DeliveryStatusRequestHeader> data;

    private List<DeliveryStatusRequestItem> item;

    public DeliveryStatusRequest(){
        this.guid = "WMS" + UUID.randomUUID().toString();
    }
}
