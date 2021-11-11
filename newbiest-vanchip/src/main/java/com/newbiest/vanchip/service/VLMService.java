package com.newbiest.vanchip.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.model.MaterialLot;

import java.util.List;

public interface VLMService {

    String getBoxId(MaterialLot materialLot) throws ClientException;

    void bindReelToBox(MaterialLot boxMaterialLot, List<MaterialLot> materialLots) throws ClientException;

    void unbindReelToBox(String customerBoxId) throws ClientException;
}
