package com.newbiest.vanchip.rest.doc.finishGood.receive;

import com.newbiest.base.msg.RequestBody;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ReceiveFinishGoodRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    public static final String ACTION_TYPE_GET_MATERIALLOT = "GetMaterialLot";
    public static final String ACTION_TYPE_FINISH_GOOD_RECEIVE = "FinishGoodReceive";

    @ApiModelProperty(value = "操作动作")
    private String actionType ;

    @ApiModelProperty(value = "单据号")
    private String documentId;

    @ApiModelProperty(value = "完成品信息")
    private List<String> materialLotIdList;
}
