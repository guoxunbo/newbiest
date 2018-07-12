package com.newbiest.security.rest.role;

import com.newbiest.msg.Request;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class RoleRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "RoleManage";

	public static final String DISPATCH_USER = "DispatchUser";
	public static final String DISPATCH_AUTHORITY = "DispatchAuthority";
	public static final String DISPATCH_ALL = "DispatchAll";

	private RoleRequestBody body;

}
