package com.newbiest.mms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotPackageType;

import java.util.List;

/**
 * Created by guoxunbo on 2019/4/2.
 */
public interface PackageService {

    MaterialLotPackageType getMaterialPackageTypeByName(String name) throws ClientException;
    List<MaterialLot> getPackageDetailLots(Long packagedLotRrn) throws ClientException;

    MaterialLot packageMLots(List<MaterialLotAction> materialLotActions, String packageType) throws ClientException;
    MaterialLot appendPacking(MaterialLot packedMaterialLot, List<MaterialLotAction> materialLotActions) throws ClientException;

    void validationPackageRule(List<MaterialLot> materialLots, MaterialLotPackageType materialLotPackageType) throws ClientException;
    List<MaterialLot> unPack(List<MaterialLotAction> materialLotActions) throws ClientException;

}
