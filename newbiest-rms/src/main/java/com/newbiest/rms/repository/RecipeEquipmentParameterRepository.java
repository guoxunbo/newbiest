package com.newbiest.rms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.rms.model.RecipeEquipmentParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2018/7/4.
 */
@Repository
public interface RecipeEquipmentParameterRepository extends JpaRepository<RecipeEquipmentParameter, Long> {

    @Modifying
    @Query("DELETE FROM RecipeEquipmentParameter where recipeEquipmentRrn = :recipeEquipmentRrn")
    void deleteByRecipeEquipmentRrn(@Param("recipeEquipmentRrn") Long recipeEquipmentRrn) throws ClientException;
}
