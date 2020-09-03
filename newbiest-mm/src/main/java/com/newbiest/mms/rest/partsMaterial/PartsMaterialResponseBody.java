package com.newbiest.mms.rest.partsMaterial;

import com.newbiest.mms.model.Parts;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

/**
 * Created by guoZhang Luo on 2019/9/3.
 */
@Data
public class PartsMaterialResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private Parts parts;

}
