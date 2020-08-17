package com.newbiest.gc.rest.wltcp.print.parameter;

import com.newbiest.gc.model.MesPackedLot;
import com.newbiest.msg.RequestBody;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;


@Data
@ApiModel("具体请求操作信息")
public class PrintWltCpLotRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private String lotId;

}
