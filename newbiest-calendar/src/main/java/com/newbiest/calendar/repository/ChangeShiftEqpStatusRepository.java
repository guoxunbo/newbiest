package com.newbiest.calendar.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.calendar.model.ChangeShift;
import com.newbiest.calendar.model.ChangeShiftEqpStatus;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2017/11/5.
 */
@Repository
public interface ChangeShiftEqpStatusRepository extends IRepository<ChangeShiftEqpStatus, Long> {
}
