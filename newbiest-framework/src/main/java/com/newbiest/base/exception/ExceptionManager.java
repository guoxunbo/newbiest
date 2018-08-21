package com.newbiest.base.exception;

import org.slf4j.Logger;

import javax.persistence.OptimisticLockException;

/**
 * 防止ClientException多层包装。所有抛出异常类都使用此方法
 * Created by guoxunbo on 2017/9/16.
 */
public class ExceptionManager {

	public static ClientException handleException(Exception e) {
		if (e instanceof OptimisticLockException) {
			return new ClientException(NewbiestException.COMMON_OPTIMISTIC_LOCK);
		} else if (e instanceof ClientParameterException) {
			return (ClientParameterException)e;
		} else if (e instanceof ClientException) {
			return (ClientException)e;
		} else {
			return new ClientException(e);
		}
	}

	public static ClientException handleException(Exception e, Logger logger) {
		logger.error(e.getMessage(), e);
		return handleException(e);
	}


}
