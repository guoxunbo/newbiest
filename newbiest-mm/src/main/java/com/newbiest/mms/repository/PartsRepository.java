package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.Parts;
import org.springframework.stereotype.Repository;


@Repository
public interface PartsRepository extends IRepository<Parts, String> {

}