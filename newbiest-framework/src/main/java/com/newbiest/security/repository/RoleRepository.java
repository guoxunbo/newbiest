package com.newbiest.security.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.security.model.NBRole;
import com.newbiest.security.repository.custom.RoleRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2017/11/5.
 */
@Repository
public interface RoleRepository extends IRepository<NBRole, Long>, RoleRepositoryCustom {

    NBRole findByRoleId(String roleId) throws ClientException;

}
