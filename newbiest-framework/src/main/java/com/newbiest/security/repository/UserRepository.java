package com.newbiest.security.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2017/9/25.
 */
@Repository
public interface UserRepository extends IRepository<NBUser, Long>, UserRepositoryCustom {

    NBUser getByObjectRrn(Long objectRrn) throws ClientException;
    NBUser getByUsername(String username) throws ClientException;


}
