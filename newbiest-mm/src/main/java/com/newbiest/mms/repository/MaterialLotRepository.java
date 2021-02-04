package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.MaterialLot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by guoxunbo on 2019/2/20.
 */
@Repository
public interface MaterialLotRepository extends IRepository<MaterialLot, String> {

    MaterialLot findByMaterialLotId(String materialLotId) throws ClientException;

    @Query("SELECT m FROM MaterialLot m, PackagedLotDetail p where p.materialLotRrn = m.objectRrn and p.packagedLotRrn = :packagedLotRrn")
    List<MaterialLot> getPackageDetailLots(String packagedLotRrn) throws ClientException;

    List<MaterialLot> findByIncomingDocId(String incomingDocId) throws ClientException;

    @Query("SELECT m FROM MaterialLot m, DocumentMLot d where d.materialLotId = m.materialLotId and d.documentId = :documentId")
    List<MaterialLot> findReservedLotsByDocId(String documentId) throws ClientException;

    List<MaterialLot> findByReserved45(String docLineObjRrn);

    List<MaterialLot> findByMaterialNameAndGradeAndReserved45IsNullAndBoxMaterialLotIdIsNull(String materialName, String grade);
}
