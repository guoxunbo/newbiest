package com.newbiest.gc.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.ErpMaterialOutOrder;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErpMaterialOutOrderRepository extends IRepository<ErpMaterialOutOrder, Long> {

    List<ErpMaterialOutOrder> findBySynStatusNotIn(List<String> asyncStatus);

    @Query("update ErpMaterialOutOrder p set p.synStatus=:synStatus, p.errorMemo = :errorMemo where p.seq in (:seqList)")
    @Modifying
    void updateSynStatusAndErrorMemoBySeq(String synStatus, String errorMemo, List<Long> seqList) throws ClientException;
}
