package com.newbiest.vanchip.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.model.MaterialLot;

import java.util.List;

/**
 * 连接MES的服务相关
 */
public interface MesService {

    void issueMLot(List<MaterialLot> materialLots) throws ClientException;
    void returnMLot(List<MaterialLot> materialLots) throws ClientException;

}
