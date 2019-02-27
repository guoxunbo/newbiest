package com.newbiest.mms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.mms.model.MaterialLot;
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
}
