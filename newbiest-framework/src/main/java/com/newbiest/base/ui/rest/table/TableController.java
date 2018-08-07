package com.newbiest.base.ui.rest.table;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.SessionContext;
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
public class TableController extends AbstractRestController {

    @Autowired
    private UIService uiService;

    @ApiOperation(value = "对Table做操作", notes = "GetByAuthority, GetData")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "TableRequest")
    @RequestMapping(value = "/tableManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public TableResponse execute(@RequestBody TableRequest request) throws Exception {
        log(log, request);
        SessionContext sc = getSessionContext(request);

        TableResponse response = new TableResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        TableResponseBody responseBody = new TableResponseBody();

        TableRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();

        if (TableRequest.GET_BY_AUTHORITY.equals(actionType)) {
            NBTable nbTable = uiService.getNBTableByAuthority(requestBody.getAuthorityRrn());
            responseBody.setTable(nbTable);
        } else if (TableRequest.ACTION_GET_BY_RRN.equals(actionType)) {
            NBTable nbTable = uiService.getNBTable(requestBody.getTable().getObjectRrn());
            responseBody.setTable(nbTable);
        } else if (TableRequest.GET_DATA.equals(actionType)) {
            List<? extends NBBase> dataList = uiService.getDataFromTableRrn(requestBody.getTable().getObjectRrn(), sc);
            responseBody.setDataList(dataList);
        }
        response.setBody(responseBody);
        return response;
    }


}
