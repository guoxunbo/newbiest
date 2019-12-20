package com.newbiest.mms.rest.unpack;

import com.newbiest.mms.model.MaterialLot;
import com.newbiest.base.msg.ResponseBody;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UnPackMaterialLotResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "被拆包的主批次")
	private List<MaterialLot> materialLots;
}
