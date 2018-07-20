package com.newbiest.base.repository;

import com.newbiest.base.model.NBMessage;
import com.newbiest.base.repository.custom.IRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2018/7/20.
 */
@Repository
public interface MessageRepository extends IRepository<NBMessage, Long> {

}
