package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.MesPackedLot;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesPackedLotRepository extends IRepository<MesPackedLot, Long> {

    MesPackedLot findByBoxId(String boxId) throws ClientException;
    MesPackedLot findByPackedLotRrn(Long packedLotRrn) throws ClientException;
    List<MesPackedLot> findByParentRrn(Long parentRrn) throws ClientException;
    List<MesPackedLot> findByCstIdAndPackedStatusAndWaferIdIsNotNull(@Param("packedStatus") String packedStatus, @Param("cstId") String cstId) throws ClientException;

    @Query("update MesPackedLot p set p.packedStatus=:packedStatus where packedLotRrn in (:packedLotRrn)")
    @Modifying
    void updatePackedStatusByPackedLotRrnList(@Param("packedStatus")String packedStatus, @Param("packedLotRrn")List<Long> packedLotRrn) throws ClientException;
}
