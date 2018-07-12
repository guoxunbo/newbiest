package com.newbiest.security.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.model.NBUser;

import java.util.List;

/**
 * Created by guoxunbo on 2018/6/1.
 */
public interface SecurityService {

    // ----User相关-----
    void login(String username, String password) throws ClientException;
    NBUser getUserByObjectRrn(Long userRrn) throws ClientException;
    NBUser getUserByUsername(String username) throws ClientException;

    List<NBAuthority> getAuthorities(Long userRrn) throws ClientException;
    NBUser saveUser(NBUser nbUser, SessionContext sc) throws ClientException;
    NBUser changePassword(NBUser user, String oldPassword, String newPassword, SessionContext sc) throws ClientException;
    NBUser resetPassword(NBUser nbUser, SessionContext sc) throws ClientException;
    NBUser getDeepUser(long userRrn, boolean orgFlag) throws ClientException;
}
