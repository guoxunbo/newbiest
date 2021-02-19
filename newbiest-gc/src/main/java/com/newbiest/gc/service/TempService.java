package com.newbiest.gc.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.scm.dto.TempCpModel;

import java.util.List;

/**
 * @author guoxunbo
 * @date 2/19/21 10:42 AM
 */
public interface TempService {

    void transferCpData(List<TempCpModel> tempCpModelList) throws ClientException;

}
