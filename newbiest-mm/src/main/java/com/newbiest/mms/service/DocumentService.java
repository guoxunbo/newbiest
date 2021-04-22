package com.newbiest.mms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.Document;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.LabMaterial;
import com.newbiest.mms.model.MaterialLot;

import java.util.List;

/**
 * 单据相关service
 * @author guoxunbo
 * @date 12/24/20 2:36 PM
 */
public interface DocumentService {

    void receiveIncomingLot(String documentId, List<MaterialLot> materialLots) throws ClientException;
    void approveDocument(Document document) throws ClientException;
    void approveDocument(String documentId) throws ClientException;

    String generatorDocId(String generatorRule) throws ClientException;

    void issueMaterialLotByDoc(String documentId, List<String> materialLotIdList);

    String createIssueLotOrder(String documentId, boolean approveFlag, List<String> materialLotIdList) throws ClientException;
    void issueMLotByDoc(String issueLotOrderId, List<String> materialLotIdList) throws ClientException;

    String createIssueMaterialOrder(String documentId, boolean approveFlag, List<String> materialLotIdList) throws ClientException;
    void issueMaterialByDoc(String issueMaterialOrderId, List<String> materialLotIdList)throws ClientException;

    void createReturnOrder(String documentId, boolean approveFlag, List<MaterialLotAction> materialLotActions) throws ClientException;
    void returnMLotByDoc(String documentId, List<String> materialLotIdList) throws ClientException;

    List<MaterialLot> getReservedMLotByDocId(String documentId) throws ClientException;

    String createDeliveryOrder(String documentId, boolean approveFlag, List<DocumentLine> documentLineList) throws ClientException;

    List<MaterialLot> validationDocReservedMLot(String documentId, List<String> validationMLotIdList) throws ClientException;

    void createIssueFinishGoodOrder(String documentId, boolean approveFlag, List<String> materialLotIdList) throws ClientException;
    void issueFinishGoodByDoc(String documentId, List<String> materialLotIdList) throws ClientException;

    void changeDocMLotStatus(String documentId, List<String> materialLotIds, String status)throws ClientException;

    Document createIssueLabMLotOrder(String documentId, boolean approveFlag, List<LabMaterial> labMaterials) throws ClientException;
    List<MaterialLot> recommendIssueLabMLot(String documentId) throws ClientException;
    void issueLabMLot(String documentId, List<String> materialLotIds) throws ClientException;

    Document getDocumentByName(String documentId, boolean throwExceptionFlag) throws ClientException;
}

