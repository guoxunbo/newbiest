package com.newbiest.base.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBRelation;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by guoxunbo on 2017/10/13.
 */
@Repository
public interface RelationRepository extends IRepository<NBRelation, Long> {

    List<NBRelation> findBySource(String source) throws ClientException;

}
