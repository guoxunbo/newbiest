package com.newbiest.mms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.mms.model.MaterialLot;

import java.util.List;

/**
 * @author guoxunbo
 * @date 4/6/21 3:04 PM
 */
public interface PrintService {

    void printWltOrCpLabel(MaterialLot materialLot) throws ClientException;
    void printMaterialLotObliqueBoxLabel(List<MaterialLot> materialLotList, String expressNumber) throws ClientException;

}
