package com.newbiest.vanchip.rest.doc.issue.material;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.DocumentLine;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class IssueMLotByDocLineRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private DocumentLine documentLine;

	private List<String> materialLotIdList;

}
