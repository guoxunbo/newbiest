package com.newbiest.vanchip.dto.erp.delivery;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class DeliveryInfoResponseData implements Serializable {

    /**
     * 交货单
     */
    private String delivery;

    /**
     * 发货模式
     */
    private String ship_mode;

    /**
     * 发货类型
     */
    private String ship_type;

    /**
     * 发货类型
     */
    private String type;
    private String type_desc;

    /**
     * 交货状态
     */
    private String statu;

    /**
     * 销售组织
     */
    private String sales_org;

    /**
     * 装运点/收货点
     */
    private String ship_point;

    /**
     * 国际贸易条款
     */
    private String incoterms;

    /**
     * 国际贸易条款位置-发运港
     */
    private String inco_loc1;

    /**
     * 发货日期
     */
    private String plan_ship_date;

    /**
     * 创建日期
     */
    private String create_date;

    /**
     * 输入时间
     */
    private String create_time;

    /**
     * 客户编码
     */
    private String cust_id;

    /**
     * 客户简称
     */
    private String cust_short;

    /**
     * 名称
     */
    private String cust_name;

    /**
     * 售达方
     */
    private String seller_id;

    /**
     * 客户简称
     */
    private String seller_short;

    /**
     * 名称
     */
    private String seller_name;

    /**
     * 街道
     */
    private String seller_addr;


    private String seller_contact;
    private String seller_telno;

    /**
     * 送达方
     */
    private String shipto_id;

    /**
     * 名称
     */
    private String shipto_name;

    /**
     *街道
     */
    private String shipto_addr;

    /**
     * 联系人
     */
    private String shipto_contact;

    /**
     * 电话
     */
    private String shipto_telno;

    /**
     * 客户送货单号
     */
    private String note_no;
    /**
     * 最终客户采购订单单号
     */
    private String ecust_po;

    /**
     * 客户销售订单号
     */
    private String cust_so;

    /**
     * 承运商id
     */
    private String shipper_id;

    /**
     * 承运商名称
     */
    private String shipper_name;

    /**
     * 备注
     */
    private String notes;

    /**
     * 实际已交货量
     */
    private BigDecimal total;

    private String field1;
    private String field2;
    private String field3;
    private String field4;
    private List<DeliveryInfoResponseItem> items;
    /**
     * 发货单
     * @param responseData
     * @param documentLine
     * @return
     */
    public DocumentLine copyDeliveryInfoToDcoumentLine(DeliveryInfoResponseData responseData, DocumentLine documentLine){
        documentLine.setReserved2(responseData.getCust_short());
        documentLine.setReserved11(responseData.getCust_name());
        documentLine.setReserved12(responseData.getSeller_name());//售达方
        documentLine.setReserved13(responseData.getSeller_addr());//街道
        documentLine.setReserved14(responseData.getSeller_contact()+"&"+responseData.getSeller_telno());
        documentLine.setReserved15(responseData.getShipto_name());
        documentLine.setReserved16(responseData.getShipto_addr());
        documentLine.setReserved17(responseData.getShipto_contact());
        documentLine.setReserved18(responseData.getShipto_telno());
        documentLine.setReserved19(responseData.getCust_so());//销售订单号
        documentLine.setReserved20(responseData.getEcust_po());//客户订单号
        documentLine.setReserved21(responseData.getNote_no());//送货号
        documentLine.setReserved6(responseData.getNotes());
        documentLine.setReserved7(responseData.getShipper_name());
        documentLine.setQty(responseData.getTotal());
        documentLine.setReserved27(responseData.getShip_type());
        documentLine.setUnHandledQty(responseData.getTotal());
        documentLine.setReserved25(responseData.getIncoterms());//Delivery Term:
        documentLine.setReserved31(responseData.getInco_loc1());
//        documentLine.setReserved10("是否保税");
//        documentLine.setReserved9("关务手册号");
//        documentLine.setReserved8("物流信息");
//        documentLine.setReserved1("PID");
//        documentLine.setReserved22("客户产品");
//        documentLine.setReserved3("客户版本");
//        documentLine.setReserved4("等级");
//        documentLine.setReserved5("MRB");
        //发货日期20210728 需进行转换
        return documentLine;
    }

    /**
     * 主材到货/退货通知
     * @param responseData
     * @param materialLot
     * @return
     */
    public MaterialLot copyDeliveryInfoToMaterialLot(DeliveryInfoResponseData responseData, MaterialLot materialLot){
        materialLot.setReserved62(responseData.getNote_no());
        return materialLot;
    }
}
