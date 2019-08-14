package com.newbiest.kms.rest.questionline;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.kms.model.QuestionLine;
import com.newbiest.kms.service.KmsService;
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

import java.util.List;

/**
 * Created by guoxunbo on 2018/7/12.
 */
@RestController
@RequestMapping("/kms")
@Slf4j
@Api(value="/kms", tags="KnowledgeManagerSystem")
public class QuestionLineController extends AbstractRestController {

    @Autowired
    KmsService kmsService;

    @ApiOperation(value = "问题详情管理")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ChangeShiftRequest")
    @RequestMapping(value = "/questionLineManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public QuestionLineResponse execute(@RequestBody QuestionLineRequest request) throws Exception {
        QuestionLineResponse response = new QuestionLineResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        QuestionLineResponseBody responseBody = new QuestionLineResponseBody();

        QuestionLineRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        QuestionLine questionLine = requestBody.getQuestionLine();

        if (QuestionLineRequest.ACTION_GET_BY_QUESTION_RRN.equals(actionType)) {
            Long questionRrn = requestBody.getQuestionRrn();
            List<QuestionLine> questionLineList = kmsService.getQuestionLineByQuestionRrn(questionRrn);
            responseBody.setQuestionLines(questionLineList);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }

}
