package com.newbiest.gc.rest.nbQuery;

import com.newbiest.gc.service.GcService;
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
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class NbQueryController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "NbQuerySearch", notes = "NBQuery查询")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "AssignRequest")
    @RequestMapping(value = "/nbQueryManger", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public NbQueryResponse execute(@RequestBody NbQueryRequest request) throws Exception {
        NbQueryResponse response = new NbQueryResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        NbQueryResponseBody responseBody = new NbQueryResponseBody();
        NbQueryRequestBody requestBody = request.getBody();
        String queryText = requestBody.getQueryText();
        Map<String, Object> paramMap = requestBody.getParamMap();
        int firstResult = requestBody.getFirstResult();
        int maxResult = requestBody.getMaxResult();
        String whereClause = requestBody.getWhereClause();
        String orderByClause = requestBody.getOrderByClause();

        List<Map> queryList = gcService.findEntityMapListByQueryText(queryText, paramMap, firstResult, maxResult, whereClause, orderByClause);

        responseBody.setMapList(queryList);
        response.setBody(responseBody);
        return response;
    }
}
