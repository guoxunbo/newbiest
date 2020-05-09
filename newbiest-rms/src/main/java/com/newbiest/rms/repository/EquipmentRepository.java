package com.newbiest.rms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.rms.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author guoxunbo
 * @date 2020-04-02 14:27
 */
@Repository
public interface EquipmentRepository extends IRepository<Equipment, String> {

    Equipment getByEquipmentId(String equipmentId) throws ClientException;

}
