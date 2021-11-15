package com.newbiest.gc.rest.scm.engManager;

import com.newbiest.gc.model.GCScmToMesEngInform;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel("具体请求操作信息")
public class EngManagerRequestBody extends RequestBody {

    private String actionType;

    private List<GCScmToMesEngInform> lotEngInfoList;

}
