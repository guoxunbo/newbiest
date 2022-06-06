package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.PackagedLotDetail;
import com.newbiest.mms.state.model.MaterialEvent;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PackagedLotDetailRepository extends IRepository<PackagedLotDetail, Long> {

    PackagedLotDetail findByPackagedLotRrnAndMaterialLotRrn(Long packagedLotRrn, Long materialLotRrn) throws ClientException;

    List<PackagedLotDetail> findByPackagedLotRrn(Long packagedLotRrn) throws ClientException;

    @Modifying
    @Query("DELETE FROM PackagedLotDetail WHERE packagedLotId in (:bboxIdList)")
    void deleteByPackagedLotIdIn(List<String> bboxIdList);
}
