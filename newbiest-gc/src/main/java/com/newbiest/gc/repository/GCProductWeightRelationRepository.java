package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.GCProductWeightRelation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GCProductWeightRelationRepository extends IRepository<GCProductWeightRelation, Long> {

    @Query("SELECT p FROM GCProductWeightRelation p where  p.productId = :productId")
    List<GCProductWeightRelation> findByProductId(@Param("productId")String productId) throws ClientException;

    GCProductWeightRelation findByProductIdAndPackageQty(@Param("productId")String productId, @Param("packageQty") BigDecimal packageQty) throws ClientException;

}
