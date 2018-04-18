package com.newbiest.base.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBQuery;
import com.newbiest.base.repository.custom.TableRepositoryCustom;
import com.newbiest.base.ui.model.NBTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2017/11/5.
 */
@Repository
public interface TableRepository extends JpaRepository <NBTable, Long>, TableRepositoryCustom {

    NBTable getByName(String name) throws ClientException;

}
