package com.newbiest.mms.rest.doc.issue.mlot;

import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.service.DocumentService;
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

@RestController
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class IssueMLotController extends AbstractRestController {

    @Autowired
    MmsService mmsService;

    @Autowired
    DocumentService documentService;

    @ApiOperation(value = "发料")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IssueMLotRequest")
    @RequestMapping(value = "/issueMaterial", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public IssueMLotResponse execute(@RequestBody IssueMLotRequest request) throws Exception {
        IssueMLotResponse response = new IssueMLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        IssueMLotResponseBody responseBody = new IssueMLotResponseBody();
        IssueMLotRequestBody requestBody = request.getBody();


        response.setBody(responseBody);
        return response;
    }



}
