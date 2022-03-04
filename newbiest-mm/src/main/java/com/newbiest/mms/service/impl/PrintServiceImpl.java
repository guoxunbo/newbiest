package com.newbiest.mms.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.annotation.BaseJpaFilter;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.*;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.print.DefaultPrintStrategy;
import com.newbiest.mms.print.IPrintStrategy;
import com.newbiest.mms.print.MLotCodePrint;
import com.newbiest.mms.print.PrintContext;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.MaterialLotUnitService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.mms.service.PrintService;
import freemarker.template.utility.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author guoxunbo
 * @date 5/22/21
 */
@Service
@Slf4j
@Transactional
@BaseJpaFilter
@Data
@EnableAsync
public class PrintServiceImpl implements PrintService {

    @Autowired
    Map<String, IPrintStrategy> printStrategyMap;

    @Autowired
    WorkStationRepository workStationRepository;

    @Autowired
    LabelTemplateRepository labelTemplateRepository;

    @Autowired
    LabelTemplateParameterRepository labelTemplateParameterRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    MmsService mmsService;

    @Autowired
    DocumentLineRepository documentLineRepository;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialLotUnitService materialLotUnitService;

    @Autowired
    PackageService packageService;

    @Autowired
    GeneratorService generatorService;

    public void print(PrintContext printContext) {
        print(DefaultPrintStrategy.DEFAULT_STRATEGY_NAME, printContext);
    }

    public void print(String strategyName, PrintContext printContext) {
        IPrintStrategy printStrategy = printStrategyMap.get(strategyName);
        if (printStrategy == null) {
            printStrategy = printStrategyMap.get(DefaultPrintStrategy.DEFAULT_STRATEGY_NAME);
        }
        if (log.isDebugEnabled()) {
            log.debug("Use context [" + printContext.toString() + "] to print");
        }
        printStrategy.print(printContext);
    }

    /**
     * 构建client打印所需参数
     * @param printContext
     * @return
     */
    public Map<String, Object> buildClientParameters(PrintContext printContext) {
        Map<String, Object> map = Maps.newHashMap();

        Map<String, Object> params = buildClientParameters(DefaultPrintStrategy.DEFAULT_STRATEGY_NAME, printContext);
        map.put("params", params);
        map.put("url", printContext.getLabelTemplate().getBartenderDestination(printContext.getWorkStation()));
        return map;
    }

    public Map<String, Object> buildClientParameters(String strategyName, PrintContext printContext) {
        IPrintStrategy printStrategy = printStrategyMap.get(strategyName);
        if (printStrategy == null) {
            printStrategy = printStrategyMap.get(DefaultPrintStrategy.DEFAULT_STRATEGY_NAME);
        }
        if (log.isDebugEnabled()) {
            log.debug("Use context [" + printContext.toString() + "] to print");
        }
        return printStrategy.buildParameters(printContext);
    }

