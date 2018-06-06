package com.newbiest.security.service;

import com.newbiest.base.exception.ClientException;
import org.springframework.stereotype.Service;

/**
 * Created by guoxunbo on 2018/6/1.
 */
public interface SecurityService {

    void login(String username, String password) throws ClientException;

}
