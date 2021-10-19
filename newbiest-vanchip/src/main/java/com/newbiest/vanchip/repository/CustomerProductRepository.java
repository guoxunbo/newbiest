package com.newbiest.vanchip.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.vanchip.model.CustomerProduct;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerProductRepository extends IRepository<CustomerProduct, String> {

    CustomerProduct findByPartNumberAndCustomerName(String PartNumber, String customerName);
}