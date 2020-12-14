package com.newbiest.gc.rest.unConfirmWaferSet;

import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.gc.model.GcUnConfirmWaferSet;
import com.newbiest.gc.service.GcService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by guozhangLuo
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "未确认晶圆追踪设置")
public class UnConfirmWaferSetController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "unConfirmWaferSet", notes = "未确认晶圆追踪设置")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "UnConfirmWaferSetRequest")
    @RequestMapping(value = "/unConfirmWaferTrackSet", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public UnConfirmWaferSetResponse execute(@RequestBody UnConfirmWaferSetRequest request) throws Exception {
        UnConfirmWaferSetResponse response = new UnConfirmWaferSetResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        UnConfirmWaferSetResponseBody responseBody = new UnConfirmWaferSetResponseBody();

        UnConfirmWaferSetRequestBody requestBody = request.getBody();
        GcUnConfirmWaferSet unConfirmWaferSet = requestBody.getUnConfirmWaferSet();

        unConfirmWaferSet = gcService.saveUnConfirmWaferTrackSetInfo(unConfirmWaferSet, requestBody.getActionType());

        responseBody.setUnConfirmWaferSet(unConfirmWaferSet);
        response.setBody(responseBody);
        return response;
    }

}
