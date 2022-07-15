package com.newbiest.mms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;

import java.util.List;
import java.util.Map;

/**
 * @author guoxunbo
 * @date 4/6/21 3:04 PM
 */
public interface PrintService {

    Map<String, Object> printCobRetestLabel(MaterialLot materialLot, String printCount, String printType) throws ClientException;
    List<Map<String, Object>> printWaferCstAndLotLabel(List<MaterialLot> materialLotList) throws ClientException;
    List<Map<String, Object>> printReceiveWltCpLotLabel(List<MaterialLot> materialLotList, String printCount) throws ClientException;
    Map<String, Object> printWltOrCpLabel(MaterialLot materialLot, String printCount) throws ClientException;
    Map<String, Object> printWltBoxLabel(MaterialLot materialLot, String printCount) throws ClientException;
    List<Map<String, Object>> printWltBboxLabel(List<MaterialLotUnit> materialLotUnitList) throws ClientException;

    List<Map<String, Object>> printRmaMaterialLotLabel(List<MaterialLot> materialLotList, String printCount) throws ClientException;

    List<Map<String, Object>> printMaterialLotObliqueBoxLabel(List<MaterialLot> materialLotList, String expressNumber) throws ClientException;
    List<Map<String, Object>>  printSamsungOuterBoxLabel(List<DocumentLine> documentLines, Integer printCount) throws ClientException;
    List<Map<String, Object>> printRwLotCstLabel(List<MaterialLot> materialLotList, String printCount) throws ClientException;
    Map<String, Object> rePrintRwLotCstLabel(MaterialLot materialLot, String printCount) throws ClientException;
    Map<String, Object> printRwCstLabel(MaterialLot materialLot, String printCount) throws ClientException;
    Map<String, Object> printRwStockOutLabel(MaterialLot materialLot) throws ClientException;
    List<Map<String, Object>> printRwLotIssueLabel(List<MaterialLot> materialLotList, String printCount) throws ClientException;
    Map<String, Object> printRWBoxLabel(MaterialLot materialLot) throws ClientException;

    Map<String, Object> printCobBoxLabel(MaterialLot materialLot, String printCount) throws ClientException;
    Map<String, Object> printCobBBoxLabel(MaterialLot materialLot) throws ClientException;
    List<Map<String, Object>> printBoxQRCodeLabel(MaterialLot materialLot, String printVboxLabelFlag) throws ClientException;

    List<Map<String, Object>> printComBoxAndCustomerLabel(MaterialLot materialLot, String subcode, String printCount) throws ClientException;
    List<Map<String, Object>> rePrintVBxoLabel(List<Map<String, Object>> parameterMapList, String labelType) throws ClientException;

    List<Map<String, Object>> printRawMlotIRLabel(List<MaterialLot> materialLots) throws ClientException;
    List<Map<String, Object>> printRawMlotGlueLabel(List<MaterialLot> materialLots) throws ClientException;
    List<Map<String, Object>> printIRABoxLabel(List<MaterialLot> materialLots) throws ClientException;

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

    //龙腾光电物料标签
    void printLongTenMLotLabel(List<Map<String, String>> mapList) throws ClientException;

    //LCD箱标签
    List<Map<String,Object>> printLCDBoxLabel(MaterialLot materialLot, String printCount) throws ClientException;

    //来料Wafer标签
    List<Map<String,Object>> printWaferLabel(List<MaterialLotUnit> materialLotUnitList)  throws ClientException;
}
