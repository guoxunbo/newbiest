package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.CheckOrder;
import org.springframework.stereotype.Repository;


@Repository
public interface CheckOrderRepository extends IRepository<CheckOrder, String> {

}
