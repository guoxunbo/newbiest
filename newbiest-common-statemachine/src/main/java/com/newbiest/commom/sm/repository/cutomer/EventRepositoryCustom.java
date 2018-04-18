package com.newbiest.commom.sm.repository.cutomer;

import com.newbiest.base.exception.ClientException;
import com.newbiest.commom.sm.model.Event;
import com.newbiest.commom.sm.model.Status;

/**
 * Created by guoxunbo on 2017/11/5.
 */
public interface EventRepositoryCustom {

    Event getEvent(Long statusModelRrn, String eventId, boolean deepFlag) throws ClientException;


}
