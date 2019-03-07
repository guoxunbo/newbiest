package com.newbiest.commom.sm.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.commom.sm.exception.StatusMachineExceptions;
import com.newbiest.commom.sm.model.Event;
import com.newbiest.commom.sm.model.EventStatus;
import com.newbiest.commom.sm.model.StatusLifeCycle;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.commom.sm.repository.StatusModelRepository;
import com.newbiest.commom.sm.service.StatusMachineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
            List<StatusModel> statusModelList = (List<StatusModel>) statusModelRepository.findByNameAndOrgRrn(name, sc.getOrgRrn());
            if (CollectionUtils.isNotEmpty(statusModelList)) {
                return statusModelList.get(0);
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

    /**
     * 触发事件，根据事件修改其目标状态
     * @param statusLifeCycle 受状态机管控的对象
     * @param eventId 事件
     * @param targetStatus 目标状态，如果指定则强转至目标状态
     * @param sc
     * @return
     * @throws ClientException
     */
    public StatusLifeCycle triggerEvent(StatusLifeCycle statusLifeCycle, String eventId, String targetStatus, SessionContext sc) throws ClientException {
        try {
            StatusModel statusModel = getStatusModelByObjectRrn(statusLifeCycle.getStatusModelRrn());
            Optional<Event> optional = statusModel.getEvents().stream().filter(event1 -> event1.getName().equals(eventId)).findFirst();
            if (!optional.isPresent()) {
                throw new ClientParameterException(StatusMachineExceptions.STATUS_MODEL_EVENT_IS_NOT_EXIST, eventId);
            }
            Event event = optional.get();
            EventStatus matchStatus = getMatchStatus(statusLifeCycle, targetStatus, event);
            if (matchStatus == null) {
                throw new ClientException(StatusMachineExceptions.EVENT_STATUS_IS_NOT_ALLOW);
            }
            statusLifeCycle.setPreStatusCategory(statusLifeCycle.getStatusCategory());
            statusLifeCycle.setPreStatus(statusLifeCycle.getStatus());
            statusLifeCycle.setPreSubStatus(statusLifeCycle.getSubStatus());

            statusLifeCycle.setStatusCategory(matchStatus.getTargetStatusCategory());
            statusLifeCycle.setStatus(matchStatus.getTargetStatus());
            statusLifeCycle.setSubStatus(matchStatus.getTargetSubStatus());
            return statusLifeCycle;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据当前状态和事件，获取满足触发事件的状态
     * @param statusLifeCycle 受状态机管控的对象
     * @param targetStatus 目标状态，如果指定则表示需要强制转到此状态，如果没有符合的，则返回null
     * @param event 事件
     */
    public EventStatus getMatchStatus(StatusLifeCycle statusLifeCycle, String targetStatus, Event event) throws ClientException {
        try {
            List<EventStatus> eventStatuses = event.getEventStatuses();
            List<EventStatus> rejectEventStatuses = eventStatuses.stream().filter(eventStatus -> EventStatus.CHECK_FLAG_REJECT.equals(eventStatus.getCheckFlag())).collect(Collectors.toList());
            List<EventStatus> allowStatuses = eventStatuses.stream().filter(eventStatus -> EventStatus.CHECK_FLAG_ALLOW.equals(eventStatus.getCheckFlag())).collect(Collectors.toList());
            //判断当前statusLifeCycle上的状态是否允许转换
            //先判断reject后判断allow
            for (EventStatus rejectStatus : rejectEventStatuses) {
                if (EventStatus.ALL_FLAG.equals(rejectStatus.getSourceStatusCategory())) {
                    throw new ClientException(StatusMachineExceptions.EVENT_STATUS_IS_NOT_ALLOW);
                }
                if (rejectStatus.getSourceStatusCategory().equalsIgnoreCase(statusLifeCycle.getStatusCategory())
                        && EventStatus.ALL_FLAG.equals(rejectStatus.getSourceStatus())) {
                    throw new ClientException(StatusMachineExceptions.EVENT_STATUS_IS_NOT_ALLOW);
                }
                if (rejectStatus.getSourceStatusCategory().equalsIgnoreCase(statusLifeCycle.getStatusCategory())
                        && rejectStatus.getSourceStatus().equalsIgnoreCase(statusLifeCycle.getStatus())
                        && EventStatus.ALL_FLAG.equalsIgnoreCase(rejectStatus.getSourceStatus())) {
                    throw new ClientException(StatusMachineExceptions.EVENT_STATUS_IS_NOT_ALLOW);
                }
                if (rejectStatus.getSourceStatusCategory().equalsIgnoreCase(statusLifeCycle.getStatusCategory())
                        && rejectStatus.getSourceStatus().equalsIgnoreCase(statusLifeCycle.getStatus())
                        && rejectStatus.getSourceStatus().equalsIgnoreCase(statusLifeCycle.getSubStatus())) {
                    throw new ClientException(StatusMachineExceptions.EVENT_STATUS_IS_NOT_ALLOW);
                }

                if (rejectStatus.getSourceStatusCategory().equals(statusLifeCycle.getStatusCategory())
                        && EventStatus.ALL_FLAG.equals(rejectStatus.getSourceStatus())) {
                    throw new ClientException(StatusMachineExceptions.EVENT_STATUS_IS_NOT_ALLOW);
                }
            }

            //找到满足条件的状态 如果没有指定targetStatus, 则返回第一笔符合条件的状态
            EventStatus matchStatus = null;
            for (EventStatus allowStatus : allowStatuses) {
                if (EventStatus.ALL_FLAG.equalsIgnoreCase(allowStatus.getSourceStatusCategory())) {
                    matchStatus = allowStatus;
                }
                if (allowStatus.getSourceStatusCategory().equalsIgnoreCase(statusLifeCycle.getStatusCategory())
                                && EventStatus.ALL_FLAG.equalsIgnoreCase(allowStatus.getSourceStatus())) {
                    matchStatus = allowStatus;
                }
                if (allowStatus.getSourceStatusCategory().equalsIgnoreCase(statusLifeCycle.getStatusCategory())
                                && allowStatus.getSourceStatus().equalsIgnoreCase(statusLifeCycle.getStatus())
                                && EventStatus.ALL_FLAG.equalsIgnoreCase(allowStatus.getSourceSubStatus())) {
                    matchStatus = allowStatus;
                }
                if (allowStatus.getSourceStatusCategory().equalsIgnoreCase(statusLifeCycle.getStatusCategory())
                                && allowStatus.getSourceStatus().equalsIgnoreCase(statusLifeCycle.getStatus())) {
                    // 因为很多statusLifeCycle不管控到subStatus 所以此处允许subStatus为空
                    // 如果都为空，则也是match的
                    if (StringUtils.isNullOrEmpty(allowStatus.getSourceSubStatus()) && StringUtils.isNullOrEmpty(statusLifeCycle.getSubStatus())) {
                        matchStatus = allowStatus;
                    } else {
                        if (allowStatus.getSourceSubStatus().equalsIgnoreCase(statusLifeCycle.getSubStatus())) {
                            matchStatus = allowStatus;
                        }
                    }
                }
                if (!StringUtils.isNullOrEmpty(targetStatus)) {
                    if (matchStatus.getTargetStatus().equalsIgnoreCase(targetStatus)) {
                        return matchStatus;
                    }
                } else {
                    return matchStatus;
                }
            }
            return matchStatus;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
}
