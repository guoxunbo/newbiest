package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.WaferHoldRelation;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WaferHoldRelationRepository extends IRepository<WaferHoldRelation, Long> {
    WaferHoldRelation findByWaferId(@Param("waferId") String waferId) throws ClientException;
}
