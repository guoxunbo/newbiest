package com.newbiest.vanchip.rest.doc.issue.mlot;

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

@RestController("VCIssueMLotByDocController")
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip客制化")
public class IssueMLotByDocController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiOperation(value = "发料")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "IssueMLotByDocRequest")
    @RequestMapping(value = "/issueMLotByDoc", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public IssueMLotByDocResponse execute(@RequestBody IssueMLotByDocRequest request) throws Exception {
        IssueMLotByDocResponse response = new IssueMLotByDocResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        IssueMLotByDocResponseBody responseBody = new IssueMLotByDocResponseBody();
        IssueMLotByDocRequestBody requestBody = request.getBody();

        vanChipService.issueMLotByDoc(requestBody.getDocumentId(), requestBody.getMaterialLotIdList());

        response.setBody(responseBody);
        return response;
    }

}
