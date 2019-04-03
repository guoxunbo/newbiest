package com.newbiest.mms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotInventory;
import com.newbiest.mms.model.RawMaterial;

/**
 * Created by guoxunbo on 2019/2/13.
 */
public interface MmsService {

    // rawMaterial
    RawMaterial saveRawMaterial(RawMaterial rawMaterial, SessionContext sc) throws ClientException;
    RawMaterial getRawMaterialByName(String name, SessionContext sc) throws ClientException;

    // MaterialLot
    MaterialLot getMLotByMLotId(String mLotId, SessionContext sc) throws ClientException;
    MaterialLot receiveMLot2Warehouse(RawMaterial rawMaterial, String mLotId, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException;
    MaterialLot stockIn(MaterialLot materialLot, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException;
    MaterialLot stockOut(MaterialLot materialLot, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException;
    MaterialLot pick(MaterialLot materialLot, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException;
    MaterialLot transfer(MaterialLot materialLot, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException;
    MaterialLot checkMaterialInventory(MaterialLot materialLot, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException;
    MaterialLot changeMaterialLotState(MaterialLot mLot, String eventId, String targetStatus, SessionContext sc) throws ClientException;

    MaterialLot holdMaterialLot(MaterialLot materialLot, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException;
    MaterialLot releaseMaterialLot(MaterialLot materialLot, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException;

    MaterialLot consumeMLot(MaterialLot materialLot, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException;

    MaterialLotInventory getMaterialLotInv(long mLotRrn, long warehouseRrn) throws ClientException;
}
