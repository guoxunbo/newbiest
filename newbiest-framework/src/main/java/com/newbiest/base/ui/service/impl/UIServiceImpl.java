package com.newbiest.base.ui.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.repository.TableRepository;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.security.SecurityException;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.repository.AuthorityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by guoxunbo on 2018/4/18.
 */
@Component
@Slf4j
public class UIServiceImpl implements UIService {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private TableRepository tableRepository;

    /**
     * 页面上点击authority的时候触发
     * @param authorityRrn
     * @return 返回带有所有栏位以及TAB的NBTABLE
     * @throws ClientException
     */
    public NBTable getNBTableByAuthority(Long authorityRrn) throws ClientException {
        try {
            NBAuthority nbAuthority = authorityRepository.getByObjectRrn(authorityRrn);
            if (nbAuthority == null) {
                throw new ClientException(SecurityException.AUTHORITY_IS_NULL);
            }
            return tableRepository.getDeepTable(nbAuthority.getTableRrn());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }


}
