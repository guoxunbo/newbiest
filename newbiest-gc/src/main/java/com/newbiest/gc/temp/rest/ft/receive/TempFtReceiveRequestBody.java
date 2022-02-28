package com.newbiest.gc.temp.rest.ft.receive;

import com.newbiest.gc.scm.dto.TempFtVboxModel;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class TempFtReceiveRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "动作类型", example = "Assign/UnAssign")
	private String actionType;

	private List<TempFtVboxModel> tempFtVboxModels;


}
