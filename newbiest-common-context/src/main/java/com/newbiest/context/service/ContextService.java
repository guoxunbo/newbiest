package com.newbiest.context.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.context.model.Context;
import com.newbiest.context.model.ContextValue;

import java.util.List;

/**
 * Created by guoxunbo on 2018/7/6.
 */
public interface ContextService {

    Context getContextByName(String name) throws ClientException;

    List<ContextValue> getContextValue(Context context, ContextValue contextValue, SessionContext sc) throws ClientException;

    void saveContextValue(Context context, List<ContextValue> contextValues, SessionContext sc) throws ClientException;


}
