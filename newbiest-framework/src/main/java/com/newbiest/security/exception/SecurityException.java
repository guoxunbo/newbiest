package com.newbiest.security.exception;

import java.io.Serializable;

/**
 * Security模块相对应的错误信息
 * Created by guoxunbo on 2018/6/1.
 */
public class SecurityException implements Serializable  {
    private static final long serialVersionUID = 1669139089066410768L;

    public static final String COMMON_USER_IS_NOT_FOUND = "common.user_is_not_found";

}
