package com.newbiest.gc.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.MesGcWltUpload;
import org.springframework.stereotype.Repository;

@Repository
public interface MesGcWltUploadRepository extends IRepository<MesGcWltUpload, Long> {

    MesGcWltUpload findByWaferId(String waferId) throws Exception;
}
