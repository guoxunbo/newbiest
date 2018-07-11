package com.newbiest.rms.repository.custom;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.rms.model.RecipeEquipmentParameterTemp;

import java.util.List;

/**
 * Created by guoxunbo on 2018/7/6.
 */
public interface RecipeEquipmentParameterTempRepositoryCustom {

    List<RecipeEquipmentParameterTemp> getByEcnId(String ecnId, String status, SessionContext sc) throws ClientException;

}
