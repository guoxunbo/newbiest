package com.newbiest.base.filter;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.main.JwtSigner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by guoxunbo on 2018/9/26.
 */
@Configuration
@WebFilter(urlPatterns = "/*", filterName = "test")
public class TokenFilter implements Filter {

    private static final String AUTHORITY_HEAD_NAME = "Authorization";

    @Autowired
    JwtSigner jwtSigner;

    /**
     * TODO 白名单路径定义
     */
    private List<String> whiteUrlList = Lists.newArrayList();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest)servletRequest;
            String auth = httpRequest.getHeader(AUTHORITY_HEAD_NAME);
            String remoteIp = servletRequest.getRemoteAddr();
            if (jwtSigner.getWhiteIPList().contains(remoteIp)) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else if (whiteUrlList.contains(httpRequest.getRequestURL())) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                //TODO 处理验证登录情况
                //            if (StringUtils.isNullOrEmpty(auth)) {
//                //TODO 没登陆
//                throw new ClientException("");
//            }
//            // 当前只会验证并不会每次生成一个新值并返回
//            jwtSigner.validate(auth);
                filterChain.doFilter(servletRequest, servletResponse);
            }




        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    @Override
    public void destroy() {

    }
}

