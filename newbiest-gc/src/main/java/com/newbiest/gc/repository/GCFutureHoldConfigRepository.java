package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.GCFutureHoldConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GCFutureHoldConfigRepository extends IRepository<GCFutureHoldConfig,Long> {

    GCFutureHoldConfig findByLotId(@Param("lotId")String lotId) throws ClientException;

    List<GCFutureHoldConfig> getByLotIdLike(@Param("lotId")String lotId) throws ClientException;

}
