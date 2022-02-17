package com.newbiest.gc.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.scm.dto.TempFtModel;

import java.util.List;

/**
 * @author luoguozhang
 * @date 2/11/22 10:02 AM
 */
public interface TempFtService {

    void transferFtData(List<TempFtModel> tempCpModelList, String fileName) throws ClientException;

}
