package com.newbiest.vanchip.dto.erp.delivery;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class DeliveryInfoResponseItem implements Serializable {

    /**
     * 交货项目号
     */
    private String line_no;

    /**
     * 物料编号
     */
    private String material;

    /**
     * 客户物料
     */
    private String cust_material;

    /**
     * 批次编号
     */
    private String batch;

    /**
     * 实际交货量
     */
    private BigDecimal quantity;

    /**
     * 销售单位
     */
    private String units;

    /**
     * po号- 封装来料订单号 对应WMS PO_NO reserved54
     */
    private String po_number;

    /**
     * 备注
     */
    private String remark;


    private String reel;
    private String dc;

    /**
     * 印字信息
     */
    private String marking;

    /**
     * 等级
     */
    private String bin_type;

    /**
     * 版本
     */
    private String version;

    /**
     * 客户订单号 对应wms 唯捷订单号 reserved6
     */
    private String customer_po;

    /**
     * 销售订单号
     */
    private String so;

    /**
     * 销售订单项目号
     */
    private String so_item;

    private String box_no;
    private String control_lot;
    private String customer_lot_no;

    /**
     * mrb
     */
    private String mrb_code;

    /**
     * 测试批次号
     */
    private String test_batch;

    /**
     * 大类描述
     */
    private String type_desc;

    /**
     * 供应商批次
     */
    private String vendor_batch;

    /**
     * PID
     */
    private String pid;

    /**
     * 保税标识
     */
    private String bonded;

    /**
     * 保税手册
     */
    private String manual;
    private String gw2;
    private String nw2;

    /**
     * Carton Size
     */
    private String carton2;

    /**
     * 发货单位
     */
    private String post_unit;

    /**
     * 物料信息
     */
    private String d_info;

    /**
     * 运单号
     */
    private String d_number;

    /**
     * Ctn No
     */
    private String ctnno;

    /**
     * 生产订单，委外订单
     */
    private String subno;

    /**
     * 物料描述
     */
    private String material_desc;

    private String field1;
    private String field2;
    private String field3;
    private String field4;

    /**
     * 待测品的数据在这补充
     * @param responseItem
     * @param materialLot
     * @return
     */
    public MaterialLot copyDeliveryInfoResponseItemToMaterialLot(DeliveryInfoResponseItem responseItem, MaterialLot materialLot){
        materialLot.setMaterialLotId(responseItem.getBox_no());
        materialLot.setMaterialName(responseItem.getMaterial());
        materialLot.setItemId(responseItem.getLine_no());
        materialLot.setIncomingQty(responseItem.getQuantity());
        materialLot.setCurrentQty(responseItem.getQuantity());
        materialLot.setReserved50(responseItem.getUnits());
        materialLot.setReserved54(responseItem.getPo_number());
        materialLot.setReserved6(responseItem.getCustomer_po());
        materialLot.setReserved14(responseItem.getRemark());
        materialLot.setReserved9(responseItem.getDc());
        materialLot.setLetteringInfo(responseItem.getMarking());
        materialLot.setGrade(responseItem.getBin_type());
        materialLot.setReserved3(responseItem.getVersion());
        materialLot.setReserved7(responseItem.getSo());
        materialLot.setReserved15(responseItem.getSo_item());
        materialLot.setReserved4(responseItem.getControl_lot());
        materialLot.setReserved5(responseItem.getCustomer_lot_no());
        materialLot.setReserved16(responseItem.getMrb_code());
        materialLot.setReserved12(responseItem.getNw2());
        materialLot.setReserved13(responseItem.getGw2());
        materialLot.setReserved17(responseItem.getPost_unit());
        materialLot.setIncomingLogInfo(responseItem.getD_info());
        materialLot.setReserved24(responseItem.getD_number());
        materialLot.setReserved2(responseItem.getCust_material());
        materialLot.setReserved18(responseItem.getBonded());
        materialLot.setReserved19(responseItem.getManual());
        materialLot.setReserved10(responseItem.getCarton2());
        materialLot.setReserved11(responseItem.getCtnno());
        materialLot.setReserved8(responseItem.getSubno());
        materialLot.setIncomingComment(responseItem.getRemark());

        //reserved1 lot no
        //物料信息不在此赋值
        return materialLot;
    }

    public DocumentLine copyDeliveryInfoResponseItemToDocumentLine(DeliveryInfoResponseItem responseItem, DocumentLine documentLine){
        documentLine.setReserved10(responseItem.getBonded());
        documentLine.setReserved9(responseItem.getManual());
        documentLine.setReserved22(responseItem.getCust_material());
        documentLine.setReserved8(responseItem.getD_number());
        documentLine.setReserved1(responseItem.getPid());
        documentLine.setReserved3(responseItem.getVersion());
        documentLine.setReserved4(responseItem.getBin_type());
        documentLine.setReserved5(responseItem.getMrb_code());
        return documentLine;
    }
}
