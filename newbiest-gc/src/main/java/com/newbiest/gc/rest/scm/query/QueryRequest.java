package com.newbiest.gc.rest.scm.query;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class QueryRequest extends Request {

    public static final String MESSAGE_NAME = "ScmLotQuery";

    public static final String ACTION_TYPE_QUERY = "Query";

    private QueryRequestBody body;
}
