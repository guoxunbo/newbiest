package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.RawMaterial;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by guoxunbo on 2019/2/20.
 */
@Repository
public interface MaterialLotRepository extends IRepository<MaterialLot, Long> {

    MaterialLot findByMaterialLotIdAndOrgRrn(String materialLotId, Long orgRrn);

    MaterialLot findByMaterialLotIdAndCategoryAndOrgRrn(String materialLotId, String category, Long orgRrn);

    @Query("SELECT m FROM MaterialLot m, PackagedLotDetail p where p.materialLotRrn = m.objectRrn and p.packagedLotRrn = :packagedLotRrn")
    List<MaterialLot> getPackageDetailsLot(Long packagedLotRrn);
}
