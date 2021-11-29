package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.GcWlatoftTesebit;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WlatoFtTestBitRepository extends IRepository<GcWlatoftTesebit, Long> {

    GcWlatoftTesebit findByWaferId(@Param("waferId")String waferId) throws ClientException;
}
