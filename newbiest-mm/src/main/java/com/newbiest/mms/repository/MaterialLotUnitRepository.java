package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.MaterialLotUnit;

import java.util.List;

public interface MaterialLotUnitRepository extends IRepository<MaterialLotUnit, String> {

    List<MaterialLotUnit> findByMaterialLotIdIn(List<String> materialLotIds);

    List<MaterialLotUnit> findByMaterialLotId(String materialLotId);


}
