package com.newbiest.mms.rest.rawmaterial;

import com.newbiest.base.msg.Response;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class RawMaterialResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private RawMaterialResponseBody body;
	
}
