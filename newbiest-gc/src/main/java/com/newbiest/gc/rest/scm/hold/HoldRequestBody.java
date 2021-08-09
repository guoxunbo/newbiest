package com.newbiest.gc.rest.scm.hold;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class HoldRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private String actionType;

	private List<MaterialLot> materialLotList;

	private String actionCode;

	private String actionReason;

	private String actionRemarks;

}
