package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.DocumentLine;
import org.springframework.stereotype.Repository;


@Repository
public interface DocumentLineRepository extends IRepository<DocumentLine, Long> {

    DocumentLine findByDocRrnAndMaterialName(Long docRrn, String materialName) throws ClientException;
    DocumentLine findByDocRrnAndReserved1(Long docRrn, String reserved1) throws ClientException;


}
