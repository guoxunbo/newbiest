package com.newbiest.rms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.rms.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by guoxunbo on 2018/7/5.
 */
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    Equipment getByEquipmentId(String equipmentId) throws ClientException;

}
