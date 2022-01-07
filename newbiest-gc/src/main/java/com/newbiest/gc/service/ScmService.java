package com.newbiest.gc.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.gc.model.GCScmToMesEngInform;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;

import java.util.List;
import java.util.Map;

/**
 * 连接SCM的服务相关
 * @author guoxunbo
 * @date 2020-08-09 10:39
 */
public interface ScmService {

    void scmSaveEngInfo(List<GCScmToMesEngInform> lotEngInfoList, String actionType) throws ClientException;
    void scmDeleteEngInfo(List<GCScmToMesEngInform> lotEngInfoList) throws ClientException;

    void retry() throws ClientException;

    List<Map<String, String>> scmLotQuery(List<Map<String, String>> lotIdList) throws ClientException;
    List<Map<String, String>> queryScmWaferByWorkOrderNo(String workOrderNo) throws ClientException;

    void scmHold(List<String> materialLotIdList, String actionCode, String actionReason, String actionRemarks) throws ClientException;
    void scmRelease(List<String> materialLotIdList, String actionCode, String actionReason, String actionRemarks) throws ClientException;

    void scmAssign(String lotId, String vendor, String poId, String materialType, String remarks, String vendorAddress) throws ClientException;
    void scmUnAssign(String lotId) throws ClientException;

    List<MaterialLotUnit> assignEngFlag(List<MaterialLotUnit> materialLotUnits) throws ClientException;
    String getMScmToken() throws ClientException;
    void addTracking(String orderId, String expressNumber, boolean isKuayueExprress) throws ClientException;
    void addScmTracking(String orderId, List<MaterialLot> materialLotList) throws ClientException;

    void sendMaterialStateReport(List<MaterialLotUnit> materialLotUnitList, String action) throws ClientException;


}
