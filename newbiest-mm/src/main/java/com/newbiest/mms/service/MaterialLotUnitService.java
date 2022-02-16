package com.newbiest.mms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;

import java.util.List;

/**
 * 晶圆相关操作
 * Created by guoxunbo on 2020-01-17 12:39
 */
public interface MaterialLotUnitService {

    List<MaterialLotUnit> createFTMLot(List<MaterialLotUnit> materialLotUnits) throws ClientException;

    List<MaterialLotUnit> queryStockOutTagMLotUnits(List<MaterialLotAction> materialLotActions)throws ClientException;

    List<MaterialLotUnit> getUnitsByMaterialLotId(String materialLotId) throws ClientException;

    List<MaterialLotUnit> createMLot(List<MaterialLotUnit> materialLotUnitList) throws ClientException;

    void receiveMLotWithUnit(MaterialLot materialLots, String warehouseName) throws ClientException;

    void validateAndCreateMLotUnit(List<MaterialLotUnit> materialLotUnitList) throws ClientException;

    List<MaterialLotUnit> getMaterialLotUnitByFabLotAndWaferId(List<MaterialLotUnit> materialLotUnitList, String importType) throws ClientException;
}
