package com.newbiest.mms.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.gc.model.MesPackedLot;
import org.springframework.stereotype.Repository;

@Repository
public interface MesPackedLotRepository extends IRepository<MesPackedLot, Long> {

    MesPackedLot findByPackedLotRrn(long packedLotRrn) throws ClientException;
    MesPackedLot findByBoxId(String boxId) throws ClientException;

}
