package com.newbiest.gc.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.gc.model.*;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by guoxunbo on 2019-08-21 12:41
 */
public interface GcService {

    void printMaterialCodeLabel(MaterialLot materialLot, String printType) throws ClientException;

    List<MaterialLot> waferUnpackMLot(List<MaterialLotUnit> materialLotUnits) throws ClientException;
    List<MaterialLot> queryIssueRawMaterialByMaterialLotIdOrLotIdAndTableRrn(String queryLotId, Long tableRrn) throws ClientException;
    MaterialLot getRwMaterialLotByMaterialLotIdAndTableRrn(String materialLotId, Long tableRrn) throws ClientException;
    void issueRwMaterial(List<MaterialLot> materialLotList) throws ClientException;
    void spareRwMaterial(List<MaterialLot> materialLotList) throws ClientException;
    void CancelSpareRwMaterial(List<MaterialLot> materialLotList) throws ClientException;
    void receiveBladeMaterial(List<MaterialLot> materialLotList) throws ClientException;
    String validateAndGetBladeMLotId(String bladeMaterialLotCode) throws ClientException;
    MaterialLot getMaterialLotByBladeMaterialCode(String bladeMaterialCode) throws ClientException;
    void receiveTapeMaterial(List<MaterialLot> materialLotList, String tapeSize) throws ClientException;
    List<MaterialLot> getMaterialLotByTapeMaterialCode(String tapeMaterialCode) throws ClientException;
    void rwStockOut(List<MaterialLot> materialLotList, List<DocumentLine> documentLineList) throws ClientException;
    void rwMaterialLotCancelStockTag(List<MaterialLot> materialLotList) throws ClientException;
    void rwMaterialLotAddShipOrderId(List<MaterialLot> materialLotList, String shipOrderId) throws ClientException;
    void rwMaterialLotStockOutTag(List<MaterialLot> materialLotList, String customerName, String abbreviation, String remarks) throws ClientException;
    List<MaterialLot> rwTagginggAutoPickMLot(List<MaterialLot> materialLotList, BigDecimal pickQty) throws ClientException;
    List<MesPackedLot> receiveRWFinishPackedLot(List<MesPackedLot> packedLots, String printLabel, String printCount) throws ClientException;
    Material saveProductAndSetStatusModelRrn(String name) throws ClientException;

    boolean validateMLotByPackageRule(MaterialLot materialLot,  List<MaterialLotAction> materialLotActions) throws ClientException;
    List<MaterialLotUnit> materialLotUnitAssignEng(List<MaterialLotUnit> materialLotUnitList) throws ClientException;

    void wltCpThreeSideShip(DocumentLine documentLine, List<MaterialLotAction> materialLotActions) throws ClientException;
    void wltCpMaterialLotSaleShip(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String checkSubCode, String actionType) throws ClientException;
    void mobileWltCpMaterialLotSaleShip(List<MaterialLotAction> materialLotActions, String erpTime, String checkSubCode, String actionType) throws ClientException;
    void rWAttributeChange(List<MaterialLot> materialLots) throws ClientException;

    String importRawMaterialLotList(List<MaterialLot> materialLotList, String importType) throws ClientException;
    void validateAndRawMaterialIssue(List<DocumentLine> documentLineList, List<MaterialLot> materialLots, String issueWithDoc) throws ClientException;
    void mobileValidateAndRawMaterialIssue(List<MaterialLot> materialLots, String erpTime, String issueWithDoc) throws ClientException;
    void scrapRawMaterial(List<MaterialLot> materialLotList, String reason, String remarks) throws ClientException;
    void receiveRawMaterial(List<MaterialLot> materialLotList) throws ClientException;
    void deleteMaterialLotAndSaveHis(List<MaterialLot> materialLotList, String deleteNote) throws ClientException;
    List<MaterialLot> queryRawMaterialByMaterialLotOrLotIdAndTableRrn(String mLotId, Long tableRrn) throws ClientException;
    List<MaterialLot> getWaitSpareRawMaterialLotListByOrderAndTableRrn(Long docLineRrn, Long tableRrn) throws ClientException;
    List<MaterialLot> getSpareRawMaterialLotListByDocLineRrrn(List<MaterialLot> materialLots, Long docLineRrn) throws ClientException;
    List<MaterialLot> getWaitSpareRawMaterialByReservedQty(List<MaterialLot> materialListLots, BigDecimal pickQty) throws ClientException;
    void rawMaterialMLotSpare(List<MaterialLot> materialLotList, Long docLineRrn) throws ClientException;
    String spareRawMLotOutDoc(List<MaterialLot> materialLotList) throws ClientException;
    void scrapRawMaterialShip(DocumentLine documentLine, List<MaterialLot> materialLotList) throws ClientException;
    void unRawMaterialSpare(List<MaterialLot> materialLotList) throws ClientException;

