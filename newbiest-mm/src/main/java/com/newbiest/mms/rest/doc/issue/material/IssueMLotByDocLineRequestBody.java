package com.newbiest.mms.rest.doc.issue.material;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.DocumentLine;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("具体请求操作信息")
public class IssueMLotByDocLineRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private String actionType;

	private DocumentLine documentLine;

	private List<String> materialLotIdList;

}
