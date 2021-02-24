package com.newbiest.mms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.Document;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 单据相关service
 * @author guoxunbo
 * @date 12/24/20 2:36 PM
 */
public interface DocumentService {

    void receiveIncomingLot(String documentId, List<MaterialLot> materialLots) throws ClientException;
    void approveDocument(Document document) throws ClientException;

    String generatorDocId(String generatorRule) throws ClientException;

    void createIssueLotOrder(String documentId, boolean approveFlag, List<String> materialLotIdList) throws ClientException;
    void createIssueMaterialOrder(String documentId, boolean approveFlag, Map<String, BigDecimal> rawMaterialQtyMap) throws ClientException;
    void issueMLotByDoc(String issueLotOrderId, List<String> materialLotIdList) throws ClientException;
    void issueMLotByDocLine(DocumentLine documentLine, List<String> materialLotIdList) throws ClientException;

    void createReturnOrder(String documentId, boolean approveFlag, List<MaterialLotAction> materialLotActions) throws ClientException;
    void returnMLotByDoc(String documentId, List<String> materialLotIdList) throws ClientException;

    List<MaterialLot> getReservedMLotByDocId(String documentId) throws ClientException;

    void createDeliveryOrder(String documentId, boolean approveFlag, List<DocumentLine> documentLineList) throws ClientException;

    List<MaterialLot> validationDocReservedMLot(String documentId, List<String> validationMLotIdList) throws ClientException;
}

