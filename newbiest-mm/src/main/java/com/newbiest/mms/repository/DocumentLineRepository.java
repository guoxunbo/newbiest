package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.DocumentLine;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DocumentLineRepository extends IRepository<DocumentLine, Long> {

    DocumentLine findByDocRrnAndMaterialName(Long docRrn, String materialName) throws ClientException;
    List<DocumentLine> findByDocRrn(Long docRrn) throws ClientException;
    DocumentLine findByDocRrnAndReserved1(Long docRrn, String reserved1) throws ClientException;

}
