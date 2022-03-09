package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.GcUnConfirmWaferSet;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GCUnConfirmWaferSetRepository extends IRepository<GcUnConfirmWaferSet, Long> {

    List<GcUnConfirmWaferSet> findByLotId(@Param("lotId") String lotId) throws ClientException;

}
