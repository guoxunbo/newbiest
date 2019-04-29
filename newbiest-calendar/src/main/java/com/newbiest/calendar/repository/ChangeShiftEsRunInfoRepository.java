package com.newbiest.calendar.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.calendar.model.ChangeShiftEqpStatus;
import com.newbiest.calendar.model.ChangeShiftEsRunInfo;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2017/11/5.
 */
@Repository
public interface ChangeShiftEsRunInfoRepository extends IRepository<ChangeShiftEsRunInfo, Long> {
}