    void waferOutOrderIssue(List<MaterialLotAction> materialLotActions) throws ClientException;
    List<MaterialLot> receiveRmaMLot(List<MaterialLotAction> materialLotActions) throws ClientException;
    GcUnConfirmWaferSet saveUnConfirmWaferTrackSetInfo(GcUnConfirmWaferSet unConfirmWaferSet, String transType) throws ClientException;
    void receiveCOBFinishGood(List<MesPackedLot> packedLotList) throws ClientException;
    void validateAndReceiveCogMLot(List<DocumentLine> documentLines, List<MaterialLotAction> materialLotActions) throws ClientException;
    void ftStockOut(List<MaterialLotAction> materialLotActions, List<DocumentLine> documentLines) throws ClientException;
    void hongKongWarehouseByOrderStockOut(DocumentLine documentLine, List<MaterialLotAction> materialLotActions) throws ClientException;
    boolean validationHKStockOutMaterialLot(MaterialLot materialLot,  List<MaterialLotAction> materialLotActions) throws ClientException;
    MaterialLot getHKWarehouseStockOutMLot(Long tableRrn, String queryLotId) throws ClientException;
    void hongKongMLotReceive(List<MaterialLotAction> materialLotActions) throws ClientException;

    void mesSaveMaterialLotUnitHis(List<MaterialLotUnit> materialLotUnitList, String transId) throws ClientException;
    void mesReceiveRawMaterialAndSaveHis(List<MaterialLot> materialLotList, String transId) throws ClientException;
    void mesRawMaterialReturnWarehouse(List<MaterialLot> materialLotList, String transId, String materialType) throws ClientException;
    void mesMaterialLotBindWorkOrderAndSaveHis(List<MaterialLot> materialLotList, String transId) throws ClientException;
    void mesMaterialLotUnitBindWorkorderAndSaveHis(List<MaterialLotUnit> materialLotUnitList, String transId) throws ClientException;
    void mesMaterialLotUnitUnBindWorkorderAndSaveHis(List<MaterialLotUnit> materialLotUnitList, String transId) throws ClientException;
    void reconMaterialLotUnitAndSaveHis(List<MaterialLotUnit> materialLotUnitList, String transId) throws ClientException;
    void lswMaterialLotUnitEngHoldAndSaveHis(List<MaterialLotUnit> materialLotUnitList, String transId) throws ClientException;

    List<MaterialLotUnit> validateAndChangeMaterialNameByImportType(List<MaterialLotUnit> materialLotUnits, String importType) throws ClientException;
    List<GCProductNumberRelation> getProductNumberRelationByDocRrn(Long documentLineRrn) throws ClientException;
    List<MaterialLot> getMaterialLotByPackageRuleAndDocLine(Long documentLineRrn, List<MaterialLotAction> materialLotActions, String packageRule) throws ClientException;
    GCProductNumberRelation saveProductNumberRelation(GCProductNumberRelation productNumberRelation, String transType) throws ClientException;
    void validationMLotMaterialName(List<MaterialLotAction> materialLotActions) throws ClientException;
    List<MaterialLotUnit> queryFTWaitIssueMLotUnitList(long tableRrn) throws ClientException;
    void stockInFTWafer(List<MaterialLotUnit> materialLotUnits ,List<StockInModel> stockInModels) throws ClientException;
    List<MaterialLotUnit> queryFTMLotByUnitIdAndTableRrn(String unitId, long tableRrn) throws ClientException;
    void receiveFTWafer(List<MaterialLotUnit> materialLotUnitList) throws ClientException;
    List<MaterialLotUnit> createFTMaterialLotAndGetImportCode(List<MaterialLotUnit> materialLotUnits, String importType) throws ClientException;
    GCWorkorderRelation saveWorkorderGradeHoldInfo(GCWorkorderRelation workorderRelation, String transType) throws ClientException;
    String getEncryptionSubCode(String grade, String subcode) throws ClientException;
    List<MaterialLot> getMaterialLotByTableRrnAndMLotId(String mLotId, long tableRrn) throws ClientException;
    MaterialLot getMaterialLotByTableRrnAndMaterialLotIdOrLotId(Long tableRrn, String queryLotId) throws ClientException;
    void validationMaterialLotVender(List<MaterialLotAction> materialLotActions) throws ClientException;
    void waferUnStockOutTagging(List<MaterialLotAction> materialLotActions) throws ClientException;
    void waferStockOutTagging(List<MaterialLotAction> materialLotActions, String stockTagNote, String customerName, String stockOutType, String poId, String address) throws ClientException;
    void wltStockOut(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String checkSubCode, String actionType) throws ClientException;
    void mobileWltStockOut(List<MaterialLotAction> materialLotActions,String erpTime, String checkSubCode, String actionType) throws ClientException;
    void wltOtherStockOut(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String actionType) throws ClientException;
    boolean validationWltStockOutMaterialLot(MaterialLot materialLot,  List<MaterialLotAction> materialLotActions) throws ClientException;
    List<MaterialLotUnit> validateImportWltPackReturn(List<MaterialLotUnit> materialLotUnitList) throws ClientException;
    boolean validateStockOutMaterialLot(MaterialLot materialLot,  List<MaterialLotAction> materialLotActions) throws ClientException;
    List<Map<String, String>> getMlotCodePrintParameter(MaterialLot materialLot, String printType) throws ClientException;
    void purchaseOutsourceWaferReceive(List<MaterialLotAction> materialLotActions)throws ClientException;
    List<MaterialLotUnit> validateAndSetWaferSource(String importType, String checkFourCodeFlag, List<MaterialLotUnit> materialLotUnitList)throws ClientException;
    void deleteCogDetail(List<GCLcdCogDetail> lcdCogDetails, String deleteNote)throws ClientException;
    void deleteCogEcretive(List<MaterialLot> lcdCogEcretiveList, String deleteNote) throws ClientException;
    String saveLCDCOGDetailList(List<MaterialLot> materialLots, String importType)throws ClientException;
    List<MaterialLot> validationAndGetWaitIssueWafer(Long tableRrn,String whereClause) throws ClientException;
    MaterialLot mobileValidationAndGetWait(Long tableRrn,String lotId) throws ClientException;
    MaterialLot queryCOBReceiveMaterialLotByTabRrnAndLotId(Long tableRrn, String lotId)throws Exception;

