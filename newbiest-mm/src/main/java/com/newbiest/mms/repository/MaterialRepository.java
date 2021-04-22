package com.newbiest.mms.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.Material;
import org.springframework.stereotype.Repository;


@Repository
public interface MaterialRepository extends IRepository<Material, String> {

}
