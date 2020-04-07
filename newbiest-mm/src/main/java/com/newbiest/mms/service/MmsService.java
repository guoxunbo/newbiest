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

    // rawMaterial
    RawMaterial saveRawMaterial(RawMaterial rawMaterial) throws ClientException;
    RawMaterial getRawMaterialByName(String name) throws ClientException;
    String generatorMLotId(RawMaterial rawMaterial) throws  ClientException;

    // MaterialLot
    MaterialLot getMLotByMLotId(String mLotId, boolean throwExceptionFlag) throws ClientException;
    MaterialLot getMLotByMLotId(String mLotId) throws ClientException;
    MaterialLot getMLotByObjectRrn(long materialLotRrn) throws ClientException;

    StatusModel getMaterialStatusModel(RawMaterial rawMaterial) throws ClientException;
    List<MaterialLot> createMaterialLotList(RawMaterial rawMaterial, List<MaterialLotAction> materialLotImportActions) throws ClientException;
    MaterialLot createMLot(RawMaterial rawMaterial, StatusModel statusModel, String mLotId, String grade, BigDecimal transQty, Map<String, Object> propsMap) throws ClientException;
    MaterialLot receiveMLot2Warehouse(RawMaterial rawMaterial, String mLotId, MaterialLotAction materialLotAction) throws ClientException;
    List<MaterialLot> receiveMLotList2Warehouse(RawMaterial rawMaterial, List<MaterialLotAction> materialLotActions) throws ClientException;

    MaterialLot stockIn(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    List<MaterialLot> stockIn(List<MaterialLot> materialLots, List<MaterialLotAction> materialLotActionList) throws ClientException;
    MaterialLot stockOut(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot pick(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLotInventory transfer(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLotInventory checkMaterialInventory(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot changeMaterialLotState(MaterialLot mLot, String eventId, String targetStatus) throws ClientException;
    void validationMergeRule(String ruleName, List<MaterialLot> materialLots) throws ClientException;
    void saveMaterialLotInventory(MaterialLotInventory materialLotInventory, BigDecimal transQty) throws ClientException;

    MaterialLot holdMaterialLot(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot releaseMaterialLot(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;

    MaterialLot consumeMLot(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;

    List<MaterialLotInventory> getMaterialLotInv(long mLotRrn) throws ClientException;
    MaterialLotInventory getMaterialLotInv(long mLotRrn, long warehouseRrn, long storageRrn) throws ClientException;

    Warehouse getWarehouseByName(String name) throws ClientException;
    public Storage getStorageByWarehouseRrnAndName(Warehouse warehouse, String storageId) throws ClientException;
}
