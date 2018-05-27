package com.newbiest.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

/**
 * Created by guoxunbo on 2018/5/25.
 */
@Slf4j
public class JwtToken {

    /**
     * 定义秘钥
     */
    private static final String SECRET_STRING = "newbiest";


    private static Map<String, Object> buildHeader() {
        return ImmutableMap.of("typ", "JWT", "alg", "HS256");
    }

    public static String sign(String waitSignStr, long duration) throws ClientException{
        try {
            JWTCreator.Builder builder = JWT.create();
            builder.withHeader(buildHeader()).withSubject(waitSignStr);
            return builder.sign(Algorithm.HMAC256(SECRET_STRING));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public static String unsign(String token) throws ClientException {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_STRING)).build();
            DecodedJWT jwt = verifier.verify(token);

//            Date exp = jwt.getExpiresAt();
//            if(exp !=null && exp.after(new Date())){
                String subject = jwt.getSubject();
                return subject;
//            }
//            return StringUtils.EMPTY;
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }


    public static void main(String[] args) {
        String token = sign("aaa", 10);
        System.out.println(token);

        String value = unsign(token);
        System.out.println(value);
    }
}
