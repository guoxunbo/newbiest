package com.newbiest.mms.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.CollectorsUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.DocumentException;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.DocumentService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.state.model.MaterialEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
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

    @Autowired
    ReturnOrderRepository returnOrderRepository;

    @Autowired
    DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    IssueFinishGoodOrderRepository issueFinishGoodOrderRepository;

    @Autowired
    IssueMLotOrderRepository issueMLotOrderRepository;

    //by客户版本备货的规则
    public static final String RESERVED_RULE_BY_CUSTOMER_VERSION = "ReservedRuleByCustomerVersion";
    //by客户版本和MRB备货的规则
    public static final String RESERVED_RULE_BY_CUSTOMER_VERSION_AND_MRB = "ReservedRuleByCustomerVersionAndMrb";
    //by客户产品备货的规则
    public static final String RESERVED_RULE_BY_CUSTOMER_PRODUCT = "ReservedRuleByCustomerProduct";
    //by客户产品和MRB备货的规则
    public static final String RESERVED_RULE_BY_CUSTOMER_PRODUCT_AND_MRB = "ReservedRuleByCustomerProductAndMrb";

    /**
     * 创建发货单
     * @param documentId         单据号 不传，系统会自己生成一个
     * @param approveFlag        是否创建即审核
     * @param documentLineList   需子单号
     * @throws ClientException
     */
    public String createDeliveryOrder(String documentId, boolean approveFlag, List<DocumentLine> documentLineList) throws ClientException {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd");

            if (StringUtils.isNullOrEmpty(documentId)) {
                documentId = generatorDocId(DeliveryOrder.GENERATOR_DELIVERY_ORDER_ID_RULE);
            }
            DeliveryOrder deliveryOrder = deliveryOrderRepository.findOneByName(documentId);
            if (deliveryOrder != null) {
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_EXIST, documentId);
            }

            List<DocumentLine> docLineList = documentLineList.stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(
                            Comparator.comparing(DocumentLine::getLineId))), ArrayList::new));
            BigDecimal totalQty = docLineList.stream().collect(CollectorsUtils.summingBigDecimal(DocumentLine::getQty));

            deliveryOrder = new DeliveryOrder();
            deliveryOrder.setName(documentId);
            deliveryOrder.setQty(totalQty);
            deliveryOrder.setUnHandledQty(totalQty);
            deliveryOrder.setStatus(Document.STATUS_CREATE);
            if (approveFlag) {
                deliveryOrder.setStatus(Document.STATUS_APPROVE);
            }
            deliveryOrder = (DeliveryOrder) baseService.saveEntity(deliveryOrder);

            Map<String, List<DocumentLine>> documentLinesMap = documentLineList.stream().collect(Collectors.groupingBy(DocumentLine::getLineId));
            List<MaterialLot> materialLots = materialLotRepository.findByMaterialCategory(Material.TYPE_PRODUCT);
            for (String documentLineId : documentLinesMap.keySet()) {
                //同一笔发货单
                List<DocumentLine> documentLines = documentLinesMap.get(documentLineId);

                DocumentLine documentLine = documentLineRepository.findByLineId(documentLineId);
                if (documentLine != null){
                    throw new ClientParameterException(DOCUMENT_IS_EXIST, documentLineId);
                }
                documentLine = documentLines.get(0);
                documentLine.setDocument(deliveryOrder);
                documentLine.setLineId(documentLineId);
                documentLine.setUnHandledQty(documentLine.getQty());

                if(!StringUtils.isNullOrEmpty(documentLine.getShippingDateValue())){
                    String shippingDate = formats.format(simpleDateFormat.parse(documentLine.getShippingDateValue()));
                    documentLine.setShippingDate(formats.parse(shippingDate));
                }
                documentLine.setReservedQty(BigDecimal.ZERO);
                documentLine.setUnReservedQty(documentLine.getQty());

                for (DocumentLine line : documentLines) {
                    //判断发货单是什么规则
                    if(!StringUtils.isNullOrEmpty(line.getCustomerProduct()) && StringUtils.isNullOrEmpty(line.getCustomerVersion()) && StringUtils.isNullOrEmpty(line.getReelCodeId())){
                        //根据客户产品
                        line.setReserved24(RESERVED_RULE_BY_CUSTOMER_PRODUCT);
                        if (!StringUtils.isNullOrEmpty(line.getReserved5())){
                            line.setReserved24(RESERVED_RULE_BY_CUSTOMER_PRODUCT_AND_MRB);
                        }
                    }else if (!StringUtils.isNullOrEmpty(line.getCustomerVersion()) && StringUtils.isNullOrEmpty(line.getCustomerProduct()) && StringUtils.isNullOrEmpty(line.getReelCodeId())){
                        //根据客户版本
                        line.setReserved24(RESERVED_RULE_BY_CUSTOMER_VERSION);
                        if (!StringUtils.isNullOrEmpty(line.getReserved5())){
                            line.setReserved24(RESERVED_RULE_BY_CUSTOMER_VERSION_AND_MRB);
                        }
                    }else if(!StringUtils.isNullOrEmpty(line.getReelCodeId()) && StringUtils.isNullOrEmpty(line.getCustomerProduct()) && StringUtils.isNullOrEmpty(line.getCustomerVersion())) {
                        //根据ReeL NO
                        List<MaterialLot> mlots = materialLots.stream().filter(mLot -> line.getReelCodeId().equals(mLot.getMaterialLotId())).collect(Collectors.toList());
                        if (CollectionUtils.isEmpty(mlots)){
                            throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, line.getLineId());
                        }
                        MaterialLot materialLot = mlots.get(0);
                        if(!StringUtils.isNullOrEmpty(materialLot.getReserved45())){
                            throw new ClientParameterException(DocumentException.MATERIAL_LOT_ALREADY_RESERVED, line.getLineId());
                        }
                        // 进行预备货，物料绑定单据号，还是需要去做备货动作
                        materialLot.setReserved45(documentLine.getLineId());
                        baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_PRE_RESERVED);
                    }else {
                        throw new ClientParameterException(DocumentException.IMPORT_TEMPLATE_ERROR);
                    }
                }
                baseService.saveEntity(documentLine);
            }
            return documentId;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 创建退料单
     * 这个退料单指产线退回仓库。而非指仓库退料到ERP
     *
     * @param documentId         单据号 不传，系统会自己生成一个
     * @param approveFlag        是否创建即approve
     * @param materialLotActions 物料批次动作
     * @throws ClientException
     */
    public void createReturnOrder(String documentId, boolean approveFlag, List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            if (StringUtils.isNullOrEmpty(documentId)) {
                documentId = generatorDocId(ReturnOrder.GENERATOR_RETURN_ORDER_RULE);
            }
            ReturnOrder returnOrder = returnOrderRepository.findOneByName(documentId);
            if (returnOrder != null) {
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_EXIST, documentId);
            }

            List<MaterialLot> materialLots = materialLotActions.stream().map(action -> mmsService.getMLotByMLotId(action.getMaterialLotId(), true)).collect(Collectors.toList());
            Map<String, MaterialLotAction> materialLotActionMap = materialLotActions.stream().collect(Collectors.toMap(MaterialLotAction::getMaterialLotId, Function.identity()));

            BigDecimal totalQty = materialLotActions.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLotAction::getTransQty));
            returnOrder = new ReturnOrder();
            returnOrder.setName(documentId);
            returnOrder.setQty(totalQty);
            returnOrder.setUnHandledQty(totalQty);
            if (approveFlag) {
                returnOrder.setStatus(Document.STATUS_APPROVE);
            }
            returnOrder = (ReturnOrder) baseService.saveEntity(returnOrder);

            //验证物料是否被create状态的单据绑定
            List<String> materialLotIds = materialLotActions.stream().map(materialLotAction -> materialLotAction.getMaterialLotId()).collect(Collectors.toList());
            validationMLotBoundOrder(materialLotIds);
            
            for (MaterialLot materialLot : materialLots) {
                MaterialLotAction materialLotAction = materialLotActionMap.get(materialLot.getMaterialLotId());
                materialLot.setCurrentQty(materialLotAction.getTransQty());
                materialLot.setReturnReason(materialLotAction.getActionReason());
                materialLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_WAIT_RETURN, StringUtils.EMPTY);
                baseService.saveHistoryEntity(materialLot, MaterialEvent.EVENT_WAIT_RETURN, materialLotAction);

                DocumentMLot documentMLot = new DocumentMLot();
                documentMLot.setDocumentId(returnOrder.getName());
                documentMLot.setMaterialLotId(materialLot.getMaterialLotId());
                documentMLot.setStatus(DocumentMLot.STATUS_CREATE);
                documentMLotRepository.save(documentMLot);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void returnMLotByDoc(String documentId, List<String> materialLotIdList) throws ClientException {
        try {
            ReturnOrder returnOrder = returnOrderRepository.findOneByName(documentId);
            if (returnOrder == null) {
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, returnOrder.getName());
            }
            if (!Document.STATUS_APPROVE.equals(returnOrder.getStatus())) {
                throw new ClientParameterException(DocumentException.DOCUMENT_STATUS_IS_NOT_ALLOW, returnOrder.getName());
            }
            List<MaterialLot> materialLots = validationDocReservedMLot(documentId, materialLotIdList);

            BigDecimal handleQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));
            for (MaterialLot materialLot : materialLots) {
                mmsService.returnMLot(materialLot);
            }

            returnOrder.setHandledQty(returnOrder.getHandledQty().add(handleQty));
            returnOrder.setUnHandledQty(returnOrder.getUnHandledQty().subtract(handleQty));
            baseService.saveEntity(returnOrder, DocumentHistory.TRANS_TYPE_RETURN);

            //更改documentMLot状态
            changeDocMLotStatus(documentId, materialLotIdList, DocumentMLot.STATUS_RETURN);

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public List<MaterialLot> validationDocReservedMLot(String documentId, List<String> validationMLotIdList) throws ClientException {
        try {
            List<MaterialLot> materialLots = Lists.newArrayList();
            List<MaterialLot> reservedMaterialLots = getReservedMLotByDocId(documentId);
            for (String materialLotId : validationMLotIdList) {
                Optional<MaterialLot> existMaterialLotOptional = reservedMaterialLots.stream().filter(materialLot -> materialLot.getMaterialLotId().equals(materialLotId)).findFirst();
                if (!existMaterialLotOptional.isPresent()) {
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, materialLotId);
                }
                materialLots.add(existMaterialLotOptional.get());
            }
            return materialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * 创建发料单
     *  此种发料单需要指定批次，即发料单直接指定批次一般用于主材使用
     * @param documentId 单据号 不传，系统会自己生成一个
     * @param approveFlag 是否创建即approve
     * @param materialLotIdList 物料批次号
     * @return 单据号
     * @throws ClientException
     */
    public String createIssueLotOrder(String documentId, boolean approveFlag, List<String> materialLotIdList) throws ClientException{
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

            //验证物料是否被create状态的单据绑定
            validationMLotBoundOrder(materialLotIdList);

            for (MaterialLot materialLot : materialLots) {
                DocumentMLot documentMLot = new DocumentMLot();
                documentMLot.setDocumentId(issueLotOrder.getName());
                documentMLot.setMaterialLotId(materialLot.getMaterialLotId());
                documentMLot.setStatus(DocumentMLot.STATUS_CREATE);
                documentMLotRepository.save(documentMLot);
            }
            return documentId;
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
                documentLine.setDocument(issueMaterialOrder);
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
            IssueMaterialOrder issueMaterialOrder = issueMaterialOrderRepository.findOneByName(documentLine.getDocId());
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
            if (documentLine.getUnHandledQty().compareTo(handleQty) < 0){
                throw new ClientParameterException(MLOT_TOTAL_QTY_GREATER_THAN_DOCLINE_UNHANDLED_QTY);
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
                throw new ClientParameterException(DOCUMENT_IS_NOT_EXIST, issueLotOrder.getName());
            }
            if (!Document.STATUS_APPROVE.equals(issueLotOrder.getStatus())) {
                throw new ClientParameterException(DOCUMENT_STATUS_IS_NOT_ALLOW, issueLotOrder.getName());
            }
            List<MaterialLot> materialLots = validationDocReservedMLot(issueLotOrderId, materialLotIdList);

            BigDecimal handleQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));
            for (MaterialLot materialLot : materialLots) {
                mmsService.issue(materialLot);
            }
            issueLotOrder.setHandledQty(issueLotOrder.getHandledQty().add(handleQty));
            issueLotOrder.setUnHandledQty(issueLotOrder.getUnHandledQty().subtract(handleQty));
            baseService.saveEntity(issueLotOrder, DocumentHistory.TRANS_TYPE_ISSUE);

            //更改documentMLot状态
            changeDocMLotStatus(issueLotOrderId, materialLotIdList, DocumentMLot.STATUS_ISSUE);

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

    public void approveDocument(String documentId) throws ClientException {
        try {
            Document document = documentRepository.findOneByName(documentId);

            approveDocument(document);
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
        return generatorDocId(generatorRule, null);
    }

    public String generatorDocId(String generatorRule, Document document) throws ClientException {
        GeneratorContext generatorContext = new GeneratorContext();
        generatorContext.setObject(document);
        generatorContext.setRuleName(generatorRule);
        return generatorService.generatorId(generatorContext);
    }

    /**
     * 创建成品发料单
     * @param documentId
     * @param approveFlag
     * @param materialLotIdList
     */
    public void createIssueFinishGoodOrder(String documentId, boolean approveFlag, List<String> materialLotIdList) throws ClientException{
        try {
            if (StringUtils.isNullOrEmpty(documentId)) {
                documentId = generatorDocId(IssueFinishGoodOrder.GENERATOR_ISSUE_FINISH_GOOD_ORDER_ID_RULE);
            }
            IssueFinishGoodOrder issueFinishGoodLotOrder = issueFinishGoodOrderRepository.findOneByName(documentId);
            if (issueFinishGoodLotOrder != null) {
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_EXIST, documentId);
            }
            List<MaterialLot> materialLots = materialLotIdList.stream().map(materialLotId -> mmsService.getMLotByMLotId(materialLotId, true)).collect(Collectors.toList());
            BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));

            issueFinishGoodLotOrder = new IssueFinishGoodOrder();
            issueFinishGoodLotOrder.setName(documentId);
            issueFinishGoodLotOrder.setQty(totalQty);
            issueFinishGoodLotOrder.setUnHandledQty(totalQty);
            if (approveFlag) {
                issueFinishGoodLotOrder.setStatus(Document.STATUS_APPROVE);
            }
            issueFinishGoodLotOrder = (IssueFinishGoodOrder) baseService.saveEntity(issueFinishGoodLotOrder);

            //验证物料是否被create状态的单据绑定
            validationMLotBoundOrder(materialLotIdList);

            for (MaterialLot materialLot : materialLots) {
                DocumentMLot documentMLot = new DocumentMLot();
                documentMLot.setDocumentId(issueFinishGoodLotOrder.getName());
                documentMLot.setMaterialLotId(materialLot.getMaterialLotId());
                documentMLot.setStatus(DocumentMLot.STATUS_CREATE);
                documentMLotRepository.save(documentMLot);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 成品发料
     */
    public void issueFinishGoodByDoc(String issueFinishGoodLotOrderId, List<String> materialLotIdList) throws ClientException{
        try {
            IssueFinishGoodOrder issueFinishGoodLotOrder = issueFinishGoodOrderRepository.findOneByName(issueFinishGoodLotOrderId);
            if (issueFinishGoodLotOrder == null) {
                throw new ClientParameterException(DOCUMENT_IS_NOT_EXIST, issueFinishGoodLotOrderId);
            }
            if (!Document.STATUS_APPROVE.equals(issueFinishGoodLotOrder.getStatus())) {
                throw new ClientParameterException(DOCUMENT_STATUS_IS_NOT_ALLOW, issueFinishGoodLotOrder.getName());
            }
            List<MaterialLot> materialLots = validationDocReservedMLot(issueFinishGoodLotOrderId, materialLotIdList);

            BigDecimal handleQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));
            for (MaterialLot materialLot : materialLots) {
                materialLot.setCurrentQty(BigDecimal.ZERO);
                materialLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_ISSUE, StringUtils.EMPTY);
                baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_ISSUE);
            }
            issueFinishGoodLotOrder.setHandledQty(issueFinishGoodLotOrder.getHandledQty().add(handleQty));
            issueFinishGoodLotOrder.setUnHandledQty(issueFinishGoodLotOrder.getUnHandledQty().subtract(handleQty));
            baseService.saveEntity(issueFinishGoodLotOrder, DocumentHistory.TRANS_TYPE_ISSUE);

            //更改documentMLot状态
            changeDocMLotStatus(issueFinishGoodLotOrderId, materialLotIdList, DocumentMLot.STATUS_ISSUE);

        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 创建发料单，内部发料使用
     * @param documentId
     * @param approveFlag
     * @param materialLotIds
     * @return
     * @throws ClientException
     */
    public String createIssueMLotOrder(String documentId, boolean approveFlag, List<String> materialLotIds) throws ClientException{
        try {
            if (StringUtils.isNullOrEmpty(documentId)) {
                documentId = generatorDocId(IssueMLotOrder.GENERATOR_ISSUE_MLOT_ORDER_ID_RULE);
            }
            IssueMLotOrder issueMLotOrder = issueMLotOrderRepository.findOneByName(documentId);

            if (issueMLotOrder != null) {
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_EXIST, documentId);
            }
            List<MaterialLot> materialLots = materialLotIds.stream().map(materialLotId -> mmsService.getMLotByMLotId(materialLotId, true)).collect(Collectors.toList());
            BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));

            issueMLotOrder = new IssueMLotOrder();
            issueMLotOrder.setName(documentId);
            issueMLotOrder.setQty(totalQty);
            issueMLotOrder.setUnHandledQty(totalQty);
            issueMLotOrder.setStatus(Document.STATUS_CREATE);
            if (approveFlag) {
                issueMLotOrder.setStatus(Document.STATUS_APPROVE);
            }
            issueMLotOrder = (IssueMLotOrder) baseService.saveEntity(issueMLotOrder);

            //验证物料是否被create状态的单据绑定
            validationMLotBoundOrder(materialLotIds);

            for (MaterialLot materialLot : materialLots) {
                DocumentMLot documentMLot = new DocumentMLot();
                documentMLot.setDocumentId(issueMLotOrder.getName());
                documentMLot.setMaterialLotId(materialLot.getMaterialLotId());
                documentMLot.setStatus(DocumentMLot.STATUS_CREATE);
                documentMLotRepository.save(documentMLot);
            }
            return documentId;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 内部发料
     * @param issueMLotId
     * @param materialLotIdList
     * @throws ClientException
     */
    public void issueMaterialLotByDoc(String issueMLotId, List<String> materialLotIdList) throws ClientException {
        try {
            IssueMLotOrder issueMLotOrder = issueMLotOrderRepository.findOneByName(issueMLotId);
            if (issueMLotOrder == null) {
                throw new ClientParameterException(DOCUMENT_IS_NOT_EXIST, issueMLotOrder.getName());
            }
            if (!Document.STATUS_APPROVE.equals(issueMLotOrder.getStatus())) {
                throw new ClientParameterException(DOCUMENT_STATUS_IS_NOT_ALLOW, issueMLotOrder.getName());
            }
            List<MaterialLot> materialLots = validationDocReservedMLot(issueMLotId, materialLotIdList);

            BigDecimal handleQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));
            for (MaterialLot materialLot : materialLots) {
                mmsService.issue(materialLot);
            }
            issueMLotOrder.setHandledQty(issueMLotOrder.getHandledQty().add(handleQty));
            issueMLotOrder.setUnHandledQty(issueMLotOrder.getUnHandledQty().subtract(handleQty));
            baseService.saveEntity(issueMLotOrder, DocumentHistory.TRANS_TYPE_ISSUE);

            //更改documentMLot状态
            changeDocMLotStatus(issueMLotId, materialLotIdList, DocumentMLot.STATUS_ISSUE);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     *验证物料绑定的单据在documentMlot中Status为Create
     * @param materialLotIds
     */
    public void validationMLotBoundOrder(List<String> materialLotIds) throws ClientException{
       try {
           List<DocumentMLot> documentMLots = documentMLotRepository.findByStatus(DocumentMLot.STATUS_CREATE);
           if (CollectionUtils.isEmpty(documentMLots)){
               return;
           }
           for (String materialLotId : materialLotIds) {
               Optional<DocumentMLot> documentMLotOptional = documentMLots.stream().filter(documentMLot -> documentMLot.getMaterialLotId().equals(materialLotId)).findFirst();
               if (documentMLotOptional.isPresent()){
                  throw new ClientParameterException(MATERIAL_LOT_ALREADY_BOUND_ORDER, materialLotId);
               }
           }
       }catch (Exception e){
           throw ExceptionManager.handleException(e, log);
       }
    }

    /**
     * 更改documentMlot状态
     * @param documentId
     * @param materialLotIds
     */
    public void changeDocMLotStatus(String documentId, List<String> materialLotIds, String status) throws ClientException{
        try {
            List<DocumentMLot> documentMLots = documentMLotRepository.findByDocumentId(documentId);
            if (CollectionUtils.isEmpty(documentMLots)){
                return;
            }

            for (String materialLotId : materialLotIds) {
                Optional<DocumentMLot> documentMLotOptional = documentMLots.stream().filter(documentMLot -> documentMLot.getMaterialLotId().equals(materialLotId)).findFirst();
                if (documentMLotOptional.isPresent()){
                    DocumentMLot documentMLot = documentMLotOptional.get();
                    documentMLot.setStatus(status);
                    baseService.saveEntity(documentMLot);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
}
