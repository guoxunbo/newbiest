package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.GCWorkorderRelation;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GCWorkorderRelationRepository extends IRepository<GCWorkorderRelation, Long> {

    GCWorkorderRelation findByWorkOrderIdAndGrade(@Param("workOrderId")String workOrderId, @Param("grade")String grade) throws ClientException;

    GCWorkorderRelation findByWorkOrderIdAndGradeIsNull(@Param("workOrderId")String workOrderId) throws ClientException;

    GCWorkorderRelation findByGradeAndWorkOrderIdIsNull(@Param("grade")String grade) throws ClientException;

}
