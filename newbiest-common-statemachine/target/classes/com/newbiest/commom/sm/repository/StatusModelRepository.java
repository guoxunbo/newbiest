package com.newbiest.commom.sm.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.commom.sm.model.StatusModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2017/11/5.
 */
@Repository
public interface StatusModelRepository extends JpaRepository<StatusModel, Long> {

    StatusModel getByObjectRrn(Long objectRrn) throws ClientException;

}
