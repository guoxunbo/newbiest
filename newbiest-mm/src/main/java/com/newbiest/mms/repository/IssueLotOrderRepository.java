package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.IncomingOrder;
import com.newbiest.mms.model.IssueLotOrder;
import org.springframework.stereotype.Repository;


@Repository
public interface IssueLotOrderRepository extends IRepository<IssueLotOrder, String> {

}
