package com.newbiest.gc.rest.validateMLotReserved;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class ValidationMLotReservedResponse extends Response {

    private static final long serialVersionUID = 1L;

    private ValidationMLotReservedResponseBody body;
}
