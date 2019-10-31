package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.MesPackedLot;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesPackedLotRepository extends IRepository<MesPackedLot, Long> {

    MesPackedLot findByBoxId(String boxId) throws ClientException;
    MesPackedLot findByPackedLotRrn(Long packedLotRrn) throws ClientException;
    List<MesPackedLot> findByParentRrn(Long parentRrn) throws ClientException;

}
