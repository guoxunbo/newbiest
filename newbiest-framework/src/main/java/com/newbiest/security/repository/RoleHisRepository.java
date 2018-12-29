package com.newbiest.security.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.security.model.NBRole;
import com.newbiest.security.model.NBRoleHis;
import com.newbiest.security.repository.custom.RoleRepositoryCustom;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2017/11/5.
 */
@Repository
public interface RoleHisRepository extends IRepository<NBRoleHis, Long> {

}
