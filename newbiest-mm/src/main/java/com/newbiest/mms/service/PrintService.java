package com.newbiest.mms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.model.MaterialLot;

import java.util.List;
import java.util.Map;

/**
 * @author guoxunbo
 * @date 4/6/21 3:04 PM
 */
public interface PrintService {
    void printMLot(MaterialLot materialLot) throws ClientException;
    void printMLotList(List<MaterialLot> materialLots) throws ClientException;

    void printBoxMLot(MaterialLot boxMaterialLot) throws ClientException;
    Map<String, Object> buildBoxParameterMap(MaterialLot boxMaterialLot) throws ClientException;

    Map<String, Object> buildVIVOBoxOtherParameterMap(MaterialLot boxMaterialLot) throws ClientException;
    void printVIVOBoxMLot(MaterialLot boxMaterialLot) throws ClientException;
    void printOPPOBoxTagMLot(MaterialLot boxMaterialLot, Map<String, Object> parameterMap) throws ClientException;
}
