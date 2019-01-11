package com.newbiest.security.repository.custom.impl;

import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.model.NBRole;
import com.newbiest.security.repository.AuthorityRepository;
import com.newbiest.security.repository.custom.RoleRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2017/11/5.
 */
@Slf4j
public class RoleRepositoryImpl implements RoleRepositoryCustom {

    @Autowired
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AuthorityRepository authorityRepository;

    public List<NBAuthority> getRoleAuthorities(long roleRrn) throws ClientException {
        try {
            EntityGraph graph = em.createEntityGraph(NBRole.class);
            graph.addSubgraph("authorities");
            Map<String, Object> props = Maps.newHashMap();
            props.put(NBBase.LAZY_FETCH_PROP, graph);
            NBRole role = em.find(NBRole.class, roleRrn, props);
            List<NBAuthority> nbAuthorities = role.getAuthorities();

            if (CollectionUtils.isNotEmpty(nbAuthorities)) {
                return nbAuthorities.stream().filter(authority -> authority.getActiveFlag())
                        .map(authority -> {
                            // 各自找到自己的subAuthorities。这里一级即可。
                            List<NBAuthority> subAuthorites = authorityRepository.findByParentRrn(authority.getObjectRrn());
                            authority.setSubAuthorities(subAuthorites);
                            return authority;
                        }).collect(Collectors.toList());
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

}
