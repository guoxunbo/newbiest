package com.newbiest.security.rest.authority;

import com.newbiest.msg.ResponseBody;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.model.NBRole;
import lombok.Data;

import java.util.List;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class AuthorityResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;
	
	private List<NBAuthority> authorities;

}
