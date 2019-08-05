package com.newbiest.mms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;

import java.util.List;

/**
 * Created by guoxunbo on 2019/4/2.
 */
public interface PackageService {

    MaterialLot packageMLots(List<MaterialLotAction> materialLotActions, String packageType, SessionContext sc) throws ClientException;
    MaterialLot additionalPacking(MaterialLot packedMaterialLot, List<MaterialLotAction> materialLotActions, SessionContext sc) throws ClientException;
    void unPack(MaterialLotAction materialLotAction, SessionContext sc) throws ClientException;

}
