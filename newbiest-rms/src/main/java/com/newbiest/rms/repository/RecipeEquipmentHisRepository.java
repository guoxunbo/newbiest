package com.newbiest.rms.repository;

import com.newbiest.rms.model.RecipeEquipmentHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2018/7/4.
 */
@Repository
public interface RecipeEquipmentHisRepository extends JpaRepository<RecipeEquipmentHis, Long> {
}
