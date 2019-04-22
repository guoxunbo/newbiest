package com.newbiest.calendar.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.calendar.model.ChangeShift;
import com.newbiest.calendar.model.ChangeShiftHistory;
import org.springframework.stereotype.Repository;


@Repository
public interface ChangeShiftHistroyRepository extends IRepository<ChangeShiftHistory, Long> {
}
