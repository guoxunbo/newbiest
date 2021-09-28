package com.newbiest.vanchip.rest.sync.incoming;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SyncIncomingOrReturnRequest extends Request {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_NAME = "SyncIncomingOrReturn";

    @ApiModelProperty(value = "同步辅材来料/退料")
    public static final String ACTION_SYNC_INCOMING_OR_RETURN = "SyncIncomingOrReturn";

    @ApiModelProperty(value = "同步主材来料/退料")
    public static final String ACTION_SYNC_MAIN_MLOT_INCOMING_OR_RETURN = "SyncMainMLotIncomingOrReturn";

    private SyncIncomingOrReturnRequestBody body;
}
