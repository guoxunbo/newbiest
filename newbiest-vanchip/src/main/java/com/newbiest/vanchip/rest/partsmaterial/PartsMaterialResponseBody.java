package com.newbiest.vanchip.rest.partsmaterial;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.Parts;
import lombok.Data;
import java.util.List;

@Data
public class PartsMaterialResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private Parts parts;

	private List<Parts> dataList;
}
