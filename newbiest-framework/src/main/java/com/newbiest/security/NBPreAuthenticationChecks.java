//package com.newbiest.security;
//
//import com.newbiest.base.exception.NewbiestException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AccountExpiredException;
//import org.springframework.security.authentication.DisabledException;
//import org.springframework.security.authentication.LockedException;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsChecker;
//
///**
// * 重写用户检查 检查Lock, Expried, Enable相关检查
// * Created by guoxunbo on 2017/11/18.
// */
//@Slf4j
//public class NBPreAuthenticationChecks implements UserDetailsChecker {
//
//    @Override
//    public void check(UserDetails user) {
//        if (!user.isAccountNonLocked()) {
//            if (log.isDebugEnabled()) {
//                log.debug(NewbiestException.COMMON_WRONG_PWD_MORE_THAN_COUNT);
//            }
//            throw new LockedException(NewbiestException.COMMON_WRONG_PWD_MORE_THAN_COUNT);
//        }
//
//        if (!user.isEnabled()) {
//            if (log.isDebugEnabled()) {
//                log.debug(NewbiestException.COMMON_USER_IS_NOT_ACTIVE);
//            }
//            throw new DisabledException(NewbiestException.COMMON_USER_IS_NOT_ACTIVE);
//        }
//
//        if (!user.isAccountNonExpired()) {
//            if (log.isDebugEnabled()) {
//                log.debug(NewbiestException.COMMON_PASSWORD_IS_EXPIRY);
//            }
//            throw new AccountExpiredException(NewbiestException.COMMON_PASSWORD_IS_EXPIRY);
//        }
//    }
//}
