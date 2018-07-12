package com.newbiest.security.rest.user;

import com.newbiest.msg.Response;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class UserResponse extends Response {
	
	private static final long serialVersionUID = 1L;
	
	private UserResponseBody body;
	
}
