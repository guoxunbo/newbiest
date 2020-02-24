package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.MaterialLotUnit;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialLotUnitRepository extends IRepository<MaterialLotUnit, Long> {

    List<MaterialLotUnit> findByMaterialLotId(String materialLotId);
}
