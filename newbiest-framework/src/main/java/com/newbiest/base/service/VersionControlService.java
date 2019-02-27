package com.newbiest.base.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.utils.SessionContext;

/**
 * Created by guoxunbo on 2019/2/21.
 */
public interface VersionControlService {

    Long getNextVersion(NBVersionControl nbVersionControl, SessionContext sc) throws ClientException;

    NBVersionControl save(NBVersionControl nbVersionControl, SessionContext sc) throws ClientException;

    NBVersionControl active(NBVersionControl nbVersionControl, SessionContext sc) throws ClientException;

    NBVersionControl unFrozen(NBVersionControl nbVersionControl, SessionContext sc) throws ClientException;

    NBVersionControl frozen(NBVersionControl nbVersionControl, SessionContext sc) throws ClientException;

    NBVersionControl inactive(NBVersionControl nbVersionControl, SessionContext sc) throws ClientException;

}
