package com.newbiest.mms.rest.partsMaterial;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guoZhang Luo on 2019/9/3.
 */
@Data
public class PartsMaterialResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private PartsMaterialResponseBody body;
	
}
