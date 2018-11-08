package com.newbiest.main;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.ImmutableMap;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by guoxunbo on 2018/5/25.
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "newbiest.token")
@PropertySource(value = "classpath:newbiest.yml", factory = YmlPropertyLoaderFactory.class)
@Data
public class JwtSigner implements Serializable{

    private static final long serialVersionUID = 4278757044452416702L;

    /**
     * 生成token的时候的加密字符串
     */
    private String secretString;

    /**
     * 发行者
     */
    private String issuer;

    /**
     * 受发者
     */
    private String audience;

    /**
     * token过期时间
     */
    private Integer expire;

    /**
     * 白名单列表 即可以不登录就可以直接访问系统 一般用于和第三方通讯
     */
    private List<String> whiteIPList;

    /**
     * 对URL进行过滤不验证是否登录
     */
    private List<String> whiteUrlList;

    /**
     * token过期时间单位 支持SECONDS, MINUTES, HOURS, DAYS, WEEKS, MONTHS, YEARS
     * @return
     */
    private String timeUnit = ChronoUnit.HOURS.name();

    private Map<String, Object> buildHeader() {
        // 使用HS256加密
        return ImmutableMap.of("alg", "HS256");
    }

    /**
     * 加密
     * @param waitSignStr
     * @return
     * @throws ClientException
     */
    public String sign(String waitSignStr) throws ClientException{
        try {
            JWTCreator.Builder builder = JWT.create();
            // 对payload进行加密
            builder = builder.withHeader(buildHeader()).withIssuer(issuer)
                    .withAudience(audience)
                    .withSubject(waitSignStr);
            // 设置过期时间
            if (expire != null && expire != 0) {
                Date expireDate = DateUtils.plus(DateUtils.now(), expire, timeUnit);
                builder.withExpiresAt(expireDate);
            }
            return builder.sign(Algorithm.HMAC256(secretString));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 验证token是否有效 并且返回解密后的payload
     * @param token
     * @return
     * @throws ClientException
     */
    public String validate(String token) throws ClientException {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretString)).build();
            // 验证token是否过期等
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getSubject();
        } catch (JWTVerificationException e) {
            log.error(e.getMessage(), e);
            //  TODO 具体异常具体分析 比如文件体不对 过期等
            Exception exception = e;
            if (e instanceof TokenExpiredException) {
                exception = new ClientException(NewbiestException.COMMON_TOKEN_IS_EXPIRED);
            }
            throw ExceptionManager.handleException(exception);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

}
