package com.newbiest.im.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.im.model.WSDefinition;
import org.springframework.stereotype.Repository;

@Repository
public interface WsDefinitionRepository extends IRepository<WSDefinition, String> {

    WSDefinition findByImIdAndEnv(String imId, String env) throws ClientException;

}
