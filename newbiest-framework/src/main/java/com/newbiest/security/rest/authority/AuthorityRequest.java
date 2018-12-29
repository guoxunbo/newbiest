package com.newbiest.security.rest.authority;

import com.newbiest.msg.Request;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class AuthorityRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "AuthorityManage";

	public static final String GET_AUTHORITY_TREE = "GetAuthorityTree";

	private AuthorityRequestBody body;

}
