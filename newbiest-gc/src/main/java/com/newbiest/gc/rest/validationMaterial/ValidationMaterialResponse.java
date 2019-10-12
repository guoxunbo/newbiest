package com.newbiest.gc.rest.validationMaterial;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class ValidationMaterialResponse extends Response {

    private static final long serialVersionUID = 1L;

    private ValidationMaterialResponseBody body;
}
