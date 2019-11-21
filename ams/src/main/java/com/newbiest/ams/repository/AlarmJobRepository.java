package com.newbiest.ams.repository;

import com.newbiest.ams.model.AlarmJob;
import com.newbiest.base.repository.custom.IRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmJobRepository extends IRepository<AlarmJob, Long> {

    List<AlarmJob> findByCategoryAndType(String category, String type);

}
