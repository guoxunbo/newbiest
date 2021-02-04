package com.newbiest.vanchip.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.annotation.BaseJpaFilter;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.CollectorsUtils;
import com.newbiest.base.utils.PropertyUtils;
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
import com.newbiest.mms.state.model.MaterialStatusModel;
import com.newbiest.vanchip.exception.VanchipExceptions;
import com.newbiest.vanchip.model.MLotDocRule;
import com.newbiest.vanchip.model.MLotDocRuleContext;
import com.newbiest.vanchip.repository.MLotDocRuleLineRepository;
import com.newbiest.vanchip.repository.MLotDocRuleRepository;
import com.newbiest.vanchip.service.MesService;
import com.newbiest.vanchip.service.VanChipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.newbiest.vanchip.exception.VanchipExceptions.MLOT_BINDED_WORKORDER;

/**
 * @author guoxunbo
 * @date 12/24/20 2:22 PM
 */
@Slf4j
@Component
@Transactional
@BaseJpaFilter
public class VanchipServiceImpl implements VanChipService {

    public static final String TRANS_TYPE_BIND_WO = "BindWo";
    public static final String TRANS_TYPE_UNBIND_WO = "UnbindWo";
    public static final String TRANS_TYPE_RESERVED = "RESERVED";
    public static final String TRANS_TYPE_UNRESERVED = "UNRESERVED";
    public static final String MLOT_RESERVED_DOC_VALIDATE_RULE_ID = "ValidateReservedRule";

    /**
     * 退料原因里是否需要Hold的关键
     */
    public static final String RETURN_HOLD_REASON = "质量问题";

    /**
     * 退料默认的HoldCode
     */
    public static final String RETURN_HOLD_CODE = "TL_HOLD";

    @Autowired
    BaseService baseService;

    @Autowired
    MmsService mmsService;

    @Autowired
    DocumentService documentService;

    @Autowired
    GeneratorService generatorService;

    @Autowired
    IncomingOrderRepository incomingOrderRepository;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    MLotDocRuleLineRepository mLotDocRuleLineRepository;

    @Autowired
    MLotDocRuleRepository mLotDocRuleRepository;

    @Autowired
    MesService mesService;

    @Autowired
    DocumentMLotRepository documentMLotRepository;

    @Autowired
    ReturnOrderRepository returnOrderRepository;

    @Autowired
    DocumentLineRepository documentLineRepository;

//    @Autowired
//    FinishGoodOrderRepository finishGoodOrderRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    DeliveryOrderRepository deliveryOrderRepository;

