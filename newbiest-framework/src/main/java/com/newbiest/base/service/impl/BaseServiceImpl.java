package com.newbiest.base.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.newbiest.base.dao.BaseDao;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBMessage;
import com.newbiest.base.repository.MessageRepository;
import com.newbiest.base.repository.OrgRepository;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.main.ApplicationContextProvider;
import com.newbiest.security.model.NBOrg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.DependsOn;
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
@DependsOn("applicationContextProvider")
public class BaseServiceImpl implements BaseService  {

    @Autowired
    OrgRepository orgRepository;

    @Autowired
    MessageRepository messageRepository;

    @PostConstruct
    public void init() {
        loadMessages();
    }

    private void loadMessages() throws ClientException {
        List<NBMessage> nbMessages = (List<NBMessage>) messageRepository.findAll(NBOrg.GLOBAL_ORG_RRN);
        NBMessage.putAll(nbMessages);
    }

    public NBOrg getOrgByName(String name) throws ClientException {
        List<NBOrg> orgs = (List<NBOrg>) orgRepository.findByNameAndOrgRrn(name, NBOrg.GLOBAL_ORG_RRN);
        return orgs.get(0);
    }

    public NBOrg getOrgByObjectRrn(Long objectRrn) throws ClientException {
        return (NBOrg) orgRepository.findByObjectRrn(objectRrn);
    }

}
