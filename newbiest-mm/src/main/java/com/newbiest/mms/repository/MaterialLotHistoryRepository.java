package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotHistory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by guoxunbo on 2019/2/20.
 */
@Repository
public interface MaterialLotHistoryRepository extends IRepository<MaterialLotHistory, Long> {

    MaterialLotHistory findTopByMaterialLotIdAndTransTypeOrderByCreatedDesc(String materialLotId, String transType);

    @Modifying
    @Query("DELETE FROM MaterialLotHistory m where m.reserved48 = :importCode")
    void deleteByImportCode(@Param("importCode") String importCode) throws ClientException;

    @Modifying
    @Query("update MaterialLotHistory m set m.created=:created, m.createdBy = :createdBy where m.materialLotId = :materialLotId and m.transType = :transType")
    void updateCreatedAndCreateByByMaterialLotIdAndTrandType(@Param("created") Date created, @Param("createdBy") String createdBy, @Param("materialLotId") String materialLotId, @Param("transType") String transType) throws ClientException;

    @Query("update MaterialLotHistory m set m.createdBy=:createdBy, m.updatedBy = :updatedBy  where m.objectRrn = :objectRrn")
    @Modifying
    void updateCreatedByAndUpdatedByObjectRrn(@Param("createdBy") String createdBy, @Param("updatedBy") String updatedBy, @Param("objectRrn") Long objectRrn) throws ClientException;

}
