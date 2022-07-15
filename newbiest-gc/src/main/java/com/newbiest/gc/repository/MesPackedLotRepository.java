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
    List<MesPackedLot> findByParentRrn(Long parentRrn) throws ClientException;
    List<MesPackedLot> findByCstIdAndPackedStatusAndTypeNotInAndWaferIdIsNotNull(@Param("cstId") String cstId,  @Param("packedStatus") String packedStatus, @Param("type") List<String> type) throws ClientException;
    List<MesPackedLot> findByCstIdAndType(@Param("cstId") String cstId, @Param("type") String type) throws ClientException;

    @Query("update MesPackedLot p set p.packedStatus=:packedStatus where packedLotRrn in (:packedLotRrn)")
    @Modifying
    void updatePackedStatusByPackedLotRrnList(@Param("packedStatus")String packedStatus, @Param("packedLotRrn")List<Long> packedLotRrn) throws ClientException;
}
