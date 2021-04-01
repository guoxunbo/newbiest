package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.DocumentLine;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentLineRepository extends IRepository<DocumentLine, String> {

    void deleteByDocRrn(String docRrn) throws ClientException;

    DocumentLine findByLineId(String lineId) throws ClientException;

    List<DocumentLine> findByDocId(String docId);
}
