package com.newbiest.im.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.im.Exceptions;
import com.newbiest.im.model.InterfaceMonitor;
import com.newbiest.im.model.WSDefinition;
import com.newbiest.im.repository.InterfaceMonitorRepository;
import com.newbiest.im.repository.WsDefinitionRepository;
import com.newbiest.im.service.IMService;
import com.newbiest.main.ApplicationContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.ws.BindingProvider;

/**
 * Created by guoxunbo on 2019-11-07 16:30
 */
@Service
@Transactional
@Slf4j
public class IMServiceImpl implements IMService {

    @Autowired
    InterfaceMonitorRepository interfaceMonitorRepository;

    @Autowired
    WsDefinitionRepository wsDefinitionRepository;

    public InterfaceMonitor getInterfaceMonitorByName(String name) throws ClientException{
        return (InterfaceMonitor) interfaceMonitorRepository.findByNameAndOrgRrn(name, ThreadLocalContext.getOrgRrn());
    }

    /**
     * 根据接口名称，构建不同环境下webservice的BindingProvider
     * @param interfaceId 接口名称
     * @param bindingProvider 从ws2java上生成的
     * @throws ClientException
     */
    public void buildWsBindingProvider(String interfaceId, BindingProvider bindingProvider) throws ClientException {
        try {
            String env = ApplicationContextProvider.getApplicationContext().getEnvironment().getActiveProfiles()[0];
            WSDefinition wsDefinition = wsDefinitionRepository.findByImIdAndEnv(interfaceId, env);
            if (wsDefinition == null) {
                throw new ClientParameterException(Exceptions.WS_IS_NOT_EXIST, interfaceId, env);
            }
            bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, wsDefinition.getUsername());
            bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, wsDefinition.getPassword());
            bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsDefinition.getUrl());
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

}
