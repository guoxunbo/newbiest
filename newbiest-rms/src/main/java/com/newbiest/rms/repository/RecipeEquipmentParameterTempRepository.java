package com.newbiest.rms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.rms.model.RecipeEquipmentParameterTemp;
import com.newbiest.rms.repository.custom.RecipeEquipmentParameterTempRepositoryCustom;

/**
 * Created by guoxunbo on 2018/7/6.
 */
public interface RecipeEquipmentParameterTempRepository extends IRepository<RecipeEquipmentParameterTemp, Long>,
        RecipeEquipmentParameterTempRepositoryCustom {

}
