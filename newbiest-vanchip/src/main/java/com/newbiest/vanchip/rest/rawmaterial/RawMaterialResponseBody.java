package com.newbiest.vanchip.rest.rawmaterial;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.RawMaterial;
import lombok.Data;

@Data
public class RawMaterialResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private RawMaterial material;

}
