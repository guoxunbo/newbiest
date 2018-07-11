package com.newbiest.msg.security.user;

import com.newbiest.msg.Request;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
@ApiModel
public class UserRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public static final String MESSAGE_NAME = "UserManage";

	public static final String ACTION_CHANGE_PASSWORD = "ChangePassword";
	public static final String ACTION_RESET_PASSWORD = "RestPassword";
	public static final String ACTION_GET_AUTHORITY = "GetAuthority";
	public static final String ACTION_LOGIN = "Login";
	public static final String ACTION_REGISTER = "Register";

	private UserRequestBody body;

}
