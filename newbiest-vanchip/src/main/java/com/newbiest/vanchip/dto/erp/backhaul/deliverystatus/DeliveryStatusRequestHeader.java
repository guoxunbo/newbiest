package com.newbiest.vanchip.dto.erp.backhaul.deliverystatus;

import com.newbiest.vanchip.dto.erp.ErpRequestHeader;
import lombok.Data;

/**
 * 交货单状态
 */
@Data
public class DeliveryStatusRequestHeader extends ErpRequestHeader {


    //1-读取；2-删除；3-发货过账
    public final static String DELIVERY_STATUS1 = "1";
    public final static String DELIVERY_STATUS2 = "2";
    public final static String DELIVERY_STATUS3 = "3";

    //01-Reel
    public final static String REEL_DELIVERY_MODE = "01";

    //02-产品型号
    public final static String PRODUCT_DELIVERY_MODE = "02";

    //03-版本号
    public final static String VERSION_DELIVERY_MODE = "03";

    /**
     * 交货单
     */
    private String delivery;

    /**
     * 状态
     */
    private String status;

    /**
     * 发货类型
     */
    private String mode;

    /**
     * 运单号
     */
    private String shipping_no;

}
