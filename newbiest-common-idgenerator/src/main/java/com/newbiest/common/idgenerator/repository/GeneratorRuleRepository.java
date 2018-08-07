package com.newbiest.common.idgenerator.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.common.idgenerator.model.GeneratorRule;
import com.newbiest.common.idgenerator.repository.custom.GeneratorRuleRepositoryCustom;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2018/8/7.
 */
@Repository
public interface GeneratorRuleRepository extends IRepository<GeneratorRule, Long>, GeneratorRuleRepositoryCustom {


}
