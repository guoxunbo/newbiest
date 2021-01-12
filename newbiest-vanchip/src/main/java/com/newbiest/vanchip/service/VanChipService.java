package com.newbiest.vanchip.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.model.MaterialLot;

import java.util.List;

/**
 * @author guoxunbo
 * @date 12/24/20 2:22 PM
 */
public interface VanChipService {

    void importIncomingOrder(String incomingDocId, List<MaterialLot> materialLots) throws ClientException;

    void unbindMesOrder(List<String> materialLotIdList) throws ClientException;
    void bindMesOrder(List<String> materialLotIdList, String workOrderId) throws ClientException;

    void deleteIncomingMaterialLot(List<MaterialLot> materialLotList, String deleteNote);
}
