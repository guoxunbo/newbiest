package com.newbiest.gc.rest.weight;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class WeightResponse extends Response {

    private static final long serialVersionUID = 1L;

    private WeightResponseBody body;

}
