package com.newbiest.mms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by guoxunbo on 2019/2/13.
 */
public interface MmsService {

    void validateFutureHoldByReceiveTypeAndProductAreaAndLotId(String receiveType, String productArea, String lotId) throws ClientException;
    void validateFutureHoldByWaferId(String waferId, MaterialLot materialLot) throws ClientException;

    // rawMaterial
    Material createRawMaterial(RawMaterial rawMaterial) throws ClientException;
    RawMaterial saveRawMaterial(RawMaterial rawMaterial) throws ClientException;
    RawMaterial getRawMaterialByName(String name) throws ClientException;
    String generatorMLotId(Material material) throws  ClientException;

    //product
    Product saveProduct(Product product) throws ClientException;
    Product getProductByName(String name) throws ClientException;
    MaterialNameInfo saveMaterialName(String materialName) throws ClientException;

    Parts saveParts(Parts parts) throws ClientException;
    Parts getPartsByName(String name) throws ClientException;

    // MaterialLot
    MaterialLot getMLotByMLotId(String mLotId, boolean throwExceptionFlag) throws ClientException;
    MaterialLot getMLotByMLotId(String mLotId) throws ClientException;
    MaterialLot getMLotByObjectRrn(long materialLotRrn) throws ClientException;
    MaterialLot getMLotByMLotIdAndBindWorkOrderId(String mLotId, boolean throwExceptionFlag) throws ClientException;

    StatusModel getMaterialStatusModel(Material material) throws ClientException;
    List<MaterialLot> createMaterialLotList(RawMaterial rawMaterial, List<MaterialLotAction> materialLotImportActions) throws ClientException;
    MaterialLot createMLot(Material material, StatusModel statusModel, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot receiveMLot2Warehouse(Material material, String mLotId, MaterialLotAction materialLotAction) throws ClientException;
    List<MaterialLot> receiveMLotList2Warehouse(Material material, List<MaterialLotAction> materialLotActions) throws ClientException;

    MaterialLot stockIn(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    List<MaterialLot> stockIn(List<MaterialLot> materialLots, List<MaterialLotAction> materialLotActionList) throws ClientException;
    MaterialLot stockOut(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot pick(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLotInventory transfer(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLotInventory checkMaterialInventory(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot changeMaterialLotState(MaterialLot mLot, String eventId, String targetStatus) throws ClientException;
    void validationMergeRule(String ruleName, List<MaterialLot> materialLots) throws ClientException;
    void saveMaterialLotInventory(MaterialLotInventory materialLotInventory, BigDecimal transQty) throws ClientException;

    boolean validationMLotByMergeRule(String ruleName, List<MaterialLot> materialLots) throws ClientException;

    MaterialLot holdMaterialLot(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot releaseMaterialLot(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;

    MaterialLot consumeMLot(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;

    List<MaterialLotInventory> getMaterialLotInv(long mLotRrn) throws ClientException;
    MaterialLotInventory getMaterialLotInv(long mLotRrn, long warehouseRrn, long storageRrn) throws ClientException;

    Warehouse getWarehouseByName(String name) throws ClientException;
    Storage getStorageByWarehouseRrnAndName(Warehouse warehouse, String storageId) throws ClientException;

    void stockInMaterialLotUnitAndSaveHis(MaterialLot materialLot, String transType)throws ClientException;
}
