package com.newbiest.gc.rest.receive.rma;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guozhangLuo on 2020/12/14.
 */
@Data
public class RMAMLotManagerResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private RMAMLotManagerResponseBody body;
	
}
