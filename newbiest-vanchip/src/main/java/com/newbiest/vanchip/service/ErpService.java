package com.newbiest.vanchip.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.InterfaceFail;
import com.newbiest.mms.model.MaterialLot;

import java.util.List;

/**
 * 连接ERP的服务相关
 */
public interface ErpService {

    void asyncDeliveryInfo() throws ClientException;

    void asyncIncomingOrReturn() throws ClientException;

    void retry(List<InterfaceFail> interfaceFailList) throws ClientException;

    void backhaulIncomingStockIn(List<MaterialLot> materialLots) throws ClientException;
    void backhaulReturnMLot(String docId, List<MaterialLot> materialLots)throws ClientException;

    void backhaulDepartmentIssueOrReturn(List<MaterialLot> materialLots, String bwart, String kostl)throws ClientException;

    void backhaulCheck(DocumentLine documentLine, List<MaterialLot> materialLots, String warehouseName)throws ClientException;

    void backhaulStockTransfer(List<MaterialLot> materialLots, List<MaterialLotAction> materialLotActions)throws ClientException;

    void backhaulScrap(String documentId, List<MaterialLot> materialLots)throws ClientException;

    void backhaulDeliveryStatus(String documentId, DocumentLine documentLine, List<MaterialLot> materialLots, String deliveryStatus, String shippingNo)throws ClientException;

    void backhaulStockIn(List<MaterialLot> materialLotList) throws ClientException;

    void backhaulMainMaterialStockIn(List<MaterialLot> materialLots) throws ClientException;
    void backhaulReturnMainMaterial(String docId, List<MaterialLot> materialLots) throws ClientException;

    void splitMLot(MaterialLot materialLot) throws ClientException;
}
