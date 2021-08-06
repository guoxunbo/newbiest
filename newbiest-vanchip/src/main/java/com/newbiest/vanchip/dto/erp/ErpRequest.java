package com.newbiest.vanchip.dto.erp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class ErpRequest implements Serializable {

    private String GUID;

    private ErpRequestHeader Header;

    public ErpRequest(){
        this.GUID = "WMS" + UUID.randomUUID().toString();
    }
}
