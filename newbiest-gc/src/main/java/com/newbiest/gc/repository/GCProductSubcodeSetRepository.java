package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.GCProductSubcode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GCProductSubcodeSetRepository extends IRepository<GCProductSubcode, Long> {

    GCProductSubcode findByProductIdAndSubcode(@Param("productId") String productId, @Param("subcode")  String subcode) throws ClientException;

}
