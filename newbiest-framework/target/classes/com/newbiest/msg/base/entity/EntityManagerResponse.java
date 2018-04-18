package com.newbiest.msg.base.entity;

import com.newbiest.msg.Response;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class EntityManagerResponse extends Response {
	
	private static final long serialVersionUID = 1L;

	private EntityManagerResponseBody body;
	
}
