package com.newbiest.rms.repository;

import com.newbiest.rms.model.RecipeEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * Created by guoxunbo on 2018/7/4.
 */
@Repository
public interface RecipeEquipmentRepository extends JpaRepository<RecipeEquipment, Long> {

}
