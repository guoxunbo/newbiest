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
public interface RecipeEquipmentUnitRepository extends IRepository<RecipeEquipmentUnit, Long> {

    @Modifying
    @Query("DELETE FROM RecipeEquipmentUnit where recipeEquipmentRrn = :recipeEquipmentRrn")
    void deleteByRecipeEquipmentRrn(@Param("recipeEquipmentRrn") Long recipeEquipmentRrn) throws ClientException;

    List<RecipeEquipmentUnit> findByUnitRecipeEquipmentRrn(Long unitRecipeEquipmentRrn) throws ClientException;
}
