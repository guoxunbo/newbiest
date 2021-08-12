package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.MaterialLotHoldInfo;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MaterialLotHoldInfoRepository extends IRepository<MaterialLotHoldInfo, Long> {

    List<MaterialLotHoldInfo> findByMaterialLotId(String materialLotId) throws ClientException;
}
