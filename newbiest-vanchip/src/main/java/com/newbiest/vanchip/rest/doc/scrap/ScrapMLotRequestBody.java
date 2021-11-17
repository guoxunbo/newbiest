package com.newbiest.vanchip.rest.doc.scrap;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("具体请求操作信息")
public class ScrapMLotRequestBody extends RequestBody {
	
	private static final long serialVersionUID = 1L;

	private String actionType;

	private String documentId;

	private List<MaterialLot> materialLotList;

}