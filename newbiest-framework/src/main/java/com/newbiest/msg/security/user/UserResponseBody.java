package com.newbiest.msg.security.user;

import com.newbiest.msg.ResponseBody;
import com.newbiest.security.model.NBUser;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class UserResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private NBUser user;

}
