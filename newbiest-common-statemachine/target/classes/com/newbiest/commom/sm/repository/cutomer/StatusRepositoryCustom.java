package com.newbiest.commom.sm.repository.cutomer;

import com.newbiest.base.exception.ClientException;
import com.newbiest.commom.sm.model.Status;

/**
 * Created by guoxunbo on 2017/11/5.
 */
public interface StatusRepositoryCustom {

    Status getStatus(Long statusModelRrn, String status) throws ClientException;
}
