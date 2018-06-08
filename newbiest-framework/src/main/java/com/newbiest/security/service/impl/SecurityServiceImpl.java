package com.newbiest.security.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.redis.RedisService;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.EncryptionUtils;
import com.newbiest.main.JwtSigner;
import com.newbiest.main.MailService;
import com.newbiest.main.NewbiestConfiguration;
import com.newbiest.security.exception.SecurityException;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.repository.UserRepository;
import com.newbiest.security.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


/**
 * Created by guoxunbo on 2018/6/1.
 */
@Service
@Slf4j
@Transactional
public class SecurityServiceImpl implements SecurityService  {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MailService mailService;

    @Autowired
    NewbiestConfiguration newbiestConfiguration;

    @Autowired
    RedisService redisService;

    @Autowired
    JwtSigner jwtSigner;

    public void login(String username, String password) throws ClientException {
        try {
            // 1. 先查找用户 用户如果没找到直接抛出异常
            NBUser nbUser = userRepository.getByUsername(username);
            if (nbUser == null) {
                throw new ClientParameterException(SecurityException.SECURITY_USER_IS_NOT_FOUND, username);
            }
            // 验证用户是否被锁住
            if (!nbUser.getInValidFlag()) {
                //TODO 是否要发送新密码邮件
                throw new ClientException(SecurityException.SECURITY_USER_IS_NOT_IN_VALIDATION);
            }
            // 验证密码错误次数大于定义的次数
            if (nbUser.getPwdWrongCount() != null && nbUser.getPwdWrongCount() > newbiestConfiguration.getPwdWrongCount()) {
                throw new ClientException(SecurityException.SECURITY_WRONG_PWD_MORE_THAN_COUNT);
            }
            // 验证密码是否过期
            if (nbUser.getPwdExpiry() != null && nbUser.getPwdExpiry().before(DateUtils.now())) {
                throw new ClientException(SecurityException.SECURITY_PASSWORD_IS_EXPIRY);
            }
            // 验证密码
            password = EncryptionUtils.md5Hex(password);
            if (!password.equals(nbUser.getPassword())) {
                userRepository.loginFail(nbUser);
                throw new ClientException(SecurityException.SECURITY_USER_PASSWORD_IS_INCORRECT);
            }
            userRepository.loginSuccess(nbUser);
            // 生成jwtToken并存入redis
            String signStr = jwtSigner.sign(nbUser.getUsername());
            redisService.put(nbUser.getUsername(), signStr);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 获取权限并转换成相应的DTO返回
     * @param userRrn
     * @return
     * @throws ClientException
     */
    public List<NBAuthority> getAuthorities(Long userRrn) throws ClientException{
        try {
            return userRepository.getTreeAuthorities(userRrn);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

}
