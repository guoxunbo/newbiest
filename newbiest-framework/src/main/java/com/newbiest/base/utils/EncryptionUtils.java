package com.newbiest.base.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 加密工具类
 * 		主要用来如果需要的话对加密进行扩展
 * @author guoxunbo
 *
 */
public class EncryptionUtils {

	/**
	 * MD5加密
	 * @param str
	 * @return
	 */
	public static String md5Hex(String str) {
		return DigestUtils.md5Hex(str);
	}

}
