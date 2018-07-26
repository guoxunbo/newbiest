package com.newbiest.msg.trans;

import com.newbiest.base.service.BaseService;
import com.newbiest.security.repository.RoleRepository;
import com.newbiest.security.repository.UserRepository;
import com.newbiest.security.service.SecurityService;
import lombok.Data;

/**
 * 处理消息的容器
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class TransContext {

    private String transactionId;
    private String request;
    private String response;

    private BaseService baseService;
    private SecurityService securityService;

    private UserRepository userRepository;
    private RoleRepository roleRepository;

}
