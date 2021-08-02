package com.newbiest.vanchip.rest.doc.query;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.Document;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.util.List;

@Data
public class QueryOrderResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private Document document;
	private List<MaterialLot> materialLotList;
}
