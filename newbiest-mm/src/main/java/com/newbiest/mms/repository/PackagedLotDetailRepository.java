package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.PackagedLotDetail;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PackagedLotDetailRepository extends IRepository<PackagedLotDetail, String> {

    PackagedLotDetail findByPackagedLotRrnAndMaterialLotRrn(String packagedLotRrn, String materialLotRrn) throws ClientException;

    List<PackagedLotDetail> findByPackagedLotRrn(String packagedLotRrn) throws ClientException;

    @Modifying
    @Query("DELETE FROM PackagedLotDetail WHERE packagedLotRrn = :packagedLotRrn")
    void deleteByPackagedLotRrn(String packagedLotRrn) throws ClientException;
}
