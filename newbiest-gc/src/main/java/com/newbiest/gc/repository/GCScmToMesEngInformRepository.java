package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.GCScmToMesEngInform;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GCScmToMesEngInformRepository extends IRepository<GCScmToMesEngInform, Long> {

    GCScmToMesEngInform findByLotId(@Param("lotId")String lotId) throws ClientException;
}
