package com.newbiest.base.ui.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.base.ui.model.NBReferenceTable;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2018/8/1.
 */
@Repository
public interface ReferenceTableRepository extends IRepository<NBReferenceTable, Long> {
}
