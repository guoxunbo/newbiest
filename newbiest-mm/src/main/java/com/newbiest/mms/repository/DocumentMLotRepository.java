package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.DocumentMLot;
import com.newbiest.mms.model.IssueLotOrder;
import org.springframework.stereotype.Repository;


@Repository
public interface DocumentMLotRepository extends IRepository<DocumentMLot, String> {

}
