package com.newbiest.security.rest.role;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class RoleResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private RoleResponseBody body;

}
