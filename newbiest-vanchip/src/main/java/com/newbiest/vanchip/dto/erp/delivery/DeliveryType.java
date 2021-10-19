package com.newbiest.vanchip.dto.erp.delivery;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class DeliveryType implements Serializable {

    //交货单类型

    /**
     * ZTLF-精测正向交货单 发货
     */
    public static final String DELIVERY_TYPE_SHIP = "ZTLF";

    //ZRL-精测客供料出库 退货。
    public static final String DELIVERY_TYPE_RETURN = "ZRL";

    //ZTL2-精测不良品交货、
    public static final String DELIVERY_TYPE_REJ_SHIP = "ZTL2";

    /**
     * ZTLR-精测RMA-自身原因、
     */
    public static final String DELIVERY_TYPE_RMA_INCOMING = "ZTLR";

    //ZTR2-精测RMA-非自身原因、
    public static final String DELIVERY_TYPE_RMA_INCOMING2 = "ZTR2";

    //ZEL-精测客供料入库交货、
    public static final String DELIVERY_TYPE_INCOMING = "ZEL";

    /**
     * 交货类型
     */
    private String type;

}
