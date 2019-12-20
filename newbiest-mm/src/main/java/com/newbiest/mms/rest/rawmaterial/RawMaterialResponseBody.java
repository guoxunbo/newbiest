package com.newbiest.mms.rest.rawmaterial;

import com.newbiest.mms.model.RawMaterial;
import com.newbiest.base.msg.ResponseBody;
import com.newbiest.security.model.NBUser;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class RawMaterialResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private RawMaterial material;

}
