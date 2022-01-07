package com.newbiest.gc.rest.scm.query;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel("具体请求操作信息")
public class QueryRequestBody extends RequestBody {

    private String actionType;

    private List<Map<String, String>> lotIdList;
}
