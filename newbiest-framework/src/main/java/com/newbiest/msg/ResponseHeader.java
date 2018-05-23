package com.newbiest.msg;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
@NoArgsConstructor
public class ResponseHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String RESULT_SUCCESS = "SUCCESS";
	public static final String RESULT_FAIL = "FAIL";

	/**
	 * 当返回错误时候消息没找到时，返回此值，代表要去NBMessage中将此值配上
	 */
	public static final Long MESSAGE_NOT_FOUND_RRN = 0L;

	private String transactionId;

	private String result = RESULT_SUCCESS;

	private String resultCode;

	private String resultChinese;

	private String resultEnglish;

	private String resultRes;

	/**
	 * 返回messageRrn. 相当于数字的一个错误码
	 */
	private Long messageRrn = MESSAGE_NOT_FOUND_RRN;
}