    void materialLotRelease(List<MaterialLot> materialLotList, String ReleaseReason, String remarks) throws ClientException;
    void materialLotHold(List<MaterialLot> materialLotList, String holdReason, String remarks) throws ClientException;
    void updateMaterialLotLocation(List<MaterialLot> materialLotList , String location, String remarks) throws ClientException;
    void updateMaterialLotTreasuryNote(List<MaterialLot> materialLotList, String treasuryNote) throws ClientException;
    void updateMaterialLotInfo(MaterialLot materialLot) throws ClientException;
    List<MaterialLot> getMaterialLotsByImportFileAndNbTable(List<MaterialLot> materialLotList, NBTable nbTable) throws ClientException;

    void validateMLotUnitProductAndSubcode(List<MaterialLotUnit> materialLotUnitList) throws ClientException;
    String validationAndGetBondedPropertyByFileName(String fileName) throws ClientException;
    GCProductSubcode saveProductSubcode(GCProductSubcode gcProductSubcode) throws ClientException;
    void importProductSubCode(List<GCProductSubcode> productSubcodeList) throws ClientException;
    List<MesPackedLot> receiveWltFinishGood(List<MesPackedLot> packedLotList, String printLabel, String printCount) throws ClientException;
    MaterialLot getWaitStockInStorageWaferByLotId(String lotId, Long tableRrn) throws ClientException;
    void deleteIncomingMaterialLot(List<MaterialLotUnit> materialLotUnitList, String deleteNote) throws ClientException;
    String saveIncomingMaterialList(List<MaterialLot> materialLots, String importType)throws ClientException;
    boolean validateRmaImportMaterialLot(List<MaterialLot> materialLotList) throws ClientException;
    void validationStockOutMaterialLot(MaterialLot materialLot,  List<MaterialLotAction> materialLotActions)throws ClientException;
    void validationAndReceiveWafer(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String receiveWithDoc) throws ClientException;
    void saveHNWarehouseImportList(List<MaterialLot> materialLotList) throws ClientException;

    void validationAndWaferIssue(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String issueWithDoc, String unPlanLot) throws ClientException;
    void mobileValidationAndWaferIssue(String erpTime, List<MaterialLotAction> materialLotActions, String issueWithDoc, String unPlanLot) throws ClientException;

    void materialLotWeight(List<WeightModel> weightModels) throws ClientException;
    MaterialLot getWaitWeightMaterialLot(String materialLotId, Long tableRrn) throws ClientException;

