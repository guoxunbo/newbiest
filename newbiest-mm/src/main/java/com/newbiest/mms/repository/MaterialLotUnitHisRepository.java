package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.MaterialLotUnitHistory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialLotUnitHisRepository extends IRepository<MaterialLotUnitHistory, Long> {

    @Modifying
    @Query("DELETE FROM MaterialLotUnitHistory m where m.materialLotId = :materialLotId")
    void deleteByMaterialLotId(@Param("materialLotId") String materialLotId) throws ClientException;
}
