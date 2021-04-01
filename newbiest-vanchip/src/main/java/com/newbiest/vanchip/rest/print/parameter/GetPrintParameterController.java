package com.newbiest.vanchip.rest.print.parameter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientParameterException;
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
import java.util.Map;

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="vc客制化接口", description = "VanChip客制化接口")
public class GetPrintParameterController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "获取标签参数")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "GetPrintParameterRequest")
    @RequestMapping(value = "/printParameterManager", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public GetPrintParameterResponse execute(@RequestBody GetPrintParameterRequest request) throws Exception {
        GetPrintParameterResponse response = new GetPrintParameterResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        GetPrintParameterResponseBody responseBody = new GetPrintParameterResponseBody();
        GetPrintParameterRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        Map<String, Object> parameterMap = Maps.newHashMap();
        List<Map<String, Object>> parameterList = Lists.newArrayList();

        if (GetPrintParameterRequest.ACTION_BOX.equals(actionType)){

            parameterMap = vanChipService.getBoxPrintParameter(requestBody.getMaterialLotId());
            responseBody.setParameterMap(parameterMap);
        }else if(GetPrintParameterRequest.ACTION_COC.equals(actionType)){

            parameterMap = vanChipService.getCOCPrintParameter(requestBody.getDocumentLineId());
            responseBody.setParameterMap(parameterMap);
        }else if (GetPrintParameterRequest.ACTION_PACKING_LIST.equals(actionType)){
            parameterMap = vanChipService.getPackingListPrintParameter(requestBody.getDocumentLineId());
            parameterList = vanChipService.getPackingListMLotParameter(requestBody.getDocumentLineId());

            responseBody.setParameterList(parameterList);
            responseBody.setParameterMap(parameterMap);
        }else if (GetPrintParameterRequest.ACTION_SHIPPING_LIST.equals(actionType)){

            parameterMap = vanChipService.getShippingListPrintParameter(requestBody.getDocumentLineId());
            responseBody.setParameterMap(parameterMap);
        }else if(GetPrintParameterRequest.ACTION_SHIPPING_LIST_MLOT.equals(actionType)){

            parameterList = vanChipService.getShippingListPrintMLotParameter(requestBody.getDocumentLineId());
            responseBody.setParameterList(parameterList);
        }else if (GetPrintParameterRequest.ACTION_PKLIST.equals(actionType)){

            parameterMap = vanChipService.getPKListParameter(requestBody.getDocumentLineId());
            parameterList = vanChipService.getPKListMLotParameter(requestBody.getDocumentLineId());
            responseBody.setParameterList(parameterList);
            responseBody.setParameterMap(parameterMap);
        }else if (GetPrintParameterRequest.ACTION_ISSUE_ORDER.equals(actionType)){
            List<MaterialLot> materialLots = vanChipService.getIssueOrderMLotParameter(requestBody.getDocumentId());
            responseBody.setMaterialLots(materialLots);
        }else{
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionType);
        }


        response.setBody(responseBody);
        return response;
    }

}
