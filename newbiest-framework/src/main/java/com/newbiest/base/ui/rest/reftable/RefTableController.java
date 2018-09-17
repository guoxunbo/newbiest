package com.newbiest.base.ui.rest.reftable;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.ui.model.NBReferenceTable;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.msg.Request;
import com.newbiest.security.model.NBOrg;
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
 * 对adTable的相关操作
 * Created by guoxunbo on 2018/7/26.
 */
@RestController
@RequestMapping("/ui")
@Slf4j
@Api(value="/ui", tags="UIService", description = "动态页面生成，比如Table,栏位，参考值, 参考表等的定义")
public class RefTableController extends AbstractRestController {

    @Autowired
    private UIService uiService;

    @ApiOperation(value = "对RefTable做操作", notes = "GetData")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "RefTableRequest")
    @RequestMapping(value = "/refTableManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public RefTableResponse execute(@RequestBody RefTableRequest request) throws Exception {
        log(log, request);
        SessionContext sc = getSessionContext(request);

        RefTableResponse response = new RefTableResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        RefTableResponseBody responseBody = new RefTableResponseBody();

        RefTableRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        NBReferenceTable referenceTable = requestBody.getReferenceTable();

        if (RefTableRequest.GET_DATA.equals(actionType)) {
            referenceTable = uiService.getReferenceTableByName(referenceTable.getName(), sc);
            List<? extends NBBase> dataList = uiService.getDataFromTableRrn(referenceTable.getTableRrn(), referenceTable.getWhereClause(), referenceTable.getOrderBy(), sc);
            responseBody.setDataList(dataList);
            responseBody.setReferenceTable(referenceTable);
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }

    @Override
    protected SessionContext getSessionContext(Request request) throws ClientException {
        RefTableRequestBody requestBody = (RefTableRequestBody) request.getBody();
        if (NBReferenceTable.REFERENCE_NAME_ORG.equals(requestBody.getReferenceTable().getName())) {
            SessionContext sc = new SessionContext();
            sc.setOrgRrn(NBOrg.GLOBAL_ORG_RRN);
            sc.setUsername(request.getHeader().getUsername());
            return sc;
        }
        return super.getSessionContext(request);
    }
}
