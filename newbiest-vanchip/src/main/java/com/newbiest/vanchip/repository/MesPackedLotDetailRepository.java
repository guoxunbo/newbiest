package com.newbiest.vanchip.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.vanchip.model.MesPackedLotDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesPackedLotDetailRepository extends IRepository<MesPackedLotDetail, Long> {

    List<MesPackedLotDetail> findByPackedLotRrn(Long packedLotRrn);
}
