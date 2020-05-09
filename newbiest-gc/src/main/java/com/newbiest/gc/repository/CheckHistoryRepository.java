package com.newbiest.gc.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.gc.model.CheckHistory;
import org.springframework.stereotype.Repository;


@Repository
public interface CheckHistoryRepository extends IRepository<CheckHistory, String> {

}
