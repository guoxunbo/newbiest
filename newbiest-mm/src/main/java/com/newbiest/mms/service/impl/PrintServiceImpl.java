package com.newbiest.mms.service.impl;

import com.google.common.collect.Maps;
import com.newbiest.base.annotation.BaseJpaFilter;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.print.DefaultPrintStrategy;
import com.newbiest.mms.print.IPrintStrategy;
import com.newbiest.mms.print.PrintContext;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PrintService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author guoxunbo
 * @date 4/6/21 3:03 PM
 */
@Service
@Slf4j
@Transactional
@BaseJpaFilter
@Data
@Async
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

    private static final String LABEL_TEMPLATE_NAME_PRINT_BOX_MLOT = "PrintBoxMLot";
    private static final String LABEL_TEMPLATE_NAME_PRINT_MLOT = "PrintMLot";

    PrintContext buildPrintContext(Object baseObject, String labelTemplateName, Map<String, Object> parameterMap) throws ClientException{
        try {
            WorkStation workStation = workStationRepository.findByIpAddress(ThreadLocalContext.getTransactionIp());
            if (workStation == null) {
                throw new ClientParameterException(MmsException.MM_WORK_STATION_IS_NOT_EXIST, ThreadLocalContext.getTransactionIp());
            }
            LabelTemplate labelTemplate = labelTemplateRepository.findOneByName(labelTemplateName);
            if (labelTemplate == null) {
                throw new ClientParameterException(MmsException.MM_LBL_TEMPLATE_IS_NOT_EXIST);
            }
            List<LabelTemplateParameter> parameterList = labelTemplateParameterRepository.findByLblTemplateRrn(labelTemplate.getObjectRrn());
            labelTemplate.setLabelTemplateParameterList(parameterList);

            PrintContext printContext = new PrintContext();
            printContext.setBaseObject(baseObject);
            printContext.setLabelTemplate(labelTemplate);
            printContext.setWorkStation(workStation);
            printContext.setParameterMap(parameterMap);

            return printContext;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 外箱标签打印
     * @param subBoxMLotId
     * @throws ClientException
     */
    @Override
    @Async
    public void printBoxMLot(String subBoxMLotId) throws ClientException {
        try {
            Map<String, Object> parameterMap = buildBoxParameterMap(subBoxMLotId);

            PrintContext printContext = buildPrintContext(null, LABEL_TEMPLATE_NAME_PRINT_BOX_MLOT, parameterMap);

            print(printContext);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获得外箱标签参数
     * @param subBoxMLotId
     * @return
     * @throws ClientException
     */
    public Map<String, Object> buildBoxParameterMap(String subBoxMLotId) throws ClientException{
        try {
            Map<String, Object> parameterMap = Maps.newHashMap();

            MaterialLot materialLot = mmsService.getMLotByMLotId(subBoxMLotId,true);
            MaterialLot boxMaterialLot = mmsService.getMLotByMLotId(materialLot.getBoxMaterialLotId(),true);

            DocumentLine deliveryOrderLine = documentLineRepository.findByObjectRrn(boxMaterialLot.getReserved44());
            String shippingDate = StringUtils.EMPTY;
            if (deliveryOrderLine.getShippingDate() != null){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                shippingDate = simpleDateFormat.format(deliveryOrderLine.getShippingDate());
            }

            parameterMap.put("from", deliveryOrderLine.getReserved11());
            parameterMap.put("to", deliveryOrderLine.getReserved15());
            parameterMap.put("toAdd", deliveryOrderLine.getReserved16());
            parameterMap.put("deliveryNumber", deliveryOrderLine.getReserved21());
            parameterMap.put("shippingDate", shippingDate);
            parameterMap.put("poNumber", deliveryOrderLine.getReserved20());

            String boxMaterialLotId = boxMaterialLot.getMaterialLotId();
            String boxNumber = boxMaterialLotId.substring(0, boxMaterialLotId.indexOf(StringUtils.SPLIT_CODE));

            //该单据总的箱数,查询箱同一单据下已经装箱的
            List<MaterialLot> materialLots = materialLotRepository.findByReserved44(deliveryOrderLine.getObjectRrn());
            materialLots = materialLots.stream().filter(mlot-> (!StringUtils.isNullOrEmpty(mlot.getCategory()) && StringUtils.YES.equals(mlot.getCategory()))).collect(Collectors.toList());

            StringBuffer qRCode = new StringBuffer();
            qRCode.append(boxMaterialLot.getReserved2());
            qRCode.append(StringUtils.SPLIT_COMMA);
            qRCode.append(boxMaterialLot.getCurrentQty().toPlainString());
            List<MaterialLot> reelMLots = materialLotRepository.findByBoxMaterialLotId(boxMaterialLotId);

            for (MaterialLot reelMLot: reelMLots) {
                qRCode.append(StringUtils.SPLIT_COMMA);
                qRCode.append(reelMLot.getMaterialLotId());

                qRCode.append(StringUtils.SPLIT_COMMA);
                qRCode.append(reelMLot.getReserved9());
            }

            BigDecimal netWeight = new BigDecimal(boxMaterialLot.getReserved12()).setScale(3, BigDecimal.ROUND_HALF_UP);
            BigDecimal grossWeight = new BigDecimal(boxMaterialLot.getReserved13()).setScale(3, BigDecimal.ROUND_HALF_UP);

            BigDecimal totalBoxQty = new BigDecimal(materialLots.size());
            parameterMap.put("partNumber", boxMaterialLot.getReserved2());
            parameterMap.put("quantity", boxMaterialLot.getCurrentQty().toPlainString());
            parameterMap.put("boxNumber", boxNumber);
            parameterMap.put("totalBoxNumber", totalBoxQty);
            parameterMap.put("grossWeight", grossWeight.toPlainString());
            parameterMap.put("netWeight", netWeight.toPlainString());
            parameterMap.put("countOfOrigin", "China");
            parameterMap.put("boxId", boxMaterialLot.getMaterialLotId());
            parameterMap.put("qRCode", qRCode);
            parameterMap.put("printNumber", 1 + StringUtils.EMPTY);

            return parameterMap;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 打标签进行异步构建
     * @param materialLot
     * @throws ClientException
     */
    @Override
    @Async
    public void printMLot(MaterialLot materialLot) throws ClientException {
        try {
            Material material = materialRepository.findOneByName(materialLot.getMaterialName());
            String iqcFlag = "检验";
            if (StringUtils.isNullOrEmpty(material.getIqcSheetName())){
                iqcFlag = "免检";
            }

            Map<String, Object> parameterMap = Maps.newHashMap();
            parameterMap.put("qty", materialLot.getCurrentQty());
            parameterMap.put("inDate", DateUtils.now());
            parameterMap.put("specification", materialLot.getMaterialDesc());
            parameterMap.put("iqcFlag", iqcFlag);

            PrintContext printContext = buildPrintContext(materialLot, LABEL_TEMPLATE_NAME_PRINT_MLOT, parameterMap);

            print(printContext);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }

    }

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

}
