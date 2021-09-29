package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.FutureHoldConfig;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FutureHoldConfigRepository extends IRepository<FutureHoldConfig,Long> {

    FutureHoldConfig findByLotId(@Param("lotId")String lotId) throws ClientException;

    List<FutureHoldConfig> getByLotIdLike(@Param("lotId")String lotId) throws ClientException;

}
