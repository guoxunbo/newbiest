package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.GCProductModelConversion;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GCProductModelConversionRepository extends IRepository<GCProductModelConversion, Long> {

    GCProductModelConversion findByProductId(@Param("productId") String productId) throws ClientException;

    GCProductModelConversion findByProductIdAndConversionModelId(@Param("productId") String productId, @Param("conversionModelId") String conversionModelId) throws ClientException;


}
