package com.newbiest.msg.trans;

import com.newbiest.base.manager.dao.NBManager;
import com.newbiest.security.repository.RoleRepository;
import com.newbiest.security.repository.UserRepository;
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

    private NBManager nbManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;

}
