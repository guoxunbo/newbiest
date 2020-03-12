package com.newbiest.rms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.rms.model.AbstractRecipeEquipment;
import com.newbiest.rms.repository.custom.AbstractRecipeEquipmentRepositoryCustom;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2018/7/4.
 */
@Repository
public interface AbstractRecipeEquipmentRepository extends IRepository<AbstractRecipeEquipment, Long>,
        AbstractRecipeEquipmentRepositoryCustom {

    AbstractRecipeEquipment getByObjectRrn(long objectRrn) throws ClientException;

}
