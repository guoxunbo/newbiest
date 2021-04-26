package com.newbiest.vanchip.rest.doc.issue.labmlot;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class IssueLabMLotOrderResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "物料批次")
	private List<MaterialLot> materialLots;
}
