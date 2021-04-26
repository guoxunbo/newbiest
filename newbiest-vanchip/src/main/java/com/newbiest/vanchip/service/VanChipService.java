package com.newbiest.vanchip.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.*;

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

    List<MaterialLot> getReservedMaterialLot(DocumentLine documentLine) throws ClientException;

    void reservedMaterialLot(DocumentLine documentLine, List<MaterialLotAction> materialLotActionList) throws ClientException;

    void unReservedMaterialLot(List<MaterialLotAction> materialLotActionList) throws ClientException;

    List<MaterialLot> printReservedOrder(DocumentLine documentLine) throws ClientException;

    List<MaterialLot> stockInFinishGood(List<MaterialLot> materialLots, List<MaterialLotAction> materialLotActionList) throws ClientException;

    void issueFinishGoodByDoc(String documentId, List<String> materialLotIds) throws ClientException;
    void batchIqc(List<String> materialLotIds, MaterialLotAction materialLotAction) throws ClientException;
    List<MaterialLot> validationAndGetWaitIqcMLot(List<String> materialLotIds) throws ClientException;

    void createFinishGoodOrder(String documentId, boolean approveFlag, List<MaterialLot> materialLots) throws ClientException;
    void receiveFinishGood(String documentId, List<String> materialLotIdList) throws ClientException;

    MaterialLot packageMaterialLots(List<MaterialLotAction> materialLotActions, String packageType) throws ClientException;
    List<MaterialLot> unPack(List<MaterialLotAction> materialLotActions) throws ClientException;
    MaterialLot appendPacking(MaterialLot packedMaterialLot, List<MaterialLotAction> materialLotActions) throws ClientException;

    void asyncMesProduct() throws ClientException;

    void stockOut(DocumentLine documentLine, List<MaterialLotAction> materialLotActions) throws ClientException;

    List<MaterialLot> getMLotByLineObjectRrn(String docLineObjectRrn) throws ClientException;

    List<MaterialLot> getWaitShipMLotByDocLine(DocumentLine documentLine) throws ClientException;

    List<MaterialLotInventory> getMLotInventoryByDoc(Document document) throws ClientException;
    List<MaterialLotInventory> getMLotInventoryByDocId(String documentId) throws ClientException;
    List<MaterialLot> getMLotByBoxMaterialLotId(String materialLot) throws ClientException;
    void picks(List<MaterialLotAction> materialLotActions) throws ClientException;

    MaterialLot weightMaterialLot(String materialLotId, String grossWeight, String cartonSize) throws ClientException;

    Map<String, Object> getBoxPrintParameter(String materialLotId) throws ClientException;
    Map<String, Object> getCOCPrintParameter(String documentLineId) throws ClientException;
    Map<String, Object> getPackingListPrintParameter(String documentLineId) throws ClientException;
    List<Map<String, Object>> getPackingListMLotParameter(String documentLineId) throws ClientException;
    Map<String, Object> getShippingListPrintParameter(String documentLineId) throws ClientException;
    List<Map<String, Object>> getShippingListPrintMLotParameter(String documentLineId) throws ClientException;
    Map<String, Object> getPKListParameter(String documentLineId) throws ClientException;
    List<Map<String, Object>> getPKListMLotParameter(String documentLineId);

    void packCheckPass(List<MaterialLot> materialLots) throws ClientException;
    void packCheckNg(MaterialLotAction materialLotAction) throws ClientException;

    void reservedSendMail(DocumentLine documentLine, List<MaterialLotAction> materialLotActionList);

    MaterialLot stockInMLotMobile(MaterialLotAction materialLotAction)throws ClientException;
    MaterialLot stockOutMLotMobile(MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot queryPackageMLotMobile(MaterialLotAction materialLotAction) throws ClientException;
    void shipOutMobile(String documentId ,MaterialLotAction materialLotAction) throws ClientException;
    MaterialLotInventory checkMlotInventoryMobile(MaterialLotAction materialLotAction) throws ClientException;

    RawMaterial saveRawMaterial(RawMaterial rawMaterial) throws ClientException;
    Product saveProduct(Product product) throws ClientException;
    LabMaterial saveLabMaterial(LabMaterial material)throws ClientException;

    Storage saveStorageInfo(Storage storages) throws ClientException;
}
