package com.newbiest.base.exception;


/**
 * 带有参数的异常
 * Created by guoxunbo on 2017/9/29.
 */
public class ClientParameterException extends ClientException {
	 
	private static final long serialVersionUID = 829907884555472415L;

	public static final String PARAMETER_PLACEHOLDER = "%s";

	private Object[] parameters;
	
	public ClientParameterException() {
	}

	public ClientParameterException(String errorCode) {
		this.setErrorCode(errorCode);
	}

	public ClientParameterException(String errorCode, Object ... parameters) {
		this.setErrorCode(errorCode);
		this.setParameters(parameters);
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public Object[] getParameters() {
		return parameters;
	}
}
