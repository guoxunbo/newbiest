package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.LabelTemplateParameter;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LabelTemplateParameterRepository extends IRepository<LabelTemplateParameter, String> {

    List<LabelTemplateParameter> findByLblTemplateRrn(String lblTemplateRrn) throws ClientException;

}
