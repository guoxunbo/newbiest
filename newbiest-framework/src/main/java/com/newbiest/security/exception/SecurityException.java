package com.newbiest.security.exception;

import java.io.Serializable;

/**
 * Security模块相对应的错误信息
 * Created by guoxunbo on 2018/6/1.
 */
public class SecurityException implements Serializable  {
    private static final long serialVersionUID = 1669139089066410768L;

    public static final String SECURITY_USER_IS_NOT_EXIST = "security.user_is_not_exist";
    public static final String SECURITY_USER_IS_NOT_IN_VALIDATION = "security.user_is_not_in_validation";
    public static final String SECURITY_USER_IS_NOT_IN_ORG = "security.user_is_not_in_org";

    public static final String SECURITY_WRONG_PWD_MORE_THAN_COUNT = "security.wrong_pwd_more_than_count";
    public static final String SECURITY_USER_PASSWORD_IS_INCORRECT = "security.user_pwd_is_incorrect";
    public static final String SECURITY_PASSWORD_IS_EXPIRY = "security.pwd_expiry";

    public static final String SECURITY_ROLE_IS_NOT_EXIST = "security.role_is_not_exist";


}
