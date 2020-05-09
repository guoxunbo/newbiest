package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.Storage;
import org.springframework.stereotype.Repository;


@Repository
public interface StorageRepository extends IRepository<Storage, String> {

    Storage findByWarehouseRrnAndName(String warehouseRrn, String name) throws ClientException;
}
