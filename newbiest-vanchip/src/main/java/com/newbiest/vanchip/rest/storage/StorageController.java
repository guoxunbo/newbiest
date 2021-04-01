package com.newbiest.vanchip.rest.storage;

import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.mms.model.Storage;
import com.newbiest.vanchip.service.VanChipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip客制化", description = "库位管理")
public class StorageController extends AbstractRestController {

    @Autowired
    VanChipService vanChipService;

    @ApiImplicitParam(name="request", value="request", required = true, dataType = "StorageRequest")
    @RequestMapping(value = "/storageManager", method = RequestMethod.POST)
    public StorageResponse excute(@RequestBody StorageRequest request)throws Exception {
        StorageResponse response = new StorageResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());
        StorageResponseBody responseBody = new StorageResponseBody();
        StorageRequestBody requestBody = request.getBody();

        String actionType = requestBody.getActionType();

        if (StorageRequest.ACTION_SAVE_STORAGE_INFO.equals(actionType)){
            Storage storage = requestBody.getStorage();
            storage = vanChipService.saveStorageInfo(storage);
            responseBody.setStorage(storage);
        }else {
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionType);
        }

        response.setBody(responseBody);
        return response;
    }

}
