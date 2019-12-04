package com.newbiest.rms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.rms.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentRepository extends IRepository<Equipment, Long> {

    Equipment getByEquipmentId(String equipmentId) throws ClientException;

}
