package com.newbiest.mms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.*;

import java.math.BigDecimal;
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
    String generatorDocId(String generatorRule, Document document) throws ClientException;

    void issueMaterialLotByDoc(String documentId, List<String> materialLotIdList);

    String createIssueLotOrder(String documentId, boolean approveFlag, List<String> materialLotIdList) throws ClientException;
    void issueMLotByDoc(String issueLotOrderId, List<String> materialLotIdList) throws ClientException;

    String createIssueMaterialOrder(String documentId, boolean approveFlag, List<String> materialLotIdList) throws ClientException;
    void issueMaterialByDoc(String issueMaterialOrderId, List<String> materialLotIdList)throws ClientException;

    void createReturnOrder(String documentId, boolean approveFlag, List<MaterialLotAction> materialLotActions) throws ClientException;
    void returnMLotByDoc(String documentId, List<String> materialLotIdList, String returnTarget) throws ClientException;
    Document createReturnMLotOrder(String documentId, boolean approveFlag, List<MaterialLotAction> materialLotActions) throws ClientException ;

    List<MaterialLot> getReservedMLotByDocId(String documentId) throws ClientException;

    String createDeliveryOrder(String documentId, boolean approveFlag, List<DocumentLine> documentLineList) throws ClientException;

    List<MaterialLot> validationDocReservedMLot(String documentId, List<String> validationMLotIdList) throws ClientException;

    void createIssueFinishGoodOrder(String documentId, boolean approveFlag, List<String> materialLotIdList) throws ClientException;
    void issueFinishGoodByDoc(String documentId, List<String> materialLotIdList) throws ClientException;

    void changeDocMLotStatus(String documentId, List<String> materialLotIds, String status)throws ClientException;

    Document createIssueByMaterialOrder(String documentId, boolean approveFlag, List<Material> materials, MaterialLotAction materialLotAction) throws ClientException;
    List<MaterialLot> recommendIssueByMaterialOrder(String documentId) throws ClientException;
    void issueByMaterial(String documentId, List<String> materialLotIds) throws ClientException;

    Document getDocumentByName(String documentId, boolean throwExceptionFlag) throws ClientException;
    DocumentLine getDocumentLineByLineId(String documentLineId, boolean throwExceptionFlag) throws ClientException;

    Document createIssueMaterialLotOrder(String documentId, boolean approveFlag, List<MaterialLot> materialLots, MaterialLotAction materialLotAction) throws ClientException;
    void issueMaterialLotByOrder(String documentId, List<String> materialLotIdList) throws ClientException;

    List<MaterialLot> getMLotByDocumentId(String documentId) throws ClientException;
    Document getDocumentByMLotIdAndDocumentCategory(String mLotId, String documentCategory)throws ClientException;
    DocumentMLot createDocumentMLot(String documentId, String mLotId)throws ClientException;
    DocumentMLot createDocumentMLot(String documentId, String materialLotId, String status) throws ClientException;

    void createReturnLotOrder(String documentId, boolean approveFlag, List<MaterialLot> materialLots)throws ClientException;
    void returnLotOrder(String returnLotId, List<String> materialLotIds) throws ClientException;

    void deleteDocument(String documentId) throws ClientException;
    Document createDocument(Document document) throws ClientException;
    DocumentLine createDocLineByDocument(DocumentLine documentLine) throws ClientException;

    Document createDocument(Document document, String documentId, String idGeneratorRule, boolean approveFlag, BigDecimal qty) throws ClientException;
    Document saveDocument(String documentId, BigDecimal handleQty, String transType) throws ClientException;
    Document saveDocument(String documentId, BigDecimal handleQty, String transType, String docStatus) throws ClientException;

    void createIssuePartsOrder(String documentId, boolean approveFlag, String materialName, BigDecimal qty, String creator, String comments) throws ClientException;

    void createCheckOrder(Document document, List<MaterialLot> materialLotList) throws ClientException;
    void createScrapOrder(Document document, List<MaterialLot> materialLotList) throws ClientException;
    void deleteScrapOrder(Document document) throws ClientException;
    //void scrapMLot(String docId, List<MaterialLot> materialLotList) throws ClientException;

    Document createDeptReturnOrder(String documentId, boolean approveFlag, List<MaterialLotAction> materialLotActions) throws ClientException;
    void deptReturnMLot(String documentId, List<String> materialLotIdList) throws ClientException;
}