    /**
     * 根据ID生成规则生成序列号
     * @return
     * @throws ClientException
     */
    public String generatorMLotsTransId(String ruleId) throws ClientException{
        try {
            GeneratorContext generatorContext = new GeneratorContext();
            generatorContext.setRuleName(ruleId);
            String id = generatorService.generatorId(ThreadLocalContext.getOrgRrn(), generatorContext);
            return id;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 构建打印参数
     * @param labelTemplateName
     * @return
     * @throws ClientException
     */
    private PrintContext buildPrintContext(String labelTemplateName, String printCount) throws ClientException{
        try {
            String ipAddress = ThreadLocalContext.getTransactionIp();
            WorkStation workStation = workStationRepository.findByIpAddress(ipAddress);
            if (workStation == null) {
                workStation = new WorkStation();
                workStation.setPrintMachineIpAddress(ipAddress);
            }
            LabelTemplate labelTemplate = labelTemplateRepository.findByName(labelTemplateName);
            if (labelTemplate == null) {
                throw new ClientParameterException(MmsException.MM_LBL_TEMPLATE_IS_NOT_EXIST, labelTemplateName);
            }
            if(!StringUtils.isNullOrEmpty(printCount)){
                labelTemplate.setPrintCount(Integer.parseInt(printCount));
            }
            List<LabelTemplateParameter> parameterList = labelTemplateParameterRepository.findByLblTemplateRrn(labelTemplate.getObjectRrn());
            labelTemplate.setLabelTemplateParameterList(parameterList);

            PrintContext printContext = new PrintContext();
            printContext.setLabelTemplate(labelTemplate);
            printContext.setWorkStation(workStation);
            return printContext;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 补打COB虚拟重测箱标签
     * @param materialLot
     * @param printCount
     * @param printType
     * @throws ClientException
     * @return
     */
    @Override
    public Map<String, Object> printCobRetestLabel(MaterialLot materialLot, String printCount, String printType) throws ClientException {
        try {
            Map<String, Object> clientPrintParams = Maps.newHashMap();

            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_WAFER_LOT_LABEL, printCount);
            Map<String, Object> parameterMap = buildWaferCstAndLotPrintParameter(materialLot, printType);
            printContext.setBaseObject(materialLot);
            printContext.setParameterMap(parameterMap);

            if (printContext.getWorkStation().getIsClientPrint()){
                clientPrintParams = buildClientParameters(printContext);
            }else {
                print(printContext);
            }
            return clientPrintParams;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 打印wafer拆箱  lotId和cstId的标签
     * @param materialLotList
     * @throws ClientException
     * @return
     */
    @Override
    public List<Map<String, Object>> printWaferCstAndLotLabel(List<MaterialLot> materialLotList) throws ClientException {
        try {
            List<Map<String, Object>> mapList = Lists.newArrayList();

            for (MaterialLot materialLot : materialLotList) {
                PrintContext lotPrintContext = buildPrintContext(LabelTemplate.PRINT_WAFER_LOT_LABEL, "1");
                Map<String, Object> lotParameterMap = buildWaferCstAndLotPrintParameter(materialLot, "");
                lotPrintContext.setBaseObject(materialLot);
                lotPrintContext.setParameterMap(lotParameterMap);

                if (lotPrintContext.getWorkStation().getIsClientPrint()){
                    Map<String, Object> lotParams = Maps.newHashMap();
                    lotParams = buildClientParameters(lotPrintContext);
                    mapList.add(lotParams);
                }else {
                    print(lotPrintContext);
                }

                PrintContext cstPrintContext = buildPrintContext(LabelTemplate.PRINT_WAFER_LOT_LABEL, "2");
                Map<String, Object> cstParameterMap = buildWaferCstAndLotPrintParameter(materialLot, "PRINT_LOT");
                cstPrintContext.setBaseObject(materialLot);
                cstPrintContext.setParameterMap(cstParameterMap);

                if (cstPrintContext.getWorkStation().getIsClientPrint()){
                    Map<String, Object> cstparams = Maps.newHashMap();
                    cstparams = buildClientParameters(cstPrintContext);
                    mapList.add(cstparams);
                }else {
                    print(cstPrintContext);
                }

            }
            return mapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private Map<String, Object> buildWaferCstAndLotPrintParameter(MaterialLot materialLot, String type) throws ClientException {
        try {
            Map<String, Object> parameterMap = Maps.newHashMap();
            parameterMap.put("deviceID", materialLot.getMaterialName());
            if ("PRINT_LOT".equals(type)) {
                parameterMap.put("boxID",materialLot.getDurable());
            } else {
                parameterMap.put("boxID",materialLot.getLotId());
            }
            parameterMap.put("frameQty", materialLot.getCurrentSubQty().toString());
            parameterMap.put("location", materialLot.getReserved6());
            parameterMap.put("subCode", materialLot.getReserved1() + materialLot.getGrade());
            parameterMap.put("cstID", materialLot.getDurable());
            parameterMap.put("chipNum", materialLot.getCurrentQty().toString());
            List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());

            if (CollectionUtils.isNotEmpty(materialLotUnitList)) {
                for (int i = 1; i <  materialLotUnitList.size() + 1; i++) {
                    parameterMap.put("frameID" + i, materialLotUnitList.get(i - 1).getUnitId());
                    parameterMap.put("chipQty" + i, materialLotUnitList.get(i - 1).getCurrentQty());
                }
                if (materialLotUnitList.size() < 13) {
                    for (int i = materialLotUnitList.size() + 1; i < 14 ; i++) {
                        parameterMap.put("frameID" + i, StringUtils.EMPTY);
                        parameterMap.put("chipQty" + i, StringUtils.EMPTY);
                    }
                }
            }
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    @Override
    public List<Map<String, Object>> printReceiveWltCpLotLabel(List<MaterialLot> materialLotList, String printCount) throws ClientException {
        try {
            List<Map<String, Object>> mapList = Lists.newArrayList();

            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_WLT_CP_BOX_LABEL, printCount);
            for(MaterialLot materialLot : materialLotList){
                Map<String, Object> parameterMap = buildWltCpPrintParameter(materialLot);
                printContext.setBaseObject(materialLot);
                printContext.setParameterMap(parameterMap);

                if (printContext.getWorkStation().getIsClientPrint()){
                    Map<String, Object> params = Maps.newHashMap();
                    params = buildClientParameters(printContext);
                    mapList.add(params);
                }else {
                    print(printContext);
                }
            }
            return mapList;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    private Map<String,Object> buildWltCpPrintParameter(MaterialLot materialLot) throws ClientException{
        try {
            Map<String, Object> parameterMap = Maps.newHashMap();
            parameterMap.put("LOTID", materialLot.getLotId());
            parameterMap.put("DEVICEID", materialLot.getMaterialName());
            parameterMap.put("QTY", materialLot.getCurrentQty().toString());
            parameterMap.put("WAFERGRADE", materialLot.getGrade());
            parameterMap.put("LOCATION", materialLot.getReserved6());
            parameterMap.put("SUBCODE", materialLot.getReserved1());
            List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());

            if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                Integer waferQty = materialLotUnitList.size();
                parameterMap.put("WAFERQTY", waferQty.toString());
                String waferIdList1 = "";
                String waferIdList2 = "";

                for(int j = 0; j <  materialLotUnitList.size() ; j++){
                    String[] waferIdList = materialLotUnitList.get(j).getUnitId().split(StringUtils.SPLIT_CODE);
                    String waferSeq = waferIdList[1] + ",";
                    if(j < 8){
                        waferIdList1 = waferIdList1 + waferSeq;
                    } else {
                        waferIdList2 = waferIdList2 + waferSeq;
                    }
                }
                if(!StringUtils.isNullOrEmpty(waferIdList1)){
                    parameterMap.put("WAFERID1", waferIdList1);
                } else {
                    parameterMap.put("WAFERID1", StringUtils.EMPTY);
                }
                if(!StringUtils.isNullOrEmpty(waferIdList2)){
                    parameterMap.put("WAFERID2", waferIdList2);
                } else {
                    parameterMap.put("WAFERID2", StringUtils.EMPTY);
                }
            }
            return parameterMap;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 打印WLT或者CP批次标签（产线接收入库）
     * @param materialLot
     * @throws ClientException
     */
    @Override
    public Map<String, Object> printWltOrCpLabel(MaterialLot materialLot, String printCount) throws ClientException {
        try {
            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_WLT_CP_BOX_LABEL, printCount);
            Map<String, Object> parameterMap = buildWltCpPrintParameter(materialLot);
            printContext.setBaseObject(materialLot);
            printContext.setParameterMap(parameterMap);

            Map<String, Object> params = Maps.newHashMap();
            if (printContext.getWorkStation().getIsClientPrint()){
                params = buildClientParameters(printContext);
            }else {
                print(printContext);
            }
            return params;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 斜标签补打
     * @param materialLotList
     * @throws ClientException
     */
    public List<Map<String, Object>> printMaterialLotObliqueBoxLabel(List<MaterialLot> materialLotList, String expressNumber) throws ClientException{
        try {
            List<Map<String, Object>> mapList = Lists.newArrayList();

            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_OBLIQUE_BOX_LABEL, "");
            List<MaterialLot> expressNumberInfoList = materialLotList.stream().filter(materialLot -> StringUtils.isNullOrEmpty(materialLot.getExpressNumber())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(expressNumberInfoList)){
                throw new ClientParameterException(MmsException.MATERIAL_LOT_NOT_RECORD_EXPRESS, expressNumberInfoList.get(0).getMaterialLotId());
            }

            Integer seq = 1;
            Integer numfix = materialLotList.size();
            //按照称重的先后排序打印标签
            List<MaterialLot> materialLots = Lists.newArrayList();
            List<MaterialLot> mLotList = Lists.newArrayList();
            for(MaterialLot materialLot : materialLotList){
                if(StringUtils.isNullOrEmpty(materialLot.getWeightSeq())){
                    materialLots.add(materialLot);
                } else {
                    mLotList.add(materialLot);
                }
            }
            if(CollectionUtils.isNotEmpty(mLotList)){
                mLotList = mLotList.stream().sorted(Comparator.comparing(MaterialLot::getWeightSeq)).collect(Collectors.toList());
                materialLots.addAll(mLotList);
            }
            for (MaterialLot materialLot : materialLots){
                Map<String, Object> parameterMap =  Maps.newHashMap();
                if (StringUtils.isNullOrEmpty(materialLot.getReserved18())){
                    parameterMap.put("CSNAME", materialLot.getShipper());
                }else{
                    parameterMap.put("CSNAME", materialLot.getReserved18());
                }
                parameterMap.put("NUMCHANG", seq.toString());
                parameterMap.put("NUMFIX", numfix.toString());
                if(StringUtils.isNullOrEmpty(expressNumber)){
                    parameterMap.put("EXNUM", materialLot.getExpressNumber());
                }else {
                    parameterMap.put("EXNUM", expressNumber);
                }
                ++seq;
                printContext.setParameterMap(parameterMap);

                if (printContext.getWorkStation().getIsClientPrint()){
                    Map<String, Object> params = Maps.newHashMap();
                    params = buildClientParameters(printContext);
                    mapList.add(params);
                }else {
                    print(printContext);
                }
            }
            return mapList;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW的CST标签打印
     * @param materialLotList
     * @param printCount
     * @throws ClientException
     * @return
     */
    public List<Map<String, Object>> printRwLotCstLabel(List<MaterialLot> materialLotList, String printCount) throws ClientException{
        try {
            List<Map<String, Object>> mapList = Lists.newArrayList();

            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_RW_LOT_CST_LABEL, printCount);
            for(MaterialLot materialLot : materialLotList){
                Map<String, Object> parameterMap = buildRwLotCsttPrintParameter(materialLot);
                printContext.setBaseObject(materialLot);
                printContext.setParameterMap(parameterMap);
                //print(printContext);

                if (printContext.getWorkStation().getIsClientPrint()){
                    Map<String, Object> params = Maps.newHashMap();
                    params = buildClientParameters(printContext);
                    mapList.add(params);
                }else {
                    print(printContext);
                }
            }
            return mapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取RW完成品接收打印参数
     * @param materialLot
     */
    private Map<String, Object> buildRwLotCsttPrintParameter(MaterialLot materialLot) throws ClientException{
        try {
            Map<String, Object> parameterMap = Maps.newHashMap();
            List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
            String productId = materialLot.getMaterialName();
            parameterMap.put("PRODUCTID", productId.substring(0, productId.lastIndexOf("-")));
            parameterMap.put("CSTID", materialLot.getLotId());
            parameterMap.put("WAFERQTY", materialLot.getCurrentSubQty().toString());
            parameterMap.put("LOCATION", materialLot.getReserved6());
            parameterMap.put("SUBCODE", materialLot.getReserved1());
            parameterMap.put("LOIID", materialLot.getLotId());
            parameterMap.put("LOTCST", materialLot.getLotCst());
            parameterMap.put("PCODE", materialLot.getPcode());
            parameterMap.put("QTY", materialLot.getCurrentQty().toString());
            int i = 1;
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                parameterMap.put("frameID" + i, materialLotUnit.getUnitId());
                parameterMap.put("chipQty" + i, materialLotUnit.getCurrentQty().toString());
                i++;
            }
            for (int j = i; j <= 13; j++) {
                parameterMap.put("frameID" + j, StringUtils.EMPTY);
                parameterMap.put("chipQty" + j, StringUtils.EMPTY);
            }
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW的LotCst标签补打
     * @param materialLot
     * @param printCount
     * @throws ClientException
     * @return
     */
    @Override
    public Map<String, Object> rePrintRwLotCstLabel(MaterialLot materialLot, String printCount) throws ClientException {
        try {

            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_RW_LOT_CST_LABEL, printCount);
            Map<String, Object> parameterMap = buildRwLotCsttPrintParameter(materialLot);
            printContext.setBaseObject(materialLot);
            printContext.setParameterMap(parameterMap);
            //print(printContext);

            Map<String, Object> params = Maps.newHashMap();
            if (printContext.getWorkStation().getIsClientPrint()){
                params = buildClientParameters(printContext);
            }else {
                print(printContext);
            }
            return params;
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 打印COB装箱标签(一箱只有一包)
     * @param materialLot
     * @throws ClientException
     * @return
     */
    @Override
    public Map<String, Object> printCobBoxLabel(MaterialLot materialLot, String printCount) throws ClientException {
        try {
            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_COB_BOX_LABEL, printCount);
            materialLot = mmsService.getMLotByMLotId(materialLot.getMaterialLotId());
            Map<String, Object> parameterMap = Maps.newHashMap();
            parameterMap.put("BOXID", materialLot.getMaterialLotId());
            parameterMap.put("SUBCODE", materialLot.getReserved1());
            parameterMap.put("LOCATION", materialLot.getReserved6());
            parameterMap.put("DEVICEID", materialLot.getMaterialName());
            parameterMap.put("CHIPNUM", materialLot.getCurrentQty().toPlainString());

            List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
            if(CollectionUtils.isNotEmpty(packageDetailLots)){
                //COB箱号，一箱只装一个真空包
                MaterialLot packedLot = packageDetailLots.get(0);
                parameterMap.put("CSTID", packedLot.getLotId());
                parameterMap.put("FRAMEQTY", packedLot.getCurrentSubQty().toPlainString());

                List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(packedLot.getMaterialLotId());

                if(CollectionUtils.isNotEmpty(materialLotUnitList) && materialLotUnitList.size() > 13){
                    throw new ClientParameterException(MmsException.MATERIALLOT_WAFER_QTY_MORE_THAN_THIRTEEN, materialLot.getMaterialLotId());
                }

                int i = 1;
                if (CollectionUtils.isNotEmpty(materialLotUnitList)){
                    for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                        parameterMap.put("FRAMEID" + i, materialLotUnit.getUnitId());
                        parameterMap.put("CHIPQTY" + i, materialLotUnit.getCurrentQty().toPlainString());
                        i++;
                    }
                }

                for (int j = i; j <= 13; j++) {
                    parameterMap.put("FRAMEID" + j, StringUtils.EMPTY);
                    parameterMap.put("CHIPQTY" + j, StringUtils.EMPTY);
                }
            } else {
                throw new ClientParameterException(MmsException.MATERIALLOT_PACKED_DETIAL_IS_NULL, materialLot.getMaterialLotId());
            }
            printContext.setBaseObject(materialLot);
            printContext.setParameterMap(parameterMap);
//            print(printContext);

            Map<String, Object> params = Maps.newHashMap();
            if (printContext.getWorkStation().getIsClientPrint()){
                params = buildClientParameters(printContext);
            }else {
                print(printContext);
            }

            return params;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取真空包或箱号标签打印参数信息
     * COB标签只打印箱号标签，不打印真空包
     * @param materialLot
     * @throws ClientException
     */
    @Override
    public Map<String, Object> printCobBBoxLabel(MaterialLot materialLot) throws ClientException {
        try {
            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_QR_CODE_LABEL, "");
            Map<String, Object> parameterMap = Maps.newHashMap();
            SimpleDateFormat formatter = new SimpleDateFormat(MaterialLot.PRINT_DATE_PATTERN);
            String date = formatter.format(new Date());
            DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(materialLot.getReserved16()));
            String printSeq = generatorMLotsTransId(MaterialLot.GENERATOR_QRCODE_LABEL_PRINT_SEQ_RULE).substring(8, 14);
            String flow = printSeq + StringUtils.UNDERLINE_CODE  + printSeq;
            String dateAndNumber = date + StringUtils.UNDERLINE_CODE + StringUtil.leftPad(materialLot.getCurrentQty().toString() , 6 , "0");
            String twoDCode = MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE + documentLine.getReserved21() + StringUtils.UNDERLINE_CODE + dateAndNumber + StringUtils.UNDERLINE_CODE + flow;
            parameterMap.put("VENDER", MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE);
            parameterMap.put("MATERIALCODE", documentLine.getReserved21() + StringUtils.UNDERLINE_CODE);
            parameterMap.put("DATEANDNUMBER", dateAndNumber + StringUtils.UNDERLINE_CODE);
            parameterMap.put("FLOW", flow);
            parameterMap.put("BOXSEQ", "BZ");
            parameterMap.put("TWODCODE", twoDCode);
            printContext.setBaseObject(materialLot);
            printContext.setParameterMap(parameterMap);

            Map<String, Object> params = Maps.newHashMap();
            if (printContext.getWorkStation().getIsClientPrint()){
                params = buildClientParameters(printContext);
            }else {
                print(printContext);
            }
            return params;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 非COB真空包或箱号二维码标签打印
     * @param materialLot
     * @param printVboxLabelFlag
     * @throws ClientException
     * @return
     */
    @Override
    public List<Map<String, Object>> printBoxQRCodeLabel(MaterialLot materialLot, String printVboxLabelFlag) throws ClientException {
        try {
            List<Map<String, Object>> mapList = Lists.newArrayList();

            //获取当前日期，时间格式yyMMdd
            SimpleDateFormat formatter = new SimpleDateFormat(MaterialLot.PRINT_DATE_PATTERN);
            String date = formatter.format(new Date());

            //从产品上获取真空包的标准数量，用于区分真空包属于零包还是散包
            Product product = mmsService.getProductByName(materialLot.getMaterialName());
            BigDecimal packageTotalQty = new BigDecimal(product.getReserved1());
            DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(materialLot.getReserved16()));

            if(StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_QR_CODE_LABEL, "1");
                Map<String, Object> parameterMap = buildQRCodeBoxLabelParameter(materialLot, documentLine, date, packageTotalQty);
                printContext.setBaseObject(materialLot);
                printContext.setParameterMap(parameterMap);
                //print(printContext);

                if (printContext.getWorkStation().getIsClientPrint()){
                    Map<String, Object> params = Maps.newHashMap();
                    params = buildClientParameters(printContext);
                    mapList.add(params);
                }else {
                    print(printContext);
                }
            } else {
                //如果勾选打印箱中真空包标签信息，需要按照整包和零包进行分组，再按照是否打印真空包flag组装Map
                List<MaterialLot> materialLotList = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                List<MaterialLot> fullPackageMLotList = new ArrayList<>();
                List<MaterialLot> zeroPackageMLotList = new ArrayList<>();
                for(MaterialLot mLot : materialLotList){
                    if(packageTotalQty.compareTo(mLot.getCurrentQty()) > 0){
                        zeroPackageMLotList.add(mLot);
                    }else {
                        fullPackageMLotList.add(mLot);
                    }
                }
                if(MaterialLot.PRINT_CHECK.equals(printVboxLabelFlag)){
                    List<Map<String, Object>> qrCodeLabelPrintParm= Lists.newArrayList();
                    if( CollectionUtils.isNotEmpty(fullPackageMLotList)){

                        qrCodeLabelPrintParm = printQRCodeLabelPrintParmByVboxStandardQty(fullPackageMLotList, documentLine, date, MLotCodePrint.VBOXSEQ_START_VZ);
                    }
                    if( CollectionUtils.isNotEmpty(zeroPackageMLotList)){

                        qrCodeLabelPrintParm = printQRCodeLabelPrintParmByVboxStandardQty(zeroPackageMLotList, documentLine, date, MLotCodePrint.VBOXSEQ_START_VL);
                    }
                    mapList.addAll(qrCodeLabelPrintParm);

                } else {
                    List<Map<String, Object>> parameterMapList = Lists.newArrayList();
                    //不打印真空包标签也要区分散包零包的箱标签
                    if( CollectionUtils.isNotEmpty(fullPackageMLotList)){
                        parameterMapList = buildQRCodeBoxLabelPrintParmByVboxStandardQty(parameterMapList, fullPackageMLotList, documentLine, date, MLotCodePrint.BOXSEQ_START_BZ);
                    }
                    if( CollectionUtils.isNotEmpty(zeroPackageMLotList)){
                        parameterMapList = buildQRCodeBoxLabelPrintParmByVboxStandardQty(parameterMapList, zeroPackageMLotList, documentLine, date, MLotCodePrint.BOXSEQ_START_BL);
                    }
                    PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_QR_CODE_LABEL, "2");
                    for(Map<String, Object> parameterMap : parameterMapList){
                        printContext.setBaseObject(materialLot);
                        printContext.setParameterMap(parameterMap);
                        //print(printContext);

                        if (printContext.getWorkStation().getIsClientPrint()){
                            Map<String, Object> params = Maps.newHashMap();
                            params = buildClientParameters(printContext);
                            mapList.add(params);
                        }else {
                            print(printContext);
                        }
                    }
                }
            }
            return mapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 构建箱子二维码标签打印参数
     * @param materialLot
     * @param documentLine
     * @param date
     * @param packageTotalQty
     * @return
     * @throws ClientException
     */
    private Map<String,Object> buildQRCodeBoxLabelParameter(MaterialLot materialLot, DocumentLine documentLine, String date, BigDecimal packageTotalQty) throws ClientException{
        try {
            Map<String, Object> parameterMap = Maps.newHashMap();
            String boxSeq = StringUtils.EMPTY;
            String printSeq = generatorMLotsTransId(MaterialLot.GENERATOR_QRCODE_LABEL_PRINT_SEQ_RULE).substring(8, 14);
            String flow = printSeq + StringUtils.UNDERLINE_CODE  + printSeq;
            parameterMap.put("VENDER", MaterialLot.GC_CODE);
            parameterMap.put("MATERIALCODE", documentLine.getReserved21());
            String dateAndNumber = date + StringUtils.UNDERLINE_CODE + StringUtil.leftPad(materialLot.getCurrentQty().toString() , 6 , "0") + StringUtils.UNDERLINE_CODE;
            parameterMap.put("DATEANDNUMBER", dateAndNumber);
            if(packageTotalQty.compareTo(materialLot.getCurrentQty()) > 0 ){
                boxSeq = MLotCodePrint.VBOXSEQ_START_VL + materialLot.getMaterialLotId().substring(materialLot.getMaterialLotId().length() - 3);
            } else {
                boxSeq = MLotCodePrint.VBOXSEQ_START_VZ + materialLot.getMaterialLotId().substring(materialLot.getMaterialLotId().length() - 3);
            }
            String twoDCode = MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE + documentLine.getReserved21() + StringUtils.UNDERLINE_CODE + dateAndNumber + StringUtils.UNDERLINE_CODE + flow;
            parameterMap.put("FLOW", flow);
            parameterMap.put("BOXSEQ", boxSeq);
            parameterMap.put("TWODCODE", twoDCode);
            return  parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取区分整包零包之后的箱标签打印参数
     * @param materialLotList
     * @param documentLine
     * @param date
     * @param boxSeq
     * @return
     * @throws ClientException
     */
    private List<Map<String,Object>> buildQRCodeBoxLabelPrintParmByVboxStandardQty( List<Map<String, Object>> parameterMapList, List<MaterialLot> materialLotList, DocumentLine documentLine, String date, String boxSeq) throws ClientException{
        try {
            Map<String, Object> parameterMap = Maps.newHashMap();
            String printSeq = StringUtils.EMPTY;
            String flow = StringUtils.EMPTY;

            Long totalQty = materialLotList.stream().collect(Collectors.summingLong(mesPackedLot -> mesPackedLot.getCurrentQty().longValue()));
            String  startPrintSeq = "";
            int vobxQty = materialLotList.size();
            for(int i=0; i < vobxQty; i++){
                printSeq = generatorMLotsTransId(MaterialLot.GENERATOR_QRCODE_LABEL_PRINT_SEQ_RULE).substring(8, 14);
                if(StringUtils.isNullOrEmpty(startPrintSeq)){
                    startPrintSeq = printSeq;
                }
                flow = printSeq + StringUtils.UNDERLINE_CODE + printSeq;
            }
            String dateAndNumber = date + StringUtils.UNDERLINE_CODE + StringUtil.leftPad(totalQty.toString(), 6 , "0");
            String twoDCode = MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE + documentLine.getReserved21() + StringUtils.UNDERLINE_CODE + dateAndNumber + StringUtils.UNDERLINE_CODE + flow;
            parameterMap.put("VENDER", MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE);
            parameterMap.put("MATERIALCODE", documentLine.getReserved21() + StringUtils.UNDERLINE_CODE);
            parameterMap.put("DATEANDNUMBER", dateAndNumber + StringUtils.UNDERLINE_CODE);
            parameterMap.put("FLOW", flow);
            parameterMap.put("BOXSEQ", MLotCodePrint.BOXSEQ_START_BL);
            parameterMap.put("TWODCODE", twoDCode);
            parameterMapList.add(parameterMap);

            //将箱号二维码信息记录到真空包上
            for(MaterialLot mLot : materialLotList){
                mLot.setBoxQrcodeInfo(twoDCode);
                materialLotRepository.saveAndFlush(mLot);
            }
            return parameterMapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取真空包和箱号标签打印参数信息
     * 物料信息已经通过真空包标准数量分组
     * @param materialLotList
     * @param documentLine
     * @param date
     * @param boxStart
     * @return
     */
    private List<Map<String, Object>> printQRCodeLabelPrintParmByVboxStandardQty(List<MaterialLot> materialLotList, DocumentLine documentLine, String date, String boxStart)throws ClientException{
        try {
            List<Map<String, Object>> mapList = Lists.newArrayList();

            List<Map<String, Object>> parameterMapList = Lists.newArrayList();
            String dateAndNumber = StringUtils.EMPTY;
            String printSeq = StringUtils.EMPTY;
            String flow = StringUtils.EMPTY;
            String boxSeq = StringUtils.EMPTY;
            String twoDCode = StringUtils.EMPTY;

            Long fullPackageTotalQty = materialLotList.stream().collect(Collectors.summingLong(mesPackedLot -> mesPackedLot.getCurrentQty().longValue()));
            String  startPrintSeq = "";
            for(MaterialLot materialLot : materialLotList){
                Map<String, Object> parameterMap = Maps.newHashMap();
                printSeq = generatorMLotsTransId(MaterialLot.GENERATOR_QRCODE_LABEL_PRINT_SEQ_RULE).substring(8, 14);
                if(StringUtils.isNullOrEmpty(startPrintSeq)){
                    startPrintSeq = printSeq;
                }
                flow = printSeq + StringUtils.UNDERLINE_CODE + printSeq;
                dateAndNumber = date + StringUtils.UNDERLINE_CODE + StringUtil.leftPad(materialLot.getCurrentQty().toString() , 6 , "0");
                boxSeq = boxStart + materialLot.getMaterialLotId().substring(materialLot.getMaterialLotId().length() - 3);
                twoDCode = MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE + documentLine.getReserved21() + StringUtils.UNDERLINE_CODE + dateAndNumber + StringUtils.UNDERLINE_CODE + flow;
                parameterMap.put("VENDER", MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE);
                parameterMap.put("MATERIALCODE", documentLine.getReserved21() + StringUtils.UNDERLINE_CODE);
                parameterMap.put("DATEANDNUMBER", dateAndNumber + StringUtils.UNDERLINE_CODE);
                parameterMap.put("FLOW", flow);
                parameterMap.put("BOXSEQ", boxSeq);
                parameterMap.put("TWODCODE", twoDCode);
                parameterMapList.add(parameterMap);

                //将二维码信息记录到真空包上
                materialLot.setVboxQrcodeInfo(twoDCode);
                materialLotRepository.saveAndFlush(materialLot);
            }
            for(Map<String, Object> parameterMap : parameterMapList){
                PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_QR_CODE_LABEL, "1");
                printContext.setParameterMap(parameterMap);
//                print(printContext);

                if (printContext.getWorkStation().getIsClientPrint()){
                    Map<String, Object> params = buildClientParameters(printContext);
                    mapList.add(params);
                }else {
                    print(printContext);
                }
            }

            //获取箱标签信息
            Map<String, Object> parameterMap = Maps.newHashMap();
            dateAndNumber = date + StringUtils.UNDERLINE_CODE + StringUtil.leftPad( fullPackageTotalQty.toString(), 6 , "0");
            flow = startPrintSeq + StringUtils.UNDERLINE_CODE + printSeq;
            twoDCode = MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE + documentLine.getReserved21() + StringUtils.UNDERLINE_CODE + dateAndNumber + StringUtils.UNDERLINE_CODE + flow;
            parameterMap.put("VENDER", MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE);
            parameterMap.put("MATERIALCODE", documentLine.getReserved21() + StringUtils.UNDERLINE_CODE);
            parameterMap.put("DATEANDNUMBER", dateAndNumber + StringUtils.UNDERLINE_CODE);
            parameterMap.put("FLOW", flow);
            parameterMap.put("BOXSEQ", MLotCodePrint.BOXSEQ_START_BL);
            parameterMap.put("TWODCODE", twoDCode);
            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_QR_CODE_LABEL, "2");
            printContext.setParameterMap(parameterMap);

            if (printContext.getWorkStation().getIsClientPrint()){
                Map<String, Object> boxPrintParams = buildClientParameters(printContext);
                mapList.add(boxPrintParams);
            }else {
                print(printContext);
            }

            //将箱号二维码信息记录到真空包上
            for(MaterialLot materialLot : materialLotList){
                materialLot.setBoxQrcodeInfo(twoDCode);
                materialLotRepository.saveAndFlush(materialLot);
            }
            return mapList;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * WLT装箱标签打印
     * @param materialLot
     * @throws ClientException
     * @return
     */
    @Override
    public Map<String, Object> printWltBoxLabel(MaterialLot materialLot, String printCount) throws ClientException {
        try {
            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_WLT_BOX_LABEL, printCount);
            Map<String, Object> parameterMap = Maps.newHashMap();
            parameterMap.put("BOXID", materialLot.getMaterialLotId());
            String productId = materialLot.getMaterialName().substring(0, materialLot.getMaterialName().lastIndexOf("-"));
            parameterMap.put("PRODUCTID", productId);
            parameterMap.put("GRADE", materialLot.getGrade());
            parameterMap.put("SECONDCODE", materialLot.getReserved1());
            parameterMap.put("LOCATION", materialLot.getReserved6());
            parameterMap.put("QUANTITY", materialLot.getCurrentQty().toPlainString());
            parameterMap.put("NUMBER", materialLot.getCurrentSubQty().toPlainString());

            List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
            int i = 1;
            if (CollectionUtils.isNotEmpty(packageDetailLots)) {
                for (MaterialLot packedMLot : packageDetailLots) {
                    parameterMap.put("CSTID" + i, packedMLot.getLotId());
                    parameterMap.put("WAFERQTY" + i, packedMLot.getCurrentSubQty().toString());
                    List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(packedMLot.getMaterialLotId());
                    String unitIdList1 = "";
                    String unitIdList2 = "";
                    for(int j = 0; j <  materialLotUnitList.size() ; j++){
                        String[] unitIdList = materialLotUnitList.get(j).getUnitId().split(StringUtils.SPLIT_CODE);
                        String waferSeq = unitIdList[1] + ",";
                        if(j < 5){
                            unitIdList1 = unitIdList1 + waferSeq;
                        } else {
                            unitIdList2 = unitIdList2 + waferSeq;
                        }
                    }
                    if(!StringUtils.isNullOrEmpty(unitIdList1)){
                        parameterMap.put("UNITID" + i + 1 , unitIdList1);
                    } else {
                        parameterMap.put("UNITID" + i + 1 , StringUtils.EMPTY);
                    }
                    if(!StringUtils.isNullOrEmpty(unitIdList2)){
                        parameterMap.put("UNITID" + i + 2 , unitIdList2);
                    }else {
                        parameterMap.put("UNITID" + i + 2 , StringUtils.EMPTY);
                    }
                    i++;
                }
            }

            if(i <= 2){
                parameterMap.put("CSTID" + i, StringUtils.EMPTY);
                parameterMap.put("WAFERQTY" + i, StringUtils.EMPTY);
                parameterMap.put("UNITID" + i + 1, StringUtils.EMPTY);
                parameterMap.put("UNITID" + i + 2, StringUtils.EMPTY);
            }
            printContext.setBaseObject(materialLot);
            printContext.setParameterMap(parameterMap);

            Map<String, Object> params = Maps.newHashMap();
            if (printContext.getWorkStation().getIsClientPrint()){
                params = buildClientParameters(printContext);
            }else {
                print(printContext);
            }
            return params;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 来料晶圆箱标签打印
     * @param materialLotUnitList
     * @throws ClientException
     */
    @Override
    public List<Map<String, Object>> printWltBboxLabel(List<MaterialLotUnit> materialLotUnitList) throws ClientException {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        PrintContext printContext = new PrintContext();
        try {
            if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                printContext = buildPrintContext(LabelTemplate.PRINT_WLT_BBOX_LABEL, "");
                Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialLotId));
                for(String materialLotId : materialLotUnitMap.keySet()){
                    Map<String, Object> parameterMap = Maps.newHashMap();
                    MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId, true);
                    parameterMap.put("LOTID", materialLot.getLotId());
                    parameterMap.put("PRODUCTID", materialLot.getMaterialName());
                    parameterMap.put("SECONDCODE", materialLot.getReserved1());
                    parameterMap.put("LOCATION", materialLot.getReserved6());
                    parameterMap.put("VENDER", materialLot.getReserved22());
                    List<MaterialLotUnit> materialLotUnits = materialLotUnitMap.get(materialLotId);

                    List<String> waferList = Lists.newArrayList();
                    Integer waferNumber = 0;
                    if(CollectionUtils.isNotEmpty(materialLotUnits)){
                        waferNumber = materialLotUnits.size();

                        materialLotUnits = materialLotUnits.stream().sorted(Comparator.comparing(MaterialLotUnit::getUnitId)).collect(Collectors.toList());
                        for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                            String[] unitIdList = materialLotUnit.getUnitId().split(StringUtils.SPLIT_CODE);
                            String waferSeq = unitIdList[1] ;
                            waferList.add(waferSeq);
                        }
                    }

                    if(CollectionUtils.isNotEmpty(waferList)){
                        List<String> strings1 = waferList.subList(0, waferList.size() > 12 ? 12 : waferList.size());
                        parameterMap.put("WAFERLIST1", StringUtils.join(strings1, StringUtils.SPLIT_COMMA));
                    } else {
                        parameterMap.put("WAFERLIST1", StringUtils.EMPTY);
                    }
                    if(waferList.size() > 12){
                        List<String> strings2 = waferList.subList(12, waferList.size());

                        parameterMap.put("WAFERLIST2", StringUtils.join(strings2, StringUtils.SPLIT_COMMA));
                    } else {
                        parameterMap.put("WAFERLIST2", StringUtils.EMPTY);
                    }

                    parameterMap.put("GRADE", materialLot.getGrade() + "(" + waferNumber.toString() + ")");
                    parameterMap.put("QTY", materialLot.getCurrentQty().toString());

                    printContext.setBaseObject(materialLot);
                    printContext.setParameterMap(parameterMap);

                    if (printContext.getWorkStation().getIsClientPrint()){
                        Map<String, Object> params = Maps.newHashMap();
                        params = buildClientParameters(printContext);
                        mapList.add(params);
                    }else {
                        print(printContext);
                    }
                }
            }
            return mapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RMA来料接收箱标签打印
     * @param materialLotList
     * @throws ClientException
     * @return
     */
    @Override
    public List<Map<String, Object>> printRmaMaterialLotLabel(List<MaterialLot> materialLotList) throws ClientException {
        try {
            List<Map<String, Object>> mapList = Lists.newArrayList();

            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_RMA_BOX_LABEL, "");
            for(MaterialLot materialLot : materialLotList){
                Map<String, Object> parameterMap = Maps.newHashMap();
                parameterMap.put("BOXID", materialLot.getMaterialLotId());
                parameterMap.put("PRODUCTID", materialLot.getMaterialName());
                parameterMap.put("GRADE", materialLot.getGrade() + StringUtils.PARAMETER_CODE + materialLot.getCurrentQty());
                parameterMap.put("LOCATION", materialLot.getReserved6());
                parameterMap.put("SUBCODE", materialLot.getReserved1());
                parameterMap.put("PASSDIES", materialLot.getReserved34());
                parameterMap.put("NGDIES", materialLot.getReserved35());

                printContext.setBaseObject(materialLot);
                printContext.setParameterMap(parameterMap);

                if (printContext.getWorkStation().getIsClientPrint()){
                    Map<String, Object> params = Maps.newHashMap();
                    params = buildClientParameters(printContext);
                    mapList.add(params);
                }else {
                    print(printContext);
                }
            }
            return mapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COM的箱标签与客户标签的打印
     * @param materialLot
     * @param subcode
     * @throws ClientException
     * @return
     */
    @Override
    public List<Map<String, Object>> printComBoxAndCustomerLabel(MaterialLot materialLot, String subcode, String printCount) throws ClientException {
        try {
            List<Map<String, Object>> mapList = Lists.newArrayList();

            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_COM_BOX_LABEL, printCount);
            Map<String, Object> parameterMap = Maps.newHashMap();
            parameterMap.put("barcode", materialLot.getMaterialLotId());
            parameterMap.put("device", materialLot.getMaterialName());
            parameterMap.put("wafernum", materialLot.getCurrentQty().toPlainString());
            parameterMap.put("subcode", subcode);

            List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
            int i = 1;
            if (CollectionUtils.isNotEmpty(packageDetailLots)) {
                for (MaterialLot packedMLot : packageDetailLots) {
                    parameterMap.put("VBox" + i, packedMLot.getMaterialLotId());
                    i++;
                }
            }
            for (int j = i; j <= 10; j++) {
                parameterMap.put("VBox" + j, StringUtils.EMPTY);
            }
            printContext.setBaseObject(materialLot);
            printContext.setParameterMap(parameterMap);
            if (printContext.getWorkStation().getIsClientPrint()){
                Map<String, Object> params = Maps.newHashMap();
                params = buildClientParameters(printContext);
                mapList.add(params);
            }else {
                print(printContext);
            }

            if (!StringUtils.isNullOrEmpty(materialLot.getReserved18()) || !StringUtils.isNullOrEmpty(materialLot.getShipper())){
                Map<String, Object> customerLabelParams = printCustomerNameLabel(materialLot);
                mapList.add(customerLabelParams);
            }
            return mapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 打印客户标签
     * @param materialLot
     * @throws ClientException
     */
    private Map<String, Object> printCustomerNameLabel(MaterialLot materialLot) throws ClientException{
        try {
            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_CUSTOMER_NAME_LABEL, "");
            Map<String, Object> parameterMap = Maps.newHashMap();
            parameterMap.put("CSNAME",materialLot.getReserved18() == null ? materialLot.getShipper() : materialLot.getReserved18());
            printContext.setBaseObject(materialLot);
            printContext.setParameterMap(parameterMap);

            Map<String, Object> params = Maps.newHashMap();
            if (printContext.getWorkStation().getIsClientPrint()){
                params = buildClientParameters(printContext);
            }else {
                print(printContext);
            }
            return params;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW打印CST标签
     * @param materialLot
     * @throws ClientException
     * @return
     */
    @Override
    public Map<String, Object> printRwCstLabel(MaterialLot materialLot, String printCount) throws ClientException {
        try {
            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_RW_CST_BOX_LABEL, printCount);
            Map<String, Object> parameterMap = Maps.newHashMap();
            String deviceID = materialLot.getMaterialName().substring(0, materialLot.getMaterialName().lastIndexOf("-"));
            parameterMap.put("DeviceID", deviceID);
            parameterMap.put("BoxID", materialLot.getMaterialLotId());
            parameterMap.put("Qty", materialLot.getCurrentQty().toPlainString());
            parameterMap.put("BP", materialLot.getReserved6());
            if(materialLot.getGrade().contains("A")){
                parameterMap.put("SubCode", "A" + materialLot.getReserved1());
            } else {
                parameterMap.put("SubCode", materialLot.getGrade() + materialLot.getReserved1());
            }
            if(StringUtils.isNullOrEmpty(materialLot.getPcode())) {
                parameterMap.put("Pcode", StringUtils.EMPTY);
            } else {
                parameterMap.put("Pcode", materialLot.getPcode());
            }
            List<MaterialLot> materialLotDetails = materialLotRepository.getByParentMaterialLotId(materialLot.getMaterialLotId());
            if (CollectionUtils.isNotEmpty(materialLotDetails)) {
                MaterialLot materialLotDetail = materialLotDetails.get(0);
                if(StringUtils.isNullOrEmpty(materialLotDetail.getLotCst())){
                    parameterMap.put("LotID", StringUtils.EMPTY);
                } else {
                    parameterMap.put("LotID", materialLotDetail.getLotCst());
                }
                parameterMap.put("CSTID", materialLotDetail.getLotId());
                List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLotDetail.getMaterialLotId());
                parameterMap.put("FrameQty", ""+materialLotUnitList.size());
                for(int i=0; i<materialLotUnitList.size(); i++){
                    parameterMap.put("FrameID" + i, "" + materialLotUnitList.get(i).getUnitId());
                    parameterMap.put("ChipQty" + i, "" + materialLotUnitList.get(i).getCurrentQty());
                }
                for(int j=materialLotUnitList.size();j < 13;j++){
                    parameterMap.put("FrameID" + j, StringUtils.EMPTY);
                    parameterMap.put("ChipQty" + j, StringUtils.EMPTY);
                }
            }
            printContext.setBaseObject(materialLot);
            printContext.setParameterMap(parameterMap);
            //print(printContext);

            Map<String, Object> clientParameters = Maps.newHashMap();
            if (printContext.getWorkStation().getIsClientPrint()){
                clientParameters = buildClientParameters(printContext);
            }else {
                print(printContext);
            }
            return clientParameters;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW出货标签打印
     * @param materialLot
     * @throws ClientException
     * @return
     */
    @Override
    public Map<String, Object> printRwStockOutLabel(MaterialLot materialLot) throws ClientException {
        try {
            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_RW_STOCK_OUT_LABEL, "");
            Map<String, Object> parameterMap = Maps.newHashMap();
            parameterMap.put("DeviceNo", materialLot.getMaterialName());
            parameterMap.put("PN", "");
            parameterMap.put("Qty", materialLot.getCurrentQty().toPlainString());
            parameterMap.put("WaferLotNo", materialLot.getLotCst());
            parameterMap.put("assyPN","");
            parameterMap.put("assyLotNo", materialLot.getLotId());
            parameterMap.put("shipLotNo", materialLot.getLotCst());
            parameterMap.put("DC", setYearWeek());
            List<MaterialLot> materialLotDetails = materialLotRepository.getByParentMaterialLotId(materialLot.getMaterialLotId());
            if (CollectionUtils.isNotEmpty(materialLotDetails)) {
                MaterialLot materialLotDetail = materialLotDetails.get(0);
                List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLotDetail.getMaterialLotId());
                parameterMap.put("FrameSlice", String.valueOf(materialLotUnitList.size()));
            }
            printContext.setBaseObject(materialLot);
            printContext.setParameterMap(parameterMap);
            //print(printContext);

            Map<String, Object> clientParameters = Maps.newHashMap();
            if (printContext.getWorkStation().getIsClientPrint()){
                clientParameters = buildClientParameters(printContext);
            }else {
                print(printContext);
            }
            return clientParameters;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW物料发料标签打印
     * @param materialLotList
     * @throws ClientException
     * @return
     */
    @Override
    public List<Map<String, Object>> printRwLotIssueLabel(List<MaterialLot> materialLotList, String printCount) throws ClientException {
        try {
            List<Map<String, Object>> mapList = Lists.newArrayList();

            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_RW_LOT_ISSUE_LABEL, printCount);
            SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
            String date = formatter.format(new Date());
            for(MaterialLot materialLot : materialLotList){
                List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
                Map<String, Object> parameter = Maps.newHashMap();
                parameter.put("CUSTOMER", MLotCodePrint.PRINT_GALAXYCORE);
                parameter.put("WAFERCOUNT", Integer.toString(materialLotUnitList.size()));
                parameter.put("MATERIALNAME", materialLot.getMaterialName());
                parameter.put("LOTID", materialLot.getLotId());
                parameter.put("PRODUCTID", materialLot.getMaterialName());
                parameter.put("INNERLOTID", materialLot.getInnerLotId());
                parameter.put("WAFERQTY", materialLot.getCurrentQty().toString());
                parameter.put("PLANTIME", materialLot.getWorkOrderPlanputTime());
                String innerLotInfo = MLotCodePrint.COMPANY_INITIALS_G + date + materialLot.getInnerLotId();
                parameter.put("INNERLOTINFO", innerLotInfo );
                printContext.setBaseObject(materialLot);
                printContext.setParameterMap(parameter);
                //print(printContext);

                if (printContext.getWorkStation().getIsClientPrint()){
                    Map<String, Object> params = Maps.newHashMap();
                    params = buildClientParameters(printContext);
                    mapList.add(params);
                }else {
                    print(printContext);
                }
            }
            return mapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW出货箱标签打印
     * @param materialLot
     * @throws ClientException
     */
    @Override
    public Map<String, Object> printRWBoxLabel(MaterialLot materialLot) throws ClientException {
        try {
            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_RW_BOX_LABEL, "");
            Map<String, Object> parameterMap = Maps.newHashMap();
            SimpleDateFormat format = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
            Calendar.getInstance().setTime(format.parse(format.format(new Date())));
            Integer year = Calendar.getInstance().get(Calendar.YEAR) % 1000;
            String years = year.toString();
            Integer week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
            String weeks = week.toString();
            if (week < 10){
                weeks = "0" + weeks;
            }
            String deviceNo = materialLot.getMaterialName().substring(0, materialLot.getMaterialName().lastIndexOf("-"));
            parameterMap.put("DeviceNo", deviceNo);
            parameterMap.put("PN", deviceNo);
            parameterMap.put("WaferLotNo", materialLot.getLotCst());
            parameterMap.put("AssyPN", deviceNo);
            parameterMap.put("AssyLotNo", materialLot.getInnerLotId());
            parameterMap.put("ShipLotNo", materialLot.getLotCst());
            parameterMap.put("Qty", materialLot.getCurrentQty());
            parameterMap.put("DC", years + weeks);
            parameterMap.put("FrameSlice", materialLot.getCurrentSubQty());
            printContext.setBaseObject(materialLot);
            printContext.setParameterMap(parameterMap);
            Map<String, Object> clientParameters = Maps.newHashMap();
            if (printContext.getWorkStation().getIsClientPrint()){
                clientParameters = buildClientParameters(printContext);
            }else {
                print(printContext);
            }
            return clientParameters;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取周信息
     * @return
     * @throws ClientException
     */
    private String setYearWeek() throws ClientException{
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
            Calendar cl = Calendar.getInstance();
            cl.setTime(sdf.parse(sdf.format(new Date())));
            int week = cl.get(Calendar.WEEK_OF_YEAR);
            cl.add(Calendar.DAY_OF_MONTH,-7);
            int year = cl.get(Calendar.YEAR);
            if(week < cl.get(Calendar.WEEK_OF_YEAR)){
                year += 1;
            }
            String yearWeek = String.valueOf(year + week);
            return yearWeek;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 真空包标签补打（打印真空包种的盒子信息）
     * @param parameterMapList
     * @throws ClientException
     * @return
     */
    @Override
    public List<Map<String, Object>> rePrintVBxoLabel(List<Map<String, Object>> parameterMapList) throws ClientException {
        try {
            List<Map<String, Object>> mapList = Lists.newArrayList();

            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_COM_VBOX_LABEL, "");
            for(Map<String, Object> parameterMap : parameterMapList){
                printContext.setParameterMap(parameterMap);

                if (printContext.getWorkStation().getIsClientPrint()){
                    Map<String, Object> params = Maps.newHashMap();
                    params = buildClientParameters(printContext);
                    mapList.add(params);
                }else {
                    print(printContext);
                }
            }
            return mapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    @Override
    public List<Map<String, Object>> printRawMlotIRLabel(List<MaterialLot> materialLots) throws ClientException {
        try {
            List<Map<String, Object>> mapList = Lists.newArrayList();
            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_IR_LABEL, "");
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DEFAULT_DATETIME_PATTERN);
            for (MaterialLot materialLot : materialLots) {
                Map<String, Object> parameterMap = Maps.newHashMap();
                parameterMap.put("VENDER", materialLot.getReserved22());
                parameterMap.put("MATERIALNAME", materialLot.getMaterialName());
                parameterMap.put("MATERIALLOTID", materialLot.getMaterialLotId());
                parameterMap.put("QTY", materialLot.getCurrentQty().toString());
                parameterMap.put("MFGDATE", sdf.format(materialLot.getMfgDate()));
                parameterMap.put("EXPDATE", sdf.format(materialLot.getExpDate()));
                parameterMap.put("REMARK", materialLot.getReserved41() == null ? "": materialLot.getReserved41());
                printContext.setBaseObject(materialLot);
                printContext.setParameterMap(parameterMap);

                if (printContext.getWorkStation().getIsClientPrint()){
                    Map<String, Object> params = Maps.newHashMap();
                    params = buildClientParameters(printContext);
                    mapList.add(params);
                }else {
                    print(printContext);
                }
            }
            return mapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    @Override
    public List<Map<String, Object>> printRawMlotGlueLabel(List<MaterialLot> materialLots) throws ClientException {
        try {
            List<Map<String, Object>> mapList = Lists.newArrayList();
            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_GLUE_LABEL, "");
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
            for (MaterialLot materialLot : materialLots) {
                Map<String, Object> parameterMap = Maps.newHashMap();
                parameterMap.put("GLUENAME", materialLot.getMaterialLotId());
                parameterMap.put("GLUETYPE", materialLot.getMaterialName());
                parameterMap.put("DESCRIPTION", materialLot.getMaterialDesc() == null ? "": materialLot.getMaterialDesc());
                parameterMap.put("PRODUCTIONDATE", sdf.format(materialLot.getMfgDate()));
                parameterMap.put("EFFECTIVEDATE", sdf.format(materialLot.getExpDate()));
                parameterMap.put("WEIGHT", materialLot.getCurrentQty().toString());
                printContext.setBaseObject(materialLot);
                printContext.setParameterMap(parameterMap);

                if (printContext.getWorkStation().getIsClientPrint()){
                    Map<String, Object> params = Maps.newHashMap();
                    params = buildClientParameters(printContext);
                    mapList.add(params);
                }else {
                    print(printContext);
                }
            }
            return mapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    @Override
    public List<Map<String, Object>> printIRABoxLabel(List<MaterialLot> materialLots) throws ClientException {
        try {
            List<Map<String, Object>> mapList = Lists.newArrayList();

            PrintContext printContext = buildPrintContext(LabelTemplate.PRINT_IRA_BOX_LABEL, "");
            Map<String,List<MaterialLot>> materialLotMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getLotId));
            Map<String, List<MaterialLot>> sortMap = materialLotMap.entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(oldValue, newValue) -> oldValue, LinkedHashMap::new));
            for (String lotId : sortMap.keySet()) {
                List<MaterialLot> materialLotList = materialLotMap.get(lotId);
                String grossQty = materialLotList.stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentQty().longValue())).toString();
                Map<String, Object> parameterMap = Maps.newHashMap();
                parameterMap.put("BOXID", lotId);
                parameterMap.put("GROSSQTY", grossQty);
                parameterMap.put("MATERIALNAME", materialLotList.get(0).getMaterialName());
                parameterMap.put("QTY", String.valueOf(materialLotList.size()));
                parameterMap.put("VENDER", materialLotList.get(0).getReserved22());
                printContext.setParameterMap(parameterMap);

                if (printContext.getWorkStation().getIsClientPrint()){
                    Map<String, Object> params = Maps.newHashMap();
                    params = buildClientParameters(printContext);
                    mapList.add(params);
                }else {
                    print(printContext);
                }
            }
            return mapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    @Override
    public void printOphelionMLotLabel(List<Map<String, String>> mapList) throws ClientException {
        for (Map<String, String> stringStringMap : mapList) {
            PrintContext printContext = buildMaterialCodePrintContext(stringStringMap, LabelTemplate.PRINT_OPHELION_MLOT_LABEL);
            print(printContext);
        }
    }

    @Override
    public void printBaichenMLotLabel(List<Map<String, String>> mapList) throws ClientException {
        for (Map<String, String> stringStringMap : mapList) {
            PrintContext printContext = buildMaterialCodePrintContext(stringStringMap, LabelTemplate.PRINT_BAI_CHEN_MLOT_LABEL);
            print(printContext);
        }
    }

    @Override
    public void printGuangBaoVBoxLabel(List<Map<String, String>> mapList) throws ClientException {
        for (Map<String, String> stringStringMap : mapList) {
            PrintContext printContext = buildMaterialCodePrintContext(stringStringMap, LabelTemplate.PRINT_GUANG_BAO_VBOX_LABEL);
            print(printContext);
        }
    }

    @Override
    public void printCobGuangBaoLabel(List<Map<String, String>> mapList) throws ClientException {
        for (Map<String, String> stringStringMap : mapList) {
            PrintContext printContext = buildMaterialCodePrintContext(stringStringMap, LabelTemplate.PRINT_COB_GUANG_BAO_LABEL);
            print(printContext);
        }
    }

    @Override
    public void printHuatianLabel(List<Map<String, String>> mapList) throws ClientException {
        for (Map<String, String> stringStringMap : mapList) {
            PrintContext printContext = buildMaterialCodePrintContext(stringStringMap, LabelTemplate.PRINT_HUA_TIAN_LABEL);
            print(printContext);
        }
    }

    @Override
    public void printShengTaiVBoxLabel(List<Map<String, String>> mapList) throws ClientException {
        for (Map<String, String> stringStringMap : mapList) {
            PrintContext printContext = buildMaterialCodePrintContext(stringStringMap, LabelTemplate.PRINT_SHENG_TAI_VBOX_LABEL);
            print(printContext);
        }
    }

    @Override
    public void prinBydLabel(List<Map<String, String>> mapList) throws ClientException {
        for (Map<String, String> stringStringMap : mapList) {
            PrintContext printContext = buildMaterialCodePrintContext(stringStringMap, LabelTemplate.PRINT_BYD_LABEL);
            print(printContext);
        }
    }

    @Override
    public void printXLGDBoxLabel(List<Map<String, String>> mapList) throws ClientException {
        for (Map<String, String> stringStringMap : mapList) {
            PrintContext printContext = buildMaterialCodePrintContext(stringStringMap, LabelTemplate.PRINT_XLGD_BOX_LABEL);
            print(printContext);
        }
    }

    @Override
    public void printShunYuLabel(List<Map<String, String>> mapList) throws ClientException {
        for (Map<String, String> stringStringMap : mapList) {
            PrintContext printContext = buildMaterialCodePrintContext(stringStringMap, LabelTemplate.PRINT_SHUN_YU_LABEL);
            print(printContext);
        }
    }

    @Override
    public void printZhongKongLabel(List<Map<String, String>> mapList) throws ClientException {
        for (Map<String, String> stringStringMap : mapList) {
            PrintContext printContext = buildMaterialCodePrintContext(stringStringMap, LabelTemplate.PRINT_ZHONG_KONG_LABEL);
            print(printContext);
        }
    }

    @Override
    public void printXingZhiMLotLabel(List<Map<String, String>> mapList) throws ClientException {
        for (Map<String, String> stringStringMap : mapList) {
            PrintContext printContext = buildMaterialCodePrintContext(stringStringMap, LabelTemplate.PRINT_XING_ZHI_MLOT_LABEL);
            print(printContext);
        }
    }

    /**
     * //一般物料标签/光宝箱/盛泰箱
     * @param mapList
     * @throws ClientException
     */
    @Override
    public void PrintGeneralMLotLabel(List<Map<String, String>> mapList) throws ClientException {
        for (Map<String, String> stringStringMap : mapList) {
            PrintContext printContext = buildMaterialCodePrintContext(stringStringMap, LabelTemplate.PRINT_GENERAL_MLOT_LABEL);
            print(printContext);
        }
    }

    /**
     *
     * @param parameterMap
     * @param labelTemplateName
     * @return
     * @throws ClientException
     */
    private PrintContext buildMaterialCodePrintContext(Map<String, String> parameterMap, String labelTemplateName) throws ClientException{
        try {
            //Map<String, String> => Map<String, Object>
            Map tempMap = parameterMap;
            Map<String,Object> destMap = tempMap;
            PrintContext printContext = buildPrintContext(labelTemplateName, "");
            printContext.setParameterMap(destMap);
            return printContext;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }


}
