package com.newbiest.security.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.repository.UserRepository;
import com.newbiest.security.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by guoxunbo on 2018/6/1.
 */
@Service
@Transactional
@Slf4j
public class SecurityServiceImpl implements SecurityService  {

    @Autowired
    UserRepository userRepository;

    public String login(String username, String password) throws ClientException{
        try {
            /**
             * TODO 1. 先查找用户 用户如果没找到直接抛出异常
             * 2. 如果找到用户
             *    2.1 验证用户是否被锁住 如果被锁住。如果用户邮箱有设置，进行发送新密码邮件并抛出异常
             *    2.2 用户密码错误
             *        2.2.1 增加用户密码错误次数 当错误次数达到相应的设置最大次数之后 锁住账户
             *        2.2.2
             */
            password = DigestUtils.md5Hex(password);

            NBUser nbUser = userRepository.getByUsernameAndPassword(username, password);
            if (nbUser == null) {
                throw new ClientException("COMMON_USER_IS_NOT_FOUND");
            }


            return "";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }


}
