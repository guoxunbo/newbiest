//package com.newbiest.commom.sm.service.impl;
//
//import com.newbiest.base.exception.ClientException;
//import com.newbiest.base.exception.ExceptionManager;
//import com.newbiest.base.utils.SessionContext;
//import com.newbiest.commom.sm.model.*;
//import com.newbiest.commom.sm.repository.StatusModelEventRepository;
//import com.newbiest.commom.sm.repository.StatusModelRepository;
//import com.newbiest.commom.sm.service.StatusMachineManager;
//import com.newbiest.commom.sm.utils.StatusModelException;
//import com.newbiest.security.model.NBRole;
//import com.newbiest.security.repository.RoleRepository;
//import com.newbiest.security.repository.UserRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
///**
// * Created by guoxunbo on 2017/11/5.
// */
//@Component
//@Transactional
//public class StatusMachineManagerBean implements StatusMachineManager {
//
//    private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Autowired
//    private StatusModelRepository statusModelRepository;
//
//    @Autowired
//    private StatusModelEventRepository statusModelEventRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    /**
//     * 触发对象事件,根据事件修改其目标状态
//     * @param lifeCycle 当前状态
//     * @param eventId 触发事件
//     * @param sc
//     *
//     * @return StateLifeCycle 变更后状态
//     */
//    public StatusLifeCycle triggerEvent(StatusLifeCycle lifeCycle, String eventId, SessionContext sc) throws ClientException {
//        return triggerEvent(lifeCycle, eventId, null, false, sc);
//    }
//
//    /**
//     * 触发对象事件,根据事件修改其目标状态
//     * @param lifeCycle 变更前状态
//     * @param eventId 触发事件
//     * @param targetState 	期望的目标状态
//     * @param isForceTarget 是否强制目标状态,true表示强制,即必须装换为目标状态,如果不能装换,则抛出异常
//     * @param sc
//     *
//     * @return StateLifeCycle 变更后状态
//     */
//    public StatusLifeCycle triggerEvent(StatusLifeCycle lifeCycle, String eventId, String targetState, boolean isForceTarget, SessionContext sc) throws ClientException {
//        try {
//            //获得状态模型
//            StatusModel statusModel = lifeCycle.getStatusModel();
//            if (statusModel == null && lifeCycle.getStatusModelRrn() != null) {
//                statusModel = statusModelRepository.getByObjectRrn(lifeCycle.getStatusModelRrn());
//            }
//            if (statusModel == null) {
//                throw new ClientException(StatusModelException.COM_SM_MODEL_IS_NOT_FOUND);
//            }
//            List<StatusModelEvent> statusModelEvents = statusModelEventRepository.getByModelRrn(statusModel.getObjectRrn());
//            if (statusModelEvents == null || statusModelEvents.size() == 0) {
//                throw new ClientException(StatusModelException.COM_SM_MODEL_EVENT_IS_NOT_FOUND);
//            }
//            // 查找当前事件的ModelEvent
//            StatusModelEvent modelEvent = null;
//            Event event = null;
//            for (StatusModelEvent statusModelEvent : statusModelEvents) {
//                if (eventId.equals(statusModelEvent.getEvent().getName())) {
//                    modelEvent = statusModelEvent;
//                    event = statusModelEvent.getEvent();
//                    break;
//                }
//            }
//            if (modelEvent == null) {
//                throw new ClientException(StatusModelException.COM_SM_EVENT_STATUS_IS_NOT_FOUND);
//            }
//            // 检查事件权限
//            checkEventAuthority(modelEvent, sc);
//
//            return lifeCycle;
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            throw ExceptionManager.handleException(e);
//        }
//    }
//
//    /**
//     * 根据当前状态查找允许变化的状态
//     * @param lifeCycle 当前状态
//     * @param targetState 目标状态
//     * @param isForceTarget 是否强制转成目标状态 如果不能转换，返回NULL 如果不强制，返回第一个对象
//     * @param event 事件
//     * @return
//     * @throws ClientException
//     */
//    public EventStatus getCurrentEventStatus(StatusLifeCycle lifeCycle, String targetState, boolean isForceTarget, Event event) throws ClientException {
//        try {
//            List<EventStatus> eventStatuses = event.getEventStatus();
//            if (eventStatuses == null || eventStatuses.size() == 0) {
//                throw new ClientException(StatusModelException.COM_SM_EVENT_STATUS_IS_NOT_FOUND);
//            }
//            List<EventStatus> allowStatuses = eventStatuses.stream().filter(eventStatus -> EventStatus.CHECK_FLAG_ALLOW.equals(eventStatus))
//                                                .collect(Collectors.toList());
//            List<EventStatus> rejectStatuses = eventStatuses.stream().filter(eventStatus -> EventStatus.CHECK_FLAG_REJECT.equals(eventStatus))
//                    .collect(Collectors.toList());
//            //1. 检查源状态是否被拒绝转换
//
//        } catch(Exception e) {
//            logger.error(e.getMessage(), e);
//            throw ExceptionManager.handleException(e);
//        }
//
//        return null;
//    }
//
//    /**
//     * 检查事件权限 为空默认是允许
//     * @param modelEvent 模型事件
//     * @param sc
//     */
//    private void checkEventAuthority(StatusModelEvent modelEvent, SessionContext sc) {
//        try {
//            sc.buildTransInfo();
//            List<NBRole> nbRoles = modelEvent.getRoles();
//            if (nbRoles != null && nbRoles.size() > 0) {
//                boolean authorityFlag = false;
//                for (NBRole role : nbRoles) {
//                    //TODO 检查权限
////                    role = roleRepository.getDeepRole(role.getObjectRrn(), false, sc);
////                    Optional optional = role.getUsers().stream().filter(user -> user.getUsername().equals(sc.getUsername())).findFirst();
////                    if (optional.isPresent()) {
////                        authorityFlag = true;
////                        break;
////                    }
//                }
//                if (!authorityFlag) {
//                    throw new ClientException(StatusModelException.COM_SM_EVENT_STATUS_NOT_AUTHORITY);
//                }
//            }
//        } catch (ClientException e) {
//            logger.error(e.getMessage(), e);
//            throw ExceptionManager.handleException(e);
//        }
//    }
//
//}
