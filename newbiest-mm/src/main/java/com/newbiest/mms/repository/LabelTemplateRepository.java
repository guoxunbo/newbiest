package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.DeliveryOrder;
import com.newbiest.mms.model.LabelTemplate;
import org.springframework.stereotype.Repository;


@Repository
public interface LabelTemplateRepository extends IRepository<LabelTemplate, String> {

}
