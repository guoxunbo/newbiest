package com.newbiest.vanchip.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBQuery;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.*;
import com.newbiest.vanchip.dto.print.model.*;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author guoxunbo
 * @date 12/24/20 2:22 PM
 */
public interface VanChipService {

    void importIncomingOrder(String incomingDocId, List<MaterialLot> materialLots) throws ClientException;

    void unbindMesOrder(List<String> materialLotIdList) throws ClientException;
    void bindMesOrder(List<String> materialLotIdList, String workOrderId) throws ClientException;

    void deleteIncomingMaterialLot(List<MaterialLot> materialLotList, String deleteNote) throws ClientException;

    List<MaterialLot> getMLotByOrderId(String documentId) throws ClientException;
    void issueMLotByDoc(String documentId, List<String> materialLotIdList) throws ClientException;
    void issueMaterialByDoc(String documentId, List<String> materialLotIdList) throws ClientException;
    void issueMLotByOrder(String documentId, List<String> materialLotIdList) throws ClientException;

    void returnMLotByDoc(String documentId, List<String> materialLotIdList) throws ClientException;

    List<MaterialLot> getReservedMLotByStandardQty(DocumentLine documentLine, BigDecimal standardQty) throws ClientException;
    List<MaterialLot> getReservedMaterialLot(DocumentLine documentLine) throws ClientException;
    void reservedMaterialLot(DocumentLine documentLine, List<MaterialLotAction> materialLotActionList) throws ClientException;
    void unReservedMaterialLot(List<MaterialLotAction> materialLotActionList) throws ClientException;

    List<MaterialLot> printReservedOrder(DocumentLine documentLine) throws ClientException;

    void issueFinishGoodByDoc(String documentId, List<String> materialLotIds) throws ClientException;
    void batchIqc(List<MaterialLotAction> materialLotActions, String urlRemark, List<MLotCheckSheetLine> mLotCheckSheetLines) throws ClientException;
    List<MaterialLot> validationAndGetWaitIqcMLot(List<String> materialLotIds) throws ClientException;

    void createFinishGoodOrder(String documentId, boolean approveFlag, List<MaterialLot> materialLots) throws ClientException;
    void receiveFinishGood(String documentId, List<String> materialLotIdList) throws ClientException;

    MaterialLot packageMaterialLots(List<MaterialLotAction> materialLotActions, String packageType) throws ClientException;
    List<MaterialLot> unPack(List<MaterialLotAction> materialLotActions) throws ClientException;
    MaterialLot appendPacking(MaterialLot packedMaterialLot, List<MaterialLotAction> materialLotActions) throws ClientException;

    List<MaterialLot> getWaitShipMLotByDocLineId(String docLineId) throws ClientException;
    void shipOut(String docLineId, List<MaterialLot> materialLots) throws ClientException;

    List<MaterialLot> getMLotByLineObjectRrn(String docLineObjectRrn) throws ClientException;
    void picks(List<MaterialLotAction> materialLotActions) throws ClientException;

    MaterialLot weightMaterialLot(String materialLotId, String grossWeight, String cartonSize) throws ClientException;

    CocPrintInfo getCOCPrintParameter (String documentLineId) throws ClientException;
    PackingListPrintInfo getPackingListPrintParameter(String documentLineId) throws ClientException;
    ShippingListPrintInfo getShippingListPrintParameter(String documentLineId) throws ClientException;
    PKListPrintInfo getPKListParameter(String documentLineId) throws ClientException;
    Map<String, Object> getBoxPrintParameter(String materialLotId) throws ClientException;
    DeliveryPrintInfo getDeliveryOrderParameter(String documentLineId) throws ClientException;
    RSAndScrapInfo getReturnSuppleOrderAndScrapOrderPrintInfo(String documentId) throws ClientException;

    void packCheck(MaterialLotAction materialLotAction) throws ClientException;

    void validataStockInByOrder(String docId, List<MaterialLotAction> materialLotAction) throws ClientException;
    MaterialLot stockInMLotMobile(MaterialLotAction materialLotAction) throws ClientException;
    void stockInMLotByOrderMobile(String docId, List<MaterialLotAction> materialLotAction) throws ClientException;

