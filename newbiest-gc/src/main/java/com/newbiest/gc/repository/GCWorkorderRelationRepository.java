package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.GCWorkorderRelation;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GCWorkorderRelationRepository extends IRepository<GCWorkorderRelation, Long> {

    GCWorkorderRelation findByWorkOrderIdAndGradeIsNullAndBoxIdIsNull(@Param("workOrderId")String workOrderId) throws ClientException;

    GCWorkorderRelation findByGradeAndWorkOrderIdIsNullAndBoxIdIsNull(@Param("grade")String grade) throws ClientException;

    GCWorkorderRelation findByBoxIdAndWorkOrderIdAndGrade(@Param("boxId")String boxId, @Param("workOrderId")String workOrderId, @Param("grade")String grade) throws ClientException;

    GCWorkorderRelation findByBoxIdAndWorkOrderIdIsNullAndGradeIsNull(@Param("boxId")String boxId) throws ClientException;

    GCWorkorderRelation findByWorkOrderIdAndGradeAndBoxIdIsNull(@Param("workOrderId")String workOrderId, @Param("grade")String grade) throws ClientException;

    GCWorkorderRelation findByWorkOrderIdAndBoxIdAndGradeIsNull(@Param("workOrderId")String workOrderId, @Param("boxId")String boxId) throws ClientException;

    GCWorkorderRelation findByBoxIdAndGradeAndWorkOrderIdIsNull(@Param("boxId")String boxId, @Param("grade")String grade) throws ClientException;
}