package com.newbiest.mms.repository;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.mms.model.LabelTemplate;
import com.newbiest.mms.model.WorkStation;
import org.springframework.stereotype.Repository;


@Repository
public interface WorkStationRepository extends IRepository<WorkStation, String> {

    WorkStation findByIpAddress(String ipAddress) throws ClientException;

}
