package com.newbiest.rms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.rms.model.RecipeEquipmentUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by guoxunbo on 2018/7/4.
 */
public interface RecipeEquipmentUnitRepository extends JpaRepository<RecipeEquipmentUnit, Long> {

    List<RecipeEquipmentUnit> getByRecipeEquipmentRrn(long recipeEquipmentRrn) throws ClientException;

    List<RecipeEquipmentUnit> getByUnitRecipeRrn(long recipeEquipmentRrn) throws ClientException;

    void deleteByRecipeEquipmentRrn(long recipeEquipmentRrn) throws ClientException;

    void deleteByUnitRecipeRrn(long recipeEquipmentRrn) throws ClientException;

}
