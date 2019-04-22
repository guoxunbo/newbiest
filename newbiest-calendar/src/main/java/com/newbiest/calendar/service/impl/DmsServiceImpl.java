package com.newbiest.calendar.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.calendar.exception.DmsException;
import com.newbiest.calendar.model.ChangeShift;
import com.newbiest.calendar.model.ChangeShiftHistory;
import com.newbiest.calendar.repository.ChangeShiftRepository;
import com.newbiest.calendar.service.DmsService;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by guoxunbo on 2019/4/22.
 */
@Component
@Transactional
@Slf4j
public class DmsServiceImpl implements DmsService{

    @Autowired
    BaseService baseService;

    @Autowired
    GeneratorService generatorService;

    @Autowired
    ChangeShiftRepository changeShiftRepository;

    public ChangeShift saveChangeShift(ChangeShift changeShift, SessionContext sc) throws ClientException{
        try {
            if (changeShift.getObjectRrn() == null) {
                String name = generatorChangeShiftName(changeShift, sc);
                changeShift.setName(name);
            }
            return (ChangeShift) baseService.saveEntity(changeShift, sc);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    public ChangeShift closeChangeShift(ChangeShift changeShift, SessionContext sc) throws ClientException{
        try {
            if (!ChangeShift.STATUS_CREATE.equals(changeShift.getStatus())) {
                throw new ClientException(DmsException.CHANGE_SHIFT_STATUS_NOT_ALLOW);
            }
            changeShift.setStatus(ChangeShift.STATUS_CLOSE);
            changeShift = changeShiftRepository.saveAndFlush(changeShift);
            baseService.saveHistoryEntity(changeShift, ChangeShiftHistory.TRANS_TYPE_CLOSE, sc);
            return changeShift;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public ChangeShift openChangeShift(ChangeShift changeShift, SessionContext sc) throws ClientException{
        try {
            if (!ChangeShift.STATUS_CLOSE.equals(changeShift.getStatus())) {
                throw new ClientException(DmsException.CHANGE_SHIFT_STATUS_NOT_ALLOW);
            }
            changeShift.setStatus(ChangeShift.STATUS_CREATE);
            changeShift = changeShiftRepository.saveAndFlush(changeShift);
            baseService.saveHistoryEntity(changeShift, ChangeShiftHistory.TRANS_TYPE_CLOSE, sc);
            return changeShift;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public String generatorChangeShiftName(ChangeShift changeShift, SessionContext sc) throws ClientException{
        try {
            GeneratorContext generatorContext = new GeneratorContext();
            generatorContext.setRuleName(ChangeShift.GENERATOR_NAME_RULE);
            generatorContext.setObject(changeShift);
            String name = generatorService.generatorId(sc.getOrgRrn(), generatorContext);
            return name;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
}
