package com.newbiest.vanchip.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.vanchip.model.MesPackedLot;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesPackedLotRepository extends IRepository<MesPackedLot, Long> {

    List<MesPackedLot> findByBoxIdIn(List<String> materialLotIds);
}
