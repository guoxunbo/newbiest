package com.newbiest.base.ui.rest.table;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.ExcelUtils;
import com.newbiest.base.utils.SessionContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

        NBTable nbTable = null;
        if (TableRequest.ACTION_GET_BY_AUTHORITY.equals(actionType)) {
            nbTable = uiService.getNBTableByAuthority(requestBody.getAuthorityRrn());
        } else if (TableRequest.ACTION_GET_BY_RRN.equals(actionType)) {
            nbTable = uiService.getDeepNBTable(requestBody.getTable().getObjectRrn());
        } else if (TableRequest.ACTION_GET_DATA.equals(actionType)) {
            if (requestBody.getTable().getObjectRrn() != null) {
                nbTable = uiService.getDeepNBTable(requestBody.getTable().getObjectRrn());
            } else {
                nbTable = uiService.getNBTableByName(requestBody.getTable().getName(), sc.getOrgRrn());
                nbTable = uiService.getDeepNBTable(nbTable.getObjectRrn());
            }
            List<? extends NBBase> dataList = uiService.getDataFromTableRrn(nbTable.getObjectRrn(), requestBody.getTable().getWhereClause(), requestBody.getTable().getOrderBy(), sc);
            responseBody.setDataList(dataList);
        } else if (TableRequest.ACTION_GET_BY_NAME.equals(actionType)) {
            nbTable = uiService.getNBTableByName(requestBody.getTable().getName(), sc.getOrgRrn());
            nbTable = uiService.getDeepNBTable(nbTable.getObjectRrn());
        }
        responseBody.setTable(nbTable);
        response.setBody(responseBody);
        return response;
    }

    @ApiOperation(value = "导出模板", notes = "根据table上栏位的exportFlag来导出模板")
    @ApiImplicitParam(name="tableRrn", value="tableRrn", required = true, dataType = "Long")
    @RequestMapping(value = "/expTemplate", method = RequestMethod.POST)
    public void execute(@RequestBody TableRequest request, HttpServletResponse servletResponse) throws Exception {
        NBTable nbTable = uiService.getDeepNBTable(request.getBody().getTable().getObjectRrn());
        servletResponse.setHeader("content-Type", "application/vnd.ms-excel;charset=utf-8");
        ExcelUtils.exportTemplateByTable(nbTable, request.getHeader().getLanguage(), servletResponse.getOutputStream());
    }

}
