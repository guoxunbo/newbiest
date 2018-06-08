package com.newbiest.security.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.security.model.NBAuthority;

import java.util.List;

/**
 * Created by guoxunbo on 2018/6/1.
 */
public interface SecurityService {

    void login(String username, String password) throws ClientException;

    List<NBAuthority> getAuthorities(Long userRrn) throws ClientException;
}
