package com.newbiest.security.repository.custom;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.model.NBUser;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * 提供一些关于user但是repository本身不支持的操作。
 * Created by guoxunbo on 2017/9/29.
 */
public interface UserRepositoryCustom {

    EntityManager getEntityManager();

    NBUser getDeepUser(Long userRrn, boolean orgFlag) throws ClientException;
    NBUser getDeepUser(String username, boolean orgFlag) throws ClientException;

    List<NBAuthority> getTreeAuthorities(long userRrn) throws ClientException;

    void loginSuccess(NBUser nbUser) throws ClientException;

    void loginFail(NBUser nbUser) throws ClientException;

}