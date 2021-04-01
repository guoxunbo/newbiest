package com.newbiest.mms.rest.materiallot.hold;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "ReleaseMLotRequest")
public class HoldMLotRequest extends Request {

    public static final String MESSAGE_NAME = "HoldMaterialLot";

    private HoldMLotRequestBody body ;

}
