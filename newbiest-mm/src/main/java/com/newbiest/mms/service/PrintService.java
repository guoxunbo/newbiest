package com.newbiest.mms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;

import java.util.List;
import java.util.Map;

/**
 * @author guoxunbo
 * @date 4/6/21 3:04 PM
 */
public interface PrintService {

    void printCobRetestLabel(MaterialLot materialLot, String printCount, String printType) throws ClientException;
    void printWaferCstAndLotLabel(List<MaterialLot> materialLotList) throws ClientException;
    void printReceiveWltCpLotLabel(List<MaterialLot> materialLotList, String printCount) throws ClientException;
    void printWltOrCpLabel(MaterialLot materialLot, String printCount) throws ClientException;
    void printWltBoxLabel(MaterialLot materialLot, String printCount) throws ClientException;
    void printWltBboxLabel(List<MaterialLotUnit> materialLotUnitList) throws ClientException;

    void printRmaMaterialLotLabel(List<MaterialLot> materialLotList) throws ClientException;

    void printMaterialLotObliqueBoxLabel(List<MaterialLot> materialLotList, String expressNumber) throws ClientException;
    void printRwLotCstLabel(List<MaterialLot> materialLotList, String printCount) throws ClientException;
    void rePrintRwLotCstLabel(MaterialLot materialLot, String printCount) throws ClientException;
    void printRwCstLabel(MaterialLot materialLot, String printCount) throws ClientException;
    void printRwStockOutLabel(MaterialLot materialLot) throws ClientException;
    void printRwLotIssueLabel(List<MaterialLot> materialLotList, String printCount) throws ClientException;

    void printCobBoxLabel(MaterialLot materialLot,  String printCount) throws ClientException;
    void printCobBBoxLabel(MaterialLot materialLot) throws ClientException;
    void printBoxQRCodeLabel(MaterialLot materialLot, String printVboxLabelFlag) throws ClientException;

    void printComBoxAndCustomerLabel(MaterialLot materialLot, String subcode, String printCount) throws ClientException;
    void rePrintVBxoLabel(List<Map<String, Object>> parameterMapList) throws ClientException;

    void printRawMlotIRLabel(List<MaterialLot> materialLots) throws ClientException;
    void printRawMlotGlueLabel(List<MaterialLot> materialLots) throws ClientException;
    void printIRABoxLabel(List<MaterialLot> materialLots) throws ClientException;

    //欧菲光
    void printOphelionMLotLabel(List<Map<String, String>> mapList) throws ClientException;

    //白辰
    void printBaichenMLotLabel(List<Map<String, String>> mapList) throws ClientException;

    //光宝真空包
    void printGuangBaoVBoxLabel(List<Map<String, String>> mapList) throws ClientException;

    //COB光宝标签
    void printCobGuangBaoLabel(List<Map<String, String>> mapList) throws ClientException;

    //华天
    void printHuatianLabel(List<Map<String, String>> mapList) throws ClientException;

    //盛泰真空包
    void printShengTaiVBoxLabel(List<Map<String, String>> mapList) throws ClientException;

    //比亚迪内箱
    void prinBydLabel(List<Map<String, String>> mapList) throws ClientException;

    //信利光电
    void printXLGDBoxLabel(List<Map<String, String>> mapList) throws ClientException;

    //舜宇
    void printShunYuLabel(List<Map<String, String>> mapList) throws ClientException;

    //中控智慧
    void printZhongKongLabel(List<Map<String, String>> mapList) throws ClientException;

    //芯智物料标签
    void printXingZhiMLotLabel(List<Map<String, String>> mapList) throws ClientException;

    //一般物料标签/光宝箱/盛泰箱
    void PrintGeneralMLotLabel(List<Map<String, String>> mapList) throws ClientException;

}
