package com.newbiest.main;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 系统框架上的一些属性定义
 * Created by guoxunbo on 2017/10/8.
 */
@Component
@ConfigurationProperties(prefix = "newbiest")
public class NewbiestConfiguration implements Serializable {

    public static final String PASSWORD_POLICY_FIXED = "fixed";
    public static final String PASSWORD_POLICY_RANDOM = "random";
    private static final long serialVersionUID = -7694217619466586668L;

    /**
     * 密码错误次数
     */
    private static int pwdWrongCount;

    /**
     * 密码生成策略
     */
    private static String pwdPolicy = PASSWORD_POLICY_FIXED;

    /**
     * 第一次登录是否需要修改密码
     */
    private static String firstLoginChangePwd = "N";

    /**
     * 查询最大数量
     */
    private static int queryMaxCount;

    /**
     * 密码有效期
     */
    private static long pwdLife;

    public static int getPwdWrongCount() {
        return pwdWrongCount;
    }

    public static void setPwdWrongCount(int pwdWrongCount) {
        NewbiestConfiguration.pwdWrongCount = pwdWrongCount;
    }

    public static String getPwdPolicy() {
        return pwdPolicy;
    }

    public static void setPwdPolicy(String pwdPolicy) {
        NewbiestConfiguration.pwdPolicy = pwdPolicy;
    }

    public static Boolean getFirstLoginChangePwd() {
        return "Y".equalsIgnoreCase(firstLoginChangePwd);
    }

    public static void setFirstLoginChangePwd(String firstLoginChangePwd) {
        NewbiestConfiguration.firstLoginChangePwd = firstLoginChangePwd;
    }

    public static int getQueryMaxCount() {
        return queryMaxCount;
    }

    public static void setQueryMaxCount(int queryMaxCount) {
        NewbiestConfiguration.queryMaxCount = queryMaxCount;
    }

    public static long getPwdLife() {
        return pwdLife;
    }

    public static void setPwdLife(long pwdLife) {
        NewbiestConfiguration.pwdLife = pwdLife;
    }
}
