package com.newbiest.vanchip.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.annotation.BaseJpaFilter;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBQuery;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.model.NBVersionControlHis;
import com.newbiest.base.repository.QueryRepository;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.service.VersionControlService;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.*;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.main.MailService;
import com.newbiest.mms.application.event.DepartmentIssueMLotApplicationEvent;
import com.newbiest.mms.application.event.HoldMLotApplicationEvent;
import com.newbiest.mms.application.event.SplitMLotApplicationEvent;
import com.newbiest.mms.application.event.StockInApplicationEvent;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.DocumentException;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.DocumentService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.mms.service.PrintService;
import com.newbiest.mms.service.impl.DocumentServiceImpl;
import com.newbiest.mms.state.model.MaterialEvent;
import com.newbiest.mms.state.model.MaterialStatus;
import com.newbiest.mms.state.model.MaterialStatusCategory;
import com.newbiest.mms.state.model.MaterialStatusModel;
import com.newbiest.security.repository.UserRepository;
import com.newbiest.ui.model.NBOwnerReferenceList;
import com.newbiest.ui.model.NBReferenceList;
import com.newbiest.vanchip.dto.erp.backhaul.issue.IssueOrReturnRequestItem;
import com.newbiest.vanchip.dto.erp.delivery.DeliveryStatus;
import com.newbiest.vanchip.dto.print.model.*;
import com.newbiest.vanchip.exception.VanchipExceptions;
import com.newbiest.vanchip.model.MLotDocRule;
import com.newbiest.vanchip.model.MLotDocRuleContext;
import com.newbiest.vanchip.model.MaterialModelConversion;
import com.newbiest.vanchip.repository.MLotDocRuleLineRepository;
import com.newbiest.vanchip.repository.MLotDocRuleRepository;
import com.newbiest.vanchip.repository.MaterialModelConversionRepository;
import com.newbiest.vanchip.service.ErpService;
import com.newbiest.vanchip.service.MesService;
import com.newbiest.vanchip.service.VanChipService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.newbiest.mms.exception.DocumentException.*;
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

    //修改生产日期
    public static final String TRANS_TYPE_UPDATE_PRODUCTION_DATE = "UpdatePD";

    //修改销售订单等信息
    public static final String TRANS_TYPE_UPDATE_SO = "UpdateSo";

    //批次前三位是'RA-'
    public static final String PRE_RA_MLOT_ID = "RA-";

    //RA的标识
    public static final String INFERIOR_PRODUCTS_FLAG_R = "R";

    //物料名称包含'RA'
    public static final String MATERIAL_NAME_RA = "RA";

    //成品入库 Hold code
    public static final String PRE_HOLD = "Pre_Hold";
    public static final String S_HOLD = "S_Hold";
    public static final String P_HOLD = "P_Hold";
    public static final String Q_HOLD = "Q_Hold";
    public static final String E_HOLD = "E_Hold";
    public static final String O_MRB_HOLD = "O_MRB_Hold";
    public static final String A_HOLD = "A_Hold";

    //根据字符 进行不同的hold
    public static final String CUSTORDERID_S = "S";
    public static final String CUSTORDERID_P = "P";
    public static final String CUSTORDERID_Q = "Q";
    public static final String CUSTORDERID_E = "E";
    public static final String CUSTORDERID_A = "A";

    /**
     * 退料原因里是否需要Hold的关键
     */
    public static final String RETURN_HOLD_REASON = "质量问题";

    /**
     * 退料原因，此原因不需上报ERP
     */
    public static final String RETURN_GRADE_REASON = "转BIN退料";

    /**
     * 退料默认的HoldCode
     */
    public static final String RETURN_HOLD_CODE = "TL_HOLD";

    public static final String GENERATOR_RESERVED_ORDER_ID = "CreateReservedOrderId" ;

    //最终客户 荣耀
    public static final String FINAL_CUSTOMER_RY = "RY" ;

    public static final String ERP_EFFECTIVE_LIFE_YEARS = "Y";
    public static final String ERP_EFFECTIVE_LIFE_WEEKS = "W";
    public static final String ERP_EFFECTIVE_LIFE_DAYS = "D";
    public static final String ERP_EFFECTIVE_LIFE_MONTHS = "M";

    //调拨MES的仓库代码
    private static final String TRANSFER_MES_WAREHOUSE = "X001";

    //盘点差异HOLD
    public static final String CHECK_HOLD_CODE = "CHECK_HOLD";

    //RMA自身原因
    public static final String RMA_TYPE_SELF_REASON = "ZTLR";

    //RMA非自身原因
    public static final String RMA_TYPE_NOT_SELF_REASON = "ZTR2";

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

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    QueryRepository queryRepository;

    @Autowired
    UIService uiService;

    @Autowired
    PrintService printService;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ErpService erpService;

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
                Material material = mmsService.getMaterialByName(materialName, true);

                MaterialStatusModel materialStatusModel = mmsService.getStatusModelByRrn(material.getStatusModelRrn());
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
                    if (Material.MATERIAL_CATEGORY_MAIN_MATERIAL.equals(material.getMaterialCategory())) {
                        if(StringUtils.isNullOrEmpty(materialLot.getReserved9())){
                            throw new ClientParameterException(VanchipExceptions.REQUIRED_FIELD_IS_EMPTY, "D/C");
                        }
                    }
                    if(!StringUtils.isNullOrEmpty(materialLot.getProductionDateValue())){
                        String productionDate = formats.format(simpleDateFormat.parse(materialLot.getProductionDateValue()));
                        materialLot.setProductionDate(formats.parse(productionDate));
                        materialLot.setProductionDateValue(StringUtils.EMPTY);
                    }
                    Map<String, Object> propMap = PropertyUtils.convertObj2Map(materialLot);
                    propMap.put("incomingDocRrn", incomingOrder.getObjectRrn());
                    propMap.put("incomingDocId", incomingOrder.getName());
                    MaterialLot mLot = mmsService.createMLot(material, materialStatusModel, materialLot.getMaterialLotId(), materialLot.getCurrentQty(), materialLot.getCurrentSubQty(), propMap);
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
            Document document = documentService.getDocumentByName(documentId, true);

            List<MaterialLot> materialLots = documentService.getReservedMLotByDocId(documentId);
            return materialLots;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *指定物料批次发料-发往MES
     * @param documentId
     * @param materialLotIdList
     * @throws ClientException
     */
    public void issueMLotByOrder(String documentId, List<String> materialLotIdList)throws ClientException{
        try {
            List<MaterialLot> stockTransferMLots = materialLotIdList.stream().map(materialLotId -> mmsService.getMLotByMLotId(materialLotId)).collect(Collectors.toList());
            List<MaterialLotAction>  materialLotActions = Lists.newArrayList();
            for (MaterialLot materialLot : stockTransferMLots) {
                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                materialLotAction.setTransQty(materialLot.getCurrentQty());
                materialLotAction.setTargetWarehouseId(TRANSFER_MES_WAREHOUSE);
                materialLotAction.setFromWarehouseId(materialLot.getLastWarehouseId());
                materialLotActions.add(materialLotAction);
            }

            Document document = documentService.getDocumentByName(documentId, true);
            if (document instanceof IssueLotOrder){
                issueMLotByDoc(documentId, materialLotIdList);
            } else if (document instanceof IssueMaterialOrder) {
                issueMaterialByDoc(documentId, materialLotIdList);
            } else if (document instanceof IssueFinishGoodOrder){
                issueFinishGoodByDoc(documentId, materialLotIdList);

                List<MaterialLotUnit> stockTransferMLotUnits = materialLotUnitRepository.findByMaterialLotIdIn(materialLotIdList);
                for (MaterialLot stockTransferMLot : stockTransferMLots) {
                    List<MaterialLotUnit> materialLotUnitList = stockTransferMLotUnits.stream().filter(mLotUnit -> mLotUnit.getMaterialLotId().equals(stockTransferMLot.getMaterialLotId())).collect(Collectors.toList());
                    stockTransferMLot.setMaterialLotUnits(materialLotUnitList);
                }
            } else {
                throw new ClientParameterException(DocumentException.DOCUMENT_CATEGORY_IS_NOT_EXIST, documentId);
            }
            //RA不报
            stockTransferMLots = stockTransferMLots.stream().filter(mLot -> !validateRaLot(mLot.getInferiorProductsFlag())).collect(Collectors.toList());
            //重测不报
            stockTransferMLots = stockTransferMLots.stream().filter(mLot -> !validateReTest(mLot.getRetestFlag())).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(stockTransferMLots)){
                //上报ERP走调拨接口。
                erpService.backhaulStockTransfer(stockTransferMLots, materialLotActions);
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
     * 退料 退到仓库
     * @param documentId
     * @param materialLotIdList
     * @throws ClientException
     */
    public void returnMLotByDoc(String documentId, List<String> materialLotIdList) throws ClientException {
        try {
            documentService.returnMLotByDoc(documentId, materialLotIdList, DocumentServiceImpl.RETURN_WAREHOUSE);

            // 如果是质量问题导致的退料需要进行HOLD处理
            List<MaterialLot> materialLots = materialLotIdList.stream().map(materialLotId -> mmsService.getMLotByMLotId(materialLotId, true)).collect(Collectors.toList());
            List<MaterialLotAction> materialLotActions = Lists.newArrayList();
            for (MaterialLot materialLot : materialLots) {
                if (!StringUtils.isNullOrEmpty(materialLot.getReturnReason()) && materialLot.getReturnReason().contains(RETURN_HOLD_REASON)) {
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
     * 根据标准数量获得待备货批次
     * @param documentLine
     * @param standardQty
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getReservedMLotByStandardQty(DocumentLine documentLine, BigDecimal standardQty) throws ClientException{
        try {
            List<MaterialLot> standardQtyMLot = Lists.newArrayList();

            DocumentLine docLine = documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());

            List<MaterialLot> waitReservedMLot = getReservedMaterialLot(docLine);
            waitReservedMLot = waitReservedMLot.stream().filter(mLot -> MaterialLot.HOLD_STATE_OFF.equals(mLot.getHoldState())).collect(Collectors.toList());
            BigDecimal unReservedQty = docLine.getUnReservedQty();
            for (MaterialLot materialLot : waitReservedMLot) {
                if (materialLot.getCurrentQty().compareTo(standardQty) == 0){
                    standardQtyMLot.add(materialLot);
                }

                unReservedQty = unReservedQty.subtract(materialLot.getCurrentQty());
                if (unReservedQty.compareTo(standardQty) < 0){
                    break;
                }
            }

            return standardQtyMLot;
        }catch (Exception e){
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

            //根据接收时间排序
            materialLots = materialLots.stream().sorted(Comparator.comparing(MaterialLot::getReceiveDate)).collect(Collectors.toList());
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
                BigDecimal currentQty = materialLot.getCurrentQty();
                transQty = transQty.add(currentQty);
                if (unReservedQty.compareTo(transQty) < 0) {
                    throw new ClientParameterException(VanchipExceptions.RESERVED_OVER_QTY,materialLot.getMaterialLotId());
                }
                //将发货单据绑定到批次上
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
            //根据docLineId 分类处理
            Map<String, List<MaterialLot>> docLineReservedMaterialLotMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getReserved45));

            for (String docLineId :docLineReservedMaterialLotMap.keySet()){
                DocumentLine docLine = documentLineRepository.findByLineId(docLineId);
                List<MaterialLot> materialLotList = docLineReservedMaterialLotMap.get(docLineId);
                materialLotList.forEach(materialLot -> {
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

    public List<MaterialLot> getWaitShipMLotByDocLineId(String docLineId) throws ClientException{
        try {
            return materialLotRepository.findByReserved45AndCategory(docLineId, StringUtils.YES);
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 成品全部hold,客户订单号信息 hold,mrb信息 Hold
     * @param materialLot
     */
    public MaterialLot autoHoldFinishGood(MaterialLot materialLot) throws ClientException{
        try {
            Product product = mmsService.getProductByName(materialLot.getMaterialName());
            if (product == null){
                throw new ClientParameterException(MmsException.MM_MATERIAL_IS_NOT_EXIST, materialLot.getMaterialName());
            }

            if (MaterialLot.HOLD_STATE_OFF.equals(materialLot.getHoldState())) {
                materialLot.setHoldState(MaterialLot.HOLD_STATE_ON);
                materialLot = materialLotRepository.saveAndFlush(materialLot);
            }
            //根据测试批次hold
            List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
            for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                List<MaterialLotAction> materialLotActions = getCustomerOrderHoldCodeAction(materialLotUnit.getReserved1());

                MaterialLotAction preHoldAction = new MaterialLotAction();
                preHoldAction.setActionCode(PRE_HOLD);
                materialLotActions.add(preHoldAction);

                String mrb = materialLotUnit.getReserved5();
                if (!StringUtils.isNullOrEmpty(mrb)){
                    MaterialLotAction action = new MaterialLotAction();
                    action.setActionCode(O_MRB_HOLD);
                    materialLotActions.add(action);
                }

                for (MaterialLotAction holdAction: materialLotActions){
                    MaterialLotHold materialLotHold = new MaterialLotHold();
                    materialLotHold.setMaterialLot(materialLot).setAction(holdAction);
                    materialLotHold.setUnitId(materialLotUnit.getUnitId());
                    materialLotHold.setUnitRrn(materialLotUnit.getObjectRrn());
                    mmsService.saveMaterialLotHold(materialLotHold);
                    baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_HOLD, holdAction);
                }
            }
            return materialLot;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public List<MaterialLotAction> getCustomerOrderHoldCodeAction(String customerOrderId) throws ClientException{
        try {
            List<MaterialLotAction> materialLotActions = Lists.newArrayList();

            String firstCustOrderId = customerOrderId.substring(0, 1);
            String secondCustOrderId = customerOrderId.substring(1, 2);
            String thirdCustOrderId = customerOrderId.substring(2, 3);

            MaterialLotAction mLotAction = new MaterialLotAction();
            //根据客户订单第一位hold
            switch (firstCustOrderId){
                case CUSTORDERID_P :
                    mLotAction.setActionCode(P_HOLD);
                    materialLotActions.add(mLotAction);
                    break;
                case CUSTORDERID_Q :
                    mLotAction.setActionCode(Q_HOLD);
                    materialLotActions.add(mLotAction);
                    break;
                default : break;
            }

            //根据客户订单第二位hold
            if (CUSTORDERID_S.equals(secondCustOrderId)){
                MaterialLotAction action = new MaterialLotAction();
                action.setActionCode(S_HOLD);
                materialLotActions.add(action);
            }

            //根据客户订单第三位hold
            if (CUSTORDERID_E.equals(thirdCustOrderId)){
                MaterialLotAction action = new MaterialLotAction();
                action.setActionCode(E_HOLD);
                materialLotActions.add(action);
            }else if (CUSTORDERID_A.equals(thirdCustOrderId)){
                MaterialLotAction action = new MaterialLotAction();
                action.setActionCode(A_HOLD);
                materialLotActions.add(action);
            }

            return materialLotActions;
        }catch (Exception  e){
            throw ExceptionManager.handleException(e, log);
        }
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
     * 相同来料单必须全部接受完才能IQC
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
     * @param materialLotActions
     * @param urlRemark 文件链接
     * @throws ClientException
     */
    public void batchIqc(List<MaterialLotAction> materialLotActions, String urlRemark, List<MLotCheckSheetLine> mLotCheckSheetLines) throws ClientException{
        try {
            for (MaterialLotAction materialLotAction : materialLotActions) {
                mmsService.iqc(materialLotAction, urlRemark, mLotCheckSheetLines);
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
            SimpleDateFormat formatter = new SimpleDateFormat( "yyyy/MM/dd"+StringUtils.BLANK_SPACE+DateUtils.DEFAULT_TIME_PATTERN);

            List<MaterialLotUnit> materialLotUnitList = Lists.newArrayList();
            materialLots.forEach(mLot ->  materialLotUnitList.addAll(mLot.getMaterialLotUnits()));
            List<String> controlLotList = materialLotUnitList.stream().map(mLotUnit -> mLotUnit.getReserved4()).collect(Collectors.toList());
            List<MaterialLot> incomingMLotByControlLotList = materialLotRepository.findByReserved4In(controlLotList);

            Map<String, List<MaterialLot>> materialLotMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot::getMaterialName));
            for (String materialName : materialLotMap.keySet()) {
                Product product = productRepository.findOneByName(materialName);
                if (product == null) {
                    throw new ClientParameterException(MmsException.MM_PRODUCT_IS_NOT_EXIST, materialName);
                }
                MaterialStatusModel materialStatusModel = mmsService.getStatusModelByRrn(product.getStatusModelRrn());

                List<MaterialLot> materialLotList = materialLotMap.get(materialName);
                for (MaterialLot materialLot : materialLotList) {

                    documentService.createDocumentMLot(finishGoodOrder.getName(), materialLot.getMaterialLotId());

                    List<MaterialLotUnit> materialLotUnits = materialLot.getMaterialLotUnits();
                    List<String> outMrbs = materialLotUnits.stream().map(materialLotUnit -> materialLotUnit.getReserved5()).distinct().collect(Collectors.toList());
                    List<String> inMrbs = materialLotUnits.stream().map(materialLotUnit -> materialLotUnit.getReserved6()).distinct().collect(Collectors.toList());
                    String outMrb = StringUtils.join(outMrbs, "/");
                    String inMrb = StringUtils.join(inMrbs, "/");

                    //MES 传过来的净重单位是mg, wms需要单位是kg
                    String netWeightStr = materialLot.getReserved12();
                    BigDecimal netWeight = BigDecimal.ZERO;
                    if (!StringUtils.isNullOrEmpty(netWeightStr)){
                        netWeight = new BigDecimal(netWeightStr);
                        BigDecimal bigDecimal = new BigDecimal(1000000);
                        netWeight = netWeight.divide(bigDecimal);
                    }

                    Map<String, Object> materialLotParaMap = Maps.newHashMap();
                    materialLotParaMap.put("grade", materialLot.getGrade());
                    materialLotParaMap.put("reserved2", materialLot.getReserved2());
                    materialLotParaMap.put("reserved3", materialLot.getReserved3());
                    materialLotParaMap.put("reserved4", materialLot.getReserved4());
                    materialLotParaMap.put("reserved6", materialLot.getReserved6());
                    materialLotParaMap.put("reserved7", materialLot.getReserved7());
                    materialLotParaMap.put("reserved8", materialLot.getReserved8());
                    materialLotParaMap.put("reserved9", materialLot.getReserved9());
                    materialLotParaMap.put("reserved12", netWeight);
                    materialLotParaMap.put("reserved16", outMrb);
                    materialLotParaMap.put("reserved47", materialLot.getReserved47());
                    materialLotParaMap.put("reserved52", inMrb);
                    materialLotParaMap.put("reserved53", materialLot.getReserved53());
                    materialLotParaMap.put("reserved54", materialLot.getReserved8());
                    materialLotParaMap.put("reserved57", materialLot.getReserved57());
                    materialLotParaMap.put("reserved58", materialLot.getReserved58());
                    materialLotParaMap.put("inferiorProductsFlag", StringUtils.isNullOrEmpty(materialLot.getInferiorProductsFlag()) ? StringUtils.NO : materialLot.getInferiorProductsFlag());
                    materialLotParaMap.put("retestFlag", materialLot.getRetestFlag());

                    if (!StringUtils.isNullOrEmpty(materialLot.getIclDate())){
                        Date productionDate = formatter.parse(materialLot.getIclDate());
                        materialLotParaMap.put("productionDate", productionDate);
                    }
                    materialLotParaMap.put("incomingQty", materialLot.getCurrentQty());

                    MaterialLot mLot = mmsService.createMLot(product, materialStatusModel, materialLot.getMaterialLotId(), materialLot.getCurrentQty(), BigDecimal.ZERO, materialLotParaMap);

                    if(CollectionUtils.isNotEmpty(materialLotUnits)){
                        materialLotUnits.forEach(materialLotUnit -> {
                            Map<String, Object> materialLotUnitParaMap = Maps.newHashMap();
                            materialLotUnitParaMap.put("unitId", materialLotUnit.getUnitId());
                            materialLotUnitParaMap.put("qty", materialLotUnit.getQty());
                            materialLotUnitParaMap.put("workOrderId", materialLotUnit.getWorkOrderId());
                            materialLotUnitParaMap.put("grade", materialLotUnit.getGrade());
                            materialLotUnitParaMap.put("reserved1", materialLotUnit.getReserved1());
                            materialLotUnitParaMap.put("reserved2", materialLotUnit.getReserved2());
                            materialLotUnitParaMap.put("reserved3", materialLotUnit.getReserved3());
                            materialLotUnitParaMap.put("reserved4", materialLotUnit.getReserved4());
                            materialLotUnitParaMap.put("reserved5", materialLotUnit.getReserved5());
                            materialLotUnitParaMap.put("reserved6", materialLotUnit.getReserved6());

                            materialLotUnitParaMap.put("reserved7", materialLotUnit.getReserved7());
                            materialLotUnitParaMap.put("reserved8", materialLotUnit.getReserved8());
                            materialLotUnitParaMap.put("reserved9", materialLotUnit.getReserved9());
                            materialLotUnitParaMap.put("reserved10", materialLotUnit.getReserved10());
                            materialLotUnitParaMap.put("reserved11", materialLotUnit.getReserved11());
                            materialLotUnitParaMap.put("reserved12", materialLotUnit.getReserved12());
                            materialLotUnitParaMap.put("reserved21", materialLotUnit.getReserved21());
                            materialLotUnitParaMap.put("reserved22", materialLotUnit.getReserved22());

                            if (!StringUtils.isNullOrEmpty(materialLotUnit.getReserved4())) {
                                Optional<MaterialLot> materialLotOptional = incomingMLotByControlLotList.stream()
                                        .filter(incomingMLot -> incomingMLot.getReserved4().equals(materialLotUnit.getReserved4())
                                        && materialLotUnit.getWorkOrderId().equals(incomingMLot.getWorkOrderId())).findFirst();
                                if (materialLotOptional.isPresent()) {
                                    MaterialLot materialLotFirst = materialLotOptional.get();
                                    materialLotUnitParaMap.put("reserved13", materialLotFirst.getReserved3());
                                    materialLotUnitParaMap.put("reserved14", materialLotFirst.getReserved5());
                                    materialLotUnitParaMap.put("reserved15", materialLotFirst.getLetteringInfo());
                                    materialLotUnitParaMap.put("reserved16", materialLotFirst.getReserved62());
                                    materialLotUnitParaMap.put("reserved17", materialLotFirst.getReserved61());
                                    materialLotUnitParaMap.put("reserved19", materialLotFirst.getIncomingComment());
                                    materialLotUnitParaMap.put("reserved20", materialLotFirst.getReserved19());
                                }
                            }

                            saveMLotUnit(product, mLot, materialLotUnit.getQty(), materialLotUnitParaMap);
                        });
                    }
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
     * 成品以及次品接收
     * @param documentId
     * @param materialLotIdList
     * @throws ClientException
     */
    public void receiveFinishGood(String documentId, List<String> materialLotIdList) throws ClientException{
        try {
            receiveFinishGoodLot(documentId, materialLotIdList);

            documentService.changeDocMLotStatus(documentId, materialLotIdList, DocumentMLot.STATUS_RECEIVE);
            String materialLotId = materialLotIdList.get(0);
            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId, true);
            if (validateProductLot(materialLot.getInferiorProductsFlag())){
                //成品接收HOLD
                List<MaterialLot> materialLots = materialLotIdList.stream().map(mLotId -> mmsService.getMLotByMLotId(mLotId, true)).collect(Collectors.toList());
                for (MaterialLot mlot : materialLots) {
                    autoHoldFinishGood(mlot);
                }
            }
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
    public void receiveFinishGoodLot(String documentId, List<String> materialLotIdList) throws ClientException{
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
                Material product = mmsService.getProductByName(materialName);
                if(product == null){
                    throw new ClientParameterException(MmsException.MM_PRODUCT_IS_NOT_EXIST, materialName);
                }
                List<MaterialLot> materialLots = materialNameMap.get(materialName);
                for (MaterialLot materialLot : materialLots){
                    Date expireDate = mmsService.calculateTargetDate(materialLot.getProductionDate(), product.getEffectiveLife(), product.getEffectiveUnit());
                    materialLot.setExpireDate(expireDate);
                    MaterialLot mLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_RECEIVE, MaterialStatus.STATUS_RECEIVE);
                    baseService.saveHistoryEntity(mLot, MaterialLotHistory.TRANS_TYPE_RECEIVE);
                }
            }
        }catch (Exception e){
            ExceptionManager.handleException(e, log);
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
        int boxQty = boxMLots.size() + 1;
        StringBuffer boxId = new StringBuffer(boxQty + StringUtils.EMPTY);

        GeneratorContext generatorContext = new GeneratorContext();
        generatorContext.setObject(documentLine);
        generatorContext.setRuleName(generatorRule);
        String  ruleId= generatorService.generatorId(generatorContext);

        boxId.append(ruleId);

        String boxMLotId = boxId.toString();
        //形如：1-xxx210524-00001;2-xxx210524-00002
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

                packedMaterialLot.setReserved44(StringUtils.EMPTY);
                packedMaterialLot.setReserved45(StringUtils.EMPTY);
            } else {
                //恢复到装箱状态
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

            packedMaterialLot.setStatusCategory(MaterialStatusCategory.STATUS_CATEGORY_USE);
            packedMaterialLot.setStatus(MaterialStatus.STATUS_PACKAGE);
            packedMaterialLot.setCurrentQty(materialLotPackageType.getPackedQty(allMaterialLotAction));

            baseService.saveEntity(packedMaterialLot, MaterialLotHistory.TRANS_TYPE_ADDITIONAL_PACKAGE, firstMaterialAction);

            packageMaterialLots(packedMaterialLot, waitToAddPackingMLots, materialLotActions);
            return packedMaterialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * 发货
     * @param docLineId 发货单号，箱批次
     * @param materialLots
     */
    public void shipOut(String docLineId, List<MaterialLot> materialLots) throws ClientException{
        try {
            DocumentLine documentLine = documentLineRepository.findByLineId(docLineId);
            DeliveryOrder deliveryOrder = deliveryOrderRepository.findOneByName(documentLine.getDocId());
            if (deliveryOrder == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, documentLine.getLineId());
            }
            materialLots = materialLots.stream().map(materialLot -> mmsService.getMLotByMLotId(materialLot.getMaterialLotId(), true)).collect(Collectors.toList());
            List<MaterialLot> materialLotList = materialLots;

            MaterialLotAction action = new MaterialLotAction();
            BigDecimal handledQty = BigDecimal.ZERO;
            for (MaterialLot materialLot : materialLots) {
                handledQty = handledQty.add(materialLot.getCurrentQty());
                action.setMaterialLotId(materialLot.getMaterialLotId());
                action.setTransQty(materialLot.getCurrentQty());

                materialLot.setCurrentQty(BigDecimal.ZERO);
                materialLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_SHIP, StringUtils.EMPTY);
                baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_SHIP, action);

                List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                if(CollectionUtils.isNotEmpty(packageDetailLots)){
                    for (MaterialLot packageLot : packageDetailLots){
                        action.setMaterialLotId(materialLot.getMaterialLotId());
                        action.setTransQty(materialLot.getCurrentQty());

                        packageLot.setCurrentQty(BigDecimal.ZERO);
                        packageLot =  mmsService.changeMaterialLotState(packageLot, MaterialEvent.EVENT_SHIP, StringUtils.EMPTY);
                        baseService.saveHistoryEntity(packageLot, MaterialLotHistory.TRANS_TYPE_SHIP, action);
                    }
                }
            }

            documentLine.setUnHandledQty(documentLine.getUnHandledQty().subtract(handledQty));
            documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
            documentLine.setStatus("Ship");
            baseService.saveEntity(documentLine);

            deliveryOrder.setUnHandledQty(deliveryOrder.getUnHandledQty().subtract(handledQty));
            deliveryOrder.setHandledQty(deliveryOrder.getHandledQty().add(handledQty));
            baseService.saveEntity(deliveryOrder);

            erpService.backhaulDeliveryStatus(docLineId, documentLine, materialLotList, DeliveryStatus.POSTING_DELIVERY_STATUS, documentLine.getReserved8());
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
     * 称重 保存毛重和箱子尺寸并计算净重
     * @param materialLotId reel No
     * @param grossWeight 毛重
     * @return
     * @throws ClientException
     */
    public MaterialLot weightMaterialLot(String materialLotId, String grossWeight, String cartonSize) throws ClientException{
        try {
            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId, true);
            MaterialLot boxMaterialLot = mmsService.getMLotByMLotId(materialLot.getBoxMaterialLotId(), true);

            //箱净重 = ReelCode的newWeight 相加
            List<MaterialLot> materialLots = materialLotRepository.findByBoxMaterialLotId(boxMaterialLot.getMaterialLotId());
            BigDecimal boxNetWeight = BigDecimal.ZERO;
            for (MaterialLot reelMLot : materialLots) {
                BigDecimal netWeight = new BigDecimal(reelMLot.getReserved12());
                boxNetWeight = boxNetWeight.add(netWeight);
            }

            boxMaterialLot.setReserved12(boxNetWeight.toPlainString());
            boxMaterialLot.setReserved10(cartonSize);
            boxMaterialLot.setReserved13(grossWeight);
            boxMaterialLot = (MaterialLot)baseService.saveEntity(boxMaterialLot, MaterialLotHistory.TRANS_TYPE_WEIGHT);
            return boxMaterialLot;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 箱标签数据
     * @param materialLotId reel No
     * @return
     * @throws ClientException
     */
    public Map<String, Object> getBoxPrintParameter(String materialLotId) throws ClientException{
        try {
            Map<String, Object> parameterMap = Maps.newHashMap();

            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId,true);
            MaterialLot boxMaterialLot = mmsService.getMLotByMLotId(materialLot.getBoxMaterialLotId(),true);

            DocumentLine deliveryOrderLine = documentLineRepository.findByObjectRrn(boxMaterialLot.getReserved44());
            String shippingDate = StringUtils.EMPTY;
            if (deliveryOrderLine.getShippingDate() != null){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                shippingDate = simpleDateFormat.format(deliveryOrderLine.getShippingDate());
            }

            parameterMap.put("from", deliveryOrderLine.getReserved11());
            parameterMap.put("to", deliveryOrderLine.getReserved15());
            parameterMap.put("toAdd", deliveryOrderLine.getReserved16());
            parameterMap.put("deliveryNumber", deliveryOrderLine.getReserved21());
            parameterMap.put("shippingDate", shippingDate);
            parameterMap.put("poNumber", deliveryOrderLine.getReserved20());

            String boxMaterialLotId = boxMaterialLot.getMaterialLotId();
            String boxNumber = boxMaterialLotId.substring(0, boxMaterialLotId.indexOf(StringUtils.SPLIT_CODE));

            //该单据总的箱数,查询箱同一单据下已经装箱的
            List<MaterialLot> materialLots = materialLotRepository.findByReserved44(deliveryOrderLine.getObjectRrn());
            materialLots = materialLots.stream().filter(mlot-> (!StringUtils.isNullOrEmpty(mlot.getCategory()) && StringUtils.YES.equals(mlot.getCategory()))).collect(Collectors.toList());

            StringBuffer qRcode = new StringBuffer();
            qRcode.append(boxMaterialLot.getReserved2());
            qRcode.append(StringUtils.SPLIT_COMMA);
            qRcode.append(boxMaterialLot.getCurrentQty().toPlainString());
            List<MaterialLot> reelMLots = materialLotRepository.findByBoxMaterialLotId(boxMaterialLotId);

            for (MaterialLot reelMLot: reelMLots) {
                qRcode.append(StringUtils.SPLIT_COMMA);
                qRcode.append(reelMLot.getMaterialLotId());

                qRcode.append(StringUtils.SPLIT_COMMA);
                qRcode.append(reelMLot.getReserved9());
            }

            BigDecimal netWeight = new BigDecimal(boxMaterialLot.getReserved12()).setScale(3, BigDecimal.ROUND_HALF_UP);
            BigDecimal grossWeight = new BigDecimal(boxMaterialLot.getReserved13()).setScale(3, BigDecimal.ROUND_HALF_UP);

            BigDecimal totalBoxQty = new BigDecimal(materialLots.size());
            parameterMap.put("partNumber", boxMaterialLot.getReserved2());
            parameterMap.put("quantity", boxMaterialLot.getCurrentQty().toPlainString());
            parameterMap.put("boxNumber", boxNumber);
            parameterMap.put("totalBoxNumber", totalBoxQty);
            parameterMap.put("grossWeight", grossWeight.toPlainString());
            parameterMap.put("netWeight", netWeight.toPlainString());
            parameterMap.put("countOfOrigin", "China");
            parameterMap.put("boxId", boxMaterialLot.getMaterialLotId());
            parameterMap.put("qRCode", qRcode);
            parameterMap.put("printNumber", 1 + StringUtils.EMPTY);

            return parameterMap;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COC参数
     * @param documentLineId
     * @return
     * @throws ClientException
     */
    public CocPrintInfo getCOCPrintParameter(String documentLineId) throws ClientException{
        try {
            CocPrintInfo cocPrintInfo = new CocPrintInfo();
            DocumentLine deliveryOrderLine = documentLineRepository.findByLineId(documentLineId);
            if (deliveryOrderLine == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, documentLineId);
            }
            String shippingDate = StringUtils.EMPTY;
            if (deliveryOrderLine.getShippingDate() != null){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                shippingDate = simpleDateFormat.format(deliveryOrderLine.getShippingDate());
            }

            List<MaterialLot> materialLots = materialLotRepository.findByReserved44(deliveryOrderLine.getObjectRrn());
            materialLots = materialLots.stream().filter(materialLot -> materialLot.getCategory() == null).collect(Collectors.toList());

            cocPrintInfo.setCustomer(deliveryOrderLine.getReserved15());
            cocPrintInfo.setDocumentLineId(deliveryOrderLine.getLineId());
            cocPrintInfo.setPartNumber(materialLots.get(0).getReserved2());
            cocPrintInfo.setInvoiceNumber(deliveryOrderLine.getReserved21());
            cocPrintInfo.setQuantity(deliveryOrderLine.getQty().toPlainString());
            cocPrintInfo.setShippingDate(shippingDate);
            cocPrintInfo.setSoNumber(deliveryOrderLine.getReserved19());
            cocPrintInfo.setPoNumber(deliveryOrderLine.getReserved20());

            return cocPrintInfo;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 装箱单参数
     * @param documentLineId
     * @return
     * @throws ClientException
     */
    public PackingListPrintInfo getPackingListPrintParameter(String documentLineId) throws ClientException{
        try {
            DocumentLine deliveryOrderLine = documentLineRepository.findByLineId(documentLineId);
            if (deliveryOrderLine == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, documentLineId);
            }
            String shippingDate = StringUtils.EMPTY;
            if (deliveryOrderLine.getShippingDate() != null){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                shippingDate = simpleDateFormat.format(deliveryOrderLine.getShippingDate());
            }

            //获得reel以及包装批次
            List<MaterialLot> reelAndBoxMLots = materialLotRepository.findByReserved44(deliveryOrderLine.getObjectRrn());

            //获得reel批次
            List<MaterialLot> reelMaterialLots = reelAndBoxMLots.stream().filter(mlot-> (StringUtils.isNullOrEmpty(mlot.getCategory()))).collect(Collectors.toList());

            //获得包装批次
            List<MaterialLot> boxMaterialLots = reelAndBoxMLots.stream().filter(mlot-> (!StringUtils.isNullOrEmpty(mlot.getCategory()) && StringUtils.YES.equals(mlot.getCategory()))).collect(Collectors.toList());

            //获得unit
            List<String> reelCodeMLotIds = reelMaterialLots.stream().map(reelMLot -> reelMLot.getMaterialLotId()).collect(Collectors.toList());
            List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotIdIn(reelCodeMLotIds);

            PackingListPrintInfo packingListPrintInfo = new PackingListPrintInfo();
            List<PackingListBoxPrintInfo> packingListBoxPrintInfos = Lists.newArrayList();

            Map<String, List<MaterialLotUnit>> reelMap = materialLotUnits.stream().collect(Collectors.groupingBy(MaterialLotUnit::getMaterialLotId));
            for (MaterialLot reelMLot:reelMaterialLots){
                List<MaterialLotUnit> units = reelMap.get(reelMLot.getMaterialLotId());

                Map<String, List<MaterialLotUnit>> controlLotMap = units.stream().collect(Collectors.groupingBy(MaterialLotUnit::getReserved4));
                for (String controlLot : controlLotMap.keySet()) {
                    PackingListBoxPrintInfo packingListBoxPrintInfo = new PackingListBoxPrintInfo();
                    List<MaterialLotUnit> unitsBycontrolLot = controlLotMap.get(controlLot);
                    MaterialLotUnit materialLotUnit = unitsBycontrolLot.get(0);
                    BigDecimal qty = unitsBycontrolLot.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLotUnit::getQty));

                    //unit属性
                    packingListBoxPrintInfo.setPart_number(materialLotUnit.getReserved3());
                    packingListBoxPrintInfo.setReel_code(materialLotUnit.getMaterialLotId());
                    packingListBoxPrintInfo.setLot_no(materialLotUnit.getReserved4());
                    packingListBoxPrintInfo.setQty(qty.toPlainString());
                    packingListBoxPrintInfo.setPo_no(materialLotUnit.getReserved1());
                    packingListBoxPrintInfo.setDc(materialLotUnit.getReserved2());
                    packingListBoxPrintInfo.setVersionNumber(materialLotUnit.getReserved13());
                    packingListBoxPrintInfo.setControlLot(materialLotUnit.getReserved4());
                    packingListBoxPrintInfo.setContromerLotNo(materialLotUnit.getReserved14());
                    //外箱属性
                    List<MaterialLot> boxMLotsbyReel = boxMaterialLots.stream().filter(boxMLot -> boxMLot.getMaterialLotId().equals(reelMLot.getBoxMaterialLotId())).collect(Collectors.toList());
                    MaterialLot boxMLot = boxMLotsbyReel.get(0);

                    //保留三位小数，
                    BigDecimal netWeight = new BigDecimal(boxMLot.getReserved12()).setScale(3, BigDecimal.ROUND_HALF_UP);
                    BigDecimal grossWeight = new BigDecimal(boxMLot.getReserved13()).setScale(3, BigDecimal.ROUND_HALF_UP);

                    packingListBoxPrintInfo.setCtn_no(boxMLot.getMaterialLotId());
                    packingListBoxPrintInfo.setCarton_size(boxMLot.getReserved10());
                    packingListBoxPrintInfo.setCarton_qty(boxMLot.getCurrentQty().toPlainString());
                    packingListBoxPrintInfo.setNw(netWeight.toPlainString());
                    packingListBoxPrintInfo.setGw(grossWeight.toPlainString());
                    String ctnIdx = boxMLot.getMaterialLotId().substring(0, boxMLot.getMaterialLotId().indexOf(StringUtils.SPLIT_CODE));
                    packingListBoxPrintInfo.setCtn_idx(Integer.valueOf(ctnIdx));

                    packingListBoxPrintInfos.add(packingListBoxPrintInfo);
                }
            }

            packingListBoxPrintInfos = packingListBoxPrintInfos.stream().sorted(Comparator.comparing(PackingListBoxPrintInfo::getReel_code)).sorted(Comparator.comparingInt(PackingListBoxPrintInfo::getCtn_idx)).collect(Collectors.toList());
            packingListPrintInfo.setPackingListBoxPrintInfos(packingListBoxPrintInfos);

            packingListPrintInfo.setCNO(boxMaterialLots.size() + StringUtils.EMPTY);
            packingListPrintInfo.setAttn(deliveryOrderLine.getReserved17());
            packingListPrintInfo.setShipAdd(deliveryOrderLine.getReserved16());
            packingListPrintInfo.setShipTo(deliveryOrderLine.getReserved15());
            packingListPrintInfo.setShipDate(shippingDate);
            packingListPrintInfo.setDocumentLineId(deliveryOrderLine.getLineId());
            packingListPrintInfo.setTel(deliveryOrderLine.getReserved18());

            BigDecimal totalQty = boxMaterialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot::getCurrentQty));
            packingListPrintInfo.setTotalQty(totalQty.toPlainString());

            BigDecimal totalNW = BigDecimal.ZERO;
            BigDecimal totalGW = BigDecimal.ZERO;
            for (MaterialLot materialLot : boxMaterialLots) {
                BigDecimal netWeight = new BigDecimal(materialLot.getReserved12()).setScale(3, BigDecimal.ROUND_HALF_UP);
                BigDecimal grossWeight = new BigDecimal(materialLot.getReserved13()).setScale(3, BigDecimal.ROUND_HALF_UP);

                totalNW = totalNW.add(netWeight);
                totalGW = totalGW.add(grossWeight);
            }
            packingListPrintInfo.setTotalNW(totalNW.toPlainString());
            packingListPrintInfo.setTotalGW(totalGW.toPlainString());
            packingListPrintInfo.setPackingListType(deliveryOrderLine.getReserved27());

            return packingListPrintInfo;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 出货单参数
     * @param documentLineId
     * @return
     * @throws ClientException
     */
    public ShippingListPrintInfo getShippingListPrintParameter(String documentLineId) throws ClientException{
        try {
            ShippingListPrintInfo shipListPrintInfo = new ShippingListPrintInfo();
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
            BigDecimal totalQty = boxMaterialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));

            String shippingDate = StringUtils.EMPTY;
            if (deliveryOrderLine.getShippingDate() != null){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
                shippingDate = simpleDateFormat.format(deliveryOrderLine.getShippingDate());
            }

            shipListPrintInfo.setDeliveryOrderLineId(deliveryOrderLine.getLineId());
            shipListPrintInfo.setShipAdd(deliveryOrderLine.getReserved16());
            shipListPrintInfo.setShipTo(deliveryOrderLine.getReserved15());
            shipListPrintInfo.setContact(deliveryOrderLine.getReserved17());
            shipListPrintInfo.setTel(deliveryOrderLine.getReserved18());
            shipListPrintInfo.setShippingDate(shippingDate);
            shipListPrintInfo.setTotalBoxQty(boxMaterialLots.size()+StringUtils.EMPTY);
            shipListPrintInfo.setTotalQty(totalQty+StringUtils.EMPTY);
            shipListPrintInfo.setFreighter(deliveryOrderLine.getReserved7());
            shipListPrintInfo.setLogisticsInfo(deliveryOrderLine.getReserved8());

            List<ShippingListBoxPrintInfo> shipListBoxPrintInfos = Lists.newArrayList();
            for (MaterialLot boxMaterialLot : boxMaterialLots) {
                ShippingListBoxPrintInfo shipListBoxPrintInfo = new ShippingListBoxPrintInfo();
                shipListBoxPrintInfo.setBox_material_lot(boxMaterialLot.getMaterialLotId());
                shipListBoxPrintInfo.setPart_Number(boxMaterialLot.getReserved2());
                shipListBoxPrintInfo.setQty(boxMaterialLot.getCurrentQty().toPlainString());

                String ctnIdx = boxMaterialLot.getMaterialLotId().substring(0, boxMaterialLot.getMaterialLotId().indexOf(StringUtils.SPLIT_CODE));
                shipListBoxPrintInfo.setCtn_idx(ctnIdx);

                shipListBoxPrintInfos.add(shipListBoxPrintInfo);
            }

            shipListBoxPrintInfos = shipListBoxPrintInfos.stream().sorted(Comparator.comparingInt(shippingListBoxPrintInfo -> Integer.valueOf(shippingListBoxPrintInfo.getCtn_idx()))).collect(Collectors.toList());

            shipListPrintInfo.setShipListBoxPrintInfos(shipListBoxPrintInfos);
            return shipListPrintInfo;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 拣配表参数
     * @param documentLineId
     * @return
     * @throws ClientException
     */
    public PKListPrintInfo getPKListParameter(String documentLineId) throws ClientException{
        try {
            PKListPrintInfo pKListPrintInfo = new PKListPrintInfo();

            DocumentLine deliveryOrderLine = documentLineRepository.findByLineId(documentLineId);
            if (deliveryOrderLine == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, documentLineId);
            }

            pKListPrintInfo.setLineId(deliveryOrderLine.getLineId());
            pKListPrintInfo.setPartNumber(deliveryOrderLine.getReserved22());
            pKListPrintInfo.setPartVersion(deliveryOrderLine.getReserved3());
            pKListPrintInfo.setCustomerCode(deliveryOrderLine.getReserved2());

            List<PKListMLotPrintInfo> pkListMLotPrintInfos = Lists.newArrayList();

            List<MaterialLot> materialLots = materialLotRepository.findByReserved44(deliveryOrderLine.getObjectRrn());
            materialLots = materialLots.stream().filter(mlot -> StringUtils.isNullOrEmpty(mlot.getPackageType())).collect(Collectors.toList());

            List<String> reelCodeMLotIds = materialLots.stream().map(mLot -> mLot.getMaterialLotId()).collect(Collectors.toList());
            List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotIdIn(reelCodeMLotIds);
            for (MaterialLot materialLot : materialLots) {
                List<MaterialLotUnit> materialLotUnitList = Lists.newArrayList();

                if (validateProductLot(materialLot.getInferiorProductsFlag())){
                    materialLotUnitList = materialLotUnits.stream().filter(mLotUnit -> materialLot.getMaterialLotId().equals(mLotUnit.getMaterialLotId())).collect(Collectors.toList());
                }else {
                    List<MaterialLotUnit> materialLotUnitcollect = materialLotUnits.stream().filter(mLotUnit -> materialLot.getMaterialLotId().equals(mLotUnit.getMaterialLotId())).collect(Collectors.toList());
                    BigDecimal totalQty = materialLotUnitcollect.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLotUnit::getQty));
                    MaterialLotUnit materialLotUnit = materialLotUnitcollect.get(0);

                    materialLotUnitRepository.getEntityManager().detach(materialLotUnit);
                    materialLotUnit.setQty(totalQty);
                    materialLotUnitList.add(materialLotUnit);
                }
                for (MaterialLotUnit unit : materialLotUnitList) {
                    PKListMLotPrintInfo pkListMLotPrintInfo = new PKListMLotPrintInfo();
                    pkListMLotPrintInfo.setStorageId(materialLot.getLastStorageId());
                    pkListMLotPrintInfo.setMaterial_lot_id(unit.getMaterialLotId());
                    pkListMLotPrintInfo.setUnit_id(unit.getUnitId());
                    pkListMLotPrintInfo.setControl_lot(unit.getReserved4());
                    pkListMLotPrintInfo.setQty(unit.getQty().toPlainString());
                    pkListMLotPrintInfo.setDc(unit.getReserved2());

                    pkListMLotPrintInfos.add(pkListMLotPrintInfo);
                }
            }
            //批次号排序
            pkListMLotPrintInfos = pkListMLotPrintInfos.stream().sorted(Comparator.comparing(PKListMLotPrintInfo::getMaterial_lot_id)).collect(Collectors.toList());
            pKListPrintInfo.setPKListMLotPrintInfos(pkListMLotPrintInfos);
            return pKListPrintInfo;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public DeliveryPrintInfo getDeliveryOrderParameter(String documentLineId) throws ClientException{
        try {
            DeliveryPrintInfo deliveryPrintInfo = new DeliveryPrintInfo();
            List<DeliveryMLotPrintInfo> deliveryMLotPrintInfoList = Lists.newArrayList();

            DocumentLine deliveryOrderLine = documentLineRepository.findByLineId(documentLineId);
            if (deliveryOrderLine == null){
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_EXIST, documentLineId);
            }
            deliveryPrintInfo.setShipFrom("");
            deliveryPrintInfo.setShipTo(deliveryOrderLine.getReserved15());
            deliveryPrintInfo.setShipAdd(deliveryOrderLine.getReserved16());
            deliveryPrintInfo.setContact(deliveryOrderLine.getReserved17());
            deliveryPrintInfo.setTel(deliveryOrderLine.getReserved18());

            deliveryPrintInfo.setDeliveryOrderLineId(deliveryOrderLine.getLineId());
            deliveryPrintInfo.setShippingDate(deliveryOrderLine.getShippingDate()+"");
            deliveryPrintInfo.setCustomerPO(deliveryOrderLine.getReserved20());
            deliveryPrintInfo.setInvNo(deliveryOrderLine.getReserved21());
            deliveryPrintInfo.setSoNo(deliveryOrderLine.getReserved19());
            deliveryPrintInfo.setFreighter(deliveryOrderLine.getReserved7());
            deliveryPrintInfo.setLogisticsInfo(deliveryOrderLine.getReserved8());

            if (!StringUtils.isNullOrEmpty(deliveryOrderLine.getReserved24())) {
                DeliveryMLotPrintInfo deliveryMLotPrintInfo = new DeliveryMLotPrintInfo();
                deliveryMLotPrintInfo.setPartNumber(deliveryOrderLine.getReserved22());
                deliveryMLotPrintInfo.setPartVersion(deliveryOrderLine.getReserved3());
                deliveryMLotPrintInfo.setGrade(deliveryOrderLine.getReserved4());
                deliveryMLotPrintInfo.setMrb(deliveryOrderLine.getReserved5());
                deliveryMLotPrintInfo.setQty(deliveryOrderLine.getQty() + "");
                deliveryMLotPrintInfo.setRemark(deliveryOrderLine.getReserved6());

                deliveryMLotPrintInfoList.add(deliveryMLotPrintInfo);
            }else {
                List<MaterialLot> reelAndBoxMLots = materialLotRepository.findByReserved45(deliveryOrderLine.getLineId());
                List<MaterialLot> reelMaterialLots = reelAndBoxMLots.stream().filter(mlot-> (StringUtils.isNullOrEmpty(mlot.getCategory()))).collect(Collectors.toList());
                Map<String, List<MaterialLot>> partNumberAndVersionMap = reelMaterialLots.stream().collect(Collectors.groupingBy(reelMLot -> reelMLot.getReserved2() + StringUtils.SPLIT_CODE + reelMLot.getReserved3()));
                for (String partNumberAndVersion : partNumberAndVersionMap.keySet()) {
                    List<MaterialLot> materialLots = partNumberAndVersionMap.get(partNumberAndVersion);
                    BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot::getCurrentQty));
                    for (MaterialLot materialLot : materialLots) {
                        DeliveryMLotPrintInfo deliveryMLotPrintInfo = new DeliveryMLotPrintInfo();
                        deliveryMLotPrintInfo.setPartNumber(materialLot.getReserved2());
                        deliveryMLotPrintInfo.setPartVersion(materialLot.getReserved3());
                        deliveryMLotPrintInfo.setQty(totalQty+"");
                        deliveryMLotPrintInfoList.add(deliveryMLotPrintInfo);
                    }
                }
            }
            deliveryPrintInfo.setDeliveryMLotPrintInfoList(deliveryMLotPrintInfoList);
            return deliveryPrintInfo;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public RSAndScrapInfo getReturnSuppleOrderAndScrapOrderPrintInfo(String documentId) throws ClientException{
        try {
            Document document = documentService.getDocumentByName(documentId, true);
            RSAndScrapInfo rSAndScrapInfo = new RSAndScrapInfo();
            rSAndScrapInfo.setCreated(document.getCreated()+"");
            rSAndScrapInfo.setPrintDate(DateUtils.nowStr());
            rSAndScrapInfo.setPrintType(document.getCategory());
            rSAndScrapInfo.setAddress(document.getReserved5());
            rSAndScrapInfo.setContact(document.getReserved7());
            rSAndScrapInfo.setTel(document.getReserved6());
            rSAndScrapInfo.setDocId(document.getName());
            rSAndScrapInfo.setLogistics(document.getReserved4());
            rSAndScrapInfo.setRmaNo(document.getReserved3());
            rSAndScrapInfo.setComment(document.getDescription());
            rSAndScrapInfo.setShippingDate(document.getShippingDate()+"");

            List<RSAndScrapMLotInfo> rsAndScrapMLotInfoList = Lists.newArrayList();
            List<MaterialLot> materialLotList = getStockUpMLot(document.getName());
            BigDecimal totalQty = materialLotList.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot::getCurrentQty));
            rSAndScrapInfo.setTotalQty(totalQty.toPlainString());

            int id = 1;
            for (MaterialLot materialLot : materialLotList){
                RSAndScrapMLotInfo rsAndScrapMLotInfo = new RSAndScrapMLotInfo();
                rsAndScrapMLotInfo.setId(id++ +StringUtils.EMPTY);
                rsAndScrapMLotInfo.setMaterialLotId(materialLot.getMaterialLotId());
                rsAndScrapMLotInfo.setMaterialName(materialLot.getMaterialName());
                rsAndScrapMLotInfo.setMaterialDesc(materialLot.getMaterialDesc());
                rsAndScrapMLotInfo.setQty(materialLot.getCurrentQty()+"");
                rsAndScrapMLotInfo.setStorageId(materialLot.getLastStorageId());
                rsAndScrapMLotInfo.setStoreUom(materialLot.getStoreUom());
                rsAndScrapMLotInfoList.add(rsAndScrapMLotInfo);
            }

            rSAndScrapInfo.setRsAndScrapMLotInfos(rsAndScrapMLotInfoList);
            return rSAndScrapInfo;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 装箱检验
     * @param materialLotAction 批次号 动作码OK/NG
     * @throws ClientException
     */
    public void packCheck(MaterialLotAction materialLotAction) throws ClientException{
        try {
            String actionCode = materialLotAction.getActionCode();
            MaterialLot boxMaterialLot = mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId());
            MaterialLot materialLot = mmsService.changeMaterialLotState(boxMaterialLot, MaterialStatus.STATUS_PACK_CHECK, actionCode);
            materialLotAction.setTransQty(materialLot.getCurrentQty());
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_PACK_CKECK, materialLotAction);

            if (MaterialStatus.STATUS_NG.equals(actionCode)){
                List<MaterialLotAction> materialLotActions = Lists.newArrayList();
                materialLotActions.add(materialLotAction);
                materialLotAction.setActionCode(MaterialLotHold.PACK_CHECK_HOLD);
                mmsService.holdMaterialLot(materialLotActions);
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
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

            //手持入库增强
            List<MaterialLot> materialLotList = Lists.newArrayList();
            materialLotList.add(materialLot);
            List<MaterialLotAction> materialLotActions = Lists.newArrayList();
            materialLotActions.add(materialLotAction);
            applicationContext.publishEvent(new StockInApplicationEvent(this, materialLotList, materialLotActions));

            return materialLot;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void validataStockInByOrder(String docId, List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            Document document = documentService.getDocumentByName(docId, true);
            if (!(document instanceof RMAIncomingOrder)) {
                return;
            }

            Map<String, List<MaterialLotAction>> targetStorageIdMap = materialLotActions.stream().collect(Collectors.groupingBy(MaterialLotAction::getTargetStorageId));
            List<MaterialLot> materialLotList = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            List<Warehouse> warehouseList = Lists.newArrayList();

            for (String targetStorageId : targetStorageIdMap.keySet()) {
                List<MaterialLotAction> materialLotActionList = targetStorageIdMap.get(targetStorageId);
                Storage storage = storageRepository.findOneByName(targetStorageId);
                Warehouse warehouse = warehouseRepository.findByObjectRrn(storage.getWarehouseRrn());
                warehouseList.add(warehouse);

                for (MaterialLotAction materialLotAction : materialLotActionList) {
                    materialLotAction.setTargetWarehouseId(warehouse.getName());
                }
            }
            Set<String> warehouseName = warehouseList.stream().map(warehouse -> warehouse.getName()).collect(Collectors.toSet());
            List<MaterialLot> subMLots = materialLotList.stream().filter(mLot -> mLot.getSubMaterialLotFlag()).collect(Collectors.toList());
            BigDecimal totalQty = materialLotList.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot::getCurrentQty));
            if (warehouseName.size() > 1 || subMLots.size() >= 1 || !document.getQty().equals(totalQty)){
                sendEmailByOrderStockIn(document, materialLotActions);
                throw new ClientParameterException(DocumentException.DOCUMENT_IS_NOT_SAME, docId);
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 来料根据单据入库
     * @param docId
     * @param materialLotActions
     * @return
     * @throws ClientException
     */
    public void stockInMLotByOrderMobile(String docId, List<MaterialLotAction> materialLotActions)throws ClientException{
        try {
            Map<String, List<MaterialLotAction>> storageIdMap = materialLotActions.stream().collect(Collectors.groupingBy(MaterialLotAction::getTargetStorageId));
            List<MaterialLot> materialLotList = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            List<MaterialLot> materialLots = Lists.newArrayList();

            for (String storageId : storageIdMap.keySet()) {
                List<MaterialLotAction> materialLotActionList = storageIdMap.get(storageId);
                Storage storage = storageRepository.findOneByName(storageId);
                Warehouse warehouse = warehouseRepository.findByObjectRrn(storage.getWarehouseRrn());
                for (MaterialLotAction materialLotAction : materialLotActionList) {
                    Optional<MaterialLot> materialLotFirst = materialLotList.stream().filter(mLot -> mLot.getMaterialLotId().equals(materialLotAction.getMaterialLotId())).findFirst();
                    MaterialLot materialLot = materialLotFirst.get();

                    materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
                    materialLotAction.setTransQty(materialLot.getCurrentQty());
                    materialLot = mmsService.stockIn(materialLot, materialLotAction);
                    materialLots.add(materialLot);
                }
            }

            if (!StringUtils.isNullOrEmpty(docId)){
                Document document = documentService.getDocumentByName(docId, true);
                if (document instanceof RMAIncomingOrder) {
                    erpService.backhaulDeliveryStatus(docId, null, materialLots, DeliveryStatus.POSTING_DELIVERY_STATUS, null);
                }
            }else {
                applicationContext.publishEvent(new StockInApplicationEvent(this, materialLots, materialLotActions));
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void sendEmailByOrderStockIn(Document document, List<MaterialLotAction> materialLotActions) throws ClientException{
        if (Document.STATUS_APPROVE.equals(document.getStatus())) {
            StringBuffer value = new StringBuffer();
            for (MaterialLotAction materialLotAction : materialLotActions) {
                value.append(" <tr> ");
                value.append(" <td> ");
                value.append(materialLotAction.getMaterialLotId());
                value.append(" </td> ");

                value.append(" <td> ");
                value.append(materialLotAction.getTransQty());
                value.append(" </td> ");

                value.append(" <td> ");
                value.append(materialLotAction.getTargetWarehouseId());
                value.append(" </td> ");
                value.append(" </tr> ");
            }
            List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>)uiService.getReferenceList("IncomingStockInEmail", "Owner");

            List<String> to = nbReferenceList.stream().map(nbRefList -> nbRefList.getKey()).collect(Collectors.toList());
            Map<String, Object> parameterMap = Maps.newHashMap();
            parameterMap.put("documentId", document.getName());
            parameterMap.put("value", value);
            mailService.sendTemplateMessage(to, "Rma单据信息不一致", "reserved_release", parameterMap);
        }
    }

    /**
     * by单据领料下架
     * @param documentId
     * @param materialLotActions
     * @throws ClientException
     */
    public void stockOutMLotByOrder(String documentId, List<MaterialLotAction> materialLotActions)throws ClientException{
        try {
            materialLotActions.forEach(materialLotAction -> stockOutMLotMobile(materialLotAction));
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 手持端 领料下架
     * @param materialLotAction 批次号,库位号
     * @return
     * @throws ClientException
     */
    public MaterialLot stockOutMLotMobile(MaterialLotAction materialLotAction)throws ClientException{
        try {
            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true);
            Storage storage = storageRepository.findOneByName(materialLotAction.getFromStorageId());

            materialLotAction.setTransQty(materialLot.getCurrentQty());
            materialLotAction.setFromWarehouseRrn(materialLot.getLastWarehouseRrn());
            materialLotAction.setFromStorageRrn(storage.getObjectRrn());
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
    public MaterialLot queryPackageMLotMobile (MaterialLotAction materialLotAction) throws ClientException {
        try {
            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true);
            if (!StringUtils.isNullOrEmpty(materialLot.getReserved44()) && StringUtils.isNullOrEmpty(materialLot.getBoxMaterialLotId())
                    && (StringUtils.isNullOrEmpty(materialLot.getCategory()) || StringUtils.NO.equals(materialLot.getCategory()) )) {
                return materialLot;
            }
            return null;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 批量转库
     * @param materialLotActions 物料批次号,来源货架号,目标货架号
     * @throws ClientException
     */
    public void transferInvMLots(List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            materialLotActions.forEach(materialLotAction -> transferInvMobile(materialLotAction));

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 手持端转库
     * @param materialLotAction 物料批次号,来源货架号，目标货架号
     * @throws ClientException
     */
    public void transferInvMobile(MaterialLotAction materialLotAction) throws ClientException{
        try {
            Storage fromStorage = storageRepository.findOneByName(materialLotAction.getFromStorageId());
            Warehouse fromWarehouse = warehouseRepository.findByObjectRrn(fromStorage.getWarehouseRrn());
            materialLotAction.setFromWarehouseRrn(fromWarehouse.getObjectRrn());
            materialLotAction.setFromWarehouseId(fromWarehouse.getName());
            materialLotAction.setFromStorageRrn(fromStorage.getObjectRrn());
            materialLotAction.setFromStorageId(fromStorage.getName());

            Storage targetStorage = storageRepository.findOneByName(materialLotAction.getTargetStorageId());
            Warehouse targetWarehouse = warehouseRepository.findByObjectRrn(targetStorage.getWarehouseRrn());
            materialLotAction.setTargetWarehouseRrn(targetWarehouse.getObjectRrn());
            materialLotAction.setTargetWarehouseId(targetWarehouse.getName());
            materialLotAction.setTargetStorageRrn(targetStorage.getObjectRrn());
            materialLotAction.setTargetStorageId(targetStorage.getName());

            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true);
            materialLotAction.setTransQty(materialLot.getCurrentQty());
            mmsService.transfer(materialLot, materialLotAction);

            //移库上报ERP
            if(!materialLotAction.getFromWarehouseRrn().equals(materialLotAction.getTargetWarehouseRrn())){
                List<MaterialLot> materialLots = Lists.newArrayList();
                materialLots.add(materialLot);
                List<MaterialLotAction> materialLotActions = Lists.newArrayList();
                materialLotActions.add(materialLotAction);
                erpService.backhaulStockTransfer(materialLots, materialLotActions);
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e,log);
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

            product = mmsService.saveProduct(product, product.getWarehouseName(), product.getIqcSheetName());
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

            if (Material.MATERIAL_CATEGORY_PARTS.equals(rawMaterial.getMaterialCategory())){
                Parts parts = new Parts();
                PropertyUtils.copyProperties(rawMaterial, parts);
                saveParts(parts);
            }
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
                throw new ClientParameterException(VanchipExceptions.CONVERSION_MATERIAL_MODEL_IS_NOT_EXIST, material);
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
                storage.setWarehouse(warehouse);
            }
            storage = (Storage)baseService.saveEntity(storage);
            return storage;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * NBQuery 查询
     * @param queryText
     * @param paramMap
     * @param firstResult
     * @param maxResult 最大查询条数
     * @param whereClause
     * @param orderByClause
     * @return
     * @throws ClientException
     */
    public List<Map> findEntityMapListByQueryText(String queryText, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause) throws ClientException {
        try {
            Query query = findEntityByQueryText(queryText, whereClause, orderByClause);
            if (firstResult > 0) {
                query.setFirstResult(firstResult);
            }
            if (maxResult > 0 ){
                query.setMaxResults(maxResult);
            }
            if (paramMap != null) {
                for (String key : paramMap.keySet()) {
                   query.setParameter(key, paramMap.get(key));
                }
            }
            query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            return query.getResultList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * @param queryText
     * @param whereClause 数据库字段名
     * @param orderByClause 数据库字段名
     * @return
     * @throws ClientException
     */
    public Query findEntityByQueryText(String queryText, String whereClause, String orderByClause) throws ClientException {
        try {
            StringBuffer sqlBuffer = new StringBuffer();
            sqlBuffer.append("SELECT * FROM (");
            sqlBuffer.append(queryText);
            if (!StringUtils.isNullOrEmpty(whereClause)) {
                sqlBuffer.append(" AND ");
                sqlBuffer.append(whereClause);
            }
            if (!StringUtils.isNullOrEmpty(orderByClause)) {
                sqlBuffer.append(" ORDER BY ");
                sqlBuffer.append(orderByClause);
            }
            sqlBuffer.append(")");
            Query query = entityManager.createNativeQuery(sqlBuffer.toString());
            return query;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public NBQuery findNBQueryByName(String queryName, boolean exceptionFlag) throws ClientException {
        try {
            NBQuery nbQuery = queryRepository.findOneByName(queryName);
            if(nbQuery == null && exceptionFlag){
                throw new ClientParameterException(VanchipExceptions.NB_QUERY_IS_NOT_EXIST, queryName);
            }
            return nbQuery;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     *验证当前仓库
     * @param materialLotAction 批次号 当前货架号
     * @throws ClientException
     */
    public void valiadateFromWarehouse(MaterialLotAction materialLotAction) throws ClientException{
        try {
            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true);
            Storage fromStorage = storageRepository.findOneByName(materialLotAction.getFromStorageId());
            Warehouse fromWarehouse = warehouseRepository.findByObjectRrn(fromStorage.getWarehouseRrn());

            MaterialLotInventory materialLotInventory = mmsService.getMaterialLotInv(materialLot.getObjectRrn(), fromWarehouse.getObjectRrn(), fromStorage.getObjectRrn());
            if (materialLotInventory == null) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_NOT_IN_INVENTORY, materialLot.getMaterialLotId());
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证目标仓库是否正确
     * @param materialLotAction 批次号 目标货架号
     * @throws ClientException
     */
    public void valiadateTargetWarehouse(MaterialLotAction materialLotAction) throws ClientException{
        try {
            Storage targetStorage = storageRepository.findOneByName(materialLotAction.getTargetStorageId());
            if(targetStorage == null){
                throw new ClientParameterException(MmsException.MM_STORAGE_IS_NOT_EXIST, materialLotAction.getTargetStorageId());
            }
            Warehouse targetWarehouse = mmsService.getWarehouseByName(targetStorage.getWarehouseName(), true);
            mmsService.validatTargetWarehouse(materialLotAction.getMaterialLotId(), targetWarehouse);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 预警
     * @throws ClientException
     */
    public void preWarning() throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotRepository.findByWarningStatusNotOrWarningStatusNullAndStatus("Expire", "In");
            materialLots.forEach(materialLot -> preWarning(materialLot));
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 预警
     * @throws ClientException
     */
    public void preWarning(MaterialLot materialLot) throws ClientException{
        try {
            Double warningLife = materialLot.getWarningLife();
            Double effectiveLife = materialLot.getEffectiveLife();
            //Year(s) ==> Years; Day(s) ==> Days
            String effectiveUnit = materialLot.getEffectiveUnit();
            if (warningLife == null || effectiveLife == null || effectiveUnit == null){
                return;
            }
            effectiveUnit = effectiveUnit.replace(StringUtils.LEFT_BRACKETS, StringUtils.EMPTY);
            effectiveUnit = effectiveUnit.replace(StringUtils.RIGHT_BRACKETS, StringUtils.EMPTY);
            if (effectiveUnit.equalsIgnoreCase(ChronoUnit.YEARS.name())){
                effectiveLife = effectiveLife * 365;
                warningLife = warningLife * 356;
            }

            //今天至生产日期的间隔天数
            Date productionDate = materialLot.getProductionDate();
            if (productionDate == null){
                return;
            }
            Long productionDateUntil = DateUtils.until(productionDate, ChronoUnit.DAYS);
            productionDateUntil = Math.abs(productionDateUntil);

            //今天至到期日期的间隔天数
            Date expireDate = materialLot.getExpireDate();
            if (expireDate == null){
                expireDate = mmsService.calculateTargetDate(materialLot.getProductionDate(), effectiveLife, effectiveUnit);
                materialLot.setExpireDate(expireDate);
            }
            Long expireDateUntil = DateUtils.until(expireDate, ChronoUnit.DAYS);

            String key = StringUtils.EMPTY;
            if (Material.MATERIAL_CATEGORY_PACKING_MATERIAL.equals(materialLot.getMaterialCategory())){
                effectiveLife = effectiveLife / 2;

                //到期时间减90天日期
                Date targetDate = DateUtils.minus(expireDate, 90, ChronoUnit.DAYS);
                Long targetDateUntil = DateUtils.until(targetDate, ChronoUnit.DAYS);
                if(expireDateUntil <=  effectiveLife.longValue()){
                    key = "BCPreWarning1";
                }
                if (expireDateUntil <= Math.abs(targetDateUntil)){
                    key = "BCPreWarning2";
                }
            }else if (Material.MATERIAL_CATEGORY_PRODUCT.equals(materialLot.getMaterialCategory())){
                if(productionDateUntil >= 180){
                    key = "CPPreWarning1";
                }
                if (productionDateUntil >= 270){
                    key = "CPPreWarning2";
                }
            }else {
                if(expireDateUntil <= warningLife.longValue()){
                    key = "Warn";
                }
            }
            if (StringUtils.isNullOrEmpty(key)){
                key = "Normal";
            }
            if(expireDateUntil <= 0){
                key = "Expire";
            }

            List<? extends NBReferenceList> nbReferenceList = uiService.getReferenceList("WarningStatus", "Owner");
            Map<String, List<NBReferenceList>> referenceListMap = nbReferenceList.stream().collect(Collectors.groupingBy(NBReferenceList::getKey));
            NBReferenceList warningStatusRef =  referenceListMap.get(key).get(0);

            //批次的警告状态不等于新状态，才进行修改
            if (!warningStatusRef.getKey().equals(materialLot.getWarningStatus())){
                materialLot.setWarningStatus(warningStatusRef.getKey());
                materialLot.setWarningStatusDesc(warningStatusRef.getDescription());
                baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_UPDATE_WARNING_STATUS);

                if (warningStatusRef.getKey().equals("Expire")){
                    List<MaterialLotAction> materialLotActions = Lists.newArrayList();
                    MaterialLotAction action = new MaterialLotAction();
                    action.setMaterialLotId(materialLot.getMaterialLotId());
                    action.setActionCode(MaterialLotHold.EXPIRE_HOLD);
                    materialLotActions.add(action);
                    mmsService.holdMaterialLot(materialLotActions);
                }
            }

        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public Parts saveParts(Parts parts) throws ClientException{
        try {

            parts = mmsService.saveParts(parts,  parts.getWarehouseName());
            return parts;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 盘点
     * @param documentLine
     * @param materialLotActions transQty盘点数量
     * @throws ClientException
     */
    public void checkMLotInventorys(DocumentLine documentLine, List<MaterialLotAction> materialLotActions)throws ClientException{
        try {
            documentLine = documentLineRepository.findByLineId(documentLine.getLineId());
            if (documentLine == null){
                throw new ClientParameterException(DOCUMENT_IS_NOT_EXIST, documentLine.getLineId());
            }
            Document document = documentService.getDocumentByName(documentLine.getDocId(), true);

            List<MaterialLot> materialLots = Lists.newArrayList();
            for (MaterialLotAction materialLotAction : materialLotActions) {
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId());
                materialLots.add(materialLot);

                materialLot.setLastDocumentInfo(document);
                mmsService.checkMaterialInventory(materialLot, materialLotAction);

                String docMLotStatus = DocumentMLot.STATUS_CHECK;
                if (materialLot.getCurrentQty().compareTo(materialLotAction.getTransQty()) != 0) {
                    docMLotStatus = DocumentMLot.STATUS_WAIT_RECHECK;
                }
                documentService.createDocumentMLot(documentLine.getLineId(), materialLot.getMaterialLotId(), docMLotStatus);
            }
            BigDecimal transTotalQty = materialLotActions.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLotAction::getTransQty));
            BigDecimal mLotTotalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot::getCurrentQty));

            documentLine.setStatus(Document.STATUS_WAIT_RECHECK);
            if (mLotTotalQty.compareTo(transTotalQty) == 0) {
                documentLine.setStatus(Document.STATUS_CLOSE);
            }
            documentLine.setQty(transTotalQty);
            documentLine = (DocumentLine)baseService.saveEntity(documentLine);

            if (mLotTotalQty.compareTo(transTotalQty) == 0) {
                if (!"ERP".equals(documentLine.getCreatedBy())) return;
                Map<String, List<MaterialLot>> warehoureNameMap = materialLots.stream().collect(Collectors.groupingBy(mLot -> mLot.getLastWarehouseId()));
                for (String warehoureName : warehoureNameMap.keySet()) {
                    erpService.backhaulCheck(documentLine, warehoureNameMap.get(warehoureName), warehoureName);
                }
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 复盘，更新库存
     * @param documentLine
     * @param materialLotActions
     * @throws ClientException
     */
    public void recheckMLotInventorys(DocumentLine documentLine, List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            documentLine = documentLineRepository.findByLineId(documentLine.getLineId());
            if (documentLine == null){
                throw new ClientParameterException(DOCUMENT_IS_NOT_EXIST, documentLine.getLineId());
            }

            for (MaterialLotAction materialLotAction : materialLotActions) {
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId());
                mmsService.recheckMaterialInventory(materialLot, materialLotAction);
            }
            List<String> materialLotIds = materialLotActions.stream().map(materialLotAction -> materialLotAction.getMaterialLotId()).collect(Collectors.toList());
            documentService.changeDocMLotStatus(documentLine.getLineId(), materialLotIds, DocumentMLot.STATUS_RECHECK);

            documentLine.setStatus(Document.STATUS_CLOSE);
            documentLine = (DocumentLine)baseService.saveEntity(documentLine);

            if (!"ERP".equals(documentLine.getCreatedBy())) return;
            List<MaterialLot> materialLotList = documentService.getReservedMLotByDocId(documentLine.getLineId());
            Map<String, List<MaterialLot>> warehoureNameMap = materialLotList.stream().collect(Collectors.groupingBy(mLot -> mLot.getLastWarehouseId()));
            for (String warehoureName : warehoureNameMap.keySet()) {
                erpService.backhaulCheck(documentLine, warehoureNameMap.get(warehoureName), warehoureName);
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获得需要复盘批次
     * @param documentLine
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getRecheckMLots(DocumentLine documentLine)throws ClientException{
        try {
            List<DocumentMLot> documentMLots = documentMLotRepository.findByDocumentId(documentLine.getLineId());
            documentMLots = documentMLots.stream().filter(docMLot -> DocumentMLot.STATUS_WAIT_RECHECK.equals(docMLot.getStatus())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(documentMLots)) {
                return null;
            }
            List<MaterialLot> materialLotList = documentMLots.stream().map(mLot -> mmsService.getMLotByMLotId(mLot.getMaterialLotId())).collect(Collectors.toList());
            return materialLotList;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void stockOutPartsByOrder(String documentId, MaterialLotAction materialLotAction) throws ClientException{
        try {
            BigDecimal totalQty = materialLotAction.getTransQty();
            Document document = documentService.saveDocument(documentId, totalQty, DocumentHistory.TRANS_TYPE_STOCK_OUT);

            List<DocumentLine> documentLines = documentLineRepository.findByDocId(documentId);
            if (CollectionUtils.isEmpty(documentLines)){
                throw new ClientParameterException(DOCUMENT_IS_NOT_EXIST, documentId);
            }
            DocumentLine documentLine = documentLines.get(0);
            documentLine.setUnHandledQty(documentLine.getUnHandledQty().subtract(totalQty));
            documentLine.setHandledQty(documentLine.getHandledQty().add(totalQty));
            documentLine = (DocumentLine)baseService.saveEntity(documentLine);

            Material material = mmsService.getMaterialByName(documentLine.getMaterialName(), true);
            materialLotAction.setFromWarehouseRrn(material.getWarehouseRrn());
            stockOutParts(material, materialLotAction);

            mesService.issuePartsMLot(documentLine);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void stockOutParts(Material material, MaterialLotAction materialLotAction) throws ClientException{
        try {
            BigDecimal transQty = materialLotAction.getTransQty();
            Warehouse warehouse = warehouseRepository.findByObjectRrn(materialLotAction.getFromWarehouseRrn());

            List<MaterialLot> materialLots = materialLotRepository.findByMaterialNameAndStatus(material.getName(), MaterialStatus.STATUS_IN);
            materialLots = materialLots.stream().filter(mLot -> mLot.getLastWarehouseId().equals(warehouse.getName())).collect(Collectors.toList());
            materialLots = materialLots.stream().sorted(Comparator.comparing(MaterialLot::getReceiveDate)).collect(Collectors.toList());

            BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot::getCurrentQty));
            if (CollectionUtils.isEmpty(materialLots) || transQty.compareTo(totalQty) > 0){
                throw new ClientParameterException(OPERATIONS_QTY_GREATER_THAN_ACTUAL_QTY, transQty);
            }
            for (MaterialLot mLot:materialLots){
                BigDecimal currentQty = mLot.getCurrentQty();
                if (transQty.compareTo(mLot.getCurrentQty()) >= 0){
                    materialLotAction.setTransQty(mLot.getCurrentQty());
                    mmsService.stockOut(mLot, materialLotAction);
                }else if (transQty.compareTo(mLot.getCurrentQty()) < 0){
                    materialLotAction.setTransQty(transQty);
                    MaterialLot materialLot = mmsService.splitMLot(mLot.getMaterialLotId(), materialLotAction);

                    materialLotAction.setTransQty(materialLot.getCurrentQty());
                    mmsService.stockOut(materialLot, materialLotAction);
                }
                transQty = transQty.subtract(currentQty);
                if (transQty.compareTo(BigDecimal.ZERO) <= 0){
                    return;
                }
            }

        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 退料，
     * 如果物料批次不存在则创建,退料则是接收
     * @param material
     * @param mLotId
     * @param materialLotAction
     * @throws ClientException
     */
    public MaterialLot returnMLot(Material material, String mLotId, MaterialLotAction materialLotAction) throws ClientException {
        try {
            MaterialLot materialLot;
            if (StringUtils.isNullOrEmpty(mLotId)) {
                StatusModel statusModel = materialStatusModelRepository.findByObjectRrn(material.getStatusModelRrn());
                materialLot = mmsService.createMLot(material, statusModel, mLotId, materialLotAction.getTransQty(), BigDecimal.ZERO, Maps.newHashMap());
            }else {
                materialLot = mmsService.getMLotByMLotId(mLotId, true);
                materialLot.setCurrentQty(materialLotAction.getTransQty());
            }
            materialLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_RETURN, MaterialStatus.STATUS_CREATE);
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_RETURN, materialLotAction);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 退料入库
     * @param material
     * @param mLotId
     * @param materialLotAction
     * @return
     * @throws ClientException
     */
    public MaterialLot returnMLotWarehouse(Material material, String mLotId, MaterialLotAction materialLotAction) throws ClientException{
        try {
            MaterialLot materialLot = returnMLot(material, mLotId, materialLotAction);

            materialLot = mmsService.stockIn(materialLot, materialLotAction);
            return materialLot;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void erpSaveMaterial(List<Material> materials) throws ClientException{
        try {
            Map<String, List<Material>> materialTypeGroupMap = materials.stream().collect(Collectors.groupingBy(Material::getMaterialTypeGroup));
            MaterialStatusModel materialStatusModel = mmsService.getStatusModelByName(Material.DEFAULT_STATUS_MODEL, false);

            for (String materialTypeGroup : materialTypeGroupMap.keySet()) {
                String materialCategory = materialTypeGroup.substring(0, 2);
                String materialClassify= materialTypeGroup.substring(2, 4);
                String materialType = materialTypeGroup.substring(4);

                List<MaterialModelConversion> materialModelConversions = materialModelConversionRepository.findByMaterialCategoryAndMaterialTypeAndMaterialClassify(materialCategory, materialType, materialClassify);
                if (CollectionUtils.isEmpty(materialModelConversions)){
                    throw new ClientParameterException(VanchipExceptions.CONVERSION_MATERIAL_MODEL_IS_NOT_EXIST, materialTypeGroupMap.get(materialTypeGroup).get(0));
                }

                for (Material erpMaterial: materialTypeGroupMap.get(materialTypeGroup)){
                    String materialTypeGroupDesc = erpMaterial.getMaterialTypeGroupDesc();
                    List<String> materialModelDesc = StringUtils.split(materialTypeGroupDesc, StringUtils.SPLIT_CODE);
                    MaterialModelConversion materialModelConversion = materialModelConversions.get(0);

                    erpMaterial.setMaterialCategory(materialModelConversion.getConversionMaterialCategory());
                    erpMaterial.setReserved3(materialModelConversion.getConversionMaterialClassify());
                    erpMaterial.setMaterialType(materialModelConversion.getConversionMaterialType());

                    erpMaterial.setReserved1(materialModelDesc.get(0));
                    erpMaterial.setReserved2(materialModelDesc.get(1));
                    erpMaterial.setReserved4(materialModelDesc.get(2));

                    if (ERP_EFFECTIVE_LIFE_YEARS.equals(erpMaterial.getEffectiveUnit())){

                        erpMaterial.setEffectiveUnit(ChronoUnit.YEARS.name());
                    }else if (ERP_EFFECTIVE_LIFE_WEEKS.equals(erpMaterial.getEffectiveUnit())){

                        erpMaterial.setEffectiveUnit(ChronoUnit.WEEKS.name());
                    }else if (ERP_EFFECTIVE_LIFE_DAYS.equals(erpMaterial.getEffectiveUnit())){

                        erpMaterial.setEffectiveUnit(ChronoUnit.DAYS.name());
                    }else if (ERP_EFFECTIVE_LIFE_MONTHS.equals(erpMaterial.getEffectiveUnit())){

                        erpMaterial.setEffectiveUnit(ChronoUnit.MONTHS.name());
                    }

                    Material material = mmsService.getMaterialByName(erpMaterial.getName(), false);
                    if (material != null){
                        Map<String, Object> propsMap = PropertyUtils.convertObj2Map(erpMaterial);
                        if (propsMap != null && propsMap.size() > 0) {
                            for (String propName : propsMap.keySet()) {
                                Object propValue = propsMap.get(propName);
                                if (propValue == null || StringUtils.isNullOrEmpty(propValue.toString())) {
                                    propValue = StringUtils.EMPTY;
                                }
                                PropertyUtils.setProperty(material, propName, propValue);
                            }
                        }
                        material.setStatusModelRrn(materialStatusModel.getObjectRrn());
                    }else {
                        if (Material.MATERIAL_CATEGORY_PRODUCT.equals(materialModelConversion.getConversionMaterialCategory())){
                            material = new Product();
                            PropertyUtils.copyProperties(erpMaterial, material);
                            material.setStatusModelRrn(materialStatusModel.getObjectRrn());
                        }else {
                            material = new RawMaterial();
                            PropertyUtils.copyProperties(erpMaterial, material);
                            material.setStatusModelRrn(materialStatusModel.getObjectRrn());
                        }
                    }
                    if (Material.MATERIAL_CATEGORY_PRODUCT.equals(materialModelConversion.getConversionMaterialCategory())){
                        material = mmsService.saveProduct((Product)material, material.getWarehouseName(), material.getIqcSheetName());
                    }else {
                        material = mmsService.saveRawMaterial((RawMaterial)material, material.getWarehouseName(), material.getIqcSheetName());
                    }
                }
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 成品HOLD增强
     * 成品根据测试批次记录hold次数
     * @param holdMLotApplicationEvent
     * @return
     * @throws ClientException
     */
    @EventListener(classes = {HoldMLotApplicationEvent.class})
    public MaterialLot holdMaterialLot(HoldMLotApplicationEvent holdMLotApplicationEvent) throws ClientException {
        try {
            MaterialLot materialLot = holdMLotApplicationEvent.getMaterialLot();
            List<MaterialLotAction> materialLotActions = holdMLotApplicationEvent.getMaterialLotActions();

            List<MaterialLotUnit> mLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
            for (MaterialLotAction materialLotAction : materialLotActions) {
                for (MaterialLotUnit unit : mLotUnits) {
                    MaterialLotHold materialLotHold = new MaterialLotHold();
                    String actionPassword = materialLotAction.getActionPassword();
                    if (!StringUtils.isNullOrEmpty(actionPassword)) {
                        materialLotHold.setActionPassword(EncryptionUtils.md5Hex(actionPassword));
                    }
                    materialLotHold.setMaterialLot(materialLot).setAction(materialLotAction);
                    materialLotHold.setUnitId(unit.getUnitId());
                    mmsService.saveMaterialLotHold(materialLotHold);
                }
                baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_HOLD, materialLotAction);
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 荣耀合批规则
     * DC不超过2种
     * @param materialLots
     * @throws ClientException
     */
    public void validationRYMergeRule(List<MaterialLot> materialLots) throws ClientException{
        try {
            List<String> reelCodeMLotIds = materialLots.stream().map(mLot -> mLot.getMaterialLotId()).collect(Collectors.toList());
            List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotIdIn(reelCodeMLotIds);
            Set<String> dcSet = materialLotUnits.stream().map(unit -> unit.getReserved2()).collect(Collectors.toSet());

            if (dcSet.size() > 1){
                throw new ClientException(VanchipExceptions.MERGER_RULE_NOT_ALLOWED);
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialLot getBoxMLotBySubBoxMLotId(String subBoxMLotId) throws ClientException {
        try {
            MaterialLot materialLot = mmsService.getMLotByMLotId(subBoxMLotId, true);
            if (StringUtils.isNullOrEmpty(materialLot.getBoxMaterialLotId())) {
                throw new ClientParameterException(VanchipExceptions.MLOT_NOT_PACKED, subBoxMLotId);
            }
            MaterialLot boxMaterialLot = mmsService.getMLotByMLotId(materialLot.getBoxMaterialLotId(), true);
            return boxMaterialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    public void printBoxMLot(String subBoxMLotId, MaterialLotAction materialLotAction, Boolean validationPrintFlag) throws ClientException{
        try {
            MaterialLot boxMaterialLot = getBoxMLotBySubBoxMLotId(subBoxMLotId);

            boxMaterialLot = mmsService.validationPrintAndAddPrintCount(boxMaterialLot, materialLotAction, validationPrintFlag);

            printService.printBoxMLot(boxMaterialLot);

            //判断是否需要打印荣耀标签
            String finalCustomer = boxMaterialLot.getReserved53();
            if (!StringUtils.isNullOrEmpty(finalCustomer) && FINAL_CUSTOMER_RY.equals(finalCustomer)){
                printRYBoxMLot(subBoxMLotId, materialLotAction, validationPrintFlag);
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void printRYBoxMLot(String subBoxMLotId, MaterialLotAction materialLotAction, Boolean validationPrintFlag) throws ClientException{
        try {
            MaterialLot boxMaterialLot = getBoxMLotBySubBoxMLotId(subBoxMLotId);

            boxMaterialLot = mmsService.validationPrintAndAddPrintCount(boxMaterialLot, materialLotAction, validationPrintFlag);

            printService.printRYBoxMLot(boxMaterialLot);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * iqc审核
     * 1.不通过重新IQC
     * @param materialLotActions
     * @throws ClientException
     */
    public void iqcApprove(List<MaterialLotAction> materialLotActions)throws ClientException{
        try {
            MaterialLotAction materialLotAction = materialLotActions.get(0);
            List<MaterialLot> materialLots = materialLotActions.stream().map(action -> mmsService.getMLotByMLotId(action.getMaterialLotId(), true)).collect(Collectors.toList());
            for (MaterialLot materialLot : materialLots) {
                materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                materialLotAction.setTransQty(materialLot.getCurrentQty());
                this.iqcApprove(materialLot, materialLotAction);
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *IQC审核动作
     * @param materialLot
     * @param materialLotAction actionCode= IQC/Approve
     * @return
     * @throws ClientException
     */
    public MaterialLot iqcApprove(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException {
        try {
            MLotCheckSheet mLotCheckSheet = mLotCheckSheetRepository.findByMaterialLotIdAndStatus(materialLot.getMaterialLotId(), MLotCheckSheet.STATUS_IN_APPROVAL);

            mLotCheckSheet.setStatus(MLotCheckSheet.STATUS_OPEN);
            if (MaterialStatus.STATUS_APPROVE.equals(materialLotAction.getActionCode())){
                mLotCheckSheet.setStatus(MLotCheckSheet.STATUS_CLOSE);
            }
            mLotCheckSheet = mLotCheckSheetRepository.saveAndFlush(mLotCheckSheet);

            materialLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_IQC_APPROVE, materialLotAction.getActionCode());
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_IQC_APPROVE, materialLotAction);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 入库增强
     * 处理入库信息回传其他系统MES/ERP
     * @param stockInApplicationEvent
     * @throws ClientException
     */
    @EventListener(classes = {StockInApplicationEvent.class})
    public void stocktEventListener(StockInApplicationEvent stockInApplicationEvent) throws ClientException{
        try {
            List<MaterialLot> materialLots = stockInApplicationEvent.getMaterialLots();
            List<MaterialLotAction> materialLotActions = stockInApplicationEvent.getMaterialLotActionList();

            this.stockInSendMes(materialLots, materialLotActions);

            this.stockInSendErp(materialLots, materialLotActions);

        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 成品入库回传MES
     * @param materialLots
     * @param materialLotActions
     * @throws ClientException
     */
    public void stockInSendMes(List<MaterialLot> materialLots, List<MaterialLotAction> materialLotActions)throws ClientException{
        try {
            //成品第一次入库回传消息给MES
            List<MaterialLot> productMLots = materialLots.stream().filter(materialLot ->
                    Material.MATERIAL_CATEGORY_PRODUCT.equals(materialLot.getMaterialCategory()) && (StringUtils.isNullOrEmpty(materialLot.getRmaFlag()))
                            && (StringUtils.isNullOrEmpty(materialLot.getInferiorProductsFlag()) || validateProductLot(materialLot.getInferiorProductsFlag()))
                            && MaterialStatus.STATUS_CREATE.equals(materialLot.getPreStatusCategory())
                            && MaterialStatus.STATUS_RECEIVE.equals(materialLot.getPreStatus())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(productMLots)){
                List<String> productMLotIds = productMLots.stream().map(materialLot -> materialLot.getMaterialLotId()).collect(Collectors.toList());
                mesService.receiveFinishGood(productMLotIds);
            }

            //次品第一次入库回传消息给MES
            List<MaterialLot> rejProductMLots = materialLots.stream().filter(materialLot ->
                    Material.MATERIAL_CATEGORY_PRODUCT.equals(materialLot.getMaterialCategory()) && (StringUtils.isNullOrEmpty(materialLot.getRmaFlag()))
                            && (validateRaLot(materialLot.getInferiorProductsFlag()) || validateRejLot(materialLot.getInferiorProductsFlag()))
                            && MaterialStatus.STATUS_CREATE.equals(materialLot.getPreStatusCategory())
                            && MaterialStatus.STATUS_RECEIVE.equals(materialLot.getPreStatus())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(rejProductMLots)){
                List<String> rejProductMLotIds = rejProductMLots.stream().map(materialLot -> materialLot.getMaterialLotId()).collect(Collectors.toList());
                mesService.receiveInferiorProduct(rejProductMLotIds);
            }

        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 入库动作回传ERP
     * @param materialLots
     * @param materialLotActions
     * @throws ClientException
     */
    public void stockInSendErp(List<MaterialLot> materialLots, List<MaterialLotAction> materialLotActions)throws ClientException{
        try {
            List<String> productMLotIds = materialLots.stream().filter(mLot -> Material.MATERIAL_CATEGORY_PRODUCT.equals(mLot.getMaterialCategory())).map(mLot -> mLot.getMaterialLotId()).collect(Collectors.toList());
            List<MaterialLotUnit> materialLotUnitList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(productMLotIds)){
                materialLotUnitList = materialLotUnitRepository.findByMaterialLotIdIn(productMLotIds);
            }
            List<MaterialLot> incomingMLots = Lists.newArrayList();
            List<MaterialLot> procutMLots = Lists.newArrayList();
            List<MaterialLot> rmaIncoming2MLots = Lists.newArrayList();
            List<MaterialLot> departmentReturnMLots = Lists.newArrayList();
            List<MaterialLot> stockTransferMLots = Lists.newArrayList();

            Map<String, List<MaterialLot>> materialNameMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot::getMaterialName));
            for (String materialName : materialNameMap.keySet()) {
                Material material = mmsService.getMaterialByName(materialName, true);

                if (this.validateRAMLot(material.getName(), null)) {
                    continue;
                }

                List<MaterialLot> materialLotList = materialNameMap.get(materialName);
                for (MaterialLot materialLot : materialLotList) {
                    if (material instanceof RawMaterial){
                        //1.主材/辅材来料入库
                        if ((MaterialStatus.STATUS_CREATE.equals(materialLot.getPreStatusCategory()) && (MaterialStatus.STATUS_RECEIVE.equals(materialLot.getPreStatus()))) ||
                                (MaterialStatus.STATUS_IQC.equals(materialLot.getPreStatusCategory()) && MaterialStatus.STATUS_APPROVE.equals(materialLot.getPreStatus()))){
                            incomingMLots.add(materialLot);
                        }else if (MaterialStatus.STATUS_RETURN.equals(materialLot.getPreStatusCategory())&& MaterialStatus.STATUS_RETURN.equals(materialLot.getPreStatus())){
                            //2.部门退料入库
                            departmentReturnMLots.add(materialLot);
                        }else if (MaterialStatus.STATUS_RETURN.equals(materialLot.getPreStatusCategory()) && MaterialStatus.STATUS_RECEIVE.equals(materialLot.getPreStatus())){
                            //3.产线退料入库
                            //退料原因：'转BIN退料'不需上报ERP
                            if(RETURN_GRADE_REASON.equals(materialLot.getReturnReason())){
                                continue;
                            }
                            stockTransferMLots.add(materialLot);
                        }
                    }else if (material instanceof Product){
                        List<MaterialLotUnit> materialLotUnits = materialLotUnitList.stream().filter(materialLotUnit -> materialLotUnit.getMaterialLotId().equals(materialLot.getMaterialLotId())).collect(Collectors.toList());
                        materialLot.setMaterialLotUnits(materialLotUnits);

                        if (StringUtils.isNullOrEmpty(materialLot.getRmaFlag()) && MaterialStatus.STATUS_CREATE.equals(materialLot.getPreStatusCategory())&& MaterialStatus.STATUS_RECEIVE.equals(materialLot.getPreStatus())){
                            //1.产线生产入库

                            //重测不需上报ERP
                            if (validateReTest(materialLot.getRetestFlag())){
                                continue;
                            }
                            if (validateRaLot(materialLot.getInferiorProductsFlag())){
                                continue;
                            }
                            procutMLots.add(materialLot);
                        }else if (!StringUtils.isNullOrEmpty(materialLot.getRmaFlag()) && RMA_TYPE_NOT_SELF_REASON.equals(materialLot.getRmaFlag()) &&
                                (MaterialStatus.STATUS_CREATE.equals(materialLot.getPreStatusCategory()) && (MaterialStatus.STATUS_RECEIVE.equals(materialLot.getPreStatus()))||
                                        (MaterialStatus.STATUS_IQC.equals(materialLot.getPreStatusCategory()) && MaterialStatus.STATUS_APPROVE.equals(materialLot.getPreStatus())))){
                            //2.RMA非自身原因来料入库
                            rmaIncoming2MLots.add(materialLot);
                        }else if (MaterialStatus.STATUS_RETURN.equals(materialLot.getPreStatusCategory())&& MaterialStatus.STATUS_RETURN.equals(materialLot.getPreStatus())){
                            //3.部门退料入库
                            departmentReturnMLots.add(materialLot);
                        }else if (MaterialStatus.STATUS_RETURN.equals(materialLot.getPreStatusCategory()) && MaterialStatus.STATUS_RECEIVE.equals(materialLot.getPreStatus())){
                            //4.产线退料入库-调拨接口
                            stockTransferMLots.add(materialLot);
                        }
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(incomingMLots)){
                List<MaterialLot> mainMaterialLotList = incomingMLots.stream().filter(mLot -> Material.MATERIAL_CATEGORY_MAIN_MATERIAL.equals(mLot.getMaterialCategory())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(mainMaterialLotList)){
                    erpService.backhaulMainMaterialStockIn(mainMaterialLotList);
                }

                List<MaterialLot> materialLotList = incomingMLots.stream().filter(mLot -> !Material.MATERIAL_CATEGORY_MAIN_MATERIAL.equals(mLot.getMaterialCategory())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(materialLotList)){
                    Map<String, List<MaterialLot>> incomingDocIdMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot::getIncomingDocId));
                    for (String incomingDocId : incomingDocIdMap.keySet()) {
                        List<MaterialLot> materialLotByIncomingDocIdList = incomingDocIdMap.get(incomingDocId);
                        erpService.backhaulIncomingStockIn(materialLotByIncomingDocIdList);
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(procutMLots)){
                Map<String, List<MaterialLot>> materialAndWarehouseMap = procutMLots.stream().collect(Collectors.groupingBy(mLot -> mLot.getMaterialName() + StringUtils.SPLIT_CODE + mLot.getLastWarehouseId()));
                for (String materialAndWarehouse : materialAndWarehouseMap.keySet()) {
                    List<MaterialLot> erpProductAndRejMLots = materialAndWarehouseMap.get(materialAndWarehouse);
                    erpService.backhaulStockIn(erpProductAndRejMLots);
                }
            }

            if (CollectionUtils.isNotEmpty(rmaIncoming2MLots)){
                erpService.backhaulMainMaterialStockIn(rmaIncoming2MLots);
            }

            if (CollectionUtils.isNotEmpty(departmentReturnMLots)){
                String lastDocId = departmentReturnMLots.get(0).getLastDocId();

                Document document = documentService.getDocumentByName(lastDocId, true);
                erpService.backhaulDepartmentIssueOrReturn(departmentReturnMLots, IssueOrReturnRequestItem.RETURN_BWART, document.getReserved2());
            }

            if (CollectionUtils.isNotEmpty(stockTransferMLots)){
                for (MaterialLotAction materialLotAction : materialLotActions) {
                    materialLotAction.setFromWarehouseId(TRANSFER_MES_WAREHOUSE);
                }
                erpService.backhaulStockTransfer(stockTransferMLots, materialLotActions);
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 部门发料 上报ERP
     * @param applicationEvent
     * @throws ClientException
     */
    @EventListener(DepartmentIssueMLotApplicationEvent.class)
    public void departmentIssueMLot(DepartmentIssueMLotApplicationEvent applicationEvent) throws ClientException{
        try {
            Document document = applicationEvent.getDocument();
            List<MaterialLot> materialLotList = applicationEvent.getMaterialLots();
            List<MaterialLotAction> materialLotActions = applicationEvent.getMaterialLotActions();

            if (document instanceof IssueByMLotOrder || document instanceof IssueByMaterialOrder){
                Map<String, List<MaterialLot>> materialNameMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot::getMaterialName));
                for (String materialName : materialNameMap.keySet()) {
                    Material material = mmsService.getMaterialByName(materialName, true);
                    if (material instanceof LabMaterial) {
                        List<MaterialLot> materialLots = materialNameMap.get(materialName);
                        materialLotList.removeAll(materialLots);
                    }
                }
                for (MaterialLot materialLot : materialLotList) {

                    materialLotRepository.getEntityManager().detach(materialLot);

                    Optional<MaterialLotAction> materialLotActionFirst = materialLotActions.stream().filter(materialLotAction -> materialLotAction.getMaterialLotId().equals(materialLot.getMaterialLotId())).findFirst();
                    MaterialLotAction materialLotAction = materialLotActionFirst.get();
                    materialLot.setCurrentQty(materialLotAction.getTransQty());
                }
                erpService.backhaulDepartmentIssueOrReturn(materialLotList, IssueOrReturnRequestItem.ISSUE_BWART, document.getReserved2());
            }

        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public List<MaterialLot> getReservedMLotByOrder(String documentLineId)throws ClientException{
        try {
            DocumentLine docLine = documentLineRepository.findByLineId(documentLineId);
            List<MaterialLot> materialLots = Lists.newArrayList();

            String reservedRule = docLine.getReserved24();
            if (StringUtils.isNullOrEmpty(reservedRule)){
                materialLots = documentService.getReservedMLotByDocId(docLine.getLineId());
                return materialLots;
            }

            materialLots = materialLotRepository.findByStatus(MaterialStatus.STATUS_IN);
            if (CollectionUtils.isEmpty(materialLots)){
                return null;
            }
            Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByMLotDocRule(materialLots, reservedRule);

            List<DocumentLine> documentLineList = Lists.newArrayList();
            documentLineList.add(docLine);
            Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMLotDocRule(documentLineList, reservedRule);
            for (String key : documentLineMap.keySet()) {
                if (!materialLotMap.keySet().contains(key)) {
                    return null;
                }
                materialLots = materialLotMap.get(key);
            }
            materialLots = materialLots.stream().sorted(Comparator.comparing(MaterialLot::getReceiveDate)).collect(Collectors.toList());
            return materialLots;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 备货
     * @param documentLineId
     * @return
     * @throws ClientException
     */
    public void stockUpMLot(String documentLineId, List<MaterialLot> materialLots)throws ClientException{
        try {
            DocumentLine documentLine = documentService.getDocumentLineByLineId(documentLineId, true);

            materialLots = materialLots.stream().map(mLot -> mmsService.getMLotByMLotId(mLot.getMaterialLotId(), true)).collect(Collectors.toList());
            BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot::getCurrentQty));
            if (documentLine.getUnReservedQty().compareTo(totalQty) < 0) {
                throw new ClientParameterException(VanchipExceptions.RESERVED_OVER_QTY, totalQty);
            }
            documentLine.setUnReservedQty(documentLine.getUnReservedQty().subtract(totalQty));
            documentLine.setReservedQty(documentLine.getReservedQty().add(totalQty));
            documentLine = (DocumentLine)documentLineRepository.save(documentLine);

            List<DocumentMLot> documentMLots = documentMLotRepository.findByStatus(DocumentMLot.STATUS_CREATE);

            for (MaterialLot materialLot : materialLots) {
                materialLot.validateMLotHold();

                materialLot.setLastDocRrn(documentLine.getObjectRrn());
                materialLot.setLastDocId(documentLine.getLineId());
                materialLot.setItemId(documentLine.getReserved30());
                baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_RESERVED);

                Optional<DocumentMLot> documentMLotOptional = documentMLots.stream().filter(documentMLot -> documentMLot.getMaterialLotId().equals(materialLot.getMaterialLotId())).findFirst();
                if (documentMLotOptional.isPresent()){
                    DocumentMLot documentMLot = documentMLotOptional.get();
                    if (documentMLot.getDocumentId().equals(documentLineId)){
                        continue;
                    }else {
                        throw new ClientParameterException(MATERIAL_LOT_ALREADY_BOUND_ORDER, materialLot.getMaterialLotId());
                    }
                }
                documentService.createDocumentMLot(documentLineId, materialLot.getMaterialLotId());
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获得已备货物料批次
     * @param docId
     * @throws ClientException
     */
    public List<MaterialLot> getStockUpMLot(String docId)throws ClientException{
        try {
            List<MaterialLot> materialLots =Lists.newArrayList();

            List<DocumentLine> documentLineList = documentLineRepository.findByDocId(docId);
            if (CollectionUtils.isNotEmpty(documentLineList)){
                for (DocumentLine docLine : documentLineList) {
                    List<MaterialLot> reservedMLotByDocId = documentService.getReservedMLotByDocId(docLine.getLineId());
                    materialLots.addAll(reservedMLotByDocId);
                }
            }else {
                materialLots = documentService.getReservedMLotByDocId(docId);
            }

            return materialLots;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 取消备货批次
     * @param documentLineId
     * @return
     * @throws ClientException
     */
    public void getUnStockUpMLot(String documentLineId, List<MaterialLot> materialLotList)throws ClientException{
        try {
            DocumentLine documentLine = documentService.getDocumentLineByLineId(documentLineId, true);

            List<DocumentMLot> documentMLotList = documentMLotRepository.findByDocumentId(documentLine.getLineId());
            List<String> materialLotIdList = materialLotList.stream().map(mLot -> mLot.getMaterialLotId()).collect(Collectors.toList());
            List<MaterialLot> materialLots = documentService.validationDocReservedMLot(documentLineId, materialLotIdList);
            for (MaterialLot materialLot : materialLots) {
                Optional<DocumentMLot> documentMLotOptional = documentMLotList.stream().filter(docMLot -> docMLot.getMaterialLotId().equals(materialLot.getMaterialLotId())).findFirst();
                if (documentMLotOptional.isPresent()) {
                    DocumentMLot documentMLot = documentMLotOptional.get();
                    documentMLotRepository.delete(documentMLot);
                }
                materialLot.setLastDocRrn("");
                materialLot.setLastDocId("");
                materialLot.setItemId("");
                baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_UNRESERVED);
            }

            BigDecimal totalQty = materialLotList.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot::getCurrentQty));
            documentLine.setUnReservedQty(documentLine.getUnReservedQty().subtract(totalQty));
            documentLine.setReservedQty(documentLine.getReservedQty().add(totalQty));
            documentLine = (DocumentLine)documentLineRepository.save(documentLine);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 仓库退供应商/ERP
     * @param documentId
     * @param materialLotList
     * @throws ClientException
     */
    public void returnMLotByDocLine(String documentId, List<MaterialLot> materialLotList) throws ClientException {
        try {
            BigDecimal handleQty = materialLotList.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));
            Document document = documentService.saveDocument(documentId, handleQty, DocumentHistory.TRANS_TYPE_RETURN, Document.STATUS_APPROVE);

            List<MaterialLot> mLotList = Lists.newArrayList();
            List<DocumentLine> documentLines = documentLineRepository.findByDocId(documentId);
            for (DocumentLine documentLine : documentLines) {
                List<String> materialLotIdList = materialLotList.stream().map(mLot -> mLot.getMaterialLotId()).collect(Collectors.toList());
                List<MaterialLot> materialLots = documentService.getReservedMLotByDocId(documentLine.getLineId());

                BigDecimal docLineQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));
                documentLine.setHandledQty(documentLine.getHandledQty().add(docLineQty));
                documentLine.setUnHandledQty(documentLine.getUnHandledQty().subtract(docLineQty));
                baseService.saveEntity(documentLine);

                List<String> materialLotIds = materialLots.stream().map(mLot -> mLot.getMaterialLotId()).collect(Collectors.toList());
                documentService.changeDocMLotStatus(documentLine.getLineId(), materialLotIds, DocumentMLot.STATUS_RETURN);

                for (MaterialLot materialLot : materialLots) {
                    mLotList.add(materialLot);
                    materialLot.setReservedQty(materialLot.getCurrentQty());
                    materialLot.setLastDocumentInfo(document);
                    mmsService.returnMaterialLot(materialLot);
                }
            }

            MaterialLot materialLot = mLotList.get(0);
            if(Material.MATERIAL_CATEGORY_MAIN_MATERIAL.equals(materialLot.getMaterialCategory()) || Material.MATERIAL_CATEGORY_PRODUCT.equals(materialLot.getMaterialCategory())){
                //客供料退或接口-待测品
                erpService.backhaulReturnMainMaterial(document.getName(), mLotList);
            }else {
                //采购退货接口-辅材
                erpService.backhaulReturnMLot(document.getName(), mLotList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialLot validateReservedMLot(String docLineId, String materialLotId) throws ClientException{
        try {
            DocumentLine documentLine = documentLineRepository.findByLineId(docLineId);
            MaterialLot materialLot = null;
            if (StringUtils.isNullOrEmpty(documentLine.getReserved24())){
                List<MaterialLot> materialLotList = documentService.getReservedMLotByDocId(documentLine.getDocId());
                Optional<MaterialLot> materialLotFirst = materialLotList.stream().filter(mLot -> mLot.getMaterialLotId().equals(materialLotId)).findFirst();
                if (!materialLotFirst.isPresent()){
                    throw new ClientParameterException(DocumentException.MATERIAL_LOT_NOT_BOUND_ORDER, materialLotId);
                }
                materialLot = materialLotFirst.get();
            }else {
                materialLot = mmsService.getMLotByMLotId(materialLotId, true);
                validateMLotAndDocLineByRule(documentLine, materialLot, documentLine.getReserved24());
            }

            materialLot.setReserved45(documentLine.getLineId());
            return materialLot;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 报废
     * @param docId
     * @param materialLotList
     * @throws ClientException
     */
    public void scrapMLot(String docId, List<MaterialLot> materialLotList) throws ClientException{
        try {
            BigDecimal handleQty = materialLotList.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));
            Document document = documentService.saveDocument(docId, handleQty, DocumentHistory.TRANS_TYPE_SCRAP);

            List<MaterialLot> mLotList = Lists.newArrayList();

            List<String> productMLotIdList = materialLotList.stream()
                    .filter(mLot -> Material.MATERIAL_CATEGORY_PRODUCT.equals(mLot.getMaterialCategory()) && validateProductLot(mLot.getInferiorProductsFlag()))
                    .map(MaterialLot::getMaterialLotId).collect(Collectors.toList());
            List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotIdIn(productMLotIdList);

            List<DocumentLine> documentLines = documentLineRepository.findByDocId(document.getName());
            BigDecimal handledQty = BigDecimal.ZERO;
            for (DocumentLine documentLine : documentLines) {
                List<MaterialLot> reservedMaterialLots = documentService.getReservedMLotByDocId(documentLine.getLineId());
                List<String> materialLotIdList = reservedMaterialLots.stream().map(mLot -> mLot.getMaterialLotId()).collect(Collectors.toList());
                documentService.changeDocMLotStatus(documentLine.getLineId(), materialLotIdList, DocumentMLot.STATUS_SCRAP);

                for (MaterialLot materialLot : materialLotList) {
                    Optional<MaterialLot> materialLotOptional = reservedMaterialLots.stream().filter(mLot -> mLot.getMaterialLotId().equals(materialLot.getMaterialLotId())).findFirst();
                    if (!materialLotOptional.isPresent()) {
                        throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, materialLot.getMaterialLotId());
                    }
                    MaterialLot mLot = materialLotOptional.get();

                    handledQty = handledQty.add(mLot.getCurrentQty());
                    List<MaterialLotUnit> materialLotUnitList = materialLotUnits.stream().filter(unit -> unit.getMaterialLotId().equals(materialLot.getMaterialLotId())).collect(Collectors.toList());
                    mLot.setMaterialLotUnits(materialLotUnitList);
                    MaterialLot materialLotClone = (MaterialLot)mLot.clone();
                    mLotList.add(materialLotClone);

                    mLot.setLastDocumentInfo(document);
                    mmsService.scrapMLot(mLot);
                }

                documentLine.setUnHandledQty(documentLine.getUnHandledQty().subtract(handledQty));
                documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
                baseService.saveEntity(documentLine);
            }
            erpService.backhaulScrap(docId, mLotList);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    @EventListener
    public void SplitMLotEventListener(SplitMLotApplicationEvent applicationEvent) throws ClientException{
        try {
            MaterialLot materialLot = applicationEvent.getMaterialLot();
            erpService.splitMLot(materialLot);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 更新重测批次的销售订单
     * @param materialLots
     * @throws ClientException
     */
    public void updateERPSo(List<MaterialLot> materialLots) throws ClientException{
        try {
            Map<String, List<MaterialLot>> materialLotIdMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot::getMaterialLotId));

            for (String materialLotId : materialLotIdMap.keySet()) {
                List<MaterialLot> materialLotList = materialLotIdMap.get(materialLotId);
                if (materialLotId == StringUtils.EMPTY){
                    for (MaterialLot materialLot : materialLotList) {
                        MaterialLot mLotByMLotId = mmsService.getMLotByMLotId(materialLotId);
                        mLotByMLotId.setReserved7(materialLot.getReserved7());
                        mLotByMLotId.setReserved15(materialLot.getReserved15());
                        mLotByMLotId.setReserved6(materialLot.getReserved6());
                        mLotByMLotId.setReserved54(materialLot.getReserved54());
                        baseService.saveEntity(mLotByMLotId, TRANS_TYPE_UPDATE_SO);
                    }
                }else {
                    MaterialLot materialLot = materialLotList.get(0);
                    MaterialLot mLotByMLotId = mmsService.getMLotByMLotId(materialLotId);
                    mLotByMLotId.setReserved7(materialLot.getReserved7());
                    mLotByMLotId.setReserved15(materialLot.getReserved15());
                    mLotByMLotId.setReserved6(materialLot.getReserved6());
                    mLotByMLotId.setReserved54(materialLot.getReserved54());
                    baseService.saveEntity(mLotByMLotId, TRANS_TYPE_UPDATE_SO);
                }
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证是否RA信息
     * @param materialName
     * @param materialLotId
     * @return
     * @throws ClientException
     */
    public Boolean validateRAMLot(String materialName, String materialLotId) throws ClientException{
        try {
            Boolean raFlag = false;
            //1.物料名称包含"RA"
            if (!StringUtils.isNullOrEmpty(materialName) && materialName.contains(MATERIAL_NAME_RA)) {
                raFlag = true;
            }
            //2.物料批次名称包含"RA-"
            if (!StringUtils.isNullOrEmpty(materialLotId) && materialLotId.length() > 3 && PRE_RA_MLOT_ID.equals(materialLotId.substring(0, 3))) {
                raFlag = true;
            }
            return raFlag;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 截取生产订单'-'之前的
     * @param workOrderId
     * @return
     * @throws ClientException
     */
    public String subStringWorkOrderId(String workOrderId) throws ClientException{
        try {
            //Pattern.compile("");
            if (!StringUtils.isNullOrEmpty(workOrderId)) {
                workOrderId = workOrderId.substring(workOrderId.indexOf(StringUtils.SPLIT_CODE));
            }
            return workOrderId;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 判断批次是否RA批次
     * @return
     */
    public boolean validateRaLot(String inferiorProductsFlag){
        return INFERIOR_PRODUCTS_FLAG_R.equals(inferiorProductsFlag);
    }

    /**
     * 判断批次是否次品批次
     * @return
     */
    public boolean validateRejLot(String inferiorProductsFlag){
        return StringUtils.YES.equals(inferiorProductsFlag);
    }

    /**
     * 判断批次是否成品批次
     * @param inferiorProductsFlag
     * @return
     */
    public boolean validateProductLot(String inferiorProductsFlag){
        return StringUtils.NO.equals(inferiorProductsFlag);
    }

    /**
     * 判断是否重测标识批次
     * @param reTestFlag
     * @return
     */
    public boolean validateReTest(String reTestFlag){
        return StringUtils.YES.equals(reTestFlag);
    }

    /**
     * 修改生产日期
     * @param materialLotId
     * @param iclDateValue
     * @return
     * @throws ClientException
     */
    public void updateProductionDate(String materialLotId, String iclDateValue) throws ClientException{
        try {
            SimpleDateFormat formatter = new SimpleDateFormat( "yyyy/MM/dd"+StringUtils.BLANK_SPACE+DateUtils.DEFAULT_TIME_PATTERN);

            MaterialLot mLotByMLotId = mmsService.getMLotByMLotId(materialLotId);
            if (mLotByMLotId == null){
                return;
            }
            if (!StringUtils.isNullOrEmpty(iclDateValue)){
                Date productionDate = formatter.parse(iclDateValue);
                Date expireDate = mmsService.calculateTargetDate(productionDate, mLotByMLotId.getEffectiveLife(), mLotByMLotId.getEffectiveUnit());

                mLotByMLotId.setProductionDate(productionDate);
                mLotByMLotId.setExpireDate(expireDate);
                baseService.saveEntity(mLotByMLotId, TRANS_TYPE_UPDATE_PRODUCTION_DATE);
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

}
