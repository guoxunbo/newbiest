package com.newbiest.security;

import com.newbiest.base.exception.ExceptionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Service;

import javax.servlet.*;
import java.io.IOException;


/**
 * 重写资源校验 满足系统检查
 * Created by guoxunbo on 2017/12/4.
 */
@Slf4j
@Service
public class NBFilterSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

    @Autowired
    private FilterInvocationSecurityMetadataSource securityMetadataSource;

    @Autowired
    public void setMyAccessDecisionManager(NBAccessDecisionManager nbAccessDecisionManager) {
        super.setAccessDecisionManager(nbAccessDecisionManager);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
//        // 当资源没有配置权限的时候，抛出异常
//        setRejectPublicInvocations(true);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation(servletRequest, servletResponse, filterChain);
        //FilterInvocation里面放入一个Url 调用securityMetadataSource的getAttributes来获取URL对应的所有权限
        // 返回权限之后 调用AccessDecisionManager的decide方法进行权限校验
        InterceptorStatusToken token = super.beforeInvocation(fi);
        try {
            //执行下一个拦截器
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        } finally {

            super.afterInvocation(token, null);
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return securityMetadataSource;
    }
}
