package com.newbiest.base.exception;


import com.newbiest.base.utils.StringUtils;

/**
 * 带有参数的异常
 * Created by guoxunbo on 2017/9/29.
 */
public class ClientParameterException extends ClientException {
	 
	private static final long serialVersionUID = 829907884555472415L;

	public static final String PARAMETER_PLACEHOLDER = "%s";
	public static final String PARAMETER_SPLIT_CODE = "#";

	private Object[] parameters;
	
	public ClientParameterException() {
	}

	public ClientParameterException(String errorCode) {
		this.setErrorCode(errorCode);
	}

	public ClientParameterException(String errorCode, Object ... parameters) {
		String code = errorCode;
		// 如果已经有了#s的话，则不进行再次拼接
		if (!errorCode.contains(PARAMETER_PLACEHOLDER)) {
			for (Object obj : parameters) {
				code += PARAMETER_SPLIT_CODE + PARAMETER_PLACEHOLDER + ",";
			}
		}
		this.setErrorCode(StringUtils.format(code, parameters));
		this.setParameters(parameters);
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public Object[] getParameters() {
		return parameters;
	}
}
