package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.LabelTemplateParameter;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface LabelTemplateParameterRepository extends IRepository<LabelTemplateParameter, Long> {

    List<LabelTemplateParameter> findByLblTemplateRrn(@Param("lblTemplateRrn") Long lblTemplateRrn) throws ClientException;

}
