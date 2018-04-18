package com.newbiest.security;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.model.NBUserHis;
import com.newbiest.security.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录成功之后
 * Created by guoxunbo on 2017/11/18.
 */
@Component
@Slf4j
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public static final String X_FORWARDED_FOR = "x-forwarded-for";
    public static final String Proxy_Client_IP = "Proxy-Client-IP";
    public static final String WL_Proxy_Client_IP = "WL-Proxy-Client-IP";
    public static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    public static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";
    public static final String UNKNOWN = "unknown";

    @Autowired
    private UserRepository userRepository;

    /**
     * 登录成功之后的操作
     * @param request
     * @param response
     * @param authentication
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        NBSecurityUser nbSecurityUser = (NBSecurityUser) authentication.getPrincipal();

        // 清空用户密码错误次数
        NBUser nbuser = nbSecurityUser.getNbUser();
        userRepository.loginSuccess(nbuser);

        String ipAddress = getIpAddress(request);
        if (log.isDebugEnabled()) {
            log.debug("The UserName" + nbSecurityUser.getUsername() + "at Ip[" +  ipAddress + "] login");
        }
        // 记录日志
        super.onAuthenticationSuccess(request, response, authentication);
    }

    /**
     * 取得IP地址
     * @param request
     * @return
     */
    public String getIpAddress(HttpServletRequest request){
        String ip = request.getHeader(X_FORWARDED_FOR);

        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(Proxy_Client_IP);
        }
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(WL_Proxy_Client_IP);
        }
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(HTTP_CLIENT_IP);
        }
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(HTTP_X_FORWARDED_FOR);
        }
        if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
