package com.newbiest.gc.rest.scm.query;

import com.newbiest.msg.Response;
import lombok.Data;

@Data
public class QueryResponse extends Response {

    private QueryResponseBody body;
}
