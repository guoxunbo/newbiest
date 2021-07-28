package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.Document;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends IRepository<Document, Long> {


}
