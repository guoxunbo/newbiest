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

    List<MaterialLotUnit> findByUnitIdAndWorkOrderIdAndState(@Param("unitId") String unitId, @Param("workOrderId") String workOrderId, @Param("state") String state) throws ClientException;

    List<MaterialLotUnit> findByUnitIdAndStateInAndReserved48IsNotNull(String unitId, List<String> stateList) throws ClientException;

    List<MaterialLotUnit> findByMaterialLotIdAndReserved12IsNull(String materialLotId) throws ClientException;

    MaterialLotUnit findByMaterialLotIdAndUnitId(String materialLotId, String unitId) throws ClientException;

    @Modifying
    @Query("update MaterialLotUnit m set m.state = :state where m.unitId = :unitId and m.materialLotId = :materialLotId")
    void updateMLotUnitByUnitIdAndMLotId(@Param("unitId") String unitId, @Param("materialLotId")  String materialLotId, @Param("state") String state) throws ClientException;

    @Modifying
    @Query("DELETE FROM MaterialLotUnit m where m.materialLotId = :materialLotId")
    void deleteByMaterialLotId(@Param("materialLotId") String materialLotId) throws ClientException;

    @Modifying
    @Query("DELETE FROM MaterialLotUnit m where m.reserved48 = :importCode")
    void deleteByImportCode(@Param("importCode") String importCode) throws ClientException;

    MaterialLotUnit findByUnitIdAndStateIn(String unitId, List<String> stateList)  throws ClientException;

    List<MaterialLotUnit> findByReserved48(@Param("reserved48") String reserved48) throws ClientException;

}
