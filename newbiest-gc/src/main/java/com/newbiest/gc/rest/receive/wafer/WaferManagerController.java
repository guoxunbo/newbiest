package com.newbiest.gc.rest.receive.wafer;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
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
 * Created by guoxunbo on 2019-08-21 13:15
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class WaferManagerController {

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

        String actionType = request.getBody().getActionType();
        List<DocumentLine> documentLineList = request.getBody().getDocumentLines();

        List<MaterialLotAction> materialLotActions = request.getBody().getMaterialLotActions();
        if (WaferManagerRequest.ACTION_TYPE_RECEIVE.equals(actionType)) {
            gcService.validationAndReceiveWafer(documentLineList, materialLotActions);
        } else if (WaferManagerRequest.ACTION_TYPE_VALIDATION_ISSUE.equals(actionType)) {
            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotActions.get(0).getMaterialLotId(), true);
            gcService.validationDocLine(documentLineList, materialLot);
        } else if (WaferManagerRequest.ACTION_TYPE_ISSUE.equals(actionType)) {
            gcService.validationAndWaferIssue(documentLineList, materialLotActions);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + request.getBody().getActionType());
        }
        return response;
    }
}
