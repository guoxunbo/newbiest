package com.newbiest.vanchip.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.vanchip.model.EmailManager;

import java.util.List;

public interface EmailManagerRepository extends IRepository<EmailManager, String> {

    List<EmailManager> findByEmailName(String emailName);
}
