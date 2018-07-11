package com.newbiest.context.repository;

import com.newbiest.context.model.ContextValue;
import com.newbiest.context.repository.custom.ContextValueRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by guoxunbo on 2018/7/6.
 */
public interface ContextValueRepository extends JpaRepository<ContextValue, Long>, ContextValueRepositoryCustom {
}
