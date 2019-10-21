package com.newbiest.gc.rest.validationDocumentLine;

import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.DocumentLine;
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
 * 验证出货规则。
 *  扫描的箱信息需要验证二级代码、产品等信息是否相同
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class ValidationDocumentLineController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "ValidationDocumentLine", notes = "验证箱信息能匹配的订单")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ValidationDocumentLineRequest")
    @RequestMapping(value = "/validationDocumentLine", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ValidationDocumentLineResponse execute(@RequestBody ValidationDocumentLineRequest request) throws Exception {
        ValidationDocumentLineResponse response = new ValidationDocumentLineResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        ValidationDocumentLineResponseBody responseBody = new ValidationDocumentLineResponseBody();
        ValidationDocumentLineRequestBody requestBody = request.getBody();

        List<DocumentLine> documentLineList = gcService.validationAndGetDocumentLineList(requestBody.getDocumentLines(), requestBody.getMaterialLot());
        if(CollectionUtils.isNotEmpty(documentLineList)){
            responseBody.setDocumentLineList(documentLineList);
        }
        response.setBody(responseBody);
        return response;
    }
}
