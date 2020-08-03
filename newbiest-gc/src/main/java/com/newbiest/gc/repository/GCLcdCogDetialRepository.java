package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.model.GCLcdCogDetail;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.newbiest.base.repository.custom.IRepository;

@Repository
public interface GCLcdCogDetialRepository extends IRepository<GCLcdCogDetail, Long> {

    GCLcdCogDetail findByBoxaIdAndBoxbId(@Param("boxaId") String boxaId, @Param("boxbId")String boxbId) throws ClientException;


}
