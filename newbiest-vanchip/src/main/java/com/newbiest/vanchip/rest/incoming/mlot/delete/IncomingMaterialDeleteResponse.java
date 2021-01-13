package com.newbiest.vanchip.rest.incoming.mlot.delete;

import com.newbiest.base.msg.Response;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class IncomingMaterialDeleteResponse extends Response {

    private static final long serialVersionUID = 1L;

    private IncomingMaterialDeleteResponseBody body;

}