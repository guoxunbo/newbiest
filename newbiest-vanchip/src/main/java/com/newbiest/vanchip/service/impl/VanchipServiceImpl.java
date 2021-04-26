package com.newbiest.vanchip.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.annotation.BaseJpaFilter;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.model.NBVersionControlHis;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.service.VersionControlService;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.*;
import com.newbiest.commom.sm.exception.StatusMachineExceptions;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.main.MailService;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.DocumentException;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.DocumentService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.mms.state.model.MaterialEvent;
import com.newbiest.mms.state.model.MaterialStatus;
import com.newbiest.mms.state.model.MaterialStatusCategory;
import com.newbiest.mms.state.model.MaterialStatusModel;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.repository.UserRepository;
import com.newbiest.vanchip.exception.VanchipExceptions;
import com.newbiest.vanchip.model.MLotDocRule;
import com.newbiest.vanchip.model.MLotDocRuleContext;
import com.newbiest.vanchip.model.MaterialModelConversion;
import com.newbiest.vanchip.repository.MLotDocRuleLineRepository;
import com.newbiest.vanchip.repository.MLotDocRuleRepository;
import com.newbiest.vanchip.repository.MaterialModelConversionRepository;
import com.newbiest.vanchip.service.MesService;
import com.newbiest.vanchip.service.VanChipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
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

    //成品入库 Hold code
    public static final String PRE_HOLD = "Pre_Hold";
    public static final String S_HOLD = "S_Hold";
    public static final String P_HOLD = "P_Hold";
    public static final String Q_HOLD = "Q_Hold";
    public static final String E_HOLD = "E_Hold";
    public static final String O_MRB_HOLD = "O_MRB_Hold";

    //根据字符 进行不同的hold
    public static final String CUSTORDERID_S = "S";
    public static final String CUSTORDERID_P = "P";
    public static final String CUSTORDERID_Q = "Q";
    public static final String CUSTORDERID_E = "E";
    /**
     * 退料原因里是否需要Hold的关键
     */
    public static final String RETURN_HOLD_REASON = "质量问题";

    /**
     * 退料默认的HoldCode
     */
    public static final String RETURN_HOLD_CODE = "TL_HOLD";

    public static final String GENERATOR_RESERVED_ORDER_ID = "CreateReservedOrderId" ;

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

    @Autowired
    ProductRepository productRepository;

    @Autowired
    DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    PackageService packageService;

    @Autowired
    FinishGoodOrderRepository finishGoodOrderRepository;

    @Autowired
    RawMaterialRepository rawMaterialRepository;

    @Autowired
    MaterialLotUnitRepository materialLotUnitRepository;

    @Autowired
    PackagedLotDetailRepository packagedLotDetailRepository;

    @Autowired
    OqcCheckSheetRepository oqcCheckSheetRepository;

    @Autowired
    CheckSheetLineRepository checkSheetLineRepository;

    @Autowired
    MLotCheckSheetRepository mLotCheckSheetRepository;

    @Autowired
    MLotCheckSheetLineRepository mLotCheckSheetLineRepository;

    @Autowired
    MaterialLotInventoryRepository materialLotInventoryRepository;

    @Autowired
    MailService mailService;

    @Autowired
    MaterialLotHistoryRepository materialLotHistoryRepository;

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    IssueMaterialOrderRepository issueMaterialOrderRepository;

    @Autowired
    MaterialModelConversionRepository materialModelConversionRepository;

    @Autowired
    MaterialStatusModelRepository materialStatusModelRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    IQCCheckSheetRepository iqcCheckSheetRepository;

    @Autowired
    LabMaterialRepository labMaterialRepository;

    @Autowired
    VersionControlService versionControlService;

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
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat formats = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);

            BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getIncomingQty));

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

            for (String materialName : materialMap.keySet()) {
                RawMaterial rawMaterial = mmsService.getRawMaterialByName(materialName);
                if (rawMaterial == null) {
                    throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_IS_NOT_EXIST, materialName);
                }
                MaterialStatusModel materialStatusModel = mmsService.getStatusModelByRrn(rawMaterial.getStatusModelRrn());
                List<MaterialLot> materialLotList = materialMap.get(materialName);

                for (MaterialLot materialLot : materialLotList) {
                    //卡控数量不带小数点，不等于零
                    BigDecimal incomingQty = materialLot.getIncomingQty();
                    incomingQty.toBigIntegerExact();
                    if (incomingQty.compareTo(BigDecimal.ZERO) == 0){
                        throw new ClientParameterException(VanchipExceptions.INCOMING_QTY_EQUAL_ZERO, materialLot.getMaterialLotId());
                    }
                    materialLot.setCurrentQty(incomingQty);

                    if(!StringUtils.isNullOrEmpty(materialLot.getExpectedDeliveryDateValue())){
                        String expectedDeliveryDate = formats.format(simpleDateFormat.parse(materialLot.getExpectedDeliveryDateValue()));
                        materialLot.setExpectedDeliveryDate(formats.parse(expectedDeliveryDate));
                        materialLot.setExpectedDeliveryDateValue(StringUtils.EMPTY);
                    }

                    Map<String, Object> propMap = PropertyUtils.convertObj2Map(materialLot);
                    propMap.put("incomingDocRrn", incomingOrder.getObjectRrn());
                    propMap.put("incomingDocId", incomingOrder.getName());
                    MaterialLot mLot = mmsService.createMLot(rawMaterial, materialStatusModel, materialLot.getMaterialLotId(), materialLot.getCurrentQty(), materialLot.getCurrentSubQty(), propMap);
                    documentMaterialLots.add(mLot);
                }
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void deleteIncomingMaterialLot(List<MaterialLot> materialLotList, String deleteNote) throws ClientException{
        try {
            List<MaterialLot>  materialLots = materialLotList.stream().filter(materialLot -> materialLot.getStatus().equals("Create")).collect(Collectors.toList());
            for (MaterialLot materialLot : materialLots){
                if (!StringUtils.isNullOrEmpty(materialLot.getWorkOrderId())){
                    throw new ClientParameterException(VanchipExceptions.MATERIAL_LOT_ALREADY_BONDING_WORKORDER_ID, materialLot.getMaterialLotId());
                }
                Document document = documentRepository.findOneByName(materialLot.getIncomingDocId());
                BigDecimal qty = document.getQty().subtract(materialLot.getIncomingQty());
                BigDecimal unHandleQty = document.getUnHandledQty().subtract(materialLot.getIncomingQty());

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

    public List<MaterialLot> getMLotByOrderId(String documentId) throws ClientException{
        try {
            Document document = documentRepository.findOneByName(documentId);
            if (document == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, documentId);
            }
            List<MaterialLot> materialLots = documentService.getReservedMLotByDocId(documentId);

            return materialLots;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *指定物料批次发料
     * @param documentId
     * @param materialLotIdList
     * @throws ClientException
     */
    public void issueMLotByOrder(String documentId, List<String> materialLotIdList)throws ClientException{
        try {
            Document document = documentService.getDocumentByName(documentId, true);
            if (document instanceof IssueLotOrder){
                issueMLotByDoc(documentId, materialLotIdList);
            } else if (document instanceof IssueMaterialOrder) {
                issueMaterialByDoc(documentId, materialLotIdList);
            } else if (document instanceof IssueFinishGoodOrder){
                issueFinishGoodByDoc(documentId, materialLotIdList);
            } else {
                throw new ClientParameterException(DocumentException.DOCUMENT_CATEGORY_IS_NOT_EXIST, documentId);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 主材发料
     * @param documentId 主材发料单
     * @param materialLotIdList
     * @throws ClientException
     */
    public void issueMLotByDoc(String documentId, List<String> materialLotIdList) throws ClientException{
        try {
            documentService.issueMLotByDoc(documentId, materialLotIdList);

            mesService.issueMLot(materialLotIdList);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 辅材发料
     * @param documentId
     * @param materialLotIdList
     * @throws ClientException
     */
    public void issueMaterialByDoc(String documentId, List<String> materialLotIdList) throws ClientException{
        try {
            documentService.issueMaterialByDoc(documentId, materialLotIdList);

            mesService.issueMLot(materialLotIdList);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 成品发料
     * @param documentId
     * @param materialLotIds
     * @throws ClientException
     */
    public void issueFinishGoodByDoc(String documentId, List<String> materialLotIds) throws ClientException{
        try {
            documentService.issueFinishGoodByDoc(documentId, materialLotIds);

            mesService.issueMLot(materialLotIds);
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
     * 获得待备货批次
     * @param documentLine
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getReservedMaterialLot(DocumentLine documentLine) throws ClientException{
        try {
            DocumentLine docLine = documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());
            List<MaterialLot> materialLots = Lists.newArrayList();

            String reservedRule = docLine.getReserved24();
            if (StringUtils.isNullOrEmpty(reservedRule)){
                //根据发货单查询,且过滤查询出已经装箱的
                materialLots = materialLotRepository.findByReserved45AndBoxMaterialLotIdIsNullAndCategoryIsNull(documentLine.getLineId());
                return materialLots;
            }

            //未备货,未装箱,在库状态(Status=In),成品(MaterialCategory=Product)
            materialLots = materialLotRepository.findByReserved45IsNullAndBoxMaterialLotIdIsNullAndStatusAndMaterialCategory(MaterialStatus.STATUS_IN, Material.TYPE_PRODUCT);
            if (CollectionUtils.isEmpty(materialLots)){
                return materialLots;
            }

            Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByMLotDocRule(materialLots, reservedRule);

            List<DocumentLine> documentLineList = Lists.newArrayList();
            documentLineList.add(documentLine);
            Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMLotDocRule(documentLineList, reservedRule);

            for (String key : documentLineMap.keySet()) {
                if (StringUtils.SPLIT_CODE.equals(key) || !materialLotMap.keySet().contains(key)) {
                   return null;
                }
                materialLots = materialLotMap.get(key);
            }
            materialLots.forEach(materialLot -> validateMLotAndDocLineByRule(docLine, materialLot, reservedRule));
            return materialLots;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 备货
     * @param documentLine 发货通知单信息
     * @param materialLotActions
     */
    public void reservedMaterialLot(DocumentLine documentLine, List<MaterialLotAction> materialLotActions)throws ClientException{
        try {
            String reservedRemake = materialLotActions.get(0).getActionComment();
            String docLineObjRrn = documentLine.getObjectRrn();
            DocumentLine deliveryDocLine = documentLineRepository.findByObjectRrn(docLineObjRrn);
            if (deliveryDocLine == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, deliveryDocLine.getDocId());
            }
            DeliveryOrder deliveryOrder = deliveryOrderRepository.findOneByName(deliveryDocLine.getDocId());
            // 单据是否审核
            if (!Document.STATUS_APPROVE.equals(deliveryOrder.getStatus())){
                throw new ClientParameterException(DocumentException.DOCUMENT_STATUS_IS_NOT_ALLOW, deliveryOrder.getName());
            }

            BigDecimal unReservedQty = deliveryDocLine.getUnReservedQty();
            BigDecimal transQty = BigDecimal.ZERO;

            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            for (MaterialLot materialLot : materialLots) {
                materialLot.validateMLotHold();
                //validateMLotAndDocLineByRule(deliveryDocLine, materialLot, MLOT_RESERVED_DOC_VALIDATE_RULE_ID);
                BigDecimal currentQty = materialLot.getCurrentQty();
                transQty = transQty.add(currentQty);
                if (unReservedQty.compareTo(transQty) < 0) {
                    throw new ClientParameterException(VanchipExceptions.RESERVED_OVER_QTY,materialLot.getMaterialLotId());
                }
                //将发货单据绑定到批次上
                materialLot.setReservedQty(materialLot.getCurrentQty());
                materialLot.setReserved44(deliveryDocLine.getObjectRrn());
                materialLot.setReserved45(deliveryDocLine.getLineId());

                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setActionComment(reservedRemake);
                baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_RESERVED, materialLotAction);
            }

            deliveryDocLine.setUnReservedQty(deliveryDocLine.getUnReservedQty().subtract(transQty));
            deliveryDocLine.setReservedQty(deliveryDocLine.getReservedQty().add(transQty));
            documentLineRepository.save(deliveryDocLine);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
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
                List<MaterialLot> materialLotList = docLineReservedMaterialLotMap.get(docLineObjRrn);
                materialLotList.forEach(materialLot -> {
                    materialLot.setReservedQty(materialLot.getReservedQty().subtract(materialLot.getReservedQty()));
                    materialLot.setReserved44(StringUtils.EMPTY);
                    materialLot.setReserved45(StringUtils.EMPTY);

                    MaterialLotAction materialLotAction = new MaterialLotAction();
                    materialLotAction.setActionComment(unReservedRemake);
                    baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_UNRESERVED, materialLotAction);

                });
                BigDecimal totalNumber = materialLotList.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot::getCurrentQty));

                docLine.setReservedQty(docLine.getReservedQty().subtract(totalNumber));
                docLine.setUnReservedQty(docLine.getUnReservedQty().add(totalNumber));
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
            //第一次打印时生成一个备货单流水号
            if(StringUtils.isNullOrEmpty(documentLine.getReserved23())){
                String reservedOrderId = documentService.generatorDocId(GENERATOR_RESERVED_ORDER_ID);
                documentLine.setReserved23(reservedOrderId);
                baseService.saveEntity(documentLine);
            }
            String docLineObjRrn = documentLine.getObjectRrn();
            List<MaterialLot> materialLotList = getMLotByLineObjectRrn(docLineObjRrn);
            //过滤掉已经打包好的
            materialLotList = materialLotList.stream().filter(mlot->StringUtils.isNullOrEmpty(mlot.getPackageType())).collect(Collectors.toList());
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

    public List<MaterialLot> getWaitShipMLotByDocLine(DocumentLine documentLine) throws ClientException{
        try {
            return materialLotRepository.findByReserved44AndCategory(documentLine.getObjectRrn(), StringUtils.YES);
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * mes成品入库
     * 入库后根据条件Hold
     * 验证hold物料入hold仓
     * @param materialLots
     * @param materialLotActionList
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> stockInFinishGood(List<MaterialLot> materialLots, List<MaterialLotAction> materialLotActionList) throws ClientException {
        try {
            List<MaterialLot> materialLotList = Lists.newArrayList();
            materialLots.forEach(materialLot -> {
                MaterialLotAction materialLotAction = materialLotActionList.stream().filter(action -> action.getMaterialLotId().equals(materialLot.getMaterialLotId())).findFirst().get();
                MaterialLot stockInMaterialLot = mmsService.stockIn(materialLot, materialLotAction);

                autoHoldFinishGood(stockInMaterialLot);

                materialLotAction.setMaterialLotId(stockInMaterialLot.getMaterialLotId());
                mmsService.validateHoldMLotMatchedHoldWarehouse(materialLotAction);

                materialLotList.add(stockInMaterialLot);
            });
            return materialLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 成品全部hold,客户订单号信息 hold,mrb信息 Hold
     * @param materialLot
     */
    public void autoHoldFinishGood(MaterialLot materialLot) throws ClientException{
        try {
            List<MaterialLotAction> materialLotActionList = getHoldFinishGoodAction(materialLot);
            if (CollectionUtils.isEmpty(materialLotActionList)){
                return;
            }
            mmsService.holdMaterialLot(materialLotActionList);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获得成品hold 动作
     * 1.成品 hold
     * 2.客户订单号 hold
     * 3.MRB物料 hold
     * @param materialLot
     */
    public List<MaterialLotAction>  getHoldFinishGoodAction(MaterialLot materialLot) throws ClientException{
        List<MaterialLotAction> materialLotActions = Lists.newArrayList();

        Material material = productRepository.findOneByName(materialLot.getMaterialName());
        if (!Material.CLASS_PRODUCT.equals(material.getClazz())){
            return  materialLotActions;
        }

        MaterialLotAction materialLotAction = new MaterialLotAction();
        materialLotAction.setActionCode(PRE_HOLD);
        materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
        materialLotActions.add(materialLotAction);

        MaterialLotAction holdByMRBAction = getHoldByMRBAction(materialLot);
        if (holdByMRBAction != null){
            materialLotActions.add(holdByMRBAction);
        }

        materialLotActions = getHoldByCustomerOrderIdAction(materialLotActions, materialLot);
        return materialLotActions;
    }

    /**
     * 根据客户订单号获得hold action
     * @param materialLot
     */
    public List<MaterialLotAction> getHoldByCustomerOrderIdAction(List<MaterialLotAction> materialLotActions ,MaterialLot materialLot) throws ClientException{
        String customerOrderId = materialLot.getReserved6();
        if (StringUtils.isNullOrEmpty(customerOrderId)){
            return materialLotActions;
        }

        String firstCustOrderId = customerOrderId.substring(0, 1);
        String secondCustOrderId = customerOrderId.substring(1, 2);
        String thirdCustOrderId = customerOrderId.substring(2, 3);

        MaterialLotAction mLotAction = new MaterialLotAction();
        //根据客户订单第一位hold
        switch (firstCustOrderId){
            case CUSTORDERID_P :
                mLotAction.setActionCode(P_HOLD);
                mLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                materialLotActions.add(mLotAction);
                break;
            case CUSTORDERID_Q :
                mLotAction.setActionCode(Q_HOLD);
                mLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                materialLotActions.add(mLotAction);
                break;
            default : break;
        }

        //根据客户订单第二位hold
        if (CUSTORDERID_S.equals(secondCustOrderId)){
            MaterialLotAction action = new MaterialLotAction();
            action.setActionCode(S_HOLD);
            action.setMaterialLotId(materialLot.getMaterialLotId());
            materialLotActions.add(action);
        }

        //根据客户订单第三位hold
        if (CUSTORDERID_E.equals(thirdCustOrderId)){
            MaterialLotAction action = new MaterialLotAction();
            action.setActionCode(E_HOLD);
            action.setMaterialLotId(materialLot.getMaterialLotId());
            materialLotActions.add(action);
        }

        return materialLotActions;
    }

    /**
     * 根据MRB属性获得hold action
     * @param materialLot
     * @return
     * @throws ClientException
     */
    public MaterialLotAction getHoldByMRBAction(MaterialLot materialLot)throws ClientException{
        String mrb = materialLot.getReserved16();
        if (StringUtils.isNullOrEmpty(mrb)){
            return null;
        }

        MaterialLotAction action = new MaterialLotAction();
        action.setActionCode(O_MRB_HOLD);
        action.setMaterialLotId(materialLot.getMaterialLotId());
        return action;
    }


    /**
     * 验证来料单 是否全部接收
     * @param incomingDocId
     */
    public Boolean validateIncomingOrderReceived(String incomingDocId) throws ClientException{
        try {
            boolean flag = false;
            List<MaterialLot> materialLots = materialLotRepository.findByIncomingDocId(incomingDocId);
            //先判定状态是否一致,过滤掉已经iqc完成的
            materialLots = materialLots.stream().filter(mlot-> MaterialStatus.STATUS_IQC.equals(mlot.getStatus()) || MaterialStatus.STATUS_CREATE.equals(mlot.getStatus())).collect(Collectors.toList());
            Set<String> statusSet = materialLots.stream().map(mLot -> mLot.getStatus()).collect(Collectors.toSet());
            if (statusSet.size() > 1 ){
                return flag;
            }
            //状态是否为IQC
            flag = statusSet.contains(MaterialStatus.STATUS_IQC);
            return flag;
        }catch (Exception  e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证并获得待IQC的物料批次
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> validationAndGetWaitIqcMLot(List<String> materialLotIds) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotIds.stream().map(materialLotId -> mmsService.getMLotByMLotId(materialLotId, true)).collect(Collectors.toList());

            Map<String, List<MaterialLot>> incomingDocIdMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot::getIncomingDocId));
            for (String incomingDocId : incomingDocIdMap.keySet()) {
                Boolean flag = validateIncomingOrderReceived(incomingDocId);
                if (!flag){
                    materialLots = materialLots.stream().filter(mlot -> !incomingDocId.equals(mlot.getIncomingDocId())).collect(Collectors.toList());
                }
            }
            return materialLots;
        }catch (Exception  e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 批量IQC
     * @param materialLotIds
     * @param materialLotAction
     * @throws ClientException
     */
    public void batchIqc(List<String> materialLotIds, MaterialLotAction materialLotAction) throws ClientException{
        try {
            for (String materialLotId : materialLotIds) {
                materialLotAction.setMaterialLotId(materialLotId);
                mmsService.iqc(materialLotAction);
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 创建成品单据
     * @param documentId
     * @param approveFlag
     * @param materialLots
     * @throws ClientException
     */
    public void createFinishGoodOrder(String documentId, boolean approveFlag, List<MaterialLot> materialLots) throws ClientException {
        try {
            if (StringUtils.isNullOrEmpty(documentId)) {
                documentId = documentService.generatorDocId(FinishGoodOrder.GENERATOR_FINISH_GOOD_ORDER_ID_RULE);
            }
            FinishGoodOrder finishGoodOrder = finishGoodOrderRepository.findOneByName(documentId);
            if (finishGoodOrder != null) {
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_EXIST, documentId);
            }
            BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot::getCurrentQty));
            finishGoodOrder = new FinishGoodOrder();
            finishGoodOrder.setName(documentId);
            finishGoodOrder.setQty(totalQty);
            finishGoodOrder.setUnHandledQty(totalQty);
            if (approveFlag) {
                finishGoodOrder.setStatus(Document.STATUS_APPROVE);
            }
            finishGoodOrder = (FinishGoodOrder) baseService.saveEntity(finishGoodOrder);

            Map<String, List<MaterialLot>> materialLotMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot::getMaterialName));

            for (String materialName : materialLotMap.keySet()) {
                Product product = productRepository.findOneByName(materialName);
                if (product == null) {
                    throw new ClientParameterException(MmsException.MM_PRODUCT_IS_NOT_EXIST, materialName);
                }
                MaterialStatusModel materialStatusModel = mmsService.getStatusModelByRrn(product.getStatusModelRrn());

                List<MaterialLot> materialLotList = materialLotMap.get(materialName);
                for (MaterialLot materialLot : materialLotList) {
                    DocumentMLot documentMLot = new DocumentMLot();
                    documentMLot.setDocumentId(finishGoodOrder.getName());
                    documentMLot.setMaterialLotId(materialLot.getMaterialLotId());
                    documentMLotRepository.save(documentMLot);

                    Map<String, Object> materialLotParaMap = Maps.newHashMap();

                    materialLotParaMap.put("grade", materialLot.getGrade());
                    materialLotParaMap.put("reserved2", product.getReserved5());
                    materialLotParaMap.put("reserved3", product.getReserved6());
                    materialLotParaMap.put("reserved6", materialLot.getReserved6());
                    materialLotParaMap.put("reserved12", materialLot.getReserved12());
                    materialLotParaMap.put("reserved16", materialLot.getReserved16());
                    materialLotParaMap.put("reserved47", materialLot.getReserved47());
                    materialLotParaMap.put("reserved48", materialLot.getReserved48());

                    MaterialLot mLot = mmsService.createMLot(product, materialStatusModel, materialLot.getMaterialLotId(), materialLot.getCurrentQty(), BigDecimal.ZERO, materialLotParaMap);

                    List<MaterialLotUnit> materialLotUnits = materialLot.getMaterialLotUnits();
                    materialLotUnits.forEach(materialLotUnit -> {
                        Map<String, Object> materialLotUnitParaMap = Maps.newHashMap();
                        materialLotUnitParaMap.put("unitId", materialLotUnit.getUnitId());
                        materialLotUnitParaMap.put("qty", materialLotUnit.getQty());
                        materialLotUnitParaMap.put("reserved1", materialLotUnit.getReserved1());
                        materialLotUnitParaMap.put("reserved2", materialLotUnit.getReserved2());
                        materialLotUnitParaMap.put("reserved3", materialLotUnit.getReserved3());
                        materialLotUnitParaMap.put("reserved4", materialLotUnit.getReserved4());
                        materialLotUnitParaMap.put("grade", materialLotUnit.getGrade());

                        saveMLotUnit(product, mLot, materialLotUnit.getQty(), materialLotUnitParaMap);
                    });
                }
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存MaterialLotUnit 信息
     * @return
     */
    public MaterialLotUnit saveMLotUnit(Material material, MaterialLot materialLot, BigDecimal transQty, Map<String, Object> propsMap) throws ClientException {
        try {
            MaterialLotUnit materialLotUnit = new MaterialLotUnit();

            if (propsMap != null && propsMap.size() > 0) {
                for (String propName : propsMap.keySet()) {
                    Object propValue = propsMap.get(propName);
                    if (propValue == null || StringUtils.isNullOrEmpty(propValue.toString())) {
                        continue;
                    }
                    PropertyUtils.setProperty(materialLotUnit, propName, propsMap.get(propName));
                }
            }
            materialLotUnit.setMaterialLot(materialLot);
            materialLotUnit.setMaterial(material);
            materialLotUnit.setQty(transQty);

            materialLotUnit = (MaterialLotUnit)baseService.saveEntity(materialLotUnit);
            return materialLotUnit;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 成品接收
     * @param documentId
     * @param materialLotIdList
     * @throws ClientException
     */
    public void receiveFinishGood(String documentId, List<String> materialLotIdList) throws ClientException{
        try {
            FinishGoodOrder finishGoodOrder = (FinishGoodOrder) finishGoodOrderRepository.findOneByName(documentId);
            if (finishGoodOrder == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_EXIST, documentId);
            }
            List<MaterialLot> materialLotList = documentService.validationDocReservedMLot(documentId, materialLotIdList);
            BigDecimal totalQty = materialLotList.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot::getCurrentQty));
            if (finishGoodOrder.getUnHandledQty().compareTo(totalQty)<0){
                throw new ClientException(DocumentException.DOCUMENT_QTY_NOT_ENOUGH);
            }
            finishGoodOrder.setUnHandledQty(finishGoodOrder.getUnHandledQty().subtract(totalQty));
            finishGoodOrder.setHandledQty(finishGoodOrder.getHandledQty().add(totalQty));
            baseService.saveEntity(finishGoodOrder);

            Map<String, List<MaterialLot>> materialNameMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot::getMaterialName));
            for (String materialName : materialNameMap.keySet()) {
                Material product = productRepository.findOneByName(materialName);
                receiveFinishGood(product, materialNameMap.get(materialName));
            }
        }catch (Exception e){
            ExceptionManager.handleException(e, log);
        }
    }

    public List<MaterialLot> receiveFinishGood(Material material ,List<MaterialLot> materialLotList) throws ClientException{
        try {
            if (material instanceof Product) {
                material = productRepository.findOneByName(material.getName());
            }
            if (material == null) {
                throw new ClientParameterException(MmsException.MM_PRODUCT_IS_NOT_EXIST, material.getName());
            }

            for (MaterialLot materialLot : materialLotList){
                //改变状态
                MaterialLot mLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_RECEIVE, MaterialStatus.STATUS_RECEIVE);
                baseService.saveHistoryEntity(mLot, MaterialLotHistory.TRANS_TYPE_RECEIVE);
            }
            return materialLotList ;
        }catch (Exception e){
            throw ExceptionManager.handleException(e,log);
        }
    }

    /**
     * 触发OQC
     * @param materialLot
     * @throws ClientException
     */
    public void triggerOqc(MaterialLot materialLot) throws ClientException{
        try {
            OqcCheckSheet oqcCheckSheet = oqcCheckSheetRepository.findOneByName(Material.OQC_SHEET_NAME);

            MLotCheckSheet mLotCheckSheet = new MLotCheckSheet();
            mLotCheckSheet.setMaterialLotId(materialLot.getMaterialLotId());
            mLotCheckSheet.setSheetName(oqcCheckSheet.getName());
            mLotCheckSheet.setSheetDesc(oqcCheckSheet.getDescription());
            mLotCheckSheet.setSheetCategory(oqcCheckSheet.getCategory());
            mLotCheckSheet = mLotCheckSheetRepository.save(mLotCheckSheet);

            List<CheckSheetLine> checkSheetLines = checkSheetLineRepository.findByCheckSheetRrn(oqcCheckSheet.getObjectRrn());
            for (CheckSheetLine checkSheetLine : checkSheetLines) {
                MLotCheckSheetLine mLotCheckSheetLine = new MLotCheckSheetLine();
                mLotCheckSheetLine.setMLotCheckSheetRrn(mLotCheckSheet.getObjectRrn());

                mLotCheckSheetLine.setName(checkSheetLine.getName());
                mLotCheckSheetLine.setDescription(checkSheetLine.getDescription());

                mLotCheckSheetLine.setSheetName(oqcCheckSheet.getName());
                mLotCheckSheetLine.setSheetDesc(oqcCheckSheet.getDescription());
                mLotCheckSheetLineRepository.save(mLotCheckSheetLine);
            };
        }catch (Exception e){
            throw ExceptionManager.handleException(e,log);
        }
    }

    /**
     * 包装
     * @param materialLotActionList
     * @param packageType
     * @return
     * @throws ClientException
     */
    public MaterialLot packageMaterialLots(List<MaterialLotAction> materialLotActionList, String packageType) throws ClientException{
        try {
            MaterialLotAction firstMaterialLotAction = materialLotActionList.get(0);

            List<MaterialLot> materialLots = materialLotActionList.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());

            packageService.validationPackageRule(materialLots, packageType);
            MaterialLotPackageType materialLotPackageType = packageService.getMaterialPackageTypeByName(packageType);

            MaterialLot packageMLot = (MaterialLot) materialLots.get(0).clone();
            DocumentLine documentLine = documentLineRepository.findByObjectRrn(packageMLot.getReserved44());

            String packageMLotId = vcGeneratorPackageMaterialLotId(materialLotPackageType.getPackIdRule(), documentLine);
            if (mmsService.getMLotByMLotId(packageMLotId) != null) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_IS_EXIST);
            }

            packageMLot.setMaterialLotId(packageMLotId);
            packageMLot.setCurrentQty(materialLotPackageType.getPackedQty(materialLotActionList));
            packageMLot.setReceiveQty(packageMLot.getCurrentQty());

            packageMLot.initialMaterialLot();
            packageMLot.initialWarehouseAndStorageInfo();
            packageMLot.setStatusCategory(MaterialStatusCategory.STATUS_CATEGORY_USE);
            packageMLot.setStatus(MaterialStatus.STATUS_PACKAGE);
            packageMLot.setPackageType(packageType);
            packageMLot.setCategory(StringUtils.YES);
            packageMLot.setMaterialType(StringUtils.isNullOrEmpty(materialLotPackageType.getTargetMaterialType()) ? packageMLot.getMaterialType() : materialLotPackageType.getTargetMaterialType());
            //TODO 计算理论重量

            packageMLot = (MaterialLot) baseService.saveEntity(packageMLot, MaterialLotHistory.TRANS_TYPE_CREATE_PACKAGE, firstMaterialLotAction);

            // 触发OQC
            triggerOqc(packageMLot);

            packageMaterialLots(packageMLot, materialLots, materialLotActionList);
            return packageMLot;
        }catch (Exception e){
            throw ExceptionManager.handleException(e,log);
        }
    }

    /**
     * VanChip客制化 生成外箱批次号
     * @param generatorRule 用于获取该单据目前装箱次数
     * @param documentLine
     * @return
     * @throws ClientException
     */
    public String vcGeneratorPackageMaterialLotId(String generatorRule, DocumentLine documentLine) throws ClientException {
        List<MaterialLot> materialLots = materialLotRepository.findByReserved44(documentLine.getObjectRrn());
        List<MaterialLot> boxMLots = materialLots.stream().filter(materialLot -> materialLot.getCategory() != null && materialLot.getCategory().equals(StringUtils.YES)).collect(Collectors.toList());

        //箱数量
        int boxQty = boxMLots.size()+1;
        StringBuffer boxId = new StringBuffer(boxQty+StringUtils.EMPTY);

        GeneratorContext generatorContext = new GeneratorContext();
        generatorContext.setObject(documentLine);
        generatorContext.setRuleName(generatorRule);
        String  ruleId= generatorService.generatorId(generatorContext);

        boxId.append(ruleId);
        String boxMLotId = boxId.toString();
        return boxMLotId;
    }

    /**
     * 对被包装批次进行操作
     * @param packedMaterialLot
     * @param waitToPackingLot
     * @param materialLotActions
     */
    private void packageMaterialLots(MaterialLot packedMaterialLot, List<MaterialLot> waitToPackingLot, List<MaterialLotAction> materialLotActions) {
           for (MaterialLot materialLot : waitToPackingLot) {
               String materialLotId = materialLot.getMaterialLotId();
               MaterialLotAction materialLotAction = materialLotActions.stream().filter(action -> materialLotId.equals(action.getMaterialLotId())).findFirst().get();

               BigDecimal currentQty = materialLot.getCurrentQty();

               if (currentQty.compareTo(materialLotAction.getTransQty()) == 0) {
                   materialLot.setBoxMaterialLotRrn(packedMaterialLot.getObjectRrn());
                   materialLot.setBoxMaterialLotId(packedMaterialLot.getMaterialLotId());
                   materialLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_PACKAGE, MaterialStatus.STATUS_PACKAGE);
               }

               materialLot = materialLotRepository.saveAndFlush(materialLot);
               baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_PACKAGE, materialLotAction);

               // 记录包装详情
               PackagedLotDetail packagedLotDetail = packagedLotDetailRepository.findByPackagedLotRrnAndMaterialLotRrn(packedMaterialLot.getObjectRrn(), materialLot.getObjectRrn());
               if (packagedLotDetail == null) {
                   packagedLotDetail = new PackagedLotDetail();
                   packagedLotDetail.setPackagedLotRrn(packedMaterialLot.getObjectRrn());
                   packagedLotDetail.setPackagedLotId(packedMaterialLot.getMaterialLotId());
                   packagedLotDetail.setMaterialLotRrn(materialLot.getObjectRrn());
                   packagedLotDetail.setMaterialLotId(materialLot.getMaterialLotId());
               }
               packagedLotDetail.setQty(packagedLotDetail.getQty().add(materialLotAction.getTransQty()));
               packagedLotDetailRepository.save(packagedLotDetail);
           }
    }

    /**
     * 拆包装
     * @param materialLotActions
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> unPack(List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            List<MaterialLot> unPackedMainMaterialLots = Lists.newArrayList();
            Map<String, List<MaterialLot>> packedLotMap = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true))
                    .collect(Collectors.groupingBy(MaterialLot::getBoxMaterialLotId));
            for (String packageMLotId : packedLotMap.keySet()) {
                MaterialLot packagedLot = mmsService.getMLotByMLotId(packageMLotId, true);
                packagedLot = unPack(packagedLot, packedLotMap.get(packageMLotId), materialLotActions);
                unPackedMainMaterialLots.add(packagedLot);
            }
            return unPackedMainMaterialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 拆包装
     * @param packedMaterialLot 待拆包装的包装批次
     * @param waitToUnPackageMLots 待拆出来的包装批次
     * @param materialLotActions 待拆出来的包装批次的动作。拆包数量以action中的数量为准
     * @throws ClientException
     */
    public MaterialLot unPack(MaterialLot packedMaterialLot, List<MaterialLot> waitToUnPackageMLots, List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            MaterialLotPackageType materialLotPackageType = packageService.getMaterialPackageTypeByName(packedMaterialLot.getPackageType());
            BigDecimal unPackedQty = materialLotPackageType.getPackedQty(materialLotActions);

            packedMaterialLot.setCurrentQty(packedMaterialLot.getCurrentQty().subtract(unPackedQty));
            if (packedMaterialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                packedMaterialLot = mmsService.changeMaterialLotState(packedMaterialLot, MaterialEvent.EVENT_UN_PACKAGE, StringUtils.EMPTY);
            } else {
                //如果进行装箱检验，恢复到装箱状态
                //TODO 减去理论重量,清空 毛重栏位
                packedMaterialLot.setStatusCategory(MaterialStatusCategory.STATUS_CATEGORY_USE);
                packedMaterialLot.setStatus(MaterialStatus.STATUS_PACKAGE);
            }
            baseService.saveEntity(packedMaterialLot, MaterialLotHistory.TRANS_TYPE_UN_PACKAGE);

            Map<String, MaterialLotAction> materialLotActionMap = materialLotActions.stream().collect(Collectors.toMap(MaterialLotAction :: getMaterialLotId, Function.identity()));

            Map<String, PackagedLotDetail> packagedLotDetails = packagedLotDetailRepository.findByPackagedLotRrn(packedMaterialLot.getObjectRrn()).stream().collect(Collectors.toMap(PackagedLotDetail :: getMaterialLotId, Function.identity()));

            for (MaterialLot waitToUnPackageMLot : waitToUnPackageMLots) {
                MaterialLotAction materialLotAction = materialLotActionMap.get(waitToUnPackageMLot.getMaterialLotId());
                waitToUnPackageMLot.setBoxMaterialLotId(StringUtils.EMPTY);
                waitToUnPackageMLot.setBoxMaterialLotRrn(StringUtils.EMPTY);
                waitToUnPackageMLot.restoreStatus();
                baseService.saveEntity(waitToUnPackageMLot, MaterialLotHistory.TRANS_TYPE_UN_PACKAGE, materialLotAction);

                // 更新packageDetail数量
                PackagedLotDetail packagedLotDetail = packagedLotDetails.get(waitToUnPackageMLot.getMaterialLotId());
                packagedLotDetail.setQty(packagedLotDetail.getQty().subtract(materialLotAction.getTransQty()));
                if (packagedLotDetail.getQty().compareTo(BigDecimal.ZERO) == 0) {
                    packagedLotDetailRepository.deleteById(packagedLotDetail.getObjectRrn());
                } else {
                    packagedLotDetailRepository.save(packagedLotDetail);
                }
            }
            return packedMaterialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 追加包装
     * 当前不卡控被追加包装批次的状态
     * @param packedMaterialLot 被追加的包装批次
     * @param materialLotActions 包装批次动作
     * @return
     */
    public MaterialLot appendPacking(MaterialLot packedMaterialLot, List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            packedMaterialLot = mmsService.getMLotByMLotId(packedMaterialLot.getMaterialLotId(), true);
            packedMaterialLot.isFinish();
            // 取第一个的materialAction作为所有者的actionCode
            MaterialLotAction firstMaterialAction = materialLotActions.get(0);
            List<MaterialLot> allMaterialLot = Lists.newArrayList();

            List<MaterialLot> waitToAddPackingMLots = materialLotActions.stream().map(action -> mmsService.getMLotByMLotId(action.getMaterialLotId())).collect(Collectors.toList());
            allMaterialLot.addAll(waitToAddPackingMLots);
            // 取到包装规则
            MaterialLotPackageType materialLotPackageType = packageService.getMaterialPackageTypeByName(packedMaterialLot.getPackageType());

            // 将包装的和以前包装的放在一起进行包装规则验证
            List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(packedMaterialLot.getObjectRrn());

            List<MaterialLotAction> allMaterialLotAction = Lists.newArrayList(materialLotActions);
            if (CollectionUtils.isNotEmpty(packageDetailLots)) {
                allMaterialLot.addAll(packageDetailLots);
                for (MaterialLot packageDetailLot : packageDetailLots) {
                    MaterialLotAction packedMLotAction = new MaterialLotAction();
                    packedMLotAction.setMaterialLotId(packageDetailLot.getMaterialLotId());
                    packedMLotAction.setTransQty(packageDetailLot.getCurrentQty());
                    allMaterialLotAction.add(packedMLotAction);
                }
            }

            materialLotPackageType.validationAppendPacking(waitToAddPackingMLots, allMaterialLotAction);
            if (!StringUtils.isNullOrEmpty(materialLotPackageType.getMergeRule())) {
                mmsService.validationMergeRule(materialLotPackageType.getMergeRule(), allMaterialLot);
            }
            //如果进行到了装箱检验或者OQC重置状态到 use-package
//            packedMaterialLot.setPreStatusCategory(packedMaterialLot.getStatusCategory());
//            packedMaterialLot.setPreStatus(packedMaterialLot.getStatus());

            packedMaterialLot.setStatusCategory(MaterialStatusCategory.STATUS_CATEGORY_USE);
            packedMaterialLot.setStatus(MaterialStatus.STATUS_PACKAGE);
            packedMaterialLot.setCurrentQty(materialLotPackageType.getPackedQty(allMaterialLotAction));
            //TODO 计算理论重量
            baseService.saveEntity(packedMaterialLot, MaterialLotHistory.TRANS_TYPE_ADDITIONAL_PACKAGE, firstMaterialAction);

            packageMaterialLots(packedMaterialLot, waitToAddPackingMLots, materialLotActions);
            return packedMaterialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }



    public List<MaterialLot> getStockOutMLot(String documentLineId)throws ClientException{

        return null;
    }

    /**
     * 发货
     * @param documentLine
     * @param materialLotActions
     */
    public void stockOut(DocumentLine documentLine, List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            documentLine = documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());

            DeliveryOrder deliveryOrder = deliveryOrderRepository.findOneByName(documentLine.getDocId());
            if (deliveryOrder == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, documentLine.getLineId());
            }

            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());

            BigDecimal handledQty = BigDecimal.ZERO;
            for (MaterialLot materialLot : materialLots) {
                handledQty = handledQty.add(materialLot.getCurrentQty());

                materialLot.setCurrentQty(BigDecimal.ZERO);
                materialLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_SHIP, StringUtils.EMPTY);
                baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_SHIP);

                List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                if(CollectionUtils.isNotEmpty(packageDetailLots)){
                    for (MaterialLot packageLot : packageDetailLots){
                        packageLot.setCurrentQty(BigDecimal.ZERO);
                        packageLot =  mmsService.changeMaterialLotState(packageLot, MaterialEvent.EVENT_SHIP, StringUtils.EMPTY);
                        baseService.saveHistoryEntity(packageLot, MaterialLotHistory.TRANS_TYPE_SHIP);
                    }
                }
            }

            documentLine.setUnHandledQty(documentLine.getUnHandledQty().subtract(handledQty));
            documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
            baseService.saveEntity(documentLine);

            deliveryOrder.setUnHandledQty(deliveryOrder.getUnHandledQty().subtract(handledQty));
            deliveryOrder.setHandledQty(deliveryOrder.getHandledQty().add(handledQty));
            baseService.saveEntity(deliveryOrder);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * 根据单据获得库存物料
     * @param document
     * @return
     * @throws ClientException
     */
    public List<MaterialLotInventory> getMLotInventoryByDoc(Document document) throws ClientException{
        try {
            document = documentRepository.findOneByName(document.getName());
            if (document == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST,document.getName());
            }
            List<MaterialLot> materialLots = Lists.newArrayList();
            if(Document.CATEGORY_INCOMING.equals(document.getCategory())){
                materialLots =  materialLotRepository.findByIncomingDocId(document.getName());
            }else {
                materialLots =  documentService.getReservedMLotByDocId(document.getName());
            }

            List<String> waitStockOutMLotIds = Lists.newArrayList();
            materialLots.forEach(materialLot -> waitStockOutMLotIds.add(materialLot.getMaterialLotId()));

            List<MaterialLotInventory> materialLotInventorys = materialLotInventoryRepository.findByMaterialLotIdIn(waitStockOutMLotIds);
            return materialLotInventorys;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *根据发料单或者发货单 获得相应库存物料
     * @param documentId 单据号
     * @return
     * @throws ClientException
     */
    public List<MaterialLotInventory> getMLotInventoryByDocId(String documentId) throws ClientException{
        try {
            List<MaterialLotInventory> materialLotInventorys = Lists.newArrayList();
            List<MaterialLot> materialLots = Lists.newArrayList();

            Document document = documentRepository.findOneByName(documentId);
            if (document != null){
                if(Document.CATEGORY_ISSUE_LOT.equals(document.getCategory()) || Document.CATEGORY_ISSUE_FINISH_GOOD.equals(document.getCategory()) || Document.CATEGORY_ISSUE_MLOT.equals(document.getCategory())){
                    materialLots =  documentService.getReservedMLotByDocId(document.getName());
                }else {
                    return materialLotInventorys;
                }

            }else {
                DocumentLine documentLine = documentLineRepository.findByLineId(documentId);
                if (documentLine == null){
                    throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST,documentId);
                }
                materialLots =  materialLotRepository.findByReserved44(documentLine.getObjectRrn());
            }

            List<String> materialLotIds = Lists.newArrayList();
            materialLots.forEach(materialLot -> materialLotIds.add(materialLot.getMaterialLotId()));

            materialLotInventorys = materialLotInventoryRepository.findByMaterialLotIdIn(materialLotIds);
            return materialLotInventorys;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据父箱号获得物料批次
     * @param materialLotId
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getMLotByBoxMaterialLotId(String materialLotId) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotRepository.findByBoxMaterialLotId(materialLotId);
            return materialLots;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 批量领料
     * @param materialLotActions
     * @throws ClientException
     */
    public void picks(List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            materialLotActions.forEach(materialLotAction -> {
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(),true);
                mmsService.pick(materialLot, materialLotAction);
            });
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 称重 保存毛重并计算净重
     * @param materialLotId
     * @param grossWeight 毛重
     * @return
     * @throws ClientException
     */
    public MaterialLot weightMaterialLot(String materialLotId, String grossWeight, String cartonSize) throws ClientException{
        try {
            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId, true);
            if (StringUtils.isNullOrEmpty(grossWeight)){
                throw new ClientException(VanchipExceptions.GROSS_WEIGHT_IS_NULL);
            }

            //箱净重 = 单个ReelCode * newWeight
            List<MaterialLot> materialLots = materialLotRepository.findByBoxMaterialLotId(materialLot.getMaterialLotId());
            String netWeightStr = materialLots.get(0).getReserved12();
            if (StringUtils.isNullOrEmpty(netWeightStr)){
                int reelCodeQtyInt = materialLots.size();
                BigDecimal netWeight = new BigDecimal(netWeightStr);
                BigDecimal reelCodeQty = new BigDecimal(reelCodeQtyInt);
                BigDecimal boxNetWeight = netWeight.multiply(reelCodeQty);
                materialLot.setReserved12(boxNetWeight.toPlainString());
            }

            materialLot.setReserved10(cartonSize);
            materialLot.setReserved13(grossWeight);
            materialLot = (MaterialLot)baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_WEIGHT);
            return materialLot;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 箱标签数据
     * @param materialLotId
     * @return
     * @throws ClientException
     */
    public Map<String, Object> getBoxPrintParameter(String materialLotId) throws ClientException{
        try {
            Map<String, Object> parameterMap = Maps.newHashMap();

            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId,true);
            DocumentLine deliveryOrderLine = documentLineRepository.findByObjectRrn(materialLot.getReserved44());
            BigDecimal deliveryOrderQty = deliveryOrderLine.getQty();

            BigDecimal totalBoxQty = deliveryOrderQty.divide(new BigDecimal("30000"),  RoundingMode.UP);

            String shippingDate = StringUtils.EMPTY;
            if (deliveryOrderLine.getShippingDate() != null){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                shippingDate = simpleDateFormat.format(deliveryOrderLine.getShippingDate());
            }

            parameterMap.put("from", deliveryOrderLine.getReserved11());
            parameterMap.put("to", deliveryOrderLine.getReserved12());
            parameterMap.put("toAdd", deliveryOrderLine.getReserved13());
            parameterMap.put("deliveryNumber", deliveryOrderLine.getReserved21());
            parameterMap.put("shippingDate", shippingDate);
            parameterMap.put("poNumber", deliveryOrderLine.getReserved20());

            String boxNumber = materialLotId.substring(0, materialLotId.indexOf(StringUtils.SPLIT_CODE));

            List<MaterialLot> materialLots = materialLotRepository.findByBoxMaterialLotId(materialLotId);
            if (CollectionUtils.isNotEmpty(materialLots)){
                parameterMap.put("partNumber", materialLots.get(0).getReserved2());
            }
            parameterMap.put("quantity", materialLot.getCurrentQty().toPlainString());
            parameterMap.put("boxNumber", boxNumber);
            parameterMap.put("totalBoxNumber", totalBoxQty );
            parameterMap.put("gW", materialLot.getReserved13());
            parameterMap.put("nW", materialLot.getReserved12());
            parameterMap.put("countOfOrigin", "China");
            parameterMap.put("boxId", materialLot.getMaterialLotId());
            parameterMap.put("printNumber", 2+StringUtils.EMPTY);

            return parameterMap;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * coc标签参数
     * @param documentLineId
     * @return
     * @throws ClientException
     */
    public Map<String, Object> getCOCPrintParameter(String documentLineId) throws ClientException{
        try {
            Map<String, Object> parameterMap = Maps.newHashMap();
            DocumentLine deliveryOrderLine = documentLineRepository.findByLineId(documentLineId);
            if (deliveryOrderLine == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, documentLineId);
            }
            String shippingDate = StringUtils.EMPTY;
            if (deliveryOrderLine.getShippingDate() != null){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                shippingDate = simpleDateFormat.format(deliveryOrderLine.getShippingDate());
            }

            parameterMap.put("partNumber", deliveryOrderLine.getReserved3());
            List<MaterialLot> materialLots = materialLotRepository.findByReserved44(deliveryOrderLine.getObjectRrn());
            materialLots = materialLots.stream().filter(materialLot -> materialLot.getCategory() == null).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(materialLots)){
                parameterMap.put("partNumber", materialLots.get(0).getReserved2() != null ? materialLots.get(0).getReserved2():deliveryOrderLine.getReserved3());
            }
            parameterMap.put("customer", deliveryOrderLine.getReserved15());
            parameterMap.put("poNumber", deliveryOrderLine.getReserved20());
            parameterMap.put("soNumber", deliveryOrderLine.getReserved19());
            parameterMap.put("invoiceNumber", deliveryOrderLine.getReserved21());
            parameterMap.put("quantity", deliveryOrderLine.getQty().toPlainString());
            parameterMap.put("shippingDate", shippingDate);

            return parameterMap;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 装箱清单数据
     * @param documentLineId
     * @return
     * @throws ClientException
     */
    public Map<String, Object> getPackingListPrintParameter(String documentLineId) throws ClientException{
        try {
            Map<String, Object> parameterMap = Maps.newHashMap();
            DocumentLine deliveryOrderLine = documentLineRepository.findByLineId(documentLineId);
            if (deliveryOrderLine == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, documentLineId);
            }
            String shippingDate = StringUtils.EMPTY;
            if (deliveryOrderLine.getShippingDate() != null){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                shippingDate = simpleDateFormat.format(deliveryOrderLine.getShippingDate());
            }
            List<MaterialLot> materialLots = materialLotRepository.findByReserved44(deliveryOrderLine.getObjectRrn());
            materialLots = materialLots.stream().filter(mlot-> (!StringUtils.isNullOrEmpty(mlot.getCategory()) && StringUtils.YES.equals(mlot.getCategory()))).collect(Collectors.toList());

            parameterMap.put("shipTo", deliveryOrderLine.getReserved15());
            parameterMap.put("shipDate", shippingDate);
            parameterMap.put("shipNo", deliveryOrderLine.getLineId());

            parameterMap.put("tel", deliveryOrderLine.getReserved18());
            parameterMap.put("attn", deliveryOrderLine.getReserved17());
            parameterMap.put("cNo", materialLots.size());

            return parameterMap;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 装箱单 物料信息
     * @param documentLineId
     * @return
     * @throws ClientException
     */
    public List<Map<String, Object>> getPackingListMLotParameter(String documentLineId) throws ClientException{
        try {
            List<Map<String, Object>> parameterList = Lists.newArrayList();

            DocumentLine deliveryOrderLine = documentLineRepository.findByLineId(documentLineId);
            if (deliveryOrderLine == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, documentLineId);
            }
            List<MaterialLot> materialLots = getMLotByLineObjectRrn(deliveryOrderLine.getObjectRrn());

            List<MaterialLot> boxMaterialLots = Lists.newArrayList();
            materialLots.forEach(mlot ->{
                if (mlot.getCategory() != null && mlot.getCategory().equals(StringUtils.YES)) {
                    boxMaterialLots.add(mlot);
                }
            });

            List<MaterialLot> reelCodeMLots = materialLots.stream().filter(mlot -> mlot.getBoxMaterialLotId() != null).collect(Collectors.toList());

            List<String> reelCodeMLotIds = Lists.newArrayList();
            reelCodeMLots.forEach(reelCodeMLot->{
                reelCodeMLotIds.add(reelCodeMLot.getMaterialLotId());
            });

            List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotIdIn(reelCodeMLotIds);

            for (MaterialLot boxMLot : boxMaterialLots) {
                List<MaterialLot> reelCodeMLotByBoxMLots = reelCodeMLots.stream().filter(mlot -> mlot.getBoxMaterialLotId().equals(boxMLot.getMaterialLotId())).collect(Collectors.toList());

                reelCodeMLotByBoxMLots.forEach(reelCodeMLot ->{

                    List<MaterialLotUnit> mLotUnitByReelCodeMLot = materialLotUnits.stream().filter(unitMLot -> reelCodeMLot.getMaterialLotId().equals(unitMLot.getMaterialLotId())).collect(Collectors.toList());
                    mLotUnitByReelCodeMLot.forEach(materialLotUnit -> {
                        Map<String, Object> parameterMap = Maps.newHashMap();
                        parameterMap.put("ctnNo", boxMLot.getMaterialLotId());
                        parameterMap.put("reelCode", reelCodeMLot.getMaterialLotId());
                        parameterMap.put("lotNo", materialLotUnit.getUnitId());

                        parameterMap.put("partNumber", reelCodeMLot.getReserved2());
                        parameterMap.put("qty", materialLotUnit.getQty().toPlainString());
                        parameterMap.put("poNo", reelCodeMLot.getReserved6());
                        parameterMap.put("dc", materialLotUnit.getReserved2());

                        parameterMap.put("cartonSize", boxMLot.getReserved10());
                        parameterMap.put("cartonQTY", boxMLot.getCurrentQty());
                        parameterMap.put("nW", boxMLot.getReserved12());
                        parameterMap.put("gW", boxMLot.getReserved13());

                        parameterList.add(parameterMap);
                    });
                });
            }
            return parameterList;
        }catch (Exception e){
            throw  ExceptionManager.handleException(e,log);
        }
    }

    /**
     * 成品库出货清单数据
     * @param documentLineId
     * @return
     * @throws ClientException
     */
    public Map<String, Object> getShippingListPrintParameter(String documentLineId) throws ClientException{
        try {
            Map<String, Object> parameterMap = Maps.newHashMap();
            DocumentLine deliveryOrderLine = documentLineRepository.findByLineId(documentLineId);
            if (deliveryOrderLine == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, documentLineId);
            }
            List<MaterialLot> materialLotList = getMLotByLineObjectRrn(deliveryOrderLine.getObjectRrn());

            List<MaterialLot> boxMaterialLots = Lists.newArrayList();
            materialLotList.forEach(mlot->{
                mlot.validateMLotHold();
                if (mlot.getCategory() != null && mlot.getCategory().equals(StringUtils.YES)) {
                    boxMaterialLots.add(mlot);
                }
            });
            BigDecimal totalBoxQty = boxMaterialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));

            String shippingDate = StringUtils.EMPTY;
            if (deliveryOrderLine.getShippingDate() != null){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
                shippingDate = simpleDateFormat.format(deliveryOrderLine.getShippingDate());
            }

            parameterMap.put("deliveryOrderLineId", deliveryOrderLine.getLineId());
            parameterMap.put("shipFrom", deliveryOrderLine.getReserved12());
            parameterMap.put("shipTo", deliveryOrderLine.getReserved15());
            parameterMap.put("shippingDate", shippingDate);
            parameterMap.put("contact", deliveryOrderLine.getReserved17());
            parameterMap.put("tel", deliveryOrderLine.getReserved18());
            parameterMap.put("totalBoxIdQty", boxMaterialLots.size()+StringUtils.EMPTY);
            parameterMap.put("totalBoxQty", totalBoxQty+"");

            return parameterMap;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

     /**
     * 成品出货 批次信息
     * @param documentLineId
     * @return
     * @throws ClientException
     */
    public List<Map<String, Object>> getShippingListPrintMLotParameter(String documentLineId) throws ClientException{
        try {
            DocumentLine deliveryOrderLine = documentLineRepository.findByLineId(documentLineId);
            if (deliveryOrderLine == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, documentLineId);
            }

            List<MaterialLot> materialLotList = getMLotByLineObjectRrn(deliveryOrderLine.getObjectRrn());

            List<Map<String, Object>> parameterList = Lists.newArrayList();

            List<MaterialLot> boxMaterialLots = Lists.newArrayList();
            materialLotList.forEach(mlot->{
                if (mlot.getCategory() != null && mlot.getCategory().equals(StringUtils.YES)) {
                    boxMaterialLots.add(mlot);
                }
            });
            boxMaterialLots.forEach(boxMaterialLot->{
                Map<String, Object> boxParameterMap = Maps.newHashMap();
                String boxId = boxMaterialLot.getMaterialLotId();

                boxParameterMap.put("boxId", boxId);
                boxParameterMap.put("materialName", boxMaterialLot.getMaterialName());
                boxParameterMap.put("qty", boxMaterialLot.getCurrentQty().toPlainString());

                String remark = boxId.substring(0, boxId.indexOf(StringUtils.SPLIT_CODE));
                boxParameterMap.put("remark", remark);
                parameterList.add(boxParameterMap);
            });

            return parameterList;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 配料单数据
     * @param documentLineId
     * @return
     * @throws ClientException
     */
    public Map<String, Object> getPKListParameter(String documentLineId) throws ClientException{
        try {
            Map<String, Object> parameterMap = Maps.newHashMap();
            DocumentLine deliveryOrderLine = documentLineRepository.findByLineId(documentLineId);
            if (deliveryOrderLine == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, documentLineId);
            }
            //第一次打印时生成一个备货单流水号
            if(StringUtils.isNullOrEmpty(deliveryOrderLine.getReserved23())){
                String reservedOrderId = documentService.generatorDocId(GENERATOR_RESERVED_ORDER_ID);
                deliveryOrderLine.setReserved23(reservedOrderId);
                deliveryOrderLine = (DocumentLine) baseService.saveEntity(deliveryOrderLine);
            }

            parameterMap.put("lineId",deliveryOrderLine.getLineId());
            parameterMap.put("pKId", deliveryOrderLine.getReserved23());
            parameterMap.put("customerVersion",deliveryOrderLine.getReserved3());
            parameterMap.put("customerCode", deliveryOrderLine.getReserved2());
            parameterMap.put("packageType", "");
            parameterMap.put("materialName", deliveryOrderLine.getMaterialName());
            return parameterMap;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 配料单 批次信息
     * @param documentLineId
     * @return
     * @throws ClientException
     */
    public List<Map<String, Object>> getPKListMLotParameter(String documentLineId) throws ClientException{
        try {
            List<Map<String, Object>> parameterList = Lists.newArrayList();
            DocumentLine deliveryOrderLine = documentLineRepository.findByLineId(documentLineId);
            if (deliveryOrderLine == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, documentLineId);
            }
            List<MaterialLot> materialLots = materialLotRepository.findByReserved44(deliveryOrderLine.getObjectRrn());

            materialLots = materialLots.stream().filter(mlot->StringUtils.isNullOrEmpty(mlot.getPackageType())).collect(Collectors.toList());

            List<String> reelCodeMLotIds = Lists.newArrayList();
            materialLots.forEach(reelCodeMLot->{
                reelCodeMLotIds.add(reelCodeMLot.getMaterialLotId());
            });

            List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotIdIn(reelCodeMLotIds);

            for (MaterialLotUnit unit : materialLotUnits) {
                Map<String, Object> parameterMap = Maps.newHashMap();
                List<MaterialLot> materialLotList = materialLots.stream().filter(materialLot -> materialLot.getMaterialLotId().equals(unit.getMaterialLotId())).collect(Collectors.toList());

                parameterMap.put("storageId", materialLotList.get(0).getLastStorageId());
                parameterMap.put("materialLotId",unit.getMaterialLotId());
                parameterMap.put("unitId", unit.getUnitId());
                parameterMap.put("controlLot", unit.getReserved4());
                parameterMap.put("qty",unit.getQty());
                parameterMap.put("dc",unit.getReserved2());

                parameterList.add(parameterMap);
            }

            return parameterList;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 装箱检验Pass
     * use package -->PackCheck OK
     * @param materialLots
     * @throws ClientException
     */
    public void packCheckPass(List<MaterialLot> materialLots) throws ClientException{
        try {
            MaterialLot boxMaterialLot = mmsService.getMLotByMLotId(materialLots.get(0).getBoxMaterialLotId());
            MaterialLot materialLot = mmsService.changeMaterialLotState(boxMaterialLot, MaterialStatus.STATUS_PACK_CHECK, MaterialStatus.STATUS_OK);
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_PACK_CKECK);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 装箱检验NG 直接hold
     * use package -->PackCheck NG
     * @param materialLotAction
     * @throws ClientException
     */
    public void packCheckNg(MaterialLotAction materialLotAction) throws ClientException{
        try {
            List<MaterialLotAction> materialLotActions = Lists.newArrayList();
            materialLotActions.add(materialLotAction);
            mmsService.holdMaterialLot(materialLotActions);

            MaterialLot boxMaterialLot = mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId());
            MaterialLot materialLot = mmsService.changeMaterialLotState(boxMaterialLot, MaterialStatus.STATUS_PACK_CHECK, MaterialStatus.STATUS_NG);
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_PACK_CKECK);

        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 发送邮件 备货数量不足时
     * @param documentLine
     * @param materialLotActionList
     * @throws ClientException
     */
    public void reservedSendMail(DocumentLine documentLine, List<MaterialLotAction> materialLotActionList) throws ClientException{
        String docLineId = documentLine.getLineId();

        List<String> reelCodeIds = materialLotActionList.stream().map(action -> action.getMaterialLotId()).collect(Collectors.toList());
        String reelCodeIdStr = reelCodeIds.toString();

        String username = ThreadLocalContext.getUsername();
        NBUser nbUser = userRepository.findByUsername(username);

        List<String> toUserList = Lists.newArrayList();
        //toUserList.add(nbUser.getEmail());
        toUserList.add("1943896827@qq.com");
        Map<String, Object> parameterMap = Maps.newHashMap();
        parameterMap.put("documentId", docLineId);
        parameterMap.put("reelNo", reelCodeIdStr);
       // mailService.sendTemplateMessage(toUserList, "WMS-Release", "reserved_Release", parameterMap);
    }

    /**
     *手持端 入库
     * @param materialLotAction 批次号，库位号
     * @return
     * @throws ClientException
     */
    public MaterialLot stockInMLotMobile(MaterialLotAction materialLotAction)throws ClientException{
        try {
            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true);
            Storage storage = storageRepository.findOneByName(materialLotAction.getTargetStorageId());
            Warehouse warehouse = warehouseRepository.findByObjectRrn(storage.getWarehouseRrn());

            materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
            materialLotAction.setTransQty(materialLot.getCurrentQty());
            materialLot = mmsService.stockIn(materialLot, materialLotAction);
            return materialLot;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 手持端 出库
     * @param materialLotAction 批次号,库位号
     * @return
     * @throws ClientException
     */
    public MaterialLot stockOutMLotMobile(MaterialLotAction materialLotAction)throws ClientException{
        try {
            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true);
            Storage storage = storageRepository.findOneByName(materialLotAction.getFromStorageId());
            Warehouse warehouse = warehouseRepository.findByObjectRrn(storage.getWarehouseRrn());

            if (!materialLot.getLastStorageId().equals(materialLotAction.getFromStorageId()) || !materialLot.getLastWarehouseId().equals(warehouse.getName())){
                throw new ClientParameterException(VanchipExceptions.WAREHOUSE_OR_STORAGE_ERROR);
            }

            materialLotAction.setTransQty(materialLot.getCurrentQty());
            materialLotAction.setFromWarehouseRrn(materialLot.getLastWarehouseRrn());
            materialLotAction.setFromStorageRrn(materialLot.getLastStorageRrn());
            materialLot = mmsService.pick(materialLot, materialLotAction);
            return materialLot;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 手持端 查询待装箱的批次
     * @param materialLotAction
     * @return
     * @throws ClientException
     */
    public MaterialLot queryPackageMLotMobile(MaterialLotAction materialLotAction) throws ClientException{
        MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true);
        if (!StringUtils.isNullOrEmpty(materialLot.getReserved44()) && StringUtils.isNullOrEmpty(materialLot.getBoxMaterialLotId()) && StringUtils.isNullOrEmpty(materialLot.getCategory())){
            return materialLot;
        }
        return null;
    }

    /**
     * 手持端 发货
     * @param documentId 发货单
     * @param materialLotAction 批次号
     * @throws ClientException
     */
    public void shipOutMobile(String documentId ,MaterialLotAction materialLotAction) throws ClientException{
        try {
            DocumentLine documentLine =documentLineRepository.findByLineId(documentId);
            List<MaterialLotAction> materialLotActions = Lists.newArrayList();
            materialLotActions.add(materialLotAction);

            stockOut(documentLine, materialLotActions);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 手持端 盘点
     * @param materialLotAction 批次号,数量，来源仓库号,来源库位号
     * @return
     * @throws ClientException
     */
    public MaterialLotInventory checkMlotInventoryMobile(MaterialLotAction materialLotAction) throws ClientException{
        try {
            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true);

            Warehouse warehouse = warehouseRepository.findOneByName(materialLotAction.getFromWarehouseId());
            Storage storage = storageRepository.findOneByName(materialLotAction.getFromStorageId());

            materialLotAction.setFromWarehouseRrn(warehouse.getObjectRrn());
            materialLotAction.setFromStorageRrn(storage.getObjectRrn());
            MaterialLotInventory materialLotInventory = mmsService.checkMaterialInventory(materialLot, materialLotAction);
            return materialLotInventory;
        }catch (Exception e){
            throw ExceptionManager.handleException(e,log);
        }
    }

    /**
     * VanChip同步MES的产品型号信息
     * @throws ClientException
     */
    public void asyncMesProduct() throws ClientException{
        try {


        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存成品物料
     * @param product
     * @return
     * @throws ClientException
     */
    public Product saveProduct(Product product) throws ClientException{
        try {
            product = (Product) conversionMaterialMode(product);

            product = mmsService.saveProduct(product, product.getWarehouseName());
           return product;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存源物料
     * @param rawMaterial
     * @return
     * @throws ClientException
     */
    public RawMaterial saveRawMaterial(RawMaterial rawMaterial) throws ClientException{
        try {
            rawMaterial = (RawMaterial) conversionMaterialMode(rawMaterial);

            rawMaterial = mmsService.saveRawMaterial(rawMaterial, rawMaterial.getWarehouseName(), rawMaterial.getIqcSheetName());
            return rawMaterial;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 转换物料分类
     * @param material
     * @return
     */
    public Material conversionMaterialMode(Material material) throws ClientException{
        try {
            String materialCategory =material.getMaterialCategory();
            String materialType = material.getMaterialType();
            String materialClassify = material.getReserved3();

            List<MaterialModelConversion> materialModelConversions = materialModelConversionRepository.findByMaterialCategoryAndMaterialTypeAndMaterialClassify(materialCategory, materialType, materialClassify);
            if (!StringUtils.isNullOrEmpty(materialType) && StringUtils.isNullOrEmpty(materialClassify)){

                materialModelConversions = materialModelConversionRepository.findByMaterialCategoryAndMaterialType(materialCategory, materialType);
            }else if (!StringUtils.isNullOrEmpty(materialClassify) && StringUtils.isNullOrEmpty(materialType)){

                materialModelConversions = materialModelConversionRepository.findByMaterialCategoryAndMaterialClassify(materialCategory, materialClassify);
            }
            if (CollectionUtils.isEmpty(materialModelConversions)){
                throw new ClientParameterException(VanchipExceptions.CONVERSION_MATERIAL_MODEL_IS_NOT_EXIST, material.getName());
            }
            material.setMaterialCategory(materialModelConversions.get(0).getConversionMaterialCategory());
            material.setMaterialType(materialModelConversions.get(0).getConversionMaterialType());
            material.setReserved3(materialModelConversions.get(0).getConversionMaterialClassify());
            return material ;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存实验室物料
     * @param labMaterial
     * @return
     * @throws ClientException
     */
    public LabMaterial saveLabMaterial(LabMaterial labMaterial)throws ClientException{
        try {
            if (!StringUtils.isNullOrEmpty(labMaterial.getWarehouseName())){
                Warehouse warehouse = mmsService.getWarehouseByName(labMaterial.getWarehouseName(), true);
                labMaterial.setWarehouseRrn(warehouse.getObjectRrn());
            }
            if (!StringUtils.isNullOrEmpty(labMaterial.getIqcSheetName())){
                IqcCheckSheet iqcCheckSheet = mmsService.getIqcSheetByName(labMaterial.getIqcSheetName(), true);
                labMaterial.setIqcSheetRrn(iqcCheckSheet.getObjectRrn());
            }

            if (labMaterial.getObjectRrn() == null) {
                LabMaterial material = labMaterialRepository.findOneByName(labMaterial.getName());
                if (material != null){
                    throw new ClientParameterException(MmsException.MM_LAB_MATERIAL_IS_EXIST);
                }

                labMaterial.setActiveTime(new Date());
                labMaterial.setActiveUser(ThreadLocalContext.getUsername());
                labMaterial.setStatus(DefaultStatusMachine.STATUS_ACTIVE);
                Long version = versionControlService.getNextVersion(labMaterial);
                labMaterial.setVersion(version);
                labMaterial = (LabMaterial)baseService.saveEntity(labMaterial, NBVersionControlHis.TRANS_TYPE_CREATE_AND_ACTIVE);
            } else {
                NBVersionControl oldData = labMaterialRepository.findByObjectRrn(labMaterial.getObjectRrn());
                labMaterial.setStatus(oldData.getStatus());
                labMaterial = (LabMaterial)baseService.saveEntity(labMaterial);
            }
            return labMaterial;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }



    /**
     * 保存库位信息
     * @param storage
     * @throws ClientException
     */
    public Storage saveStorageInfo(Storage storage) throws ClientException{
        try {
            String warehouseName = storage.getWarehouseName();
            if (!StringUtils.isNullOrEmpty(warehouseName)){
                Warehouse warehouse = warehouseRepository.findOneByName(warehouseName);
                if (warehouse == null){
                    throw new ClientParameterException(VanchipExceptions.WAREHOUSE_NAME_IS_NOT_EXIST, warehouseName);
                }
                storage.setWarehouseRrn(warehouse.getObjectRrn());
            }
            storage = (Storage)baseService.saveEntity(storage);
            return storage;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

}
