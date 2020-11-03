package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.GCProductNumberRelation;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface GCProductNumberRelationRepository extends IRepository<GCProductNumberRelation, Long> {

    GCProductNumberRelation findByProductIdAndPackageQtyAndBoxPackedQty(@Param("productId") String productId, @Param("packageQty") BigDecimal packageQty, @Param("boxPackedQty") BigDecimal boxPackedQty) throws ClientException;

    GCProductNumberRelation findByProductIdAndDefaultFlag(@Param("productId")String productId, @Param("defaultFlag") String defaultFlag) throws ClientException;
}
