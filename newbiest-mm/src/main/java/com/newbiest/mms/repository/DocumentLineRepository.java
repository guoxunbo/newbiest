package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.DocumentLine;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DocumentLineRepository extends IRepository<DocumentLine, Long> {

    List<DocumentLine> findByDocId(String docId) throws ClientException;
    DocumentLine findByDocRrnAndReserved1(Long docRrn, String reserved1) throws ClientException;

    DocumentLine findByDocIdAndMaterialNameAndReserved3AndReserved2AndReserved7(@Param("docId")String docId, @Param("materialName")String materialName, @Param("reserved3")String grade, @Param("reserved2")String subCode, @Param("reserved7")String bondedProperty);
}
