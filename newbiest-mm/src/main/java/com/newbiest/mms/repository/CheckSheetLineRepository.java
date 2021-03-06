package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.CheckSheetLine;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CheckSheetLineRepository extends IRepository<CheckSheetLine, String> {

    List<CheckSheetLine> findByCheckSheetRrn(String checkSheetRrn) throws ClientException;

}
