package com.newbiest.vanchip.dto.erp.backhaul.deliverystatus;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import lombok.Data;

import java.io.Serializable;

/**
 * 交货单状态
 */
@Data
public class DeliveryStatusRequestItem implements Serializable {

    //交货单
    private String delivery;

    //行项目
    private String items;

    //物料
    private String material;

    //PO号
    private String po_number;

    //Remark
    private String remark;

    private String version;

    private String control_lot;

    private String customer_lot_no;

    private String marking;

    private String dc;

    private String mrb_code;

    //等级
    private String bin_type;

    //客户订单号码
    private String customer_po;

    //销售订单号
    private String so;

    //销售订单项目号
    private String so_item;

    //大类描述
    private String type_desc;

    //供应商批次
    private String vender_batch;

    //box_no
    private String box_no;

    //批次
    private String batch;

    //内盒号(Reel/Tray)
    private String reel;

    //测试批次
    private String test_batch;


    public DeliveryStatusRequestItem copyMaterialLotToDeliveryStatusRequestItem(DeliveryStatusRequestItem requestItem, MaterialLot materialLot){
        requestItem.setItems(materialLot.getItemId());
        requestItem.setMaterial(materialLot.getMaterialName());
        requestItem.setPo_number(materialLot.getReserved6());
        requestItem.setRemark(materialLot.getIncomingComment());
        requestItem.setVersion(materialLot.getReserved3());
        requestItem.setControl_lot(materialLot.getReserved4());
        requestItem.setCustomer_lot_no(materialLot.getReserved5());
        requestItem.setMarking(materialLot.getLetteringInfo());
        requestItem.setDc(materialLot.getReserved9());
        requestItem.setMrb_code(materialLot.getReserved16());
        requestItem.setBin_type(materialLot.getGrade());
        requestItem.setCustomer_po(materialLot.getReserved54());
        requestItem.setSo(materialLot.getReserved7());

        requestItem.setBatch(materialLot.getMaterialLotId());
        requestItem.setVender_batch(materialLot.getMaterialLotId());
        //大类描述
        //销售订单项目号
        return requestItem;
    }

    public DeliveryStatusRequestItem copyMaterialLotUnitToDeliveryStatusRequestItem(DeliveryStatusRequestItem requestItem, MaterialLotUnit materialLotUnit){
        requestItem.setMaterial(materialLotUnit.getMaterialName());
        requestItem.setPo_number(materialLotUnit.getReserved2());
        requestItem.setControl_lot(materialLotUnit.getReserved4());
        requestItem.setDc(materialLotUnit.getReserved2());
        requestItem.setMrb_code(materialLotUnit.getReserved5());
        requestItem.setBin_type(materialLotUnit.getGrade());
        requestItem.setCustomer_po(materialLotUnit.getReserved1());
        requestItem.setTest_batch(materialLotUnit.getUnitId());
        return requestItem;
    }
}
