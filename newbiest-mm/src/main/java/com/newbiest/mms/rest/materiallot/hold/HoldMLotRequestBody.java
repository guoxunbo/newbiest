package com.newbiest.mms.rest.materiallot.hold;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class HoldMLotRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiParam("暂停动作")
    private List<MaterialLotAction> materialLotActions;
}
