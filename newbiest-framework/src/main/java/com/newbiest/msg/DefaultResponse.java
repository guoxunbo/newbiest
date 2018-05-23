package com.newbiest.msg;

import com.google.common.collect.Lists;
import com.newbiest.base.model.NBMessage;
import com.newbiest.base.utils.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.NONE)
public class DefaultResponse extends Response {
	
	public DefaultResponse(String transactionId) {
		super(transactionId);
	}
	
	public DefaultResponse() {
		
	}

	private Object[] parameters;

	private String transactionId;

	public DefaultResponse buildFailResponse(String errorCode) {
		DefaultResponse response = new DefaultResponse();
		ResponseHeader header = new ResponseHeader();
		header.setResult(ResponseHeader.RESULT_FAIL);
		header.setResultCode(errorCode);
		header.setTransactionId(transactionId);
		NBMessage nbMessage = NBMessage.get(errorCode);
		if (nbMessage != null) {
			String messageZh = nbMessage.getMessageZh();
			String message = nbMessage.getMessage();
			String messageRes = nbMessage.getMessageRes();
			if (parameters != null && parameters.length > 0) {
				messageZh = StringUtils.format(nbMessage.getMessageZh(), parameters);
				message = StringUtils.format(nbMessage.getMessage(), parameters);
				messageRes = StringUtils.format(nbMessage.getMessageRes(), parameters);
			}
			header.setMessageRrn(nbMessage.getObjectRrn());
			header.setResultChinese(messageZh);
			header.setResultEnglish(message);
			header.setResultRes(messageRes);
		}
		if (parameters != null && parameters.length > 0) {
			header.setResultCode(StringUtils.format(errorCode, parameters));
		}
		response.setHeader(header);
		return response;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
}
