package com.newbiest.im.service;

import com.newbiest.base.exception.ClientException;

import javax.xml.ws.BindingProvider;

/**
 * Created by guoxunbo on 2019-11-07 16:30
 */
public interface IMService {

    void buildWsBindingProvider(String interfaceId, BindingProvider bindingProvider) throws ClientException;

}
