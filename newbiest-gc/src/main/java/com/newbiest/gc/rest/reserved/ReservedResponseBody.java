package com.newbiest.gc.rest.reserved;

import com.newbiest.base.ui.model.NBTable;
import com.newbiest.gc.model.GCProductNumberRelation;
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

	@ApiModelProperty(example = "包装规格列表")
	private List<GCProductNumberRelation> boxPackedQtyList;

	@ApiModelProperty(example = "默认包装规格")
	private GCProductNumberRelation defaultPackedRule;

}
