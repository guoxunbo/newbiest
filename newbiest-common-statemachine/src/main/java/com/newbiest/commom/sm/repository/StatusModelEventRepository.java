package com.newbiest.commom.sm.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.commom.sm.model.Event;
import com.newbiest.commom.sm.model.StatusModelEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by guoxunbo on 2017/11/5.
 */
@Repository
public interface StatusModelEventRepository extends JpaRepository<StatusModelEvent, Long> {

    List<StatusModelEvent> getByModelRrn(Long modelRrn) throws ClientException;

}
