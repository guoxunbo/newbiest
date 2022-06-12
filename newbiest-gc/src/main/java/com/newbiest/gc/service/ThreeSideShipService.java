package com.newbiest.gc.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DocumentLine;

import java.util.List;

/**
 * @author luoguozhang
 * @date 5/12/22 10:02 PM
 */
public interface ThreeSideShipService {

    void wltCpMLotSaleShip(DocumentLine documentLine, List<MaterialLotAction> materialLotActions, String checkSubCode) throws ClientException;

    void ftRwMLotSaleShip(List<DocumentLine> documentLines, List<MaterialLotAction> materialLotActions, String ruleId)  throws ClientException;

    void comSaleShip(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions)   throws ClientException;
}
