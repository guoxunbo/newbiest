package com.newbiest.gc.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;

import java.util.List;

/**
 * 连接SCM的服务相关
 * @author guoxunbo
 * @date 2020-08-09 10:39
 */
public interface ScmService {

    void retry() throws ClientException;

    void scmHold(List<String> materialLotIdList, String actionCode, String actionReason, String actionRemarks) throws ClientException;
    void scmRelease(List<String> materialLotIdList, String actionCode, String actionReason, String actionRemarks) throws ClientException;

    void scmAssign(String lotId, String vendor, String poId, String materialType, String remarks, String vendorAddress) throws ClientException;
    void scmUnAssign(String lotId) throws ClientException;

    List<MaterialLotUnit> assignEngFlag(List<MaterialLotUnit> materialLotUnits) throws ClientException;
    String getMScmToken() throws ClientException;
    void addTracking(String orderId, String expressNumber, boolean isKuayueExprress) throws ClientException;
    void addScmTracking(String orderId, List<MaterialLot> materialLotList) throws ClientException;

    void sendMaterialStateReport(List<MaterialLot> materialLots, String action) throws ClientException;


}
