package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.MaterialLotHold;
import com.newbiest.mms.state.model.MaterialEvent;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialLotHoldRepository extends IRepository<MaterialLotHold, String> {

    List<MaterialLotHold> findByMaterialLotId(String materialLotId) throws ClientException;

}
