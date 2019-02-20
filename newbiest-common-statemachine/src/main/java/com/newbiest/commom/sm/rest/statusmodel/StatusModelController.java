package com.newbiest.commom.sm.rest.statusmodel;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.commom.sm.exception.StatusMachineExceptions;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.commom.sm.service.StatusMachineService;
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
@RequestMapping("/common/sm")
@Slf4j
@Api(value="/common/sm", tags="StatusMachineService", description = "状态机管理")
public class StatusModelController extends AbstractRestController {

    @Autowired
    StatusMachineService statusMachineService;

    @Autowired
    BaseService baseService;

    @ApiOperation(value = "对物料状态模型做操作", notes = "支持DispatchEvent等")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "StatusModelRequest")
    @RequestMapping(value = "/statusModelManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public StatusModelResponse execute(@RequestBody StatusModelRequest request) throws Exception {
        log(log, request);
        SessionContext sc = getSessionContext(request);

        StatusModelResponse response = new StatusModelResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        StatusModelResponseBody responseBody = new StatusModelResponseBody();

        StatusModelRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        StatusModel requestSm = requestBody.getStatusModel();

        StatusModel statusModel = null;

        if (StatusModelRequest.ACTION_GET_BY_ID.equals(actionType) || StatusModelRequest.ACTION_GET_BY_RRN.equals(actionType)) {
            if (requestSm.getObjectRrn() != null) {
                statusModel = statusMachineService.getStatusModelByObjectRrn(requestSm.getObjectRrn());
            } else if (!StringUtils.isNullOrEmpty(requestSm.getName())) {
                statusModel = statusMachineService.getStatusModelByName(statusModel.getName(), sc);
            }
            if (statusModel == null) {
                throw new ClientParameterException(StatusMachineExceptions.STATUS_MODEL_IS_NOT_EXIST, requestSm.getObjectRrn() != null ? requestSm.getObjectRrn() : requestSm.getName());
            }
            statusModel = (StatusModel) baseService.findEntity(statusModel, true);
        } else if (StatusModelRequest.ACTION_DISPATCH_EVENT.equals(actionType)) {
            statusModel = statusMachineService.getStatusModelByObjectRrn(requestSm.getObjectRrn());
            statusModel.setEvents(requestSm.getEvents());
            statusMachineService.saveStatusModel(statusModel);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        responseBody.setStatusModel(statusModel);
        response.setBody(responseBody);
        return response;
    }

}
