package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.Customer;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerRepository extends IRepository<Customer, Long> {

    Customer getByName(String name) throws ClientException;

}
