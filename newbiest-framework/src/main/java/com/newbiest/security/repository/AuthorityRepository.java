package com.newbiest.security.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.model.NBRole;
import com.newbiest.security.repository.custom.RoleRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2018/04/18.
 */
@Repository
public interface AuthorityRepository extends IRepository<NBAuthority, Long> {

}
