package com.newbiest.gc.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.model.MaterialLotUnit;

import java.util.List;

/**
 * 连接SCM的服务相关
 * @author guoxunbo
 * @date 2020-08-09 10:39
 */
public interface ScmService {

    void assignEngFlag(List<MaterialLotUnit> materialLotUnits) throws ClientException;
    String getMScmToken() throws ClientException;
    void addTracking(String orderId, String expressNumber, boolean isKuayueExprress) throws ClientException;

}
