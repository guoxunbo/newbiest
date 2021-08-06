package com.newbiest.vanchip.dto.erp.delivery;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.vanchip.dto.erp.ErpRequestHeader;
import lombok.Data;

import java.util.List;

/**
 * 采购到货通知单（待测品）/退货通知
 * 发货通知单，不良品发货通知单/RMA退货通知单
 */
@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class DeliveryInfoRequestHeader extends ErpRequestHeader {

    //销售组织
    public static final String DEFAULT_SALES_ORG = "2000";

    //发货模式
    //01-Reel
    public final static String REEL_SHIP_MODE = "01";
    //02-产品型号
    public final static String PRODUCT_SHIP_MODE = "02";
    //03-版本号
    public final static String VERSION_SHIP_MODE = "03";

    //交货状态
    //1-读取；2-删除；3-发货过账
    public final static String READ_DELIVERY_STATUS = "1";
    public final static String DEL_DELIVERY_STATUS = "2";
    public final static String POSTING_DELIVERY_STATUS = "3";

    //交货单类型
    //ZTLF-精测正向交货单 发货
    public static final String DELIVERY_TYPE_SHIP = "ZTLF";

    //ZRL-精测客供料出库 退货。
    public static final String DELIVERY_TYPE_RETURN = "ZRL";

    //ZTL2-精测不良品交货、
    public static final String DELIVERY_TYPE_REJ_SHIP = "ZTL2";
    //ZTLR-精测RMA-自身原因、
    public static final String DELIVERY_TYPE_RMA_INCOMING = "ZTLR";
    //ZTR2-精测RMA-非自身原因、
    public static final String DELIVERY_TYPE_RMA_INCOMING2 = "ZTR2";
    //ZEL-精测客供料入库交货、
    public static final String DELIVERY_TYPE_INCOMING = "ZEL";


    /**
     * 销售组织
     */
    private String sales_org = DEFAULT_SALES_ORG;

    private String begin_date;

    private String end_date;

    /**
     * 发货模式
     */
    private String ship_mode;

    /**
     * 交货状态
     */
    private List<DeliveryStatus> statu;

    /**
     * 交货类型
     */
    private List<DeliveryType> type;

    /**
     * 交货单
     */
    private List<Delivery> delivery;

    private String field1;
    private String field2;
    private String field3;
    private String field4;
}
