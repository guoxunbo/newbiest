package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.LabelTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface LabelTemplateRepository extends IRepository<LabelTemplate, Long> {

    LabelTemplate findByName(@Param("name")String name) throws ClientException;

}
