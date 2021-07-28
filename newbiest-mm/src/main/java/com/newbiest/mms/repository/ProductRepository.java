package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.PackagedLotDetail;
import com.newbiest.mms.model.Product;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by guozhangLuo on 2020/8/11.
 */
@Repository
public interface ProductRepository extends IRepository<Product, Long> {

    List<Product> findByMaterialType(@Param("materialType") String materialType) throws ClientException;


}
