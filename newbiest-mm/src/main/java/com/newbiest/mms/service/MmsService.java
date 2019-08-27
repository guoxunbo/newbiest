package com.newbiest.mms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotInventory;
import com.newbiest.mms.model.RawMaterial;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by guoxunbo on 2019/2/13.
 */
public interface MmsService {

    // rawMaterial
    RawMaterial saveRawMaterial(RawMaterial rawMaterial) throws ClientException;
    RawMaterial getRawMaterialByName(String name) throws ClientException;

    // MaterialLot
    MaterialLot getMLotByMLotId(String mLotId, boolean throwExceptionFlag) throws ClientException;
    MaterialLot getMLotByMLotId(String mLotId) throws ClientException;
    MaterialLot receiveMLot2Warehouse(RawMaterial rawMaterial, String mLotId, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot stockIn(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot stockOut(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot pick(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot transfer(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot checkMaterialInventory(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot changeMaterialLotState(MaterialLot mLot, String eventId, String targetStatus) throws ClientException;
    void validationMergeRule(String ruleName, List<MaterialLot> materialLots) throws ClientException;
    void saveMaterialLotInventory(MaterialLotInventory materialLotInventory, BigDecimal transQty) throws ClientException;

    MaterialLot holdMaterialLot(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;
    MaterialLot releaseMaterialLot(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;

    MaterialLot consumeMLot(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException;

    List<MaterialLotInventory> getMaterialLotInv(long mLotRrn) throws ClientException;
    MaterialLotInventory getMaterialLotInv(long mLotRrn, long warehouseRrn) throws ClientException;
}
