package com.newbiest.security.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.security.model.NBAuthority;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by guoxunbo on 2018/04/18.
 */
@Repository
public interface AuthorityRepository extends IRepository<NBAuthority, Long> {

    List<NBAuthority> findByParentRrn(long parentRrn) throws ClientException;

}
