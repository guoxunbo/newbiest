package com.newbiest.vanchip.rest.IncomingMatLotManager.Receive;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IncomingMaterialImportReceiveRequest extends Request {

    public static final String MESSAGE_NAME = "VCIncomingMaterialReceiveManager";

    public static final String ACTION_TYPE_RECEIVE = "Receive";

    public static final String ACTION_TYPE_GET_MATERIAL_LOT = "GetMaterialLot";

    private IncomingMaterialImportReceiveRequestBody body;
}
