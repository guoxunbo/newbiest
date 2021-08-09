package com.newbiest.vanchip.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;

import java.util.List;

/**
 * 连接ERP的服务相关
 */
public interface ErpService {

    void asyncDeliveryInfo() throws ClientException;

    void asyncIncomingOrReturn() throws ClientException;

    void backhaulIncomingOrReturn(DocumentLine documentLine, List<MaterialLot> materialLots) throws ClientException;

    void backhaulDepartmentIssueOrReturn(List<MaterialLot> materialLots, String bwart, String kostl)throws ClientException;

    void backhaulCheck(String documentId, List<MaterialLot> materialLots, Boolean recheckFlag)throws ClientException;

    void backhaulStockTransfer(List<MaterialLot> materialLots, List<MaterialLotAction> materialLotActions)throws ClientException;

    void backhaulScrap(String documentId, String kostl, List<MaterialLot> materialLots)throws ClientException;

    void backhaulDeliveryStatus(String documentId, DocumentLine documentLine, List<MaterialLot> materialLots, String deliveryStatus)throws ClientException;

}
