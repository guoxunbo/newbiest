package com.newbiest.rtm.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.rtm.model.DynaxAnalyseResult;
import com.newbiest.rtm.model.DynaxAnalyseResultDetail;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2019/5/27.
 */
@Repository
public interface DynaxAnalyseResultDetailRepository extends IRepository<DynaxAnalyseResultDetail, Long> {

    @Modifying
    @Query("DELETE FROM DynaxAnalyseResultDetail where resultRrn = :resultRrn")
    void deleteByResultRrn(@Param("resultRrn") Long resultRrn) throws ClientException;
}
