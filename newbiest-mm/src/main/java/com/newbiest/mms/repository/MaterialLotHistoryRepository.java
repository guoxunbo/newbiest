package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotHistory;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2019/2/20.
 */
@Repository
public interface MaterialLotHistoryRepository extends IRepository<MaterialLotHistory, Long> {

    MaterialLotHistory findTopByMaterialLotIdAndTransTypeOrderByCreatedDesc(String materialLotId, String transType);

}
