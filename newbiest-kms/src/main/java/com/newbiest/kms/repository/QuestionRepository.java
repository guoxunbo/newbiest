package com.newbiest.kms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.kms.model.Question;
import org.springframework.stereotype.Repository;


@Repository
public interface QuestionRepository extends IRepository<Question, Long> {
}
