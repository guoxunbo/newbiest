package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.Product;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends IRepository<Product, String> {

}