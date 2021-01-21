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

    public static final String BIND_WO = "BindWo";
    public static final String UNBIND_WO = "UnbindWo";

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
    MaterialLotHistoryRepository materialLotHistoryRepository;

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

    public void bindMesOrder(List<String> materialLotIdList, String workOrderId) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotIdList.stream().map(materialLotId -> mmsService.getMLotByMLotId(materialLotId, true)).collect(Collectors.toList());
            Optional<MaterialLot> bindedMLot = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getWorkOrderId())).findFirst();
            if (bindedMLot.isPresent()) {
                throw new ClientParameterException(MLOT_BINDED_WORKORDER, bindedMLot.get().getMaterialLotId());
            }
            for (MaterialLot materialLot : materialLots) {
                materialLot.setWorkOrderId(workOrderId);
                baseService.saveEntity(materialLot, BIND_WO);
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
                baseService.saveEntity(materialLot, UNBIND_WO);
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

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_DELETE);
                history.setActionComment(deleteNote);
                materialLotHistoryRepository.save(history);
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
     * 创建退料单
     * @param documentId
     * @param approveFlag
     * @param materialLotIdAndQtyAndReasonMapList
     * @throws ClientException
     */
    public void createReturnOrder(String documentId, boolean approveFlag, List<Map<String, String>> materialLotIdAndQtyAndReasonMapList) throws ClientException{
        try {
            if (StringUtils.isNullOrEmpty(documentId)){
                documentId = documentService.generatorDocId(ReturnOrder.GENERATOR_RETURN_ORDER_RULE);
            }
            ReturnOrder returnOrder = returnOrderRepository.findOneByName(documentId);
            if (returnOrder != null) {
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_EXIST, documentId);
            }

            List<MaterialLot> materialLotList = Lists.newArrayList();
            for (Map<String, String> mLotId : materialLotIdAndQtyAndReasonMapList){
                MaterialLot materialLot = new MaterialLot();
                for (String map : mLotId.keySet()){
                    PropertyUtils.setProperty(materialLot, map, mLotId.get(map));
                }
                materialLotList.add(materialLot);
            }
            BigDecimal totalQty = materialLotList.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));

            returnOrder = new ReturnOrder();
            returnOrder.setName(documentId);
            returnOrder.setQty(totalQty);
            returnOrder.setUnHandledQty(totalQty);
            if (approveFlag) {
                returnOrder.setStatus(Document.STATUS_APPROVE);
            }
            returnOrder = (ReturnOrder) baseService.saveEntity(returnOrder);

            for (MaterialLot materialLot : materialLotList) {
                MaterialLot mlot =mmsService.getMLotByMLotId(materialLot.getMaterialLotId());
                if (mlot == null){
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, mlot);
                }
                DocumentMLot documentMLot = new DocumentMLot();
                documentMLot.setDocumentId(returnOrder.getName());
                documentMLot.setMaterialLotId(materialLot.getMaterialLotId());
                documentMLotRepository.save(documentMLot);

                mlot.setReturnMlotReason(materialLot.getReturnMlotReason());
                baseService.saveEntity(mlot);
                baseService.saveHistoryEntity(mlot, MaterialLotHistory.TRANS_TYPE_CREATE_RETURN_ORDER);
            }
        }catch (Exception e){
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
            ReturnOrder returnOrder = returnOrderRepository.findOneByName(documentId);
            if (returnOrder == null) {
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, returnOrder);
            }
            if (!Document.STATUS_APPROVE.equals(returnOrder.getStatus())) {
                throw new ClientParameterException(DocumentException.DOCUMENT_STATUS_IS_NOT_ALLOW, returnOrder.getName());
            }
            List<MaterialLot> materialLots = materialLotRepository.findReservedLotsByDocId(documentId);

            BigDecimal handleQty = BigDecimal.ZERO;
            for (String materialLotId : materialLotIdList) {
                Optional<MaterialLot> existMaterialLotOptional = materialLots.stream().filter(materialLot -> materialLot.getMaterialLotId().equals(materialLotId)).findFirst();
                if (!existMaterialLotOptional.isPresent()) {
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, materialLotId);
                }
                MaterialLot materialLot = existMaterialLotOptional.get();
                handleQty = handleQty.add(materialLot.getCurrentQty());

                materialLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_RECEIVE, MaterialStatus.STATUS_IQC);
                baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_RETURN);
            }
            returnOrder.setHandledQty(returnOrder.getHandledQty().add(handleQty));
            returnOrder.setUnHandledQty(returnOrder.getUnHandledQty().subtract(handleQty));
            baseService.saveEntity(returnOrder, DocumentHistory.TRANS_TYPE_RETURN);

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
}
