package com.newbiest.gc.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;

import java.util.List;

/**
 * 连接SCM的服务相关
 * @author guoxunbo
 * @date 2020-08-09 10:39
 */
public interface ScmService {

    void scmAssign(String lotId, String vendor, String poId, String materialType, String remarks) throws ClientException;
    void scmUnAssign(String lotId) throws ClientException;

    List<MaterialLotUnit> assignEngFlag(List<MaterialLotUnit> materialLotUnits) throws ClientException;
    String getMScmToken() throws ClientException;
    void addTracking(String orderId, String expressNumber, boolean isKuayueExprress) throws ClientException;
    void addScmTracking(String orderId, List<MaterialLot> materialLotList) throws ClientException;
}
