package com.newbiest.main;

import com.newbiest.base.utils.StringUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 系统框架上的一些属性定义
 * Created by guoxunbo on 2017/10/8.
 */
@Component
@ConfigurationProperties(prefix = "newbiest")
@Data
public class NewbiestConfiguration implements Serializable {

    public static final String PASSWORD_POLICY_FIXED = "fixed";
    public static final String PASSWORD_POLICY_RANDOM = "random";
    private static final long serialVersionUID = -7694217619466586668L;

    /**
     * 密码错误次数
     */
    private int pwdWrongCount;

    /**
     * 密码生成策略
     */
    private String pwdPolicy = PASSWORD_POLICY_FIXED;

    /**
     * 第一次登录是否需要修改密码
     */
    private String firstLoginChangePwd = StringUtils.NO;

    /**
     * 查询最大数量
     */
    private int queryMaxCount;

    /**
     * 密码有效期
     */
    private long pwdLife;

    public Boolean getFirstLoginChangePwd() {
        return StringUtils.YES.equalsIgnoreCase(firstLoginChangePwd);
    }


}
