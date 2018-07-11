package com.newbiest.base.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.dao.BaseDao;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBMessage;
import com.newbiest.base.repository.OrgRepository;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.security.model.NBOrg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by guoxunbo on 2018/6/6.
 */
@Service
@Slf4j
@Transactional
public class BaseServiceImpl implements BaseService  {

    @Autowired
    BaseDao baseDao;

    @Autowired
    OrgRepository orgRepository;

    @PostConstruct
    public void loadMessages() throws ClientException {
        List<NBMessage> nbMessages = (List<NBMessage>) baseDao.getEntityList(NBOrg.GLOBAL_ORG_RRN, NBMessage.class);
        NBMessage.putAll(nbMessages);
    }

    public NBOrg getOrgByName(String name) throws ClientException {
        return orgRepository.getByName(name);
    }

    public NBOrg getOrgByObjectRrn(Long objectRrn) throws ClientException {
        return orgRepository.getByObjectRrn(objectRrn);
    }

}
