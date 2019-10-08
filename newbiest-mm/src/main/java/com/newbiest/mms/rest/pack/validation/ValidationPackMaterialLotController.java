package com.newbiest.mms.rest.pack.validation;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotPackageType;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.msg.Request;
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
 * 验证物料批次包装规则
 *  当前追加包装只验证合批规则，其他都不验证。
 */
@RestController
@RequestMapping("/mms")
@Slf4j
@Api(value="/mms", tags="MaterialManagerSystem", description = "物料管理相关")
public class ValidationPackMaterialLotController extends AbstractRestController {

    @Autowired
    PackageService packageService;

    @Autowired
    MmsService mmsService;

    @ApiOperation(value = "ValidationPackRule", notes = "验证包装规则")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "ValidationPackMaterialLotRequest")
    @RequestMapping(value = "/validationPackMaterialLots", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ValidationPackMaterialLotResponse execute(@RequestBody ValidationPackMaterialLotRequest request) throws Exception {
        ValidationPackMaterialLotResponse response = new ValidationPackMaterialLotResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        ValidationPackMaterialLotResponseBody responseBody = new ValidationPackMaterialLotResponseBody();

        ValidationPackMaterialLotRequestBody requestBody = request.getBody();
        String actionType = requestBody.getActionType();
        String packageType = requestBody.getPackageType();

        List<MaterialLot> waitToPackLots = requestBody.getWaitToPackMaterialLots();
        if (ValidationPackMaterialLotRequest.ACTION_VALIDATION_PACK.equals(actionType)) {
            packageService.validationPackageRule(waitToPackLots, packageType);
        } else if (ValidationPackMaterialLotRequest.ACTION_VALIDATION_APPEND.equals(actionType)) {
            MaterialLot packagedMaterialLot = mmsService.getMLotByMLotId(requestBody.getPackagedMaterialLotId());
            // 当前追加包装只验证合批规则，其他都不验证。
            MaterialLotPackageType materialLotPackageType = packageService.getMaterialPackageTypeByName(packagedMaterialLot.getPackageType());
            if (!StringUtils.isNullOrEmpty(materialLotPackageType.getMergeRule())) {
                mmsService.validationMergeRule(materialLotPackageType.getMergeRule(), waitToPackLots);
            }
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }
        response.setBody(responseBody);
        return response;
    }

}
