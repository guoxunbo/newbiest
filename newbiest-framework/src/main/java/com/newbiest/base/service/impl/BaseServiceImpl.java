package com.newbiest.base.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.dao.BaseDao;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBMessage;
import com.newbiest.base.service.BaseService;
import com.newbiest.security.model.NBOrg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by guoxunbo on 2018/6/6.
 */
@Service
@Slf4j
public class BaseServiceImpl implements BaseService  {

    @Autowired
    BaseDao baseDao;

    @PostConstruct
    public void loadMessages() throws ClientException {
        List<NBMessage> nbMessages = (List<NBMessage>) baseDao.getEntityList(NBOrg.GLOBAL_ORG_RRN, NBMessage.class);
        NBMessage.putAll(nbMessages);
    }

}
