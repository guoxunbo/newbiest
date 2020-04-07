package com.newbiest.rms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.rms.model.RecipeEquipment;
import com.newbiest.rms.repository.custom.RecipeEquipmentRepositoryCustom;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author guoxunbo
 * @date 2020-04-02 14:27
 */
@Repository
public interface RecipeEquipmentRepository extends IRepository<RecipeEquipment, Long>, RecipeEquipmentRepositoryCustom {

    List<RecipeEquipment> getByNameAndEquipmentIdAndPatternOrderByVersionDesc(String name, String equipmentId, String pattern) throws ClientException;

}
