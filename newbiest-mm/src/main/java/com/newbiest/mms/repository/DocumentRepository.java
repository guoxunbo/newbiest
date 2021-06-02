package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.Document;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DocumentRepository extends IRepository<Document, String> {

    List<Document> findByNameIn(List<String> documentIds) throws ClientException;


}
