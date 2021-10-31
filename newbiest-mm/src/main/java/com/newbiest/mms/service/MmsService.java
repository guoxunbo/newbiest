package com.newbiest.mms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.*;
import com.newbiest.mms.state.model.MaterialStatusModel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by guoxunbo on 2019/2/13.
 */
public interface MmsService {


    MaterialStatusModel getStatusModelByRrn(String statusModelRrn) throws ClientException;
    MaterialStatusModel getStatusModelByName(String statusModelName, boolean throwExceptionFlag) throws ClientException;

    MaterialLot createMLot(Material material, StatusModel statusModel, String mLotId, BigDecimal transQty, BigDecimal transSubQty, Map<String, Object> propsMap) throws ClientException;
    List<MaterialLot> receiveMLot(Material material, List<MaterialLot> materialLotList) throws ClientException;
    MLotCheckSheet iqc(MaterialLotAction materialLotJudgeAction, String urlRemark, List<MLotCheckSheetLine> mLotCheckSheetLines) throws ClientException;
    MLotCheckSheet oqc(MaterialLotAction materialLotJudgeAction) throws ClientException;
    MaterialLot issue(MaterialLot materialLot) throws ClientException;
    MaterialLot returnMLot(MaterialLot materialLot) throws ClientException;
    MaterialLot waitReturnMLot(MaterialLot materialLot) throws ClientException;
    MaterialLot returnMaterialLot(MaterialLot materialLot) throws ClientException;
    MaterialLot scrapMLot(MaterialLot materialLot) throws ClientException;

    List<MaterialLot>  holdMaterialLot(List<MaterialLotAction> materialLotActions) throws ClientException;
    MaterialLot holdMaterialLot(String materialLotId, List<MaterialLotAction> materialLotActions) throws ClientException;
    void saveMaterialLotHold(MaterialLotHold materialLotHold) throws ClientException;

    void releaseMaterialLot(List<MaterialLotHold>  materialLotHolds, MaterialLotAction releaseLotAction) throws ClientException;
    void releaseMaterialLot(String materialLotId, List<MaterialLotHold>  materialLotHolds, MaterialLotAction releaseLotAction) throws ClientException;

    List<MaterialLot> splitStandardMLot(String parentMaterialLotId, BigDecimal standardQty) throws ClientException;

    MaterialLot splitMLot(String parentMaterialLotId, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot splitAndPrintMLot(String parentMaterialLotId, MaterialLotAction materialLotAction) throws ClientException;

    // rawMaterial
    RawMaterial saveRawMaterial(RawMaterial rawMaterial) throws ClientException;
    RawMaterial saveRawMaterial(RawMaterial rawMaterial, String warehouseName,String iqcSheetName) throws ClientException;
    RawMaterial getRawMaterialByName(String name) throws ClientException;

    // MaterialLot
    MaterialLot getMLotByMLotId(String mLotId, boolean throwExceptionFlag) throws ClientException;
    MaterialLot getMLotByMLotId(String mLotId) throws ClientException;
    MaterialLot getMLotByObjectRrn(String materialLotRrn) throws ClientException;
    List<MaterialLot> getMLotByIncomingDocId(String incomingDocId) throws ClientException;

    MaterialLot receiveMLot2Warehouse(Material material, String mLotId, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot stockIn(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    List<MaterialLot> stockIn(List<MaterialLot> materialLots, List<MaterialLotAction> materialLotActionList) throws ClientException;
    MaterialLot stockOut(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot pick(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLotInventory transfer(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLotInventory checkMaterialInventory(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLotInventory recheckMaterialInventory(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;

    MaterialLot changeMaterialLotState(MaterialLot mLot, String eventId, String targetStatus) throws ClientException;
    void validationMergeRule(String ruleName, List<MaterialLot> materialLots) throws ClientException;
    void saveMaterialLotInventory(MaterialLotInventory materialLotInventory, BigDecimal transQty) throws ClientException;

    MaterialLot consumeMLot(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;

    List<MaterialLotInventory> getMaterialLotInv(String mLotRrn) throws ClientException;
    MaterialLotInventory getMaterialLotInv(String mLotRrn, String warehouseRrn, String storageRrn) throws ClientException;

    IqcCheckSheet getIqcSheetByName(String name, boolean throwExceptionFlag) throws ClientException;

    Warehouse getWarehouseByName(String name, boolean throwExceptionFlag) throws ClientException;
    Storage getStorageByWarehouseRrnAndName(Warehouse warehouse, String storageId) throws ClientException;
    //product
    Product saveProduct(Product product) throws ClientException;
    Product saveProduct(Product product, String warehouseName, String iqcSheetName) throws ClientException;
    Product getProductByName(String name) throws ClientException;

    Parts saveParts(Parts parts) throws ClientException;
    Parts saveParts(Parts parts, String warehouseName) throws ClientException;
    Parts getPartsByName(String name, boolean throwExceptionFlag) throws ClientException;

    void validatTargetWarehouse (String materialLotId, Warehouse targetWarehouse) throws ClientException;
    Date calculateTargetDate (Date time, Double duration, String timeUnit) throws ClientException;

    List<Material> getMaterialStockQty(List<Material> materials) throws ClientException;

    Material getMaterialByName(String name, boolean throwExceptionFlag) throws ClientException;

    void printMLot(MaterialLot materialLot, MaterialLotAction materialLotAction, Boolean validationPrintFlag) throws ClientException;
    void printMLot(MaterialLot materialLot) throws ClientException;
    MaterialLot validationPrintAndAddPrintCount(MaterialLot materialLot, MaterialLotAction materialLotAction, Boolean validationPrintFlag)throws ClientException;
}
