package com.newbiest.vanchip.dto.erp.delivery;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class Delivery implements Serializable {

    /**
     * 交货单
     */
    private String delivery;

    public Delivery(String delivery){
        this.delivery = delivery;
    }
}