    public void bindMesOrder(List<String> materialLotIdList, String workOrderId) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotIdList.stream().map(materialLotId -> mmsService.getMLotByMLotId(materialLotId, true)).collect(Collectors.toList());
            Optional<MaterialLot> bindMLot = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getWorkOrderId())).findFirst();
            if (bindMLot.isPresent()) {
                throw new ClientParameterException(MLOT_BINDED_WORKORDER, bindMLot.get().getMaterialLotId());
            }
            for (MaterialLot materialLot : materialLots) {
                materialLot.setWorkOrderId(workOrderId);
                baseService.saveEntity(materialLot, TRANS_TYPE_BIND_WO);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void unbindMesOrder(List<String> materialLotIdList) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotIdList.stream().map(materialLotId -> mmsService.getMLotByMLotId(materialLotId, true)).collect(Collectors.toList());
            for (MaterialLot materialLot : materialLots) {
                materialLot.setWorkOrderId(StringUtils.EMPTY);
                baseService.saveEntity(materialLot, TRANS_TYPE_UNBIND_WO);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 来料导入
     * @param materialLots
     */
    public void importIncomingOrder(String incomingDocId, List<MaterialLot> materialLots) throws ClientException {
        try {
            BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));

            //来料单创建即审核通过
            if (StringUtils.isNullOrEmpty(incomingDocId)) {
                incomingDocId = documentService.generatorDocId(IncomingOrder.GENERATOR_INCOMING_ORDER_ID_RULE);
            }
            IncomingOrder incomingOrder = incomingOrderRepository.findOneByName(incomingDocId);
            if (incomingOrder != null) {
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_EXIST, incomingDocId);
            }
            incomingOrder = new IncomingOrder();
            incomingOrder.setName(incomingDocId);
            incomingOrder.setQty(totalQty);
            incomingOrder.setUnHandledQty(totalQty);
            incomingOrder.setStatus(Document.STATUS_APPROVE);
            incomingOrder = (IncomingOrder) baseService.saveEntity(incomingOrder);

            List<MaterialLot> documentMaterialLots = Lists.newArrayList();
            Map<String, List<MaterialLot>> materialMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getMaterialName));
            final IncomingOrder _incomingOrder = incomingOrder;
            materialMap.keySet().forEach(materialName -> {
                RawMaterial rawMaterial = mmsService.getRawMaterialByName(materialName);
                if (rawMaterial == null) {
                    throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_IS_NOT_EXIST, materialName);
                }
                MaterialStatusModel materialStatusModel = mmsService.getStatusModelByRrn(rawMaterial.getStatusModelRrn());
                List<MaterialLot> materialLotList = materialMap.get(materialName);

                for (MaterialLot materialLot : materialLotList) {
                    Map<String, Object> propMap = PropertyUtils.convertObj2Map(materialLot);
                    propMap.put("incomingDocRrn", _incomingOrder.getObjectRrn());
                    propMap.put("incomingDocId", _incomingOrder.getName());
                    MaterialLot mLot = mmsService.createMLot(rawMaterial, materialStatusModel, materialLot.getMaterialLotId(), materialLot.getCurrentQty(), materialLot.getCurrentSubQty(), propMap);
                    documentMaterialLots.add(mLot);
                }
            });
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void deleteIncomingMaterialLot(List<MaterialLot> materialLotList, String deleteNote) throws ClientException{
        try {
            List<MaterialLot>  materialLots = materialLotList.stream().filter(materialLot -> materialLot.getStatus().equals("Create")).collect(Collectors.toList());
            for (MaterialLot materialLot:materialLots){
                if (!StringUtils.isNullOrEmpty(materialLot.getWorkOrderId())){
                    throw new ClientParameterException(VanchipExceptions.UNIT_ID_ALREADY_BONDING_WORKORDER_ID, materialLot);
                }
                Document document = documentRepository.findOneByName(materialLot.getIncomingDocId());
                BigDecimal qty = document.getQty().subtract(materialLot.getCurrentQty());
                BigDecimal unHandleQty = document.getUnHandledQty().subtract(materialLot.getCurrentQty());

                document.setQty(qty);
                document.setUnHandledQty(unHandleQty);
                baseService.saveEntity(document);
                if (BigDecimal.ZERO == qty){
                    documentRepository.delete(document);
                }
                materialLotRepository.delete(materialLot);

                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setActionComment(deleteNote);
                baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_DELETE);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }

    }

    public void issueMLotByDoc(String documentId, List<String> materialLotIdList) throws ClientException{
        try {
            documentService.issueMLotByDoc(documentId, materialLotIdList);

            mesService.issueMLot(materialLotIdList);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void issueMLotByDocLine(DocumentLine documentLine, List<String> materialLotIdList) throws  ClientException{
        try {
            documentService.issueMLotByDocLine(documentLine, materialLotIdList);
            mesService.issueMLot(materialLotIdList);
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 退料
     * @param documentId
     * @param materialLotIdList
     * @throws ClientException
     */
    public void returnMLotByDoc(String documentId, List<String> materialLotIdList) throws ClientException {
        try {
            documentService.returnMLotByDoc(documentId, materialLotIdList);

            // 如果是质量问题导致的退料需要进行HOLD处理
            List<MaterialLot> materialLots = materialLotIdList.stream().map(materialLotId -> mmsService.getMLotByMLotId(materialLotId, true)).collect(Collectors.toList());
            List<MaterialLotAction> materialLotActions = Lists.newArrayList();
            for (MaterialLot materialLot : materialLots) {
                if (materialLot.getReturnReason().contains(RETURN_HOLD_REASON)) {
                    MaterialLotAction materialLotAction = new MaterialLotAction();
                    materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                    materialLotAction.setActionCode(RETURN_HOLD_CODE);
                    materialLotAction.setActionReason(materialLot.getReturnReason());
                    materialLotActions.add(materialLotAction);
                }
            }
            if (CollectionUtils.isNotEmpty(materialLotActions)) {
                mmsService.holdMaterialLot(materialLotActions);
            }
            mesService.returnMLot(materialLotIdList);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 辅材单据验证
     * @param documentLine
     * @param materialLotIds
     * @return
     * @throws ClientException
     */
    public MaterialLot validationDocLineAndMaterialLot(DocumentLine documentLine, List<String> materialLotIds) throws ClientException{
        try {
            String materialLotId = materialLotIds.get(0) ;
            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId);
            if (materialLot == null){
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST,materialLotId);
            }
            List<MaterialLot> materialLotList = Lists.newArrayList();
            materialLotList.add(materialLot);
            Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByMLotDocRule(materialLotList, "DocLineAndMaterialLot");

            documentLine = documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());
            List<DocumentLine> documentLineList = Lists.newArrayList();
            documentLineList.add(documentLine);
            Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMLotDocRule(documentLineList, "DocLineAndMaterialLot");

            for (String key : materialLotMap.keySet()) {
                if (!documentLineMap.keySet().contains(key)) {
                    throw new ClientParameterException(VanchipExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLotMap.get(key).get(0).getMaterialLotId());
                }
                BigDecimal mLotCurrentQty = materialLotMap.get(key).get(0).getCurrentQty();
                BigDecimal docLineUnHandledQty = documentLineMap.get(key).get(0).getUnHandledQty();
                if (mLotCurrentQty.compareTo(docLineUnHandledQty) > 0) {
                    throw new ClientException(VanchipExceptions.MLOT_QTY_GREATER_THAN_DOCLINE_UNHANDLEQTY);
                }
            }
            return materialLot;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    private Map<String,List<MaterialLot>> groupMaterialLotByMLotDocRule(List<MaterialLot> materialLots, String ruleId) throws ClientException{
        try {
            Map<String,List<MaterialLot>> materialLotMap = Maps.newHashMap();
            List<MLotDocRule> mLotDocLineRule = mLotDocRuleRepository.findByName(ruleId);
            if (CollectionUtils.isEmpty(mLotDocLineRule)) {
                throw new ClientParameterException(VanchipExceptions.MLOT_DOC_VALIDATE_RULE_IS_NOT_EXIST, ruleId);
            }
            MLotDocRuleContext mLotDocRuleContext = new MLotDocRuleContext();
            mLotDocRuleContext.setMaterialLotList(materialLots);
            mLotDocRuleContext.setMLotDocRuleLines(mLotDocLineRule.get(0).getLines());
            materialLotMap = mLotDocRuleContext.validateAndGetMLot();
            return materialLotMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private Map<String,List<DocumentLine>> groupDocLineByMLotDocRule(List<DocumentLine> documentLineList, String ruleName) throws ClientException{
        try {
            Map<String,List<DocumentLine>> documentLineMap = Maps.newHashMap();
            List<MLotDocRule> mLotDocLineRule = mLotDocRuleRepository.findByName(ruleName);
            if (CollectionUtils.isEmpty(mLotDocLineRule)) {
                throw new ClientParameterException(VanchipExceptions.MLOT_DOC_VALIDATE_RULE_IS_NOT_EXIST, ruleName);
            }
            MLotDocRuleContext mLotDocRuleContext = new MLotDocRuleContext();
            mLotDocRuleContext.setDocumentLineList(documentLineList);
            mLotDocRuleContext.setMLotDocRuleLines(mLotDocLineRule.get(0).getLines());
            documentLineMap = mLotDocRuleContext.validationAndGetDocLine();
            return documentLineMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void validateMLotAndDocLineByRule(DocumentLine documentLine, MaterialLot materialLot, String ruleName) throws ClientException{
        try {
            List<MLotDocRule> mLotDocLineRule = mLotDocRuleRepository.findByName(ruleName);
            if (CollectionUtils.isEmpty(mLotDocLineRule)) {
                throw new ClientParameterException(VanchipExceptions.MLOT_DOC_VALIDATE_RULE_IS_NOT_EXIST, ruleName);
            }
            MLotDocRuleContext mLotDocRuleContext = new MLotDocRuleContext();
            mLotDocRuleContext.setSourceObject(materialLot);
            mLotDocRuleContext.setTargetObject(documentLine);
            mLotDocRuleContext.setMLotDocRuleLines(mLotDocLineRule.get(0).getLines());
            mLotDocRuleContext.validation();
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *根据备货单信息 获得相应的mlot
     * @param documentLine
     * @return
     */
    public List<MaterialLot> getReservedMaterialLot(DocumentLine documentLine) {
        DocumentLine docLine = documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());
        List<MaterialLot> materialLots = Lists.newArrayList();

        //根据产品号,等级,未备货,未装箱
        materialLots = materialLotRepository.findByMaterialNameAndGradeAndReserved44IsNullAndBoxMaterialLotIdIsNull(docLine.getReserved2(), docLine.getReserved4());

        materialLots.forEach(materialLot -> validateMLotAndDocLineByRule(docLine, materialLot, MLOT_RESERVED_DOC_VALIDATE_RULE_ID));

        return materialLots;
    }

    /**
     * 备货
     * @param documentLine 发货通知单信息
     * @param materialLotActions
     */
    public void reservedMaterialLot(DocumentLine documentLine, List<MaterialLotAction> materialLotActions){
        String reservedRemake = materialLotActions.get(0).getActionComment();
        String docLineObjRrn = documentLine.getObjectRrn();
        DocumentLine deliveryDocLine = documentLineRepository.findByObjectRrn(docLineObjRrn);
        if (deliveryDocLine == null){
            throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, deliveryDocLine.getDocId());
        }
        DeliveryOrder deliveryOrder = deliveryOrderRepository.findOneByName(deliveryDocLine.getDocId());
        BigDecimal unHandledQty = deliveryDocLine.getUnHandledQty();
        BigDecimal handledQty = BigDecimal.ZERO;
        List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
        for (MaterialLot materialLot : materialLots) {
            materialLot.validateMLotHold();
            validateMLotAndDocLineByRule(deliveryDocLine, materialLot, MLOT_RESERVED_DOC_VALIDATE_RULE_ID);
            BigDecimal currentQty = materialLot.getCurrentQty();
            handledQty = handledQty.add(currentQty);
            if (unHandledQty.compareTo(handledQty) < 0) {
                throw new ClientParameterException(VanchipExceptions.RESERVED_OVER_QTY,materialLot.getMaterialLotId());
            }
            //将发货单据绑定到批次上
            materialLot.setReceiveQty(materialLot.getCurrentQty());
            materialLot.setReserved44(deliveryDocLine.getObjectRrn());
            materialLot.setReserved45(deliveryDocLine.getLineId());

            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setActionComment(reservedRemake);
            baseService.saveHistoryEntity(materialLot, TRANS_TYPE_RESERVED, materialLotAction);
        }
        deliveryDocLine.setUnHandledQty(deliveryDocLine.getUnHandledQty().subtract(handledQty));
        deliveryDocLine.setHandledQty(deliveryDocLine.getHandledQty().add(handledQty));
        documentLineRepository.save(deliveryDocLine);

        deliveryOrder.setUnHandledQty(deliveryOrder.getUnHandledQty().subtract(handledQty));
        deliveryOrder.setHandledQty(deliveryOrder.getHandledQty().add(handledQty));
        deliveryOrderRepository.save(deliveryOrder);
    }

    /**
     * 取消备货
     * 根据批次号，不考虑docLine
     * @param materialLotActions 包含批次号，数量
     * @throws ClientException
     */
    public void unReservedMaterialLot(List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            String unReservedRemake = materialLotActions.get(0).getActionComment();
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            //根据docLineRrn 分类处理
            Map<String, List<MaterialLot>> docLineReservedMaterialLotMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getReserved44));

            for (String docLineObjRrn :docLineReservedMaterialLotMap.keySet()){
                DocumentLine docLine = documentLineRepository.findByObjectRrn(docLineObjRrn);
                Document doc = documentRepository.findByObjectRrn(docLine.getDocRrn());
                List<MaterialLot> materialLotList = docLineReservedMaterialLotMap.get(docLineObjRrn);
                materialLotList.forEach(materialLot -> {
                    materialLot.setReceiveQty(materialLot.getReservedQty().subtract(materialLot.getReservedQty()));
                    materialLot.setReserved44(StringUtils.EMPTY);
                    materialLot.setReserved45(StringUtils.EMPTY);

                    materialLot = materialLotRepository.saveAndFlush(materialLot);

                    MaterialLotAction materialLotAction = new MaterialLotAction();
                    materialLotAction.setActionComment(unReservedRemake);
                    baseService.saveHistoryEntity(materialLot, TRANS_TYPE_UNRESERVED, materialLotAction);

                });
                BigDecimal totalNumber = materialLotList.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot::getCurrentQty));
                doc.setHandledQty(doc.getHandledQty().subtract(totalNumber));
                doc.setUnHandledQty(doc.getUnHandledQty().add(totalNumber));
                baseService.saveEntity(doc);

                docLine.setHandledQty(docLine.getHandledQty().subtract(totalNumber));
                docLine.setUnHandledQty(docLine.getUnHandledQty().add(totalNumber));
                baseService.saveEntity(docLine);
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e,log);
        }

    }

    /**
     * 打印备货单
     * @param documentLine
     * @return 绑定了该单据的物料批次
     * @throws ClientException
     */
    public List<MaterialLot> printReservedOrder(DocumentLine documentLine) throws ClientException{
        try {
            String docLineObjRrn = documentLine.getObjectRrn();
            List<MaterialLot> materialLotList = getMLotByLineObjectRrn(docLineObjRrn);
            return materialLotList;
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public List<MaterialLot> getMLotByLineObjectRrn(String docLineObjectRrn) throws ClientException{
        try {
            return materialLotRepository.findByReserved44(docLineObjectRrn);
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


}
