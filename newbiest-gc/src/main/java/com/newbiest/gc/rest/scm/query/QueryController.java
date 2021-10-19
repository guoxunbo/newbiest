package com.newbiest.gc.rest.scm.query;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.service.ScmService;
import com.newbiest.msg.Request;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.service.SecurityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
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
@Api(value = "/gc", tags = "gc客制化接口", description = "GalaxyCore客制化接口")
public class QueryController {

    @Autowired
    ScmService scmService;

    @Autowired
    SecurityService securityService;

    @ApiImplicitParam(name = "request", value = "request", required = true, dataType = "QueryRequest")
    @RequestMapping(value = "/scmLotQuery", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public QueryResponse execute(@RequestBody QueryRequest request) throws Exception {
        QueryResponse response = new QueryResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        QueryResponseBody responseBody = new QueryResponseBody();
        QueryRequestBody requestBody = request.getBody();

        String username = request.getHeader().getUsername();
        NBUser nbUser = securityService.getUserByUsername(username);
        if (nbUser == null) {
            nbUser = new NBUser();
            nbUser.setUsername(username);
            nbUser.setDescription(username);
            securityService.saveUser(nbUser);
        }

        log.info("lotIdList is " + requestBody.getLotIdList());
        String actionType = requestBody.getActionType();
        if (QueryRequest.ACTION_TYPE_QUERY.equals(actionType)) {
            List<Map<String, String>> materialLotList = scmService.scmLotQuery(requestBody.getLotIdList());
            responseBody.setMaterialLotList(materialLotList);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + actionType);
        }

        response.setBody(responseBody);
        return response;
    }
}
