package com.newbiest.base.ui.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.base.ui.model.NBTable;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2017/11/5.
 */
@Repository
public interface TableRepository extends IRepository<NBTable, Long> {

}
