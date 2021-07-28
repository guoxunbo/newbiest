package com.newbiest.gc.rest.receive.ft;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guozhangLuo on 2020/10/12.
 */
@Data
public class FTMLotManagerResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private FTMLotManagerResponseBody body;
	
}
