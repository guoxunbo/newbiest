package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.MLotCheckSheetLine;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MLotCheckSheetLineRepository extends IRepository<MLotCheckSheetLine, String> {

    List<MLotCheckSheetLine> findByMLotCheckSheetRrn(String mLotCheckSheetRrn);
}
