package com.newbiest.vanchip.service;

import com.newbiest.base.exception.ClientException;

import java.util.List;

/**
 * 连接MES的服务相关
 */
public interface MesService {

    void issueMLot(List<String> materialLotIdList) throws ClientException;
    void returnMLot(List<String> materialLotIdList) throws ClientException;

}
