package com.newbiest.context.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.context.model.MergeRule;
import org.springframework.stereotype.Repository;

@Repository
public interface MergeRuleRepository extends IRepository<MergeRule, Long> {

}
