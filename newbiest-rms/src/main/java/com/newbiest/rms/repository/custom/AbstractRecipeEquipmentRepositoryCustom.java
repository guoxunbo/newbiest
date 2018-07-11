package com.newbiest.rms.repository.custom;

import com.newbiest.base.exception.ClientException;
import com.newbiest.rms.model.AbstractRecipeEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by guoxunbo on 2018/7/4.
 */
public interface AbstractRecipeEquipmentRepositoryCustom {

    AbstractRecipeEquipment getDeepRecipeEquipment(long objectRrn) throws ClientException;
    AbstractRecipeEquipment getGoldenRecipe(long orgRrn, String eqpType, String recipeName, String status, String pattern, boolean bodyFlag) throws ClientException;
    List<AbstractRecipeEquipment> getRecipeEquipment(long orgRrn, String recipeName, String equipmentId, String equipmentType, String pattern) throws ClientException;
    AbstractRecipeEquipment getActiveRecipeEquipment(long orgRrn, String recipeName, String equipmentId, String pattern, boolean bodyFlag) throws ClientException;

}
