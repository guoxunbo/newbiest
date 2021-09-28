package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.RMAIncomingOrder;
import org.springframework.stereotype.Repository;

@Repository
public interface RMAIncomingOrderRepository extends IRepository<RMAIncomingOrder, String> {

}
