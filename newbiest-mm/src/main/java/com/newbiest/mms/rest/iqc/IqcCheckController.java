package com.newbiest.mms.rest.iqc;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.dto.MaterialLotJudgeAction;
import com.newbiest.mms.model.MLotCheckSheetLine;
import com.newbiest.mms.service.MmsService;
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
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "IQC检查相关")
public class IqcCheckController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "iqc检查")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IqcCheckRequest")
    @RequestMapping(value = "/IqcCheck", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public IqcCheckResponse execute(@RequestBody IqcCheckRequest request) throws Exception {
        IqcCheckResponse response = new IqcCheckResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        IqcCheckResponseBody responseBody = new IqcCheckResponseBody();

        IqcCheckRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        String checkSheetRrn = requestBody.getCheckSheetRrn();
        MaterialLotJudgeAction materialLotJudgeAction = requestBody.getMaterialLotJudgeAction();

        if (IqcCheckRequest.ACTION_GET_MLOT_CHECK_SHEET_LINE.equals(actionType)) {
            List<MLotCheckSheetLine> mLotCheckSheetLines = mmsService.getMLotCheckSheetLinesByMLotCheckSheetRrn(checkSheetRrn);
            responseBody.setMLotCheckSheetLines(mLotCheckSheetLines);
        } else if (IqcCheckRequest.ACTION_IQC_CHECK.equals(actionType)) {
            mmsService.iqc(materialLotJudgeAction);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }


}
