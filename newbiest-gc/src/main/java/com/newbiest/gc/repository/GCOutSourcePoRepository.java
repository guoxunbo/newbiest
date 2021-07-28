package com.newbiest.gc.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.GCOutSourcePo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GCOutSourcePoRepository extends IRepository<GCOutSourcePo, Long> {

    @Query("SELECT distinct(g.supplierName) FROM GCOutSourcePo g")
    List<String> getSupplierName() throws Exception;

    GCOutSourcePo findByPoIdAndMaterialName(@Param("poId") String poId, @Param("materialName") String materialName);

}
