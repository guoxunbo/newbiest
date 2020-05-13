package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.gc.model.GCLcdCogDetial;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.newbiest.base.repository.custom.IRepository;

@Repository
public interface GCLcdCogDetialRepository extends IRepository<GCLcdCogDetial, Long> {

    @Query("SELECT l FROM GCLcdCogDetial l where l.boxaId = :boxaId and l.boxbId = :boxbId")
    GCLcdCogDetial getGcLcdCogDetialByBoxaIdAndBoxbId(@Param("boxaId") String boxaId, @Param("boxbId")String boxbId) throws ClientException;


}
