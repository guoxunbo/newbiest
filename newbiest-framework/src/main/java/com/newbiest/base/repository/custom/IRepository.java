package com.newbiest.base.repository.custom;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBBase;
import com.newbiest.main.NewbiestConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * Created by guoxunbo on 2018/7/16.
 */
@NoRepositoryBean
public interface IRepository<T extends NBBase, ID> extends JpaRepository<T, ID>{

    List<? extends NBBase> findAll(long orgRrn) throws ClientException;
    List<? extends NBBase> findAll(long orgRrn, int maxResult, String whereClause, String orderBy) throws ClientException;
    List<? extends NBBase> findAll(long orgRrn, int firstResult, int maxResult, String whereClause, String orderBy) throws ClientException;

    boolean support(String className) throws ClientException;

    NBBase findByObjectRrn(long objectRrn) throws ClientException;
    List<? extends NBBase> findByNameAndOrgRrn(String name, long orgRrn) throws ClientException;

}


