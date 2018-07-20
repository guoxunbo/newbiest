package com.newbiest.base.ui.repository;

import com.alibaba.druid.sql.builder.SQLBuilderFactory;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.factory.SqlBuilderFactory;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.repository.custom.TableRepositoryCustom;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2017/11/5.
 */
@Repository
public interface TableRepository extends IRepository<NBTable, Long>, TableRepositoryCustom {

}
