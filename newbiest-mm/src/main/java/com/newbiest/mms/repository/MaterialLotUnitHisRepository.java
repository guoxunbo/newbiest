package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.MaterialLotUnitHistory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface MaterialLotUnitHisRepository extends IRepository<MaterialLotUnitHistory, Long> {

    @Modifying
    @Query("DELETE FROM MaterialLotUnitHistory m where m.reserved48 = :importCode")
    void deleteByImportCode(@Param("importCode") String importCode) throws ClientException;

    @Modifying
    @Query("update MaterialLotUnitHistory m set m.created=:created, m.createdBy = :createdBy where m.materialLotId = :materialLotId and m.transType = :transType")
    void updateCreatedAndCreateByByMaterialLotIdAndTrandType(@Param("created") Date created, @Param("createdBy") String createdBy, @Param("materialLotId") String materialLotId, @Param("transType") String transType) throws ClientException;

}
