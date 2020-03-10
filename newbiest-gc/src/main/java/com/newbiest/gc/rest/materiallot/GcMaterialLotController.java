package com.newbiest.gc.rest.materiallot;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.ui.model.NBOwnerReferenceList;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.base.msg.Request;
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
 * GlaxyCore 对物料批次的批量操作客制化
 * 中转箱，备货等等
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class GcMaterialLotController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "对物料批做操作", notes = "接收。消耗。hold/release等")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GcMaterialLotRequest")
    @RequestMapping(value = "/materialLotManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GcMaterialLotResponse execute(@RequestBody GcMaterialLotRequest request) throws Exception {
        GcMaterialLotResponse response = new GcMaterialLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GcMaterialLotResponseBody responseBody = new GcMaterialLotResponseBody();

        GcMaterialLotRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        List<MaterialLot> materialLots = requestBody.getMaterialLots();

        if (GcMaterialLotRequest.ACTION_BIND_RELAY_BOX.equals(actionType)) {
            gcService.bindRelaxBox(materialLots, requestBody.getRelayBoxId());
        } else if (GcMaterialLotRequest.ACTION_UNBIND_RELAY_BOX.equals(actionType)) {
            gcService.unbindRelaxBox(materialLots);
        } else if (GcMaterialLotRequest.ACTION_GET_PACK_CASE_CHECK_LIST.equals(actionType)) {
            List<NBOwnerReferenceList> judgePackCaseItemList = gcService.getJudgePackCaseCheckList();
            responseBody.setJudgePackCaseItemList(judgePackCaseItemList);
        } else if (GcMaterialLotRequest.ACTION_JUDGE_PACKED_LOT.equals(actionType)) {
            gcService.judgePackedMaterialLot(materialLots, requestBody.getCheckList());
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }

}
