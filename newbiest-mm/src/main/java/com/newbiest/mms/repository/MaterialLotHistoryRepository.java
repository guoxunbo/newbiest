package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotHistory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2019/2/20.
 */
@Repository
public interface MaterialLotHistoryRepository extends IRepository<MaterialLotHistory, Long> {

    MaterialLotHistory findTopByMaterialLotIdAndTransTypeOrderByCreatedDesc(String materialLotId, String transType);

    @Modifying
    @Query("DELETE FROM MaterialLotHistory m where m.reserved48 = :importCode")
    void deleteByImportCode(@Param("importCode") String importCode) throws ClientException;
}
