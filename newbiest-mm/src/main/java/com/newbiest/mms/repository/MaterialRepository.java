package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.Material;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by guoxunbo on 2019/2/20.
 */
@Repository
public interface MaterialRepository extends IRepository<Material, Long> {

    @Query("SELECT distinct(m.name) FROM Material m where  m.materialCategory in(:materialCategory)")
    List<String> findNameByMaterialCategory(@Param("materialCategory")List<String> materialCategory) throws ClientException;
}
