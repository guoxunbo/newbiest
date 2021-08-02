package com.newbiest.vanchip.rest.doc.finishGood.receive;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.service.DocumentService;
import com.newbiest.mms.state.model.MaterialStatus;
import com.newbiest.vanchip.service.VanChipService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="MaterialManagerSystem", description = "成品相关")
public class ReceiveFinishGoodController {

    @Autowired
    VanChipService vanChipService;

    @Autowired
    DocumentService documentService;

    @ApiOperation(value = "对完成品做操作", notes = "接收")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ReceiveFinishGoodRequest")
    @RequestMapping(value = "/receiveFinishGood", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ReceiveFinishGoodResponse execute(@RequestBody ReceiveFinishGoodRequest request) throws Exception {
        ReceiveFinishGoodResponse response = new ReceiveFinishGoodResponse();
        ReceiveFinishGoodResponseBody responseBody = new ReceiveFinishGoodResponseBody();
        ReceiveFinishGoodRequestBody requestBody = request.getBody();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        String actionType = requestBody.getActionType();

        if (ReceiveFinishGoodRequestBody.ACTION_TYPE_GET_MATERIALLOT.equals(actionType)){
            List<MaterialLot> materialLots = documentService.getReservedMLotByDocId(requestBody.getDocumentId());
            materialLots = materialLots.stream().filter(mLot-> MaterialStatus.STATUS_CREATE.equals(mLot.getStatus())).collect(Collectors.toList());
            responseBody.setMaterialLotList(materialLots);
        }else if (ReceiveFinishGoodRequestBody.ACTION_TYPE_FINISH_GOOD_RECEIVE.equals(actionType)){
            vanChipService.receiveFinishGood(requestBody.getDocumentId(), requestBody.getMaterialLotIdList());
        }else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }

        response.setBody(responseBody);
        return response;
    }
}
