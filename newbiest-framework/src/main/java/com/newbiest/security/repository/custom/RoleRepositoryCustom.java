package com.newbiest.security.repository.custom;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.model.NBRole;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by guoxunbo on 2017/11/5.
 */
public interface RoleRepositoryCustom {

    EntityManager getEntityManager();

    NBRole getDeepRole(Long roleRrn, boolean authorityFlag, SessionContext sc) throws ClientException;

    List<NBAuthority> getRoleAuthorities(long roleRrn) throws ClientException;


}
