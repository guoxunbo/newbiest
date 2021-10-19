package com.newbiest.vanchip.rest.sync.incoming;

import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.vanchip.service.ErpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip客制化", description = "同步来料/退料信息")
public class SyncIncomingOrReturnController extends AbstractRestController {

    @Autowired
    ErpService erpService;

    @ApiImplicitParam(name="request", value="request", required = true, dataType = "UploadFileRequest")
    @RequestMapping(value = "/syncERPMaterialLot", method = RequestMethod.POST)
    public SyncIncomingOrReturnResponse excute(@RequestBody SyncIncomingOrReturnRequest request)throws Exception {
        SyncIncomingOrReturnResponse response = new SyncIncomingOrReturnResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        SyncIncomingOrReturnResponseBody responseBody = new SyncIncomingOrReturnResponseBody();
        SyncIncomingOrReturnRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();
        if (SyncIncomingOrReturnRequest.ACTION_SYNC_INCOMING_OR_RETURN.equals(actionType)){
            erpService.asyncIncomingOrReturn();
        }else if (SyncIncomingOrReturnRequest.ACTION_SYNC_MAIN_MLOT_INCOMING_OR_RETURN.equals(actionType)){
            erpService.asyncDeliveryInfo();
        }else {
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionType);
        }

        response.setBody(responseBody);
        return response;
    }
}
