package com.newbiest.context.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.common.exception.ContextException;
import com.newbiest.context.model.Context;
import com.newbiest.context.model.ContextValue;
import com.newbiest.context.repository.ContextRepository;
import com.newbiest.context.repository.ContextValueRepository;
import com.newbiest.context.service.ContextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Session;
import java.util.List;

/**
 * Created by guoxunbo on 2018/7/6.
 */
@Service
@Slf4j
public class ContextServiceImpl implements ContextService {

    @Autowired
    private ContextRepository contextRepository;

    @Autowired
    private ContextValueRepository contextValueRepository;

    public Context getContextByName(String name) throws ClientException {
        try {
            Context context = contextRepository.getByName(name);
            if (context == null) {
                throw new ClientParameterException(ContextException.CONTEXT_IS_NOT_EXIST, name);
            }
            return context;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public List<ContextValue> getContextValue(Context context, ContextValue contextValue) throws ClientException {
        try {
            return contextValueRepository.getContextValue(context, contextValue);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    @Override
    public void saveContextValue(Context context, List<ContextValue> contextValues) throws ClientException {
        contextValues.stream().forEach(value -> {
            value.setContextRrn(context.getObjectRrn());
            contextValueRepository.save(value);
        });
    }


}
