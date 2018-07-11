package com.newbiest.rms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.rms.model.RecipeEquipmentParameterTemp;
import com.newbiest.rms.repository.custom.RecipeEquipmentParameterTempRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by guoxunbo on 2018/7/6.
 */
public interface RecipeEquipmentParameterTempRepository extends JpaRepository<RecipeEquipmentParameterTemp, Long>,
        RecipeEquipmentParameterTempRepositoryCustom {

}
