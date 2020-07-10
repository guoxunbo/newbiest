package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.MesPackedLotRelation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesPackedLotRelationRepository extends IRepository<MesPackedLotRelation, Long> {

    MesPackedLotRelation findByPackedLotRrn(Long packedLotRrn) throws ClientException;
}
