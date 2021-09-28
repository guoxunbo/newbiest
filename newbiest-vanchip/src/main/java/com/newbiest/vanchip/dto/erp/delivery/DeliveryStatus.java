package com.newbiest.vanchip.dto.erp.delivery;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class DeliveryStatus implements Serializable {

    //1-读取;
    public final static String READ_DELIVERY_STATUS = "1";
    //2-删除;
    public final static String DEL_DELIVERY_STATUS = "2";
    //3-发货过账;
    public final static String POSTING_DELIVERY_STATUS = "3";

    /**
     * 交货状态
     */
    private String statu;

}
