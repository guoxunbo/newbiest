//package com.newbiest.security;
//
//import com.newbiest.base.utils.EncryptionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
//
///**
// * Security配置
// * Created by guoxunbo on 2017/9/6.
// */
//@Configuration
//@EnableWebSecurity
//public class NBWebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Autowired
//    private NBUserDetailsService userDetailsService;
//
//    @Autowired
//    private DaoAuthenticationProvider authenticationProvider;
//
//    @Autowired
//    private NBFilterSecurityInterceptor nbFilterSecurityInterceptor;
//
//    /**
//     * 注入DaoAuthenticationProvider直接用户登录验证，密码加盐，显示用户没找到异常等
//     *
//     * @return
//     */
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider daoAuthenticationProvider = new NBDaoAuthenticationProvider();
//        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
//        daoAuthenticationProvider.setPreAuthenticationChecks(new NBPreAuthenticationChecks());
//        // 使用BCryptPasswordEncoder加解密 从spring-security3开始，固定盐值的加密已经被废除。使用随机盐值+BCrypt的强散列哈希加密实现
//        daoAuthenticationProvider.setPasswordEncoder(EncryptionUtils.passwordEncoder);
//        // 显示UserNotFoundExceptions
//        daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
//        return daoAuthenticationProvider;
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(authenticationProvider);
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        //TODO 替代登录页面 统一通过Security进行管理
//        http.authorizeRequests()//配置安全策略
//                .anyRequest().authenticated()//其余的所有请求都需要验证
//                .and()
//                .formLogin()
//                .successHandler(new LoginSuccessHandler())
//                .failureHandler(new LoginFailureHandler())
//                .and()
//                .logout()
//                .permitAll();//定义logout不需要验证
//        http.addFilterAfter(nbFilterSecurityInterceptor, FilterSecurityInterceptor.class);
//        http.csrf().disable();
//    }
//
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/manage/**","/test/**");
//
//    }
//}
