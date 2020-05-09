package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.DocumentLine;
import org.springframework.stereotype.Repository;


@Repository
public interface DocumentLineRepository extends IRepository<DocumentLine, String> {

    DocumentLine findByDocRrnAndMaterialName(String docRrn, String materialName) throws ClientException;
    DocumentLine findByDocRrnAndReserved1(String docRrn, String reserved1) throws ClientException;


}
