package com.newbiest.vanchip.rest.pack.packCheck;

import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
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

/**
 * 装箱检验
 */
@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="VanChip客制化")
public class PackCheckController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "装箱检验", notes = "装箱检验")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "PackCheckRequest")
    @RequestMapping(value = "/packCheckManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public PackCheckResponse execute(@RequestBody PackCheckRequest request) throws Exception {
        PackCheckResponse response = new PackCheckResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        PackCheckResponseBody responseBody = new PackCheckResponseBody();
        PackCheckRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        if (PackCheckRequest.ACTION_TYPE_PACK_CHECK_PASS.equals(actionType)){

            vanChipService.packCheckPass(requestBody.getMaterialLots());
        }else if (PackCheckRequest.ACTION_TYPE_PACK_CHECK_NG.equals(actionType)){

            vanChipService.packCheckNg(requestBody.getMaterialLotAction());
        }else {
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionType);
        }
        response.setBody(responseBody);
        return response;
    }

}
