package com.newbiest.gc.rest.vboxHoldSet;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class VboxHoldSetResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private VboxHoldSetResponseBody body;
	
}
