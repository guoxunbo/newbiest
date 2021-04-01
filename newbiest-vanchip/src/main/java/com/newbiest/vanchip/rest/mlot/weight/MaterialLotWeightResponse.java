package com.newbiest.vanchip.rest.mlot.weight;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class MaterialLotWeightResponse extends Response {

    private static final long serialVersionUID = 1L;

    private MaterialLotWeightResponseBody body;
}
