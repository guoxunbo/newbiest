package com.newbiest.security.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.model.NBRole;
import com.newbiest.security.model.NBUser;

import java.util.List;

/**
 * Created by guoxunbo on 2018/6/1.
 */
public interface SecurityService {

    // ----User相关-----
    void login(String username, String password, SessionContext sc) throws ClientException;
    NBUser getUserByObjectRrn(Long userRrn) throws ClientException;
    NBUser getUserByUsername(String username) throws ClientException;

    List<NBAuthority> getAuthorities(Long userRrn) throws ClientException;
    NBUser saveUser(NBUser nbUser, SessionContext sc) throws ClientException;
    NBUser changePassword(NBUser user, String oldPassword, String newPassword, SessionContext sc) throws ClientException;
    NBUser resetPassword(NBUser nbUser, SessionContext sc) throws ClientException;
    NBUser getDeepUser(long userRrn, boolean orgFlag) throws ClientException;
    void deleteUser(NBUser user) throws ClientException;

    // ----Role相关----
    NBRole saveRole(NBRole nbRole) throws ClientException;
    NBRole getRoleByObjectRrn(Long objectRrn) throws ClientException;
    NBRole getRoleByRoleId(String roleId) throws ClientException;
    NBRole getDeepRole(Long roleRrn, boolean authorityFlag, SessionContext sc) throws ClientException;
    void deleteRole(NBRole nbRole) throws ClientException;
}
