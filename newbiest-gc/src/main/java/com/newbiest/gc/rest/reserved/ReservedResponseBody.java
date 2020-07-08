package com.newbiest.gc.rest.reserved;

import com.newbiest.base.ui.model.NBTable;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ReservedResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<MaterialLot> materialLotList;

	@ApiModelProperty(example = "动态表")
	private NBTable table;
}
