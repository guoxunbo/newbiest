package com.newbiest.gc.rest.receive.fg;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.model.MesPackedLot;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PrintService;
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2019-08-21 13:15
 */
@RestController
@RequestMapping("/gc")
@Slf4j
@Api(value="/gc", tags="gc客制化接口", description = "GalaxyCore客制化接口")
public class FinishGoodController {

    @Autowired
    GcService gcService;

    @Autowired
    MmsService mmsService;

    @Autowired
    PrintService printService;

    @ApiOperation(value = "对完成品做操作", notes = "接收")
    @ApiImplicitParam(name="request", value="request", required = true, dataType = "FinishGoodRequest")
    @RequestMapping(value = "/finishGoodManage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public FinishGoodResponse execute(@RequestBody FinishGoodRequest request) throws Exception {
        FinishGoodResponse response = new FinishGoodResponse();
        response.getHeader().setTransactionId(request.getHeader().getTransactionId());

        FinishGoodResponseBody responseBody = new FinishGoodResponseBody();
        FinishGoodRequestBody requestBody = request.getBody();

        if(FinishGoodRequest.ACTION_COM_RECEIVE.equals(requestBody.getActionType())){
            gcService.receiveFinishGood(requestBody.getMesPackedLots(), StringUtils.EMPTY);
        } else if(FinishGoodRequest.ACTION_WLT_RECEIVE.equals(requestBody.getActionType())){
            List<MesPackedLot> mesPackedLots = gcService.receiveWltFinishGood(requestBody.getMesPackedLots(), requestBody.getPrintLabel(), requestBody.getPrintCount());

            if(!StringUtils.isNullOrEmpty(requestBody.getPrintLabel())){
                mesPackedLots = mesPackedLots.stream().sorted(Comparator.comparing(MesPackedLot::getScanSeq)).collect(Collectors.toList());
                List<MaterialLot> materialLots = Lists.newArrayList();
                for(MesPackedLot mesPackedLot : mesPackedLots){
                    MaterialLot materialLot = mmsService.getMLotByMLotId(mesPackedLot.getBoxId(), true);
                    materialLots.add(materialLot);
                }
                List<Map<String, Object>> mapList = printService.printReceiveWltCpLotLabel(materialLots, requestBody.getPrintCount());
                responseBody.settingClientPrint(mapList);
            }
        } else if(FinishGoodRequest.ACTION_COB_RECEIVE.equals(requestBody.getActionType())){
            gcService.receiveCOBFinishGood(requestBody.getMesPackedLots());
        } else if(FinishGoodRequest.ACTION_LS_GRADE_QUERY.equals(requestBody.getActionType())){
            List<MesPackedLot> mesPackedLots = gcService.queryLSGradeMesPakedLotListByTableRrn(requestBody.getTableRrn());
            responseBody.setPackedLotList(mesPackedLots);
        } else if(FinishGoodRequest.ACTION_LS_GRADE_RECEIVE.equals(requestBody.getActionType())){
            gcService.receiveFinishGood(requestBody.getMesPackedLots(), "LSGrade");
        } else {
            throw new ClientException(Request.NON_SUPPORT_ACTION_TYPE + requestBody.getActionType());
        }

        response.setBody(responseBody);
        return response;
    }
}
