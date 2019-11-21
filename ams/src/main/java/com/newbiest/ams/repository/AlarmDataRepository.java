package com.newbiest.ams.repository;

import com.newbiest.ams.model.AlarmData;
import com.newbiest.ams.model.AlarmJob;
import com.newbiest.base.repository.custom.IRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmDataRepository extends IRepository<AlarmData, Long> {

}
