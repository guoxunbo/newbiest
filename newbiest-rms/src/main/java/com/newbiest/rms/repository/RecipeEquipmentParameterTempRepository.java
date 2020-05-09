package com.newbiest.rms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.rms.model.RecipeEquipmentParameterTemp;

import java.util.List;

/**
 * Created by guoxunbo on 2018/7/6.
 */
public interface RecipeEquipmentParameterTempRepository extends IRepository<RecipeEquipmentParameterTemp, String> {

    List<RecipeEquipmentParameterTemp> findByEcnIdAndStatus(String ecnId, String status) throws ClientException;
}
