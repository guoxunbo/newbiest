package com.newbiest.vanchip.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.model.MaterialLot;

public interface VLMService {

    void getBoxId(MaterialLot materialLot) throws ClientException;
}
