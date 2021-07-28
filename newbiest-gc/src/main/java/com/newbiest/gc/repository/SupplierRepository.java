package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.Supplier;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface SupplierRepository extends IRepository<Supplier, Long> {

    Supplier getByNameAndType(@Param("name") String name, @Param("type") String type) throws ClientException;

}
