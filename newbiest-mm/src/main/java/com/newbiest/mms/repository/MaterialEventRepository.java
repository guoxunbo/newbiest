package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.state.model.MaterialEvent;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2019/1/4.
 */
@Repository
public interface MaterialEventRepository extends IRepository<MaterialEvent, String> {

}
