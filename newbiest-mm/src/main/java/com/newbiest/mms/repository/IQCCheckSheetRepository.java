package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.IqcCheckSheet;
import com.newbiest.mms.model.Storage;
import org.springframework.stereotype.Repository;


@Repository
public interface IQCCheckSheetRepository extends IRepository<IqcCheckSheet, String> {

}
