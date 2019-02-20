package com.newbiest.commom.sm.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.commom.sm.repository.StatusModelRepository;
import com.newbiest.commom.sm.service.StatusMachineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by guoxunbo on 2018/8/10.
 */
@Service
@Transactional
@Slf4j
public class StatusMachineServiceImpl implements StatusMachineService {

    @Autowired
    StatusModelRepository statusModelRepository;

    public StatusModel getStatusModelByObjectRrn(long objectRrn) throws ClientException {
        try {
            return (StatusModel) statusModelRepository.findByObjectRrn(objectRrn);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public StatusModel getStatusModelByName(String name, SessionContext sc) throws ClientException {
        try {
            List<StatusModel> materialStatusModelList = (List<StatusModel>) statusModelRepository.findByNameAndOrgRrn(name, sc.getOrgRrn());
            if (CollectionUtils.isNotEmpty(materialStatusModelList)) {
                return materialStatusModelList.get(0);
            }
            return null;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public StatusModel saveStatusModel(StatusModel statusModel) throws ClientException {
        try {
            return statusModelRepository.save(statusModel);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

}
