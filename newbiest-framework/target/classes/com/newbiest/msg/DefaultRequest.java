package com.newbiest.msg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by guoxunbo on 2017/9/29.
 */
public class DefaultRequest extends Request {

	@Override
	public RequestBody getBody() {
		return null;
	}

}
