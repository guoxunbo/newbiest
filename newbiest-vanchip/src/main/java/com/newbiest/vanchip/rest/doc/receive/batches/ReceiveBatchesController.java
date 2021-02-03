package com.newbiest.vanchip.rest.doc.receive.batches;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.MaterialLot;
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

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="MaterialManagerSystem", description = "物料管理相关")
public class ReceiveBatchesController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "根据单据，接收前进行分批")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ReceiveBatchesRequest")
    @RequestMapping(value = "/receiveBatches", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ReceiveBatchesResponse execute(@RequestBody ReceiveBatchesRequest request) throws Exception {
        ReceiveBatchesResponse response = new ReceiveBatchesResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ReceiveBatchesResponseBody responseBody = new ReceiveBatchesResponseBody();

        ReceiveBatchesRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        if(ReceiveBatchesRequest.ACTION_TYPE_BATCHES.equals(actionType)){
            List<MaterialLot> materialLotList = requestBody.getMaterialLotList();
            String materialLotId = materialLotList.get(0).getMaterialLotId();

            MaterialLot materialLot = vanChipService.receiveBatches(materialLotId, requestBody.getBatchesQty());

            List<MaterialLot> materialLots = Lists.newArrayList();
            materialLots.add(materialLot);
            responseBody.setMaterialLotList(materialLots);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }
        response.setBody(responseBody);
        return response;
    }

}
