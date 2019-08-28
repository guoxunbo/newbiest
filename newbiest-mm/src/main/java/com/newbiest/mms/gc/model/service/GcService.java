package com.newbiest.mms.gc.model.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.gc.model.MesPackedLot;
import com.newbiest.mms.gc.model.StockOutCheck;
import com.newbiest.mms.model.MaterialLot;

import java.util.List;

/**
 * Created by guoxunbo on 2019-08-21 12:41
 */
public interface GcService {

    void receiveFinishGood(List<MesPackedLot> packedLotList) throws ClientException;

    void bindRelaxBox(List<MaterialLot> materialLots, String relaxBoxId) throws ClientException;
    void unbindRelaxBox(List<MaterialLot> materialLots) throws ClientException;

    void judgePackedMaterialLot(List<MaterialLot> materialLots, String judgeGrade, String judgeCode) throws ClientException;

    List<StockOutCheck> getStockOutCheckList() throws ClientException;
    MaterialLot stockOutCheck(MaterialLot materialLot, String checkResult) throws ClientException;

}
