package com.newbiest.base.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.security.model.NBOrg;

/**
 * Created by guoxunbo on 2018/6/6.
 */
public interface BaseService {

    void loadMessages() throws ClientException;

    NBOrg getOrgByName(String name) throws ClientException;
    NBOrg getOrgByObjectRrn(Long objectRrn) throws ClientException;

}
