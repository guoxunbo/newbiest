package com.newbiest.rms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.rms.model.RecipeEquipmentUnit;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeEquipmentUnitRepository extends IRepository<RecipeEquipmentUnit, String> {

    @Modifying
    @Query("DELETE FROM RecipeEquipmentUnit where recipeEquipmentRrn = :recipeEquipmentRrn")
    void deleteByRecipeEquipmentRrn(@Param("recipeEquipmentRrn") String recipeEquipmentRrn) throws ClientException;

    List<RecipeEquipmentUnit> findByUnitRecipeEquipmentRrn(String unitRecipeEquipmentRrn) throws ClientException;
}
