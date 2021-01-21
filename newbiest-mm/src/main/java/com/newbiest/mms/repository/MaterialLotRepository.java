package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.MaterialLot;
import org.springframework.data.jpa.repository.Modifying;
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

    List<MaterialLot> getByParentMaterialLotId(@Param("parentMaterialLotId")String parentMaterialLotId);

    List<MaterialLot> getByExpressNumber(String expressNumber) throws Exception;

    @Query("SELECT distinct(m.grade) FROM MaterialLot m where  m.materialName = :materialName and  m.statusCategory not in(:statusCategory) and m.packageType is null")
    List<String> getGradeByMaterialNameAndStatusCategory(@Param("materialName")String materialName, @Param("statusCategory")String statusCategory);

    @Query("SELECT distinct(m.reserved1) FROM MaterialLot m where  m.materialName = :materialName and  m.statusCategory not in(:statusCategory) and m.packageType is null")
    List<String> getSubcodeByMaterialNameAndStatusCategory(@Param("materialName")String materialName, @Param("statusCategory")String statusCategory);

    MaterialLot findByLotIdAndStatusCategoryNotIn(@Param("lotId")String lotId, @Param("statusCategory") String statusCategory);

    MaterialLot findByLotIdAndStatusCategoryInAndStatusIn(String lotId, List<String> statusCategory,  List<String> status);

    @Modifying
    @Query("DELETE FROM MaterialLot m where m.reserved48 = :importCode")
    void deleteByImportType(@Param("importCode") String importCode) throws ClientException;
}
