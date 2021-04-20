package com.newbiest.mms.service.impl;

import com.google.common.collect.Maps;
import com.newbiest.base.annotation.BaseJpaFilter;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.LabelTemplate;
import com.newbiest.mms.model.LabelTemplateParameter;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.WorkStation;
import com.newbiest.mms.print.DefaultPrintStrategy;
import com.newbiest.mms.print.IPrintStrategy;
import com.newbiest.mms.print.PrintContext;
import com.newbiest.mms.repository.LabelTemplateParameterRepository;
import com.newbiest.mms.repository.LabelTemplateRepository;
import com.newbiest.mms.repository.WorkStationRepository;
import com.newbiest.mms.service.PrintService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    /**
     * 打标签进行异步构建
     * @param materialLot
     * @throws ClientException
     */
    @Override
    @Async
    public void printMLot(MaterialLot materialLot) throws ClientException {
        try {
            WorkStation workStation = workStationRepository.findByIpAddress(ThreadLocalContext.getTransactionIp());
            if (workStation == null) {
                throw new ClientParameterException(MmsException.MM_WORK_STATION_IS_NOT_EXIST, ThreadLocalContext.getTransactionIp());
            }
            LabelTemplate labelTemplate = labelTemplateRepository.findOneByName("PrintMLot");
            if (labelTemplate == null) {
                throw new ClientParameterException(MmsException.MM_LBL_TEMPLATE_IS_NOT_EXIST);
            }
            List<LabelTemplateParameter> parameterList = labelTemplateParameterRepository.findByLblTemplateRrn(labelTemplate.getObjectRrn());
            labelTemplate.setLabelTemplateParameterList(parameterList);

            PrintContext printContext = new PrintContext();
            printContext.setBaseObject(materialLot);
            printContext.setLabelTemplate(labelTemplate);
            printContext.setWorkStation(workStation);

            Map<String, Object> parameterMap = Maps.newHashMap();
            parameterMap.put("qty", materialLot.getCurrentQty());
            parameterMap.put("inDate", DateUtils.now());
            parameterMap.put("specification", materialLot.getMaterialDesc());
            printContext.setParameterMap(parameterMap);

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