    List<MaterialLot> getPackedDetailsAndNotReserved(List<String> packedLotRrn) throws ClientException;
    List<MaterialLot> getWaitForReservedMaterialLot(Long documentLineRrn, Long tableRrn)  throws ClientException;
    DocumentLine reservedMaterialLot(Long documentLineRrn, List<MaterialLotAction> materialLotActions, String stockNote) throws ClientException;
    void unReservedMaterialLot(List<MaterialLotAction> materialLotActions) throws ClientException;

    MaterialLot getWaitStockInStorageMaterialLotByLotIdOrMLotId(String mLotId, Long tableRrn) throws ClientException;
    MaterialLot getMaterialLotByMaterialLotIdAndTableRrn(String materialLotId, long tableRrn) throws ClientException;
    void stockIn(List<StockInModel> stockInModels) throws ClientException;

    MesPackedLot findByPackedLotRrn(Long packedLotRrn) throws ClientException;
    List<MesPackedLot> findByParentRrn(Long parentRrn) throws ClientException;
    void receiveFinishGood(List<MesPackedLot> packedLotList) throws ClientException;

    void bindRelaxBox(List<MaterialLot> materialLots, String relaxBoxId) throws ClientException;
    void unbindRelaxBox(List<MaterialLot> materialLots) throws ClientException;

    void judgePackedMaterialLot(List<MaterialLot> materialLots, List<StockOutCheck> checkList) throws ClientException;
    void cancelCheckMaterialLot(List<MaterialLot> materialLots, String cancelReason) throws ClientException;

    List<NBOwnerReferenceList> getJudgePackCaseCheckList() throws ClientException;
    List<NBOwnerReferenceList> getWltJudgePackCaseCheckList() throws ClientException;
    List<NBOwnerReferenceList> getStockOutCheckList() throws ClientException;
    List<NBOwnerReferenceList> getWltStockOutCheckList() throws ClientException;
    List<NBOwnerReferenceList> getReferenceListByName(String reserenceName) throws ClientException;
    void stockOutCheck(List<MaterialLot> materialLots, List<StockOutCheck> ngStockOutCheckList) throws ClientException;

    void asyncReceiveOrder() throws ClientException;
    void asyncShipOrder() throws ClientException;
    void asyncCogReceiveOrder() throws ClientException;

    void asyncFtRetestIssueOrder() throws ClientException;
    void asyncOtherIssueOrder() throws ClientException;
    void asyncOtherStockOutOrder() throws ClientException;
    void asyncOtherShipOrder() throws ClientException;
    void asyncMaterialIssueOrder() throws ClientException;
    void asyncRawMaterialOtherShipOrder() throws ClientException;

    void asyncReTestOrder() throws ClientException;
    void asyncWaferIssueOrder() throws ClientException;
    void asyncWaferIssueOrderAndOtherIssueOrder() throws ClientException;
    void asyncWltCpShipOrder() throws ClientException;

    void asyncMesProduct() throws ClientException;
    void asyncMesWaferType() throws ClientException;
    void asyncMesProductAndSubcode() throws ClientException;
    void asyncMesProductModelConversion() throws ClientException;
    void asyncProductGradeAndSubcode() throws ClientException;
    void asyncPoSupplier() throws ClientException;
    void asyncMesMaterialModel() throws ClientException;
    void asyncMesGlueType() throws ClientException;
    void asyncMaterialName() throws ClientException;

    void valaidateAndMergeErpDocLine(List<DocumentLine> documentLineList) throws ClientException;

    void checkMaterialInventory(List<MaterialLot> existMaterialLots, List<MaterialLot> errorMaterialLots) throws ClientException;

    void validationDocLine(List<DocumentLine> documentLineList, MaterialLot materialLot) throws ClientException;
    void validationDocLine(DocumentLine documentLine, MaterialLot materialLot) throws ClientException;

    void stockOut(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions) throws ClientException;
    void reTest(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String reTestType) throws ClientException;
    void mobileReTest(List<MaterialLotAction> materialLotActions, String erpTime) throws ClientException;
    List<DocumentLine> validationAndGetDocumentLineList(List<DocumentLine> documentLines, MaterialLot materialLot) throws ClientException;

    List<Map> findEntityMapListByQueryName(String queryName, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause) throws ClientException;
    List<Map> findEntityMapListByQueryText(String queryText, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause) throws ClientException;
    List<MaterialLot>  getWaitChangeStorageMaterialLotByRelayBoxId(String relayBoxId) throws ClientException;
    void transferStorage(List<RelayBoxStockInModel> relayBoxStockInModel) throws  ClientException;
    List<MaterialLot> getMaterialLotAndDocUserToUnReserved(Long tableRrn,String whereClause) throws ClientException;

}
