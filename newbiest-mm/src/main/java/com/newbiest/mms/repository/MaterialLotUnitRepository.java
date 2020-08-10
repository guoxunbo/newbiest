package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.MaterialLotUnit;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialLotUnitRepository extends IRepository<MaterialLotUnit, Long> {

    List<MaterialLotUnit> findByMaterialLotId(String materialLotId);

    List<MaterialLotUnit> findByUnitIdAndState(@Param("unitId") String unitId, @Param("state") String state) throws ClientException;

    @Modifying
    @Query("update MaterialLotUnit m set m.state = :state where m.unitId = :unitId and m.materialLotId = :materialLotId")
    void updateMLotUnitByUnitIdAndMLotId(@Param("unitId") String unitId, @Param("materialLotId")  String materialLotId, @Param("state") String state) throws ClientException;

    @Modifying
    @Query("update MaterialLotUnit m set m.productType = :prodType where m.unitId in (:unitId)")
    void updateProdTypeByUnitIds(@Param("prodType") String prodType, @Param("unitId") List<String> unitId) throws ClientException;

}
