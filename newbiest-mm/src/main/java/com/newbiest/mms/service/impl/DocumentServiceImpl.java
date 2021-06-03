package com.newbiest.mms.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.CollectorsUtils;
import com.newbiest.base.utils.PreConditionalUtils;
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
import com.newbiest.mms.state.model.MaterialStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    LabMaterialRepository labMaterialRepository;

    @Autowired
    IssueByMaterialOrderRepository issueByMaterialOrderRepository;

    @Autowired
    IssueByMLotOrderRepository issueByMLotOrderRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    ReturnLotOrderRepository returnLotOrderRepository;

    @Autowired
    PackagedLotDetailRepository packagedLotDetailRepository;

    //BY客户版本备货 reserved5+reserved4+reserved3
    public static final String CREATE_BY_CUSTOMER_VERSION_RESERVED_RULE = "createByCustomerVersionReservedRule";

    //BY客户产品备货 reserved5+reserved4+reserved23
    public static final String CREATE_BY_CUSTOMER_PRODUCT_RESERVED_RULE = "createByCustomerProductReservedRule";

    /**
     * 仓库退料到供应商
     */
    public static final String RETURN_SUPPLIER = "returnSupplier";

    /**
     * 产线退料到仓库
     */
    public static final String RETURN_WAREHOUSE = "returnWarehouse";

    /**
     * 创建发货单
     * @param documentId
     * @param approveFlag
     * @param documentLineList
     * @return
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
            BigDecimal totalQty = documentLineList.stream().collect(CollectorsUtils.summingBigDecimal(DocumentLine::getQty));

            deliveryOrder = new DeliveryOrder();
            deliveryOrder.setName(documentId);
            deliveryOrder.setQty(totalQty);
            deliveryOrder.setUnHandledQty(totalQty);
            deliveryOrder.setStatus(Document.STATUS_CREATE);
            if (approveFlag) {
                deliveryOrder.setStatus(Document.STATUS_APPROVE);
            }
            deliveryOrder = (DeliveryOrder) baseService.saveEntity(deliveryOrder);

            //在库的成品
            List<MaterialLot> materialLots = materialLotRepository.findByMaterialCategoryAndStatus(Material.TYPE_PRODUCT, MaterialStatus.STATUS_IN);
            Map<String, List<DocumentLine>> documentLinesMap = documentLineList.stream().collect(Collectors.groupingBy(DocumentLine::getLineId));
            for (String documentLineId : documentLinesMap.keySet()) {
                DocumentLine documentLine = documentLineRepository.findByLineId(documentLineId);
                if (documentLine != null){
                    throw new ClientParameterException(DOCUMENT_IS_EXIST, documentLineId);
                }
                List<DocumentLine> documentLines = documentLinesMap.get(documentLineId);

                if(documentLines.get(0).getLineId() == StringUtils.EMPTY){
                    for (DocumentLine line : documentLines) {
                        if(!StringUtils.isNullOrEmpty(line.getCustomerProduct()) && StringUtils.isNullOrEmpty(line.getCustomerVersion())
                                && StringUtils.isNullOrEmpty(line.getReelCodeId())){
                            line.setReserved24(CREATE_BY_CUSTOMER_PRODUCT_RESERVED_RULE);
                        }else if (!StringUtils.isNullOrEmpty(line.getCustomerVersion()) && StringUtils.isNullOrEmpty(line.getCustomerProduct())
                                && StringUtils.isNullOrEmpty(line.getReelCodeId())) {
                            line.setReserved24(CREATE_BY_CUSTOMER_VERSION_RESERVED_RULE);
                        }
                        if(!StringUtils.isNullOrEmpty(line.getShippingDateValue())){
                            String shippingDate = formats.format(simpleDateFormat.parse(line.getShippingDateValue()));
                            line.setShippingDate(formats.parse(shippingDate));
                        }

                        String lineId = generatorDocId("createDeliveryOrderLineId", deliveryOrder);
                        line.setLineId(lineId);
                        line.setDocument(deliveryOrder);
                        line.setUnHandledQty(line.getQty());
                        line.setReservedQty(BigDecimal.ZERO);
                        line.setUnReservedQty(line.getQty());
                        baseService.saveEntity(line);
                    }
                }else {
                    documentLine = documentLines.get(0);
                    documentLine.setDocument(deliveryOrder);
                    if(!StringUtils.isNullOrEmpty(documentLine.getShippingDateValue())){
                        String shippingDate = formats.format(simpleDateFormat.parse(documentLine.getShippingDateValue()));
                        documentLine.setShippingDate(formats.parse(shippingDate));
                    }

                    BigDecimal byReelTotalQty = documentLines.stream().collect(CollectorsUtils.summingBigDecimal(DocumentLine::getQty));
                    documentLine.setQty(byReelTotalQty);
                    documentLine.setUnHandledQty(byReelTotalQty);
                    documentLine.setReservedQty(BigDecimal.ZERO);
                    documentLine.setUnReservedQty(byReelTotalQty);
                    baseService.saveEntity(documentLine);

                    List<String> reelCodeIds = documentLines.stream().map(docLine -> docLine.getReelCodeId()).collect(Collectors.toList());
                    for (String reelCodeId:reelCodeIds) {
                        List<MaterialLot> materialLotList = materialLots.stream().filter(materialLot -> materialLot.getMaterialLotId().equals(reelCodeId)).collect(Collectors.toList());

                        if (CollectionUtils.isNotEmpty(materialLotList)){
                            MaterialLot materialLot = materialLotList.get(0);

                            //进行预备货，物料绑定单据号，还是需要去做备货动作
                            materialLot.setReserved45(documentLine.getLineId());
                            baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_PRE_RESERVED);

                        }else {
                            throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, documentLine.getReelCodeId());
                        }
                    }
                }
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
            BigDecimal qty = materialLotActions.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLotAction::getTransQty));

            Document returnOrder = createDocument(new ReturnOrder(), documentId, ReturnOrder.GENERATOR_RETURN_ORDER_RULE,approveFlag, qty);

            //验证批次是否被create状态的单据绑定
            List<String> materialLotIds = materialLotActions.stream().map(materialLotAction -> materialLotAction.getMaterialLotId()).collect(Collectors.toList());
            validationMLotBoundOrder(materialLotIds);

            List<MaterialLot> materialLots = materialLotActions.stream().map(action -> mmsService.getMLotByMLotId(action.getMaterialLotId(), true)).collect(Collectors.toList());
            Map<String, MaterialLotAction> materialLotActionMap = materialLotActions.stream().collect(Collectors.toMap(MaterialLotAction::getMaterialLotId, Function.identity()));

            for (MaterialLot materialLot : materialLots) {
                MaterialLotAction materialLotAction = materialLotActionMap.get(materialLot.getMaterialLotId());
                materialLot.setCurrentQty(materialLotAction.getTransQty());
                materialLot.setReturnReason(materialLotAction.getActionReason());
                materialLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_WAIT_RETURN, StringUtils.EMPTY);
                baseService.saveHistoryEntity(materialLot, MaterialEvent.EVENT_WAIT_RETURN, materialLotAction);

                //将单据与批次绑定
                createDocumentMLot(returnOrder.getName(), materialLot.getMaterialLotId());
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 退料
     * @param documentId
     * @param materialLotIdList
     * @param returnTarget 仓库OR供应商
     * @throws ClientException
     */
    public void returnMLotByDoc(String documentId, List<String> materialLotIdList, String returnTarget) throws ClientException {
        try {
            List<MaterialLot> materialLots = validationDocReservedMLot(documentId, materialLotIdList);

            BigDecimal handleQty = BigDecimal.ZERO;
            if (RETURN_WAREHOUSE.equals(returnTarget)){
                handleQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));

                for (MaterialLot materialLot : materialLots) {
                    mmsService.returnMLot(materialLot);
                }
            }else if (RETURN_SUPPLIER.equals(returnTarget)){
                handleQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getReservedQty));

                for (MaterialLot materialLot : materialLots) {
                    mmsService.returnMaterialLot(materialLot);
                }
            }

            //更新document数量
            saveDocument(documentId, handleQty, DocumentHistory.TRANS_TYPE_RETURN);

            //更改documentMLot状态
            changeDocMLotStatus(documentId, materialLotIdList, DocumentMLot.STATUS_RETURN);

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 创建退料单
     * 这个退料单指仓库退回供应商
     * @param documentId
     * @param approveFlag
     * @param materialLotActions 批次号 退料数量(可以只退部分)
     * @throws ClientException
     */
    public Document createReturnMLotOrder(String documentId, boolean approveFlag, List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            BigDecimal qty = materialLotActions.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLotAction::getTransQty));
            Document returnMLotOrder = createDocument(new ReturnMLotOrder(), documentId, ReturnMLotOrder.GENERATOR_RETURN_MLOT_ORDER_RULE, approveFlag, qty);

            //验证批次是否被create状态的单据绑定
            List<String> materialLotIds = materialLotActions.stream().map(materialLotAction -> materialLotAction.getMaterialLotId()).collect(Collectors.toList());
            validationMLotBoundOrder(materialLotIds);

            List<MaterialLot> materialLots = materialLotActions.stream().map(action -> mmsService.getMLotByMLotId(action.getMaterialLotId(), true)).collect(Collectors.toList());
            Map<String, MaterialLotAction> materialLotActionMap = materialLotActions.stream().collect(Collectors.toMap(MaterialLotAction::getMaterialLotId, Function.identity()));
            for (MaterialLot materialLot : materialLots) {
                MaterialLotAction materialLotAction = materialLotActionMap.get(materialLot.getMaterialLotId());
                materialLot.setReservedQty(materialLotAction.getTransQty());
                materialLot.setReturnReason(materialLotAction.getActionReason());
                materialLotRepository.save(materialLot);
                //将单据与批次绑定
                createDocumentMLot(returnMLotOrder.getName(), materialLot.getMaterialLotId());
            }
            return returnMLotOrder;
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
            List<MaterialLot> materialLots = materialLotIdList.stream().map(materialLotId -> mmsService.getMLotByMLotId(materialLotId, true)).collect(Collectors.toList());
            BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));

            Document issueLotOrder = createDocument(new IssueLotOrder(), documentId, IssueLotOrder.GENERATOR_ISSUE_LOT_ORDER_ID_RULE,approveFlag, totalQty);

            //验证物料是否被create状态的单据绑定
            validationMLotBoundOrder(materialLotIdList);

            for (MaterialLot materialLot : materialLots) {
                createDocumentMLot(issueLotOrder.getName(), materialLot.getMaterialLotId()) ;
            }
            return documentId;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *创建辅材发料单
     * @param documentId
     * @param approveFlag
     * @param materialLotIdList
     * @throws ClientException
     */
    public String createIssueMaterialOrder(String documentId, boolean approveFlag, List<String> materialLotIdList) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotIdList.stream().map(materialLotId -> mmsService.getMLotByMLotId(materialLotId, true)).collect(Collectors.toList());
            BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));

            //创建发料单
            Document issueMaterialOrder = createDocument(new IssueMaterialOrder(), documentId, IssueMaterialOrder.GENERATOR_ISSUE_MATERIAL_ORDER_ID_RULE,approveFlag, totalQty);

            //验证物料是否被create状态的单据绑定
            validationMLotBoundOrder(materialLotIdList);

            for (MaterialLot materialLot : materialLots) {
                createDocumentMLot(issueMaterialOrder.getName(), materialLot.getMaterialLotId()) ;
            }
            return documentId;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public List<MaterialLot> getReservedMLotByDocId(String documentId) throws ClientException {
        return materialLotRepository.findReservedLotsByDocId(documentId);
    }

    /**
     * 指定物料批次发料
     * @param documentId
     * @param materialLotIdList
     */
    public void issueMaterialLotByDoc(String documentId, List<String> materialLotIdList) throws ClientException {
        try {
            Document document = getDocumentByName(documentId, true);
            if (document instanceof IssueLotOrder){
                issueMLotByDoc(documentId, materialLotIdList);
            }else if (document instanceof IssueMaterialOrder){
                issueMaterialByDoc(documentId, materialLotIdList);
            }else if (document instanceof IssueFinishGoodOrder){
                issueFinishGoodByDoc(documentId, materialLotIdList);
            }else {
                throw new ClientParameterException(DOCUMENT_CATEGORY_IS_NOT_EXIST, documentId);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 主材发料
     *  不根据物料发料，单据会事先绑好物料批次进行发料
     * @param issueLotOrderId 发料单号
     * @param materialLotIdList 发料的物料批次
     * @throws ClientException
     */
    public void issueMLotByDoc(String issueLotOrderId, List<String> materialLotIdList) throws ClientException {
        try {
            List<MaterialLot> materialLots = validationDocReservedMLot(issueLotOrderId, materialLotIdList);
            BigDecimal handleQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));

            for (MaterialLot materialLot : materialLots) {
                mmsService.issue(materialLot);
            }
            saveDocument(issueLotOrderId, handleQty, DocumentHistory.TRANS_TYPE_ISSUE);
            //更改documentMLot状态
            changeDocMLotStatus(issueLotOrderId, materialLotIdList, DocumentMLot.STATUS_ISSUE);

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 辅材发料
     * 单据会事先绑好物料批次进行发料
     * @param issueMaterialOrderId 发料单号
     * @param materialLotIdList 发料的物料批次
     * @throws ClientException
     */
    public void issueMaterialByDoc(String issueMaterialOrderId, List<String> materialLotIdList) throws ClientException {
        try {
            List<MaterialLot> materialLots = validationDocReservedMLot(issueMaterialOrderId, materialLotIdList);

            BigDecimal handleQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));
            for (MaterialLot materialLot : materialLots) {
                mmsService.issue(materialLot);
            }
            saveDocument(issueMaterialOrderId, handleQty, DocumentHistory.TRANS_TYPE_ISSUE);

            //更改documentMLot状态
            changeDocMLotStatus(issueMaterialOrderId, materialLotIdList, DocumentMLot.STATUS_ISSUE);

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
                Material material = mmsService.getMaterialByName(materialName, true);
                mmsService.receiveMLot(material, materialLotMap.get(materialName));
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
            List<MaterialLot> materialLots = materialLotIdList.stream().map(materialLotId -> mmsService.getMLotByMLotId(materialLotId, true)).collect(Collectors.toList());
            BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));

            Document issueFinishGoodOrder = createDocument(new IssueFinishGoodOrder(), documentId, IssueFinishGoodOrder.GENERATOR_ISSUE_FINISH_GOOD_ORDER_ID_RULE, approveFlag, totalQty);

            //验证物料是否被create状态的单据绑定
            validationMLotBoundOrder(materialLotIdList);

            for (MaterialLot materialLot : materialLots) {
                createDocumentMLot(issueFinishGoodOrder.getName(), materialLot.getMaterialLotId()) ;
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
            List<MaterialLot> materialLots = validationDocReservedMLot(issueFinishGoodLotOrderId, materialLotIdList);
            BigDecimal handleQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));

            for (MaterialLot materialLot : materialLots) {
                mmsService.issue(materialLot);
            }
            saveDocument(issueFinishGoodLotOrderId, handleQty, DocumentHistory.TRANS_TYPE_ISSUE);
            //更改documentMLot状态
            changeDocMLotStatus(issueFinishGoodLotOrderId, materialLotIdList, DocumentMLot.STATUS_ISSUE);
        }catch (Exception e){
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
     * 更改单据与批次的绑定状态
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

    /**
     * 创建单据与物料批次绑定关系
     * @param documentId
     * @param materialLotId
     * @return
     * @throws ClientException
     */
    public DocumentMLot createDocumentMLot(String documentId, String materialLotId) throws ClientException{
        try {
            DocumentMLot documentMLot = new DocumentMLot();
            documentMLot.setDocumentId(documentId);
            documentMLot.setMaterialLotId(materialLotId);
            documentMLot.setStatus(DocumentMLot.STATUS_CREATE);
            documentMLot = documentMLotRepository.save(documentMLot);
            return documentMLot;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *指定物料而非物料批次
     * @param documentId
     * @param approveFlag
     * @param materials 包含PickQty
     * @throws ClientException
     */
    public Document createIssueByMaterialOrder(String documentId, boolean approveFlag, List<Material> materials) throws ClientException{
        try {
            if (StringUtils.isNullOrEmpty(documentId)) {
                documentId = generatorDocId(IssueByMaterialOrder.GENERATOR_ISSUE_BY_MATERIAL_ORDER_ID_RULE);
            }
            IssueByMaterialOrder issueByMaterialOrder = issueByMaterialOrderRepository.findOneByName(documentId);
            if (issueByMaterialOrder != null) {
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_EXIST, documentId);
            }
            BigDecimal totalQty = materials.stream().collect(CollectorsUtils.summingBigDecimal(Material :: getPickQty));

            issueByMaterialOrder = new IssueByMaterialOrder();
            issueByMaterialOrder.setName(documentId);
            issueByMaterialOrder.setQty(totalQty);
            issueByMaterialOrder.setUnHandledQty(totalQty);
            if (approveFlag) {
                issueByMaterialOrder.setStatus(Document.STATUS_APPROVE);
            }
            issueByMaterialOrder = (IssueByMaterialOrder) baseService.saveEntity(issueByMaterialOrder);

            for (Material material : materials){
                BigDecimal pickQty = material.getPickQty();
                material = materialRepository.findOneByName(material.getName());
                if (material == null){
                    throw  new ClientParameterException(MmsException.MM_LAB_MATERIAL_IS_NOT_EXIST, material.getName());
                }
                DocumentLine documentLine = new DocumentLine();
                documentLine.setDocument(issueByMaterialOrder);
                documentLine.setMaterial(material);
                documentLine.setQty(pickQty);
                documentLine.setUnHandledQty(pickQty);
                baseService.saveEntity(documentLine);
            }

            return issueByMaterialOrder;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 给指定物料的发料单进行推荐
     * @param documentId
     * @return
     */
    public List<MaterialLot> recommendIssueByMaterialOrder(String documentId) throws ClientException{
        try {
            List<MaterialLot> materialLots = Lists.newArrayList();
            List<DocumentLine> documentLines = documentLineRepository.findByDocId(documentId);

            for (DocumentLine documentLine : documentLines){
                List<MaterialLot> materialLotList = getMLotByDocumentLineAndFIFO(documentLine);

                materialLots.addAll(materialLotList);
            }
            return materialLots;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);

        }
    }

    public List<MaterialLot> getMLotByDocumentLineAndFIFO(DocumentLine documentLine) throws ClientException{
        try {
            String materialName = documentLine.getMaterialName();

            List<MaterialLot> materialLots = materialLotRepository.findByMaterialNameAndStatus(materialName, MaterialStatus.STATUS_IN);

            //根据接收时间排序
            materialLots = materialLots.stream().sorted(Comparator.comparing(MaterialLot::getReceiveDate)).collect(Collectors.toList());

            List<MaterialLot> materialLotList = Lists.newArrayList();
            for (MaterialLot materialLot : materialLots){
                BigDecimal docLineUnHandleQty = documentLine.getUnHandledQty();

                materialLot.setPickQty(documentLine.getQty());
                materialLotList.add(materialLot);
                docLineUnHandleQty.subtract(materialLot.getCurrentQty());

                if (docLineUnHandleQty.compareTo(materialLot.getCurrentQty()) < 0 ){
                    return materialLotList;
                }
            }
            return materialLotList;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 指定物料进行发料
     * @param documentId
     * @param materialLotIds
     * @return
     */
    public void issueByMaterial(String documentId, List<String> materialLotIds) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotIds.stream().map(materialLotId -> mmsService.getMLotByMLotId(materialLotId, true)).collect(Collectors.toList());
            BigDecimal handleQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));
            Document issueByMaterialOrder = saveDocument(documentId, handleQty, DocumentHistory.TRANS_TYPE_ISSUE);

            for (MaterialLot materialLot : materialLots) {
                mmsService.issue(materialLot);

                DocumentMLot documentMLot = new DocumentMLot();
                documentMLot.setDocumentId(issueByMaterialOrder.getName());
                documentMLot.setMaterialLotId(materialLot.getMaterialLotId());
                documentMLot.setStatus(DocumentMLot.STATUS_ISSUE);
                documentMLotRepository.save(documentMLot);
            }

            Map<String, List<MaterialLot>> materialLotMaps = materialLots.stream().collect(Collectors.groupingBy(MaterialLot::getMaterialName));
            List<DocumentLine> docLines = documentLineRepository.findByDocId(documentId);
            for (String materialName:materialLotMaps.keySet()){
                Optional<DocumentLine> documentLineOptional = docLines.stream().filter(docLine -> docLine.getMaterialName().equals(materialName)).findAny();

                List<MaterialLot> materialLotList= materialLotMaps.get(materialName);
                handleQty = materialLotList.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot ::getCurrentQty));

                DocumentLine documentLine = documentLineOptional.get();
                documentLine.setHandledQty(documentLine.getHandledQty().add(handleQty));
                documentLine.setUnHandledQty(documentLine.getUnHandledQty().subtract(handleQty));
                baseService.saveEntity(documentLine, DocumentHistory.TRANS_TYPE_ISSUE);
            }

        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public Document getDocumentByName(String documentId, boolean throwExceptionFlag) throws ClientException{
        try {
            Document document = documentRepository.findOneByName(documentId);
            if (document == null && throwExceptionFlag){
                throw new ClientParameterException(DOCUMENT_IS_NOT_EXIST, documentId);
            }
            return document;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 指定物料批次 以及数量
     * @param documentId
     * @param approveFlag
     * @param materialLots
     * @return
     * @throws ClientException
     */
    public Document createIssueMaterialLotOrder(String documentId, boolean approveFlag, List<MaterialLot> materialLots) throws ClientException{
        BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getPickQty));
        Document issueByMLotOrder = createDocument(new IssueByMLotOrder(), documentId, IssueByMLotOrder.GENERATOR_ISSUE_BY_MLOT_ORDER_ID_RULE, approveFlag, totalQty);

        List<String> materialLotIdList = materialLots.stream().map(materialLot -> materialLot.getMaterialLotId()).collect(Collectors.toList());
        validationMLotBoundOrder(materialLotIdList);

        for (MaterialLot materialLot : materialLots) {
            //reservedQty指定发料数量
            materialLot.setReservedQty(materialLot.getPickQty());
            materialLotRepository.save(materialLot);

            createDocumentMLot(issueByMLotOrder.getName(), materialLot.getMaterialLotId());
//            DocumentMLot documentMLot = new DocumentMLot();
//            documentMLot.setDocumentId(issueByMLotOrder.getName());
//            documentMLot.setMaterialLotId(materialLot.getMaterialLotId());
//            documentMLot.setStatus(DocumentMLot.STATUS_CREATE);
//            documentMLotRepository.save(documentMLot);
        }
        return issueByMLotOrder;
    }


    /**
     * 支持子批代替母批发料
      * @param documentId
     * @param materialLotIds
     * @throws ClientException
     */
    public void issueMaterialLotByOrder(String documentId, List<String> materialLotIds) throws ClientException{
        List<DocumentMLot> documentMLots = documentMLotRepository.findByDocumentId(documentId);
        List<MaterialLot> materialLots = materialLotIds.stream().map(materialLotId -> mmsService.getMLotByMLotId(materialLotId, true)).collect(Collectors.toList());
        BigDecimal handleQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));
        saveDocument(documentId, handleQty, DocumentHistory.TRANS_TYPE_ISSUE);

        for (MaterialLot materialLot : materialLots) {
            String materialLotId = materialLot.getMaterialLotId();
            Optional<DocumentMLot> documentMLotOptional = documentMLots.stream().filter(mLot -> mLot.getMaterialLotId().equals(materialLotId)).findFirst();

            if (!documentMLotOptional.isPresent()){
                //如果是子批,验证母批是否与物料绑定
                if(!materialLot.getSubMaterialLotFlag()){
                    throw new ClientParameterException(DocumentException.MATERIAL_LOT_NOT_BOUND_ORDER);
                }

                String parentMaterialLotId = materialLot.getParentMaterialLotId();
                Optional<DocumentMLot> docMLotOptional = documentMLots.stream().filter(mLot -> mLot.getMaterialLotId().equals(parentMaterialLotId)).findFirst();
                if (!docMLotOptional.isPresent()){
                    throw new ClientParameterException(DocumentException.MATERIAL_LOT_NOT_BOUND_ORDER);
                }

                //改变母批次与单据绑定的状态
                DocumentMLot documentMLot = docMLotOptional.get();
                documentMLot.setStatus(DocumentMLot.STATUS_ISSUE_SUB_MLOT);
                documentMLotRepository.save(documentMLot);

                //将当前的批次与单据进行绑定
                DocumentMLot docMLot = new DocumentMLot();
                docMLot.setDocumentId(documentId);
                docMLot.setMaterialLotId(materialLot.getMaterialLotId());
                docMLot.setStatus(DocumentMLot.STATUS_ISSUE);
                documentMLotRepository.save(docMLot);

                //将母批reservedQty（领料数量）栏位置空
                MaterialLot parentMaterialLot = mmsService.getMLotByMLotId(parentMaterialLotId, true);
                parentMaterialLot.setReservedQty(BigDecimal.ZERO);
            }else {
                //该单据与物料确认绑定
                DocumentMLot documentMLot = documentMLotOptional.get();
                documentMLot.setStatus(DocumentMLot.STATUS_ISSUE);
                documentMLotRepository.save(documentMLot);
            }
            //清空reservedQty(领料数量) 栏位
            materialLot.setReservedQty(BigDecimal.ZERO);
            mmsService.issue(materialLot);
        }
    }


    /**
     * 创建单据
     * @param document 根据单据类型不同，传入不同的单据
     * @param documentId
     * @param idGeneratorRule
     * @param approveFlag
     * @param qty
     * @return
     * @throws ClientException
     */
    public Document createDocument(Document document, String documentId, String idGeneratorRule, boolean approveFlag, BigDecimal qty) throws ClientException{
        try {
            if (StringUtils.isNullOrEmpty(documentId)) {
                documentId = generatorDocId(idGeneratorRule);
            }
            Document doc = documentRepository.findOneByName(documentId);
            if (doc != null) {
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_EXIST, documentId);
            }

            document.setName(documentId);
            document.setQty(qty);
            document.setUnHandledQty(qty);
            if (approveFlag) {
                document.setStatus(Document.STATUS_APPROVE);
            }
            document = (Document)baseService.saveEntity(document);
            return document;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *更新单据处理数量
     * @param documentId
     * @param handleQty
     * @param transType
     * @return
     * @throws ClientException
     */
    public Document saveDocument(String documentId, BigDecimal handleQty, String transType) throws ClientException{
        try {
            Document document = documentRepository.findOneByName(documentId);
            if (document == null) {
                throw new ClientParameterException(DOCUMENT_IS_NOT_EXIST, documentId);
            }
            if (!Document.STATUS_APPROVE.equals(document.getStatus())) {
                throw new ClientParameterException(DOCUMENT_STATUS_IS_NOT_ALLOW, documentId);
            }
            document.setHandledQty(document.getHandledQty().add(handleQty));
            document.setUnHandledQty(document.getUnHandledQty().subtract(handleQty));
            baseService.saveEntity(document, transType);
            return document;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 创建退货单
     * @param returnLotId
     * @param approveFlag
     * @param materialLots
     * @throws ClientException
     */
    public void createReturnLotOrder(String returnLotId, boolean approveFlag, List<MaterialLot> materialLots) throws ClientException{
        try {
            BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot::getCurrentQty));
            ReturnLotOrder returnLotOrder = (ReturnLotOrder)createDocument(new ReturnLotOrder(), returnLotId, ReturnLotOrder.GENERATOR_RETURN_LOT_ORDER_RULE, approveFlag, totalQty);

            List<MaterialLot> mLots = materialLotRepository.findByStatus(MaterialStatus.STATUS_SHIP);
            for (MaterialLot materialLot : materialLots){
                Optional<MaterialLot> materialLotOptional = mLots.stream().filter(mLot -> mLot.getMaterialLotId().equals(materialLot.getMaterialLotId()))
                        .findFirst();
                if (!materialLotOptional.isPresent()){
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, materialLot.getMaterialLotId());
                }
                MaterialLot mLot = materialLotOptional.get();

                mLot.setCurrentQty(materialLot.getCurrentQty());
                mmsService.waitReturnMLot(mLot);

                createDocumentMLot(returnLotOrder.getName(), materialLot.getMaterialLotId());
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *退货 厂内发出的物品
     *进行解绑,状态为接收
     * @param returnLotId
     * @param materialLotIds
     * @throws ClientException
     */
    public void returnLotOrder(String returnLotId, List<String> materialLotIds) throws ClientException{
        try {
            List<MaterialLot> materialLots = validationDocReservedMLot(returnLotId, materialLotIds);
            BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot::getCurrentQty));
            saveDocument(returnLotId, totalQty, DocumentHistory.TRANS_TYPE_RETURN);

            Map<String, List<MaterialLot>> boxMLotMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot::getBoxMaterialLotRrn));
            for (String boxMLotRrn : boxMLotMap.keySet()) {
                List<PackagedLotDetail> packagedLotDetails = packagedLotDetailRepository.findByPackagedLotRrn(boxMLotRrn);
                for (PackagedLotDetail packagedLotDetail : packagedLotDetails) {
                    packagedLotDetailRepository.deleteById(packagedLotDetail.getObjectRrn());
                }
            }

            for (MaterialLot materialLot : materialLots) {
                //取消箱号绑定
                materialLot.setBoxMaterialLotRrn(StringUtils.EMPTY);
                materialLot.setBoxMaterialLotId(StringUtils.EMPTY);
                //取消单据绑定
                materialLot.setReserved44(StringUtils.EMPTY);
                materialLot.setReserved45(StringUtils.EMPTY);
                mmsService.returnMLot(materialLot);
            }

            changeDocMLotStatus(returnLotId, materialLotIds, DocumentMLot.STATUS_RETURN);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据单据号获得绑定的批次
     * @param documentId
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getMLotByDocumentId(String documentId) throws ClientException{
        try{
            List<MaterialLot> materialLots = Lists.newArrayList();
            Document document = documentRepository.findOneByName(documentId);
            if(document == null){
                DocumentLine documetnLine = documentLineRepository.findByLineId(documentId);
                if(documetnLine == null){
                    throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, documentId);
                }

                materialLots = materialLotRepository.findByReserved44(documetnLine.getObjectRrn());
            }else{
                materialLots = getReservedMLotByDocId(documentId);
            }

            if(document.getCategory().equals(Document.CATEGORY_INCOMING)){
                materialLots = materialLotRepository.findByIncomingDocId(document.getName());
            }
            return materialLots;
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据批次和单据类别获得单据
     * @param mLotId
     * @param documentCategory
     * @return
     * @throws ClientException
     */
    public Document getDocumentByMLotIdAndDocumentCategory(String mLotId, String documentCategory) throws ClientException{
        try{
            PreConditionalUtils.checkNotNull(documentCategory, StringUtils.EMPTY);
            MaterialLot materialLot = mmsService.getMLotByMLotId(mLotId, true);
            List<DocumentMLot> documentMLots = documentMLotRepository.findByMaterialLotId(mLotId);
            List<String> documentIds = documentMLots.stream().map(docMLot -> docMLot.getDocumentId()).collect(Collectors.toList());
            documentIds.add(materialLot.getIncomingDocId());
            List<Document> documents = documentRepository.findByNameIn(documentIds);
            Optional<Document> documentOptional = documents.stream().filter(document -> document.getCategory().equals(documentCategory)).findFirst();
            if (!documentOptional.isPresent()){
                throw new ClientParameterException(DOCUMENT_IS_NOT_EXIST);
            }
            return documentOptional.get();
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 单据删除
     * 用户可以删除自己创建还未处理的单据
     * @param documentId
     * @return
     * @throws ClientException
     */
    public void deleteDocument(String documentId) throws ClientException{
        try{
            Document document = getDocumentByName(documentId, true);
            String userName = ThreadLocalContext.getUsername();

            if (document.getQty().compareTo(document.getUnHandledQty()) != 0 || !userName.equals(document.getCreatedBy())){
                throw new ClientParameterException(DOCUMENT_NOT_CANT_DELETE, documentId);
            }

            List<DocumentMLot> documentMLots = documentMLotRepository.findByDocumentId(document.getName());
            if (!CollectionUtils.isNotEmpty(documentMLots)){
                List<MaterialLot> materialLots = documentMLots.stream().map(documentMLot -> mmsService.getMLotByMLotId(documentMLot.getMaterialLotId(), true)).collect(Collectors.toList());
                materialLots.forEach(mLot -> {
                    mLot.setReservedQty(BigDecimal.ZERO);
                    materialLotRepository.save(mLot);
                });
                documentMLotRepository.deleteInBatch(documentMLots);
            }else {
                List<DocumentLine> documentLine = documentLineRepository.findByDocId(documentId);
                documentLineRepository.deleteInBatch(documentLine);
            }
            documentRepository.delete(document);


        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
}