    MaterialLot stockOutMLotMobile(MaterialLotAction materialLotAction) throws ClientException;
    void stockOutMLotByOrder(String documentId, List<MaterialLotAction> materialLotAction) throws ClientException;
    MaterialLot queryPackageMLotMobile(MaterialLotAction materialLotAction) throws ClientException;
    //MaterialLotInventory checkMlotInventoryMobile(MaterialLotAction materialLotAction) throws ClientException;
    void transferInvMobile(MaterialLotAction materialLotAction) throws ClientException;

    RawMaterial saveRawMaterial(RawMaterial rawMaterial) throws ClientException;
    Product saveProduct(Product product) throws ClientException;
    LabMaterial saveLabMaterial(LabMaterial material)throws ClientException;

    Storage saveStorageInfo(Storage storages) throws ClientException;

    List<Map> findEntityMapListByQueryText(String queryText, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause) throws ClientException;
    Query findEntityByQueryText(String queryText, String whereClause, String orderByClause) throws ClientException;
    NBQuery findNBQueryByName(String queryName, boolean exceptionFlag) throws ClientException;

    void valiadateFromWarehouse(MaterialLotAction materialLotAction) throws ClientException;
    void valiadateTargetWarehouse(MaterialLotAction materialLotAction) throws ClientException;
    void transferInvMLots(List<MaterialLotAction> materialLotActions) throws ClientException;

    void preWarning() throws ClientException;

    Parts saveParts(Parts parts) throws ClientException;

    void sendMLotInvByErp(DocumentLine documentLine, List<MaterialLotAction> materialLotActions) throws ClientException;
    void checkMLotInventorys(DocumentLine documentLine,List<MaterialLotAction> materialLotActions) throws ClientException;
    void recheckMLotInventorys(DocumentLine documentLine,List<MaterialLotAction> materialLotActions) throws ClientException;
    List<MaterialLot> getRecheckMLots(DocumentLine documentLine)throws ClientException;

    void stockOutPartsByOrder(String documentId, MaterialLotAction materialLotAction) throws ClientException;
    void stockOutParts(Material material, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot returnMLotWarehouse(Material material, String mLotId, MaterialLotAction materialLotAction) throws ClientException;

    void erpSaveMaterial(List<Material> materials) throws ClientException;

    MaterialLot getBoxMLotBySubBoxMLotId(String subBoxMLotId) throws ClientException;
    void printBoxMLot(String subBoxMaterialLotId, MaterialLotAction materialLotAction, Boolean validationPrintFlag) throws ClientException;
    void iqcApprove(List<MaterialLotAction> materialLotActions) throws ClientException;
    void startIqc(List<String> materialLotIds)throws ClientException;

    List<MaterialLot> getReservedMLotByOrder(String documentLineId)throws ClientException;
    void stockUpMLot(String documentLineId, List<MaterialLot> materialLots)throws ClientException;
    List<MaterialLot> getStockUpMLot(String docId)throws ClientException;
    MaterialLot validateReservedMLot(String docLineId, String materialLotId) throws ClientException;

    void updateERPSo(List<MaterialLot> materialLots) throws ClientException;
    void returnMLotByDocLine(String documentId, List<MaterialLot> materialLotList) throws ClientException;
    void scrapMLot(String docId, List<MaterialLot> materialLotList)throws ClientException;
    void updateProductionDate(String materialLotId, String iclDateValue) throws ClientException;

    void sendMailByPackingList() throws ClientException;
    void asyncIncomingOrReturn() throws ClientException;
    void asyncDeliveryInfo() throws ClientException;

    DocumentLine deleteDeliveryOrder(String deliveryOrderLineId) throws ClientException;
    Map<String, Object> buildBoxParameterMap(MaterialLot boxMaterialLot) throws ClientException;

    List<MaterialLot> updateRmaNo(List<MaterialLot> materialLots, MaterialLotAction materialLotAction) throws ClientException;

}
