package com.newbiest.vanchip.rest.nbquery;

import com.newbiest.base.model.NBQuery;
import com.newbiest.base.service.BaseService;
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

import javax.persistence.Query;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="vc客制化接口", description = "VanChip客制化接口")
public class NbQueryController {

    @Autowired
    VanChipService vanChipService;
    @Autowired
    BaseService baseService;
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
        String queryName = requestBody.getQueryName();

        NBQuery nbQuery = vanChipService.findNBQueryByName(queryName, true);
        List<Map> queryList = vanChipService.findEntityMapListByQueryText(nbQuery.getQueryText(), paramMap, firstResult, maxResult, whereClause, orderByClause);
        Query query = vanChipService.findEntityByQueryText(nbQuery.getQueryText(), whereClause, orderByClause);

//        List<Map> entityMapListByQueryName = baseService.findEntityMapListByQueryName(queryName, paramMap, firstResult, maxResult, whereClause, orderByClause);
//
        Integer totalCount = query.getResultList().size();
        responseBody.setTotalCount(totalCount);
        responseBody.setMapList(queryList);
        response.setBody(responseBody);
        return response;
    }
}
