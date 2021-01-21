package com.newbiest.mms.rest.doc.issue.material;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.util.List;

@Data
public class IssueMLotByDocLineResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	List<MaterialLot> materialLotList ;
}
