package com.newbiest.context.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.context.model.Context;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guoxunbo on 2018/7/6.
 */
@Repository
public interface ContextRepository extends IRepository<Context, Long> {

    Context getByName(String name) throws ClientException;

}
