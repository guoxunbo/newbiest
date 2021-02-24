package com.newbiest.vanchip.rest.mlot.release;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLotHold;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class ReleaseMLotRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    @ApiParam("待释放的MaterialLotHold")
    private List<MaterialLotHold> materialLotHolds;

    @ApiParam("释放动作")
    private MaterialLotAction materialLotAction;
}
