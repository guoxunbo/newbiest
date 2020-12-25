package com.newbiest.mms.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.CollectorsUtils;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.DocumentLineRepository;
import com.newbiest.mms.repository.DocumentRepository;
import com.newbiest.mms.repository.IncomingOrderRepository;
import com.newbiest.mms.service.DocumentService;
import com.newbiest.mms.service.MmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.newbiest.mms.exception.DocumentException.*;

/**
 * @author guoxunbo
 * @date 12/24/20 2:36 PM
 */
@Transactional
@Component
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    BaseService baseService;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    DocumentLineRepository documentLineRepository;

    @Autowired
    MmsService mmsService;

    @Autowired
    IncomingOrderRepository incomingOrderRepository;

    /**
     * 新增单据
     * @param document
     * @throws ClientException
     */
    private Document createDocument(Document document) throws ClientException {
        try {
            document = (Document) baseService.saveEntity(document);
            documentLineRepository.deleteByDocRrn(document.getObjectRrn());
            if (CollectionUtils.isNotEmpty(document.getDocumentLines())) {
                for (DocumentLine documentLine : document.getDocumentLines()) {
                    documentLine.setDocRrn(document.getObjectRrn());
                    documentLine.setDocId(document.getName());
                    baseService.saveEntity(documentLine);
                }
            }
            return document;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 创建接收单，以及创建物料批次
     * @param documentId
     *
     * @throws ClientException
     */
    public void createIncomingOrder(String documentId, List<MaterialLot> materialLots) throws ClientException {
        try {
            IncomingOrder incomingOrder = incomingOrderRepository.findOneByName(documentId);
            if (incomingOrder != null) {
                throw new ClientParameterException(DOCUMENT_IS_EXIST);
            }
            incomingOrder = new IncomingOrder();
            incomingOrder.setName(documentId);
            incomingOrder.setDescription(documentId);
            BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getReceiveQty));
            incomingOrder.setQty(totalQty);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 审核单据
     * @param document
     * @throws ClientException
     */
    @Override
    public void approveDocument(Document document) throws ClientException {
        try {
            document = documentRepository.findByObjectRrn(document.getObjectRrn());

            document.setStatus(Document.STATUS_APPROVE);
            document.setApproveTime(DateUtils.now());
            document.setApproveUser(ThreadLocalContext.getUsername());
            baseService.saveEntity(document, DocumentHistory.TRANS_TYPE_APPROVE);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }




}
