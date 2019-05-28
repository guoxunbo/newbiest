package com.newbiest.rtm.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.rtm.model.AnalyseResult;
import com.newbiest.rtm.model.DynaxAnalyseResult;
import com.newbiest.rtm.model.DynaxAnalyseResultDetail;
import lombok.Data;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

/**
 * Created by guoxunbo on 2019/5/27.
 */
@Repository
public interface DynaxAnalyseResultRepository extends IRepository<DynaxAnalyseResult, Long> {

    @Modifying
    @Query("DELETE FROM DynaxAnalyseResult where fileName = :fileName")
    void deleteByFileName(@Param("fileName") String fileName) throws ClientException;

    List<DynaxAnalyseResult> getByFileName(String fileName) throws ClientException;

}
