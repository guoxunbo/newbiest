package com.newbiest.mms.gc.model.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.gc.model.MesPackedLot;

import java.util.List;

/**
 * Created by guoxunbo on 2019-08-21 12:41
 */
public interface GcService {

    void receiveFinishGood(List<MesPackedLot> packedLotList, long warehouseRrn) throws ClientException;

}
