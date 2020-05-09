package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.MaterialHistory;
import com.newbiest.mms.model.RawMaterial;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2019/2/20.
 */
@Repository
public interface MaterialHistoryRepository extends IRepository<MaterialHistory, String> {

}
