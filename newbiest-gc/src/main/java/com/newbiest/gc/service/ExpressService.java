package com.newbiest.gc.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.model.DeliveryOrder;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;

import java.util.List;
import java.util.Map;

/**
 * 和跨域速递的接口。
 * @author guoxunbo
 * @date 2020-07-08 13:49
 */
public interface ExpressService {

    List<MaterialLot> planOrder(List<MaterialLot> materialLots, int serviceMode, int payMode) throws ClientException;
    void cancelOrderByMaterialLots(List<MaterialLot> materialLots) throws ClientException;

    List<DeliveryOrder> recordExpressNumber(List<DeliveryOrder> deliveryOrders) throws ClientException;

    List<MaterialLot> recordExpressNumber(List<MaterialLot> materialLots, String expressNumber, String planOrderType) throws ClientException;

    List<Map<String, String>> getPrintLabelParameterList(List<MaterialLot> materialLotList, String expressNumber) throws ClientException;
}
