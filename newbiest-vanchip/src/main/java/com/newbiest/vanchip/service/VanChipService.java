package com.newbiest.vanchip.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;

import java.util.List;
import java.util.Map;

/**
 * @author guoxunbo
 * @date 12/24/20 2:22 PM
 */
public interface VanChipService {

    void importIncomingOrder(String incomingDocId, List<MaterialLot> materialLots) throws ClientException;

    void unbindMesOrder(List<String> materialLotIdList) throws ClientException;
    void bindMesOrder(List<String> materialLotIdList, String workOrderId) throws ClientException;

    void deleteIncomingMaterialLot(List<MaterialLot> materialLotList, String deleteNote) throws ClientException;

    void issueMLotByDoc(String documentId, List<String> materialLotIdList) throws ClientException;
    void issueMLotByDocLine(DocumentLine documentLine, List<String> materialLotIdList) throws ClientException;

    void returnMLotByDoc(String documentId, List<String> materialLotIdList) throws ClientException;

    MaterialLot validationDocLineAndMaterialLot(DocumentLine documentLine, List<String> materialLotIdList) throws ClientException;

    List<MaterialLot> getReservedMaterialLot(DocumentLine documentLine);

    void reservedMaterialLot(DocumentLine documentLine, List<MaterialLotAction> materialLotActionList) throws ClientException;

    void unReservedMaterialLot(List<MaterialLotAction> materialLotActionList) throws ClientException;

    List<MaterialLot> printReservedOrder(DocumentLine documentLine) throws ClientException;

    List<MaterialLot> stockIn(List<MaterialLot> materialLots, List<MaterialLotAction> materialLotActionList) throws ClientException;
}
