package com.newbiest.kms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.kms.model.QuestionHistory;
import org.springframework.stereotype.Repository;


@Repository
public interface QuestionHistoryRepository extends IRepository<QuestionHistory, Long> {
}
