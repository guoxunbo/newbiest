package com.newbiest.calendar.rest.changeshift;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.rest.entity.EntityRequest;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.calendar.model.ChangeShift;
import com.newbiest.calendar.service.DmsService;
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

/**
 * Created by guoxunbo on 2018/7/12.
 */
@RestController
@RequestMapping("/dms")
@Slf4j
@Api(value="/dms", tags="DailyManagerSystem", description = "日常管理")
public class ChangeShiftController extends AbstractRestController {

    @Autowired
    DmsService dmsService;

    @ApiOperation(value = "对交接班做操作", notes = "对交接班定义做处理")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ChangeShiftRequest")
    @RequestMapping(value = "/changeShiftManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ChangeShiftResponse execute(@RequestBody ChangeShiftRequest request) throws Exception {
        log(log, request);
        SessionContext sc = getSessionContext(request);

        ChangeShiftResponse response = new ChangeShiftResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ChangeShiftResponseBody responseBody = new ChangeShiftResponseBody();

        ChangeShiftRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        ChangeShift changeShift = requestBody.getChangeShift();

        if (ChangeShiftRequest.ACTION_CREATE.equals(actionType)) {
            changeShift = dmsService.saveChangeShift(changeShift, sc);
        } else if (ChangeShiftRequest.ACTION_UPDATE.equals(actionType)) {
            validateEntity(changeShift);
            changeShift = (ChangeShift) saveEntity(changeShift, sc);
        } else if (ChangeShiftRequest.ACTION_CLOSE.equals(actionType)) {
            validateEntity(changeShift);
            changeShift = dmsService.closeChangeShift(changeShift, sc);
        } else if (ChangeShiftRequest.ACTION_OPEN.equals(actionType)) {
            validateEntity(changeShift);
            changeShift = dmsService.openChangeShift(changeShift, sc);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        responseBody.setChangeShift(changeShift);
        response.setBody(responseBody);
        return response;
    }

}
