package com.newbiest.gc.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.express.dto.OrderInfo;
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

    void batchCancelOrderByWayBillNumber(List<OrderInfo> orderInfoList) throws ClientException;
    OrderInfo getOrderInfoByWayBillNumber(String wayBillNumber) throws ClientException;
    List<MaterialLot> planOrder(List<MaterialLot> materialLots, int serviceMode, int payMode, String orderTime) throws ClientException;
    void cancelOrderByMaterialLots(List<MaterialLot> materialLots) throws ClientException;

    List<DocumentLine> recordExpressNumber(List<DocumentLine> documentLineList) throws ClientException;

    List<MaterialLot> recordExpressNumber(List<MaterialLot> materialLots, String expressNumber, String expressCompany, String planOrderType) throws ClientException;
}
