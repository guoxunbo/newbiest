package com.newbiest.gc.rest.excelExport;

import com.google.common.collect.Lists;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.ExcelUtils;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/gc")
@Slf4j
public class ExportExcelController {

    @Autowired
    BaseService baseService;

    @Autowired
    UIService uiService;

    @Autowired
    GcService gcService;

    @ApiOperation(value = "excel导出", notes = "export")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "exportExcelRequest")
    @RequestMapping(value = "/gcExport", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public void export(@RequestBody ExportExcelRequest request, HttpServletResponse servletResponse) throws Exception {
        ExportExcelRequestBody requestBody = request.getBody();
        servletResponse.setHeader("content-Type", "application/vnd.ms-excel;charset=utf-8");

        NBTable nbTable = this.uiService.getNBTableByName(request.getBody().getTableName());
        List<MaterialLotUnit> materialLotUnits = gcService.getMaterialLotUnitListByMaterialLotList(requestBody.getMaterialLotList());
        ExcelUtils.exportByTable(nbTable, (Collection)materialLotUnits, request.getHeader().getLanguage(), servletResponse.getOutputStream());
    }
}
