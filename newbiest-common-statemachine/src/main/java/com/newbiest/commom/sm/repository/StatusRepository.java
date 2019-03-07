package com.newbiest.commom.sm.repository;

import com.alibaba.druid.support.spring.stat.annotation.Stat;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.commom.sm.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2017/11/5.
 */
@Repository
public interface StatusRepository extends IRepository<Status, Long> {

}
