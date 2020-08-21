package com.newbiest.gc.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.gc.model.*;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DeliveryOrder;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;

import java.util.List;
import java.util.Map;

/**
 * Created by guoxunbo on 2019-08-21 12:41
 */
public interface GcService {

    void wltStockOut(DocumentLine documentLine, List<MaterialLotAction> materialLotActions) throws ClientException;
    boolean validationWltStockOutMaterialLot(MaterialLot materialLot,  List<MaterialLotAction> materialLotActions) throws ClientException;
    List<MaterialLotUnit> validateImportWltPackReturn(List<MaterialLotUnit> materialLotUnitList) throws ClientException;
    boolean validateStockOutMaterialLot(MaterialLot materialLot,  List<MaterialLotAction> materialLotActions) throws ClientException;
    List<Map<String, String>> getMlotCodePrintParameter(List<MaterialLot> materialLotList, String printType) throws ClientException;
    void purchaseOutsourceWaferReceive(List<MaterialLotAction> materialLotActions)throws ClientException;
    List<MaterialLotUnit> validateAndSetWaferSource(String importType, String checkFourCodeFlag, List<MaterialLotUnit> materialLotUnitList)throws ClientException;
    void deleteCogDetail(List<GCLcdCogDetail> lcdCogDetails, String deleteNote)throws ClientException;
    void deleteCogEcretive(List<MaterialLot> lcdCogEcretiveList, String deleteNote) throws ClientException;
    Map<String, String> getCOBBoxLabelPrintParamater(MaterialLot materialLot) throws ClientException;
    List<Map<String, String>> getBoxQRCodeLabelPrintParamater(MaterialLot materialLot, String printVboxLabelFlag) throws ClientException;
    String saveLCDCOGDetailList(List<MaterialLot> materialLots, String importType)throws ClientException;
    List<MaterialLot> validationAndGetWaitIssueWafer(List<MaterialLotAction> materialLotActions) throws ClientException;
    void materialLotRelease(List<MaterialLot> materialLotList, String ReleaseReason, String remarks) throws ClientException;
    void materialLotHold(List<MaterialLot> materialLotList, String holdReason, String remarks) throws ClientException;
    void updateMaterialLotLocation(List<MaterialLot> materialLotList , String location, String remarks) throws ClientException;
    void updateMaterialLotTreasuryNote(List<MaterialLot> materialLotList, String treasuryNote) throws ClientException;
    void validateMLotUnitProductAndSubcode(List<MaterialLotUnit> materialLotUnitList) throws ClientException;
    String validationAndGetBondedPropertyByFileName(String fileName) throws ClientException;
    GCProductSubcode saveProductSubcode(GCProductSubcode gcProductSubcode) throws ClientException;
    void receiveWltFinishGood(List<MesPackedLot> packedLotList) throws ClientException;
    MaterialLot getWaitStockInStorageWaferByLotId(String lotId) throws ClientException;
    void deleteIncomingMaterialLot(List<MaterialLotUnit> materialLotUnitList, String deleteNote) throws ClientException;
    String saveIncomingMaterialList(List<MaterialLot> materialLots, String importType)throws ClientException;
    void validationStockOutMaterialLot(MaterialLot materialLot,  List<MaterialLotAction> materialLotActions)throws ClientException;
    void validationAndReceiveWafer(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions) throws ClientException;

    void validationAndWaferIssue(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions) throws ClientException;

    void materialLotWeight(List<WeightModel> weightModels) throws ClientException;
    MaterialLot getWaitWeightMaterialLot(String materialLotId) throws ClientException;

    List<MaterialLot> getPackedDetailsAndNotReserved(List<String> packedLotRrn) throws ClientException;
    List<MaterialLot> getWaitForReservedMaterialLot(Long documentLineRrn, Long tableRrn)  throws ClientException;
    DocumentLine reservedMaterialLot(Long documentLineRrn, List<MaterialLotAction> materialLotActions, String stockNote) throws ClientException;
    void unReservedMaterialLot(List<MaterialLotAction> materialLotActions) throws ClientException;

    MaterialLot getWaitStockInStorageMaterialLot(String materialLotId) throws ClientException;
    void stockIn(List<StockInModel> stockInModels) throws ClientException;

    MesPackedLot findByPackedLotRrn(Long packedLotRrn) throws ClientException;
    List<MesPackedLot> findByParentRrn(Long parentRrn) throws ClientException;
    void receiveFinishGood(List<MesPackedLot> packedLotList,boolean doWltReceiveFlag) throws ClientException;

    void bindRelaxBox(List<MaterialLot> materialLots, String relaxBoxId) throws ClientException;
    void unbindRelaxBox(List<MaterialLot> materialLots) throws ClientException;

    void judgePackedMaterialLot(List<MaterialLot> materialLots, List<StockOutCheck> checkList) throws ClientException;

    List<NBOwnerReferenceList> getJudgePackCaseCheckList() throws ClientException;
    List<NBOwnerReferenceList> getWltJudgePackCaseCheckList() throws ClientException;
    List<NBOwnerReferenceList> getStockOutCheckList() throws ClientException;
    List<NBOwnerReferenceList> getWltStockOutCheckList() throws ClientException;
    List<NBOwnerReferenceList> getReferenceListByName(String reserenceName) throws ClientException;
    void stockOutCheck(List<MaterialLot> materialLots, List<StockOutCheck> ngStockOutCheckList, String expressNumber) throws ClientException;

    void asyncReceiveOrder() throws ClientException;
    void asyncShipOrder() throws ClientException;

    void asyncOtherIssueOrder() throws ClientException;
    void asyncOtherStockOutOrder() throws ClientException;
    void asyncOtherShipOrder() throws ClientException;

    void asyncReTestOrder() throws ClientException;
    void asyncWaferIssueOrder() throws ClientException;
    void asyncWaferIssueOrderAndOtherIssueOrder() throws ClientException;

    void asyncMesProduct() throws ClientException;
    void asyncMesWaferType() throws ClientException;
    void asyncMesProductAndSubcode() throws ClientException;
    void asyncMesProductModelConversion() throws ClientException;
    void asyncProductGradeAndSubcode() throws ClientException;

    void checkMaterialInventory(List<MaterialLot> existMaterialLots, List<MaterialLot> errorMaterialLots) throws ClientException;

    void validationDocLine(List<DocumentLine> documentLineList, MaterialLot materialLot) throws ClientException;
    void validationDocLine(DocumentLine documentLine, MaterialLot materialLot) throws ClientException;
    
    void stockOut(DocumentLine documentLine, List<MaterialLotAction> materialLotActions) throws ClientException;
    void reTest(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions) throws ClientException;
    List<DocumentLine> validationAndGetDocumentLineList(List<DocumentLine> documentLines, MaterialLot materialLot) throws ClientException;

    List<Map> findEntityMapListByQueryName(String queryName, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause) throws ClientException;
    List<MaterialLot>  getWaitChangeStorageMaterialLotByRelayBoxId(String relayBoxId) throws ClientException;
    void transferStorage(List<RelayBoxStockInModel> relayBoxStockInModel) throws  ClientException;
    List<MaterialLot> getMaterialLotAndDocUserToUnReserved(Long tableRrn,String whereClause) throws ClientException;
}
