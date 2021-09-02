package com.newbiest.gc.rest.productSubcodeSet;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.factory.ModelFactory;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.utils.ExcelUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.model.GCProductSubcode;
import com.newbiest.gc.service.GcService;
import com.newbiest.msg.DefaultParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by guozhangLuo
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "产品二级代码设定")
public class ProductSubcodeSetController extends AbstractRestController {

    @Autowired
    GcService gcService;

    @ApiOperation(value = "productSubcodeSetting", notes = "产品二级代码设定")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MaterialLotImportSearchRequest")
    @RequestMapping(value = "/productSubcodeSet", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ProductSubcodeSetResponse execute(@RequestBody ProductSubcodeSetRequest request) throws Exception {
        ProductSubcodeSetResponse response = new ProductSubcodeSetResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ProductSubcodeSetResponseBody responseBody = new ProductSubcodeSetResponseBody();

        ProductSubcodeSetRequestBody requestBody = request.getBody();
        GCProductSubcode productSubcode = requestBody.getProductSubcode();

        productSubcode = gcService.saveProductSubcode(productSubcode);

        responseBody.setProductSubcode(productSubcode);
        response.setBody(responseBody);
        return response;
    }

    @ApiImplicitParam(name="request", value="request", required = true, dataType = "MaterialLotImportSearchRequest")
    @RequestMapping(value = "/productSubcodeImport", method = RequestMethod.POST)
    public ProductSubcodeSetResponse excute(@RequestParam MultipartFile file, @RequestParam String request) throws Exception {
        ProductSubcodeSetRequest productSubcodeSetRequest = DefaultParser.getObjectMapper().readerFor(ProductSubcodeSetRequest.class).readValue(request);
        ProductSubcodeSetRequestBody requestBody = productSubcodeSetRequest.getBody();
        ProductSubcodeSetResponse response = new ProductSubcodeSetResponse();
        response.getHeader().setTransactionId(productSubcodeSetRequest.getHeader().getTransactionId());
        ProductSubcodeSetResponseBody responseBody = new ProductSubcodeSetResponseBody();

        NBTable nbTable = uiService.getDeepNBTable(requestBody.getTable().getObjectRrn());
        ClassLoader classLoader = ModelFactory.getModelClassLoader(nbTable.getModelClass());
        if (classLoader == null) {
            throw new ClientParameterException(NewbiestException.COMMON_MODEL_CLASS_LOADER_IS_NOT_EXIST, nbTable.getModelClass());
        }

        BiMap<String, String> fieldMap = HashBiMap.create(ExcelUtils.buildHeaderByTable(nbTable, productSubcodeSetRequest.getHeader().getLanguage()));
        fieldMap = fieldMap.inverse();
        List<GCProductSubcode> productSubcodeList = (List) ExcelUtils.importExcel(classLoader.loadClass(nbTable.getModelClass()), fieldMap, file.getInputStream(), StringUtils.EMPTY);
        gcService.importProductSubCode(productSubcodeList);

        response.setBody(responseBody);
        return response;
    }

}
