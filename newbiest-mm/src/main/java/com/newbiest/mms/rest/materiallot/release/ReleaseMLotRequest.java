package com.newbiest.mms.rest.materiallot.release;

import com.newbiest.base.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "ReleaseMLotRequest")
public class ReleaseMLotRequest extends Request {

    public static final String MESSAGE_NAME = "ReleaseMaterialLot";

    private ReleaseMLotRequestBody body ;

}
