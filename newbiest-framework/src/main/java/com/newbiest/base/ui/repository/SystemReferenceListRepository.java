package com.newbiest.base.ui.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.base.ui.model.NBSystemReferenceList;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by guoxunbo on 2018/7/31.
 */
@Repository
public interface SystemReferenceListRepository extends IRepository<NBSystemReferenceList, Long> {

    List<NBSystemReferenceList> findByReferenceName(String referenceName) throws ClientException;

}
