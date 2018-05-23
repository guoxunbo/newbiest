//package com.newbiest.security;
//
//import com.newbiest.base.exception.ClientParameterException;
//import com.newbiest.base.exception.NewbiestException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetails;
//
///**
// * 重载验证方法以及抛出的异常
// * Created by guoxunbo on 2017/11/18.
// */
//@Slf4j
//public class NBDaoAuthenticationProvider extends DaoAuthenticationProvider {
//
//    @Override
//    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
//        Object salt = null;
//
//        if (getSaltSource() != null) {
//            salt = getSaltSource().getSalt(userDetails);
//        }
//
//        if (authentication.getCredentials() == null) {
//            if(log.isDebugEnabled()) {
//                log.debug(NewbiestException.COMMON_PASSWORD_IS_NULL);
//            }
//
//            throw new BadCredentialsException(messages.getMessage(
//                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
//                    NewbiestException.COMMON_PASSWORD_IS_NULL));
//        }
//
//        String presentedPassword = authentication.getCredentials().toString();
//
//        if (!getPasswordEncoder().isPasswordValid(userDetails.getPassword(),
//                presentedPassword, salt)) {
//            if (log.isDebugEnabled()) {
//                logger.debug(NewbiestException.COMMON_USER_PASSWORD_IS_INCORRECT);
//            }
//
//            throw new BadCredentialsException(messages.getMessage(
//                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
//                    NewbiestException.COMMON_USER_PASSWORD_IS_INCORRECT + ClientParameterException.PARAMETER_SPLIT_CODE + authentication.getName()));
//        }
//    }
//}
