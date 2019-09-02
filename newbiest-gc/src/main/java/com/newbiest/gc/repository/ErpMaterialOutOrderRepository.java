package com.newbiest.gc.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.ErpMaterialOutOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErpMaterialOutOrderRepository extends IRepository<ErpMaterialOutOrder, Long> {

    List<ErpMaterialOutOrder> findBySynStatusNotIn(List<String> asyncStatus);

}
