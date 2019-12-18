package com.newbiest.gc.rest.weight;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class WeightRequest extends Request {
    private static final long serialVersionUID = 1L;

    public static final String MESSAGE_NAME = "GCWeight";

    public static final String ACTION_QUERY = "Query";
    public static final String ACTION_WEIGHT = "Weight";

    private WeightRequestBody body;
}
