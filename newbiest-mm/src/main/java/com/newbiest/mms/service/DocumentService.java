package com.newbiest.mms.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.mms.model.Document;
import com.newbiest.mms.model.MaterialLot;

import java.util.List;

/**
 * 单据相关service
 * @author guoxunbo
 * @date 12/24/20 2:36 PM
 */
public interface DocumentService {

    void receiveIncomingLot(String documentId, List<MaterialLot> materialLots) throws ClientException;
    void approveDocument(Document document) throws ClientException;


}
