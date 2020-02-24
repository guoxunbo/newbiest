package com.newbiest.gc.rest.receive.wafer;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class WaferManagerResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private WaferManagerResponseBody body;
	
}
