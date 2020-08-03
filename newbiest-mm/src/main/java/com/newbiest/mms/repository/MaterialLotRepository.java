package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.MaterialLot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by guoxunbo on 2019/2/20.
 */
@Repository
public interface MaterialLotRepository extends IRepository<MaterialLot, Long> {

    MaterialLot findByMaterialLotIdAndOrgRrn(String materialLotId, Long orgRrn);

    @Query("SELECT m FROM MaterialLot m, PackagedLotDetail p where p.materialLotRrn = m.objectRrn and p.packagedLotRrn = :packagedLotRrn order by m.materialLotId")
    List<MaterialLot> getPackageDetailLots(@Param("packagedLotRrn")Long packagedLotRrn);

    @Query("SELECT m FROM MaterialLot m, PackagedLotDetail p where p.materialLotRrn = m.objectRrn and p.packagedLotId in (:packagedLotId) and m.reserved16 is null")
    List<MaterialLot> getPackedDetailsAndNotReserved(@Param("packagedLotId")List<String> packagedLotId);

    MaterialLot getByLotId(@Param("lotId")String lotId);

    List<MaterialLot> getByParentMaterialLotId(@Param("parentMaterialLotId")String parentMaterialLotId);

    List<MaterialLot> getByExpressNumber(String expressNumber) throws Exception;
}
