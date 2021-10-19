package com.newbiest.vanchip.rest.upload;

import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.factory.FileStrategyFactory;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.msg.DefaultParser;
import com.newbiest.base.msg.Request;
import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.service.FileStrategyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/vc")
@Slf4j
@Api(value="/vc", tags="Vanchip客制化", description = "文件上传")
public class UploadFileController extends AbstractRestController {

    @ApiImplicitParam(name="request", value="request", required = true, dataType = "UploadFileRequest")
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public UploadFileResponse excute(@RequestParam MultipartFile file, @RequestParam String request)throws Exception {
        UploadFileRequest uploadFilereQuest = DefaultParser.getObjectMapper().readerFor(UploadFileRequest.class).readValue(request);
        UploadFileRequestBody requestBody = uploadFilereQuest.getBody();
        UploadFileResponse response = new UploadFileResponse();

        response.getHeader().setTransactionId(uploadFilereQuest.getHeader().getTransactionId());
        UploadFileResponseBody responseBody = new UploadFileResponseBody();

        String fileName = file.getOriginalFilename();
        InputStream fileInputStream = file.getInputStream();

        String actionType = requestBody.getActionType();
        if (UploadFileRequest.ACTION_UPLOAD_FILE.equals(actionType)){
            String modelClass = requestBody.getModelClass();

            FileStrategyService fileStrategyService = FileStrategyFactory.getFileStrategy(modelClass);
            fileStrategyService.uploadFile((NBBase) getClass(modelClass).newInstance(), null, fileName, fileInputStream);
        } else {
            throw new ClientParameterException(Request.NON_SUPPORT_ACTION_TYPE, actionType);
        }

        response.setBody(responseBody);
        return response;
    }
}
