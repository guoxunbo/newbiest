//package com.newbiest.security;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.access.AccessDecisionManager;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.access.ConfigAttribute;
//import org.springframework.security.authentication.InsufficientAuthenticationException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.stereotype.Service;
//
//import java.util.Collection;
//import java.util.List;
//
///**
// * 检查当前登录用户是否拥有资源权限
// * Created by guoxunbo on 2017/12/4.
// */
//@Service
//@Slf4j
//public class NBAccessDecisionManager implements AccessDecisionManager {
//
//    @Override
//    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
//        if (configAttributes == null || configAttributes.size() == 0) {
//            return;
//        }
//        for (ConfigAttribute configAttribute : configAttributes) {
//            String roleId = configAttribute.getAttribute();
//            for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
//                if (roleId.equalsIgnoreCase(grantedAuthority.getAuthority())) {
//                    return;
//                }
//            }
//        }
//        throw new AccessDeniedException("no Authority");
//    }
//
//    @Override
//    public boolean supports(ConfigAttribute attribute) {
//        return true;
//    }
//
//    @Override
//    public boolean supports(Class<?> clazz) {
//        return true;
//    }
//}
