package com.newbiest.gc.rest.receive.ft;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.gc.model.StockInModel;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.service.MmsService;
import com.newbiest.msg.Request;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by guozhangLuo on 2020-10-12
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class FTMLotManagerController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "FT来料接收、发料", notes = "接收")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "FTMLotManagerRequest")
    @RequestMapping(value = "/ftMaterialLotManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public FTMLotManagerResponse execute(@RequestBody FTMLotManagerRequest request) throws Exception {
        FTMLotManagerResponse response = new FTMLotManagerResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        FTMLotManagerResponseBody responseBody = new FTMLotManagerResponseBody();
        FTMLotManagerRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (FTMLotManagerRequest.ACTION_TYPE_RECEIVE.equals(actionType)) {
            List<MaterialLotUnit> materialLotUnits = requestBody.getMaterialLotUnitList();
            gcService.receiveFTWafer(materialLotUnits);
        } else if(FTMLotManagerRequest.ACTION_TYPE_QUERY.equals(actionType)){
            List<MaterialLotUnit> materialLotUnitList = gcService.queryFTMLotByUnitIdAndTableRrn(requestBody.getUnitId(), requestBody.getTableRrn());
            responseBody.setMaterialLotUnitList(materialLotUnitList);
        } else if(FTMLotManagerRequest.ACTION_TYPE_STOCK_IN.equals(actionType)){
            List<MaterialLotUnit> materialLotUnits = requestBody.getMaterialLotUnitList();
            List<StockInModel> stockInModels = requestBody.getStockInModels();
            gcService.stockInFTWafer(materialLotUnits, stockInModels);
        } else if(FTMLotManagerRequest.ACTION_TYPE_QUERY_WAIT_ISSUE_UNIT.equals(actionType)){
            List<MaterialLotUnit> materialLotUnitList = gcService.queryFTWaitIssueMLotUnitList(requestBody.getTableRrn());
            responseBody.setMaterialLotUnitList(materialLotUnitList);
        } else if(FTMLotManagerRequest.ACTION_TYPE_FT_ISSUE.equals(actionType)){
            List<MaterialLotAction> materialLotActions = requestBody.getMaterialLotActions();
            gcService.validationAndWaferIssue(requestBody.getDocumentLines(), materialLotActions, requestBody.getIssueWithDoc(), requestBody.getUnPlanLot());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + request.getBody().getActionType());
        }
        response.setBody(responseBody);
        return response;
    }
}
