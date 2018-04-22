package com.newbiest.security.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.security.model.NBAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2018/04/18.
 */
@Repository
public interface AuthorityRepository extends JpaRepository<NBAuthority, Long> {

    NBAuthority getByObjectRrn(long objectRrn) throws ClientException;

}
