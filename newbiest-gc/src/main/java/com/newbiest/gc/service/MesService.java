package com.newbiest.gc.service;


import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.mms.model.MaterialLot;
import java.util.List;

/**
 * 连接MES的服务相关
 * @author guozhangLuo
 * @date 2020-09-23
 */
public interface MesService {

    void materialLotUnitPlanLot(List<MaterialLot> materialLots, SessionContext sc) throws ClientException;

}
