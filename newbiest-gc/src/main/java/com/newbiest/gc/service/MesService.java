package com.newbiest.gc.service;


import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.model.MaterialLotUnit;

import java.util.List;

/**
 * 连接MES的服务相关
 * @author guozhangLuo
 * @date 2020-09-23
 */
public interface MesService {

    void materialLotUnitPlanLot(List<MaterialLotUnit> materialLotUnits) throws ClientException;

}
