package com.newbiest.base.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.security.model.NBOrg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2018/7/11.
 */
@Repository
public interface OrgRepository extends JpaRepository<NBOrg, Long> {

    NBOrg getByObjectRrn(long objectRrn) throws ClientException;

    NBOrg getByName(String name) throws ClientException;

}
