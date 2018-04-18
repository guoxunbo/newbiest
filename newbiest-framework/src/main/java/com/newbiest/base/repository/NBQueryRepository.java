package com.newbiest.base.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2017/11/5.
 */
@Repository
public interface NBQueryRepository extends JpaRepository <NBQuery, Long> {

    NBQuery getByName(String name) throws ClientException;

}
