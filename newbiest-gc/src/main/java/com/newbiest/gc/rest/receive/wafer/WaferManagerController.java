package com.newbiest.gc.rest.receive.wafer;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.service.MaterialLotUnitService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.msg.Request;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import liquibase.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by guoxunbo on 2019-08-21 13:15
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class WaferManagerController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "对完成品做操作", notes = "接收")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "WaferManagerRequest")
    @RequestMapping(value = "/waferManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public WaferManagerResponse execute(@RequestBody WaferManagerRequest request) throws Exception {
        WaferManagerResponse response = new WaferManagerResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        WaferManagerResponseBody responseBody = new WaferManagerResponseBody();
        WaferManagerRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        List<DocumentLine> documentLineList = request.getBody().getDocumentLines();

        List<MaterialLotAction> materialLotActions = request.getBody().getMaterialLotActions();
        if (WaferManagerRequest.ACTION_TYPE_RECEIVE.equals(actionType)) {
            gcService.validationAndReceiveWafer(documentLineList, materialLotActions, requestBody.getReceiveWithDoc());
        } else if (WaferManagerRequest.ACTION_TYPE_VALIDATION_ISSUE.equals(actionType)) {
            MaterialLot materialLot = mmsService.getMLotByMLotIdAndBindWorkOrderId(materialLotActions.get(0).getMaterialLotId(), true);
            gcService.validationDocLine(documentLineList, materialLot);
            responseBody.setWorkOrderId(materialLot.getWorkOrderId());
        } else if (WaferManagerRequest.ACTION_TYPE_ISSUE.equals(actionType)) {
            gcService.validationAndWaferIssue(documentLineList, materialLotActions, requestBody.getIssueWithDoc(), requestBody.getUnPlanLot());
        } else if(WaferManagerRequest.ACTION_TYPE_VALIDATION_WAIT_ISSUE.equals(actionType)){
            List<MaterialLot> materialLotList = gcService.validationAndGetWaitIssueWafer(requestBody.getTableRrn(), requestBody.getWhereClause());
            responseBody.setMaterialLotList(materialLotList);
        } else if(WaferManagerRequest.ACTION_TYPE_PURCHASEOUTSOURE_RECEIVE.equals(actionType)){
            gcService.purchaseOutsourceWaferReceive(materialLotActions);
        } else if(WaferManagerRequest.ACTION_TYPE_HK_MLOT_RECEIVE.equals(actionType)){
            gcService.hongKongMLotReceive(materialLotActions);
        } else if(WaferManagerRequest.ACTION_TYPE_COG_MLOT_RECEIVE.equals(actionType)){
            gcService.validateAndReceiveCogMLot(documentLineList, materialLotActions);
        } else if(WaferManagerRequest.ACTION_TYPE_OUTORDER_ISSUE.equals(actionType)){
            gcService.waferOutOrderIssue(materialLotActions);
        } else if (WaferManagerRequest.ACTION_TYPE_MOBILE_GET_WAFER.equals(actionType)){
            MaterialLot materialLot = gcService.mobileValidationAndGetWait(requestBody.getTableRrn(), requestBody.getLotId());
            responseBody.setMaterialLot(materialLot);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + request.getBody().getActionType());
        }
        response.setBody(responseBody);
        return response;
    }
}
