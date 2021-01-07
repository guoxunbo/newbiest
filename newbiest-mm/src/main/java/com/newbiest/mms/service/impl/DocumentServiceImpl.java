package com.newbiest.mms.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.CollectorsUtils;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.mms.exception.DocumentException;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.DocumentService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.state.model.MaterialEvent;
import com.newbiest.mms.state.model.MaterialStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    GeneratorService generatorService;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    DocumentLineRepository documentLineRepository;

    @Autowired
    MmsService mmsService;

    @Autowired
    IncomingOrderRepository incomingOrderRepository;

    @Autowired
    IssueLotOrderRepository issueLotOrderRepository;

    @Autowired
    IssueMaterialOrderRepository issueMaterialOrderRepository;

    @Autowired
    DocumentMLotRepository documentMLotRepository;

    @Autowired
    MaterialLotRepository materialLotRepository;

    /**
     * 创建发料单
     *  此种发料单需要指定批次，即发料单直接指定批次一般用于主材使用
     * @param documentId 单据号 不传，系统会自己生成一个
     * @param approveFlag 是否创建即approve
     * @param materialLotIdList 物料批次号
     * @throws ClientException
     */
    public void createIssueLotOrder(String documentId, boolean approveFlag, List<String> materialLotIdList) throws ClientException{
        try {
            if (StringUtils.isNullOrEmpty(documentId)) {
                documentId = generatorDocId(IssueLotOrder.GENERATOR_ISSUE_LOT_ORDER_ID_RULE);
            }
            IssueLotOrder issueLotOrder = issueLotOrderRepository.findOneByName(documentId);
            if (issueLotOrder != null) {
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_EXIST, documentId);
            }
            List<MaterialLot> materialLots = materialLotIdList.stream().map(materialLotId -> mmsService.getMLotByMLotId(materialLotId, true)).collect(Collectors.toList());
            BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));

            issueLotOrder = new IssueLotOrder();
            issueLotOrder.setName(documentId);
            issueLotOrder.setQty(totalQty);
            issueLotOrder.setUnHandledQty(totalQty);
            if (approveFlag) {
                issueLotOrder.setStatus(Document.STATUS_APPROVE);
            }
            issueLotOrder = (IssueLotOrder) baseService.saveEntity(issueLotOrder);

            for (MaterialLot materialLot : materialLots) {
                materialLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_ISSUE_RESERVED, StringUtils.EMPTY);
                baseService.saveHistoryEntity(materialLot, MaterialEvent.EVENT_ISSUE_RESERVED);

                DocumentMLot documentMLot = new DocumentMLot();
                documentMLot.setDocumentId(issueLotOrder.getName());
                documentMLot.setMaterialLotId(materialLot.getMaterialLotId());
                documentMLotRepository.save(documentMLot);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 创建发料单
     *  此种发料单不指定批次，只指定型号 一般用于源材料的使用
     * @param documentId 单据号 不传，系统会自己生成一个
     * @param approveFlag 是否创建即approve
     * @param rawMaterialQtyMap 物料对应的数量
     * @throws ClientException
     */
    public void createIssueMaterialOrder(String documentId, boolean approveFlag, Map<String, BigDecimal> rawMaterialQtyMap) throws ClientException{
        try {
            if (StringUtils.isNullOrEmpty(documentId)) {
                documentId = generatorDocId(IssueMaterialOrder.GENERATOR_ISSUE_MATERIAL_ORDER_ID_RULE);
            }
            IssueMaterialOrder issueMaterialOrder = issueMaterialOrderRepository.findOneByName(documentId);
            if (issueMaterialOrder != null) {
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_EXIST, documentId);
            }
            BigDecimal totalQty = rawMaterialQtyMap.values().stream().reduce(BigDecimal::add).get();

            issueMaterialOrder = new IssueMaterialOrder();
            issueMaterialOrder.setName(documentId);
            issueMaterialOrder.setQty(totalQty);
            issueMaterialOrder.setUnHandledQty(totalQty);
            if (approveFlag) {
                issueMaterialOrder.setStatus(Document.STATUS_APPROVE);
            }
            issueMaterialOrder = (IssueMaterialOrder) baseService.saveEntity(issueMaterialOrder);

            for (String rawMaterialName : rawMaterialQtyMap.keySet()) {
                RawMaterial rawMaterial = mmsService.getRawMaterialByName(rawMaterialName);
                if (rawMaterial == null) {
                    throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_IS_NOT_EXIST, rawMaterialName);
                }
                DocumentLine documentLine = new DocumentLine();
                documentLine.setDocId(issueMaterialOrder.getName());
                documentLine.setDocRrn(issueMaterialOrder.getObjectRrn());
                documentLine.setMaterial(rawMaterial);
                documentLine.setQty(rawMaterialQtyMap.get(rawMaterialName));
                documentLine.setUnHandledQty(rawMaterialQtyMap.get(rawMaterialName));
                baseService.saveEntity(documentLine);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 发料
     *  根据物料清单发料，不卡控物料批次。只验证发料规则
     * @param documentLine 发料明细单
     * @param materialLotIdList 发料的物料批次
     * @throws ClientException
     */
    public void issueMLotByDocLine(DocumentLine documentLine, List<String> materialLotIdList) throws ClientException {
        try {
            IssueMaterialOrder issueMaterialOrder = issueMaterialOrderRepository.findByObjectRrn(documentLine.getObjectRrn());
            if (issueMaterialOrder == null) {
                throw new ClientParameterException(DOCUMENT_IS_NOT_EXIST, documentLine.getDocId());
            }
            if (!Document.STATUS_APPROVE.equals(issueMaterialOrder.getStatus())) {
                throw new ClientParameterException(DOCUMENT_STATUS_IS_NOT_ALLOW, documentLine.getDocId());
            }
            // TODO 维护单据规则，以及验证规则
            List<MaterialLot> materialLots = materialLotIdList.stream().map(materialLotId -> mmsService.getMLotByMLotId(materialLotId, true)).collect(Collectors.toList());
            BigDecimal handleQty = BigDecimal.ZERO;

            for (MaterialLot materialLot : materialLots) {
                handleQty = handleQty.add(materialLot.getCurrentQty());
                mmsService.issue(materialLot);
            }

            documentLine.setHandledQty(documentLine.getHandledQty().add(handleQty));
            documentLine.setUnHandledQty(documentLine.getUnHandledQty().subtract(handleQty));
            baseService.saveEntity(documentLine, DocumentHistory.TRANS_TYPE_ISSUE);

            issueMaterialOrder.setHandledQty(issueMaterialOrder.getHandledQty().add(handleQty));
            issueMaterialOrder.setUnHandledQty(issueMaterialOrder.getUnHandledQty().subtract(handleQty));
            baseService.saveEntity(issueMaterialOrder, DocumentHistory.TRANS_TYPE_ISSUE);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public List<MaterialLot> getReservedMLotByDocId(String documentId) throws ClientException {
        return materialLotRepository.findReservedLotsByDocId(documentId);
    }

    /**
     * 发料
     *  不根据物料发料，单据会事先绑好物料批次进行发料
     * @param issueLotOrderId 发料单号
     * @param materialLotIdList 发料的物料批次
     * @throws ClientException
     */
    public void issueMLotByDoc(String issueLotOrderId, List<String> materialLotIdList) throws ClientException {
        try {
            IssueLotOrder issueLotOrder = issueLotOrderRepository.findOneByName(issueLotOrderId);
            if (issueLotOrder == null) {
                throw new ClientParameterException(DOCUMENT_IS_NOT_EXIST, issueLotOrder);
            }
            if (!Document.STATUS_APPROVE.equals(issueLotOrder.getStatus())) {
                throw new ClientParameterException(DOCUMENT_STATUS_IS_NOT_ALLOW, issueLotOrder.getName());
            }
            List<MaterialLot> materialLots = getReservedMLotByDocId(issueLotOrder.getName());

            BigDecimal handleQty = BigDecimal.ZERO;
            for (String materialLotId : materialLotIdList) {
                Optional<MaterialLot> existMaterialLotOptional = materialLots.stream().filter(materialLot -> materialLot.getMaterialLotId().equals(materialLotId)).findFirst();
                if (!existMaterialLotOptional.isPresent()) {
                    throw new ClientParameterException(DOCUMENT_NOT_RESERVED_MLOT, materialLotId);
                }
                MaterialLot materialLot = existMaterialLotOptional.get();
                if (!MaterialStatus.STATUS_RESERVED.equals(materialLot.getStatus())) {
                    throw new ClientParameterException(DOCUMENT_NOT_RESERVED_MLOT, materialLotId);
                }
                handleQty = handleQty.add(materialLot.getCurrentQty());
                mmsService.issue(materialLot);
            }
            issueLotOrder.setHandledQty(issueLotOrder.getHandledQty().add(handleQty));
            issueLotOrder.setUnHandledQty(issueLotOrder.getUnHandledQty().subtract(handleQty));
            baseService.saveEntity(issueLotOrder, DocumentHistory.TRANS_TYPE_ISSUE);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 来料接收
     *  此时是实物的接收。物料批次在来料导入的时候已经记录
     *  接收时候要验证是否都是同一个单据的同一个单据的才能一起接收
     * @param documentId 来料单据号
     * @param materialLots 接收的物料批次
     * @throws ClientException
     */
    public void receiveIncomingLot(String documentId, List<MaterialLot> materialLots) throws ClientException{
        try {
            IncomingOrder incomingOrder = incomingOrderRepository.findOneByName(documentId);
            if (incomingOrder  == null) {
                throw new ClientParameterException(DOCUMENT_IS_NOT_EXIST);
            }
            if (!Document.STATUS_APPROVE.equals(incomingOrder.getStatus())) {
                throw new ClientParameterException(DOCUMENT_STATUS_IS_NOT_ALLOW);
            }
            Map<String, List<MaterialLot>> docMaterialLots = materialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getIncomingDocId));
            if (docMaterialLots.keySet().size() != 1 || !documentId.equals(materialLots.get(0).getIncomingDocId())) {
                throw new ClientException(DOCUMENT_IS_NOT_SAME);
            }
            BigDecimal receiveQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));
            if (incomingOrder.getUnHandledQty().compareTo(receiveQty) < 0) {
                throw new ClientParameterException(DOCUMENT_QTY_NOT_ENOUGH, documentId);
            }

            Map<String, List<MaterialLot>> materialLotMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getMaterialName));
            for (String materialName : materialLotMap.keySet()) {
                RawMaterial rawMaterial = mmsService.getRawMaterialByName(materialName);
                mmsService.receiveMLot(rawMaterial, materialLotMap.get(materialName));
            }
            incomingOrder.setHandledQty(incomingOrder.getHandledQty().add(receiveQty));
            incomingOrder.setUnHandledQty(incomingOrder.getUnHandledQty().subtract(receiveQty));
            incomingOrderRepository.save(incomingOrder);
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
            baseService.saveEntity(document, DocumentHistory.TRANS_TYPE_APPROVE);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public String generatorDocId(String generatorRule) throws ClientException {
        GeneratorContext generatorContext = new GeneratorContext();
        generatorContext.setRuleName(generatorRule);
        return generatorService.generatorId(generatorContext);
    }

}
