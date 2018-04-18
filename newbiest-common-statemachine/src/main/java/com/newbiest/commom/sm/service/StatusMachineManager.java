package com.newbiest.commom.sm.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.commom.sm.model.StatusLifeCycle;

/**
 * Created by guoxunbo on 2017/11/5.
 */
public interface StatusMachineManager {

    StatusLifeCycle triggerEvent(StatusLifeCycle lifeCycle, String eventId, SessionContext sc) throws ClientException;
    StatusLifeCycle triggerEvent(StatusLifeCycle lifeCycle, String eventId, String targetState, boolean isForceTarget, SessionContext sc) throws ClientException;

}
