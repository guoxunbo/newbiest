package com.newbiest.rtm.rest;

import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.base.ui.rest.table.TableRequest;
import com.newbiest.base.threadlocal.SessionContext;
import com.newbiest.msg.DefaultParser;
import com.newbiest.rtm.analyse.AnalyseContext;
import com.newbiest.rtm.model.AnalyseResult;
import com.newbiest.rtm.service.RtmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by guoxunbo on 2019/5/27.
 */
@RestController
@RequestMapping("/rtm")
@Slf4j
@Api(value="/rtm", tags="RealTimeManager", description = "RealTimeManager")
public class AnalyseController extends AbstractRestController{

    @Autowired
    RtmService rtmService;

    @ApiOperation(value = "分析文件", notes = "分析设备产生的文件，如bin结果文件")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "AnalyseRequest")
    @RequestMapping(value = "/analyseFile", method = RequestMethod.POST)
    public AnalyseResponse execute(@RequestParam MultipartFile file, @RequestParam String request) throws Exception {
        AnalyseRequest tableRequest = DefaultParser.getObjectMapper().readerFor(AnalyseRequest.class).readValue(request);
        AnalyseResponse response = new AnalyseResponse();
        response.getHeader().setTransactionId(tableRequest.getHeader().getTransactionId());
        AnalyseResponseBody responseBody = new AnalyseResponseBody();

        AnalyseContext analyseContext = new AnalyseContext();
        analyseContext.setInputStream(file.getInputStream());
        analyseContext.setFileName(file.getOriginalFilename());

        rtmService.analyseFile(analyseContext);
        return response;
    }

}
