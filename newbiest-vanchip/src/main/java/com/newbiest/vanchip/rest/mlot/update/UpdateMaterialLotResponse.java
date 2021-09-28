package com.newbiest.vanchip.rest.mlot.update;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class UpdateMaterialLotResponse extends Response {

    private static final long serialVersionUID = 1L;

    private UpdateMaterialLotResponseBody body;
}
