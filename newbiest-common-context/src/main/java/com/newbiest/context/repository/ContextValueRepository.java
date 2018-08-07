package com.newbiest.context.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.context.model.ContextValue;
import com.newbiest.context.repository.custom.ContextValueRepositoryCustom;

/**
 * Created by guoxunbo on 2018/7/6.
 */
public interface ContextValueRepository extends IRepository<ContextValue, Long>, ContextValueRepositoryCustom {
}
