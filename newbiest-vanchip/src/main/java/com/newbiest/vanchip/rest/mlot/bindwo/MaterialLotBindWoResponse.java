package com.newbiest.vanchip.rest.mlot.bindwo;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class MaterialLotBindWoResponse extends Response {

    private static final long serialVersionUID = 1L;

    private MaterialLotBindWoResponseBody body;
}
