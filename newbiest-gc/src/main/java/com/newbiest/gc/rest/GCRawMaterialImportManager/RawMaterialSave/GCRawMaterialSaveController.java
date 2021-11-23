package com.newbiest.gc.rest.GCRawMaterialImportManager.RawMaterialSave;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.Request;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gc")
@Slf4j
public class GCRawMaterialSaveController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "原材料处理", notes = "rawMaterialManger")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GCRawMaterialImportRequest")
    @RequestMapping(value = "/RawMaterialSave", method = RequestMethod.POST)
    public GCRawMaterialSaveResponse excute(@RequestBody GCRawMaterialSaveRequest request)throws Exception {
        GCRawMaterialSaveRequestBody requestBody = request.getBody();
        GCRawMaterialSaveResponse response = new GCRawMaterialSaveResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GCRawMaterialSaveResponseBody responseBody = new GCRawMaterialSaveResponseBody();

        String actionType = requestBody.getActionType();
        List<MaterialLot> materialLotList = requestBody.getMaterialLotList();
        if(GCRawMaterialSaveRequest.ACTION_TYPE_CREATE.equals(actionType)){
            String  importCode =gcService.importRawMaterialLotList(materialLotList,requestBody.getImportType());
            responseBody.setImportCode(importCode);
        } else if(GCRawMaterialSaveRequest.ACTION_TYPE_RECEIVE.equals(actionType)){
            gcService.receiveRawMaterial(materialLotList);
        } else if(GCRawMaterialSaveRequest.ACTION_TYPE_ISSUE.equals(actionType)) {
            gcService.validateAndRawMaterialIssue(requestBody.getDocumentLineList() ,materialLotList, requestBody.getIssueWithDoc());
        } else if(GCRawMaterialSaveRequest.ACTION_TYPE_SCRAP.equals(actionType)){
            gcService.scrapRawMaterial(materialLotList, requestBody.getReason(), requestBody.getRemarks());
        } else if(GCRawMaterialSaveRequest.ACTION_TYPE_DELETE.equals(actionType)){
            gcService.deleteMaterialLotAndSaveHis(materialLotList, requestBody.getRemarks());
        } else if(GCRawMaterialSaveRequest.ACTION_TYPE_QUERY_SPARE_MLOT.equals(actionType)){
            List<MaterialLot> materialLots = gcService.getWaitSpareRawMaterialLotListByOrderAndTableRrn(requestBody.getDocLineRrn(), requestBody.getTableRrn());
            responseBody.setMaterialLotList(materialLots);
        } else if(GCRawMaterialSaveRequest.ACTION_TYPE_GET_SPARE_RAW_MLOT.equals(actionType)){
            List<MaterialLot> materialLots = gcService.getSpareRawMaterialLotListByDocLineRrrn(materialLotList, requestBody.getDocLineRrn());
            responseBody.setMaterialLotList(materialLots);
        } else if(GCRawMaterialSaveRequest.ACTION_TYPE_GET_SPARE_RWA_OUT_DOC.equals(actionType)){
            List<MaterialLot> materialLots = gcService.getWaitSpareRawMaterialByReservedQty(materialLotList, requestBody.getPickQty());
            responseBody.setMaterialLotList(materialLots);
        } else if(GCRawMaterialSaveRequest.ACTION_TYPE_SPARE_RAW_OUT_DOC.equals(actionType)){
            String spareRuleId = gcService.spareRawMLotOutDoc(materialLotList);
            responseBody.setSpareCode(spareRuleId);
        } else if(GCRawMaterialSaveRequest.ACTION_TYPE_SPARE_RAW_MLOT.equals(actionType)){
            gcService.rawMaterialMLotSpare(materialLotList, requestBody.getDocLineRrn());
        } else if(GCRawMaterialSaveRequest.ACTION_TYPE_QUERY_ISSUE_RAW_MATERIAL_LOT.equals(actionType)){
            List<MaterialLot> materialLots = gcService.queryIssueRawMaterialByMaterialLotIdOrLotIdAndTableRrn(requestBody.getQueryLotId(), requestBody.getTableRrn());
            responseBody.setMaterialLotList(materialLots);
        } else if(GCRawMaterialSaveRequest.ACTION_TYPE_SCRAP_RAW_MATERIAL_SHIP.equals(actionType)){
            gcService.scrapRawMaterialShip(requestBody.getDocumentLine(), requestBody.getMaterialLotList());
        } else if(GCRawMaterialSaveRequest.ACTION_TYPE_GC_UN_RAW_MATERIAL_SPARE.equals(actionType)){
            gcService.unRawMaterialSpare(requestBody.getMaterialLotList());
        } else if (GCRawMaterialSaveRequest.ACTION_TYPE_MOBILE_ISSUE.equals(actionType)){
            gcService.mobileValidateAndRawMaterialIssue(materialLotList, requestBody.getErpTime(), GCRawMaterialSaveRequest.ISSUE_WITH_DOC);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }
}
