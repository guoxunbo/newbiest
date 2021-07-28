package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.model.WaferHoldRelation;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.newbiest.base.repository.custom.IRepository;

@Repository
public interface GCWaferHoldRelationRepository extends IRepository<WaferHoldRelation, Long> {
    WaferHoldRelation findByWaferId(@Param("waferId") String waferId) throws ClientException;
}
