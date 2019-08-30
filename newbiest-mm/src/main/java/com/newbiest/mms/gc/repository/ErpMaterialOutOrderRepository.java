package com.newbiest.mms.gc.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.gc.model.ErpMaterialOutOrder;
import com.newbiest.mms.gc.model.ErpSo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErpMaterialOutOrderRepository extends IRepository<ErpMaterialOutOrder, Long> {

    List<ErpMaterialOutOrder> findBySynStatusNotIn(List<String> asyncStatus);

}
