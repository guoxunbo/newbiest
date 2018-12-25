package com.newbiest.security.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.security.model.NBUserHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2018/7/12.
 */
@Repository
public interface UserHistoryRepository extends IRepository<NBUserHis, Long> {
}
