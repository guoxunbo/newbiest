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
import com.newbiest.mms.print.IPrintStrategy;
import com.newbiest.mms.print.PrintContext;
import com.newbiest.mms.repository.LabelTemplateParameterRepository;
import com.newbiest.mms.repository.LabelTemplateRepository;
import com.newbiest.mms.repository.WorkStationRepository;
import com.newbiest.mms.service.PrintService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PrintServiceImpl implements PrintService {

    public static final String PRINT_MATERIAL_LOT = "printMLot";

    @Autowired
    Map<String, IPrintStrategy> printStrategyMap;

    @Autowired
    WorkStationRepository workStationRepository;

    @Autowired
    LabelTemplateRepository labelTemplateRepository;

    @Autowired
    LabelTemplateParameterRepository labelTemplateParameterRepository;

    @Override
    public void printMLot(MaterialLot materialLot) throws ClientException {
        try {
            String transactionIp = ThreadLocalContext.getTransactionIp();
            WorkStation workStation = workStationRepository.findByIpAddress(transactionIp);
            if (workStation == null) {
                throw new ClientParameterException(MmsException.MM_WORK_STATION_IS_NOT_EXIST, transactionIp);
            }
            LabelTemplate labelTemplate = labelTemplateRepository.findOneByName("PrintMLot");
            if (labelTemplate == null) {
                throw new ClientParameterException(MmsException.MM_LBL_TEMPLATE_IS_NOT_EXIST, transactionIp);
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

            IPrintStrategy printStrategy = printStrategyMap.get("PrintMLot");
            if (printStrategy == null) {
                printStrategy = printStrategyMap.get("defaultPrintStrategy");
            }
//            IPrintStrategy printStrategy = new DefaultPrintStrategy();
            printStrategy.print(printContext);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }

    }

}
