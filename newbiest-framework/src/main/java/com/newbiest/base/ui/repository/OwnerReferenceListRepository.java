package com.newbiest.base.ui.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by guoxunbo on 2018/7/31.
 */
@Repository
public interface OwnerReferenceListRepository extends IRepository<NBOwnerReferenceList, Long> {

    List<NBOwnerReferenceList> findByReferenceNameAndOrgRrn(String referenceName, Long orgRrn) throws ClientException;

}
