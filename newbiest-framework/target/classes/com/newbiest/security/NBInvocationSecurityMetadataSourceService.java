package com.newbiest.security;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.model.NBRole;
import com.newbiest.security.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 资源对应权限的的过滤器
 * Created by guoxunbo on 2017/12/2.
 */
@Service
@Slf4j
public class NBInvocationSecurityMetadataSourceService implements FilterInvocationSecurityMetadataSource {

    @Autowired
    private RoleRepository roleRepository;

    /**
     *  所有资源路径
     */
    private static Map<String, Collection<ConfigAttribute>> resourceMap;

    @PostConstruct
    private void loadResource() {
        try {
            resourceMap = Maps.newHashMap();
            List<NBRole> roles = roleRepository.findAll();
            if (CollectionUtils.isNotEmpty(roles)) {
                for (NBRole role : roles) {
                    ConfigAttribute configAttribute = new SecurityConfig(role.getRoleId());
                    List<NBAuthority> authorities = roleRepository.getRoleAuthorities(role.getObjectRrn());
                    if (CollectionUtils.isNotEmpty(authorities)) {
                        for (NBAuthority authority : authorities) {
                            String url = authority.getUrl();
                            if (resourceMap.containsKey(authority.getUrl())) {
                                Collection<ConfigAttribute> attributes = resourceMap.get(url);
                                attributes.add(configAttribute);
                                resourceMap.put(url, attributes);
                            } else {
                                Collection<ConfigAttribute> attributes = Lists.newArrayList();
                                attributes.add(configAttribute);
                                resourceMap.put(url, attributes);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 根据URL查找相应的权限配置 如果在权限表中，则返回给 Filter的decide 方法，用来判定用户是否有此权限。如果不在权限表中则放行
     * @param object
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation filterInvocation = (FilterInvocation) object;
        if (resourceMap == null) {
            loadResource();
        }
        for (String resourceUrl : resourceMap.keySet()) {
            RequestMatcher requestMatcher = new AntPathRequestMatcher(resourceUrl);
            if (requestMatcher.matches(filterInvocation.getHttpRequest())) {
                return resourceMap.get(resourceUrl);
            }
        }
        return null;
    }


    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return Lists.newArrayList();
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
