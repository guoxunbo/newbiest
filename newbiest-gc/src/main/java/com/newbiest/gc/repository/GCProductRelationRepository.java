package com.newbiest.gc.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.GCProductRelation;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GCProductRelationRepository extends IRepository<GCProductRelation, Long> {

    GCProductRelation findByProductIdAndGradeSubcodeAndType(@Param("productId") String productId,@Param("gradeSubcode")String gradeSubcode, @Param("type")String type);

}
