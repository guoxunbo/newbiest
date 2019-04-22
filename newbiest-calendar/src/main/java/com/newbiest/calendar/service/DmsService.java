package com.newbiest.calendar.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.calendar.model.ChangeShift;

/**
 * Created by guoxunbo on 2019/4/22.
 */
public interface DmsService {

    ChangeShift saveChangeShift(ChangeShift changeShift, SessionContext sc) throws ClientException;
    ChangeShift closeChangeShift(ChangeShift changeShift, SessionContext sc) throws ClientException;
    ChangeShift openChangeShift(ChangeShift changeShift, SessionContext sc) throws ClientException;
}
