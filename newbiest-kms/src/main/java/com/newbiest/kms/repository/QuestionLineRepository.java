package com.newbiest.kms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.kms.model.QuestionLine;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface QuestionLineRepository extends IRepository<QuestionLine, Long> {

    List<QuestionLine> findByQuestionRrn(Long questionRrn);

}
