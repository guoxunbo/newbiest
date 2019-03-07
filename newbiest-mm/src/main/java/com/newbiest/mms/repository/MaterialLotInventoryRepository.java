package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.MaterialLotInventory;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2019/2/20.
 */
@Repository
public interface MaterialLotInventoryRepository extends IRepository<MaterialLotInventory, Long> {

    MaterialLotInventory findByMaterialLotRrn(long materialLotRrn) throws ClientException;

}
