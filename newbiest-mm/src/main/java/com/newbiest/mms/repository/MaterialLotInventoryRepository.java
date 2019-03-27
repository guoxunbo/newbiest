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

    /**
     * 一个批次可以在多个仓库中，但是一个仓库中只能存在一个相同批次号的物料批次
     */
    MaterialLotInventory findByMaterialLotRrnAndWarehouseRrn(long materialLotRrn, long warehouseRrn) throws ClientException;

}
