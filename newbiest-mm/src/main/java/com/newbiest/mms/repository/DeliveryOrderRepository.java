package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.DeliveryOrder;
import org.springframework.stereotype.Repository;


@Repository
public interface DeliveryOrderRepository extends IRepository<DeliveryOrder, Long> {

}
