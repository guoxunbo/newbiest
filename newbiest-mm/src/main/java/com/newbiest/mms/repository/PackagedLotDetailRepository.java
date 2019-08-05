package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.PackagedLotDetail;
import com.newbiest.mms.state.model.MaterialEvent;
import org.springframework.stereotype.Repository;


@Repository
public interface PackagedLotDetailRepository extends IRepository<PackagedLotDetail, Long> {

    PackagedLotDetail findByPackagedLotRrnAndMaterialLotRrn(Long packagedLotRrn, Long materialLotRrn) throws ClientException;

}
