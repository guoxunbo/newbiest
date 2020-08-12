package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.ErpMaterialOutaOrder;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ErpMaterialOutAOrderRepository extends IRepository<ErpMaterialOutaOrder, Long> {

    List<ErpMaterialOutaOrder> findByTypeAndSynStatusNotIn(String type, List<String> asyncStatus);

    @Query("update ErpMaterialOutaOrder p set p.synStatus=:synStatus, p.errorMemo = :errorMemo where p.seq in (:seqList)")
    @Modifying
    void updateSynStatusAndErrorMemoBySeq(@Param("synStatus") String synStatus, @Param("errorMemo") String errorMemo, @Param("seqList") List<Long> seqList) throws ClientException;

}
