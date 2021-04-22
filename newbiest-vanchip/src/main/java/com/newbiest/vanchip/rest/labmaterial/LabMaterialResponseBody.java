package com.newbiest.vanchip.rest.labmaterial;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.LabMaterial;
import lombok.Data;

@Data
public class LabMaterialResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private LabMaterial material;

}
