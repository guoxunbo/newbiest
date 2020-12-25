package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.Document;
import com.newbiest.mms.model.IncomingOrder;
import org.springframework.stereotype.Repository;


@Repository
public interface IncomingOrderRepository extends IRepository<IncomingOrder, String> {

}
