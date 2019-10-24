package com.newbiest.kms.rest.question;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.kms.model.Question;
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

/**
 * Created by guoxunbo on 2018/7/12.
 */
@RestController
@RequestMapping("/kms")
@Slf4j
@Api(value="/kms", tags="KnowledgeManagerSystem")
public class QuestionController extends AbstractRestController {

    @Autowired
    KmsService kmsService;

    @ApiOperation(value = "问题管理")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ChangeShiftRequest")
    @RequestMapping(value = "/questionManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public QuestionResponse execute(@RequestBody QuestionRequest request) throws Exception {
        QuestionResponse response = new QuestionResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        QuestionResponseBody responseBody = new QuestionResponseBody();

        QuestionRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        Question question = requestBody.getQuestion();

        if (QuestionRequest.ACTION_CREATE.equals(actionType)) {
            validationUniqueObjectByTableRrn(requestBody.getTableRrn(), question);
            question = kmsService.saveQuestion(question);
        } else if (QuestionRequest.ACTION_UPDATE.equals(actionType)) {
            validateEntity(question);
            question = kmsService.saveQuestion(question);
        } else if (QuestionRequest.ACTION_CLOSE.equals(actionType)) {
            validateEntity(question);
            question = kmsService.closeQuestion(question);
        }  else if (QuestionRequest.ACTION_WATCHING.equals(actionType)) {
            validateEntity(question);
            question = kmsService.watchQuestion(question);
        }  else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        responseBody.setQuestion(question);
        response.setBody(responseBody);
        return response;
    }

}
