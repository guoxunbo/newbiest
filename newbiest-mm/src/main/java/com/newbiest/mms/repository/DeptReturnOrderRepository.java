package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.DeptReturnOrder;
import org.springframework.stereotype.Repository;

/**
 * 部门退料单
 */
@Repository
public interface DeptReturnOrderRepository extends IRepository<DeptReturnOrder, String> {

}
