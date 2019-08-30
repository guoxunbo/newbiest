package com.newbiest.mms.gc.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.gc.model.ErpSo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErpSoRepository extends IRepository<ErpSo, Long> {

    List<ErpSo> findByTypeAndSynStatusNotIn(String type, List<String> asyncStatus);

}
