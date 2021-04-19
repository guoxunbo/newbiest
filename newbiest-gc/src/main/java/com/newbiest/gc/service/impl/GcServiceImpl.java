package com.newbiest.gc.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.model.NBQuery;
import com.newbiest.base.repository.QueryRepository;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.service.VersionControlService;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.*;
import com.newbiest.commom.sm.exception.StatusMachineExceptions;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.common.exception.ContextException;
import com.newbiest.gc.GcExceptions;
import com.newbiest.gc.model.*;
import com.newbiest.gc.repository.*;
import com.newbiest.gc.scm.send.mlot.state.MaterialLotStateReportRequestBody;
import com.newbiest.gc.service.GcService;
import com.newbiest.gc.service.MesService;
import com.newbiest.gc.service.ScmService;
import com.newbiest.mms.SystemPropertyUtils;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.MaterialLotUnitService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.mms.state.model.MaterialEvent;
import com.newbiest.mms.state.model.MaterialStatus;
import com.newbiest.mms.state.model.MaterialStatusModel;
import com.newbiest.mms.utils.CollectorsUtils;
import freemarker.template.utility.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.Transformers;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.common.idgenerator.service.GeneratorService;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.newbiest.mms.exception.MmsException.MM_RAW_MATERIAL_IS_NOT_EXIST;
import static com.newbiest.mms.exception.MmsException.MM_PRODUCT_ID_IS_NOT_EXIST;

/**
 * Created by guoxunbo on 2019-08-21 12:41
 */
@Service
@Slf4j
@Transactional
public class GcServiceImpl implements GcService {

    public static final String TRANS_TYPE_BIND_RELAY_BOX = "BindRelayBox";
    public static final String TRANS_TYPE_UNBIND_RELAY_BOX = "UnbindRelayBox";
    public static final String TRANS_TYPE_JUDGE = "Judge";
    public static final String TRANS_TYPE_OQC = "OQC";
    public static final String TRANS_TYPE_UPDATE_TREASURY_NOTE = "UpdateTreasuryNote";
    public static final String TRANS_TYPE_UPDATE_LOCATION = "UpdateLocation";
    public static final String TRANS_TYPE_TRANSFER_WAREHOUSE = "TransferWarehouse";

    public static final String REFERENCE_NAME_STOCK_OUT_CHECK_ITEM_LIST = "StockOutCheckItemList";
    public static final String REFERENCE_NAME_WLTSTOCK_OUT_CHECK_ITEM_LIST = "WltStockOutCheckItemList";
    public static final String REFERENCE_NAME_PACK_CASE_CHECK_ITEM_LIST = "PackCaseCheckItemList";
    public static final String REFERENCE_NAME_WLTPACK_CASE_CHECK_ITEM_LIST = "WltPackCaseCheckItemList";
    public static final String REFERENCE_NAME_PRODUCT_DECS_LIST = "ProductDescList";
    public static final String REFERENCE_NAME_ENCRYPTION_SUBCODE_LIST = "EncryptionSubcodeList";

    public static final String EVENT_OQC = "OQC";

    public static final String WAREHOUSE_SH = "SH_STOCK";
    public static final String WAREHOUSE_ZJ = "ZJ_STOCK";
    public static final String WAREHOUSE_HK = "HK_STOCK";

    public static final String BONDED_PROPERTITY_SH = "SH";
    public static final String BONDED_PROPERTITY_ZSH = "ZSH";
    public static final String BONDED_PROPERTITY_HK = "HK";

    @Autowired
    MesPackedLotRepository mesPackedLotRepository;

    @Autowired
    MesPackedLotRelationRepository mesPackedLotRelationRepository;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialLotHistoryRepository materialLotHistoryRepository;

    @Autowired
    MmsService mmsService;

    @Autowired
    MaterialLotUnitService materialLotUnitService;

    @Autowired
    BaseService baseService;

    @Autowired
    UIService uiService;

    @Autowired
    GeneratorService generatorService;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    ErpSoRepository erpSoRepository;

    @Autowired
    ErpMaterialOutOrderRepository erpMaterialOutOrderRepository;

    @Autowired
    ErpMaterialOutAOrderRepository erpMaterialOutAOrderRepository;

    @Autowired
    ErpSoaOrderRepository erpSoaOrderRepository;

    @Autowired
    OtherStockOutOrderRepository otherStockOutOrderRepository;

    @Autowired
    MaterialIssueOrderRepository materialIssueOrderRepository;

    @Autowired
    ErpSobOrderRepository erpSobOrderRepository;

    @Autowired
    OtherShipOrderRepository otherShipOrderRepository;

    @Autowired
    DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    ReTestOrderRepository reTestOrderRepository;

    @Autowired
    WaferIssueOrderRepository waferIssueOrderRepository;

    @Autowired
    ReceiveOrderRepository receiveOrderRepository;

    @Autowired
    CogReceiveOrderRepository cogReceiveOrderRepository;

    @Autowired
    DocumentLineRepository documentLineRepository;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    DocumentHistoryRepository documentHistoryRepository;

    @Autowired
    DocumentLineHistoryRepository documentLineHistoryRepository;

    @Autowired
    MaterialLotJudgeHisRepository materialLotJudgeHisRepository;

    @Autowired
    CheckHistoryRepository checkHistoryRepository;

    @Autowired
    MaterialLotInventoryRepository materialLotInventoryRepository;

    @Autowired
    QueryRepository queryRepository;

    @Autowired
    EntityManager em;

    @Autowired
    RawMaterialRepository rawMaterialRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    VersionControlService versionControlService;

    @Autowired
    MaterialStatusModelRepository materialStatusModelRepository;

    @Autowired
    PackageService packageService;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ErpMoRepository erpMoRepository;

    @Autowired
    MaterialLotUnitRepository materialLotUnitRepository;

    @Autowired
    MaterialLotUnitHisRepository materialLotUnitHisRepository;

    @Autowired
    MesWaferReceiveRepository mesWaferReceiveRepository;

    @Autowired
    MesWaferReceiveHisRepository mesWaferReceiveHisRepository;

    @Autowired
    GCProductSubcodeSetRepository gcProductSubcodeSetRepository;

    @Autowired
    GCProductModelConversionRepository gcProductModelConversionRepository;

    @Autowired
    PackagedLotDetailRepository packagedLotDetailRepository;

    @Autowired
    GCLcdCogDetialRepository gcLcdCogDetialRepository;

    @Autowired
    GCLcdCogDetialHisRepository gcLcdCogDetialHisRepository;

    @Autowired
    ErpInStockRepository erpInStockRepository;

    @Autowired
    ErpMoaRepository erpMoaRepository;

    @Autowired
    ScmService scmService;

    @Autowired
    MesService mesService;

    @Autowired
    GCProductRelationRepository productRelationRepository;

    @Autowired
    GCOutSourcePoRepository outSourcePoRepository;

    @Autowired
    GCOutSourcePoHisRepository outSourcePoHisRepository;

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    GCWorkorderRelationRepository workorderRelationRepository;

    @Autowired
    GCWorkorderRelationHisRepository workorderRelationHisRepository;

    @Autowired
    MLotDocRuleRepository mLotDocRuleRepository;

    @Autowired
    GCProductNumberRelationRepository productNumberRelationRepository;

    @Autowired
    GCProductWeightRelationRepository productWeightRelationRepository;

    @Autowired
    GCProductNumberRelationHisRepository productNumberRelationHisRepository;

    @Autowired
    GCUnConfirmWaferSetRepository unConfirmWaferSetRepository;

    @Autowired
    GCUnConfirmWaferSetHisRepository unConfirmWaferSetHisRepository;

    @Autowired
    ErpMaterialOutRepository erpMaterialOutRepository;

    @Autowired
    ErpMaterialInRepository erpMaterialInRepository;

    @Autowired
    MaterialNameInfoRepository materialNameInfoRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    GCFutureHoldConfigRepository futureHoldConfigRepository;


    /**
     * 根据单据和动态表RRN获取可以被备货的批次
     * @param
     * @param
     * @return
     */
    public List<MaterialLot> getWaitForReservedMaterialLot(Long documentLineRrn, Long tableRrn)  throws ClientException {
        try {
            List<MaterialLot> waitForReservedMaterialLots = Lists.newArrayList();
            DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLineRrn);
            if (documentLine.getUnReservedQty().compareTo(BigDecimal.ZERO) > 0) {
                NBTable nbTable = uiService.getDeepNBTable(tableRrn);
                StringBuffer whereClause = new StringBuffer();
                if (!StringUtils.isNullOrEmpty(nbTable.getWhereClause())) {
                    whereClause.append(nbTable.getWhereClause());
                    whereClause.append(" AND ");
                }
                whereClause.append(" reserved16 is null");
                whereClause.append(" AND ");
                whereClause.append(" materialName = '" + documentLine.getMaterialName() + "'");
                whereClause.append(" AND ");
                whereClause.append(" grade ='" + documentLine.getReserved3() + "'");

                List<MaterialLot> materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause.toString(), "");
                if (CollectionUtils.isNotEmpty(materialLots)) {
                    for (MaterialLot materialLot : materialLots) {
                        try {
                            validateMLotAndDocLineByRule(documentLine, materialLot, MaterialLot.MLOT_RESERVED_DOC_VALIDATE_RULE_ID);
                            if(nbTable.getName().equals("MMReservedCase")){
                                List<String> packedLotIdList = new ArrayList<String>();
                                packedLotIdList.add(materialLot.getMaterialLotId());
                                List<MaterialLot> packedMaterialLots = getPackedDetailsAndNotReserved(packedLotIdList);
                                if(CollectionUtils.isNotEmpty(packedMaterialLots)){
                                    waitForReservedMaterialLots.add(materialLot);
                                }
                            }else{
                                waitForReservedMaterialLots.add(materialLot);
                            }
                        } catch (Exception e) {
                            // 验证不过 Do nothing。
                        }
                    }
                }
            } else {
                throw new ClientException("");
            }
            return waitForReservedMaterialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * @param packedLotIdList
     * @return
     */
    public List<MaterialLot> getPackedDetailsAndNotReserved(List<String> packedLotIdList) throws ClientException{
        return materialLotRepository.getPackedDetailsAndNotReserved(packedLotIdList);
    }

    /**
     * 备货物料批次 不管是真空包还是箱
     * @param documentLineRrn
     * @param materialLotActions
     * @return
     * @throws ClientException
     */
    public DocumentLine reservedMaterialLot(Long documentLineRrn, List<MaterialLotAction> materialLotActions ,String stockNote) throws ClientException {
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            List<MaterialLot> holdMaterialLot = materialLots.stream().filter(materialLot -> MaterialLot.HOLD_STATE_ON.equals(materialLot.getHoldState())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(holdMaterialLot)){
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_ALREADY_HOLD, holdMaterialLot.get(0).getMaterialLotId());
            }
            DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLineRrn);
            BigDecimal unReservedQty = documentLine.getUnReservedQty();
            BigDecimal reservedQty = BigDecimal.ZERO;
            for (MaterialLot materialLot : materialLots) {
                if (!StringUtils.isNullOrEmpty(materialLot.getReserved16())) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_RESERVED_BY_ANOTHER);
                }
                validateMLotAndDocLineByRule(documentLine, materialLot, MaterialLot.MLOT_RESERVED_DOC_VALIDATE_RULE_ID);
                BigDecimal currentQty = materialLot.getCurrentQty();
                reservedQty = reservedQty.add(currentQty);
                if (unReservedQty.compareTo(reservedQty) < 0) {
                    throw new ClientParameterException(GcExceptions.RESERVED_OVER_QTY);
                }
                materialLot.setReservedQty(materialLot.getCurrentQty());
                materialLot.setReserved16(documentLine.getObjectRrn().toString());
                materialLot.setReserved17(documentLine.getDocId());
                materialLot.setReserved18(stockNote);

                materialLot.setDocDate(documentLine.getErpCreated());
                materialLot.setShipper(documentLine.getReserved12());
                materialLot.setReserved51(documentLine.getReserved15());
                materialLot.setReserved52(documentLine.getReserved20());
                materialLot.setReserved53(documentLine.getReserved21());
                materialLot = materialLotRepository.saveAndFlush(materialLot);
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_RESERVED);
                materialLotHistoryRepository.save(history);
            }

            Map<String, List<MaterialLot>> parentMaterialLots = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));

            for (String parentMLotId : parentMaterialLots.keySet()) {
                MaterialLot parentMLot = mmsService.getMLotByMLotId(parentMLotId);
                BigDecimal totalReservedQty = parentMaterialLots.get(parentMLotId).stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getReservedQty));
                BigDecimal parentMaterialLotReservedQty = parentMLot.getReservedQty() == null ? BigDecimal.ZERO : parentMLot.getReservedQty();
                parentMLot.setReservedQty(parentMaterialLotReservedQty.add(totalReservedQty));

                parentMLot.setReserved16(documentLine.getObjectRrn().toString());
                parentMLot.setReserved17(documentLine.getDocId());
                parentMLot.setReserved18(stockNote);

                parentMLot.setDocDate(documentLine.getErpCreated());
                parentMLot.setShipper(documentLine.getReserved12());
                parentMLot.setReserved51(documentLine.getReserved15());
                parentMLot.setReserved52(documentLine.getReserved20());
                parentMLot.setReserved53(documentLine.getReserved21());
                materialLotRepository.saveAndFlush(parentMLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(parentMLot, MaterialLotHistory.TRANS_TYPE_RESERVED);
                materialLotHistoryRepository.save(history);

            }

            documentLine.setUnReservedQty(unReservedQty.subtract(reservedQty));
            BigDecimal lineReservedQty = documentLine.getReservedQty() == null ? BigDecimal.ZERO : documentLine.getReservedQty();
            documentLine.setReservedQty(lineReservedQty.add(reservedQty));
            documentLine = documentLineRepository.saveAndFlush(documentLine);
            baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_RESERVED);

            DeliveryOrder deliveryOrder = (DeliveryOrder) deliveryOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            BigDecimal docReservedQty = deliveryOrder.getReservedQty() == null ? BigDecimal.ZERO : deliveryOrder.getReservedQty();
            deliveryOrder.setUnReservedQty(deliveryOrder.getUnReservedQty().subtract(reservedQty));
            deliveryOrder.setReservedQty(docReservedQty.add(reservedQty));
            deliveryOrder = deliveryOrderRepository.saveAndFlush(deliveryOrder);
            baseService.saveHistoryEntity(deliveryOrder, MaterialLotHistory.TRANS_TYPE_RESERVED);
            return documentLine;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void unReservedMaterialLot(List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            // 根据documentLine分组 依次还原数量
            Map<String, List<MaterialLot>> docLineReservedMaterialLotMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getReserved16));
            Map<Long, BigDecimal> docUnReservedQtyMap = Maps.newHashMap();

            //还原箱备货数量
            Map<String, List<MaterialLot>> parentMaterialLots = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));

            parentMaterialLots.keySet().forEach(parentMLotId -> {
                MaterialLot parentMLot = mmsService.getMLotByMLotId(parentMLotId);
                BigDecimal totalUnReservedQty = parentMaterialLots.get(parentMLotId).stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getReservedQty));
                parentMLot.setReservedQty(parentMLot.getReservedQty().subtract(totalUnReservedQty));
                parentMLot.setReserved19(StringUtils.EMPTY);
                parentMLot.setReserved20(StringUtils.EMPTY);

                if (parentMLot.getReservedQty().compareTo(BigDecimal.ZERO) == 0) {
                    parentMLot.clearReservedInfo();
                }
                materialLotRepository.saveAndFlush(parentMLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(parentMLot, MaterialLotHistory.TRANS_TYPE_UN_RESERVED);
                materialLotHistoryRepository.save(history);
            });

            for (String docLine : docLineReservedMaterialLotMap.keySet()) {
                List<MaterialLot> docLineReservedMaterialLots = docLineReservedMaterialLotMap.get(docLine);
                DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(docLine));
                BigDecimal unReservedQty = BigDecimal.ZERO;
                for (MaterialLot materialLot : docLineReservedMaterialLots) {
                    unReservedQty = unReservedQty.add(materialLot.getReservedQty());

                    materialLot.setReservedQty(BigDecimal.ZERO);
                    materialLot.clearReservedInfo();
                    materialLot = materialLotRepository.saveAndFlush(materialLot);
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_UN_RESERVED);
                    materialLotHistoryRepository.save(history);
                }
                documentLine.setReservedQty(documentLine.getReservedQty().subtract(unReservedQty));
                documentLine.setUnReservedQty(documentLine.getUnReservedQty().add(unReservedQty));
                documentLine = documentLineRepository.saveAndFlush(documentLine);
                baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_UN_RESERVED);

                // 还原主单据数量
                BigDecimal docUnReservedQty = BigDecimal.ZERO;
                if (docUnReservedQtyMap.containsKey(documentLine.getDocRrn())) {
                    docUnReservedQty = docUnReservedQtyMap.get(documentLine.getDocRrn());
                }
                docUnReservedQty.add(unReservedQty);
                docUnReservedQtyMap.put(documentLine.getDocRrn(), unReservedQty);
            }

            for (Long docRrn : docUnReservedQtyMap.keySet()) {
                DeliveryOrder deliveryOrder = (DeliveryOrder) deliveryOrderRepository.findByObjectRrn(docRrn);
                deliveryOrder.setUnReservedQty(deliveryOrder.getUnReservedQty().add(docUnReservedQtyMap.get(docRrn)));
                deliveryOrder.setReservedQty(deliveryOrder.getReservedQty().subtract(docUnReservedQtyMap.get(docRrn)));
                deliveryOrder = deliveryOrderRepository.saveAndFlush(deliveryOrder);

                baseService.saveHistoryEntity(deliveryOrder, MaterialLotHistory.TRANS_TYPE_UN_RESERVED);
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据物料批次号及表单主键获取物料批次信息，不存在的抛出异常
     * @param materialLotId
     * @return
     */
    public MaterialLot getMaterialLotByMaterialLotIdAndTableRrn(String materialLotId, long tableRrn) throws ClientException {
        try {
            MaterialLot materialLot = new MaterialLot();
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String _whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();
            StringBuffer clauseBuffer = new StringBuffer();
            clauseBuffer.append(" materialLotId = ");
            clauseBuffer.append("'" + materialLotId + "'");
            if (!StringUtils.isNullOrEmpty(_whereClause)) {
                clauseBuffer.append(" AND ");
                clauseBuffer.append(_whereClause);
            }
            _whereClause = clauseBuffer.toString();
            List<MaterialLot> materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);
            if(CollectionUtils.isEmpty(materialLots)){
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, materialLotId);
            } else {
                materialLot = materialLots.get(0);
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据物料批次号或者LOT_ID获取可以库位调整的物料批次信息
     * @param mLotId
     * @return materialLot
     */
    public MaterialLot getWaitStockInStorageMaterialLotByLotIdOrMLotId(String mLotId) throws ClientException{
        try {
            MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(mLotId,  ThreadLocalContext.getOrgRrn());
            if(materialLot == null){
                materialLot = materialLotRepository.findByLotIdAndStatusCategoryNotIn(mLotId, MaterialLot.STATUS_FIN);
            }
            if(materialLot == null){
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, mLotId);
            } else{
                materialLot.isFinish();
            }
            return materialLot;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取到可以入库的批次
     *  当前只验证了物料批次是否是完结
     * @param lotId
     * @return
     */
    public MaterialLot getWaitStockInStorageWaferByLotId(String lotId, Long tableRrn) throws ClientException {
        try {
            MaterialLot materialLot = new MaterialLot();
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String _whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();
            StringBuffer clauseBuffer = new StringBuffer();
            clauseBuffer.append(" lotId = ");
            clauseBuffer.append("'" + lotId + "'");

            if (!StringUtils.isNullOrEmpty(_whereClause)) {
                clauseBuffer.append(" AND ");
                clauseBuffer.append(_whereClause);
            }
            _whereClause = clauseBuffer.toString();
            List<MaterialLot> materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);

            if(CollectionUtils.isNotEmpty(materialLots)){
                materialLot = materialLots.get(0);
            }
            if (StringUtils.isNullOrEmpty(materialLot.getMaterialLotId())) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, lotId);
            }
            materialLot.isFinish();
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 入库位
     * @return
     */
    public void stockIn(List<StockInModel> stockInModels) throws ClientException {
        try {
            ThreadLocalContext.getSessionContext().buildTransInfo();
            Map<String, StockInModel> stockInModelMap = stockInModels.stream().collect(Collectors.toMap(StockInModel :: getMaterialLotId, Function.identity()));

            //1. 把箱批次和普通的物料批次区分出来
            List<MaterialLot> materialLots = stockInModels.stream().map(model -> mmsService.getMLotByMLotId(model.getMaterialLotId(), true)).collect(Collectors.toList());
            List<MaterialLot> packageMaterialLots = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getPackageType())).collect(Collectors.toList());
            List<MaterialLot> normalMaterialLots = materialLots.stream().filter(materialLot -> materialLot.getParentMaterialLotRrn() == null).collect(Collectors.toList());

            //2. 普通批次才做绑定中转箱功能，直接release原来的中转箱号
            for (MaterialLot materialLot : normalMaterialLots) {
                StockInModel stockInModel = stockInModelMap.get(materialLot.getMaterialLotId());
                // 为空则不处理
                if (StringUtils.isNullOrEmpty(stockInModel.getRelaxBoxId())) {
                    continue;
                }
                bindRelaxBox(Lists.newArrayList(materialLot), stockInModel.getRelaxBoxId());
            }
            //3. 入库
            for (MaterialLot materialLot : materialLots) {
                StockInModel stockInModel = stockInModelMap.get(materialLot.getMaterialLotId());
                String storageId = stockInModel.getStorageId();
                // 为空则不处理
                if (StringUtils.isNullOrEmpty(storageId)) {
                    continue;
                }
                if (StringUtils.isNullOrEmpty(materialLot.getReserved13())) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_WAREHOUSE_IS_NULL, materialLot.getMaterialLotId());
                }

                MaterialLotAction action = new MaterialLotAction();
                action.setTargetWarehouseRrn(Long.parseLong(materialLot.getReserved13()));
                action.setTargetStorageId(storageId);
                action.setTransQty(materialLot.getCurrentQty());
                action.setTransCount(materialLot.getCurrentSubQty());

                List<MaterialLotInventory> materialLotInvList = mmsService.getMaterialLotInv(materialLot.getObjectRrn());
                // 如果为空就是做入库事件 如果不是空则做转库事件
                if (CollectionUtils.isNotEmpty(materialLotInvList)) {
                    //GC一个批次只会入库一次
                    MaterialLotInventory materialLotInventory = materialLotInvList.get(0);
                    action.setFromWarehouseRrn(materialLotInventory.getWarehouseRrn());
                    action.setFromStorageRrn(materialLotInventory.getStorageRrn());
                    mmsService.transfer(materialLot, action);
                } else {
                    materialLot = mmsService.stockIn(materialLot, action);
                }
                materialLot.setReserved14(storageId);
                materialLotRepository.save(materialLot);

                //如果箱号入库位，将箱中所有真空包或Lot的库位号更新
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                    List<MaterialLot> packDetials = materialLotRepository.getPackageDetailLots(materialLot.getObjectRrn());
                    if(CollectionUtils.isNotEmpty(packDetials)){
                        for(MaterialLot packedLot: packDetials){
                            packedLot.setReserved14(storageId);
                            if(StringUtils.isNullOrEmpty(materialLot.getReserved8())){
                                packedLot.setReserved8(materialLot.getReserved8());
                            }
                            packedLot = materialLotRepository.saveAndFlush(packedLot);

                            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(packedLot, MaterialLotHistory.TRANS_TYPE_TRANSFER_PARENT);
                            materialLotHistoryRepository.save(history);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * GC盘点。
     * 这个盘点不是传统上的盘点库存数量，而是只记录盘点历史。会传递不存在的
     * 应格科要求要记录到一个单独表中。记录这个盘点历史。
     *
     */
    public void checkMaterialInventory(List<MaterialLot> existMaterialLots, List<MaterialLot> notExistMaterialLots) throws ClientException {
        try {
            if (CollectionUtils.isNotEmpty(existMaterialLots)) {
                for (MaterialLot materialLot : existMaterialLots) {
                    materialLot = mmsService.getMLotByMLotId(materialLot.getMaterialLotId(), true);
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_CHECK);
                    history.setTransQty(materialLot.getCurrentQty());
                    materialLotHistoryRepository.save(history);

                    CheckHistory checkHistory = new CheckHistory();
                    PropertyUtils.copyProperties(materialLot, checkHistory, new HistoryBeanConverter());
                    checkHistory.setTransQty(materialLot.getCurrentQty());
                    checkHistory.setTransType(MaterialLotHistory.TRANS_TYPE_CHECK);
                    checkHistory.setObjectRrn(null);
                    checkHistory.setHisSeq(ThreadLocalContext.getTransRrn());
                    checkHistoryRepository.save(checkHistory);

                    List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                    if (CollectionUtils.isNotEmpty(packageDetailLots)) {
                        for (MaterialLot packageDetailLot : packageDetailLots) {
                            history = (MaterialLotHistory) baseService.buildHistoryBean(packageDetailLot, MaterialLotHistory.TRANS_TYPE_CHECK);
                            history.setTransQty(packageDetailLot.getCurrentQty());
                            materialLotHistoryRepository.save(history);

                            checkHistory = new CheckHistory();
                            PropertyUtils.copyProperties(packageDetailLot, checkHistory, new HistoryBeanConverter());
                            checkHistory.setTransQty(packageDetailLot.getCurrentQty());
                            checkHistory.setTransType(MaterialLotHistory.TRANS_TYPE_CHECK);
                            checkHistory.setObjectRrn(null);
                            checkHistory.setHisSeq(ThreadLocalContext.getTransRrn());
                            checkHistoryRepository.save(checkHistory);

                            this.saveUnitCheckHis(packageDetailLot.getMaterialLotId());
                        }
                    }

                    this.saveUnitCheckHis(materialLot.getMaterialLotId());
                }
            }

            if (CollectionUtils.isNotEmpty(notExistMaterialLots)) {
                for (MaterialLot materialLot : notExistMaterialLots) {
                    CheckHistory checkHistory = new CheckHistory();
                    PropertyUtils.copyProperties(materialLot, checkHistory, new HistoryBeanConverter());
                    checkHistory.setTransQty(BigDecimal.ZERO);
                    checkHistory.setErrorFlag(true);
                    checkHistory.setActionCode("Error");
                    checkHistory.setTransType(MaterialLotHistory.TRANS_TYPE_CHECK);
                    checkHistory.setObjectRrn(null);
                    checkHistory.setHisSeq(ThreadLocalContext.getTransRrn());
                    checkHistoryRepository.save(checkHistory);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存unit的盘点历史
     * @param materialLotId
     */
    private void saveUnitCheckHis(String materialLotId) {
        List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLotId);
        if (CollectionUtils.isNotEmpty(materialLotUnitList)) {
            for (MaterialLotUnit materialLotUnit : materialLotUnitList) {
                MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotHistory.TRANS_TYPE_CHECK);
                materialLotUnitHistory.setTransQty(materialLotUnit.getCurrentQty());
                materialLotUnitHisRepository.save(materialLotUnitHistory);

                CheckHistory checkHistory = new CheckHistory();
                checkHistory.setMaterialLotUnit(materialLotUnit);
                checkHistory.setTransQty(materialLotUnit.getCurrentQty());
                checkHistory.setTransType(MaterialLotHistory.TRANS_TYPE_CHECK);
                checkHistory.setObjectRrn(null);
                checkHistory.setHisSeq(ThreadLocalContext.getTransRrn());
                checkHistoryRepository.save(checkHistory);
            }
        }
    }


    /**
     * Wafer发料时同时同步wafer发料单和其他发料单
     * @throws ClientException
     */
    public void asyncWaferIssueOrderAndOtherIssueOrder() throws ClientException {
        asyncWaferIssueOrder();
        asyncOtherIssueOrder();
    }

    /**
     * WLT|CP发货单、三方销售单据
     * @throws ClientException
     */
    public void asyncWltCpShipOrder() throws ClientException {
        asyncOtherStockOutOrder();
        asyncOtherShipOrder();
    }

    /**
     * 同步wafer发料单据
     * @throws ClientException
     */
    public void asyncWaferIssueOrder() throws ClientException {
        try {
            List<ErpMaterialOutOrder> waferIssueOrders = erpMaterialOutOrderRepository.findByTypeAndSynStatusNotIn(ErpMaterialOutOrder.TYPE_TV, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();

            if (CollectionUtils.isNotEmpty(waferIssueOrders)) {
                Map<String, List<ErpMaterialOutOrder>> documentIdMap = waferIssueOrders.stream().collect(Collectors.groupingBy(ErpMaterialOutOrder :: getCcode));
                for (String documentId : documentIdMap.keySet()) {
                    List<ErpMaterialOutOrder> documentIdList = documentIdMap.get(documentId);
                    //把即将同步的同Ccode数据按createseq分组
                    Map<String, List<ErpMaterialOutOrder>> sameCreateSeqOrder = documentIdList.stream().filter(erpMaterialOutOrder -> !StringUtils.isNullOrEmpty(erpMaterialOutOrder.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpMaterialOutOrder :: getCreateSeq));
                    List<WaferIssueOrder> waferIssueOrderList = waferIssueOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(waferIssueOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            for(ErpMaterialOutOrder erpMaterialOutOrder : documentIdList){
                                erpMaterialOutOrder.setUserId(Document.SYNC_USER_ID);
                                erpMaterialOutOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                                erpMaterialOutOrder.setErrorMemo(ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID);
                                erpMaterialOutOrderRepository.save(erpMaterialOutOrder);
                            }
                            continue;
                        }
                    }
                    WaferIssueOrder waferIssueOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(waferIssueOrderList)) {
                        //如果有不同create_seq
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpMaterialOutOrder erpMaterialOutOrder : documentIdList) {
                                asyncDuplicateSeqList.add(erpMaterialOutOrder.getSeq());
                            }
                            continue;
                        }
                        waferIssueOrder = new WaferIssueOrder();
                        waferIssueOrder.setName(documentId);
                        waferIssueOrder.setStatus(Document.STATUS_OPEN);
                        waferIssueOrder.setReserved31(ErpMaterialOutOrder.SOURCE_TABLE_NAME);
                    } else {
                        waferIssueOrder = waferIssueOrderList.get(0);
                        totalQty = waferIssueOrder.getQty();
                        boolean differentCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(waferIssueOrder.getReserved32())){
                                differentCreateSeq = true;
                                for  (ErpMaterialOutOrder erpMaterialOutOrder : documentIdList) {
                                    asyncDuplicateSeqList.add(erpMaterialOutOrder.getSeq());
                                }
                                break;
                            }
                        }
                        if(differentCreateSeq){
                            continue;
                        }
                    }
                    waferIssueOrder.setName(documentId);

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpMaterialOutOrder erpMaterialOutOrder : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (waferIssueOrder.getObjectRrn() != null) {
                                documentLine = documentLineRepository.findByDocRrnAndReserved1(waferIssueOrder.getObjectRrn(), String.valueOf(erpMaterialOutOrder.getSeq()));
                                if (documentLine != null) {
                                    if (ErpSo.SYNC_STATUS_CHANGED.equals(erpMaterialOutOrder.getSynStatus())) {
                                        if (documentLine != null && documentLine.getHandledQty().compareTo(erpMaterialOutOrder.getIquantity()) > 0) {
                                            throw new ClientException("gc.order_handled_qty_gt_qty");
                                        }
                                    }
                                }
                            }

                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            if (documentLine == null) {
                                documentLine = new DocumentLine();
                                Material material = mmsService.getRawMaterialByName(erpMaterialOutOrder.getCinvcode());
                                if (material == null) {
                                    throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, erpMaterialOutOrder.getCinvcode());
                                }
                                documentLine.setDocId(documentId);
                                documentLine.setErpCreated(DateUtils.parseDate(erpMaterialOutOrder.getDdate()));
                                documentLine.setMaterialRrn(material.getObjectRrn());
                                documentLine.setMaterialName(material.getName());
                                documentLine.setReserved1(String.valueOf(erpMaterialOutOrder.getSeq()));
                                documentLine.setReserved2(erpMaterialOutOrder.getSecondcode());
                                documentLine.setReserved3(erpMaterialOutOrder.getGrade());
                                documentLine.setReserved4(erpMaterialOutOrder.getCfree3());
                                documentLine.setReserved5(erpMaterialOutOrder.getCmaker());
                                documentLine.setReserved6(erpMaterialOutOrder.getChandler());
                                documentLine.setReserved7(erpMaterialOutOrder.getOther1());
                                documentLine.setReserved8(erpMaterialOutOrder.getCusname());
                                documentLine.setReserved9(waferIssueOrder.CATEGORY_WAFER_ISSUE);
                                documentLine.setReserved31(ErpMaterialOutOrder.SOURCE_TABLE_NAME);
                            }

                            totalQty = totalQty.add(erpMaterialOutOrder.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpMaterialOutOrder.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLines.add(documentLine);

                            waferIssueOrder.setOwner(erpMaterialOutOrder.getChandler());
                            waferIssueOrder.setReserved32(erpMaterialOutOrder.getCreateSeq());
                            asyncSuccessSeqList.add(erpMaterialOutOrder.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpMaterialOutOrder.setUserId(Document.SYNC_USER_ID);
                            erpMaterialOutOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpMaterialOutOrder.setErrorMemo(e.getMessage());
                            erpMaterialOutOrderRepository.save(erpMaterialOutOrder);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        waferIssueOrder.setQty(totalQty);
                        waferIssueOrder.setUnHandledQty(waferIssueOrder.getQty().subtract(waferIssueOrder.getHandledQty()));
                        waferIssueOrder.setReserved31(ErpMaterialOutOrder.SOURCE_TABLE_NAME);
                        waferIssueOrder = (WaferIssueOrder) baseService.saveEntity(waferIssueOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(waferIssueOrder);
                        baseService.saveEntity(documentLine);
                    }

                    // 保存单据的时候同步下客户
                    if (!StringUtils.isNullOrEmpty(waferIssueOrder.getSupplierName())) {
                        savaCustomer(waferIssueOrder.getSupplierName());
                    }
                }
                if (CollectionUtils.isNotEmpty(asyncSuccessSeqList)) {
                    if(asyncSuccessSeqList.size() >= Document.SEQ_MAX_LENGTH){
                        List<List<Long>> asyncSuccessSeqGroupList = getSeqListGroupByCount(asyncSuccessSeqList);
                        for(List seqGroup : asyncSuccessSeqGroupList){
                            erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, seqGroup);
                        }
                    } else {
                        erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, asyncSuccessSeqList);
                    }
                }
                if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                    if(asyncDuplicateSeqList.size() >= Document.SEQ_MAX_LENGTH){
                        List<List<Long>> asyncDuplicateSeqGroupList = getSeqListGroupByCount(asyncDuplicateSeqList);
                        for(List seqGroup : asyncDuplicateSeqGroupList){
                            erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpMaterialOutaOrder.SYNC_STATUS_SYNC_ERROR,
                                    ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, seqGroup);                        }
                    } else {
                        erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpMaterialOutaOrder.SYNC_STATUS_SYNC_ERROR,
                                ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, asyncDuplicateSeqList);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步重测单据
     * @throws ClientException
     */
    public void asyncReTestOrder() throws ClientException {
        try {
            List<ErpMaterialOutOrder> erpMaterialOutOrders = erpMaterialOutOrderRepository.findByTypeAndSynStatusNotIn(ErpMaterialOutOrder.TYPE_RO, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();

            if (CollectionUtils.isNotEmpty(erpMaterialOutOrders)) {
                Map<String, List<ErpMaterialOutOrder>> documentIdMap = erpMaterialOutOrders.stream().collect(Collectors.groupingBy(ErpMaterialOutOrder :: getCcode));
                for (String documentId : documentIdMap.keySet()) {
                    List<ErpMaterialOutOrder> documentIdList = documentIdMap.get(documentId);
                    //把即将同步的同Ccode数据按createseq分组
                    Map<String, List<ErpMaterialOutOrder>> sameCreateSeqOrder = documentIdList.stream().filter(erpMaterialOutOrder -> !StringUtils.isNullOrEmpty(erpMaterialOutOrder.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpMaterialOutOrder :: getCreateSeq));
                    List<ReTestOrder> reTestOrderList = reTestOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(reTestOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            for(ErpMaterialOutOrder erpMaterialOutOrder : documentIdList){
                                erpMaterialOutOrder.setUserId(Document.SYNC_USER_ID);
                                erpMaterialOutOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                                erpMaterialOutOrder.setErrorMemo(ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID);
                                erpMaterialOutOrderRepository.save(erpMaterialOutOrder);
                            }
                            continue;
                        }
                    }
                    ReTestOrder reTestOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(reTestOrderList)) {
                        //如果有不同create_seq
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpMaterialOutOrder erpMaterialOutOrder : documentIdList) {
                                asyncDuplicateSeqList.add(erpMaterialOutOrder.getSeq());
                            }
                            continue;
                        }
                        reTestOrder = new ReTestOrder();
                        reTestOrder.setName(documentId);
                        reTestOrder.setStatus(Document.STATUS_OPEN);
                        reTestOrder.setReserved31(ErpMaterialOutOrder.SOURCE_TABLE_NAME);
                    } else {
                        reTestOrder = reTestOrderList.get(0);
                        totalQty = reTestOrder.getQty();
                        boolean differentCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(reTestOrder.getReserved32())){
                                differentCreateSeq = true;
                                for  (ErpMaterialOutOrder erpMaterialOutOrder : documentIdList) {
                                    asyncDuplicateSeqList.add(erpMaterialOutOrder.getSeq());
                                }
                                break;
                            }
                        }
                        if(differentCreateSeq){
                            continue;
                        }
                    }
                    reTestOrder.setName(documentId);

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpMaterialOutOrder erpMaterialOutOrder : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (reTestOrder.getObjectRrn() != null) {
                                documentLine = documentLineRepository.findByDocRrnAndReserved1(reTestOrder.getObjectRrn(), String.valueOf(erpMaterialOutOrder.getSeq()));
                                if (documentLine != null) {
                                    if (ErpSo.SYNC_STATUS_CHANGED.equals(erpMaterialOutOrder.getSynStatus())) {
                                        if (documentLine != null && documentLine.getHandledQty().compareTo(erpMaterialOutOrder.getIquantity()) > 0) {
                                            throw new ClientException("gc.order_handled_qty_gt_qty");
                                        }
                                    }
                                }
                            }
                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            if (documentLine == null) {
                                documentLine = new DocumentLine();
                                Material material = mmsService.getProductByName(erpMaterialOutOrder.getCinvcode());
                                if (material == null) {
                                    throw new ClientParameterException(MM_PRODUCT_ID_IS_NOT_EXIST, erpMaterialOutOrder.getCinvcode());
                                }
                                documentLine.setDocId(documentId);
                                documentLine.setErpCreated(DateUtils.parseDate(erpMaterialOutOrder.getDdate()));
                                documentLine.setMaterialRrn(material.getObjectRrn());
                                documentLine.setMaterialName(material.getName());
                                documentLine.setReserved1(String.valueOf(erpMaterialOutOrder.getSeq()));
                                documentLine.setReserved2(erpMaterialOutOrder.getSecondcode());
                                documentLine.setReserved3(erpMaterialOutOrder.getGrade());
                                documentLine.setReserved4(erpMaterialOutOrder.getCfree3());
                                documentLine.setReserved5(erpMaterialOutOrder.getCmaker());
                                documentLine.setReserved6(erpMaterialOutOrder.getChandler());
                                documentLine.setReserved7(erpMaterialOutOrder.getOther1());
                                documentLine.setReserved8(erpMaterialOutOrder.getCusname());
                                documentLine.setReserved9(ReTestOrder.CATEGORY_RETEST);
                                documentLine.setReserved31(ErpMaterialOutOrder.SOURCE_TABLE_NAME);

                            }

                            totalQty = totalQty.add(erpMaterialOutOrder.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpMaterialOutOrder.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLines.add(documentLine);

                            reTestOrder.setOwner(erpMaterialOutOrder.getChandler());
                            reTestOrder.setReserved32(erpMaterialOutOrder.getCreateSeq());
                            asyncSuccessSeqList.add(erpMaterialOutOrder.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpMaterialOutOrder.setUserId(Document.SYNC_USER_ID);
                            erpMaterialOutOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpMaterialOutOrder.setErrorMemo(e.getMessage());
                            erpMaterialOutOrderRepository.save(erpMaterialOutOrder);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        reTestOrder.setQty(totalQty);
                        reTestOrder.setUnHandledQty(reTestOrder.getQty().subtract(reTestOrder.getHandledQty()));
                        reTestOrder.setReserved31(ErpMaterialOutOrder.SOURCE_TABLE_NAME);
                        reTestOrder = (ReTestOrder) baseService.saveEntity(reTestOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(reTestOrder);
                        baseService.saveEntity(documentLine);
                    }

                    // 保存单据的时候同步下客户
                    if (!StringUtils.isNullOrEmpty(reTestOrder.getSupplierName())) {
                        savaCustomer(reTestOrder.getSupplierName());
                    }
                }
                if (CollectionUtils.isNotEmpty(asyncSuccessSeqList)) {
                    if(asyncSuccessSeqList.size() >= Document.SEQ_MAX_LENGTH){
                        List<List<Long>> asyncSuccessSeqGroupList = getSeqListGroupByCount(asyncSuccessSeqList);
                        for(List seqGroup : asyncSuccessSeqGroupList){
                            erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, seqGroup);
                        }
                    } else {
                        erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, asyncSuccessSeqList);
                    }
                }
                if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                    if(asyncDuplicateSeqList.size() >= Document.SEQ_MAX_LENGTH){
                        List<List<Long>> asyncDuplicateSeqGroupList = getSeqListGroupByCount(asyncDuplicateSeqList);
                        for(List seqGroup : asyncDuplicateSeqGroupList){
                            erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_ERROR, ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, seqGroup);
                        }
                    } else {
                        erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_ERROR, ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, asyncDuplicateSeqList);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 晶圆根据匹配规则自动匹配单据
     * F等级的COB晶圆接收不需要匹配单据
     * @param documentLineList
     * @param materialLotActions
     * @throws ClientException
     */
    public void validationAndReceiveWafer(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String receiveWithDoc) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            if(!StringUtils.isNullOrEmpty(receiveWithDoc)){
                List<MaterialLot> materialLotList = new ArrayList<>();
                List<MaterialLot> fGradeMLotList = new ArrayList<>();
                for(MaterialLot materialLot : materialLots){
                    if(MaterialLot.IMPORT_COB.equals(materialLot.getReserved49()) && MaterialLot.GEADE_F.equals(materialLot.getGrade())){
                        fGradeMLotList.add(materialLot);
                    } else {
                        materialLotList.add(materialLot);
                    }
                }
                if(CollectionUtils.isNotEmpty(materialLotList)){
                    Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByImportType(materialLotList, MaterialLot.WAFER_RECEIVE_DOC_VALIDATE_RULE_ID, MaterialLot.COB_WAFER_RECEIVE_DOC_VALIDATE_RULE_ID);
                    documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());
                    Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMLotDocRule(documentLineList, MaterialLot.WAFER_RECEIVE_DOC_VALIDATE_RULE_ID);
                    // 确保所有的物料批次都能匹配上单据, 并且数量足够
                    for (String key : materialLotMap.keySet()) {
                        if (!documentLineMap.keySet().contains(key)) {
                            throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLotMap.get(key).get(0).getLotId());
                        }
                        Long totalMaterialLotQty = getTotalMaterialLotQtyByMLotUnitImportType(materialLotMap.get(key));
                        Long totalUnhandledQty = documentLineMap.get(key).stream().collect(Collectors.summingLong(documentLine -> documentLine.getUnHandledQty().longValue()));
                        if (totalMaterialLotQty.compareTo(totalUnhandledQty) > 0) {
                            throw new ClientException(GcExceptions.OVER_DOC_QTY);
                        }
                        receiveWafer(documentLineMap.get(key), materialLotMap.get(key));
                    }
                }

                if(CollectionUtils.isNotEmpty(fGradeMLotList)){
                    for(MaterialLot materialLot : fGradeMLotList){
                        materialLotUnitService.receiveMLotWithUnit(materialLot, WAREHOUSE_ZJ);
                    }
                }
            } else {
                for(MaterialLot materialLot : materialLots){
                    materialLotUnitService.receiveMLotWithUnit(materialLot, WAREHOUSE_ZJ);
                }
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private Map<String,List<MaterialLot>> groupMaterialLotByImportType(List<MaterialLot> materialLots, String ruleName, String cobRuleName) throws ClientException{
        try {
            Map<String, List<MaterialLot>> materialLotMap = Maps.newHashMap();
            List<MaterialLot> cobMaterialLotList = Lists.newArrayList();
            List<MaterialLot> materialLotList = Lists.newArrayList();
            for (MaterialLot materialLot : materialLots){
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved49()) && MaterialLot.IMPORT_COB.equals(materialLot.getReserved49())){
                    cobMaterialLotList.add(materialLot);
                } else {
                    materialLotList.add(materialLot);
                }
            }
            if(CollectionUtils.isNotEmpty(materialLotList)){
                materialLotMap = groupMaterialLotByMLotDocRule(materialLotList, ruleName);
            }
            if(CollectionUtils.isNotEmpty(cobMaterialLotList)){
                Map<String, List<MaterialLot>>  cobMLotMap = groupMaterialLotByMLotDocRule(cobMaterialLotList, cobRuleName);
                if(cobMLotMap != null && cobMLotMap.keySet().size() > 0){
                    for(String mLotInfo : cobMLotMap.keySet()){
                        if(materialLotMap.containsKey(mLotInfo)){
                            materialLotMap.get(mLotInfo).addAll(cobMLotMap.get(mLotInfo));
                        } else {
                            materialLotMap.put(mLotInfo, cobMLotMap.get(mLotInfo));
                        }
                    }
                }
            }

            return materialLotMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private Long getTotalMaterialLotQtyByMLotUnitImportType(List<MaterialLot> materialLotList) throws ClientException {
        try{
            Long totalMaterialLotQty = 0L;
            for (MaterialLot materialLot : materialLotList){
                String importType = materialLot.getReserved49();
                if(MaterialLot.IMPORT_LCD_CP.equals(importType) || MaterialLot.IMPORT_SENSOR_CP.equals(importType) || MaterialLot.IMPORT_WLA.equals(importType)){
                    totalMaterialLotQty += materialLot.getCurrentSubQty().longValue();
                } else {
                    totalMaterialLotQty += materialLot.getCurrentQty().longValue();
                }
            }
            return totalMaterialLotQty;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    public List<Map<String,String>> validationAndWaferIssue(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String issueWithDoc, String unPlanLot) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());

            if (!StringUtils.isNullOrEmpty(issueWithDoc)) {
                documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());
                Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMLotDocRule(documentLineList, MaterialLot.WAFER_ISSUE_DOC_VALIDATE_RULE_ID);
                Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByImportType(materialLots, MaterialLot.WAFER_ISSUE_DOC_VALIDATE_RULE_ID, MaterialLot.COB_WAFER_ISSUE_DOC_VALIDATE_RULE_ID);

                // 确保所有的物料批次都能匹配上单据, 并且数量足够
                for (String key : materialLotMap.keySet()) {
                    if (!documentLineMap.keySet().contains(key)) {
                        throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLotMap.get(key).get(0).getMaterialLotId());
                    }
                    Long totalUnhandledQty = documentLineMap.get(key).stream().collect(Collectors.summingLong(documentLine -> documentLine.getUnHandledQty().longValue()));
                    Long totalMaterialLotQty = getTotalMaterialLotQtyByMLotUnitImportType(materialLotMap.get(key));
                    if (totalMaterialLotQty.compareTo(totalUnhandledQty) > 0) {
                        throw new ClientException(GcExceptions.OVER_DOC_QTY);
                    }
                    waferIssue(documentLineMap.get(key), materialLotMap.get(key));
                }

            } else {
                waferIssueWithOutDocument(materialLots);
            }

            //将晶圆信息保存至Mes backendWaferReceive表中
            mesService.saveBackendWaferReceive(materialLots);

            //RW的来料发料之后自动打印RW发料标签
            List<MaterialLot> rwMaterialLots = materialLots.stream().filter(materialLot -> materialLot.getMaterialName().endsWith("-2.1") && MaterialLot.SCP_WAFER_SOURCE.equals(materialLot.getReserved50())).collect(Collectors.toList());
            List<Map<String, String>> parameterMapList = getRWIssueMaterialLotPrintParameter(rwMaterialLots);


            if(StringUtils.isNullOrEmpty(unPlanLot)){
                boolean waferIssueToMesPlanLot = SystemPropertyUtils.getWaferIssueToMesPlanLot();
                log.info("wafer issue to mes plan lot flag is " + waferIssueToMesPlanLot);
                if(waferIssueToMesPlanLot){
                    log.info("wafer issue to mes plan lot start ");
                    mesService.materialLotUnitPlanLot(materialLots, ThreadLocalContext.getSessionContext());
                    log.info("wafer issue to mes plan lot end ");
                }
            }

            return parameterMapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 晶圆发料 但是不和单据挂钩
     * @param materialLots
     */
    public void waferIssueWithOutDocument(List<MaterialLot> materialLots) throws ClientException {
        try {
            for (MaterialLot materialLot : materialLots) {
                String importType = materialLot.getReserved49();
                if (MaterialLot.IMPORT_LCD_CP.equals(importType) || MaterialLot.IMPORT_SENSOR_CP.equals(importType) || MaterialLot.IMPORT_WLA.equals(importType)) {
                    materialLot.setCurrentSubQty(BigDecimal.ZERO);
                    mmsService.changeMaterialLotState(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE, StringUtils.EMPTY);
                    materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
                    changeMLotUnitStateAndSaveMesWaferBackendWaferReceive(materialLot);
                } else {
                    materialLot.setCurrentQty(BigDecimal.ZERO);
                    if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                        mmsService.changeMaterialLotState(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE, StringUtils.EMPTY);
                        materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
                        changeMLotUnitStateAndSaveMesWaferBackendWaferReceive(materialLot);
                    }
                }
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 晶圆发料
     * @param documentLines
     * @param materialLots
     */
    public void waferIssue(List<DocumentLine> documentLines, List<MaterialLot> materialLots) {
        try {
            documentLines = vlidateDocMergeAndSortDocumentLinesBySeq(documentLines);
            for (DocumentLine documentLine: documentLines) {

                BigDecimal unhandedQty = documentLine.getUnHandledQty();
                Iterator<MaterialLot> iterator = materialLots.iterator();
                while (iterator.hasNext()) {
                    MaterialLot materialLot = iterator.next();
                    String importType = materialLot.getReserved49();
                    if (StringUtils.isNullOrEmpty(materialLot.getReserved12())) {
                        materialLot.setReserved12(documentLine.getObjectRrn().toString());
                    } else {
                        materialLot.setReserved12(materialLot.getReserved12() + StringUtils.SEMICOLON_CODE + documentLine.getObjectRrn().toString());
                    }
                    if(MaterialLot.IMPORT_LCD_CP.equals(importType) || MaterialLot.IMPORT_SENSOR_CP.equals(importType) || MaterialLot.IMPORT_WLA.equals(importType)){
                        BigDecimal currentSubQty = materialLot.getCurrentSubQty();
                        if (unhandedQty.compareTo(currentSubQty) >= 0) {
                            unhandedQty = unhandedQty.subtract(currentSubQty);
                            currentSubQty = BigDecimal.ZERO;
                        } else {
                            currentSubQty = currentSubQty.subtract(unhandedQty);
                            unhandedQty = BigDecimal.ZERO;
                        }
                        materialLot.setCurrentSubQty(currentSubQty);
                        if (materialLot.getCurrentSubQty().compareTo(BigDecimal.ZERO) == 0) {
                            mmsService.changeMaterialLotState(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE, StringUtils.EMPTY);
                            materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
                            iterator.remove();
                            changeMLotUnitStateAndSaveMesWaferBackendWaferReceive(materialLot);
                        } else {
                            List<MaterialLotInventory> materialLotInvList = mmsService.getMaterialLotInv(materialLot.getObjectRrn());
                            if (CollectionUtils.isNotEmpty(materialLotInvList)) {
                                //GC一个批次只会在一个库存
                                MaterialLotInventory materialLotInv = materialLotInvList.get(0);
                                materialLotInv.setStockQty(materialLot.getCurrentQty());
                                materialLot.setCurrentSubQty(currentSubQty);
                                materialLotInventoryRepository.save(materialLotInv);
                            }
                        }
                    } else {
                        BigDecimal currentQty = materialLot.getCurrentQty();
                        if (unhandedQty.compareTo(currentQty) >= 0) {
                            unhandedQty = unhandedQty.subtract(currentQty);
                            currentQty = BigDecimal.ZERO;
                        } else {
                            currentQty = currentQty.subtract(unhandedQty);
                            unhandedQty = BigDecimal.ZERO;
                        }
                        materialLot.setCurrentQty(currentQty);
                        if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                            mmsService.changeMaterialLotState(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE, StringUtils.EMPTY);
                            materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
                            iterator.remove();
                            changeMLotUnitStateAndSaveMesWaferBackendWaferReceive(materialLot);
                        } else {
                            List<MaterialLotInventory> materialLotInvList = mmsService.getMaterialLotInv(materialLot.getObjectRrn());
                            if (CollectionUtils.isNotEmpty(materialLotInvList)) {
                                //GC一个批次只会在一个库存
                                MaterialLotInventory materialLotInv = materialLotInvList.get(0);
                                materialLotInv.setStockQty(currentQty);
                                materialLotInv.setCurrentSubQty(materialLot.getCurrentSubQty());
                                materialLotInventoryRepository.save(materialLotInv);
                            }
                        }
                    }

                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE);
                    history.setReserved12(documentLine.getObjectRrn().toString());
                    materialLotHistoryRepository.save(history);
                    if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }

                BigDecimal handledQty = documentLine.getUnHandledQty().subtract(unhandedQty);
                if(handledQty.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                } else {
                    documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
                    documentLine.setUnHandledQty(unhandedQty);
                    documentLine = documentLineRepository.saveAndFlush(documentLine);
                    baseService.saveHistoryEntity(documentLine, GCMaterialEvent.EVENT_WAFER_ISSUE);

                    WaferIssueOrder waferIssueOrder = (WaferIssueOrder) waferIssueOrderRepository.findByObjectRrn(documentLine.getDocRrn());
                    waferIssueOrder.setHandledQty(waferIssueOrder.getHandledQty().add(handledQty));
                    waferIssueOrder.setUnHandledQty(waferIssueOrder.getUnHandledQty().subtract(handledQty));
                    waferIssueOrderRepository.save(waferIssueOrder);
                    baseService.saveHistoryEntity(waferIssueOrder, GCMaterialEvent.EVENT_WAFER_ISSUE);
                    //晶圆发料单的回写来源表有两个，分别判断是否存在并回写数据
                    Optional<ErpMaterialOutOrder> erpMaterialOutOrderOptional = erpMaterialOutOrderRepository.findById(Long.valueOf(documentLine.getReserved1()));
                    if(erpMaterialOutOrderOptional.isPresent()) {
                        ErpMaterialOutOrder erpMaterialOutOrder = erpMaterialOutOrderOptional.get();
                        erpMaterialOutOrder.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
                        erpMaterialOutOrder.setLeftNum(erpMaterialOutOrder.getLeftNum().subtract(handledQty));
                        if (StringUtils.isNullOrEmpty(erpMaterialOutOrder.getDeliveredNum())) {
                            erpMaterialOutOrder.setDeliveredNum(handledQty.toPlainString());
                        } else {
                            BigDecimal docHandledQty = new BigDecimal(erpMaterialOutOrder.getDeliveredNum());
                            docHandledQty = docHandledQty.add(handledQty);
                            erpMaterialOutOrder.setDeliveredNum(docHandledQty.toPlainString());
                        }
                        erpMaterialOutOrderRepository.save(erpMaterialOutOrder);
                    } else {
                        Optional<ErpMaterialOutaOrder> erpMaterialOutAOrderOptional = erpMaterialOutAOrderRepository.findById(Long.valueOf(documentLine.getReserved1()));
                        if(erpMaterialOutAOrderOptional.isPresent()) {
                            ErpMaterialOutaOrder erpMaterialOutAOrder = erpMaterialOutAOrderOptional.get();
                            erpMaterialOutAOrder.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
                            erpMaterialOutAOrder.setLeftNum(erpMaterialOutAOrder.getLeftNum().subtract(handledQty));
                            if (StringUtils.isNullOrEmpty(erpMaterialOutAOrder.getDeliveredNum())) {
                                erpMaterialOutAOrder.setDeliveredNum(handledQty.toPlainString());
                            } else {
                                BigDecimal docHandledQty = new BigDecimal(erpMaterialOutAOrder.getDeliveredNum());
                                docHandledQty = docHandledQty.add(handledQty);
                                erpMaterialOutAOrder.setDeliveredNum(docHandledQty.toPlainString());
                            }
                            erpMaterialOutAOrderRepository.save(erpMaterialOutAOrder);
                        } else {
                            throw new ClientParameterException(GcExceptions.ERP_WAFER_ISSUE_ORDER_IS_NOT_EXIST, documentLine.getReserved1());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void changeMLotUnitStateAndSaveMesWaferBackendWaferReceive(MaterialLot materialLot) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnits = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
            for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                materialLotUnit.setState(MaterialLotUnit.STATE_ISSUE);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, GCMaterialEvent.EVENT_WAFER_ISSUE);
                history.setTransQty(materialLotUnit.getCurrentQty());
                materialLotUnitHisRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 晶圆接收
     * @param documentLines
     * @param materialLots
     * @throws ClientException
     */
    private void receiveWafer(List<DocumentLine> documentLines, List<MaterialLot> materialLots) throws ClientException{
        try {
            documentLines = vlidateDocMergeAndSortDocumentLinesBySeq(documentLines);
            for (DocumentLine documentLine: documentLines) {

                BigDecimal unhandedQty = documentLine.getUnHandledQty();
                Map<String, BigDecimal> mLotQty = Maps.newHashMap();
                for(MaterialLot materialLot : materialLots) {
                    String importType = materialLot.getReserved49();
                    if(MaterialLot.IMPORT_LCD_CP.equals(importType) || MaterialLot.IMPORT_SENSOR_CP.equals(importType) || MaterialLot.IMPORT_WLA.equals(importType)) {
                        mLotQty.put(materialLot.getMaterialLotId(), materialLot.getCurrentSubQty());
                    } else {
                        mLotQty.put(materialLot.getMaterialLotId(), materialLot.getCurrentQty());
                    }
                }

                Iterator<MaterialLot> iterator = materialLots.iterator();

                while (iterator.hasNext()) {
                    MaterialLot materialLot = iterator.next();
                    String importType = materialLot.getReserved49();
                    if (StringUtils.isNullOrEmpty(materialLot.getReserved12())) {
                        materialLot.setReserved12(documentLine.getObjectRrn().toString());
                    } else {
                        materialLot.setReserved12(materialLot.getReserved12() + StringUtils.SEMICOLON_CODE + documentLine.getObjectRrn().toString());
                    }
                    if(MaterialLot.IMPORT_LCD_CP.equals(importType) || MaterialLot.IMPORT_SENSOR_CP.equals(importType) || MaterialLot.IMPORT_WLA.equals(importType)) {
                        BigDecimal waferQty = materialLot.getCurrentSubQty();
                        if (unhandedQty.compareTo(waferQty) >= 0) {
                            unhandedQty = unhandedQty.subtract(waferQty);
                            waferQty =  BigDecimal.ZERO;
                        } else {
                            waferQty = waferQty.subtract(unhandedQty);
                            unhandedQty = BigDecimal.ZERO;
                        }
                        materialLot.setCurrentSubQty(waferQty);
                        if (materialLot.getCurrentSubQty().compareTo(BigDecimal.ZERO) == 0) {
                            //数量进行还原。不能扣减。
                            materialLot.setCurrentSubQty(mLotQty.get(materialLot.getMaterialLotId()));
                            List<MaterialLotUnit> receiveUnits = materialLotUnitService.receiveMLotWithUnit(materialLot, WAREHOUSE_ZJ);
                            iterator.remove();
                        }
                    } else {
                        BigDecimal currentQty = materialLot.getCurrentQty();
                        if (unhandedQty.compareTo(currentQty) >= 0) {
                            unhandedQty = unhandedQty.subtract(currentQty);
                            currentQty =  BigDecimal.ZERO;
                        } else {
                            currentQty = currentQty.subtract(unhandedQty);
                            unhandedQty = BigDecimal.ZERO;
                        }
                        materialLot.setCurrentQty(currentQty);
                        if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                            //数量进行还原。不能扣减。
                            materialLot.setCurrentQty(mLotQty.get(materialLot.getMaterialLotId()));
                            List<MaterialLotUnit> receiveUnits = materialLotUnitService.receiveMLotWithUnit(materialLot, WAREHOUSE_ZJ);
                            iterator.remove();
                        }
                    }
                    if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }
                BigDecimal handledQty = documentLine.getUnHandledQty().subtract(unhandedQty);
                if(handledQty.compareTo(BigDecimal.ZERO) == 0){
                    break;
                } else {
                    documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
                    documentLine.setUnHandledQty(unhandedQty);
                    documentLine = documentLineRepository.saveAndFlush(documentLine);
                    baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_RECEIVE);

                    ReceiveOrder receiveOrder = (ReceiveOrder) receiveOrderRepository.findByObjectRrn(documentLine.getDocRrn());
                    receiveOrder.setHandledQty(receiveOrder.getHandledQty().add(handledQty));
                    receiveOrder.setUnHandledQty(receiveOrder.getUnHandledQty().subtract(handledQty));
                    receiveOrder = receiveOrderRepository.saveAndFlush(receiveOrder);
                    baseService.saveHistoryEntity(receiveOrder, MaterialLotHistory.TRANS_TYPE_RECEIVE);

                    validateDocAndUpdateErpSo(documentLine, handledQty);
                }
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *
     * @param documentLineList
     * @param materialLotActions
     * @throws ClientException
     */
    public void reTest(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());
            Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMLotDocRule(documentLineList, MaterialLot.MLOT_RETEST_DOC_VALIDATE_RULE_ID);
            Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByMLotDocRule(materialLots, MaterialLot.MLOT_RETEST_DOC_VALIDATE_RULE_ID);

            // 确保所有的物料批次都能匹配上单据, 并且数量足够
            for (String key : materialLotMap.keySet()) {
                if (!documentLineMap.keySet().contains(key)) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLotMap.get(key).get(0).getMaterialLotId());
                }
                Long totalUnhandledQty = documentLineMap.get(key).stream().collect(Collectors.summingLong(documentLine -> documentLine.getUnHandledQty().longValue()));
                Long totalMaterialLotQty = materialLotMap.get(key).stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentQty().longValue()));
                if (totalMaterialLotQty.compareTo(totalUnhandledQty) > 0) {
                    throw new ClientException(GcExceptions.OVER_DOC_QTY);
                }
                reTestMaterialLots(documentLineMap.get(key), materialLotMap.get(key));
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }

    }

    /**
     * 重测发料。更新单据信息。
     * @param documentLines
     * @param materialLots
     * @throws ClientException
     */
    private void reTestMaterialLots(List<DocumentLine> documentLines, List<MaterialLot> materialLots) throws ClientException{
        try {
            documentLines = vlidateDocMergeAndSortDocumentLinesBySeq(documentLines);
            for (DocumentLine documentLine: documentLines) {

                BigDecimal unhandedQty = documentLine.getUnHandledQty();
                Iterator<MaterialLot> iterator = materialLots.iterator();
                while (iterator.hasNext()) {
                    MaterialLot materialLot = iterator.next();
                    BigDecimal currentQty = materialLot.getCurrentQty();
                    if (unhandedQty.compareTo(currentQty) >= 0) {
                        unhandedQty = unhandedQty.subtract(currentQty);
                        currentQty = BigDecimal.ZERO;
                    } else {
                        currentQty = currentQty.subtract(unhandedQty);
                        unhandedQty = BigDecimal.ZERO;
                    }
                    materialLot.setCurrentQty(currentQty);
                    if (StringUtils.isNullOrEmpty(materialLot.getReserved12())) {
                        materialLot.setReserved12(documentLine.getObjectRrn().toString());
                    } else {
                        materialLot.setReserved12(materialLot.getReserved12() + StringUtils.SEMICOLON_CODE + documentLine.getObjectRrn().toString());
                    }
                    if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                        mmsService.changeMaterialLotState(materialLot, GCMaterialEvent.EVENT_RETEST, StringUtils.EMPTY);
                        materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
                        iterator.remove();
                    } else {
                        List<MaterialLotInventory> materialLotInvList = mmsService.getMaterialLotInv(materialLot.getObjectRrn());
                        if (CollectionUtils.isNotEmpty(materialLotInvList)) {
                            //GC一个批次只会在一个库存
                            MaterialLotInventory materialLotInv = materialLotInvList.get(0);
                            materialLotInv.setStockQty(currentQty);
                            materialLotInventoryRepository.save(materialLotInv);
                        }
                    }
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, GCMaterialEvent.EVENT_RETEST);
                    materialLotHistoryRepository.save(history);
                    if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }
                BigDecimal handledQty = documentLine.getUnHandledQty().subtract(unhandedQty);
                if(handledQty.compareTo(BigDecimal.ZERO) == 0){
                    break;
                } else {
                    documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
                    documentLine.setUnHandledQty(unhandedQty);
                    documentLine = documentLineRepository.saveAndFlush(documentLine);
                    baseService.saveHistoryEntity(documentLine, GCMaterialEvent.EVENT_RETEST);

                    ReTestOrder reTestOrder = (ReTestOrder) reTestOrderRepository.findByObjectRrn(documentLine.getDocRrn());
                    reTestOrder.setHandledQty(reTestOrder.getHandledQty().add(handledQty));
                    reTestOrder.setUnHandledQty(reTestOrder.getUnHandledQty().subtract(handledQty));
                    reTestOrder = reTestOrderRepository.saveAndFlush(reTestOrder);
                    baseService.saveHistoryEntity(reTestOrder, GCMaterialEvent.EVENT_RETEST);

                    Optional<ErpMaterialOutOrder> erpMaterialOutOrderOptional = erpMaterialOutOrderRepository.findById(Long.valueOf(documentLine.getReserved1()));
                    if (!erpMaterialOutOrderOptional.isPresent()) {
                        throw new ClientParameterException(GcExceptions.ERP_RETEST_ORDER_IS_NOT_EXIST, documentLine.getReserved1());
                    }

                    ErpMaterialOutOrder erpMaterialOutOrder = erpMaterialOutOrderOptional.get();
                    erpMaterialOutOrder.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
                    erpMaterialOutOrder.setLeftNum(erpMaterialOutOrder.getLeftNum().subtract(handledQty));
                    if (StringUtils.isNullOrEmpty(erpMaterialOutOrder.getDeliveredNum())) {
                        erpMaterialOutOrder.setDeliveredNum(handledQty.toPlainString());
                    } else {
                        BigDecimal docHandledQty = new BigDecimal(erpMaterialOutOrder.getDeliveredNum());
                        docHandledQty = docHandledQty.add(handledQty);
                        erpMaterialOutOrder.setDeliveredNum(docHandledQty.toPlainString());
                    }
                    erpMaterialOutOrderRepository.save(erpMaterialOutOrder);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void validationStockDocLine(DocumentLine documentLine, MaterialLot materialLot) throws ClientException{
        validateMLotAndDocLineByRule(documentLine, materialLot, MaterialLot.MLOT_SHIP_DOC_VALIDATE_RULE_ID);
        // 出货验证箱中真空包的备货信息和出货单rrn是否一致
        List<MaterialLot> packageDetailLots = materialLotRepository.getPackageDetailLots(materialLot.getObjectRrn());
        if (CollectionUtils.isNotEmpty(packageDetailLots)) {
            for (MaterialLot packagedMaterialLot : packageDetailLots) {
                try {
                    Assert.assertEquals(packagedMaterialLot.getReserved16() , documentLine.getObjectRrn().toString());
                } catch (AssertionError e) {
                    throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "reservedDocRrn", documentLine.getObjectRrn(), packagedMaterialLot.getReserved16());
                }
            }
        } else {
            try {
                Assert.assertEquals(materialLot.getReserved16() , documentLine.getObjectRrn().toString());
            } catch (AssertionError e) {
                throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "reservedDocRrn", documentLine.getObjectRrn(), materialLot.getReserved16());
            }
        }

    }

    /**
     * 验证
     * @param documentLineList
     * @param materialLot
     * @throws ClientException
     */
    public void validationDocLine(List<DocumentLine> documentLineList, MaterialLot materialLot) throws ClientException {
        if (CollectionUtils.isEmpty(documentLineList)) {
            documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());
            for (DocumentLine documentLine : documentLineList) {
                validateMLotAndDocLineByRule(documentLine, materialLot, MaterialLot.WAFER_ISSUE_DOC_VALIDATE_RULE_ID);
            }
        }
    }

    /**
     * 验证单据和物料批次是否吻合
     *  1，物料名称
     *  2. 二级代码
     *  3. 等级
     *  4. 保税属性
     * @param documentLine
     * @param materialLot
     * @throws ClientException
     */
    public void validationDocLine(DocumentLine documentLine, MaterialLot materialLot) throws ClientException{
        try {
            Assert.assertEquals(documentLine.getMaterialName(), materialLot.getMaterialName());
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "materialName", documentLine.getMaterialName(), materialLot.getMaterialName());
        }

        String materialSecondCode = materialLot.getReserved1() + materialLot.getGrade();
        try {
            Assert.assertEquals(documentLine.getReserved2(), materialSecondCode);
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "secondcode", documentLine.getReserved2(),  materialSecondCode);
        }

        try {
            Assert.assertEquals(documentLine.getReserved3(), materialLot.getGrade());
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "grade", documentLine.getReserved3(), materialLot.getGrade());
        }
        try {
            Assert.assertEquals(documentLine.getReserved7(), materialLot.getReserved6());
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "other1", documentLine.getReserved7(), materialLot.getReserved6());
        }
    }

    /**
     * 物料批次根据发货单进行发货，更新单据数据以及，更改ERP的中间表数据
     *  documentLine 产品型号 materialName，二级代码 reserved2，等级 reserved3,  物流 reserved7 一致
     *  materialLot 产品型号 materialName，二级代码 reserved1，等级 grade,  物料 reserved6 一致
     */
    public void stockOut(DocumentLine documentLine, List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            Set treasuryNoteInfo = materialLots.stream().map(materialLot -> materialLot.getReserved4()).collect(Collectors.toSet());
            if (treasuryNoteInfo != null &&  treasuryNoteInfo.size() > 1) {
                throw new ClientParameterException(GcExceptions.MATERIAL_LOT_TREASURY_INFO_IS_NOT_SAME);
            }
            for (MaterialLot materialLot : materialLots) {
                validationStockDocLine(documentLine, materialLot);
            }

            //获取发货的物料批次的快递单号
            String expressNumber = StringUtils.EMPTY;
            Map<String, List<MaterialLot>> mLotExpressMap = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getExpressNumber()))
                    .collect(Collectors.groupingBy(MaterialLot :: getExpressNumber));
            for (String expressId : mLotExpressMap.keySet()){
                expressNumber += expressId + StringUtils.SEMICOLON_CODE;
            }
            BigDecimal handledQty = BigDecimal.ZERO;
            for (MaterialLot materialLot : materialLots) {
                handledQty = handledQty.add(materialLot.getCurrentQty());
                // 变更事件，并清理掉库存
                materialLot.setCurrentQty(BigDecimal.ZERO);
                materialLot.setReserved12(documentLine.getObjectRrn().toString());
                changeMaterialLotStatusAndSaveHistory(materialLot);

                //箱中的真空包也触发出货事件，修改状态、记录历史
                List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                if(CollectionUtils.isNotEmpty(packageDetailLots)){
                    for (MaterialLot packageLot : packageDetailLots){
                        changeMaterialLotStatusAndSaveHistory(packageLot);
                    }
                }
            }

            // 验证当前操作数量是否超过待检查数量
            BigDecimal unHandleQty =  documentLine.getUnHandledQty().subtract(handledQty);
            if (unHandleQty.compareTo(BigDecimal.ZERO) < 0) {
                throw new ClientParameterException(GcExceptions.OVER_DOC_QTY);
            }

            documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());
            documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
            documentLine.setUnHandledQty(unHandleQty);
            documentLine.setExpressNumber(expressNumber);
            documentLine = documentLineRepository.saveAndFlush(documentLine);
            baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_SHIP);

            // 获取到主单据
            DeliveryOrder deliveryOrder = (DeliveryOrder) deliveryOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            deliveryOrder.setHandledQty(deliveryOrder.getHandledQty().add(handledQty));
            deliveryOrder.setUnHandledQty(deliveryOrder.getUnHandledQty().subtract(handledQty));
            deliveryOrder = deliveryOrderRepository.saveAndFlush(deliveryOrder);
            baseService.saveHistoryEntity(deliveryOrder, MaterialLotHistory.TRANS_TYPE_SHIP);

            validateDocAndUpdateErpSo(documentLine, handledQty);

            if (SystemPropertyUtils.getConnectMscmFlag()) {
                scmService.addScmTracking(documentLine.getDocId(), materialLots);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void changeMaterialLotStatusAndSaveHistory(MaterialLot materialLot) throws ClientException {
        try {
            mmsService.changeMaterialLotState(materialLot,  MaterialEvent.EVENT_SHIP, StringUtils.EMPTY);
            materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_SHIP);
            materialLotHistoryRepository.save(history);
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步接收单据
     * @throws ClientException
     */
    public void asyncReceiveOrder() throws ClientException {
        try {
            List<ErpSo> erpSos = erpSoRepository.findByTypeAndSynStatusNotIn(ErpSo.TYPE_TV, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS, ErpSo.SYNC_STATUS_MERGE));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(erpSos)) {
                Map<String, List<ErpSo>> documentIdMap = erpSos.stream().collect(Collectors.groupingBy(ErpSo :: getCcode));

                for (String documentId : documentIdMap.keySet()) {
                    List<ErpSo> documentIdList = documentIdMap.get(documentId);
                    //把即将同步的同Ccode数据按createseq分组
                    Map<String, List<ErpSo>> sameCreateSeqOrder = documentIdList.stream().filter(erpSo -> !StringUtils.isNullOrEmpty(erpSo.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpSo :: getCreateSeq));
                    List<ReceiveOrder> receiveOrderList = receiveOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(receiveOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            savaErpSoErrorInfo(documentIdList);
                            continue;
                        }
                    }
                    ReceiveOrder receiveOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(receiveOrderList)) {
                        //如果有不同create_seq
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpSo erpSo : documentIdList) {
                                asyncDuplicateSeqList.add(erpSo.getSeq());
                            }
                            continue;
                        }
                        receiveOrder = new ReceiveOrder();
                        receiveOrder.setName(documentId);
                        receiveOrder.setStatus(Document.STATUS_OPEN);
                        receiveOrder.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                    } else {
                        receiveOrder = receiveOrderList.get(0);
                        totalQty = receiveOrder.getQty();
                        boolean differentCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(receiveOrder.getReserved32())){
                                differentCreateSeq = true;
                                for  (ErpSo erpSo : documentIdList) {
                                    asyncDuplicateSeqList.add(erpSo.getSeq());
                                }
                                break;
                            }
                        }
                        if(differentCreateSeq){
                            continue;
                        }
                    }

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpSo erpSo : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (receiveOrder.getObjectRrn() != null) {
                                documentLine = validateDocQtyAndGetDocument(documentLine ,receiveOrder.getObjectRrn(), erpSo);
                            }

                            Date erpCreatedDate = DateUtils.parseDate(erpSo.getDdate());
                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            documentLine = validateAndSetErpSoToDocumentLine(documentLine, erpSo, erpCreatedDate, Document.CATEGORY_RECEIVE);

                            totalQty = totalQty.add(erpSo.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpSo.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLine.setUnReservedQty(documentLine.getQty().subtract(documentLine.getReservedQty()));
                            documentLines.add(documentLine);

                            // 同一个单据下，所有的客户都是一样的。
                            receiveOrder.setSupplierName(erpSo.getCusname());
                            receiveOrder.setOwner(erpSo.getChandler());
                            receiveOrder.setReserved32(erpSo.getCreateSeq());
                            if (receiveOrder.getErpCreated() == null) {
                                receiveOrder.setErpCreated(erpCreatedDate);
                            } else {
                                if (receiveOrder.getErpCreated().after(erpCreatedDate)) {
                                    receiveOrder.setErpCreated(erpCreatedDate);
                                }
                            }
                            asyncSuccessSeqList.add(erpSo.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpSo.setUserId(Document.SYNC_USER_ID);
                            erpSo.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpSo.setErrorMemo(e.getMessage());
                            erpSoRepository.save(erpSo);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        receiveOrder.setQty(totalQty);
                        receiveOrder.setUnHandledQty(receiveOrder.getQty().subtract(receiveOrder.getHandledQty()));
                        receiveOrder.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                        receiveOrder = (ReceiveOrder) baseService.saveEntity(receiveOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(receiveOrder);
                        baseService.saveEntity(documentLine);
                    }
                    // 保存单据的时候同步下客户
                    if (!StringUtils.isNullOrEmpty(receiveOrder.getSupplierName())) {
                        savaCustomer(receiveOrder.getSupplierName());
                    }
                }
                updateErpSoOrderSynStatusAndErrorMemoAndUserId(asyncSuccessSeqList, asyncDuplicateSeqList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步COG接收单据
     * @throws ClientException
     */
    public void asyncCogReceiveOrder() throws ClientException {
        try {
            List<ErpSo> erpSoList = erpSoRepository.findByTypeAndSynStatusNotIn(ErpSo.TYPE_COG, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS, ErpSo.SYNC_STATUS_MERGE));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(erpSoList)) {
                Map<String, List<ErpSo>> documentIdMap = erpSoList.stream().collect(Collectors.groupingBy(ErpSo :: getCcode));

                for (String documentId : documentIdMap.keySet()) {
                    List<ErpSo> erpSos = documentIdMap.get(documentId);
                    Map<String, List<ErpSo>> sameCreateSeqOrder = erpSos.stream().filter(erpSo -> !StringUtils.isNullOrEmpty(erpSo.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpSo :: getCreateSeq));
                    List<CogReceiveOrder> cogReceiveOrderList = cogReceiveOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(cogReceiveOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            savaErpSoErrorInfo(erpSos);
                            continue;
                        }
                    }
                    CogReceiveOrder cogReceiveOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(cogReceiveOrderList)) {
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpSo erpSo : erpSos) {
                                asyncDuplicateSeqList.add(erpSo.getSeq());
                            }
                            continue;
                        }
                        cogReceiveOrder = new CogReceiveOrder();
                        cogReceiveOrder.setName(documentId);
                        cogReceiveOrder.setStatus(Document.STATUS_OPEN);
                        cogReceiveOrder.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                    } else {
                        cogReceiveOrder = cogReceiveOrderList.get(0);
                        totalQty = cogReceiveOrder.getQty();
                        boolean difCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(cogReceiveOrder.getReserved32())){
                                difCreateSeq = true;
                                for  (ErpSo erpSo : erpSos) {
                                    asyncDuplicateSeqList.add(erpSo.getSeq());
                                }
                                break;
                            }
                        }
                        if(difCreateSeq){
                            continue;
                        }
                    }

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpSo erpSo : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (cogReceiveOrder.getObjectRrn() != null) {
                                documentLine = validateDocQtyAndGetDocument(documentLine ,cogReceiveOrder.getObjectRrn(), erpSo);
                            }

                            Date erpCreatedDate = DateUtils.parseDate(erpSo.getDdate());
                            documentLine = validateAndSetErpSoToDocumentLine(documentLine, erpSo, erpCreatedDate, CogReceiveOrder.CATEGORY_COG_RECEIVE);

                            totalQty = totalQty.add(erpSo.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpSo.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLine.setUnReservedQty(documentLine.getQty().subtract(documentLine.getReservedQty()));
                            documentLines.add(documentLine);

                            cogReceiveOrder.setReserved32(erpSo.getCreateSeq());
                            cogReceiveOrder.setOwner(erpSo.getChandler());
                            cogReceiveOrder.setSupplierName(erpSo.getCusname());
                            if (cogReceiveOrder.getErpCreated() == null) {
                                cogReceiveOrder.setErpCreated(erpCreatedDate);
                            } else {
                                if (cogReceiveOrder.getErpCreated().after(erpCreatedDate)) {
                                    cogReceiveOrder.setErpCreated(erpCreatedDate);
                                }
                            }
                            asyncSuccessSeqList.add(erpSo.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpSo.setUserId(Document.SYNC_USER_ID);
                            erpSo.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpSo.setErrorMemo(e.getMessage());
                            erpSoRepository.save(erpSo);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        cogReceiveOrder.setQty(totalQty);
                        cogReceiveOrder.setUnHandledQty(cogReceiveOrder.getQty().subtract(cogReceiveOrder.getHandledQty()));
                        cogReceiveOrder.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                        cogReceiveOrder = (CogReceiveOrder) baseService.saveEntity(cogReceiveOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(cogReceiveOrder);
                        baseService.saveEntity(documentLine);
                    }
                    if (!StringUtils.isNullOrEmpty(cogReceiveOrder.getSupplierName())) {
                        savaCustomer(cogReceiveOrder.getSupplierName());
                    }
                }
                updateErpSoOrderSynStatusAndErrorMemoAndUserId(asyncSuccessSeqList, asyncDuplicateSeqList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private DocumentLine validateDocQtyAndGetDocument(DocumentLine documentLine ,Long objectRrn, ErpSo erpSo) throws ClientException{
        try {
            documentLine = documentLineRepository.findByDocRrnAndReserved1(objectRrn, String.valueOf(erpSo.getSeq()));
            if (documentLine != null) {
                if (ErpSo.SYNC_STATUS_CHANGED.equals(erpSo.getSynStatus())) {
                    if (documentLine != null && documentLine.getHandledQty().compareTo(erpSo.getIquantity()) > 0) {
                        throw new ClientException("gc.order_handled_qty_gt_qty");
                    }
                }
            }
            return documentLine;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void savaErpSoErrorInfo(List<ErpSo> documentIdList) throws ClientException{
        try {
            if(CollectionUtils.isNotEmpty(documentIdList)){
                for(ErpSo erpSo : documentIdList){
                    erpSo.setUserId(Document.SYNC_USER_ID);
                    erpSo.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                    erpSo.setErrorMemo(ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID);
                    erpSoRepository.save(erpSo);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void updateErpSoOrderSynStatusAndErrorMemoAndUserId(List<Long> asyncSuccessSeqList, List<Long> asyncDuplicateSeqList) throws ClientException{
        try {
            if (CollectionUtils.isNotEmpty(asyncSuccessSeqList)) {
                if(asyncSuccessSeqList.size() >= Document.SEQ_MAX_LENGTH){
                    List<List<Long>> successSeqGroupList = getSeqListGroupByCount(asyncSuccessSeqList);
                    for(List seqGroupList : successSeqGroupList){
                        erpSoRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, seqGroupList);
                    }
                } else {
                    erpSoRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, asyncSuccessSeqList);
                }
            }

            if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                if(asyncDuplicateSeqList.size() >= Document.SEQ_MAX_LENGTH){
                    List<List<Long>> duplicateSeqGroupList = getSeqListGroupByCount(asyncDuplicateSeqList);
                    for(List seqGroupList : duplicateSeqGroupList){
                        erpSoRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_ERROR,ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, seqGroupList);
                    }
                } else {
                    erpSoRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_ERROR,ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, asyncDuplicateSeqList);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private DocumentLine validateAndSetErpSoToDocumentLine(DocumentLine documentLine, ErpSo erpSo, Date erpCreatedDate, String docType) throws ClientException{
        try {
            if (documentLine == null) {
                documentLine = new DocumentLine();
                Material material = new Material();
                if(DeliveryOrder.CATEGORY_DELIVERY.equals(docType)){
                    material = mmsService.getProductByName(erpSo.getCinvcode());
                    if (material == null) {
                        throw new ClientParameterException(MM_PRODUCT_ID_IS_NOT_EXIST, erpSo.getCinvcode());
                    }
                } else {
                    material = mmsService.getRawMaterialByName(erpSo.getCinvcode());
                    if (material == null) {
                        throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, erpSo.getCinvcode());
                    }
                }
                documentLine.setErpCreated(erpCreatedDate);
                documentLine.setMaterialRrn(material.getObjectRrn());
                documentLine.setMaterialName(material.getName());
                documentLine.setReserved1(String.valueOf(erpSo.getSeq()));
                documentLine.setReserved2(erpSo.getSecondcode());
                documentLine.setReserved3(erpSo.getGrade());
                documentLine.setReserved4(erpSo.getCfree3());
                documentLine.setReserved5(erpSo.getCmaker());
                documentLine.setReserved6(erpSo.getChandler());
                documentLine.setReserved7(erpSo.getOther1());

                documentLine.setReserved8(erpSo.getCusname());
                documentLine.setReserved9(docType);

                documentLine.setReserved10(erpSo.getGCode());
                documentLine.setReserved11(erpSo.getGName());
                documentLine.setReserved12(erpSo.getOther8());
                documentLine.setReserved15(erpSo.getOther18());
                documentLine.setReserved17(erpSo.getOther3());
                documentLine.setReserved20(erpSo.getOther9());
                documentLine.setReserved21(erpSo.getOther10());
                documentLine.setReserved27(erpSo.getOther7());
                documentLine.setReserved28(erpSo.getOther4());
                documentLine.setReserved30(erpSo.getOther5());
                documentLine.setDocType(erpSo.getCvouchtype());
                documentLine.setDocName(erpSo.getCvouchname());
                documentLine.setDocBusType(erpSo.getCbustype());
                documentLine.setDocSource(erpSo.getCsource());
                documentLine.setWarehouseCode(erpSo.getCwhcode());
                documentLine.setWarehouseName(erpSo.getCwhname());
                documentLine.setReserved31(ErpSo.SOURCE_TABLE_NAME);
            }
            return documentLine;
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 对同步的seq进行分组，每组小于1000
     * @param asyncSeqList
     * @return
     * @throws ClientException
     */
    private List<List<Long>> getSeqListGroupByCount(List<Long> asyncSeqList) throws ClientException{
        try {
            List<List<Long>> seqGroupList = Lists.newArrayList();
            int length = Document.SEQ_LENGTH;

            int size = asyncSeqList.size();
            int count = (size + length - 1) / length;
            for (int i = 0; i < count; i++) {
                List<Long> seqGroup = asyncSeqList.subList(i * length, ((i + 1) * length > size ? size : length * (i + 1)));
                seqGroupList.add(seqGroup);
            }
            return seqGroupList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步出货单
     * @throws ClientException
     */
    public void asyncShipOrder() throws ClientException {
        try {
            List<ErpSo> erpSos = erpSoRepository.findByTypeAndSynStatusNotIn(ErpSo.TYPE_SO, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS, ErpSo.SYNC_STATUS_MERGE));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(erpSos)) {
                Map<String, List<ErpSo>> documentIdMap = erpSos.stream().collect(Collectors.groupingBy(ErpSo :: getCcode));

                for (String documentId : documentIdMap.keySet()) {
                    List<ErpSo> documentIdList = documentIdMap.get(documentId);
                    //把即将同步的同Ccode数据按createseq分组
                    Map<String, List<ErpSo>> sameCreateSeqOrder = documentIdList.stream().filter(erpSo -> !StringUtils.isNullOrEmpty(erpSo.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpSo :: getCreateSeq));
                    List<DeliveryOrder> deliveryOrderList = deliveryOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(deliveryOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            savaErpSoErrorInfo(erpSos);
                            continue;
                        }
                    }
                    DeliveryOrder deliveryOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(deliveryOrderList)) {
                        //如果有不同create_seq
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpSo erpSo : documentIdList) {
                                asyncDuplicateSeqList.add(erpSo.getSeq());
                            }
                            continue;
                        }
                        deliveryOrder = new DeliveryOrder();
                        deliveryOrder.setName(documentId);
                        deliveryOrder.setStatus(Document.STATUS_OPEN);
                        deliveryOrder.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                    } else {
                        deliveryOrder = deliveryOrderList.get(0);
                        totalQty = deliveryOrder.getQty();
                        boolean differentCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(deliveryOrder.getReserved32())){
                                differentCreateSeq = true;
                                for  (ErpSo erpSo : documentIdList) {
                                    asyncDuplicateSeqList.add(erpSo.getSeq());
                                }
                                break;
                            }
                        }
                        if(differentCreateSeq){
                            continue;
                        }
                    }

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpSo erpSo : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (deliveryOrder.getObjectRrn() != null) {
                                documentLine = validateDocQtyAndGetDocument(documentLine ,deliveryOrder.getObjectRrn(), erpSo);
                            }

                            Date erpCreatedDate = DateUtils.parseDate(erpSo.getDdate());
                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            documentLine = validateAndSetErpSoToDocumentLine(documentLine, erpSo, erpCreatedDate, DeliveryOrder.CATEGORY_DELIVERY);

                            totalQty = totalQty.add(erpSo.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpSo.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLine.setUnReservedQty(documentLine.getQty().subtract(documentLine.getReservedQty()));
                            documentLines.add(documentLine);

                            // 同一个单据下，所有的客户都是一样的。
                            deliveryOrder.setSupplierName(erpSo.getCusname());
                            deliveryOrder.setOwner(erpSo.getChandler());
                            deliveryOrder.setReserved32(erpSo.getCreateSeq());
                            if (deliveryOrder.getErpCreated() == null) {
                                deliveryOrder.setErpCreated(erpCreatedDate);
                            } else {
                                if (deliveryOrder.getErpCreated().after(erpCreatedDate)) {
                                    deliveryOrder.setErpCreated(erpCreatedDate);
                                }
                            }
                            asyncSuccessSeqList.add(erpSo.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpSo.setUserId(Document.SYNC_USER_ID);
                            erpSo.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpSo.setErrorMemo(e.getMessage());
                            erpSoRepository.save(erpSo);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        deliveryOrder.setQty(totalQty);
                        deliveryOrder.setUnHandledQty(deliveryOrder.getQty().subtract(deliveryOrder.getHandledQty()));
                        deliveryOrder.setUnReservedQty(deliveryOrder.getQty().subtract(deliveryOrder.getReservedQty()));
                        deliveryOrder.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                        deliveryOrder = (DeliveryOrder) baseService.saveEntity(deliveryOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(deliveryOrder);
                        baseService.saveEntity(documentLine);
                    }

                    // 保存单据的时候同步下客户
                    if (!StringUtils.isNullOrEmpty(deliveryOrder.getSupplierName())) {
                        savaCustomer(deliveryOrder.getSupplierName());
                    }
                }
                updateErpSoOrderSynStatusAndErrorMemoAndUserId(asyncSuccessSeqList, asyncDuplicateSeqList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 出货前检查。
     *  直接以检查结果做状态 当stockOutCheckList为空的时候就是OK。因为GC要求，OK时候不记录检查项
     * @param materialLots
     * @param stockOutCheckList 检查项
     * @return
     */
    public void stockOutCheck(List<MaterialLot> materialLots, List<StockOutCheck> stockOutCheckList) throws ClientException {
        try {
            String checkResult = StockOutCheck.RESULT_OK;
            List<StockOutCheck> ngStockOutCheckList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(stockOutCheckList)) {
                ngStockOutCheckList = stockOutCheckList.stream().filter(checkItem -> StockOutCheck.RESULT_NG.equals(checkItem.getResult())).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(ngStockOutCheckList)) {
                checkResult = StockOutCheck.RESULT_NG;
            }

            for (MaterialLot materialLot : materialLots) {
                //20190917 GC要求 如果判了NG。并且装箱检查是PASS的，将PASS改成PASS0
                if (StockOutCheck.RESULT_NG.equals(checkResult) && StockOutCheck.RESULT_PASS.equals(materialLot.getReserved9())) {
                    materialLot.setReserved9(materialLot.getReserved9() + "0");

                    // 20190921 GC要求，被包装的批次都需要也需要改成materialLot相关信息
                    List<MaterialLot> packedMaterialLots = materialLotRepository.getPackageDetailLots(materialLot.getObjectRrn());
                    if (CollectionUtils.isNotEmpty(packedMaterialLots)) {
                        for (MaterialLot packedMaterialLot : packedMaterialLots) {
                            packedMaterialLot.setReserved9(materialLot.getReserved9());
                            materialLotRepository.save(packedMaterialLot);
                        }
                    }
                } else if(StockOutCheck.RESULT_OK.equals(checkResult) && MaterialLot.STATUS_STOCK.equals(materialLot.getStatusCategory())){
                    materialLot.setReserved9(StockOutCheck.RESULT_PASS );
                } else if(StockOutCheck.RESULT_NG.equals(checkResult) && MaterialLot.STATUS_STOCK.equals(materialLot.getStatusCategory())){
                    materialLot.setReserved9(StockOutCheck.RESULT_PASS + "0");
                }

                materialLot = mmsService.changeMaterialLotState(materialLot, EVENT_OQC, checkResult);
//              GC要求只记录NG的判定历史即可
                if (CollectionUtils.isNotEmpty(ngStockOutCheckList)) {
                    // 保存每个项目的判定结果
                    MaterialLot finalMaterialLot = materialLot;
                    ngStockOutCheckList.forEach(stockOutCheck -> {
                        MaterialLotJudgeHis materialLotJudgeHis = new MaterialLotJudgeHis();
                        materialLotJudgeHis.setMaterialLotRrn(finalMaterialLot.getObjectRrn());
                        materialLotJudgeHis.setMaterialLotId(finalMaterialLot.getMaterialLotId());
                        materialLotJudgeHis.setItemName(stockOutCheck.getName());
                        materialLotJudgeHis.setResult(stockOutCheck.getResult());
                        materialLotJudgeHis.setTransType(MaterialLotJudgeHis.TRANS_TYPE_OQC);
                        materialLotJudgeHis.setHisSeq(ThreadLocalContext.getTransRrn());
                        materialLotJudgeHisRepository.save(materialLotJudgeHis);
                    });
                }
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_OQC);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取装箱检验的检查项
     * @return
     * @throws ClientException
     */
    public List<NBOwnerReferenceList> getJudgePackCaseCheckList() throws ClientException {
        List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(REFERENCE_NAME_PACK_CASE_CHECK_ITEM_LIST, NBReferenceList.CATEGORY_OWNER);
        if (CollectionUtils.isNotEmpty(nbReferenceList)) {
            return nbReferenceList;
        }
        return Lists.newArrayList();
    }

    public List<NBOwnerReferenceList> getWltJudgePackCaseCheckList() throws ClientException {
        List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(REFERENCE_NAME_WLTPACK_CASE_CHECK_ITEM_LIST, NBReferenceList.CATEGORY_OWNER);
        if (CollectionUtils.isNotEmpty(nbReferenceList)) {
            return nbReferenceList;
        }
        return Lists.newArrayList();
    }

    public List<NBOwnerReferenceList> getStockOutCheckList() throws ClientException {
        List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(REFERENCE_NAME_STOCK_OUT_CHECK_ITEM_LIST, NBReferenceList.CATEGORY_OWNER);
        if (CollectionUtils.isNotEmpty(nbReferenceList)) {
            return nbReferenceList;
        }
        return Lists.newArrayList();
    }

    public List<NBOwnerReferenceList> getWltStockOutCheckList() throws ClientException {
        List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(REFERENCE_NAME_WLTSTOCK_OUT_CHECK_ITEM_LIST, NBReferenceList.CATEGORY_OWNER);
        if (CollectionUtils.isNotEmpty(nbReferenceList)) {
            return nbReferenceList;
        }
        return Lists.newArrayList();
    }

    public List<NBOwnerReferenceList> getReferenceListByName(String reserenceName) throws ClientException {
        List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(reserenceName, NBReferenceList.CATEGORY_OWNER);
        if (CollectionUtils.isNotEmpty(nbReferenceList)) {
            return nbReferenceList;
        }
        return Lists.newArrayList();
    }

    public MesPackedLot findByPackedLotRrn(Long packedLotRrn) throws ClientException {
        return mesPackedLotRepository.findByPackedLotRrn(packedLotRrn);
    }

    public List<MesPackedLot> findByParentRrn(Long parentRrn) throws ClientException {
        return mesPackedLotRepository.findByParentRrn(parentRrn);
    }
    /**
     * 接收MES的完成品
     * @param packedLotList
     */
    public void receiveFinishGood(List<MesPackedLot> packedLotList) throws ClientException {
        try {
            Map<String, List<MesPackedLot>> packedLotMap = packedLotList.stream().collect(Collectors.groupingBy(MesPackedLot :: getProductId));
            Map<String, Warehouse> warehouseMap = Maps.newHashMap();

            List<MaterialLot> scmReportHoldMLotList = Lists.newArrayList();
            List<ErpMo> erpMos = Lists.newArrayList();
            List<ErpMoa> erpMoaList = Lists.newArrayList();
            MesPackedLotRelation mesPackedLotRelation;

            for (String productId : packedLotMap.keySet()) {
                List<MesPackedLot> mesPackedLotList = packedLotMap.get(productId);
                Material material = mmsService.getProductByName(productId);
                if (material == null) {
                    throw new ClientParameterException(MM_PRODUCT_ID_IS_NOT_EXIST, productId);
                }

                List<MesPackedLot> mesPackedLots = packedLotMap.get(productId);
                List<MaterialLotAction> materialLotActions = Lists.newArrayList();
                for (MesPackedLot mesPackedLot : mesPackedLots) {
                    String productCateGory = mesPackedLot.getProductCategory();
                    if(MesPackedLot.PRODUCT_CATEGORY_FT.equals(productCateGory) || MesPackedLot.PRODUCT_CATEGORY_WLFT.equals(productCateGory)){
                        if(MesPackedLot.REPLACE_FLAG.equals(mesPackedLot.getReplaceFlag())){
                            if(!StringUtils.isNullOrEmpty(mesPackedLot.getPrintModelId())){
                                material = mmsService.getProductByName(mesPackedLot.getPrintModelId());
                                if (material == null) {
                                    throw new ClientParameterException(MM_PRODUCT_ID_IS_NOT_EXIST, mesPackedLot.getPrintModelId());
                                }
                            }
                        }
                    }
                    //WLT产线接收时验证MM_PACKEND_LOT_RELATION表中有没有记录物料编码信息
                    if(!StringUtils.isNullOrEmpty(mesPackedLot.getCstId())){
                        List<MesPackedLot> mesPackedLotUnits = mesPackedLotRepository.findByCstIdAndPackedStatusAndWaferIdIsNotNull(mesPackedLot.getCstId(), MesPackedLot.PACKED_STATUS_IN);
                        mesPackedLotRelation = mesPackedLotRelationRepository.findByPackedLotRrn(mesPackedLotUnits.get(0).getPackedLotRrn());
                    } else {
                        mesPackedLotRelation = mesPackedLotRelationRepository.findByPackedLotRrn(mesPackedLotList.get(0).getPackedLotRrn());
                    }
                    if(!MesPackedLot.PRODUCT_CATEGORY_COB.equals(mesPackedLot.getProductCategory()) &&
                            !MesPackedLot.PRODUCT_CATEGORY_COM.equals(mesPackedLot.getProductCategory()) &&
                            !(MesPackedLot.PRODUCT_CATEGORY_FT.equals(mesPackedLot.getProductCategory()) && MaterialLotUnit.BOX_TYPE.equals(mesPackedLot.getType()))){
                        if(mesPackedLotRelation == null){
                            throw new ClientException(GcExceptions.CORRESPONDING_RAW_MATERIAL_INFO_IS_EMPTY);
                        } else {
                            //获取mm_packend_lot_Relation表中的Vender（供应商）存入GC_SUPPLIER表中
                            String vender = mesPackedLotRelation.getVender();
                            if(!StringUtils.isNullOrEmpty(vender)){
                                Supplier supplier = supplierRepository.getByNameAndType(vender, Supplier.TYPE_VENDER);
                                if(supplier == null){
                                    supplier = new Supplier();
                                    supplier.setName(vender);
                                    supplier.setType(Supplier.TYPE_VENDER);
                                    supplierRepository.save(supplier);
                                }
                            }
                        }
                    }

                    MaterialLotAction materialLotAction = new MaterialLotAction();
                    materialLotAction.setMaterialLotId(mesPackedLot.getBoxId());
                    materialLotAction.setGrade(mesPackedLot.getGrade());
                    materialLotAction.setTransQty(BigDecimal.valueOf(mesPackedLot.getQuantity()));
                    materialLotAction.setSourceModelId(mesPackedLot.getProductId());

                    // 真空包产地是SH的入SH仓库，是ZJ的入浙江仓库
                    // 20191217 产地是空的话则是ZJ仓库
                    String warehouseName = WAREHOUSE_ZJ;
                    if (!StringUtils.isNullOrEmpty(mesPackedLot.getLocation()) && mesPackedLot.getLocation().equalsIgnoreCase("SH")) {
                        warehouseName = WAREHOUSE_SH;
                    }

                    Warehouse warehouse;
                    if (!warehouseMap.containsKey(warehouseName)) {
                        warehouse = mmsService.getWarehouseByName(warehouseName);
                        if (warehouse == null) {
                            warehouse = new Warehouse();
                            warehouse.setName(warehouseName);
                            warehouse = warehouseRepository.saveAndFlush(warehouse);
                        }
                        warehouseMap.put(warehouseName, warehouse);
                    }
                    warehouse = warehouseMap.get(warehouseName);

                    materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());

                    // 需要赋值的Map
                    Map<String, Object> otherReceiveProps = Maps.newHashMap();
                    otherReceiveProps.put("reserved1", mesPackedLot.getLevelTwoCode());
                    otherReceiveProps.put("reserved2", mesPackedLot.getWaferId());
                    otherReceiveProps.put("reserved3", mesPackedLot.getSalesNote());
                    otherReceiveProps.put("reserved4", mesPackedLot.getTreasuryNote());
                    otherReceiveProps.put("reserved5", mesPackedLot.getProductionNote());
                    otherReceiveProps.put("reserved6", mesPackedLot.getBondedProperty());
                    otherReceiveProps.put("reserved13", warehouse.getObjectRrn().toString());
                    otherReceiveProps.put("workOrderId", mesPackedLot.getWorkorderId());
                    otherReceiveProps.put("productType", mesPackedLot.getProductType());
                    otherReceiveProps.put("reserved49", mesPackedLot.getImportType());
                    otherReceiveProps.put("reserved21", mesPackedLot.getErpProductId());
                    otherReceiveProps.put("reserved24", mesPackedLot.getFabDevice());
                    if(mesPackedLotRelation != null && MaterialLotUnit.PRODUCT_CATEGORY_WLT.equals(mesPackedLot.getProductCategory())){
                        otherReceiveProps.put("reserved22", mesPackedLotRelation.getVender());
                    } else if(MaterialLotUnit.PRODUCT_CATEGORY_WLFT.equals(mesPackedLot.getProductCategory()) || (MaterialLotUnit.PRODUCT_CATEGORY_FT.equals(mesPackedLot.getProductCategory()) && MaterialLotUnit.BOX_TYPE.equals(mesPackedLot.getType()))){
                        otherReceiveProps.put("reserved22", MesPackedLot.ZJ_SUB_NAME);
                    } else {
                        otherReceiveProps.put("reserved22", mesPackedLot.getSubName());
                    }
                    if(mesPackedLotRelation != null){
                        otherReceiveProps.put("reserved25", mesPackedLotRelation.getWaferProperty());
                    }
                    otherReceiveProps.put("lotId", mesPackedLot.getCstId());
                    String productCategory = mesPackedLot.getProductCategory();
                    if(!StringUtils.isNullOrEmpty(productCategory)){
                        mLotSetWaferSourceAndReserved7(otherReceiveProps, productCategory, mesPackedLot);
                    }
                    if(mesPackedLot.getWaferQty() != null){
                        BigDecimal waferQty = new BigDecimal(mesPackedLot.getWaferQty().toString());
                        materialLotAction.setTransCount(waferQty);
                    }
                    materialLotAction.setPropsMap(otherReceiveProps);

                    materialLotActions.add(materialLotAction);

                    if((MesPackedLot.PRODUCT_CATEGORY_FT.equals(productCateGory) && !MaterialLotUnit.BOX_TYPE.equals(mesPackedLot.getType()))  || MesPackedLot.PRODUCT_CATEGORY_WLFT.equals(productCateGory)){
                        // ERP_MOA插入数据
                        ErpMoa erpMoa = new ErpMoa();
                        erpMoa.setFQty(mesPackedLot.getQuantity());
                        erpMoa.setWarehouseCode(warehouseName);
                        erpMoa.setMesPackedLot(mesPackedLot);
                        //从MM_PACKED_LOT_RELATION表中获取物料型号、物料数据等相关数据
                        erpMoa.setCMemo("EMPTY");
                        erpMoa.setMaterialBonded(mesPackedLotRelation.getMaterialBonded());
                        erpMoa.setMaterialCode(mesPackedLotRelation.getMaterialCode());
                        erpMoa.setMaterialQty(mesPackedLotRelation.getMaterialQty());
                        erpMoa.setMaterialGrade(mesPackedLotRelation.getMaterialGrade());
                        erpMoa.setMaterialVersion(mesPackedLotRelation.getMaterialVersion());
                        erpMoa.setProdCate(mesPackedLotRelation.getProductType());

                        erpMoaList.add(erpMoa);
                    } else if(MesPackedLot.PRODUCT_CATEGORY_CP.equals(productCateGory) || MesPackedLot.PRODUCT_CATEGORY_WLT.equals(productCateGory)
                            || MesPackedLot.PRODUCT_CATEGORY_LSP.equals(productCateGory) || MesPackedLot.PRODUCT_CATEGORY_LCP.equals(productCateGory)
                            || MesPackedLot.PRODUCT_CATEGORY_SCP.equals(productCateGory)){
                        // ERP_MOA插入数据
                        ErpMoa erpMoa = new ErpMoa();
                        erpMoa.setFQty(mesPackedLot.getWaferQty());
                        erpMoa.setWarehouseCode(warehouseName);
                        erpMoa.setMesPackedLot(mesPackedLot);

                        //从MM_PACKED_LOT_RELATION表中获取物料型号、物料数据等相关数据
                        erpMoa.setCMemo("EMPTY");
                        erpMoa.setMaterialBonded(mesPackedLotRelation.getMaterialBonded());
                        erpMoa.setMaterialCode(mesPackedLotRelation.getMaterialCode());
                        erpMoa.setMaterialQty(mesPackedLot.getWaferQty());
                        erpMoa.setMaterialGrade(mesPackedLotRelation.getMaterialGrade());
                        erpMoa.setMaterialVersion(mesPackedLotRelation.getMaterialVersion());
                        erpMoa.setProdCate(mesPackedLotRelation.getProductType());

                        erpMoaList.add(erpMoa);
                    } else if(MesPackedLot.PRODUCT_CATEGORY_COM.equals(productCateGory)){
                        // ERP MO插入数据
                        ErpMo erpMo = new ErpMo();
                        erpMo.setCCode(mesPackedLot.getShipSerialNumber());
                        erpMo.setDDate(mesPackedLot.getFinalOperationTime());
                        erpMo.setSecondcode(mesPackedLot.getLevelTwoCode());
                        erpMo.setCWHCode(warehouseName);
                        if (StringUtils.isNullOrEmpty(mesPackedLot.getWorkorderId())) {
                            erpMo.setCmoCode(ErpMo.DEFAULT_WO_ID);
                        } else {
                            erpMo.setCmoCode(mesPackedLot.getWorkorderId());
                        }
                        if(StringUtils.isNullOrEmpty(mesPackedLot.getErpProductId())){
                            erpMo.setCinVCode(mesPackedLot.getProductId());
                        } else {
                            erpMo.setCinVCode(mesPackedLot.getErpProductId());
                        }
                        erpMo.setFQty(mesPackedLot.getQuantity());

                        erpMo.setCGrade(mesPackedLot.getGrade());
                        erpMo.setBonded(mesPackedLot.getBondedProperty());
                        erpMos.add(erpMo);
                    }
                }
                List<MaterialLot> materialLotList = mmsService.receiveMLotList2Warehouse(material, materialLotActions);
                for(MaterialLot materialLot : materialLotList){
                    String workOrderId = materialLot.getWorkOrderId();
                    String grade = materialLot.getGrade();
                    String lotId = materialLot.getLotId();
                    GCWorkorderRelation workorderRelation = workorderRelationRepository.findByWorkOrderIdAndGrade(workOrderId, grade);
                    if(workorderRelation == null){
                        workorderRelation = workorderRelationRepository.findByWorkOrderIdAndGradeIsNull(workOrderId);
                    }
                    if(workorderRelation == null){
                        workorderRelation = workorderRelationRepository.findByGradeAndWorkOrderIdIsNull(grade);
                    }
                    if(workorderRelation != null){
                        MaterialLotAction materialLotAction = new MaterialLotAction();
                        materialLotAction.setActionReason(workorderRelation.getHoldReason());
                        mmsService.holdMaterialLot(materialLot, materialLotAction);

                        if(!StringUtils.isNullOrEmpty(materialLot.getLotId()) && (MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(materialLot.getReserved7())
                                || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CLASSIFY_CP.equals(materialLot.getReserved7()))){
                            scmReportHoldMLotList.add(materialLot);
                        }
                    }
                    GCFutureHoldConfig futureHoldConfig = futureHoldConfigRepository.findByLotId(lotId);
                    if (futureHoldConfig != null){
                        MaterialLotAction materialLotAction = new MaterialLotAction();
                        materialLotAction.setActionReason(futureHoldConfig.getHoldReason());
                        mmsService.holdMaterialLot(materialLot, materialLotAction);

                        if(MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CLASSIFY_CP.equals(materialLot.getReserved7())){
                            scmReportHoldMLotList.add(materialLot);
                        }
                    }
                }
            };

            packedLotList = packedLotList.stream().filter(packedLot -> packedLot.getPackedLotRrn() != null).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(packedLotList)){
                mesPackedLotRepository.updatePackedStatusByPackedLotRrnList(MesPackedLot.PACKED_STATUS_RECEIVED, packedLotList.stream().map(MesPackedLot :: getPackedLotRrn).collect(Collectors.toList()));
            }

            if(CollectionUtils.isNotEmpty(scmReportHoldMLotList)){
                scmService.sendMaterialStateReport(scmReportHoldMLotList, MaterialLotStateReportRequestBody.ACTION_TYPE_HOLD, ThreadLocalContext.getSessionContext());
            }

            //TODO 如果后续有ERP dblink问题。此处需要考虑批量插入。JPA提供的saveAll本质也是for循环调用save。
            if(CollectionUtils.isNotEmpty(erpMos)){
                erpMoRepository.saveAll(erpMos);
            }
            if(CollectionUtils.isNotEmpty(erpMoaList)){
                erpMoaRepository.saveAll(erpMoaList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void mLotSetWaferSourceAndReserved7(Map<String,Object> otherReceiveProps, String productCategory, MesPackedLot mesPackedLot) throws ClientException{
        try {
            String inFlag = mesPackedLot.getInFlag();
            String type = mesPackedLot.getType();
            String productId = mesPackedLot.getProductId();

            if(MaterialLot.PRODUCT_CATEGORY.equals(productCategory)){
                otherReceiveProps.put("reserved50", MaterialLot.COM_WAFER_SOURCE);
                otherReceiveProps.put("reserved7", productCategory);
            } else if(MaterialLotUnit.PRODUCT_CATEGORY_WLT.equals(productCategory)){
                if(MaterialLot.MM_PACKED_LOTIN_FLAG.equals(inFlag)){
                    otherReceiveProps.put("reserved7", MaterialLot.WLT_IN_FLAG_PRODUCTCATEGORY);
                } else {
                    otherReceiveProps.put("reserved7", productCategory);
                }
                if(productId.endsWith("-2.5")){
                    otherReceiveProps.put("reserved50", MaterialLot.WLT_IN_FLAG_WAFER_SOURCE);
                } else if(productId.endsWith("-2.6")){
                    otherReceiveProps.put("reserved50", MaterialLot.WLT_WAFER_SOURCE);
                } else {
                    otherReceiveProps.put("reserved50", MaterialLot.ERROR_WAFER_SOUCE);
                }
            } else if(MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(productCategory)){
                if(MaterialLot.MM_PACKED_LOTIN_FLAG.equals(inFlag)){
                    otherReceiveProps.put("reserved7", MaterialLot.CP_IN_FLAG_PRODUCTCATEGORY);
                } else {
                    otherReceiveProps.put("reserved7", productCategory);
                }
                if(productId.endsWith("-1") || productId.endsWith("-2.5")){
                    otherReceiveProps.put("reserved50", MaterialLot.LCP_IN_FLAG_WAFER_SOURCE);
                } else if(productId.endsWith("-2.6")){
                    otherReceiveProps.put("reserved50", MaterialLot.LCP_WAFER_SOURCE);
                } else {
                    otherReceiveProps.put("reserved50", MaterialLot.ERROR_WAFER_SOUCE);
                }
            } else if(MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(productCategory)){
                if(MaterialLot.MM_PACKED_LOTIN_FLAG.equals(inFlag)){
                    otherReceiveProps.put("reserved7", MaterialLot.CP_IN_FLAG_PRODUCTCATEGORY);
                } else {
                    otherReceiveProps.put("reserved7", productCategory);
                }
                if(productId.endsWith("-0") || productId.endsWith("-1") || productId.endsWith("-1.3") || productId.endsWith("-1.5") || productId.endsWith("-1.6") || productId.endsWith("-2")){
                    otherReceiveProps.put("reserved50", MaterialLot.SCP_IN_FLAG_WAFER_SOURCE);
                } else if(productId.endsWith("-1.1") || productId.endsWith("-1.4") || productId.endsWith("-2.1")){
                    otherReceiveProps.put("reserved50", MaterialLot.SCP_WAFER_SOURCE);
                } else {
                    otherReceiveProps.put("reserved50", MaterialLot.ERROR_WAFER_SOUCE);
                }
            } else if(MaterialLotUnit.PRODUCT_CATEGORY_FT.equals(productCategory)){
                if(MaterialLotUnit.BOX_TYPE.equals(type)){
                    otherReceiveProps.put("reserved50", MaterialLot.FT_COB_WAFER_SOURCE);
                    otherReceiveProps.put("reserved7", MaterialLotUnit.PRODUCT_CATEGORY_FT_COB);
                } else {
                    otherReceiveProps.put("reserved50", MaterialLot.FT_WAFER_SOURCE);
                    otherReceiveProps.put("reserved7", productCategory);
                }

            } else if(MaterialLotUnit.PRODUCT_CATEGORY_WLFT.equals(productCategory)){
                otherReceiveProps.put("reserved50", MaterialLot.WLFT_WAFER_SOURCE);
                otherReceiveProps.put("reserved7", productCategory);
            } else if(MaterialLotUnit.PRODUCT_CATEGORY_FT_COB.equals(productCategory)){
                otherReceiveProps.put("reserved50", MaterialLot.COB_WAFER_SOURCE);
                otherReceiveProps.put("reserved7", productCategory);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次绑定中转箱
     * @throws ClientException
     */
    public void bindRelaxBox(List<MaterialLot> materialLots, String relaxBoxId) throws ClientException{
        try {
            materialLots.forEach(materialLot -> {
                materialLot.setReserved8(relaxBoxId);
                materialLotRepository.save(materialLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_BIND_RELAY_BOX);
                materialLotHistoryRepository.save(history);
            });
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }

    }

    /**
     * 物料批次取消绑定中转箱
     * @throws ClientException
     */
    public void unbindRelaxBox(List<MaterialLot> materialLots) throws ClientException{
        try {
            materialLots.forEach(materialLot -> {
                materialLot.setReserved8(StringUtils.EMPTY);
                materialLotRepository.save(materialLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_UNBIND_RELAY_BOX);
                materialLotHistoryRepository.save(history);
            });
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }

    }

    public void judgeMaterialLot(MaterialLot materialLot, String judgeGrade, String judgeCode, List<StockOutCheck> ngCheckList) {
        materialLot.setReserved9(judgeGrade);
        materialLot.setReserved10(judgeCode);
        materialLotRepository.save(materialLot);
        MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_JUDGE);
        materialLotHistoryRepository.save(history);

        if (CollectionUtils.isNotEmpty(ngCheckList)) {
            // 保存每个项目的判定结果 GC要求只记录NG的判定历史即可
            MaterialLot finalMaterialLot = materialLot;
            ngCheckList.forEach(stockOutCheck -> {
                MaterialLotJudgeHis materialLotJudgeHis = new MaterialLotJudgeHis();
                materialLotJudgeHis.setMaterialLotRrn(finalMaterialLot.getObjectRrn());
                materialLotJudgeHis.setMaterialLotId(finalMaterialLot.getMaterialLotId());
                materialLotJudgeHis.setItemName(stockOutCheck.getName());
                materialLotJudgeHis.setResult(stockOutCheck.getResult());
                materialLotJudgeHis.setTransType(TRANS_TYPE_JUDGE);
                materialLotJudgeHis.setHisSeq(ThreadLocalContext.getTransRrn());
                materialLotJudgeHisRepository.save(materialLotJudgeHis);
            });
        }
    }

    /**
     * 装箱判定
     * @throws ClientException
     */
    public void judgePackedMaterialLot(List<MaterialLot> materialLots,List<StockOutCheck> checkList) throws ClientException{
        try {
            String judgeGrade = StockOutCheck.RESULT_PASS;
            String judgeCode = StockOutCheck.RESULT_PASS;
            List<StockOutCheck> ngCheckList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(checkList)) {
                ngCheckList = checkList.stream().filter(checkItem -> StockOutCheck.RESULT_NG.equals(checkItem.getResult())).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(ngCheckList)) {
                judgeGrade = StockOutCheck.RESULT_NG;
                judgeCode = StockOutCheck.RESULT_NG;
            }

            //WLT对未装箱的LOT也要做LOT装箱检验，ParentMaterialLotId可能为空(只能出现整箱或者单个LOT)
            Map<String, List<MaterialLot>> packedLotMap = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            if(packedLotMap != null && packedLotMap.keySet().size() > 0){
                for (String packageMLotId : packedLotMap.keySet())  {
                    MaterialLot parentMLot = mmsService.getMLotByMLotId(packageMLotId, true);
                    judgeMaterialLot(parentMLot, judgeGrade, judgeCode, ngCheckList);

                    for (MaterialLot packagedMLot : packedLotMap.get(packageMLotId)) {
                        judgeMaterialLot(packagedMLot, judgeGrade, judgeCode, ngCheckList);
                    }
                }
            } else {
                for (MaterialLot materialLot : materialLots) {
                    judgeMaterialLot(materialLot, judgeGrade, judgeCode, ngCheckList);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }

    }

    /**
     * 获取能和箱信息匹配的订单信息
     * @return
     * @throws ClientException
     */
    public List<DocumentLine> validationAndGetDocumentLineList(List<DocumentLine> documentLines, MaterialLot materialLot) throws ClientException {
        List<DocumentLine> documentLineList = new ArrayList<>();
        for (DocumentLine documentLine : documentLines) {
            String materialSecondCode = materialLot.getReserved1() + materialLot.getGrade();
            if(documentLine.getMaterialName().equals(materialLot.getMaterialName()) && documentLine.getReserved2().equals(materialSecondCode) &&
                    documentLine.getReserved3().equals(materialLot.getGrade()) && documentLine.getReserved7().equals(materialLot.getReserved6()) ){
                documentLineList.add(documentLine);
            }
        }
        return  documentLineList;
    }

    /**
     * 格科同步MES的产品号、描述、单位
     */
    public void asyncMesProduct() throws ClientException{
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            Product product = new Product();
            List<Map> materialList = findEntityMapListByQueryName(Material.QUERY_PRODUCTINFO,null,0,999,"","");
            if(CollectionUtils.isNotEmpty(materialList)){
                for (Map<String, String> m :materialList)  {
                    String productId = m.get("INSTANCE_ID");
                    String productDesc = m.get("INSTANCE_DESC");
                    String storeUom = m.get("STORE_UOM");
                    Integer packageTotalQty = 0;
                    if(m.get("BOX_STANDARD_QTY") != null && m.get("PACKAGE_STANDARD_QTY") != null){
                        Integer boxStandardQty = Integer.parseInt(String.valueOf((Object)m.get("BOX_STANDARD_QTY")));
                        Integer packageStandardQty = Integer.parseInt(String.valueOf((Object)m.get("PACKAGE_STANDARD_QTY")));
                        packageTotalQty = boxStandardQty * packageStandardQty;
                    }

                    product = mmsService.getProductByName(productId);
                    if(product == null){
                        product = new Product();
                        product.setName(productId);
                        product.setDescription(productDesc);
                        product.setStoreUom(storeUom);
                        product.setMaterialCategory(Material.TYPE_PRODUCT);
                        product.setMaterialType(Material.TYPE_PRODUCT);
                        product.setReserved1(packageTotalQty.toString());
                        product = mmsService.saveProduct(product);

                        List<MaterialStatusModel> statusModels = materialStatusModelRepository.findByNameAndOrgRrn(Material.DEFAULT_STATUS_MODEL, sc.getOrgRrn());
                        if (CollectionUtils.isNotEmpty(statusModels)) {
                            product.setStatusModelRrn(statusModels.get(0).getObjectRrn());
                        } else {
                            throw new ClientException(StatusMachineExceptions.STATUS_MODEL_IS_NOT_EXIST);
                        }
                        productRepository.save(product);
                    } else {
                        product.setMaterialCategory(Material.TYPE_PRODUCT);
                        product.setMaterialType(Material.TYPE_PRODUCT);
                        product.setReserved1(packageTotalQty.toString());
                        productRepository.saveAndFlush(product);
                    }
                }
            }
            asyncMaterialName();
            asyncMesWarehouseProductType();
            asyncMesProductPrintModelId();
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科同步MES的产品入库型号
     */
    public void asyncMesWarehouseProductType() {
        try {
            Product product = new Product();
            List<Map> warehouseModelList = findEntityMapListByQueryName(Material.QUERY_WAREHOUSE_PRODUCT_MODEL,null,0,999,"","");
            if(CollectionUtils.isNotEmpty(warehouseModelList)){
                for (Map<String, String> m :warehouseModelList)  {
                    String productId = m.get("IN_STORAGE_MODEL_ID");
                    product = mmsService.getProductByName(productId);
                    if(product == null){
                        saveProductAndSetStatusModelRrn(productId);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科同步MES金线、IRA型号、TAPE、BLADE
     */
    public void asyncMesMaterialModel() throws ClientException{
        try {
            RawMaterial material = new RawMaterial();
            List<Map> materialModelList = findEntityMapListByQueryName(Material.QUERY_MATERIAL_MODEL,null,0,999,"","");
            if(CollectionUtils.isNotEmpty(materialModelList)){
                for(Map<String, String>  map : materialModelList){
                    String materialId = map.get("INSTANCE_ID");
                    String materialType = map.get("OBJECT");
                    String materialDesc = map.get("INSTANCE_DESC");
                    String storeuUom = map.get("STORE_UOM");

                    material = mmsService.getRawMaterialByName(materialId);
                    if(material == null){
                        material = new RawMaterial();
                        material.setName(materialId);
                        material.setDescription(materialDesc);
                        material.setStoreUom(storeuUom);
                        material.setMaterialCategory(Material.TYPE_MATERIAL);
                        if(Material.MATERIAL_TYPE_IR.equals(materialType)){
                            material.setMaterialType(Material.MATERIAL_TYPE_IRA);
                        } else if(Material.MATERIAL_TYPE_WIRE.equals(materialType)){
                            material.setMaterialType(Material.MATERIAL_TYPE_GOLD);
                        } else if(Material.MATERIAL_TYPE_TAPE.equals(materialType)) {
                            material.setMaterialType(Material.MATERIAL_TYPE_TAPE);
                        } else if(Material.MATERIAL_TYPE_BLADE.equals(materialType)) {
                            material.setMaterialType(Material.MATERIAL_TYPE_BLADE);
                        }

                        mmsService.createRawMaterial(material);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步MES胶水型号
     */
    public void asyncMesGlueType() throws ClientException{
        try {
            RawMaterial material = new RawMaterial();
            List<Map> glueTypeList = findEntityMapListByQueryName(Material.QUERY_GLUE_TYPE,null,0,999,"","");
            if(CollectionUtils.isNotEmpty(glueTypeList)){
                for(Map<String, String>  map : glueTypeList) {
                    String glueTypeName = map.get("NAME");
                    String glueTypeDesc = map.get("DESCRIPTION");

                    material = mmsService.getRawMaterialByName(glueTypeName);
                    if(material == null){
                        material = new RawMaterial();
                        material.setName(glueTypeName);
                        material.setDescription(glueTypeDesc);
                        material.setMaterialCategory(Material.TYPE_MATERIAL);
                        material.setMaterialType(Material.MATERIAL_TYPE_GLUE);

                        mmsService.createRawMaterial(material);
                    }

                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科同步MES的产品打印型号
     */
    public void asyncMesProductPrintModelId() {
        try {
            Product product = new Product();
            List<Map> productPrintModelList = findEntityMapListByQueryName(Material.QUERY_PRODUCT_PRINT_MODELID,null,0,999,"","");
            if(CollectionUtils.isNotEmpty(productPrintModelList)){
                for (Map<String, String> m :productPrintModelList)  {
                    String prodcutPrintModelId = m.get("PRINT_PRODUCT_MODEL");
                    product = mmsService.getProductByName(prodcutPrintModelId);
                    if(product == null) {
                        saveProductAndSetStatusModelRrn(prodcutPrintModelId);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 创建产品号
     * @param name
     * @throws ClientException
     */
    public Material saveProductAndSetStatusModelRrn(String name) throws ClientException{
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            Product product = new Product();
            product.setName(name);
            product.setMaterialCategory(Material.TYPE_PRODUCT);
            product.setMaterialType(Material.TYPE_PRODUCT);
            product = mmsService.saveProduct(product);

            List<MaterialStatusModel> statusModels = materialStatusModelRepository.findByNameAndOrgRrn(Material.DEFAULT_STATUS_MODEL, sc.getOrgRrn());
            if (CollectionUtils.isNotEmpty(statusModels)) {
                product.setStatusModelRrn(statusModels.get(0).getObjectRrn());
            } else {
                throw new ClientException(StatusMachineExceptions.STATUS_MODEL_IS_NOT_EXIST);
            }
            Material material = productRepository.save(product);
            return material;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步产品号或者晶圆型号至GC_PRODUCT_NAME表中
     * @throws ClientException
     */
    public void asyncMaterialName() throws ClientException{
        try {
            List<String> materialNameList = materialRepository.findNameByMaterialCategory(Lists.newArrayList(Material.TYPE_PRODUCT, Material.MATERIAL_TYPE));
            for(String materialName : materialNameList){
                if(!StringUtils.isNullOrEmpty(materialName)) {
                    List<MaterialNameInfo> materialNameInfoList = materialNameInfoRepository.findByNameAndOrgRrn(materialName, ThreadLocalContext.getOrgRrn());
                    if (CollectionUtils.isEmpty(materialNameInfoList)) {
                        MaterialNameInfo materialNameInfo = new MaterialNameInfo();
                        materialNameInfo.setName(materialName);
                        materialNameInfoRepository.saveAndFlush(materialNameInfo);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科同步MES的晶圆型号、描述、单位
     */
    public void asyncMesWaferType() throws ClientException{
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            RawMaterial rawMaterial = new RawMaterial();
            List<Map> materialList = findEntityMapListByQueryName(Material.QUERY_WAFERTYPEINFO,null,0,999,"","");
            if(CollectionUtils.isNotEmpty(materialList)){
                for (Map<String, String> m :materialList)  {
                    String productId = m.get("INSTANCE_ID");
                    String productDesc = m.get("INSTANCE_DESC");
                    String storeUom = m.get("STORE_UOM");

                    rawMaterial = mmsService.getRawMaterialByName(productId);
                    if(rawMaterial == null){
                        rawMaterial = new RawMaterial();
                        rawMaterial.setName(productId);
                        rawMaterial.setDescription(productDesc);
                        rawMaterial.setStoreUom(storeUom);
                        mmsService.createRawMaterial(rawMaterial);
                    } else {
                        rawMaterial.setMaterialCategory(Material.TYPE_WAFER);
                        rawMaterial.setMaterialType(Material.TYPE_WAFER);
                        rawMaterialRepository.saveAndFlush(rawMaterial);
                    }
                }
            }
            asyncMesMaterialModel();
            asyncMesGlueType();
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科同步MES的晶圆型号、描述、单位
     */
    public void asyncMesProductAndSubcode() throws ClientException{
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            Material material = new Material();
            List<GCProductSubcode> productSubcodes = Lists.newArrayList();
            List<Map> productSubcodeList = findEntityMapListByQueryName(Material.QUERY_PRODUCT_SUBCODE,null,0,999,"","");
            if(CollectionUtils.isNotEmpty(productSubcodeList)){
                for(Map<String, String> m : productSubcodeList){
                    String productId = m.get("MODEL_ID");
                    String subcode = m.get("SUB_CODE");
                    String modelClass = m.get("MODEL_CLASS");
                    if(modelClass.equals(Material.CLASS_PRODUCT)){
                        material = mmsService.getProductByName(productId);
                    } else {
                        material = mmsService.getRawMaterialByName(productId);
                    }
                    if(material != null){
                        GCProductSubcode productSubcode = gcProductSubcodeSetRepository.findByProductIdAndSubcode(productId, subcode);
                        if(productSubcode == null){
                            productSubcode = new GCProductSubcode();
                            productSubcode.setProductId(productId);
                            productSubcode.setSubcode(subcode);
                            productSubcode = gcProductSubcodeSetRepository.saveAndFlush(productSubcode);
                        }
                        productSubcodes.add(productSubcode);
                    }
                }

                //删除MES中不存在的产品二级代码信息
                List<GCProductSubcode> gcProductSubcodes = gcProductSubcodeSetRepository.findAll();
                gcProductSubcodes.removeAll(productSubcodes);
                if(CollectionUtils.isNotEmpty(gcProductSubcodes)){
                    for(GCProductSubcode productSubcode : gcProductSubcodes){
                        gcProductSubcodeSetRepository.deleteById(productSubcode.getObjectRrn());
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科同步MES的产品型号转换信息
     */
    public void asyncMesProductModelConversion() throws ClientException{
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            Material material = new Material();
            List<Map> productModelList = findEntityMapListByQueryName(Material.QUERY_PRODUCT_MODEL_CONVERSION,null,0,999,"","");
            List<GCProductModelConversion> productModelConversionList = Lists.newArrayList();
            if(CollectionUtils.isNotEmpty(productModelList)){
                for(Map<String, String> m : productModelList){
                    String productId = m.get("MODEL_ID");
                    String conversionModelId = m.get("CONVERSION_MODEL_ID");
                    String modelCategory = m.get("MODEL_CATEGORY");
                    material = mmsService.getProductByName(productId);
                    if(material == null){
                        material = mmsService.getRawMaterialByName(productId);
                    }
                    if(material != null){
                        GCProductModelConversion productModelConversion = gcProductModelConversionRepository.findByProductId(productId);
                        if(productModelConversion == null){
                            productModelConversion = new GCProductModelConversion();
                            productModelConversion.setProductId(productId);
                            productModelConversion.setConversionModelId(conversionModelId);
                            productModelConversion.setModelCategory(modelCategory);
                            productModelConversion = gcProductModelConversionRepository.saveAndFlush(productModelConversion);
                            productModelConversionList.add(productModelConversion);
                        } else {
                            GCProductModelConversion oldProductModelConversion = gcProductModelConversionRepository.findByProductIdAndConversionModelId(productId, conversionModelId);
                            if(oldProductModelConversion == null){
                                oldProductModelConversion = new GCProductModelConversion();
                                oldProductModelConversion.setProductId(productId);
                                oldProductModelConversion.setConversionModelId(conversionModelId);
                                oldProductModelConversion.setModelCategory(modelCategory);
                                oldProductModelConversion = gcProductModelConversionRepository.saveAndFlush(oldProductModelConversion);
                            }
                            productModelConversionList.add(oldProductModelConversion);
                        }
                    }
                }
            }
            //MES中删除的产品信息，WMS中也需要删除
            List<GCProductModelConversion> allProductModelConversion = gcProductModelConversionRepository.findAll();
            Map<String, GCProductModelConversion> productModelConversionMap = productModelConversionList.stream().collect(Collectors.toMap(GCProductModelConversion :: getProductId, Function.identity()));
            for(GCProductModelConversion productModelConversion : allProductModelConversion){
                if(!productModelConversionMap.containsKey(productModelConversion.getProductId())){
                    gcProductModelConversionRepository.deleteById(productModelConversion.getObjectRrn());
                }
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 定时同步mms_material_lot中的产品、等级、二级代码信息
     * @throws ClientException
     */
    public void asyncProductGradeAndSubcode() throws ClientException {
        try {
            List<Product> productList = productRepository.findByMaterialType(Material.TYPE_PRODUCT);
            GCProductRelation productRelation = new GCProductRelation();
            for (Product product : productList){
                List<String> gradeList = materialLotRepository.getGradeByMaterialNameAndStatusCategory(product.getName(), MaterialLot.STATUS_FIN);
                for (String grade : gradeList){
                    productRelation = productRelationRepository.findByProductIdAndGradeSubcodeAndType(product.getName(), grade, GCProductRelation.GRADE_TYPE);
                    if(productRelation == null){
                        productRelation = new GCProductRelation();
                        productRelation.setProductId(product.getName());
                        productRelation.setGradeSubcode(grade);
                        productRelation.setType(GCProductRelation.GRADE_TYPE);
                        productRelationRepository.save(productRelation);
                    }
                }
                List<String> subCodeList = materialLotRepository.getSubcodeByMaterialNameAndStatusCategory(product.getName(), MaterialLot.STATUS_FIN);
                for (String subcode : subCodeList){
                    productRelation = productRelationRepository.findByProductIdAndGradeSubcodeAndType(product.getName(), subcode, GCProductRelation.SUBCODE_TYPE);
                    if(productRelation == null){
                        productRelation = new GCProductRelation();
                        productRelation.setProductId(product.getName());
                        productRelation.setGradeSubcode(subcode);
                        productRelation.setType(GCProductRelation.SUBCODE_TYPE);
                        productRelationRepository.save(productRelation);
                    }
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 定时同步PO表中的供应商名称
     * @throws ClientException
     */
    public void asyncPoSupplier() throws ClientException{
        try {
            List<String> supplierNameList = outSourcePoRepository.getSupplierName();
            for(String sulipperName : supplierNameList){
                Supplier supplier = supplierRepository.getByNameAndType(sulipperName, Supplier.TYPE_PO_SUPPLER);
                if(supplier == null){
                    supplier = new Supplier();
                    supplier.setName(sulipperName);
                    supplier.setType(Supplier.TYPE_PO_SUPPLER);
                    supplierRepository.save(supplier);
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    @Override
    public List<Map> findEntityMapListByQueryName(String queryName, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause) throws ClientException {
        try {
            NBQuery nbQuery = queryRepository.findByName(queryName);
            if (nbQuery == null) {
                throw new ClientParameterException(NewbiestException.COMMON_QUERY_IS_NOT_EXIST, queryName);
            }
            return findEntityMapListByQueryText(nbQuery.getQueryText(), paramMap, firstResult, maxResult, whereClause, orderByClause);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public List<Map> findEntityMapListByQueryText(String queryText, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause) throws ClientException {
        try {
            StringBuffer sqlBuffer = new StringBuffer();
            sqlBuffer.append("SELECT * FROM (");
            sqlBuffer.append(queryText);
            sqlBuffer.append(")");
            if (!StringUtils.isNullOrEmpty(whereClause)) {
                sqlBuffer.append(" WHERE ");
                sqlBuffer.append(whereClause);
            }
            if (!StringUtils.isNullOrEmpty(orderByClause)) {
                sqlBuffer.append(" ORDER BY ");
                sqlBuffer.append(orderByClause);
            }

            Query query = em.createNativeQuery(sqlBuffer.toString());
            if (firstResult > 0) {
                query.setFirstResult(firstResult);
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
     * 获取需要称重的箱信息
     * @return
     * @throws ClientException
     */
    public MaterialLot getWaitWeightMaterialLot(String materialLotId, Long tableRrn) throws ClientException {
        try {
            MaterialLot materialLot = getWltMaterialLotToStockOut(tableRrn, materialLotId);
            if(materialLot.getObjectRrn() == null){
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, materialLotId);
            } else if(MaterialLot.PRODUCT_CATEGORY.equals(materialLot.getReserved7()) && !StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                //获取物料批次的理论重量
                materialLot = queryMaterialLotTheoryWeightAndFolatValue(materialLot);
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据物料批次号和tableRrn获取物料信息
     * @param materialLotId
     * @param tableRrn
     * @return
     * @throws ClientException
     */
    private List<MaterialLot> queryMaterialLotByTableRrnAndMaterialLotId(Long tableRrn, String materialLotId) throws ClientException{
        try {
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();
            StringBuffer clauseBuffer = new StringBuffer();
            clauseBuffer.append(" materialLotId = ");
            clauseBuffer.append("'" + materialLotId + "'");
            if (!StringUtils.isNullOrEmpty(whereClause)) {
                clauseBuffer.append(" AND ");
                clauseBuffer.append(whereClause);
            }
            whereClause = clauseBuffer.toString();
            List<MaterialLot> materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause, orderBy);
            return materialLots;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取箱号的理论重量及浮点值
     * @param materialLot
     * @return
     * @throws ClientException
     */
    private MaterialLot queryMaterialLotTheoryWeightAndFolatValue(MaterialLot materialLot) throws ClientException{
        try {
            GCProductWeightRelation productWeightRelation = new GCProductWeightRelation();
            BigDecimal totalWeight = BigDecimal.ZERO;
            String materialName = materialLot.getMaterialName();
            List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
            if(CollectionUtils.isNotEmpty(packageDetailLots)){
                List<GCProductWeightRelation> productWeightRelations = productWeightRelationRepository.findByProductId(materialName);
                if(CollectionUtils.isNotEmpty(productWeightRelations)){
                    BigDecimal minPackageQty = productWeightRelations.get(0).getMinPackedQty();
                    BigDecimal maxPackageQty = productWeightRelations.get(0).getMaxPackedQty();
                    //获取箱中真空包的总重量
                    BigDecimal vboxTotalWeight = getPackedDetialTotalWeight(minPackageQty, maxPackageQty, packageDetailLots);
                    if(vboxTotalWeight.compareTo(BigDecimal.ZERO) > 0){
                        //获取总重量的配置信息
                        productWeightRelation = validateAndGetProductRelationByPackedLotDetial(packageDetailLots, minPackageQty, maxPackageQty);
                        if(productWeightRelation != null && productWeightRelation.getPackageQty().compareTo(BigDecimal.ZERO) > 0){
                            totalWeight = totalWeight.add(vboxTotalWeight);
                            BigDecimal packageChipWeight = productWeightRelation.getPackageChipWeight();//整包芯片数量
                            BigDecimal packageNumber = productWeightRelation.getPackageQty();//整包颗数
                            BigDecimal floatValue = productWeightRelation.getFloatQty();//整包颗数
                            totalWeight = productWeightRelation.getBoxWeight().add(totalWeight).add(packageChipWeight.divide(packageNumber, 6, BigDecimal.ROUND_HALF_UP).multiply(materialLot.getCurrentQty()));
                            materialLot.setTheoryWeight(totalWeight.setScale(4, BigDecimal.ROUND_HALF_UP));
                            materialLot.setFloatValue(floatValue);
                        }
                    }
                }
            }
            return  materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据真空包数量确定箱子总重量的配置规则
     * @param packageDetailLots
     * @return
     * @throws ClientException
     */
    private GCProductWeightRelation validateAndGetProductRelationByPackedLotDetial(List<MaterialLot> packageDetailLots, BigDecimal minPackageQty, BigDecimal maxPackageQty) throws  ClientException{
        try {
            String materialName = packageDetailLots.get(0).getMaterialName();
            GCProductWeightRelation productWeightRelation = productWeightRelationRepository.findByProductIdAndPackageQty(materialName, minPackageQty);
            for(MaterialLot materialLot: packageDetailLots){
                if(materialLot.getCurrentQty().compareTo(minPackageQty) > 0){
                    productWeightRelation = productWeightRelationRepository.findByProductIdAndPackageQty(materialName, maxPackageQty);
                    break;
                }
            }
            return productWeightRelation;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取箱中真空包的总重量
     * @param minPackageQty
     * @param packageDetailLots
     * @return
     * @throws ClientException
     */
    private BigDecimal getPackedDetialTotalWeight(BigDecimal minPackageQty, BigDecimal maxPackageQty, List<MaterialLot> packageDetailLots) throws ClientException{
        try {
            boolean flag = false;
            //每包真空包重量=盘重量*（每包实际颗数/每盘芯片数+1）+盖重量*（每包实际颗数/每盘芯片数/盘数+1）+管夹重量*（每包实际颗数/每盘芯片数/盘数）
            BigDecimal totalWeight = BigDecimal.ZERO;
            GCProductWeightRelation relation = new GCProductWeightRelation();
            for(MaterialLot materialLot : packageDetailLots){
                BigDecimal currentQty = materialLot.getCurrentQty();
                if(currentQty.compareTo(minPackageQty) <= 0 ){
                    relation = productWeightRelationRepository.findByProductIdAndPackageQty(materialLot.getMaterialName(), minPackageQty);
                } else {
                    relation = productWeightRelationRepository.findByProductIdAndPackageQty(materialLot.getMaterialName(), maxPackageQty);
                }
                if(relation == null){
                    flag = true;
                    break;
                } else if(BigDecimal.ZERO.compareTo(relation.getDiscChipQty()) == 0 || BigDecimal.ZERO.compareTo(relation.getDiscQty()) == 0){
                    flag = true;
                    break;
                }
                BigDecimal discWeight = relation.getDiscWeight();//盘重量
                BigDecimal coverWeight = relation.getCoverWeight();//盖重量
                BigDecimal clipWeight = relation.getClipWeight();//管夹重量
                BigDecimal discQty = relation.getDiscQty();//盘数
                BigDecimal chipWeight = currentQty.divide(relation.getDiscChipQty(), 6, BigDecimal.ROUND_HALF_UP);//每包平均芯片重量
                BigDecimal vboxWeight = discWeight.multiply(chipWeight.add(BigDecimal.ONE)).add(coverWeight.multiply(chipWeight.divide(discQty, 6, BigDecimal.ROUND_HALF_UP).add(BigDecimal.ONE))).add(clipWeight.multiply(chipWeight.divide(discQty, 6, BigDecimal.ROUND_HALF_UP)));
                totalWeight = totalWeight.add(vboxWeight);
            }
            if(flag){
                totalWeight = BigDecimal.ZERO;
            }
            return totalWeight;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次称重
     * @param weightModels
     * @throws ClientException
     */
    public void materialLotWeight(List<WeightModel> weightModels) throws ClientException {
        try {
            List<MaterialLot> materialLotList = Lists.newArrayList();
            //将单箱称重与多箱称重区分开
            List<WeightModel> boxsWeightList = weightModels.stream().filter(weightModel -> !StringUtils.isNullOrEmpty(weightModel.getBoxsWeightFlag())).collect(Collectors.toList());
            List<WeightModel> boxWeightList = weightModels.stream().filter(weightModel -> StringUtils.isNullOrEmpty(weightModel.getBoxsWeightFlag())).collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(boxsWeightList)){
                //多箱称重需要按照多箱扫描序号赋值多箱称重事务号
                boxsWeightList = boxsWeightList.stream().sorted(Comparator.comparing(WeightModel::getBoxsScanSeq)).collect(Collectors.toList());
                Map<Long, List<WeightModel>> weightModelMap =  boxsWeightList.stream().collect(Collectors.groupingBy(WeightModel :: getBoxsScanSeq));
                for(Long boxsScanSeq : weightModelMap.keySet()){
                    String transId = generatorMLotsTransId(MaterialLot.GENERATOR_MATERIAL_LOT_WEIGHT_RULE);
                    List<WeightModel>  boxsWeightModelList = weightModelMap.get(boxsScanSeq);
                    for(WeightModel weightModel : boxsWeightModelList){
                        MaterialLot materialLot = mmsService.getMLotByMLotId(weightModel.getMaterialLotId());
                        materialLot.setReserved19(weightModel.getWeight());
                        materialLot.setReserved20(transId);
                        materialLot.setScanSeq(weightModel.getScanSeq());
                        materialLotList.add(materialLot);
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(boxWeightList)){
                for(WeightModel weightModel : boxWeightList){
                    MaterialLot materialLot = mmsService.getMLotByMLotId(weightModel.getMaterialLotId());
                    materialLot.setScanSeq(weightModel.getScanSeq());
                    materialLot.setReserved19(weightModel.getWeight());
                    materialLotList.add(materialLot);
                }
            }

            materialLotList = materialLotList.stream().sorted(Comparator.comparing(MaterialLot::getScanSeq)).collect(Collectors.toList());

            //称重记录
            for (MaterialLot materialLot : materialLotList) {
                String weightSeq = generatorMLotsTransId(MaterialLot.GENERATOR_QRCODE_LABEL_WEIGHT_SEQ_RULE);
                materialLot.setWeightSeq(weightSeq);
                materialLotRepository.save(materialLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_WEIGHT);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据ID生成规则设置的生成多箱称重事务号/来料导入编号
     * @return
     * @throws ClientException
     */
    public String generatorMLotsTransId(String ruleId) throws ClientException{
        try {
            GeneratorContext generatorContext = new GeneratorContext();
            generatorContext.setRuleName(ruleId);
            String id = generatorService.generatorId(ThreadLocalContext.getOrgRrn(), generatorContext);
            return id;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取到可以入库的批次
     * @param relayBoxId
     * @return
     */
    public List<MaterialLot> getWaitChangeStorageMaterialLotByRelayBoxId(String relayBoxId) throws ClientException {
        try {
            StringBuffer whereClause = new StringBuffer();
            whereClause.append(" reserved8 = '" + relayBoxId + "'");
            whereClause.append(" and reserved14 is not null ");
            whereClause.append(" and statusCategory <> 'Fin' ");

            List<MaterialLot> materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause.toString(), "");
            return materialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void transferStorage(List<RelayBoxStockInModel> relayBoxStockInModels) throws ClientException {
        try {
            ThreadLocalContext.getSessionContext().buildTransInfo();
            Map<String, RelayBoxStockInModel> relayBoxStockInModelMap = relayBoxStockInModels.stream().collect(Collectors.toMap(RelayBoxStockInModel :: getMaterialLotId, Function.identity()));

            //1. 把箱批次和普通的物料批次区分出来
            List<MaterialLot> materialLots = relayBoxStockInModels.stream().map(model -> mmsService.getMLotByMLotId(model.getMaterialLotId(), true)).collect(Collectors.toList());

            //3. 入库
            for (MaterialLot materialLot : materialLots) {
                RelayBoxStockInModel relayBoxStockInModel = relayBoxStockInModelMap.get(materialLot.getMaterialLotId());
                String storageId = relayBoxStockInModel.getStorageId();

                if (StringUtils.isNullOrEmpty(materialLot.getReserved13())) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_WAREHOUSE_IS_NULL, materialLot.getMaterialLotId());
                }
                MaterialLotAction action = new MaterialLotAction();
                action.setTargetWarehouseRrn(Long.parseLong(materialLot.getReserved13()));
                action.setTargetStorageId(storageId);
                action.setTransQty(materialLot.getCurrentQty());
                action.setTransCount(materialLot.getCurrentSubQty());

                List<MaterialLotInventory> materialLotInvList = mmsService.getMaterialLotInv(materialLot.getObjectRrn());
                // 如果为空就是做入库事件 如果不是空则做转库事件
                if (CollectionUtils.isNotEmpty(materialLotInvList)) {
                    MaterialLotInventory materialLotInventory = materialLotInvList.get(0);
                    action.setFromWarehouseRrn(materialLotInventory.getWarehouseRrn());
                    action.setFromStorageRrn(materialLotInventory.getStorageRrn());
                    mmsService.transfer(materialLot, action);
                } else {
                    materialLot = mmsService.stockIn(materialLot, action);
                }
                materialLot.setReserved14(storageId);
                materialLotRepository.save(materialLot);
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证出货的物料信息是否全部备货、
     *      如果是包装批次，要验证内部所有的物料批次是否都备货到了相同的DocLine以及备货备注是否一致
     *      验证扫描的物料批次基础信息是否一致
     * @param waitValidationLot 待验证的物料批次
     * @param validatedMLotActions 以验证过的物料批次动作
     */
    public void validationStockOutMaterialLot(MaterialLot waitValidationLot, List<MaterialLotAction> validatedMLotActions)throws ClientException{
        try {
            waitValidationLot = mmsService.getMLotByMLotId(waitValidationLot.getMaterialLotId(), true);
            if (waitValidationLot.getReservedQty().compareTo(waitValidationLot.getCurrentQty()) != 0) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_RESERVED_ALL, waitValidationLot.getMaterialLotId());
            }
            List<MaterialLot> packageDetailLots = materialLotRepository.getPackageDetailLots(waitValidationLot.getObjectRrn());
            if(CollectionUtils.isNotEmpty(packageDetailLots)){
                Set reservedInfo = packageDetailLots.stream().map(mLot -> mLot.getReserved16() + StringUtils.SPLIT_CODE + mLot.getReserved18()).collect(Collectors.toSet());
                if (reservedInfo == null || reservedInfo.size() > 1) {
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_RESERVED_INFO_IS_NOT_SAME, packageDetailLots.get(0).getReserved16() + StringUtils.SPLIT_CODE + packageDetailLots.get(0).getReserved18());
                }
            }
            if (CollectionUtils.isNotEmpty(validatedMLotActions)) {
                MaterialLot validatedMLot = mmsService.getMLotByMLotId(validatedMLotActions.get(0).getMaterialLotId(), true);
                try {
                    Assert.assertEquals(waitValidationLot.getMaterialName(), validatedMLot.getMaterialName());
                } catch (AssertionError e) {
                    throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "materialName", waitValidationLot.getMaterialName(), validatedMLot.getMaterialName());
                }

                try {
                    Assert.assertEquals(waitValidationLot.getReserved1(), validatedMLot.getReserved1());
                } catch (AssertionError e) {
                    throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "secondcode", waitValidationLot.getReserved1(),  validatedMLot.getReserved1());
                }

                try {
                    Assert.assertEquals(waitValidationLot.getGrade(), validatedMLot.getGrade());
                } catch (AssertionError e) {
                    throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "grade", waitValidationLot.getGrade(), validatedMLot.getGrade());
                }
                try {
                    Assert.assertEquals(waitValidationLot.getReserved6(), validatedMLot.getReserved6());
                } catch (AssertionError e) {
                    throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "other1", waitValidationLot.getReserved6(), validatedMLot.getReserved6());
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科要求扫描箱号的时候不满足条件的箱号信息显示表单的异常栏位信息，不做异常提示
     * 验证出货的物料信息是否全部备货、
     *      如果是包装批次，要验证内部所有的物料批次是否都备货到了相同的DocLine以及备货备注是否一致
     *      验证扫描的物料批次基础信息是否一致
     * @param waitValidationLot 待验证的物料批次
     * @param validatedMLotActions 以验证过的物料批次动作
     */
    public boolean validateStockOutMaterialLot(MaterialLot waitValidationLot, List<MaterialLotAction> validatedMLotActions)throws ClientException{
        try {
            boolean falg = true;
            waitValidationLot = mmsService.getMLotByMLotId(waitValidationLot.getMaterialLotId(), true);
            if (waitValidationLot.getReservedQty().compareTo(waitValidationLot.getCurrentQty()) != 0) {
                falg = false;
            }
            List<MaterialLot> packageDetailLots = materialLotRepository.getPackageDetailLots(waitValidationLot.getObjectRrn());
            if(CollectionUtils.isNotEmpty(packageDetailLots)){
                Set reservedInfo = packageDetailLots.stream().map(mLot -> mLot.getReserved16() + StringUtils.SPLIT_CODE + mLot.getReserved18()).collect(Collectors.toSet());
                if (reservedInfo == null || reservedInfo.size() > 1) {
                    falg = false;
                }
            }
            falg = validationMaterialLotInfo(waitValidationLot, validatedMLotActions, falg);

            return falg;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存来料信息
     * @param materialLotList
     * @param importType
     * @return
     */
    public String saveIncomingMaterialList(List<MaterialLot> materialLotList, String importType) throws ClientException {
        try {
            ThreadLocalContext.getSessionContext().buildTransInfo();
            String importCode = "";
            if(importType.equals(MaterialLotUnit.SAMSUING_PACKING_LIST)){
                importCode = generatorMLotsTransId(MaterialLot.GENERATOR_INCOMING_MLOT_IMPORT_CODE_RULE);
                for(MaterialLot materialLot: materialLotList){
                    materialLot.setReserved48(importCode);
                    List<MaterialLotUnit> materialLotUnitList = getMaterialLotUnitList(materialLot);
                    materialLotUnitList = materialLotUnitAssignEng(materialLotUnitList);
                    materialLotUnitService.createMLot(materialLotUnitList);
                }
            } else if (importType.equals(MaterialLotUnit.LCD_COG_FINISH_PRODUCT)){
                //来料导入模板LCD（COG成品-ECRETIVE）数据处理
                Map<String, List<MaterialLot>> materialLotMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot:: getParentMaterialLotId));
                importCode = generatorMLotsTransId(MaterialLot.GENERATOR_INCOMING_MLOT_IMPORT_CODE_RULE);
                for (String parentMaterialLotId : materialLotMap.keySet()) {
                    if (mmsService.getMLotByMLotId(parentMaterialLotId) != null) {
                        throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, parentMaterialLotId);
                    }
                    List<MaterialLot> materialLots = materialLotMap.get(parentMaterialLotId);
                    //合成一条箱信息
                    MaterialLot parentMaterialLot = new MaterialLot();
                    Material material = mmsService.getRawMaterialByName(materialLots.get(0).getMaterialName());
                    if (material == null) {
                        RawMaterial rawMaterial = new RawMaterial();
                        rawMaterial.setName(materialLots.get(0).getMaterialName());
                        material = mmsService.createRawMaterial(rawMaterial);
                    }
                    parentMaterialLot.setMaterial(material);
                    Long totalMaterialLotQty = materialLotMap.get(parentMaterialLotId).stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentQty().longValue()));
                    parentMaterialLot.setMaterialLotId(parentMaterialLotId);
                    parentMaterialLot.setLotId(parentMaterialLotId);
                    parentMaterialLot.setCurrentQty(BigDecimal.valueOf(totalMaterialLotQty));
                    parentMaterialLot.setMaterialLot(materialLots.get(0));
                    parentMaterialLot.initialMaterialLot();
                    parentMaterialLot.setStatusModelRrn(material.getStatusModelRrn());
                    parentMaterialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
                    parentMaterialLot.setStatus(MaterialStatus.STATUS_CREATE);
                    parentMaterialLot.setPackageType(MaterialLot.PACKAGE_TYPE);
                    parentMaterialLot.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_COG);
                    parentMaterialLot.setReserved48(importCode);
                    parentMaterialLot.setReserved49(MaterialLot.IMPORT_COG);
                    parentMaterialLot.setReserved50("17");
                    parentMaterialLot = materialLotRepository.saveAndFlush(parentMaterialLot);

                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(parentMaterialLot, NBHis.TRANS_TYPE_CREATE);
                    history.setTransQty(parentMaterialLot.getCurrentQty());
                    materialLotHistoryRepository.save(history);

                    //保存真空包信息
                    for(MaterialLot materialLot : materialLots){
                        if (mmsService.getMLotByMLotId(materialLot.getMaterialLotId()) != null) {
                            throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, materialLot.getMaterialLotId());
                        }
                        materialLot.setMaterial(material);
                        materialLot.initialMaterialLot();
                        materialLot.setStatusModelRrn(material.getStatusModelRrn());
                        materialLot.setParentMaterialLotRrn(parentMaterialLot.getObjectRrn());
                        materialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
                        materialLot.setStatus(MaterialStatus.STATUS_CREATE);
                        materialLot.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_COG);
                        materialLot.setReserved48(importCode);
                        materialLot.setReserved49(MaterialLot.IMPORT_COG);
                        materialLot.setReserved50("17");
                        materialLot = materialLotRepository.saveAndFlush(materialLot);

//                        //存入库存中
//                        PackagedLotDetail packagedLotDetail = new PackagedLotDetail();
//                        packagedLotDetail.setPackagedLotRrn(parentMaterialLot.getObjectRrn());
//                        packagedLotDetail.setPackagedLotId(parentMaterialLot.getMaterialLotId());
//                        packagedLotDetail.setMaterialLotRrn(materialLot.getObjectRrn());
//                        packagedLotDetail.setMaterialLotId(materialLot.getMaterialLotId());
//                        packagedLotDetail.setQty(materialLot.getCurrentQty());
//                        packagedLotDetailRepository.save(packagedLotDetail);

                        history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE);
                        history.setTransQty(materialLot.getCurrentQty());
                        materialLotHistoryRepository.save(history);
                    }
                }
            } else if(importType.equals(MaterialLotUnit.RMA_GOOD_PRODUCT) || importType.equals(MaterialLotUnit.RMA_RETURN)
                    ||importType.equals(MaterialLotUnit.RMA_PURE)){
                importCode = generatorMLotsTransId(MaterialLot.GENERATOR_INCOMING_MLOT_IMPORT_CODE_RULE);
                Map<String, List<MaterialLot>> materialLotMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot:: getMaterialName));
                for(String materialName : materialLotMap.keySet()){
                    List<MaterialLot> materialLots = materialLotMap.get(materialName);
                    Material material = mmsService.getProductByName(materialName);
                    if (material == null) {
                        Product product = new Product();
                        product.setName(materialName);
                        product.setMaterialCategory(Material.TYPE_PRODUCT);
                        product.setMaterialType(Material.TYPE_PRODUCT);
                        material = mmsService.saveProduct(product);
                        StatusModel statusModel = mmsService.getMaterialStatusModel(material);
                        material.setStatusModelRrn(statusModel.getObjectRrn());
                    }
                    //删除已经存在的物料批次信息，重新导入
                    deleteRmaMaterialLotAndUnit(materialLots);
                    for(MaterialLot materialLot : materialLots){
                        materialLot.setMaterial(material);
                        materialLot.setLotId(materialLot.getMaterialLotId());
                        materialLot.setReserved48(importCode);
                        materialLot.initialMaterialLot();
                        materialLot.setStatusModelRrn(material.getStatusModelRrn());
                        materialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
                        materialLot.setStatus(MaterialStatus.STATUS_CREATE);
                        materialLot.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_RMA);
                        if(StringUtils.isNullOrEmpty(materialLot.getReserved35())){
                            materialLot.setReserved35("0");
                        }
                        if(MaterialLotUnit.RMA_GOOD_PRODUCT.equals(importType)){
                            materialLot.setReserved50("11");
                            materialLot.setReserved49(MaterialLot.IMPORT_RMA);
                        } else if(MaterialLotUnit.RMA_RETURN.equals(importType)){
                            materialLot.setReserved50("12");
                            materialLot.setReserved49(MaterialLot.IMPORT_RETURN);
                        } else if(MaterialLotUnit.RMA_PURE.equals(importType)){
                            materialLot.setReserved50("15");
                            materialLot.setReserved49(MaterialLot.IMPORT_CRMA);
                        }
                        MaterialLot oldMLot = mmsService.getMLotByMLotId(materialLot.getMaterialLotId());
                        if(oldMLot != null){
                            materialLotRepository.delete(oldMLot);
                        }
                        materialLot = materialLotRepository.saveAndFlush(materialLot);
                        MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE);
                        materialLotHistoryRepository.save(history);

                        MaterialLotUnit materialLotUnit = new MaterialLotUnit();
                        materialLotUnit.setMaterialLotId(materialLot.getMaterialLotId());
                        materialLotUnit.setLotId(materialLot.getMaterialLotId());
                        materialLotUnit.setState(MaterialStatus.STATUS_CREATE);
                        materialLotUnit.setMaterial(material);
                        materialLotUnit.setMaterialLot(materialLot);
                        materialLotUnit.setRmaMaterialLot(materialLot);
                        materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                        MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_CREATE);
                        materialLotUnitHisRepository.save(materialLotUnitHistory);
                    }
                }
            }
            return importCode;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 删除RMA已经存在的物料批次及Unit信息，重新导入
     * @param materialLots
     * @throws ClientException
     */
    private void deleteRmaMaterialLotAndUnit(List<MaterialLot> materialLots) throws ClientException{
        try {
            for(MaterialLot materialLot : materialLots){
                MaterialLot oldMaterialLot = mmsService.getMLotByMLotId(materialLot.getMaterialLotId());
                if(oldMaterialLot != null){
                    materialLotRepository.deleteById(oldMaterialLot.getObjectRrn());
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(oldMaterialLot, NBHis.TRANS_TYPE_DELETE);
                    materialLotHistoryRepository.save(history);

                    List<MaterialLotInventory> materialLotInvList = mmsService.getMaterialLotInv(oldMaterialLot.getObjectRrn());
                    if(CollectionUtils.isNotEmpty(materialLotInvList)){
                        materialLotInventoryRepository.deleteByMaterialLotRrn(oldMaterialLot.getObjectRrn());
                    }
                }

                List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                    for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                        materialLotUnitRepository.deleteById(materialLotUnit.getObjectRrn());

                        MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_DELETE);
                        materialLotUnitHisRepository.save(materialLotUnitHistory);
                    }
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    private List<MaterialLotUnit> getMaterialLotUnitList(MaterialLot materialLot) {
        try {
            List<MaterialLotUnit> materialLotUnitList = new ArrayList<>();
            String waferId = materialLot.getReserved31();
            String [] unitIdArray = waferId.split(",");
            Arrays.sort(unitIdArray);
            String fabLotId = materialLot.getReserved30().split("\\.")[0];
            String lotId = fabLotId + "." + unitIdArray[0].split("\\.")[1];
            for(int i=0; i< unitIdArray.length; i++){
                MaterialLotUnit materialLotUnit = new MaterialLotUnit();
                materialLotUnit.setMaterialLot(materialLot);
                materialLotUnit.setLotId(lotId);
                materialLotUnit.setGrade(MaterialLotUnit.SAMSUING_GRADE);
                materialLotUnit.setReserved4(materialLot.getReserved6());
                materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_CP);
                materialLotUnit.setCurrentQty(materialLot.getCurrentQty());
                materialLotUnit.setCurrentSubQty(BigDecimal.ONE);
                materialLotUnit.setReserved13(materialLot.getReserved13());
                materialLotUnit.setReserved14(materialLot.getReserved14());
                materialLotUnit.setReserved47(materialLot.getReserved47());
                materialLotUnit.setReserved48(materialLot.getReserved48());
                materialLotUnit.setReserved49(MaterialLot.IMPORT_SENSOR_CP);
                String waferSource = valiateMaterialNameAndGetWaferSource(materialLot.getMaterialName());
                materialLotUnit.setReserved50(waferSource);
                materialLotUnit.setUnitId(unitIdArray[i]);
                materialLotUnitList.add(materialLotUnit);
            }
            return materialLotUnitList;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void deleteIncomingMaterialLot(List<MaterialLotUnit> materialLotUnitList, String deleteNote) throws ClientException{
        try {
            //卡控已经排料的晶圆不能删除
            List<MaterialLotUnit> bindWorkorderMLotUnits = materialLotUnitList.stream().filter(materialLotUnit -> !StringUtils.isNullOrEmpty(materialLotUnit.getWorkOrderId())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(bindWorkorderMLotUnits)){
                throw new ClientParameterException(GcExceptions.UNIT_ID_ALREADY_BONDING_WORKORDER_ID, bindWorkorderMLotUnits.get(0).getLotId());
            }

            //按照箱号分组
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialLotId));
            for(String materialLotId : materialLotUnitMap.keySet()){
                materialLotUnitRepository.deleteByMaterialLotId(materialLotId);
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId);
                materialLotRepository.delete(materialLot);

                // 记录历史
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_DELETE);
                history.setActionComment(deleteNote);
                materialLotHistoryRepository.save(history);
            }

            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_DELETE);
                history.setActionComment(deleteNote);
                materialLotUnitHisRepository.save(history);
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收WLT的完成品
     * @param packedLotList
     */
    public List<Map<String,String>> receiveWltFinishGood(List<MesPackedLot> packedLotList, String printLabel) throws ClientException {
        try {
            List<Map<String, String>> parameterMapList = Lists.newArrayList();
            List<MaterialLot> materialLotList = Lists.newArrayList();
            Map<String, List<MesPackedLot>> packedLotMap = packedLotList.stream().collect(Collectors.groupingBy(MesPackedLot :: getCstId));
            List<MesPackedLot> mesPackedLots = Lists.newArrayList();
            for(String cstId : packedLotMap.keySet()){
                List<MesPackedLot> mesPackedLotList = packedLotMap.get(cstId);
                Long totalQuantity = mesPackedLotList.stream().collect(Collectors.summingLong(mesPackedLot -> mesPackedLot.getQuantity().longValue()));
                Material material = mmsService.getProductByName(mesPackedLotList.get(0).getProductId());
                if (material == null) {
                    throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_IS_NOT_EXIST, mesPackedLotList.get(0).getProductId());
                }

                MesPackedLot mesPackedLot = getReceicvePackedLotByPackedWafer(mesPackedLotList.get(0), material, totalQuantity, mesPackedLotList.size());
                mesPackedLots.add(mesPackedLot);
            }
            receiveFinishGood(mesPackedLots);
            materialLotList = saveMaterialLotUnitAndSaveHis(packedLotMap, mesPackedLots, materialLotList);

            mesPackedLotRepository.updatePackedStatusByPackedLotRrnList(MesPackedLot.PACKED_STATUS_RECEIVED, packedLotList.stream().map(MesPackedLot :: getPackedLotRrn).collect(Collectors.toList()));
            if(!StringUtils.isNullOrEmpty(printLabel)){
                mesPackedLots = mesPackedLots.stream().sorted(Comparator.comparing(MesPackedLot::getScanSeq)).collect(Collectors.toList());
                for(MesPackedLot mesPackedLot : mesPackedLots){
                    MaterialLot materialLot = mmsService.getMLotByMLotId(mesPackedLot.getBoxId());
                    Map<String, String> parameterMap = getWltCpPrintParameter(materialLot);
                    parameterMapList.add(parameterMap);
                }
            }
            return parameterMapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存晶圆信息，修改MES入库晶圆的状态
     * @param packedLotMap
     * @param mesPackedLots
     * @param materialLotList
     * @throws ClientException
     */
    private List<MaterialLot> saveMaterialLotUnitAndSaveHis(Map<String, List<MesPackedLot>> packedLotMap, List<MesPackedLot> mesPackedLots, List<MaterialLot> materialLotList) throws ClientException{
        try {
            for(MesPackedLot mesPackedLot : mesPackedLots){
                String cstId = mesPackedLot.getCstId();
                List<MesPackedLot> mesPackedLotList = packedLotMap.get(cstId);
                MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(mesPackedLot.getBoxId(), ThreadLocalContext.getOrgRrn());
                Material material = mmsService.getProductByName(materialLot.getMaterialName());
                MesPackedLotRelation mesPackedLotRelation = mesPackedLotRelationRepository.findByPackedLotRrn(mesPackedLotList.get(0).getPackedLotRrn());
                for(MesPackedLot packedLot : mesPackedLotList){
                    MaterialLotUnit materialLotUnit = new MaterialLotUnit();
                    if(MaterialLotUnit.PRODUCT_CATEGORY_RW.equals(materialLot.getReserved7())){
                        materialLotUnit.setUnitId(packedLot.getBoxId());
                        materialLotUnit.setLotCst(packedLot.getLotId());
                    } else {
                        materialLotUnit.setUnitId(packedLot.getWaferId());
                    }
                    materialLotUnit.setMaterialLotId(materialLot.getMaterialLotId());
                    materialLotUnit.setMaterialLotRrn(materialLot.getObjectRrn());
                    materialLotUnit.setLotId(cstId);
                    materialLotUnit.setMaterial(material);
                    materialLotUnit.setState(MaterialLotUnit.STATE_IN);
                    materialLotUnit.setGrade(packedLot.getGrade());
                    materialLotUnit.setWorkOrderId(packedLot.getWorkorderId());
                    materialLotUnit.setCurrentQty(BigDecimal.valueOf(packedLot.getQuantity()));
                    materialLotUnit.setCurrentSubQty(BigDecimal.ONE);
                    materialLotUnit.setTreasuryNote(packedLot.getTreasuryNote());
                    materialLotUnit.setReceiveQty(BigDecimal.valueOf(packedLot.getQuantity()));
                    materialLotUnit.setReserved1(packedLot.getLevelTwoCode());
                    materialLotUnit.setReserved3(String.valueOf(mesPackedLotList.size()));
                    materialLotUnit.setReserved4(packedLot.getBondedProperty());
                    materialLotUnit.setReserved13(materialLot.getReserved13());
                    materialLotUnit.setReserved18("0");
                    materialLotUnit.setReserved38(packedLot.getWaferMark());
                    materialLotUnit.setReserved50(materialLot.getReserved50());
                    materialLotUnit.setReserved22(materialLot.getReserved22());
                    materialLotUnit.setReserved24(materialLot.getReserved24());
                    if(mesPackedLotRelation != null){
                        materialLotUnit.setReserved25(mesPackedLotRelation.getWaferProperty());
                    }
                    materialLotUnit =  materialLotUnitRepository.saveAndFlush(materialLotUnit);

                    MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_CREATE);
                    history.setTransQty(materialLotUnit.getReceiveQty());
                    materialLotUnitHisRepository.save(history);
                }
                materialLotList.add(materialLot);
            }

            return materialLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * RW产线接收入库
     * @param packedLots
     * @param printLabel
     * @throws ClientException
     */
    public List<Map<String, String>> receiveRWFinishPackedLot(List<MesPackedLot> packedLots, String printLabel) throws ClientException {
        try {
            List<Map<String, String>> parameterMapList = Lists.newArrayList();
            List<MaterialLot> materialLotList = Lists.newArrayList();
            Map<String, List<MesPackedLot>> packedLotMap = packedLots.stream().collect(Collectors.groupingBy(MesPackedLot :: getCstId));
            List<MesPackedLot> mesPackedLots = Lists.newArrayList();
            for(String cstId : packedLotMap.keySet()){
                List<MesPackedLot> packedLotList = packedLotMap.get(cstId);
                Long totalQuantity = packedLotList.stream().collect(Collectors.summingLong(mesPackedLot -> mesPackedLot.getQuantity().longValue()));
                String productId = packedLotList.get(0).getProductId();
                Material material = mmsService.getProductByName(productId);
                if (material == null) {
                    throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_IS_NOT_EXIST, productId);
                }

                MesPackedLot mesPackedLot = getRwReceicvePackedLotByBoxId(packedLotList.get(0), material, totalQuantity, packedLotList.size());
                mesPackedLots.add(mesPackedLot);
            }
            receiveRwFinishGood(mesPackedLots);
            saveMaterialLotUnitAndSaveHis(packedLotMap, mesPackedLots, materialLotList);

            mesPackedLotRepository.updatePackedStatusByPackedLotRrnList(MesPackedLot.PACKED_STATUS_RECEIVED, packedLots.stream().map(MesPackedLot :: getPackedLotRrn).collect(Collectors.toList()));
            if(!StringUtils.isNullOrEmpty(printLabel)){
                mesPackedLots = mesPackedLots.stream().sorted(Comparator.comparing(MesPackedLot::getScanSeq)).collect(Collectors.toList());
                for(MesPackedLot packedLot : mesPackedLots){
                    Map<String, String> parameterMap = getRwPackedLotPrintParameter(packedLot, packedLotMap);
                    parameterMapList.add(parameterMap);
                }
            }

            return parameterMapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW完成品接收入库
     * @param packedLotList
     * @throws ClientException
     */
    private void receiveRwFinishGood(List<MesPackedLot> packedLotList) throws ClientException{
        try {
            Map<String, List<MesPackedLot>> packedLotMap = packedLotList.stream().collect(Collectors.groupingBy(MesPackedLot :: getProductId));
            Map<String, Warehouse> warehouseMap = Maps.newHashMap();
            List<ErpMoa> erpMoaList = Lists.newArrayList();

            for (String productId : packedLotMap.keySet()) {
                Material material = mmsService.getProductByName(productId);
                if (material == null) {
                    throw new ClientParameterException(MM_PRODUCT_ID_IS_NOT_EXIST, productId);
                }

                List<MesPackedLot> mesPackedLots = packedLotMap.get(productId);
                List<MaterialLotAction> materialLotActions = Lists.newArrayList();
                for (MesPackedLot mesPackedLot : mesPackedLots) {
                    //从发料Lot的获取无聊编码信息
                    MaterialLot materialLot = materialLotRepository.findByLotIdAndWorkOrderIdAndStatus(mesPackedLot.getLotId(), mesPackedLot.getWorkorderId(), MaterialLotUnit.STATE_ISSUE);
                    if(materialLot == null){
                        throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, mesPackedLot.getLotId());
                    } else {
                        if(!StringUtils.isNullOrEmpty(materialLot.getReserved22())){
                            Supplier supplier = supplierRepository.getByNameAndType(materialLot.getReserved22(), Supplier.TYPE_VENDER);
                            if(supplier == null){
                                supplier = new Supplier();
                                supplier.setType(Supplier.TYPE_VENDER);
                                supplier.setName(materialLot.getReserved22());
                                supplierRepository.save(supplier);
                            }
                        }
                    }

                    MaterialLotAction materialLotAction = new MaterialLotAction();
                    materialLotAction.setMaterialLotId(mesPackedLot.getBoxId());
                    materialLotAction.setGrade(mesPackedLot.getGrade());
                    materialLotAction.setTransQty(BigDecimal.valueOf(mesPackedLot.getQuantity()));
                    materialLotAction.setSourceModelId(mesPackedLot.getProductId());

                    // 产地是空的话则是ZJ仓库
                    String warehouseName = WAREHOUSE_ZJ;
                    if (!StringUtils.isNullOrEmpty(mesPackedLot.getLocation()) && mesPackedLot.getLocation().equalsIgnoreCase("SH")) {
                        warehouseName = WAREHOUSE_SH;
                    }

                    Warehouse warehouse;
                    if (!warehouseMap.containsKey(warehouseName)) {
                        warehouse = mmsService.getWarehouseByName(warehouseName);
                        if (warehouse == null) {
                            warehouse = new Warehouse();
                            warehouse.setName(warehouseName);
                            warehouse = warehouseRepository.saveAndFlush(warehouse);
                        }
                        warehouseMap.put(warehouseName, warehouse);
                    }
                    warehouse = warehouseMap.get(warehouseName);

                    materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());

                    // 需要赋值的Map
                    Map<String, Object> otherReceiveProps = Maps.newHashMap();
                    otherReceiveProps.put("lotId", mesPackedLot.getCstId());
                    otherReceiveProps.put("lotCst", mesPackedLot.getLotId());
                    otherReceiveProps.put("pcode", mesPackedLot.getPcode());
                    otherReceiveProps.put("workOrderId", mesPackedLot.getWorkorderId());
                    otherReceiveProps.put("productType", mesPackedLot.getProductType());
                    otherReceiveProps.put("reserved1", mesPackedLot.getLevelTwoCode());
                    otherReceiveProps.put("reserved2", mesPackedLot.getWaferId());
                    otherReceiveProps.put("reserved3", mesPackedLot.getSalesNote());
                    otherReceiveProps.put("reserved4", mesPackedLot.getTreasuryNote());
                    otherReceiveProps.put("reserved5", mesPackedLot.getProductionNote());
                    otherReceiveProps.put("reserved6", mesPackedLot.getBondedProperty());
                    otherReceiveProps.put("reserved13", warehouse.getObjectRrn().toString());
                    otherReceiveProps.put("reserved21", mesPackedLot.getErpProductId());
                    otherReceiveProps.put("reserved22", mesPackedLot.getSubName());
                    otherReceiveProps.put("reserved24", mesPackedLot.getFabDevice());
                    otherReceiveProps.put("reserved49", mesPackedLot.getImportType());
                    otherReceiveProps.put("reserved7", mesPackedLot.getProductCategory());
                    otherReceiveProps.put("reserved50", MaterialLot.LCP_WAFER_SOURCE);

                    otherReceiveProps.put("reserved25", materialLot.getReserved25());

                    if(mesPackedLot.getWaferQty() != null){
                        BigDecimal waferQty = new BigDecimal(mesPackedLot.getWaferQty().toString());
                        materialLotAction.setTransCount(waferQty);
                    }
                    materialLotAction.setPropsMap(otherReceiveProps);
                    materialLotActions.add(materialLotAction);

                    // ERP_MOA插入数据
                    ErpMoa erpMoa = new ErpMoa();
                    erpMoa.setFQty(mesPackedLot.getWaferQty());
                    erpMoa.setWarehouseCode(warehouseName);
                    erpMoa.setMesPackedLot(mesPackedLot);
                    erpMoa.setCMOCode(mesPackedLot.getShipSequenceNumber());

                    //从发料的Lot上获取物料编码等相关数据
                    erpMoa.setCMemo("EMPTY");
                    erpMoa.setMaterialBonded(materialLot.getReserved6());
                    erpMoa.setMaterialCode(materialLot.getMaterialName());
                    erpMoa.setMaterialQty(mesPackedLot.getWaferQty());
                    erpMoa.setMaterialGrade(materialLot.getGrade());
                    erpMoa.setMaterialVersion(materialLot.getReserved1());
                    erpMoa.setProdCate(materialLot.getProductType());

                    erpMoaList.add(erpMoa);
                }
                mmsService.receiveMLotList2Warehouse(material, materialLotActions);
            };

            List<Long> parentRrnList = packedLotList.stream().map(MesPackedLot :: getParentRrn).collect(Collectors.toList());
            mesPackedLotRepository.updatePackedStatusByPackedLotRrnList(MesPackedLot.PACKED_STATUS_RECEIVED, parentRrnList);

            if(CollectionUtils.isNotEmpty(erpMoaList)){
                erpMoaRepository.saveAll(erpMoaList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 组建RW待接收完成品
     * @param mesPackedLot
     * @param material
     * @param totalQuantity
     * @param count
     * @return
     */
    private MesPackedLot getRwReceicvePackedLotByBoxId(MesPackedLot mesPackedLot, Material material, Long totalQuantity, int count) throws ClientException{
        try {
            MesPackedLot packedLot = new MesPackedLot();
            MaterialLot materialLot = materialLotRepository.findByLotIdAndWorkOrderIdAndStatus(mesPackedLot.getLotId(), mesPackedLot.getWorkorderId(), MaterialLotUnit.STATE_ISSUE);
            if(!StringUtils.isNullOrEmpty(materialLot.getReserved13()) && MaterialLot.SH_WAREHOUSE.equals(materialLot.getReserved13())){
                packedLot.setLocation(MaterialLot.LOCATION_SH);
            }

            PropertyUtils.copyProperties(mesPackedLot, packedLot, new HistoryBeanConverter());
            String mLotId = mmsService.generatorMLotId(material);
            packedLot.setBoxId(mLotId);
            packedLot.setPackedLotRrn(null);
            packedLot.setSubName(materialLot.getReserved22());
            packedLot.setProductType(materialLot.getProductType());
            packedLot.setImportType(materialLot.getReserved49());
            packedLot.setWaferId(StringUtils.EMPTY);
            packedLot.setFabDevice(materialLot.getReserved24());
            packedLot.setQuantity(totalQuantity.intValue());
            packedLot.setWaferQty(count);

            return packedLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取RW完成品接收打印参数
     */
    private Map<String,String> getRwPackedLotPrintParameter(MesPackedLot packedLot, Map<String, List<MesPackedLot>> packedLotMap) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            List<MesPackedLot> packedLotList = packedLotMap.get(packedLot.getCstId());
            String productId = packedLot.getProductId();
            parameterMap.put("PRODUCTID", productId.substring(0, productId.lastIndexOf("-")));
            parameterMap.put("CSTID", packedLot.getCstId());
            parameterMap.put("WAFERQTY", packedLot.getWaferQty().toString());
            parameterMap.put("LOCATION", packedLot.getBondedProperty());
            parameterMap.put("SUBCODE", packedLot.getLevelTwoCode());
            parameterMap.put("LOIID", packedLot.getCstId());
            parameterMap.put("LOTCST", packedLot.getLotId());
            parameterMap.put("PCODE", packedLot.getPcode());
            parameterMap.put("QTY", packedLot.getQuantity().toString());
            int i = 1;
            for(MesPackedLot mesPackedLot : packedLotList){
                parameterMap.put("frameID" + i, mesPackedLot.getBoxId());
                parameterMap.put("chipQty" + i, mesPackedLot.getQuantity().toString());
                i++;
            }
            for (int j = i; j <= 13; j++) {
                parameterMap.put("frameID" + j, StringUtils.EMPTY);
                parameterMap.put("chipQty" + j, StringUtils.EMPTY);
            }

            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COB产线接收入库
     * @param packedLotList
     * @throws ClientException
     */
    public void receiveCOBFinishGood(List<MesPackedLot> packedLotList) throws ClientException {
        try {
            List<MaterialLot> materialLotList = Lists.newArrayList();
            Map<String, List<MesPackedLot>> packedLotMap = packedLotList.stream().map(packedLot -> mesPackedLotRepository.findByBoxId(packedLot.getBoxId())).collect(Collectors.groupingBy(MesPackedLot :: getCstId));
            List<MesPackedLot> mesPackedLots = Lists.newArrayList();
            for(String cstId : packedLotMap.keySet()){
                List<MesPackedLot> mesPackedLotList = packedLotMap.get(cstId);
                Long totalQty = mesPackedLotList.stream().collect(Collectors.summingLong(mesPackedLot -> mesPackedLot.getQuantity().longValue()));
                String productId = mesPackedLotList.get(0).getProductId();
                Material material = mmsService.getProductByName(productId);
                if (material == null) {
                    throw new ClientParameterException(MmsException.MM_PRODUCT_ID_IS_NOT_EXIST, productId);
                }

                //将晶圆信息合并为一个箱号
                MesPackedLot mesPackedLot = getReceicvePackedLotByPackedWafer(mesPackedLotList.get(0), material, totalQty, mesPackedLotList.size());
                mesPackedLots.add(mesPackedLot);
            }
            receiveFinishGood(mesPackedLots);
            saveMaterialLotUnitAndSaveHis(packedLotMap, mesPackedLots, materialLotList);

            mesPackedLotRepository.updatePackedStatusByPackedLotRrnList(MesPackedLot.PACKED_STATUS_RECEIVED, packedLotList.stream().map(MesPackedLot :: getPackedLotRrn).collect(Collectors.toList()));
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 将晶圆信息合并为一个箱号
     * @param mesPackedLot
     * @param material
     * @param totalQty
     * @param number
     * @return
     */
    private MesPackedLot getReceicvePackedLotByPackedWafer(MesPackedLot mesPackedLot, Material material, Long totalQty, int number) throws ClientException{
        try {
            MaterialLot materialLot = new MaterialLot();
            MesPackedLot packedLot = new MesPackedLot();
            String subName = StringUtils.EMPTY;
            String fabDevice = StringUtils.EMPTY;
            String location = StringUtils.EMPTY;
            String inFlag = mesPackedLot.getInFlag();
            String productCategory = mesPackedLot.getProductCategory();
            PropertyUtils.copyProperties(mesPackedLot, packedLot, new HistoryBeanConverter());
            if(StringUtils.isNullOrEmpty(mesPackedLot.getMaterialLotName())){
                List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByUnitIdAndWorkOrderIdAndState(mesPackedLot.getWaferId(), mesPackedLot.getWorkorderId(), MaterialLotUnit.STATE_ISSUE);
                if(CollectionUtils.isNotEmpty(materialLotUnits)){
                    materialLot = mmsService.getMLotByMLotId(materialLotUnits.get(0).getMaterialLotId());
                } else {
                    throw new ClientParameterException(GcExceptions.WAFER_ID__IS_NOT_EXIST, mesPackedLot.getWaferId(), mesPackedLot.getWorkorderId());
                }
            } else {
                materialLot = mmsService.getMLotByMLotId(mesPackedLot.getMaterialLotName());
            }
            if(materialLot != null){
                if(!MaterialLotUnit.PRODUCT_CATEGORY_WLT.equals(productCategory)){
                    if(MesPackedLot.IN_FLAG_ONE.equals(inFlag)){
                        subName = materialLot.getReserved22();
                    } else {
                        if(MesPackedLot.ZH_WAREHOUSE.equals(materialLot.getReserved13())){
                            subName = MesPackedLot.ZJ_SUB_NAME;
                        } else {
                            subName = MesPackedLot.SH_SUB_NAME;
                        }
                    }
                }

                if(MaterialLotUnit.PRODUCT_CATEGORY_WLT.equals(productCategory) || MaterialLotUnit.PRODUCT_CATEGORY_CP.equals(productCategory) ||
                        MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(productCategory) || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(productCategory)){
                    fabDevice = materialLot.getReserved24();
                }

                if(!StringUtils.isNullOrEmpty(materialLot.getReserved13()) && MaterialLot.SH_WAREHOUSE.equals(materialLot.getReserved13())){
                    location = MaterialLot.LOCATION_SH;
                }

                packedLot.setLocation(location);
                packedLot.setFabDevice(fabDevice);
                packedLot.setImportType(materialLot.getReserved49());
                packedLot.setSubName(subName);
                packedLot.setProductType(materialLot.getProductType());
            }

            String mLotId = mmsService.generatorMLotId(material);
            packedLot.setBoxId(mLotId);
            packedLot.setPackedLotRrn(null);
            packedLot.setWaferId("");
            packedLot.setQuantity(totalQty.intValue());
            packedLot.setWaferQty(number);

            return packedLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 产品型号二级代码信息
     * @param productId
     * @param subcode
     * @return
     */
    private GCProductSubcode getProductAndSubcodeInfo(String productId, String subcode) throws ClientException {
        try {
            return gcProductSubcodeSetRepository.findByProductIdAndSubcode(productId, subcode);
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存产品二级代码信息
     * @param productSubcode
     * @return
     */
    public GCProductSubcode saveProductSubcode(GCProductSubcode productSubcode) throws ClientException {
        try {
            String productId = productSubcode.getProductId().trim();
            GCProductSubcode oldProductSubcode = getProductAndSubcodeInfo(productId ,productSubcode.getSubcode().trim());
            if(oldProductSubcode != null){
                throw new ClientParameterException(GcExceptions.PRODUCT_AND_SUBCODE_IS_EXIST);
            }
            RawMaterial rawMaterial = mmsService.getRawMaterialByName(productId);
            if(rawMaterial == null){
                rawMaterial = new RawMaterial();
                rawMaterial.setName(productId);
                mmsService.createRawMaterial(rawMaterial);
            }
            productSubcode.setProductId(productId);
            productSubcode.setSubcode(productSubcode.getSubcode().trim());
            productSubcode = gcProductSubcodeSetRepository.saveAndFlush(productSubcode);
            return productSubcode;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void importProductSubCode(List<GCProductSubcode> productSubcodeList) throws ClientException{
        try {
            for(GCProductSubcode productSubcode : productSubcodeList){
                String materialName = productSubcode.getProductId();
                if(StringUtils.isNullOrEmpty(materialName)){
                    throw new ClientParameterException(GcExceptions.PRODUCT_ID_CANNOT_EMPTY, materialName);
                }
                saveProductSubcode(productSubcode);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据导入文件名称获取保税属性
     * @param fileName
     * @return
     */
    public String validationAndGetBondedPropertyByFileName(String fileName) throws ClientException{
        try {
            String bondedProperty = "";
            List<NBOwnerReferenceList> bondedProReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(MaterialLot.BONDED_PROPERTY_LIST, NBReferenceList.CATEGORY_OWNER);
            String bondedPro = fileName.substring(fileName.lastIndexOf("_")+1);
            bondedPro = bondedPro.substring(0, bondedPro.indexOf("."));
            for (NBOwnerReferenceList nbOwnerReferenceList : bondedProReferenceList){
                if(nbOwnerReferenceList.getValue().equals(bondedPro)){
                    bondedProperty = bondedPro;
                }
            }
            if(StringUtils.isNullOrEmpty(bondedProperty)){
                throw new ClientParameterException(GcExceptions.FILE_NAME_CANNOT_BONDED_PROPERTY_INFO);
            }
            return bondedProperty;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证来料信息中产品型号和二级代码是否存在
     */
    public void validateMLotUnitProductAndSubcode(List<MaterialLotUnit> materialLotUnitList) throws ClientException{
        try {
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getLotId));
            for (String lotId : materialLotUnitMap.keySet()){
                MaterialLotUnit materialLotUnit = materialLotUnitMap.get(lotId).get(0);
                GCProductSubcode gcProductSubcode = getProductAndSubcodeInfo(materialLotUnit.getMaterialName(), materialLotUnit.getReserved1());
                if(gcProductSubcode == null ){
                    throw new ClientParameterException(GcExceptions.PRODUCT_AND_SUBCODE_IS_NOT_EXIST, materialLotUnit.getMaterialName() + StringUtils.SPLIT_CODE + materialLotUnit.getReserved1());
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 批量修改真空包的入库备注
     */
    public void updateMaterialLotTreasuryNote(List<MaterialLot> materialLotList, String treasuryNote) throws ClientException{
        try {
            for (MaterialLot materialLot : materialLotList){
                materialLot.setReserved4(treasuryNote);
                materialLot = materialLotRepository.saveAndFlush(materialLot);
                // 记录历史
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_UPDATE_TREASURY_NOTE);
                history.setTransQty(materialLot.getCurrentQty());
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 批量修改真空包的保税属性
     */
    public void updateMaterialLotLocation(List<MaterialLot> materialLotList, String location, String remarks) throws ClientException{
        try {
            for (MaterialLot materialLot : materialLotList){
                materialLot.setReserved6(location);
                materialLot = materialLotRepository.saveAndFlush(materialLot);

                List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                    for (MaterialLotUnit materialLotUnit : materialLotUnitList){
                        materialLotUnit.setReserved4(location);
                        materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                        MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, TRANS_TYPE_UPDATE_LOCATION);
                        materialLotUnitHistory.setActionComment(remarks);
                        materialLotUnitHistory.setTransQty(materialLotUnit.getCurrentQty());
                        materialLotUnitHisRepository.save(materialLotUnitHistory);
                    }
                }
                // 记录历史
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_UPDATE_LOCATION);
                history.setActionComment(remarks);
                history.setTransQty(materialLot.getCurrentQty());
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 真空包批量HOLD
     */
    public void materialLotHold(List<MaterialLot> materialLotList, String holdReason, String remarks) throws ClientException{
        try {
            //对CP的物料批次Hold时报告状态
            List<MaterialLot> scmReportMLotList = Lists.newArrayList();
            for (MaterialLot materialLot : materialLotList){
                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setTransQty(materialLot.getCurrentQty());
                materialLotAction.setActionComment(remarks);
                materialLotAction.setActionReason(holdReason);
                mmsService.holdMaterialLot(materialLot,materialLotAction);

                //对箱Hold的时候对箱里面的真空包也做HOLD操作
                List<MaterialLot> packageDetailLots = materialLotRepository.getPackageDetailLots(materialLot.getObjectRrn());
                if(CollectionUtils.isNotEmpty(packageDetailLots)){
                    for(MaterialLot packageLot : packageDetailLots){
                        materialLotAction.setTransQty(packageLot.getCurrentQty());
                        mmsService.holdMaterialLot(packageLot,materialLotAction);
                    }
                }

                if(!StringUtils.isNullOrEmpty(materialLot.getLotId()) && (MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(materialLot.getReserved7())
                        || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CLASSIFY_CP.equals(materialLot.getReserved7()))){
                    scmReportMLotList.add(materialLot);
                }
            }

            //对已经装箱的真空包HOLD时，对箱号也做Hold标记
            Map<String, List<MaterialLot>> packedMLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            for (String parentMaterialLotId : packedMLotMap.keySet()){
                MaterialLot materialLot = mmsService.getMLotByMLotId(parentMaterialLotId);
                materialLot.setHoldState(MaterialLot.HOLD_STATE_ON);
                materialLot = materialLotRepository.saveAndFlush(materialLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_HOLD);
                history.setActionComment(remarks);
                history.setActionReason(holdReason);
                materialLotHistoryRepository.save(history);
            }

            if(CollectionUtils.isNotEmpty(scmReportMLotList)){
                scmService.sendMaterialStateReport(scmReportMLotList, MaterialLotStateReportRequestBody.ACTION_TYPE_HOLD, ThreadLocalContext.getSessionContext());
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 真空包批量释放
     */
    public void materialLotRelease(List<MaterialLot> materialLotList, String ReleaseReason, String remarks) throws ClientException{
        try {
            List<MaterialLot> scmReleaseReportMLotList = Lists.newArrayList();
            for (MaterialLot materialLot : materialLotList){
                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setTransQty(materialLot.getCurrentQty());
                materialLotAction.setActionComment(remarks);
                materialLotAction.setActionReason(ReleaseReason);
                mmsService.releaseMaterialLot(materialLot,materialLotAction);

                //对箱RELESAE的时候对箱里面的真空包也做RELESAE操作
                List<MaterialLot> packageDetailLots = materialLotRepository.getPackageDetailLots(materialLot.getObjectRrn());
                if(CollectionUtils.isNotEmpty(packageDetailLots)){
                    for(MaterialLot packageLot : packageDetailLots){
                        materialLotAction.setTransQty(packageLot.getCurrentQty());
                        mmsService.releaseMaterialLot(packageLot,materialLotAction);
                    }
                }

                if(!StringUtils.isNullOrEmpty(materialLot.getLotId()) && (MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(materialLot.getReserved7())
                        || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CLASSIFY_CP.equals(materialLot.getReserved7()))){
                    scmReleaseReportMLotList.add(materialLot);
                }
            }

            //对已经装箱的真空包Release的时候，所有的真空包都release之后，清除箱号hold标记
            Map<String, List<MaterialLot>> packedMLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            for(String packedMLotId : packedMLotMap.keySet()){
                List<MaterialLot> packedMLotDetials = materialLotRepository.getByParentMaterialLotId(packedMLotId);
                List<MaterialLot> holdMLotList = packedMLotDetials.stream().filter(materialLot -> MaterialLot.HOLD_STATE_ON.equals(materialLot.getHoldState())).collect(Collectors.toList());
                if(CollectionUtils.isEmpty(holdMLotList)){
                    MaterialLot materialLot = mmsService.getMLotByMLotId(packedMLotId);
                    materialLot.setHoldState(MaterialLot.HOLD_STATE_OFF);
                    materialLot.setHoldReason(StringUtils.EMPTY);
                    materialLot = materialLotRepository.saveAndFlush(materialLot);

                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_RELEASE);
                    history.setActionComment(remarks);
                    history.setActionReason(ReleaseReason);
                    materialLotHistoryRepository.save(history);
                }
            }

            if(CollectionUtils.isNotEmpty(scmReleaseReportMLotList)){
                scmService.sendMaterialStateReport(scmReleaseReportMLotList, MaterialLotStateReportRequestBody.ACTION_TYPE_RELEASE, ThreadLocalContext.getSessionContext());
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 查询已经绑定工单并且计划投入日期小于当前日期的物料批次信息
     * @param tableRrn
     * @param whereClause
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> validationAndGetWaitIssueWafer(Long tableRrn,String whereClause) throws ClientException{
        try {
            //获取当前日期，时间格式yyMMdd
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String _whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();

            if (!StringUtils.isNullOrEmpty(nbTable.getWhereClause())) {
                StringBuffer clauseBuffer = new StringBuffer(_whereClause);
                if(!StringUtils.isNullOrEmpty(whereClause)){
                    clauseBuffer.append(" AND ");
                    clauseBuffer.append(whereClause);
                }
                _whereClause = clauseBuffer.toString();
            }

            List<MaterialLot> materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);
            List<MaterialLot> materialLotList = Lists.newArrayList();
            for(MaterialLot materialLot : materialLots){
                String workOrderPlanTime = materialLot.getWorkOrderPlanputTime();
                if(!StringUtils.isNullOrEmpty(workOrderPlanTime)){
                    Date workOrderPlanPutTime = formatter.parse(workOrderPlanTime);
                    if(workOrderPlanPutTime.before(new Date())){
                        materialLotList.add(materialLot);
                    }
                }
            }
            return materialLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取FT待发料的晶圆信息（已经绑定工单的晶圆信息）
     * @param tableRrn
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> queryFTWaitIssueMLotUnitList(long tableRrn) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnitList = Lists.newArrayList();
            //获取当前日期，时间格式yyMMdd
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String _whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();

            if (!StringUtils.isNullOrEmpty(nbTable.getWhereClause())) {
                StringBuffer clauseBuffer = new StringBuffer(_whereClause);
                _whereClause = clauseBuffer.toString();
            }

            List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);
            for(MaterialLotUnit materialLotUnit : materialLotUnits){
                String workOrderPlanTime = materialLotUnit.getWorkOrderPlanputTime();
                if(!StringUtils.isNullOrEmpty(workOrderPlanTime)){
                    Date workOrderPlanPutTime = formatter.parse(workOrderPlanTime);
                    if(workOrderPlanPutTime.before(new Date())){
                        materialLotUnitList.add(materialLotUnit);
                    }
                }
            }
            return materialLotUnitList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存来料信息
     * @param materialLotList
     * @param importType
     * @return
     */
    public String saveLCDCOGDetailList(List<MaterialLot> materialLotList, String importType) throws ClientException {
        try {
            ThreadLocalContext.getSessionContext().buildTransInfo();
            String importCode = generatorMLotsTransId(MaterialLot.GENERATOR_INCOMING_MLOT_IMPORT_CODE_RULE);
            for(MaterialLot materialLot : materialLotList){
                GCLcdCogDetail gcLcdCogDetail = gcLcdCogDetialRepository.findByBoxaIdAndBoxbId(materialLot.getMaterialLotId(), materialLot.getParentMaterialLotId());
                if(gcLcdCogDetail != null){
                    throw new ClientParameterException(GcExceptions.BOXAID_AND_BOXBID_IS_EXIST, gcLcdCogDetail.getBoxaId() + StringUtils.SPLIT_CODE + gcLcdCogDetail.getBoxbId());
                } else {
                    gcLcdCogDetail = new GCLcdCogDetail();
                }
                gcLcdCogDetail.setGcLcdCogDetail(materialLot);
                gcLcdCogDetail.setWarehouseId(materialLot.getReserved13());
                gcLcdCogDetail.setImportCode(importCode);
                gcLcdCogDetail.setImportType(importType);
                gcLcdCogDetialRepository.save(gcLcdCogDetail);
            }
            return importCode;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取非COB真空包或箱号二维码标签打印参数信息
     * @param materialLot
     * @param printVboxLabelFlag
     * @return
     */
    public List<Map<String, String>> getBoxQRCodeLabelPrintParamater(MaterialLot materialLot, String printVboxLabelFlag) throws ClientException {
        try {
            ThreadLocalContext.getSessionContext().buildTransInfo();
            List<Map<String, String>> parameterMapList = Lists.newArrayList();

            if(StringUtils.isNullOrEmpty(materialLot.getReserved16())){
                throw new ClientParameterException(GcExceptions.MATERIALLOT_RESERVED_ORDER_IS_NULL, materialLot.getMaterialLotId());
            }

            //获取当前日期，时间格式yyMMdd
            SimpleDateFormat formatter = new SimpleDateFormat(MaterialLot.PRINT_DATE_PATTERN);
            String date = formatter.format(new Date());

            //从产品上获取真空包的标准数量，用于区分真空包属于零包还是散包
            Product product = mmsService.getProductByName(materialLot.getMaterialName());
            BigDecimal packageTotalQty = new BigDecimal(product.getReserved1());
            ErpSo erpSo = getErpSoByReserved16(materialLot.getReserved16());

            String dateAndNumber = StringUtils.EMPTY;
            String twoDCode = StringUtils.EMPTY;
            String boxSeq = StringUtils.EMPTY;
            String printSeq = StringUtils.EMPTY;
            String flow = StringUtils.EMPTY;

            if(StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                Map<String, String> parameterMap = Maps.newHashMap();
                printSeq = generatorMLotsTransId(MaterialLot.GENERATOR_QRCODE_LABEL_PRINT_SEQ_RULE).substring(8, 14);
                flow = printSeq + StringUtils.UNDERLINE_CODE  + printSeq;
                parameterMap.put("VENDER", MaterialLot.GC_CODE);
                parameterMap.put("MATERIALCODE", erpSo.getOther10());
                dateAndNumber = date + StringUtils.UNDERLINE_CODE + StringUtil.leftPad(materialLot.getCurrentQty().toString() , 6 , "0") + StringUtils.UNDERLINE_CODE;
                parameterMap.put("DATEANDNUMBER", dateAndNumber);
                if(packageTotalQty.compareTo(materialLot.getCurrentQty()) > 0 ){
                    boxSeq = MLotCodePrint.VBOXSEQ_START_VL + materialLot.getMaterialLotId().substring(materialLot.getMaterialLotId().length() - 3);
                } else {
                    boxSeq = MLotCodePrint.VBOXSEQ_START_VZ + materialLot.getMaterialLotId().substring(materialLot.getMaterialLotId().length() - 3);
                }
                twoDCode = MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE + erpSo.getOther10() + StringUtils.UNDERLINE_CODE + dateAndNumber + StringUtils.UNDERLINE_CODE + flow;
                parameterMap.put("FLOW", flow);
                parameterMap.put("BOXSEQ", boxSeq);
                parameterMap.put("TWODCODE", twoDCode);
                parameterMap.put("printCount", "1");

                parameterMapList.add(parameterMap);
            } else {
                //如果勾选打印箱中真空包标签信息，需要按照整包和零包进行分组，再按照是否打印真空包flag组装Map
                List<MaterialLot> materialLotList = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                List<MaterialLot> fullPackageMLotList = new ArrayList<>();
                List<MaterialLot> zeroPackageMLotList = new ArrayList<>();
                for(MaterialLot mLot : materialLotList){
                    if(packageTotalQty.compareTo(mLot.getCurrentQty()) > 0){
                        zeroPackageMLotList.add(mLot);
                    }else {
                        fullPackageMLotList.add(mLot);
                    }
                }
                if(MaterialLot.PRINT_CHECK.equals(printVboxLabelFlag)){
                    if( CollectionUtils.isNotEmpty(fullPackageMLotList)){
                        parameterMapList = getQRCodeLabelPrintParmByVboxStandardQty(parameterMapList ,fullPackageMLotList, erpSo, date, MLotCodePrint.VBOXSEQ_START_VZ);
                    }
                    if( CollectionUtils.isNotEmpty(zeroPackageMLotList)){
                        parameterMapList = getQRCodeLabelPrintParmByVboxStandardQty(parameterMapList ,zeroPackageMLotList, erpSo, date, MLotCodePrint.VBOXSEQ_START_VL);
                    }
                } else {
                    //不打印真空包标签也要区分散包零包的箱标签
                    if( CollectionUtils.isNotEmpty(fullPackageMLotList)){
                        parameterMapList = getQRCodeBoxLabelPrintParmByVboxStandardQty(parameterMapList, fullPackageMLotList, erpSo, date, MLotCodePrint.BOXSEQ_START_BZ);
                    }
                    if( CollectionUtils.isNotEmpty(zeroPackageMLotList)){
                        parameterMapList = getQRCodeBoxLabelPrintParmByVboxStandardQty(parameterMapList, zeroPackageMLotList, erpSo, date, MLotCodePrint.BOXSEQ_START_BL);
                    }
                }
            }
            return parameterMapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取区分整包零包之后的箱标签打印参数
     * @param parameterMapList
     * @param materialLotList
     * @param erpSo
     * @param date
     * @param boxSeq
     * @return
     * @throws ClientException
     */
    private List<Map<String,String>> getQRCodeBoxLabelPrintParmByVboxStandardQty(List<Map<String,String>> parameterMapList, List<MaterialLot> materialLotList, ErpSo erpSo, String date, String boxSeq) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            String dateAndNumber = StringUtils.EMPTY;
            String printSeq = StringUtils.EMPTY;
            String flow = StringUtils.EMPTY;
            String twoDCode = StringUtils.EMPTY;

            Long totalQty = materialLotList.stream().collect(Collectors.summingLong(mesPackedLot -> mesPackedLot.getCurrentQty().longValue()));
            String  startPrintSeq = "";
            int vobxQty = materialLotList.size();
            for(int i=0; i < vobxQty; i++){
                printSeq = generatorMLotsTransId(MaterialLot.GENERATOR_QRCODE_LABEL_PRINT_SEQ_RULE).substring(8, 14);
                if(StringUtils.isNullOrEmpty(startPrintSeq)){
                    startPrintSeq = printSeq;
                }
                flow = printSeq + StringUtils.UNDERLINE_CODE + printSeq;
            }
            dateAndNumber = date + StringUtils.UNDERLINE_CODE + StringUtil.leftPad(totalQty.toString(), 6 , "0");
            twoDCode = MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE + erpSo.getOther10() + StringUtils.UNDERLINE_CODE + dateAndNumber + StringUtils.UNDERLINE_CODE + flow;
            parameterMap.put("VENDER", MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE);
            parameterMap.put("MATERIALCODE", erpSo.getOther10() + StringUtils.UNDERLINE_CODE);
            parameterMap.put("DATEANDNUMBER", dateAndNumber + StringUtils.UNDERLINE_CODE);
            parameterMap.put("FLOW", flow);
            parameterMap.put("BOXSEQ", MLotCodePrint.BOXSEQ_START_BL);
            parameterMap.put("TWODCODE", twoDCode);
            parameterMap.put("printCount", "2");
            parameterMapList.add(parameterMap);

            //将箱号二维码信息记录到真空包上
            for(MaterialLot mLot : materialLotList){
                mLot.setBoxQrcodeInfo(twoDCode);
                materialLotRepository.saveAndFlush(mLot);
            }
            return parameterMapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取真空包和箱号标签打印参数信息
     * 物料信息已经通过真空包标准数量分组
     * @param parameterMapList
     * @param materialLotList
     * @return
     */
    private List<Map<String, String>> getQRCodeLabelPrintParmByVboxStandardQty(List<Map<String, String>> parameterMapList, List<MaterialLot> materialLotList, ErpSo erpSo, String date, String boxStart)throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            String dateAndNumber = StringUtils.EMPTY;
            String printSeq = StringUtils.EMPTY;
            String flow = StringUtils.EMPTY;
            String boxSeq = StringUtils.EMPTY;
            String twoDCode = StringUtils.EMPTY;

            Long fullPackageTotalQty = materialLotList.stream().collect(Collectors.summingLong(mesPackedLot -> mesPackedLot.getCurrentQty().longValue()));
            String  startPrintSeq = "";
            for(MaterialLot materialLot : materialLotList){
                parameterMap = Maps.newHashMap();
                printSeq = generatorMLotsTransId(MaterialLot.GENERATOR_QRCODE_LABEL_PRINT_SEQ_RULE).substring(8, 14);
                if(StringUtils.isNullOrEmpty(startPrintSeq)){
                    startPrintSeq = printSeq;
                }
                flow = printSeq + StringUtils.UNDERLINE_CODE + printSeq;
                dateAndNumber = date + StringUtils.UNDERLINE_CODE + StringUtil.leftPad(materialLot.getCurrentQty().toString() , 6 , "0");
                boxSeq = boxStart + materialLot.getMaterialLotId().substring(materialLot.getMaterialLotId().length() - 3);
                twoDCode = MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE + erpSo.getOther10() + StringUtils.UNDERLINE_CODE + dateAndNumber + StringUtils.UNDERLINE_CODE + flow;
                parameterMap.put("VENDER", MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE);
                parameterMap.put("MATERIALCODE", erpSo.getOther10() + StringUtils.UNDERLINE_CODE);
                parameterMap.put("DATEANDNUMBER", dateAndNumber + StringUtils.UNDERLINE_CODE);
                parameterMap.put("FLOW", flow);
                parameterMap.put("BOXSEQ", boxSeq);
                parameterMap.put("TWODCODE", twoDCode);
                parameterMap.put("printCount", "1");
                parameterMapList.add(parameterMap);

                //将二维码信息记录到真空包上
                materialLot.setVboxQrcodeInfo(twoDCode);
                materialLotRepository.saveAndFlush(materialLot);
            }
            //获取箱标签信息
            parameterMap = Maps.newHashMap();
            dateAndNumber = date + StringUtils.UNDERLINE_CODE + StringUtil.leftPad( fullPackageTotalQty.toString(), 6 , "0");
            flow = startPrintSeq + StringUtils.UNDERLINE_CODE + printSeq;
            twoDCode = MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE + erpSo.getOther10() + StringUtils.UNDERLINE_CODE + dateAndNumber + StringUtils.UNDERLINE_CODE + flow;
            parameterMap.put("VENDER", MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE);
            parameterMap.put("MATERIALCODE", erpSo.getOther10() + StringUtils.UNDERLINE_CODE);
            parameterMap.put("DATEANDNUMBER", dateAndNumber + StringUtils.UNDERLINE_CODE);
            parameterMap.put("FLOW", flow);
            parameterMap.put("BOXSEQ", MLotCodePrint.BOXSEQ_START_BL);
            parameterMap.put("TWODCODE", twoDCode);
            parameterMap.put("printCount", "2");
            parameterMapList.add(parameterMap);

            //将箱号二维码信息记录到真空包上
            for(MaterialLot materialLot : materialLotList){
                materialLot.setBoxQrcodeInfo(twoDCode);
                materialLotRepository.saveAndFlush(materialLot);
            }
            return parameterMapList;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * 获取真空包或箱号标签打印参数信息
     * COB标签只打印箱号标签，不打印真空包
     * @param materialLot
     * @return
     */
    public Map<String, String> getCOBBoxLabelPrintParamater(MaterialLot materialLot) throws ClientException {
        try {
            ThreadLocalContext.getSessionContext().buildTransInfo();
            if(StringUtils.isNullOrEmpty(materialLot.getReserved16())){
                throw new ClientParameterException(GcExceptions.MATERIALLOT_RESERVED_ORDER_IS_NULL, materialLot.getMaterialLotId());
            }
            Map<String, String> parameterMap = Maps.newHashMap();
            SimpleDateFormat formatter = new SimpleDateFormat(MaterialLot.PRINT_DATE_PATTERN);
            String date = formatter.format(new Date());

            ErpSo erpSo = getErpSoByReserved16(materialLot.getReserved16());
            String printSeq = generatorMLotsTransId(MaterialLot.GENERATOR_QRCODE_LABEL_PRINT_SEQ_RULE).substring(8, 14);
            String flow = printSeq + StringUtils.UNDERLINE_CODE  + printSeq;
            String dateAndNumber = date + StringUtils.UNDERLINE_CODE + StringUtil.leftPad(materialLot.getCurrentQty().toString() , 6 , "0");
            String twoDCode = MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE + erpSo.getOther10() + StringUtils.UNDERLINE_CODE + dateAndNumber + StringUtils.UNDERLINE_CODE + flow;
            parameterMap.put("VENDER", MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE);
            parameterMap.put("MATERIALCODE", erpSo.getOther10() + StringUtils.UNDERLINE_CODE);
            parameterMap.put("DATEANDNUMBER", dateAndNumber + StringUtils.UNDERLINE_CODE);
            parameterMap.put("FLOW", flow);
            parameterMap.put("BOXSEQ", "BZ");
            parameterMap.put("TWODCODE", twoDCode);

            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 删除COG明细信息
     * @param lcdCogDetails
     * @param deleteNote
     * @return
     */
    public void deleteCogDetail(List<GCLcdCogDetail> lcdCogDetails, String deleteNote) throws ClientException{
        try {
            for(GCLcdCogDetail lcdCogDetail : lcdCogDetails){
                gcLcdCogDetialRepository.delete(lcdCogDetail);

                GCLcdCogDetailHis history = (GCLcdCogDetailHis) baseService.buildHistoryBean(lcdCogDetail, NBHis.TRANS_TYPE_DELETE);
                history.setActionComment(deleteNote);
                gcLcdCogDetialHisRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void deleteCogEcretive(List<MaterialLot> lcdCogEcretiveList, String deleteNote) throws ClientException{
        try {
            //RMA模板的数据可能箱号为空，赋值避免后面分组报错
            for (MaterialLot materialLot:lcdCogEcretiveList) {
                if(StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())){
                    materialLot.setParentMaterialLotId("");
                }
            }
            Map<String, List<MaterialLot>> materialLotMap = lcdCogEcretiveList.stream().collect(Collectors.groupingBy(MaterialLot:: getParentMaterialLotId));
            for (String materialLotId:materialLotMap.keySet()) {
                if(StringUtils.isNullOrEmpty(materialLotId)){
                    continue;
                }
                List<MaterialLot> fullBoxData = materialLotRepository.getByParentMaterialLotId(materialLotId);
                if(materialLotMap.get(materialLotId).size() < fullBoxData.size()){
                    throw new ClientParameterException(GcExceptions.MUST_DELETE_FULL_BOX_DATA, materialLotId);
                }
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId);
                materialLotRepository.delete(materialLot);
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_DELETE);
                materialLotHistoryRepository.save(history);
            }
            for (MaterialLot materialLot:lcdCogEcretiveList) {
                materialLotRepository.delete(materialLot);
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_DELETE);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证晶圆是否是ENG
     * @param materialLotUnitList
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> materialLotUnitAssignEng(List<MaterialLotUnit> materialLotUnitList) throws ClientException{
        try {
            if (SystemPropertyUtils.getConnectScmFlag()) {
                // 请求SCM做是否是ENG产品的验证
                log.info("Request  SCM  validation product ENG or Prod");
                materialLotUnitList = scmService.assignEngFlag(materialLotUnitList);
                log.info("Request  SCM  validation product ENG or Prod  end ");
            }
            return materialLotUnitList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public List<MaterialLotUnit> validateAndSetWaferSource(String importType, String checkFourCodeFlag, List<MaterialLotUnit> materialLotUnitList) throws ClientException{
        try {
            //按照载具号分组，相同载具号的产品型号、晶圆数量必须一致(暂时只对WLA未测（-2.5）模板做特殊验证处理)
            String waferSource = StringUtils.EMPTY;
            Map<String, List<MaterialLotUnit>> mLotUnitMap = new HashMap<>();
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getLotId));
            if(MaterialLotUnit.WLA_UNMEASURED.equals(importType)){
                Pattern pattern = Pattern.compile("^[_].{3,4}$");
                for (String lotId : materialLotUnitMap.keySet()) {
                    List<MaterialLotUnit> materialLotUnits = materialLotUnitMap.get(lotId);
                    Integer waferCount = materialLotUnits.get(0).getCurrentSubQty().intValue();
                    if(waferCount != materialLotUnits.size()){
                        throw new ClientParameterException(GcExceptions.MATERIAL_LOT_WAFER_QTY_IS_NOT_SAME_REAL_QTY, lotId);
                    }
                    mLotUnitMap = groupMaterialLotUnitByMLotIdAndMaterialNameAndWaferQty(materialLotUnits);
                    if(mLotUnitMap.size() > 1){
                        throw new ClientParameterException(GcExceptions.MATERIALNAME_AND_WAFERQTY_IS_NOT_SAME, lotId);
                    }
                }
                for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                    materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_WLA);
                    materialLotUnit.setReserved50(MaterialLot.WLA_WAFER_SOURCE);
                    materialLotUnit.setReserved49(MaterialLot.IMPORT_WLA);
                    materialLotUnit.setReserved32(materialLotUnit.getCurrentQty().toString());
                    Matcher matcher = pattern.matcher(materialLotUnit.getReserved38());
                    if(!matcher.find()){
                        throw new ClientParameterException(GcExceptions.MATERIAL_LOT_FOUR_CODE_ERROR, materialLotUnit.getLotId());
                    }
                }
            } else if(MaterialLotUnit.FAB_SENSOR.equals(importType) || MaterialLotUnit.FAB_SENSOR_2UNMEASURED.equals(importType) || MaterialLotUnit.SENSOR_CP_KLT.equals(importType)
                    || MaterialLotUnit.SENSOR_CP.equals(importType) || MaterialLotUnit.SENSOR_UNMEASURED.equals(importType)){
                //根据页面是否勾选四位码检测flag，验证来料信息四位码是否符合，不符合不让导入
                if(MaterialLot.PRINT_CHECK.equals(checkFourCodeFlag)){
                    validateMaterialLotUnitFourCode(materialLotUnitList);
                }
                //验证同一个载具号的晶圆型号是否一致
                for (String lotId : materialLotUnitMap.keySet()) {
                    mLotUnitMap = materialLotUnitMap.get(lotId).stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialName));
                    if(mLotUnitMap.size() > 1){
                        throw new ClientParameterException(GcExceptions.MATERIALNAME_IS_NOT_SAME, lotId);
                    }
                    for(String materialName : mLotUnitMap.keySet()){
                        //通过晶圆型号末尾的数字获取不同的Wafer Source
                        waferSource = valiateMaterialNameAndGetWaferSource(materialName);

                        List<MaterialLotUnit> materialLotUnits = mLotUnitMap.get(materialName);
                        for(MaterialLotUnit materialLotUnit : materialLotUnits){
                            materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_CP);
                            materialLotUnit.setReserved32(materialLotUnit.getCurrentQty().toString());
                            materialLotUnit.setReserved50(waferSource);
                            materialLotUnit.setReserved49(MaterialLot.IMPORT_SENSOR_CP);
                        }
                    }
                }
            }  else if(MaterialLotUnit.FAB_LCD_PTC.equals(importType) || MaterialLotUnit.FAB_LCD_SILTERRA.equals(importType)
                    || MaterialLotUnit.LCD_CP_25UNMEASURED.equals(importType)){
                for (String lotId : materialLotUnitMap.keySet()) {
                    mLotUnitMap = materialLotUnitMap.get(lotId).stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialName));
                    if(mLotUnitMap.size() > 1){
                        throw new ClientParameterException(GcExceptions.MATERIALNAME_IS_NOT_SAME, lotId);
                    }
                    for(String materialName : mLotUnitMap.keySet()){
                        if(materialName.endsWith("-1") || materialName.endsWith("-2.5") || materialName.endsWith("-2.55")){
                            waferSource = MaterialLot.WAFER_SOURCE_END3;
                        } else if(materialName.endsWith("-2.6")) {
                            waferSource = MaterialLot.WAFER_SOURCE_END4;
                        } else {
                            throw new ClientParameterException(GcExceptions.MATERIALNAME_IS_ERROR, materialName);
                        }
                        List<MaterialLotUnit> materialLotUnits = mLotUnitMap.get(materialName);
                        for(MaterialLotUnit materialLotUnit : materialLotUnits){
                            materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_CP);
                            materialLotUnit.setReserved32(materialLotUnit.getCurrentQty().toString());
                            materialLotUnit.setReserved50(waferSource);
                            materialLotUnit.setReserved49(MaterialLot.IMPORT_LCD_CP);
                        }
                    }
                }
            } else if(MaterialLotUnit.SENSOR_PACK_RETURN_COGO.equals(importType)|| MaterialLotUnit.SENSOR_TPLCC.equals(importType)){
                for (MaterialLotUnit materialLotUnit : materialLotUnitList) {
                    materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_SENSOR);
                    materialLotUnit.setReserved32(materialLotUnit.getCurrentQty().toString());
                    materialLotUnit.setReserved50(MaterialLot.SENSOR_WAFER_SOURCE);
                    materialLotUnit.setReserved49(MaterialLot.IMPORT_SENSOR);
                }
            } else {
                for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                    if(MaterialLotUnit.WLT_PACK_RETURN.equals(importType)){
                        materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_WLT);
                        materialLotUnit.setReserved50(MaterialLot.WLT_PACK_RETURN_WAFER_SOURCE);
                        materialLotUnit.setReserved49(MaterialLot.IMPORT_WLT);
                    } else if(MaterialLotUnit.COB_FINISH_PRODUCT.equals(importType)){
                        materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_COB);
                        materialLotUnit.setReserved50(MaterialLot.COB_WAFER_SOURCE);
                        materialLotUnit.setReserved49(MaterialLot.IMPORT_COB);
                    } else if(MaterialLotUnit.SOC_FINISH_PRODUCT.equals(importType)){
                        materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_SOC);
                        materialLotUnit.setReserved50(MaterialLot.SOC_WAFER_SOURCE);
                        materialLotUnit.setReserved49(MaterialLot.IMPORT_SOC);
                    }
                }
            }
            return materialLotUnitList;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * SensorCp型号的晶圆型号验证并获取WaferSource
     * @param materialName
     * @return
     * @throws ClientException
     */
    private String valiateMaterialNameAndGetWaferSource(String materialName) throws ClientException{
        try {
            String waferSource = StringUtils.EMPTY;
            if(materialName.endsWith("-1") || materialName.endsWith("-2") || materialName.endsWith("-1.3")){
                waferSource = MaterialLot.WAFER_SOURCE_END1;
            } else if(materialName.endsWith("-2.1") || materialName.endsWith("-1.1") || materialName.endsWith("-1.4")) {
                waferSource = MaterialLot.WAFER_SOURCE_END2;
            } else {
                throw new ClientParameterException(GcExceptions.MATERIALNAME_IS_ERROR, materialName);
            }
            return waferSource;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证来料信息的四位码
     * @param materialLotUnitList
     */
    private void validateMaterialLotUnitFourCode(List<MaterialLotUnit> materialLotUnitList) {
        try {
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                String waferMark = materialLotUnit.getReserved38();
                if(!StringUtils.isNullOrEmpty(waferMark)){
                    if(waferMark.length() == 4){
                        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]$");
                        Matcher matcher = pattern.matcher(waferMark);
                        if(!matcher.matches()){
                            throw new ClientParameterException(GcExceptions.MATERIAL_LOT_FOUR_CODE_ERROR, materialLotUnit.getLotId());
                        }
                    } else {
                        Pattern pattern = Pattern.compile("^[_][a-zA-Z0-9]{4}$");
                        Matcher matcher = pattern.matcher(waferMark);
                        if(!matcher.matches()){
                            throw new ClientParameterException(GcExceptions.MATERIAL_LOT_FOUR_CODE_ERROR, materialLotUnit.getLotId());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 来料根据晶圆型号、晶圆数量分组
     * @param materialLotUnitList
     * @return
     */
    public Map<String, List<MaterialLotUnit>> groupMaterialLotUnitByMLotIdAndMaterialNameAndWaferQty(List<MaterialLotUnit> materialLotUnitList) {
        return  materialLotUnitList.stream().collect(Collectors.groupingBy(materialLotUnit -> {
            StringBuffer key = new StringBuffer();

            key.append(materialLotUnit.getMaterialName());
            key.append(StringUtils.SPLIT_CODE);

            key.append(materialLotUnit.getCurrentSubQty());
            key.append(StringUtils.SPLIT_CODE);

            return key.toString();
        }));
    }

    /**
     * 委外采购晶圆接收
     * @param materialLotActions
     */
    public void purchaseOutsourceWaferReceive(List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnits = Lists.newArrayList();
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            Warehouse warehouse = new Warehouse();
            for(MaterialLot materialLot : materialLots){
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved13())){
                    warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                }
                if(warehouse == null){
                    throw new ClientParameterException(GcExceptions.WAREHOUSE_CANNOT_EMPTY);
                }
                String warehouseName = warehouse.getName();

                log.info("receive materialLot and materialLotUnits");
                List<MaterialLotUnit> units = materialLotUnitService.receiveMLotWithUnit(materialLot, warehouseName);
                materialLotUnits.addAll(units);
            }

            for(MaterialLot materialLot : materialLots){
                warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                log.info("insert materialLot to mte_in_stock");
                saveErpInStock(materialLot, materialLot.getProductType(), warehouse.getName());
                log.info("insert materialLot to  mte_in_stock end");
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 给中间表保存数据
     * @param materialLot
     * @param prodCate
     * @param warehouseName
     * @throws ClientException
     */
    private void saveErpInStock(MaterialLot materialLot, String prodCate, String warehouseName) throws ClientException{
        try {
            ErpInStock erpInStock = new ErpInStock();
            erpInStock.setProdCate(prodCate);
            erpInStock.setMaterialLot(materialLot);
            if(ErpInStock.WAREHOUSE_ZJ_STOCK.equals(warehouseName)){
                erpInStock.setWarehouse(ErpInStock.ZJ_STOCK);
            } else if(ErpInStock.WAREHOUSE_SH_STOCK.equals(warehouseName)){
                erpInStock.setWarehouse(ErpInStock.SH_STOCK);
            } else if(ErpInStock.WAREHOUSE_HK_STOCK.equals(warehouseName)){
                erpInStock.setWarehouse(ErpInStock.HK_STOCK);
            } else {
                throw new ClientParameterException(GcExceptions.ERP_WAREHOUSE_CODE_IS_UNDEFINED, warehouseName);
            }
            erpInStockRepository.save(erpInStock);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取物料编码标签打印参数
     * @param materialLot
     * @param printType
     * @return
     */
    public List<Map<String, String>> getMlotCodePrintParameter(MaterialLot materialLot, String printType) throws ClientException {
        try {
            ThreadLocalContext.getSessionContext().buildTransInfo();
            List<MaterialLot> packageMLotList = Lists.newArrayList();
            List<Map<String, String>> parameterMapList = Lists.newArrayList();
            //获取当前日期，时间格式yyyy-MM-dd
            SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
            Calendar calendar = Calendar.getInstance();
            String date = formatter.format(new Date());
            Warehouse warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
            ErpSo erpSo = getErpSoByReserved16(materialLot.getReserved16());
            String productType = getProductType(materialLot.getMaterialName());

            //将物料编码记录到真空包上
            if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                packageMLotList = materialLotRepository.getPackageDetailLots(materialLot.getObjectRrn());
                for(MaterialLot packedMLot : packageMLotList){
                    packedMLot.setMaterialCode(erpSo.getOther10());
                    materialLotRepository.saveAndFlush(packedMLot);
                }
            }

            if(MLotCodePrint.GENERAL_MLOT_LABEL.equals(printType)){
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                    for(MaterialLot packageMLot : packageMLotList){
                        Map<String, String> parameterMap = getGeneralMLotPrintParamater(erpSo, packageMLot, warehouse, date, productType);
                        parameterMap.put("LABEL", MLotCodePrint.VBOX_LABEL);
                        parameterMap.put("printCount", "1");
                        parameterMapList.add(parameterMap);
                    }
                    Map<String, String> parameterMap = getGeneralMLotPrintParamater(erpSo, materialLot, warehouse, date, productType);
                    parameterMap.put("LABEL", MLotCodePrint.BOX_LABEL);
                    parameterMap.put("printCount", "2");
                    parameterMapList.add(parameterMap);
                } else {
                    Map<String, String> parameterMap = getGeneralMLotPrintParamater(erpSo, materialLot, warehouse, date, productType);
                    parameterMap.put("LABEL", MLotCodePrint.VBOX_LABEL);
                    parameterMap.put("printCount", "1");
                    parameterMapList.add(parameterMap);
                }
            } else if (MLotCodePrint.OPHELION_MLOT_LABEL.equals(printType)){
                String startDate = formatter.format(materialLot.getReceiveDate());
                calendar.setTime(materialLot.getReceiveDate());
                calendar.add(Calendar.YEAR, +1);
                String endDate = formatter.format(calendar.getTime());
                String [] endDateStrArray = endDate.split(StringUtils.SPLIT_CODE);
                if(endDateStrArray[1].equals("02") && endDateStrArray[2].equals("29")){
                    endDate = endDateStrArray[0] + StringUtils.SPLIT_CODE + endDateStrArray[1] + StringUtils.SPLIT_CODE + "28";
                }
                formatter = new SimpleDateFormat(MLotCodePrint.DATE_PATTERN);
                String effectiveDate = formatter.format(materialLot.getReceiveDate());//有效日期
                String expirationDate = formatter.format(calendar.getTime());//失效日期
                String printSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_OPHELION_MLOT_LABEL_PRINT_SEQ_RULE).substring(2, 7);
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())) {
                    for(MaterialLot packageMLot : packageMLotList) {
                        Map<String, String> parameterMap = getOphelionMLotPrintParamater(erpSo, packageMLot, startDate, date, endDate, effectiveDate, expirationDate, printSeq);
                        parameterMap.put("printCount", "1");
                        parameterMapList.add(parameterMap);
                    }
                    Map<String, String> parameterMap = getOphelionMLotPrintParamater(erpSo, materialLot, startDate, date, endDate, effectiveDate, expirationDate, printSeq);
                    parameterMap.put("printCount", "2");
                    parameterMapList.add(parameterMap);
                } else {
                    Map<String, String> parameterMap = getOphelionMLotPrintParamater(erpSo, materialLot, startDate, date, endDate, effectiveDate, expirationDate, printSeq);
                    parameterMap.put("printCount", "1");
                    parameterMapList.add(parameterMap);
                }
            } else if(MLotCodePrint.BAICHEN_MLOT_LABEL.equals(printType)){
                String firstVboxSeq = "";
                String poName = erpSo.getOther10();
                String ponoPrefix = "";
                if(!StringUtils.isNullOrEmpty(poName)){
                    int poSize = poName.length();
                    if(poSize < 20){
                        ponoPrefix = StringUtil.leftPad("" , 20 - poSize , "#");
                    }
                }
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                    for(MaterialLot packageMLot : packageMLotList){
                        String vboxSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_BAICHEN_MLOT_LABEL_PRINT_SEQ_RULE).substring(8, 16);
                        if(StringUtils.isNullOrEmpty(firstVboxSeq)){
                            firstVboxSeq = vboxSeq;
                        }
                        Map<String, String> parameterMap = getBaiChenMLotPrintParamater(erpSo, packageMLot, vboxSeq, ponoPrefix);
                        parameterMap.put("printCount", "1");
                        parameterMapList.add(parameterMap);
                    }
                    Map<String, String> parameterMap = getBaiChenMLotPrintParamater(erpSo, materialLot, firstVboxSeq, ponoPrefix);
                    parameterMap.put("printCount", "2");
                    parameterMapList.add(parameterMap);
                } else {
                    String vboxSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_BAICHEN_MLOT_LABEL_PRINT_SEQ_RULE).substring(8, 16);
                    vboxSeq = vboxSeq.substring(vboxSeq.length() - 8, vboxSeq.length());
                    Map<String, String> parameterMap = getBaiChenMLotPrintParamater(erpSo, materialLot, vboxSeq, ponoPrefix);
                    parameterMap.put("printCount", "1");
                    parameterMapList.add(parameterMap);
                }
            } else if(MLotCodePrint.GUANGBAO_BOX_LABEL.equals(printType)){
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                    for(MaterialLot packageMLot : packageMLotList){
                        Map<String, String> parameterMap = getGuangBaoVboxMLotPrintParamater(erpSo, packageMLot);
                        parameterMapList.add(parameterMap);
                    }
                    Map<String, String> parameterMap = getGeneralMLotPrintParamater(erpSo, materialLot, warehouse, date, productType);
                    parameterMap.put("LABEL", MLotCodePrint.BOX_LABEL);
                    parameterMap.put("printCount", "2");
                    parameterMapList.add(parameterMap);
                } else {
                    Map<String, String> parameterMap = getGuangBaoVboxMLotPrintParamater(erpSo, materialLot);
                    parameterMapList.add(parameterMap);
                }
            } else if(MLotCodePrint.GUANGBAO_VBOX_LABEL.equals(printType)){
                Map<String, String> parameterMap = getGuangBaoVboxMLotPrintParamater(erpSo, materialLot);
                parameterMapList.add(parameterMap);
            } else if(MLotCodePrint.COB_GUANGBAO_LABEL.equals(printType)){
                String materialDesc = StringUtils.EMPTY;
                List<NBOwnerReferenceList> productDescList = getProductDecsList();
                if(CollectionUtils.isNotEmpty(productDescList)){
                    for (NBOwnerReferenceList referenceList : productDescList) {
                        if(referenceList.getKey().equals(materialLot.getMaterialName())){
                            materialDesc = referenceList.getValue();
                        }
                    }
                }
                if(StringUtils.isNullOrEmpty(materialDesc)){
                    throw new ClientParameterException(GcExceptions.MATERIAL_NAME_DESCRIPTION_IS_NOT_CONFIGURED, materialLot.getMaterialName());
                }
                Map<String, String> parameterMap = getCOBGuangBaoMLotPrintParamater(erpSo, materialLot, materialDesc);
                parameterMapList.add(parameterMap);
            } else if(MLotCodePrint.HUATIAN_LABEL.equals(printType)){
                String huaTianPrintSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_HUATIAN_LABEL_PRINT_SEQ_RULE);
                calendar.setTime(new Date());
                calendar.add(Calendar.YEAR, +1);
                String effectiveDate = formatter.format(calendar.getTime());
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                    for(MaterialLot packageMLot : packageMLotList){
                        Map<String, String> parameterMap = getHuaTianMLotPrintParamater(erpSo, packageMLot, warehouse, huaTianPrintSeq, productType, date, effectiveDate);
                        parameterMapList.add(parameterMap);
                    }
                    Map<String, String> parameterMap = getHuaTianMLotPrintParamater(erpSo, materialLot, warehouse, huaTianPrintSeq, productType, date, effectiveDate);
                    parameterMap.put("printCount", "2");
                    parameterMapList.add(parameterMap);
                }
            } else if(MLotCodePrint.SHENGTAI_BOX_LABEL.equals(printType)){
                formatter = new SimpleDateFormat(MaterialLot.PRINT_DATE_PATTERN);
                String stockOutDate = formatter.format(new Date());
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())) {
                    String seq = generatorMLotsTransId(MLotCodePrint.GENERATOR_SHENGTAI_LABEL_PRINT_SEQ_RULE);
                    seq = seq.substring(2,6) + seq.substring(8,11);
                    Map<String, String> parameterMap = getGeneralMLotPrintParamater(erpSo, materialLot, warehouse, date, productType);
                    parameterMap.put("LABEL", MLotCodePrint.BOX_LABEL);
                    parameterMap.put("printCount", "2");
                    parameterMapList.add(parameterMap);
                    for (MaterialLot packageMLot : packageMLotList) {
                        parameterMap = getShengTaiVboxMLotPrintParamater(packageMLot, productType, stockOutDate, seq);
                        parameterMapList.add(parameterMap);
                    }
                }
            } else if(MLotCodePrint.SHENGTAI_VBOX_LABEL.equals(printType)){
                formatter = new SimpleDateFormat(MaterialLot.PRINT_DATE_PATTERN);
                String stockOutDate = formatter.format(new Date());
                String seq = generatorMLotsTransId(MLotCodePrint.GENERATOR_SHENGTAI_LABEL_PRINT_SEQ_RULE);
                seq = seq.substring(2,6) + seq.substring(8,11);
                Map<String, String> parameterMap = getShengTaiVboxMLotPrintParamater(materialLot, productType, stockOutDate, seq);
                parameterMapList.add(parameterMap);
            } else if (MLotCodePrint.BYD_LABEL.equals(printType)){
                String strLabel = MLotCodePrint.BYD_ORDER_ID + StringUtils.SEMICOLON_CODE;
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())) {
                    String boxSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_BYD_BOX_LABEL_PRINT_SEQ_RULE).substring(6, 10);
                    for (MaterialLot packageMLot : packageMLotList) {
                        Map<String, String> parameterMap = getBYDMLotPrintParamater(erpSo, packageMLot, productType, date);
                        String vboxSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_BYD_VBOX_LABEL_PRINT_SEQ_RULE).substring(7, 11);
                        //截取箱号数字起6位作为标签号
                        String vboxLabelId = getLabelIdByMLotId(packageMLot.getMaterialLotId());
                        parameterMap.put("STRLABEL", strLabel + vboxLabelId + vboxSeq);
                        parameterMap.put("printCount", "1");
                        parameterMapList.add(parameterMap);
                    }
                    Map<String, String> parameterMap = getBYDMLotPrintParamater(erpSo, materialLot, productType, date);
                    String boxLabelId = getLabelIdByMLotId(materialLot.getMaterialLotId());
                    parameterMap.put("STRLABEL", strLabel + boxLabelId + boxSeq);
                    parameterMap.put("printCount", "2");
                    parameterMapList.add(parameterMap);
                }
            } else if(MLotCodePrint.XLGD_BOX_LABEL.equals(printType)){
                Map<String, String> parameterMap = getXLGDMLotPrintParamater(erpSo, materialLot, productType);
                parameterMapList.add(parameterMap);
            } else if(MLotCodePrint.SHUN_YU_LABEL.equals(printType)){
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())) {
                    for (MaterialLot packageMLot : packageMLotList) {
                        Map<String, String> parameterMap = getShunYuMLotPrintParamater(erpSo, packageMLot, productType);
                        //截取箱号数字起6位作为生产批号
                        String batchNumber = getLabelIdByMLotId(packageMLot.getMaterialLotId());
                        parameterMap.put("BATCHNUMBER",  batchNumber );
                        parameterMap.put("printCount", "1");
                        parameterMapList.add(parameterMap);
                    }
                    Map<String, String> parameterMap = getShunYuMLotPrintParamater(erpSo, materialLot, productType);
                    String batchNumber = getLabelIdByMLotId(materialLot.getMaterialLotId());
                    parameterMap.put("BATCHNUMBER", batchNumber);
                    parameterMap.put("printCount", "2");
                    parameterMapList.add(parameterMap);
                }
            } else if (MLotCodePrint.ZHONG_KONG_LABEL.equals(printType)){
                Map<String, String> parameterMap = getZhongKongMLotPrintParamater(erpSo, materialLot, date);
                parameterMapList.add(parameterMap);
            } else if(MLotCodePrint.XING_ZHI_MLOT_LABEL.equals(printType)){
                Map<String, String> parameterMap = getXingZhiMLotPrintParamater(erpSo, materialLot, date);
                parameterMapList.add(parameterMap);
            }
            return parameterMapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private Map<String,String> getXingZhiMLotPrintParamater(ErpSo erpSo, MaterialLot materialLot, String date) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            String printSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_XINZHI_MLOT_LABEL_PRINT_SEQ_RULE).substring(8, 11);
            SimpleDateFormat formatter = new SimpleDateFormat(MLotCodePrint.SHUNYU_PRINT_DATE_PATTERN);
            String createDate = formatter.format(materialLot.getCreated());
            parameterMap.put("STRPONO", erpSo.getCcode());
            parameterMap.put("STRPL", date + printSeq);
            parameterMap.put("STRBOXID", materialLot.getMaterialLotId());
            parameterMap.put("STRWEIGHT", "/");
            String materialCode = erpSo.getOther16();
            if(StringUtils.isNullOrEmpty(materialCode)){
                materialCode = "/";
            }
            parameterMap.put("STRPN", materialCode);
            parameterMap.put("STRDC", createDate);
            parameterMap.put("STRTOTALQTY", materialLot.getCurrentQty().toString());
            String poNo = StringUtil.rightPad(erpSo.getCcode() , 30 , "@");
            String strPL = StringUtil.rightPad(date + printSeq , 20 , "@");
            String strPN = StringUtil.rightPad(materialCode , 25 , "@");
            String origin = StringUtil.rightPad(MLotCodePrint.ORIGIN , 9 , "@");
            String totalQty = StringUtil.rightPad(materialLot.getCurrentQty().toString() , 6 , "@");
            String qrCode = poNo + strPL + strPN;
            List<MaterialLot> materialLotList = packageService.getPackageDetailLots(materialLot.getObjectRrn());
            Integer seq = 1;
            for(MaterialLot mLot : materialLotList){
                qrCode += StringUtil.rightPad(mLot.getMaterialLotId() , 20 , "@");
                qrCode += StringUtil.rightPad(createDate , 10 , "@");
                qrCode += StringUtil.leftPad(seq.toString() , 2 , "0");
                qrCode += StringUtil.rightPad(mLot.getCurrentQty().toString() , 6 , "@");
                qrCode += MLotCodePrint.VBOX_SEQ;
                ++seq;
            }
            qrCode = qrCode + origin + totalQty + MLotCodePrint.END_STR;
            parameterMap.put("STRQRCODE", qrCode);
            parameterMap.put("portId", MLotCodePrint.XING_ZHI_PORTID);
            parameterMap.put("printCount", "2");
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 中控智慧标签
     * @param erpSo
     * @param materialLot
     * @param date
     * @return
     * @throws ClientException
     */
    private Map<String,String> getZhongKongMLotPrintParamater(ErpSo erpSo, MaterialLot materialLot, String date) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            parameterMap.put("NUMBER", erpSo.getOther16());
            parameterMap.put("PRODUCTNAME", materialLot.getMaterialName());
            parameterMap.put("NUM", materialLot.getCurrentQty().toString());
            parameterMap.put("BOXID", materialLot.getMaterialLotId());
            parameterMap.put("DATE", date);
            parameterMap.put("ADDRESS", erpSo.getOther19());
            String qrCode = ",," + materialLot.getCurrentQty().toString() + ",,,,ICGKW004A," + erpSo.getOther16() + "," + materialLot.getMaterialName() + ",," + date + ",";
            parameterMap.put("QRCODE", qrCode);
            parameterMap.put("portId", MLotCodePrint.ZHONG_KONG_PORTID);
            parameterMap.put("printCount", "2");
            return parameterMap;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 信利光电标签打印
     * @param erpSo
     * @param materialLot
     * @return
     * @throws ClientException
     */
    private Map<String,String> getShunYuMLotPrintParamater(ErpSo erpSo, MaterialLot materialLot, String productType) throws ClientException{
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            Map<String, String> parameterMap = Maps.newHashMap();
            SimpleDateFormat formatter = new SimpleDateFormat(MLotCodePrint.SHUNYU_PRINT_DATE_PATTERN);
            String createDate = formatter.format(materialLot.getCreated());
            String materialLotId =  materialLot.getMaterialLotId();
            String batchNumber = StringUtils.EMPTY;
            if(StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                batchNumber = materialLotId.substring(materialLotId.length() - 10, materialLotId.length() - 4);
            } else {
                batchNumber = materialLotId.substring(materialLotId.length() - 9, materialLotId.length() - 3);
            }
            parameterMap.put("NAME", productType);
            parameterMap.put("CLIENTNAME", erpSo.getOther16());
            parameterMap.put("USERID", sc.getUsername());
            parameterMap.put("DATE", createDate);
            parameterMap.put("BATCHNUMBER", batchNumber);
            parameterMap.put("TOTALNUM", materialLot.getCurrentQty().toString());
            parameterMap.put("BOXID", materialLot.getMaterialLotId());
            parameterMap.put("portId", MLotCodePrint.SHUNYU_PORTID);
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 信利光电标签打印
     * @param erpSo
     * @param materialLot
     * @return
     * @throws ClientException
     */
    private Map<String,String> getXLGDMLotPrintParamater(ErpSo erpSo, MaterialLot materialLot, String productType) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(materialLot.getCreated());
            calendar.add(Calendar.YEAR, +1);
            calendar.add(Calendar.MONTH, +6);
            String effectiveDate = formatter.format(calendar.getTime());
            String createDate = formatter.format(materialLot.getCreated());
            parameterMap.put("STRORDERNUMBER", erpSo.getCcode());
            parameterMap.put("STRCODE", erpSo.getOther16());
            parameterMap.put("STRQUANTITY", materialLot.getCurrentQty().toString());
            parameterMap.put("STRBATCH", materialLot.getReserved1());
            parameterMap.put("STRTRADETYPE", MLotCodePrint.XLGD_TRADETYPE);
            parameterMap.put("STRNAME", MLotCodePrint.XLGD_NAME);
            parameterMap.put("STRMODEL", productType);
            parameterMap.put("STRPRODUCTIONDATE", createDate);
            parameterMap.put("STRDATE", effectiveDate);
            parameterMap.put("STRBRAND", MLotCodePrint.XLGD_BRAND);
            parameterMap.put("STRBBOXID", materialLot.getMaterialLotId());
            String strQrCode = erpSo.getOther16() + StringUtils.SEMICOLON_CODE + materialLot.getCurrentQty().toString() +StringUtils.SEMICOLON_CODE + materialLot.getReserved1() +
                    StringUtils.SEMICOLON_CODE + createDate + StringUtils.SEMICOLON_CODE + effectiveDate + StringUtils.SEMICOLON_CODE + "GALAXYCORE.INC;" + erpSo.getCcode();
            parameterMap.put("STRQRCODE", strQrCode);
            parameterMap.put("portId", MLotCodePrint.XLGD_PORTID);
            parameterMap.put("printCount", "2");
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 截取物料批次号数字起6位作为标签号
     * @param materialLotId
     * @return
     * @throws ClientException
     */
    private String getLabelIdByMLotId(String materialLotId) throws ClientException{
        try {
            Pattern pattern = Pattern.compile("([0-9].{5,5})");
            Matcher isCheck = pattern.matcher(materialLotId);
            isCheck.find();
            String labelId = isCheck.group(1);
            return labelId;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 比亚迪内箱标签
     * @param erpSo
     * @param materialLot
     * @param productType
     * @param date
     * @return
     * @throws ClientException
     */
    private Map<String,String> getBYDMLotPrintParamater(ErpSo erpSo, MaterialLot materialLot, String productType, String date) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            parameterMap.put("STRCODING", productType);
            parameterMap.put("STRDESCRIPTION", materialLot.getMaterialDesc());
            parameterMap.put("STRCLIENT", erpSo.getOther16());
            parameterMap.put("STRDATE", date);
            parameterMap.put("STRBATCH", materialLot.getMaterialLotId());
            parameterMap.put("STRNUM", materialLot.getCurrentQty().toString() + "/PCS");
            parameterMap.put("portId", MLotCodePrint.BYD_PORTID);
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 盛泰真空包打印
     * @param materialLot
     * @param productType
     * @param stockOutDate
     * @param seq
     * @return
     * @throws ClientException
     */
    private Map<String,String> getShengTaiVboxMLotPrintParamater(MaterialLot materialLot, String productType, String stockOutDate, String seq) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            String vboxSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_SHENGTAI_VBOX_LABEL_PRINT_SEQ_RULE).substring(2,7);
            String strRno = "R" + MLotCodePrint.STR_SUPPLIER + stockOutDate + vboxSeq;
            parameterMap.put("STRPN", materialLot.getMaterialName());
            parameterMap.put("STRPNNAME", productType);
            parameterMap.put("STRSUPPLIER", MLotCodePrint.STR_SUPPLIER);
            parameterMap.put("STRCOUNT", materialLot.getCurrentQty().toString());
            parameterMap.put("STRVID", materialLot.getMaterialLotId());
            parameterMap.put("STRDATE", stockOutDate);
            parameterMap.put("STRNUMBER", seq);
            parameterMap.put("STRRNO", strRno);

            String strQrCode = strRno + "|" + materialLot.getMaterialName() + "|" + seq + "|" + stockOutDate + "|" + MLotCodePrint.STR_SUPPLIER + "|" + materialLot.getCurrentQty().toString();
            parameterMap.put("STRQRCODE", strQrCode);
            parameterMap.put("printCount", "1");
            parameterMap.put("portId", MLotCodePrint.COB_SHENGTAI_VBOX_PORTID);
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据产品号截取产品信息
     * @param materialName
     * @return
     * @throws ClientException
     */
    private String getProductType(String materialName) throws ClientException{
        try {
            String productType = StringUtils.EMPTY;
            String [] materialNameArray = materialName.split(StringUtils.SPLIT_CODE);
            if(materialNameArray.length >= 3){
                productType = materialName.substring(0, materialName.indexOf(StringUtils.SPLIT_CODE,materialName.indexOf(StringUtils.SPLIT_CODE)+1 ));
            } else {
                productType = materialName;
            }
            return productType;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 华天标签打印
     * @param erpSo
     * @param materialLot
     * @param warehouse
     * @param huaTianPrintSeq
     * @param productType
     * @param stockOutDate
     * @param effectiveDate
     * @return
     * @throws ClientException
     */
    private Map<String,String> getHuaTianMLotPrintParamater(ErpSo erpSo, MaterialLot materialLot, Warehouse warehouse, String huaTianPrintSeq,
                                                            String productType, String stockOutDate, String effectiveDate) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            parameterMap.put("CODE", erpSo.getOther16());
            if(warehouse.getName().equals(WAREHOUSE_HK)){
                parameterMap.put("SUPPLIERNAME", MLotCodePrint.HK_SUPPLIER);
            } else {
                parameterMap.put("SUPPLIERNAME", MLotCodePrint.SH_SUPPLIER);
            }
            parameterMap.put("TYPE", productType);
            parameterMap.put("ORDER", huaTianPrintSeq);
            parameterMap.put("DATE2", effectiveDate);
            parameterMap.put("NUM", materialLot.getCurrentQty().toString());
            parameterMap.put("DATE", stockOutDate);
            parameterMap.put("ID", erpSo.getCcode());
            parameterMap.put("portId", MLotCodePrint.COB_HUATIAN_PORTID);
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COB光宝标签打印
     * @param erpSo
     * @param materialLot
     * @return
     * @throws ClientException
     */
    private Map<String,String> getCOBGuangBaoMLotPrintParamater(ErpSo erpSo, MaterialLot materialLot, String materialDesc) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            String seq = generatorMLotsTransId(MLotCodePrint.GENERATOR_GUANGBAO_VBOX_LABEL_PRINT_SEQ_RULE).substring(2, 13);
            String monthSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_GUANGBAO_VBOX_LABEL_PRINT_MONTH_SEQ_RULE).substring(2, 14);
            SimpleDateFormat formatter = new SimpleDateFormat(MLotCodePrint.PRINT_DATE_PATTERN);
            String date = formatter.format(new Date());
            String reelId = "I50111" + date.substring(2,4);
            //月份转为16进制
            String month = Integer.toHexString(Integer.parseInt(date.substring(4,6)));
            Integer cobMonthSeq = Integer.parseInt(monthSeq.substring(monthSeq.length() - 6, monthSeq.length()));
            //十进制转36进制
            reelId = reelId + month + StringUtil.leftPad(tenTo36(cobMonthSeq) , 6 , "0");

            parameterMap.put("PARTNUM", erpSo.getOther16());
            parameterMap.put("MATERIALDESC", materialDesc);
            parameterMap.put("DATECODE", date);
            parameterMap.put("LOTNO", seq);
            parameterMap.put("QUANTITY", materialLot.getCurrentQty().toString());
            parameterMap.put("REELID", reelId);
            parameterMap.put("CODE", "P" + erpSo.getOther16() + StringUtils.SEMICOLON_CODE + "D" + date + StringUtils.SEMICOLON_CODE + "L" + seq + StringUtils.SEMICOLON_CODE
                    + "VI50111" + "Q" + materialLot.getCurrentQty().toString()+ StringUtils.SEMICOLON_CODE + "R" + reelId + StringUtils.SEMICOLON_CODE + "U000000");
            parameterMap.put("portId", MLotCodePrint.COB_GUANGBAO_PORTID);
            parameterMap.put("printCount", "2");
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private List<NBOwnerReferenceList> getProductDecsList() throws ClientException {
        List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(REFERENCE_NAME_PRODUCT_DECS_LIST, NBReferenceList.CATEGORY_OWNER);
        if (CollectionUtils.isNotEmpty(nbReferenceList)) {
            return nbReferenceList;
        }
        return Lists.newArrayList();
    }

    /**
     * 光宝真空包打印参数
     * @param erpSo
     * @param materialLot
     * @return
     * @throws ClientException
     */
    private Map<String,String> getGuangBaoVboxMLotPrintParamater(ErpSo erpSo, MaterialLot materialLot) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            String vboxSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_GUANGBAO_VBOX_LABEL_PRINT_SEQ_RULE).substring(2, 13);
            String vboxMonthSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_GUANGBAO_VBOX_LABEL_PRINT_MONTH_SEQ_RULE).substring(2, 14);
            SimpleDateFormat formatter = new SimpleDateFormat(MLotCodePrint.PRINT_DATE_PATTERN);
            String date = formatter.format(new Date());
            String reelId = "I50111" + date.substring(2,4);
            String month = Integer.toHexString(Integer.parseInt(date.substring(4,6)));
            Integer monthSeq = Integer.parseInt(vboxMonthSeq.substring(vboxMonthSeq.length() - 6, vboxMonthSeq.length()));
            //十进制转36进制
            reelId = reelId + month + StringUtil.leftPad(tenTo36(monthSeq) , 6 , "0");

            parameterMap.put("PARTNUM", erpSo.getOther16());
            parameterMap.put("DATECODE", date);
            parameterMap.put("LOTNO", vboxSeq);
            parameterMap.put("QUANTITY", materialLot.getCurrentQty().toString());
            parameterMap.put("REELID", reelId);
            parameterMap.put("WAFERID", materialLot.getMaterialLotId());
            parameterMap.put("CODE", "P" + erpSo.getOther16() + StringUtils.SEMICOLON_CODE + "D" + date + StringUtils.SEMICOLON_CODE + "L" + vboxSeq + StringUtils.SEMICOLON_CODE
                    + "VI50111" + "Q" + materialLot.getCurrentQty().toString()+ StringUtils.SEMICOLON_CODE + "R" + reelId + StringUtils.SEMICOLON_CODE + "U000000");
            parameterMap.put("portId", MLotCodePrint.GUANGBAO_VBOX_PORTID);
            parameterMap.put("printCount", "1");
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *  10进制转36进制
     * @param num
     * @return
     */
    public static String tenTo36(int num) {
        StringBuffer sBuffer = new StringBuffer();
        String[] X36_ARRAY = "0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z".split(",");
        if(num == 0) {
            sBuffer.append("0");
        }
        while(num > 0) {
            sBuffer.append(X36_ARRAY[num % 36]);
            num = num / 36;
        }
        return sBuffer.reverse().toString();
    }

    /**
     * 欧菲光物料标签
     * @param erpSo
     * @param materialLot
     * @param startDate
     * @param date
     * @param printSeq
     * @return
     * @throws ClientException
     */
    private Map<String,String> getOphelionMLotPrintParamater(ErpSo erpSo, MaterialLot materialLot, String startDate, String date,String endDate,String effectiveDate,String expirationDate, String printSeq) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            parameterMap.put("SUPPLIERCODE", MLotCodePrint.SUPPLIER_CODE);
            parameterMap.put("ORDERID", erpSo.getCcode());
            parameterMap.put("MATERIALCODE", erpSo.getOther16());
            parameterMap.put("CURRENTQTY", materialLot.getCurrentQty().toString());
            parameterMap.put("MLOTID", materialLot.getMaterialLotId());
            parameterMap.put("STARTDATE", startDate);
            parameterMap.put("ENDDATE", endDate);
            parameterMap.put("PRINTDATE", date);
            parameterMap.put("QC", MLotCodePrint.QC);
            if(expirationDate.endsWith("0229")){
                expirationDate = expirationDate.substring(0,2) + "0228";
            }
            String code = MLotCodePrint.SUPPLIER_CODE + "|"  + erpSo.getOther16() + "|" + materialLot.getMaterialLotId() + "|"
                    + materialLot.getCurrentSubQty().toString() + "|"  + effectiveDate + "|" + expirationDate + "|" + printSeq;
            parameterMap.put("CODE", code);
            parameterMap.put("portId", MLotCodePrint.OPHELION_MLOT_PORTID);
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * 一般物料标签/光宝箱标签 参数
     * @param erpSo
     * @param materialLot
     * @param warehouse
     * @param date
     * @param productType
     * @return
     * @throws ClientException
     */
    private Map<String,String> getGeneralMLotPrintParamater(ErpSo erpSo, MaterialLot materialLot,Warehouse warehouse, String date, String productType) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            parameterMap.put("CUSTOMER", erpSo.getCusname());
            parameterMap.put("MLOTCODE", erpSo.getOther16());
            if(warehouse.getName().equals(WAREHOUSE_HK)){
                parameterMap.put("SUPPLIER", MLotCodePrint.HK_SUPPLIER);
            } else {
                parameterMap.put("SUPPLIER", MLotCodePrint.SH_SUPPLIER);
            }
            parameterMap.put("CURRENTQTY", materialLot.getCurrentQty().toString());
            parameterMap.put("ORDERID", erpSo.getCcode());
            parameterMap.put("OUTDATE", date);
            parameterMap.put("DELIVERYPLACE", MLotCodePrint.DELIVERY_PLACE);
            parameterMap.put("PRODUCTTYPE", productType);
            parameterMap.put("MLOTID", materialLot.getMaterialLotId());
            parameterMap.put("portId", MLotCodePrint.GENERAL_MLOT_PORTID);

            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 百辰物料标签参数
     * @param erpSo
     * @param materialLot
     * @param vboxSeq
     * @return
     * @throws ClientException
     */
    private Map<String, String> getBaiChenMLotPrintParamater(ErpSo erpSo, MaterialLot materialLot, String vboxSeq,String ponoPrefix) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            SimpleDateFormat formatter = new SimpleDateFormat(MLotCodePrint.DATE_PATTERN);
            parameterMap.put("MATERIALCODE", erpSo.getOther16());
            parameterMap.put("SHIPCODE", MLotCodePrint.SHIP_CODE);
            parameterMap.put("DATEDAY", formatter.format(new Date()));
            parameterMap.put("SERIALCODE", vboxSeq);
            parameterMap.put("TWODCODE1", erpSo.getOther16() + MLotCodePrint.SHIP_CODE + formatter.format(new Date()) + vboxSeq);

            String packageQty = StringUtil.leftPad(materialLot.getCurrentQty().toString() , 8 , "0");
            parameterMap.put("PONOPREFIX", ponoPrefix);
            parameterMap.put("PONO", erpSo.getOther10());
            parameterMap.put("PACKAGEQTY", packageQty);
            parameterMap.put("TWODCODE2", ponoPrefix + erpSo.getOther10() + packageQty);

            parameterMap.put("MEMO", erpSo.getOther16());
            parameterMap.put("TWODCODE3", erpSo.getOther16());

            formatter = new SimpleDateFormat(MaterialLot.PRINT_DATE_PATTERN);
            parameterMap.put("DATEMONTH", formatter.format(new Date()));
            parameterMap.put("TWODCODE4", formatter.format(new Date()));
            parameterMap.put("BOXID", materialLot.getMaterialLotId());
            parameterMap.put("portId", MLotCodePrint.BAICHEN_MLOT_PORTID);
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 通过备货单据rrn获取单据信息
     * @param reserved16
     * @return
     */
    private ErpSo getErpSoByReserved16(String reserved16) throws ClientException{
        try {
            long documentLineRrn = Long.parseLong(reserved16);
            DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLineRrn);
            Long seq = Long.parseLong(documentLine.getReserved1());
            ErpSo erpSo = erpSoRepository.findBySeq(seq);
            return erpSo;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * 同步其他发料单
     * @throws ClientException
     */
    public void asyncOtherIssueOrder() throws ClientException {
        try {
            List<ErpMaterialOutaOrder> otherIssueOrders = erpMaterialOutAOrderRepository.findByTypeAndSynStatusNotIn(ErpMaterialOutaOrder.TYPE_TV, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(otherIssueOrders)) {
                Map<String, List<ErpMaterialOutaOrder>> documentIdMap = otherIssueOrders.stream().collect(Collectors.groupingBy(ErpMaterialOutaOrder :: getCcode));
                for (String documentId : documentIdMap.keySet()) {
                    List<ErpMaterialOutaOrder> documentIdList = documentIdMap.get(documentId);
                    //把即将同步的同Ccode数据按createseq分组
                    Map<String, List<ErpMaterialOutaOrder>> sameCreateSeqOrder = documentIdList.stream().filter(erpMaterialOutaOrder -> !StringUtils.isNullOrEmpty(erpMaterialOutaOrder.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpMaterialOutaOrder :: getCreateSeq));
                    //由于取消值为WaferIssueA的CATEGORY，所以用WaferIssueOrder替代OtherIssueOrder
                    List<WaferIssueOrder> otherIssueOrderList = waferIssueOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(otherIssueOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            updateErpMaterialOutAErrorInfo(documentIdList);
                            continue;
                        }
                    }
                    WaferIssueOrder otherIssueOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(otherIssueOrderList)) {
                        //如果有不同create_seq
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpMaterialOutaOrder erpMaterialOutaOrder : documentIdList) {
                                asyncDuplicateSeqList.add(erpMaterialOutaOrder.getSeq());
                            }
                            continue;
                        }
                        otherIssueOrder = new WaferIssueOrder();
                        otherIssueOrder.setName(documentId);
                        otherIssueOrder.setStatus(Document.STATUS_OPEN);
                        otherIssueOrder.setReserved31(ErpMaterialOutaOrder.SOURCE_TABLE_NAME);
                    } else {
                        otherIssueOrder = otherIssueOrderList.get(0);
                        totalQty = otherIssueOrder.getQty();
                        boolean differentCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(otherIssueOrder.getReserved32())){
                                differentCreateSeq = true;
                                for  (ErpMaterialOutaOrder erpMaterialOutaOrder : documentIdList) {
                                    asyncDuplicateSeqList.add(erpMaterialOutaOrder.getSeq());
                                }
                                break;
                            }
                        }
                        if(differentCreateSeq){
                            continue;
                        }
                    }
                    otherIssueOrder.setName(documentId);

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpMaterialOutaOrder erpMaterialOutaOrder : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (otherIssueOrder.getObjectRrn() != null) {
                                validationIssueOrderQty(otherIssueOrder.getObjectRrn(), erpMaterialOutaOrder);
                            }
                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            if (documentLine == null) {
                                documentLine = new DocumentLine();
                                setIssueOrderInfoToDocumentLine(documentLine, erpMaterialOutaOrder);
                                documentLine.setDocId(documentId);
                                documentLine.setReserved9(ErpMaterialOutaOrder.CATEGORY_WAFER_ISSUEA);
                            }

                            totalQty = totalQty.add(erpMaterialOutaOrder.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpMaterialOutaOrder.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLines.add(documentLine);

                            otherIssueOrder.setOwner(erpMaterialOutaOrder.getChandler());
                            otherIssueOrder.setReserved32(erpMaterialOutaOrder.getCreateSeq());
                            asyncSuccessSeqList.add(erpMaterialOutaOrder.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpMaterialOutaOrder.setUserId(Document.SYNC_USER_ID);
                            erpMaterialOutaOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpMaterialOutaOrder.setErrorMemo(e.getMessage());
                            erpMaterialOutAOrderRepository.save(erpMaterialOutaOrder);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        otherIssueOrder.setQty(totalQty);
                        otherIssueOrder.setUnHandledQty(otherIssueOrder.getQty().subtract(otherIssueOrder.getHandledQty()));

                        otherIssueOrder.setReserved31(ErpMaterialOutaOrder.SOURCE_TABLE_NAME);
                        otherIssueOrder = (WaferIssueOrder) baseService.saveEntity(otherIssueOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(otherIssueOrder);
                        baseService.saveEntity(documentLine);
                    }
                }

                updateErpMaterialOutASyncStatusAndErrorMemoAndUserId(asyncSuccessSeqList, asyncDuplicateSeqList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 发料单信息整合
     * @param documentLine
     * @param erpMaterialOutaOrder
     * @throws ClientException
     */
    private void setIssueOrderInfoToDocumentLine(DocumentLine documentLine, ErpMaterialOutaOrder erpMaterialOutaOrder) throws ClientException{
        try {
            Material material = mmsService.getRawMaterialByName(erpMaterialOutaOrder.getCinvcode());
            if (material == null) {
                throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, erpMaterialOutaOrder.getCinvcode());
            }
            documentLine.setErpCreated(DateUtils.parseDate(erpMaterialOutaOrder.getDdate()));
            documentLine.setMaterialRrn(material.getObjectRrn());
            documentLine.setMaterialName(material.getName());
            documentLine.setReserved1(String.valueOf(erpMaterialOutaOrder.getSeq()));
            documentLine.setReserved2(erpMaterialOutaOrder.getSecondcode());
            documentLine.setReserved3(erpMaterialOutaOrder.getGrade());
            documentLine.setReserved5(erpMaterialOutaOrder.getCmaker());
            documentLine.setReserved6(erpMaterialOutaOrder.getChandler());
            documentLine.setReserved7(erpMaterialOutaOrder.getOther1());
            documentLine.setReserved31(ErpMaterialOutaOrder.SOURCE_TABLE_NAME);
        } catch (Exception e) {
            throw  ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证发料单的数量是否正确
     * @param issueOrderRrn
     * @param erpMaterialOutaOrder
     */
    private void validationIssueOrderQty(Long issueOrderRrn, ErpMaterialOutaOrder erpMaterialOutaOrder) throws ClientException{
        try {
            DocumentLine documentLine = documentLineRepository.findByDocRrnAndReserved1(issueOrderRrn, String.valueOf(erpMaterialOutaOrder.getSeq()));
            if (documentLine != null) {
                if (ErpMaterialOutaOrder.SYNC_STATUS_CHANGED.equals(erpMaterialOutaOrder.getSynStatus())) {
                    if (documentLine != null && documentLine.getHandledQty().compareTo(erpMaterialOutaOrder.getIquantity()) > 0) {
                        throw new ClientException("gc.order_handled_qty_gt_qty");
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改同步成功之后的状态
     * @param asyncSuccessSeqList
     * @param asyncDuplicateSeqList
     */
    private void updateErpMaterialOutASyncStatusAndErrorMemoAndUserId(List<Long> asyncSuccessSeqList, List<Long> asyncDuplicateSeqList) throws ClientException{
        try {
            if (CollectionUtils.isNotEmpty(asyncSuccessSeqList)) {
                if(asyncSuccessSeqList.size() >= Document.SEQ_MAX_LENGTH){
                    List<List<Long>> asyncSuccessSeqGroupList = getSeqListGroupByCount(asyncSuccessSeqList);
                    for(List seqGroup : asyncSuccessSeqGroupList){
                        erpMaterialOutAOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpMaterialOutaOrder.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, seqGroup);
                    }
                } else {
                    erpMaterialOutAOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpMaterialOutaOrder.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, asyncSuccessSeqList);
                }
            }

            if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                if(asyncDuplicateSeqList.size() >= Document.SEQ_MAX_LENGTH){
                    List<List<Long>> asyncDuplicateSeqGroupList = getSeqListGroupByCount(asyncDuplicateSeqList);
                    for(List seqGroup : asyncDuplicateSeqGroupList){
                        erpMaterialOutAOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpMaterialOutaOrder.SYNC_STATUS_SYNC_ERROR, ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, seqGroup);
                    }
                } else {
                    erpMaterialOutAOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpMaterialOutaOrder.SYNC_STATUS_SYNC_ERROR, ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, asyncDuplicateSeqList);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步原材料发料单
     * @throws ClientException
     */
    public void asyncMaterialIssueOrder() throws ClientException {
        try {
            List<ErpMaterialOutaOrder> materialIssueOrders = erpMaterialOutAOrderRepository.findByTypeAndSynStatusNotIn(ErpMaterialOutaOrder.TYPE_MV, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(materialIssueOrders)) {
                Map<String, List<ErpMaterialOutaOrder>> documentIdMap = materialIssueOrders.stream().collect(Collectors.groupingBy(ErpMaterialOutaOrder :: getCcode));
                for (String documentId : documentIdMap.keySet()) {
                    List<ErpMaterialOutaOrder> documentIdList = documentIdMap.get(documentId);
                    Map<String, List<ErpMaterialOutaOrder>> sameCreateSeqOrder = documentIdList.stream().filter(erpMaterialOutaOrder -> !StringUtils.isNullOrEmpty(erpMaterialOutaOrder.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpMaterialOutaOrder :: getCreateSeq));
                    List<MaterialIssueOrder> materialIssueOrderList = materialIssueOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(materialIssueOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            updateErpMaterialOutAErrorInfo(documentIdList);
                            continue;
                        }
                    }
                    MaterialIssueOrder materialIssueOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(materialIssueOrderList)) {
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpMaterialOutaOrder erpMOutaOrder : documentIdList) {
                                asyncDuplicateSeqList.add(erpMOutaOrder.getSeq());
                            }
                            continue;
                        }
                        materialIssueOrder = new MaterialIssueOrder();
                        materialIssueOrder.setName(documentId);
                        materialIssueOrder.setStatus(Document.STATUS_OPEN);
                        materialIssueOrder.setReserved31(ErpMaterialOutaOrder.SOURCE_TABLE_NAME);
                    } else {
                        materialIssueOrder = materialIssueOrderList.get(0);
                        totalQty = materialIssueOrder.getQty();
                        boolean createSeqFlag = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(materialIssueOrder.getReserved32())){
                                createSeqFlag = true;
                                for  (ErpMaterialOutaOrder erpMaterialOutaOrder : documentIdList) {
                                    asyncDuplicateSeqList.add(erpMaterialOutaOrder.getSeq());
                                }
                                break;
                            }
                        }
                        if(createSeqFlag){
                            continue;
                        }
                    }
                    materialIssueOrder.setName(documentId);

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpMaterialOutaOrder erpMaterialOutaOrder : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (materialIssueOrder.getObjectRrn() != null) {
                                validationIssueOrderQty(materialIssueOrder.getObjectRrn(), erpMaterialOutaOrder);
                            }

                            if (documentLine == null) {
                                documentLine = new DocumentLine();
                                setIssueOrderInfoToDocumentLine(documentLine, erpMaterialOutaOrder);
                                documentLine.setReserved9(MaterialIssueOrder.CATEGORY_WAFER_ISSUE);
                                documentLine.setDocId(documentId);
                            }

                            totalQty = totalQty.add(erpMaterialOutaOrder.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpMaterialOutaOrder.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLines.add(documentLine);

                            materialIssueOrder.setOwner(erpMaterialOutaOrder.getChandler());
                            materialIssueOrder.setReserved32(erpMaterialOutaOrder.getCreateSeq());
                            asyncSuccessSeqList.add(erpMaterialOutaOrder.getSeq());
                        } catch (Exception e) {
                            erpMaterialOutaOrder.setUserId(Document.SYNC_USER_ID);
                            erpMaterialOutaOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpMaterialOutaOrder.setErrorMemo(e.getMessage());
                            erpMaterialOutAOrderRepository.save(erpMaterialOutaOrder);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        materialIssueOrder.setQty(totalQty);
                        materialIssueOrder.setUnHandledQty(materialIssueOrder.getQty().subtract(materialIssueOrder.getHandledQty()));

                        materialIssueOrder.setReserved31(ErpMaterialOutaOrder.SOURCE_TABLE_NAME);
                        materialIssueOrder = (MaterialIssueOrder) baseService.saveEntity(materialIssueOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(materialIssueOrder);
                        baseService.saveEntity(documentLine);
                    }
                }
                updateErpMaterialOutASyncStatusAndErrorMemoAndUserId(asyncSuccessSeqList, asyncDuplicateSeqList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步失败，保存同步错误信息
     * @param documentIdList
     * @throws ClientException
     */
    private void updateErpMaterialOutAErrorInfo(List<ErpMaterialOutaOrder> documentIdList) throws ClientException{
        try {
            for(ErpMaterialOutaOrder erpMOutaOrder : documentIdList){
                erpMOutaOrder.setUserId(Document.SYNC_USER_ID);
                erpMOutaOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                erpMOutaOrder.setErrorMemo(ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID);
                erpMaterialOutAOrderRepository.save(erpMOutaOrder);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步其他出货单
     * @throws ClientException
     */
    public void asyncOtherStockOutOrder() throws ClientException {
        try {
            List<ErpSoa> erpSos = erpSoaOrderRepository.findBySynStatusNotIn(Lists.newArrayList(ErpSoa.SYNC_STATUS_OPERATION, ErpSoa.SYNC_STATUS_SYNC_SUCCESS, ErpSoa.SYNC_STATUS_MERGE));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(erpSos)) {
                Map<String, List<ErpSoa>> documentIdMap = erpSos.stream().collect(Collectors.groupingBy(ErpSoa :: getSocode));

                for (String documentId : documentIdMap.keySet()) {
                    List<ErpSoa> documentIdList = documentIdMap.get(documentId);
                    //把即将同步的同Ccode数据按createseq分组
                    Map<String, List<ErpSoa>> sameCreateSeqOrder = documentIdList.stream().filter(erpSoa -> !StringUtils.isNullOrEmpty(erpSoa.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpSoa :: getCreateSeq));
                    List<OtherStockOutOrder> otherStockOutOrderList = otherStockOutOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(otherStockOutOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            for(ErpSoa erpSoa : documentIdList){
                                erpSoa.setUserId(Document.SYNC_USER_ID);
                                erpSoa.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                                erpSoa.setErrorMemo(ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID);
                                erpSoaOrderRepository.save(erpSoa);
                            }
                            continue;
                        }
                    }
                    OtherStockOutOrder otherStockOutOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(otherStockOutOrderList)) {
                        //如果有不同create_seq
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpSoa erpSoa : documentIdList) {
                                asyncDuplicateSeqList.add(erpSoa.getSeq());
                            }
                            continue;
                        }
                        otherStockOutOrder = new OtherStockOutOrder();
                        otherStockOutOrder.setName(documentId);
                        otherStockOutOrder.setStatus(Document.STATUS_OPEN);
                        otherStockOutOrder.setReserved31(ErpSoa.SOURCE_TABLE_NAME);
                    } else {
                        otherStockOutOrder = otherStockOutOrderList.get(0);
                        totalQty = otherStockOutOrder.getQty();
                        boolean differentCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(otherStockOutOrder.getReserved32())){
                                differentCreateSeq = true;
                                for  (ErpSoa erpSoa : documentIdList) {
                                    asyncDuplicateSeqList.add(erpSoa.getSeq());
                                }
                                break;
                            }
                        }
                        if(differentCreateSeq){
                            continue;
                        }
                    }

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpSoa erpSoa : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (otherStockOutOrder.getObjectRrn() != null) {
                                documentLine = documentLineRepository.findByDocRrnAndReserved1(otherStockOutOrder.getObjectRrn(), String.valueOf(erpSoa.getSeq()));
                                if (documentLine != null) {
                                    if (ErpSoa.SYNC_STATUS_CHANGED.equals(erpSoa.getSynStatus())) {
                                        if (documentLine != null && documentLine.getHandledQty().compareTo(erpSoa.getQuantity()) > 0) {
                                            throw new ClientException("gc.order_handled_qty_gt_qty");
                                        }
                                    }
                                }
                            }

                            Date erpCreatedDate = DateUtils.parseDate(erpSoa.getOrderDate());
                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            if (documentLine == null) {
                                documentLine = new DocumentLine();
                                Material material = mmsService.getProductByName(erpSoa.getInvcode());
                                if (material == null) {
                                    throw new ClientParameterException(MM_PRODUCT_ID_IS_NOT_EXIST, erpSoa.getInvcode());
                                }
                                documentLine.setDocId(documentId);
                                documentLine.setErpCreated(erpCreatedDate);
                                documentLine.setMaterialRrn(material.getObjectRrn());
                                documentLine.setMaterialName(material.getName());

                                documentLine.setReserved1(String.valueOf(erpSoa.getSeq()));
                                documentLine.setReserved2(erpSoa.getBatch());
                                documentLine.setReserved3(erpSoa.getGrade());
                                documentLine.setReserved4(erpSoa.getFree3());
                                documentLine.setReserved5(erpSoa.getShipMaker());
                                documentLine.setReserved6(erpSoa.getShipVerifier());
                                documentLine.setReserved7(erpSoa.getOther1());

                                documentLine.setReserved8(erpSoa.getCusname());
                                documentLine.setReserved9(OtherStockOutOrder.CATEGORY_DELIVERYA);

                                documentLine.setReserved10(erpSoa.getOther16());
                                documentLine.setReserved11(erpSoa.getOther17());
                                documentLine.setReserved12(erpSoa.getCusabbName());
                                documentLine.setReserved13(erpSoa.getMemo());
                                documentLine.setReserved14(erpSoa.getItemcode());
                                documentLine.setReserved15(erpSoa.getShipAddress());
                                documentLine.setReserved17(erpSoa.getOther13());
                                documentLine.setReserved18(erpSoa.getOther6());
                                documentLine.setReserved19(erpSoa.getOther8());
                                documentLine.setReserved20(erpSoa.getCusperson());
                                documentLine.setReserved21(erpSoa.getCusphone());
                                documentLine.setReserved22(erpSoa.getOther11());
                                documentLine.setReserved23(erpSoa.getOther12());
                                documentLine.setReserved24(erpSoa.getOther13());
                                documentLine.setReserved27(erpSoa.getOdm());
                                documentLine.setReserved28(erpSoa.getOther19());
                                documentLine.setReserved29(erpSoa.getOther18());
                                documentLine.setReserved31(ErpSoa.SOURCE_TABLE_NAME);
                                documentLine.setThreeSideTransaction(erpSoa.getCuscode());
                            }

                            totalQty = totalQty.add(erpSoa.getQuantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpSoa.getQuantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLine.setUnReservedQty(documentLine.getQty().subtract(documentLine.getReservedQty()));
                            documentLines.add(documentLine);

                            // 同一个单据下，所有的客户都是一样的。
                            otherStockOutOrder.setSupplierName(erpSoa.getCusname());
                            otherStockOutOrder.setOwner(erpSoa.getShipVerifier());
                            otherStockOutOrder.setReserved32(erpSoa.getCreateSeq());
                            if (otherStockOutOrder.getErpCreated() == null) {
                                otherStockOutOrder.setErpCreated(erpCreatedDate);
                            } else {
                                if (otherStockOutOrder.getErpCreated().after(erpCreatedDate)) {
                                    otherStockOutOrder.setErpCreated(erpCreatedDate);
                                }
                            }
                            asyncSuccessSeqList.add(erpSoa.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpSoa.setUserId(Document.SYNC_USER_ID);
                            erpSoa.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpSoa.setErrorMemo(e.getMessage());
                            erpSoaOrderRepository.save(erpSoa);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        otherStockOutOrder.setQty(totalQty);
                        otherStockOutOrder.setUnHandledQty(otherStockOutOrder.getQty().subtract(otherStockOutOrder.getHandledQty()));
                        otherStockOutOrder.setUnReservedQty(otherStockOutOrder.getQty().subtract(otherStockOutOrder.getReservedQty()));
                        otherStockOutOrder.setReserved31(ErpSoa.SOURCE_TABLE_NAME);
                        otherStockOutOrder = (OtherStockOutOrder) baseService.saveEntity(otherStockOutOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(otherStockOutOrder);
                        baseService.saveEntity(documentLine);
                    }

                    // 保存单据的时候同步下客户
                    if (!StringUtils.isNullOrEmpty(otherStockOutOrder.getSupplierName())) {
                        savaCustomer(otherStockOutOrder.getSupplierName());
                    }
                }

                if (CollectionUtils.isNotEmpty(asyncSuccessSeqList)) {
                    if(asyncSuccessSeqList.size() >= Document.SEQ_MAX_LENGTH){
                        List<List<Long>> asyncSuccessSeqGroupList = getSeqListGroupByCount(asyncSuccessSeqList);
                        for(List seqGroup : asyncSuccessSeqGroupList){
                            erpSoaOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSoa.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, seqGroup);
                        }
                    } else {
                        erpSoaOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSoa.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, asyncSuccessSeqList);
                    }
                }
                if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                    if(asyncDuplicateSeqList.size() >= Document.SEQ_MAX_LENGTH){
                        List<List<Long>> asyncDuplicateSeqGroupList = getSeqListGroupByCount(asyncDuplicateSeqList);
                        for(List seqGroup : asyncDuplicateSeqGroupList){
                            erpSoaOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSoa.SYNC_STATUS_SYNC_ERROR, ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, seqGroup);                        }
                    } else {
                        erpSoaOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSoa.SYNC_STATUS_SYNC_ERROR, ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, asyncDuplicateSeqList);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void savaCustomer(String cuetomerName) throws ClientException{
        try {
            Customer customer = customerRepository.getByName(cuetomerName);
            if (customer == null) {
                customer = new Customer();
                customer.setName(cuetomerName);
                customer.setDescription(cuetomerName);
                customerRepository.save(customer);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步其他发货单
     * @throws ClientException
     */
    public void asyncOtherShipOrder() throws ClientException {
        try {
            List<ErpSob> erpSobs = erpSobOrderRepository.findBySynStatusNotIn(Lists.newArrayList(ErpSob.SYNC_STATUS_OPERATION, ErpSob.SYNC_STATUS_SYNC_SUCCESS, ErpSob.SYNC_STATUS_MERGE));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(erpSobs)) {
                Map<String, List<ErpSob>> documentIdMap = erpSobs.stream().collect(Collectors.groupingBy(ErpSob :: getCcode));

                for (String documentId : documentIdMap.keySet()) {
                    List<ErpSob> documentIdList = documentIdMap.get(documentId);
                    //把即将同步的同Ccode数据按createseq分组
                    Map<String, List<ErpSob>> sameCreateSeqOrder = documentIdList.stream().filter(erpSob -> !StringUtils.isNullOrEmpty(erpSob.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpSob :: getCreateSeq));
                    List<OtherShipOrder> otherShipOrderList = otherShipOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(otherShipOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            for(ErpSob erpSob : documentIdList){
                                erpSob.setUserId(Document.SYNC_USER_ID);
                                erpSob.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                                erpSob.setErrorMemo(ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID);
                                erpSobOrderRepository.save(erpSob);
                            }
                            continue;
                        }
                    }
                    OtherShipOrder otherShipOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(otherShipOrderList)) {
                        //如果有不同create_seq
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpSob erpSob : documentIdList) {
                                asyncDuplicateSeqList.add(erpSob.getSeq());
                            }
                            continue;
                        }
                        otherShipOrder = new OtherShipOrder();
                        otherShipOrder.setName(documentId);
                        otherShipOrder.setStatus(Document.STATUS_OPEN);
                        otherShipOrder.setReserved31(ErpSob.SOURCE_TABLE_NAME);
                    } else {
                        otherShipOrder = otherShipOrderList.get(0);
                        totalQty = otherShipOrder.getQty();
                        boolean differentCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(otherShipOrder.getReserved32())){
                                differentCreateSeq = true;
                                for  (ErpSob erpSob : documentIdList) {
                                    asyncDuplicateSeqList.add(erpSob.getSeq());
                                }
                                break;
                            }
                        }
                        if(differentCreateSeq){
                            continue;
                        }
                    }

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpSob erpSob : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (otherShipOrder.getObjectRrn() != null) {
                                documentLine = documentLineRepository.findByDocRrnAndReserved1(otherShipOrder.getObjectRrn(), String.valueOf(erpSob.getSeq()));
                                if (documentLine != null) {
                                    if (ErpSo.SYNC_STATUS_CHANGED.equals(erpSob.getSynStatus())) {
                                        if (documentLine != null && documentLine.getHandledQty().compareTo(erpSob.getIquantity()) > 0) {
                                            throw new ClientException("gc.order_handled_qty_gt_qty");
                                        }
                                    }
                                }
                            }

                            Date erpCreatedDate = DateUtils.parseDate(erpSob.getDdate());
                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            if (documentLine == null) {
                                documentLine = new DocumentLine();
                                Material material = mmsService.getProductByName(erpSob.getCinvcode());
                                if (material == null) {
                                    material = saveProductAndSetStatusModelRrn(erpSob.getCinvcode());
                                }
                                documentLine.setDocId(documentId);
                                documentLine.setErpCreated(erpCreatedDate);
                                documentLine.setMaterialRrn(material.getObjectRrn());
                                documentLine.setMaterialName(material.getName());

                                documentLine.setReserved1(String.valueOf(erpSob.getSeq()));
                                documentLine.setReserved2(erpSob.getSecondcode());
                                documentLine.setReserved3(erpSob.getGrade());
                                documentLine.setReserved4(erpSob.getCfree3());
                                documentLine.setReserved5(erpSob.getCmaker());
                                documentLine.setReserved6(erpSob.getChandler());
                                documentLine.setReserved7(erpSob.getOther1());
                                documentLine.setReserved8(erpSob.getOther9());

                                documentLine.setReserved9(OtherShipOrder.CATEGORY_DELIVERYB);
                                documentLine.setReserved16(erpSob.getOther2());
                                documentLine.setReserved19(erpSob.getOther8());
                                documentLine.setReserved20(erpSob.getOther9());
                                documentLine.setReserved21(erpSob.getOther10());
                                documentLine.setReserved22(erpSob.getOther11());
                                documentLine.setReserved23(erpSob.getOther12());
                                documentLine.setReserved24(erpSob.getOther13());
                                documentLine.setReserved25(erpSob.getOther14());
                                documentLine.setReserved26(erpSob.getOther15());
                                documentLine.setReserved30(erpSob.getOther5());

                                documentLine.setDocType(erpSob.getCvouchtype());
                                documentLine.setDocName(erpSob.getCvouchname());
                                documentLine.setDocBusType(erpSob.getCbustype());
                                documentLine.setDocSource(erpSob.getCsource());
                                documentLine.setWarehouseCode(erpSob.getCwhcode());
                                documentLine.setWarehouseName(erpSob.getCwhname());
                                documentLine.setReserved31(ErpSob.SOURCE_TABLE_NAME);
                            }

                            totalQty = totalQty.add(erpSob.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpSob.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLine.setUnReservedQty(documentLine.getQty().subtract(documentLine.getReservedQty()));
                            documentLines.add(documentLine);

                            // 同一个单据下，所有的客户都是一样的。
                            otherShipOrder.setSupplierName(erpSob.getOther9());
                            otherShipOrder.setOwner(erpSob.getChandler());
                            otherShipOrder.setReserved32(erpSob.getCreateSeq());
                            if (otherShipOrder.getErpCreated() == null) {
                                otherShipOrder.setErpCreated(erpCreatedDate);
                            } else {
                                if (otherShipOrder.getErpCreated().after(erpCreatedDate)) {
                                    otherShipOrder.setErpCreated(erpCreatedDate);
                                }
                            }
                            asyncSuccessSeqList.add(erpSob.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpSob.setUserId(Document.SYNC_USER_ID);
                            erpSob.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpSob.setErrorMemo(e.getMessage());
                            erpSobOrderRepository.save(erpSob);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        otherShipOrder.setQty(totalQty);
                        otherShipOrder.setUnHandledQty(otherShipOrder.getQty().subtract(otherShipOrder.getHandledQty()));
                        otherShipOrder.setUnReservedQty(otherShipOrder.getQty().subtract(otherShipOrder.getReservedQty()));
                        otherShipOrder = (OtherShipOrder) baseService.saveEntity(otherShipOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(otherShipOrder);
                        baseService.saveEntity(documentLine);
                    }

                    // 保存单据的时候同步下客户
                    if (!StringUtils.isNullOrEmpty(otherShipOrder.getSupplierName())) {
                        savaCustomer(otherShipOrder.getSupplierName());
                    }
                }

                if (CollectionUtils.isNotEmpty(asyncSuccessSeqList)) {
                    if(asyncSuccessSeqList.size() >= Document.SEQ_MAX_LENGTH){
                        List<List<Long>> asyncSuccessSeqGroupList = getSeqListGroupByCount(asyncSuccessSeqList);
                        for(List seqGroup : asyncSuccessSeqGroupList){
                            erpSobOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, seqGroup);
                        }
                    } else {
                        erpSobOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, asyncSuccessSeqList);
                    }
                }
                if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                    if(asyncDuplicateSeqList.size() >= Document.SEQ_MAX_LENGTH){
                        List<List<Long>> asyncDuplicateSeqGroupList = getSeqListGroupByCount(asyncDuplicateSeqList);
                        for(List seqGroup : asyncDuplicateSeqGroupList){
                            erpSobOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_ERROR, ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, seqGroup);                        }
                    } else {
                        erpSobOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_ERROR, ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, asyncDuplicateSeqList);
                    }

                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 按条件查询需要取消备货的数据并带出
     * @param tableRrn
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getMaterialLotAndDocUserToUnReserved(Long tableRrn, String whereClause) throws ClientException {
        try {
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String _whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();

            // 没传递查询条件 则默认使用InitWhereClause进行查询
            if (StringUtils.isNullOrEmpty(_whereClause)) {
                _whereClause = nbTable.getInitWhereClause();
            } else {
                if (!StringUtils.isNullOrEmpty(nbTable.getWhereClause())) {
                    StringBuffer clauseBuffer = new StringBuffer(_whereClause);
                    if(!StringUtils.isNullOrEmpty(whereClause)){
                        clauseBuffer.append(" AND ");
                        clauseBuffer.append(whereClause);
                    }
                    _whereClause = clauseBuffer.toString();
                }
            }

            List<MaterialLot> materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);

            Map<String, List<MaterialLot>> docLineMaterialLotMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot:: getReserved16));

            for(String docLineRrn : docLineMaterialLotMap.keySet()){
                List<MaterialLot> docLineMaterialLot = docLineMaterialLotMap.get(docLineRrn);
                DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(docLineRrn));
                for(MaterialLot materialLot : docLineMaterialLot){
                    materialLot.setDocumentLineUser(documentLine.getReserved8());
                }
            }

            return materialLots;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 验证WLT封装回货模板是否经WLA处理，根据产品型号转换表验证晶圆型号是否需要转换
     * @return
     * @param materialLotUnitList
     * @throws ClientException
     */
    public List<MaterialLotUnit> validateImportWltPackReturn(List<MaterialLotUnit> materialLotUnitList) throws ClientException {
        try {
            Map<String, List<MaterialLotUnit>> materialUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialName));
            for(String materialName : materialUnitMap.keySet()){
                Map<String, List<MaterialLotUnit>> subcodeMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getReserved1));
                for(String subcode : subcodeMap.keySet()){
                    GCProductSubcode gcProductSubcode = getProductAndSubcodeInfo(materialName, subcode);
                    if(gcProductSubcode == null ){
                        throw new ClientParameterException(GcExceptions.PRODUCT_AND_SUBCODE_IS_NOT_EXIST, materialName + StringUtils.SPLIT_CODE + subcode);
                    }
                }
            }
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                String unitId = materialLotUnit.getUnitId();
                String materialName = materialLotUnit.getMaterialName();
                Material material = new Material();
                //验证晶圆是否在经过WLA测试,经过WLA测试的根据产品型号转换做验证
                MesPackedLotRelation mesPackedLotRelation = mesPackedLotRelationRepository.findByWaferId(unitId);
                if(mesPackedLotRelation == null){
                    //验证是否存在产品型号转换，存在即将晶圆型号转换成产品型号转换的型号
                    GCProductModelConversion productModelConversion = gcProductModelConversionRepository.findByProductId(materialName);
                    if(productModelConversion != null){
                        materialLotUnit.setSourceProductId(materialName);
                        materialName = productModelConversion.getConversionModelId();
                        material = mmsService.getRawMaterialByName(materialName);
                        if(material == null){
                            RawMaterial rawMaterial = new RawMaterial();
                            rawMaterial.setName(materialName);
                            material = mmsService.createRawMaterial(rawMaterial);
                        }
                    } else {
                        material = mmsService.getRawMaterialByName(materialName);
                        if(material == null){
                            throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, materialName);
                        }
                    }
                } else {
                    if(Integer.parseInt(mesPackedLotRelation.getBinId1()) < Integer.parseInt(materialLotUnit.getReserved34()) ||
                            Integer.parseInt(mesPackedLotRelation.getBinId2()) < Integer.parseInt(materialLotUnit.getReserved42()) ||
                            Integer.parseInt(mesPackedLotRelation.getBinId4()) < Integer.parseInt(materialLotUnit.getReserved43())){
                        throw new ClientParameterException(GcExceptions.INCOMINGMLOT_QTY_AND_SENTOUT_QTY_DISCREPANCY, materialLotUnit.getUnitId());
                    }
                    materialLotUnit.setSourceProductId(materialName);
                    String testProductId = mesPackedLotRelation.getTestModelId();
                    materialName = testProductId.split("-")[0] + "-3.5";
                    material = mmsService.getRawMaterialByName(materialName);
                    if(material == null){
                        RawMaterial rawMaterial = new RawMaterial();
                        rawMaterial.setName(materialName);
                        mmsService.createRawMaterial(rawMaterial);
                    }
                }
                materialLotUnit.setMaterialName(materialName);
                materialLotUnit.setReserved6(StringUtils.EMPTY);
                materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_WLT);
                materialLotUnit.setReserved49(MaterialLot.IMPORT_WLT);
                materialLotUnit.setReserved50("7");
            }
            materialLotUnitList = materialLotUnitService.createFTMLot(materialLotUnitList);
            return materialLotUnitList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * Wlt/CP验证物料批次的产品、二级代码、保税属性是否一致
     * 不需要验证等级是否一致
     * @param waitValidationMLot 待验证的物料批次
     * @param validatedMLotActions 已经验证过的物料批次
     */
    public boolean validationWltStockOutMaterialLot(MaterialLot waitValidationMLot, List<MaterialLotAction> validatedMLotActions) throws ClientException{
        try {
            boolean flag = true;
            waitValidationMLot = mmsService.getMLotByMLotId(waitValidationMLot.getMaterialLotId(), true);
            if (CollectionUtils.isNotEmpty(validatedMLotActions)) {
                MaterialLot validatedMLot = mmsService.getMLotByMLotId(validatedMLotActions.get(0).getMaterialLotId(), true);
                try {
                    Assert.assertEquals(waitValidationMLot.getMaterialName(),  validatedMLot.getMaterialName());
                } catch (AssertionError e) {
                    flag = false;
                }

                try {
                    Assert.assertEquals(waitValidationMLot.getReserved1(),  validatedMLot.getReserved1());
                } catch (AssertionError e) {
                    flag = false;
                }
                try {
                    Assert.assertEquals(waitValidationMLot.getReserved6(),  validatedMLot.getReserved6());
                } catch (AssertionError e) {
                    flag = false;
                }
            }
            return flag;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证扫描的物料批次基础信息是否一致
     * @param waitValidationMLot 待验证的物料批次
     * @param validatedMLotActions 已经验证过的物料批次
     */
    private boolean validationMaterialLotInfo(MaterialLot waitValidationMLot, List<MaterialLotAction> validatedMLotActions, boolean falg)throws ClientException{
        try {
            if (CollectionUtils.isNotEmpty(validatedMLotActions)) {
                MaterialLot validatedMLot = mmsService.getMLotByMLotId(validatedMLotActions.get(0).getMaterialLotId(), true);
                try {
                    Assert.assertEquals(waitValidationMLot.getMaterialName(), validatedMLot.getMaterialName());
                } catch (AssertionError e) {
                    falg = false;
                }

                try {
                    Assert.assertEquals(waitValidationMLot.getReserved1(), validatedMLot.getReserved1());
                } catch (AssertionError e) {
                    falg = false;
                }

                try {
                    Assert.assertEquals(waitValidationMLot.getGrade(), validatedMLot.getGrade());
                } catch (AssertionError e) {
                    falg = false;
                }

                try {
                    Assert.assertEquals(waitValidationMLot.getReserved6(), validatedMLot.getReserved6());
                } catch (AssertionError e) {
                    falg = false;
                }
            }
            return falg;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 三方销售
     * @param documentLine
     * @param materialLotActionList
     * @throws ClientException
     */
    public void wltCpThreeSideShip(DocumentLine documentLine, List<MaterialLotAction> materialLotActionList) throws ClientException{
        try {
            documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());
            List<MaterialLot> materialLots = materialLotActionList.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());

            List<MaterialLot> materialLotList = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getThreeSideOrder())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(materialLotList)){
                for(MaterialLot materialLot : materialLotList){
                    DocumentLine threeSideOrder = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(materialLot.getThreeSideOrder()));
                    if(documentLine.getObjectRrn() == threeSideOrder.getObjectRrn()){
                        throw new ClientParameterException(GcExceptions.MATERIAL_LOT_HAS_BEEN_SOLD_BY_THREE_PARTIES, materialLotList.get(0).getMaterialLotId(), documentLine.getDocId());
                    }
                }
            }

            BigDecimal handledQty = BigDecimal.ZERO;
            for(MaterialLot materialLot : materialLots){
                String materialName = materialLot.getMaterialName();
                if(materialName.endsWith(MaterialLot.STOCKOUT_TYPE_35) || materialName.endsWith(MaterialLot.STOCKOUT_TYPE_4)){
                    handledQty = handledQty.add(materialLot.getCurrentQty());
                } else {
                    handledQty = handledQty.add(materialLot.getCurrentSubQty());
                }
                validateMLotAndDocLineByRule(documentLine, materialLot, MaterialLot.MLOT_THREESIDE_DOC_VALIDATE_RULE_ID);
            }

            BigDecimal unHandleQty =  documentLine.getUnHandledQty().subtract(handledQty);
            if (unHandleQty.compareTo(BigDecimal.ZERO) < 0) {
                throw new ClientParameterException(GcExceptions.OVER_DOC_QTY, documentLine.getDocId());
            }

            for (MaterialLot materialLot : materialLots) {
                materialLot = changeMLotWarehouseByDocumentLineAndSaveHis(materialLot, documentLine);
                List<MaterialLot> packageLotDetails = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                if(CollectionUtils.isNotEmpty(packageLotDetails)){
                    for (MaterialLot packageLot : packageLotDetails){
                        packageLot.setReserved13(materialLot.getReserved13());
                        packageLot.setReserved6(materialLot.getReserved6());
                        packageLot.setThreeSideOrder(documentLine.getObjectRrn().toString());
                        packageLot = materialLotRepository.saveAndFlush(packageLot);

                        MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(packageLot, MaterialLotHistory.TRANS_TYPE_THREE_SIDE);
                        materialLotHistoryRepository.save(history);

                        saveMaterialLotUnitWarehouseAndSaveHis(packageLot, materialLot.getReserved13(), materialLot.getReserved6());
                    }
                } else if(StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())){
                    saveMaterialLotUnitWarehouseAndSaveHis(materialLot, materialLot.getReserved13(), materialLot.getReserved6());
                }
            }

            documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
            documentLine.setUnHandledQty(unHandleQty);
            documentLine = documentLineRepository.saveAndFlush(documentLine);
            baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_THREE_SIDE);

            OtherStockOutOrder otherStockOutOrder = (OtherStockOutOrder) otherStockOutOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            otherStockOutOrder.setHandledQty(otherStockOutOrder.getHandledQty().add(handledQty));
            otherStockOutOrder.setUnHandledQty(otherStockOutOrder.getUnHandledQty().subtract(handledQty));
            otherStockOutOrder = otherStockOutOrderRepository.saveAndFlush(otherStockOutOrder);
            baseService.saveHistoryEntity(otherStockOutOrder, MaterialLotHistory.TRANS_TYPE_THREE_SIDE);

            validateAndUpdateErpSoa(documentLine, handledQty);
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证单据是否合单，并更新中间表ERP_SOA的数量信息
     * @param documentLine
     * @param handledQty
     */
    private void validateAndUpdateErpSoa(DocumentLine documentLine, BigDecimal handledQty) throws ClientException {
        try{
            if(StringUtils.isNullOrEmpty(documentLine.getMergeDoc())){
                Optional<ErpSoa> erpSoaOptional = erpSoaOrderRepository.findById(Long.valueOf(documentLine.getReserved1()));
                if (!erpSoaOptional.isPresent()) {
                    throw new ClientParameterException(GcExceptions.ERP_SOA_IS_NOT_EXIST, documentLine.getReserved1());
                }

                ErpSoa erpSoa = erpSoaOptional.get();
                erpSoa.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
                erpSoa.setLeftNum(erpSoa.getLeftNum().subtract(handledQty));
                if (StringUtils.isNullOrEmpty(erpSoa.getDeliveredNum())) {
                    erpSoa.setDeliveredNum(handledQty.toPlainString());
                } else {
                    BigDecimal docHandledQty = new BigDecimal(erpSoa.getDeliveredNum());
                    docHandledQty = docHandledQty.add(handledQty);
                    erpSoa.setDeliveredNum(docHandledQty.toPlainString());
                }
                erpSoaOrderRepository.save(erpSoa);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改晶圆的仓库和保税属性
     * @param materialLot
     * @param warehouseRrn
     * @param bondedProperty
     * @throws ClientException
     */
    private void saveMaterialLotUnitWarehouseAndSaveHis(MaterialLot materialLot, String warehouseRrn, String bondedProperty) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                materialLotUnit.setReserved13(warehouseRrn);
                materialLotUnit.setReserved4(bondedProperty);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotHistory.TRANS_TYPE_THREE_SIDE);
                materialLotUnitHisRepository.save(materialLotUnitHistory);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据单据的三方标记修改物料批次的仓库和保税属性
     * @param materialLot
     * @param documentLine
     * @throws ClientException
     */
    private MaterialLot changeMLotWarehouseByDocumentLineAndSaveHis(MaterialLot materialLot, DocumentLine documentLine) throws ClientException{
        try {
            String cuscode = documentLine.getThreeSideTransaction();
            if(DocumentLine.CUSCODE_9006.equals(cuscode) || DocumentLine.CUSCODE_C1001.equals(cuscode)) {
                List<Warehouse> warehouseList = warehouseRepository.findByNameAndOrgRrn(WAREHOUSE_SH, ThreadLocalContext.getOrgRrn());
                materialLot.setReserved13(warehouseList.get(0).getObjectRrn().toString());
                materialLot.setReserved6(BONDED_PROPERTITY_SH);
            } else if(DocumentLine.CUSCODE_C2837.equals(cuscode) || DocumentLine.CUSCODE_C9009.equals(cuscode)) {
                List<Warehouse> warehouseList = warehouseRepository.findByNameAndOrgRrn(WAREHOUSE_ZJ, ThreadLocalContext.getOrgRrn());
                materialLot.setReserved13(warehouseList.get(0).getObjectRrn().toString());
                materialLot.setReserved6(BONDED_PROPERTITY_ZSH);
            } else if(DocumentLine.CUSCODE_C001.equals(cuscode)) {
                List<Warehouse> warehouseList = warehouseRepository.findByNameAndOrgRrn(WAREHOUSE_HK, ThreadLocalContext.getOrgRrn());
                materialLot.setReserved13(warehouseList.get(0).getObjectRrn().toString());
                materialLot.setReserved6(BONDED_PROPERTITY_HK);
            } else {
                throw new ClientParameterException(GcExceptions.ERP_SOA_CUSCODE_IS_ERROR, cuscode);
            }
            materialLot.setThreeSideOrder(documentLine.getObjectRrn().toString());
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_THREE_SIDE);
            materialLotHistoryRepository.save(history);

            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * WLT/CP销售出货
     * @param documentLineList
     * @param materialLotActions
     * @param checkSubCode
     * @throws ClientException
     */
    public void wltCpMaterialLotSaleShip(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String checkSubCode) throws ClientException{
        try {
            List<DocumentLine> saleShipOrder = documentLineList.stream().filter(documentLine ->  ErpSoa.SOURCE_TABLE_NAME.equals(documentLine.getReserved31()) &&
                    !DocumentLine.CUSCODE_9006.equals(documentLine.getThreeSideTransaction()) && !DocumentLine.CUSCODE_C1001.equals(documentLine.getThreeSideTransaction())
                            && !DocumentLine.CUSCODE_C2837.equals(documentLine.getThreeSideTransaction()) && !DocumentLine.CUSCODE_C9009.equals(documentLine.getThreeSideTransaction())
                            && !DocumentLine.CUSCODE_C001.equals(documentLine.getThreeSideTransaction())).collect(Collectors.toList());

            if(CollectionUtils.isEmpty(saleShipOrder)){
                throw new ClientParameterException(GcExceptions.CHOOSE_STOCK_OUT_ORDER_PLEASE, ErpSoa.SOURCE_TABLE_NAME);
            }
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());

            documentLineList = saleShipOrder.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());

            Map<String, List<DocumentLine>> saleShipOrderMap = groupDocLineByMaterialAndSecondCodeAndBondPropAndShipper(documentLineList, checkSubCode);
            Map<String, List<MaterialLot>> materialLotMap = groupWaferByMaterialAndSecondCodeAndBondPropAndShipper(materialLots, checkSubCode);

            // 确保所有的物料批次都能匹配上单据, 并且数量足够
            for (String key : materialLotMap.keySet()) {
                validateDocAndMlotShipQtyAndMaterialAndSecondCodeInfo(key, materialLotMap, saleShipOrderMap);
                wltCpStockOut(saleShipOrderMap.get(key), materialLotMap.get(key));
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证物料信息是否匹配存在批次单据，并且数量足够
     * @param materialInfo
     * @param materialLotMap
     * @param documentLineMap
     * @throws ClientException
     */
    private void validateDocAndMlotShipQtyAndMaterialAndSecondCodeInfo(String materialInfo, Map<String,List<MaterialLot>> materialLotMap, Map<String,List<DocumentLine>> documentLineMap) throws ClientException{
        try {
            if (!documentLineMap.keySet().contains(materialInfo)) {
                throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLotMap.get(materialInfo).get(0).getMaterialLotId());
            }

            Long totalOrderQty = documentLineMap.get(materialInfo).stream().collect(Collectors.summingLong(documentLine -> documentLine.getUnHandledQty().longValue()));
            //根据出货形态验证出货时消耗的颗数还是片数
            BigDecimal totalQty = BigDecimal.ZERO;
            for(MaterialLot materialLot : materialLotMap.get(materialInfo)){
                if(MaterialLot.STOCKOUT_TYPE_35.equals(materialLot.getReserved54())){
                    totalQty = totalQty.add(materialLot.getCurrentQty());
                } else {
                    totalQty = totalQty.add(materialLot.getCurrentSubQty());
                }
            }
            Long totalMaterialLotQty = totalQty.longValue();
            if (totalMaterialLotQty.compareTo(totalOrderQty) > 0) {
                throw new ClientException(GcExceptions.OVER_DOC_QTY);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * WLT/CP物料批次根据发货单进行发货，更新单据数据以及，更改ERP的中间表数据
     *  documentLine 产品型号 materialName，二级代码 reserved2， 物流 reserved7 一致
     *  materialLot 产品型号 materialName，二级代码 reserved1， 物料 reserved6 一致
     */
    public void wltStockOut(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String checkSubCode) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            List<DocumentLine> shipOrderList = documentLineList.stream().filter(documentLine -> ErpSob.SOURCE_TABLE_NAME.equals(documentLine.getReserved31())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(shipOrderList)){
                throw new ClientParameterException(GcExceptions.CHOOSE_STOCK_OUT_ORDER_PLEASE, ErpSob.SOURCE_TABLE_NAME);
            }
            documentLineList = shipOrderList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());

            Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMaterialAndSecondCodeAndBondPropAndShipper(documentLineList, checkSubCode);
            Map<String, List<MaterialLot>> materialLotMap = groupWaferByMaterialAndSecondCodeAndBondPropAndShipper(materialLots, checkSubCode);

            // 确保所有的物料批次都能匹配上单据, 并且数量足够
            for (String key : materialLotMap.keySet()) {
                validateDocAndMlotShipQtyAndMaterialAndSecondCodeInfo(key, materialLotMap, documentLineMap);
                wltCpStockOut(documentLineMap.get(key), materialLotMap.get(key));
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次（WLT）按照 物料名称+二级代码+保税属性+客户名称分类
     * CP : 物料名称(去掉最后一个'-' + 标注形态)+二级代码+保税属性+客户名称分类
     * @param materialLots
     * @return
     */
    private Map<String,List<MaterialLot>> groupWaferByMaterialAndSecondCodeAndBondPropAndShipper(List<MaterialLot> materialLots, String checkSubCode) {
        return  materialLots.stream().collect(Collectors.groupingBy(materialLot -> {
            StringBuffer key = new StringBuffer();
            String materialName = StringUtils.EMPTY;
            if(!StringUtils.isNullOrEmpty(materialLot.getReserved7()) && MaterialLotUnit.PRODUCT_CATEGORY_WLT.equals(materialLot.getReserved7())){
                materialName = materialLot.getMaterialName();
            } else {
                materialName = materialLot.getMaterialName().substring(0, materialLot.getMaterialName().lastIndexOf("-")) + materialLot.getReserved54();
            }
            key.append(materialName);
            key.append(StringUtils.SPLIT_CODE);

            if(!StringUtils.isNullOrEmpty(checkSubCode)){
                key.append(materialLot.getReserved1());
                key.append(StringUtils.SPLIT_CODE);
            }

            key.append(materialLot.getReserved6());
            key.append(StringUtils.SPLIT_CODE);

            if(StringUtils.isNullOrEmpty(materialLot.getReserved55())){
                key.append(materialLot.getReserved55());
            } else{
                key.append(materialLot.getReserved55().toUpperCase());
            }
            key.append(StringUtils.SPLIT_CODE);

            return key.toString();
        }));
    }

    /**
     * 单据按照 物料名称+二级代码+保税属性+客户名称分类
     * @param documentLineList
     * @return
     */
    public Map<String, List<DocumentLine>> groupDocLineByMaterialAndSecondCodeAndBondPropAndShipper(List<DocumentLine> documentLineList, String checkSubCode) {
        return documentLineList.stream().collect(Collectors.groupingBy(documentLine -> {
            StringBuffer key = new StringBuffer();
            key.append(documentLine.getMaterialName());
            key.append(StringUtils.SPLIT_CODE);

            if(!StringUtils.isNullOrEmpty(checkSubCode)){
                key.append(documentLine.getReserved2());
                key.append(StringUtils.SPLIT_CODE);
            }

            key.append(documentLine.getReserved7());
            key.append(StringUtils.SPLIT_CODE);

            if(StringUtils.isNullOrEmpty(documentLine.getReserved8())){
                key.append(documentLine.getReserved8());
            } else {
                key.append(documentLine.getReserved8().toUpperCase());
            }
            key.append(StringUtils.SPLIT_CODE);
            return key.toString();
        }));
    }

    /**
     * WLT/CP出货  消耗的都是片数
     * @param documentLines
     * @param materialLots
     * @throws ClientException
     */
    private void wltCpStockOut(List<DocumentLine> documentLines, List<MaterialLot> materialLots) throws ClientException{
        try {
            documentLines = vlidateDocMergeAndSortDocumentLinesBySeq(documentLines);
            for(DocumentLine documentLine : documentLines){
                BigDecimal unhandedQty = documentLine.getUnHandledQty();
                Iterator<MaterialLot> iterator = materialLots.iterator();
                while (iterator.hasNext()) {
                    MaterialLot materialLot = iterator.next();
                    if(MaterialLot.STOCKOUT_TYPE_35.equals(materialLot.getReserved54())){
                        BigDecimal currentQty = materialLot.getCurrentQty();
                        if (unhandedQty.compareTo(currentQty) >= 0) {
                            unhandedQty = unhandedQty.subtract(currentQty);
                            currentQty = BigDecimal.ZERO;
                        } else {
                            currentQty = currentQty.subtract(unhandedQty);
                            unhandedQty = BigDecimal.ZERO;
                        }
                        materialLot.setCurrentQty(currentQty);
                        if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                            saveDocLineRrnAndChangeStatus(materialLot, documentLine);
                            iterator.remove();
                        }
                    } else {
                        BigDecimal currentSubQty = materialLot.getCurrentSubQty();
                        if (unhandedQty.compareTo(currentSubQty) >= 0) {
                            unhandedQty = unhandedQty.subtract(currentSubQty);
                            currentSubQty = BigDecimal.ZERO;
                        } else {
                            currentSubQty = currentSubQty.subtract(unhandedQty);
                            unhandedQty = BigDecimal.ZERO;
                        }
                        materialLot.setCurrentSubQty(currentSubQty);
                        if (materialLot.getCurrentSubQty().compareTo(BigDecimal.ZERO) == 0){
                            saveDocLineRrnAndChangeStatus(materialLot, documentLine);
                            iterator.remove();
                        }
                    }
                    if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }

                BigDecimal handledQty = documentLine.getUnHandledQty().subtract(unhandedQty);
                if(handledQty.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                } else {
                    documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
                    documentLine.setUnHandledQty(unhandedQty);
                    documentLine = documentLineRepository.saveAndFlush(documentLine);
                    baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_SHIP);

                    if(ErpSob.SOURCE_TABLE_NAME.equals(documentLine.getReserved31())){
                        updateDocQyAndErpSobSynStatusAndQty(documentLine, handledQty);
                    } else if(ErpSoa.SOURCE_TABLE_NAME.equals(documentLine.getReserved31())) {
                        updateDocQyAndErpSoaSynStatusAndQty(documentLine, handledQty);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 单据种存在合单的单据，先扣减数量，其次按照单据数量扣减
     * @param documentLines
     * @return
     * @throws ClientException
     */
    private List<DocumentLine> vlidateDocMergeAndSortDocumentLinesBySeq(List<DocumentLine> documentLines) throws ClientException{
        try {
            List<DocumentLine> documentLineList = Lists.newArrayList();
            List<DocumentLine> mergeDocLineList = documentLines.stream().filter(documentLine -> DocumentLine.DOC_MERGE.equals(documentLine.getMergeDoc())).collect(Collectors.toList());
            List<DocumentLine> unMergeDocLineList = documentLines.stream().filter(documentLine -> StringUtils.isNullOrEmpty(documentLine.getMergeDoc())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(mergeDocLineList)){
                mergeDocLineList = mergeDocLineList.stream().sorted(Comparator.comparing(DocumentLine :: getCreated)).collect(Collectors.toList());
                documentLineList.addAll(mergeDocLineList);
            }
            if(CollectionUtils.isNotEmpty(unMergeDocLineList)){
                for(DocumentLine documentLine : unMergeDocLineList){
                    documentLine.setErpSeq(Integer.parseInt(documentLine.getReserved1()));
                }
                unMergeDocLineList = unMergeDocLineList.stream().sorted(Comparator.comparing(DocumentLine :: getErpSeq)).collect(Collectors.toList());
                documentLineList.addAll(unMergeDocLineList);
            }
            return documentLineList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 销售出单据信息修改，回写ERP_SOA表数量信息
     * @param documentLine
     * @param handledQty
     * @throws ClientException
     */
    private void updateDocQyAndErpSoaSynStatusAndQty(DocumentLine documentLine, BigDecimal handledQty) throws ClientException{
        try {
            OtherStockOutOrder otherStockOutOrder = (OtherStockOutOrder) otherStockOutOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            otherStockOutOrder.setHandledQty(otherStockOutOrder.getHandledQty().add(handledQty));
            otherStockOutOrder.setUnHandledQty(otherStockOutOrder.getUnHandledQty().subtract(handledQty));
            otherStockOutOrder = otherStockOutOrderRepository.saveAndFlush(otherStockOutOrder);
            baseService.saveHistoryEntity(otherStockOutOrder, MaterialLotHistory.TRANS_TYPE_SHIP);

            validateAndUpdateErpSoa(documentLine, handledQty);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 销售单修改单据数量并回写中间表（ETM_SOB）
     * @param documentLine
     * @param handledQty
     * @throws ClientException
     */
    private void updateDocQyAndErpSobSynStatusAndQty(DocumentLine documentLine, BigDecimal handledQty) throws ClientException{
        try {
            OtherShipOrder otherShipOrder = (OtherShipOrder) otherShipOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            otherShipOrder.setHandledQty(otherShipOrder.getHandledQty().add(handledQty));
            otherShipOrder.setUnHandledQty(otherShipOrder.getUnHandledQty().subtract(handledQty));
            otherShipOrder = otherShipOrderRepository.saveAndFlush(otherShipOrder);
            baseService.saveHistoryEntity(otherShipOrder, MaterialLotHistory.TRANS_TYPE_SHIP);

            if(StringUtils.isNullOrEmpty(documentLine.getMergeDoc())){
                Optional<ErpSob> erpSobOptional = erpSobOrderRepository.findById(Long.valueOf(documentLine.getReserved1()));
                if (!erpSobOptional.isPresent()) {
                    throw new ClientParameterException(GcExceptions.ERP_SOB_IS_NOT_EXIST, documentLine.getReserved1());
                }

                ErpSob erpSob = erpSobOptional.get();
                erpSob.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
                erpSob.setLeftNum(erpSob.getLeftNum().subtract(handledQty));
                if (StringUtils.isNullOrEmpty(erpSob.getDeliveredNum())) {
                    erpSob.setDeliveredNum(handledQty.toPlainString());
                } else {
                    BigDecimal docHandledQty = new BigDecimal(erpSob.getDeliveredNum());
                    docHandledQty = docHandledQty.add(handledQty);
                    erpSob.setDeliveredNum(docHandledQty.toPlainString());
                }
                erpSobOrderRepository.save(erpSob);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 记录单号至物料批次上，并且修改物料批次状态，记录历史
     * @param materialLot
     * @param documentLine
     */
    private void saveDocLineRrnAndChangeStatus(MaterialLot materialLot, DocumentLine documentLine) throws ClientException{
        try {
            if (StringUtils.isNullOrEmpty(materialLot.getReserved12())) {
                materialLot.setReserved12(documentLine.getObjectRrn().toString());
            } else {
                materialLot.setReserved12(materialLot.getReserved12() + StringUtils.SEMICOLON_CODE + documentLine.getObjectRrn().toString());
            }
            changeMaterialLotStatusAndSaveHistory(materialLot);

            //单lot出货修改unit表晶圆状态，记录历史
            List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
            if(CollectionUtils.isNotEmpty(packageDetailLots)){
                changPackageDetailLotStatusAndSaveHis(packageDetailLots);
            } else if(StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())){
                List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
                for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                    materialLotUnit.setState(MaterialLotUnit.STATE_OUT);
                    materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                    MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_STOCK_OUT);
                    materialLotUnitHisRepository.save(materialLotUnitHistory);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证Wlt/CP出货单据和物料批次是否吻合
     *  1，物料名称
     *  2. 二级代码（绝对相等）
     *  4. 保税属性
     * @param documentLine
     * @param materialLot
     * @throws ClientException
     */
    public void validationWltStockOutDocLine(DocumentLine documentLine, MaterialLot materialLot) throws ClientException{
        try {
            Assert.assertEquals(documentLine.getMaterialName(), materialLot.getMaterialName());
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "materialName", documentLine.getMaterialName(), materialLot.getMaterialName());
        }
        try {
            Assert.assertEquals(documentLine.getReserved2(), materialLot.getReserved1());
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "secondcode", documentLine.getReserved2(),  materialLot.getReserved1());
        }

        try {
            Assert.assertEquals(documentLine.getReserved7(), materialLot.getReserved6());
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "other1", documentLine.getReserved7(), materialLot.getReserved6());
        }
    }

    /**
     * 晶圆出货标注
     * @param materialLotActions
     * @param stockTagNote
     * @param customerName
     * @param stockOutType
     * @param poId
     * @throws ClientException
     */
    public void waferStockOutTagging(List<MaterialLotAction> materialLotActions, String stockTagNote, String customerName, String stockOutType, String poId, String address) throws ClientException {
        try {
            List<MaterialLot> materialLotList = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            if(!StringUtils.isNullOrEmpty(poId)){
                //按照产品型号分组扣减PO，PO号和产品号唯一确定一条PO信息
                Map<String, List<MaterialLot>> materialNameMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot :: getMaterialName));
                for(String materialName : materialNameMap.keySet()){
                    BigDecimal totalTaggingQty = materialNameMap.get(materialName).stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentSubQty));
                    GCOutSourcePo outSourcePo = outSourcePoRepository.findByPoIdAndMaterialName(poId, materialName);
                    if(outSourcePo != null){
                        if(outSourcePo.getUnHandledQty().subtract(totalTaggingQty).compareTo(BigDecimal.ZERO) < 0){
                            throw new ClientParameterException(GcExceptions.MATERIAL_LOT_TAG_QTY_OVER_PO_QTY, poId);
                        } else {
                            BigDecimal unHandledQty = outSourcePo.getUnHandledQty();
                            BigDecimal handledQty = outSourcePo.getHandledQty();
                            unHandledQty = unHandledQty.subtract(totalTaggingQty);
                            handledQty = handledQty.add(totalTaggingQty);
                            outSourcePo.setUnHandledQty(unHandledQty);
                            outSourcePo.setHandledQty(handledQty);
                            outSourcePo = outSourcePoRepository.saveAndFlush(outSourcePo);

                            GCOutSourcePoHis history = (GCOutSourcePoHis) baseService.buildHistoryBean(outSourcePo, GCOutSourcePoHis.TRANS_TYPE_STOCK_OUT_TAG);
                            outSourcePoHisRepository.save(history);
                        }
                    }
                }
            }

            for(MaterialLot materialLot : materialLotList){
                taggingMaterialLotAndSaveHis(materialLot, stockOutType, customerName, poId, stockTagNote, address);
            }

            //如果LOT已经装箱，验证箱中所有的LOT是否已经标注，如果全部标注，对箱号进行标注(箱中LOT的标注信息保持一致)
            Map<String, List<MaterialLot>> packedLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            if(packedLotMap != null && packedLotMap.keySet().size() > 0){
                for(String parentMaterialLotId : packedLotMap.keySet()){
                    MaterialLot materialLot = mmsService.getMLotByMLotId(parentMaterialLotId, true);
                    validateMaterilaLotTaggingInfo(materialLot, stockOutType, customerName, poId, stockTagNote,address);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 标注物料批次并记录历史
     * @param materialLot
     * @param stockOutType
     * @param customerName
     * @param poId
     * @param stockTagNote
     * @throws ClientException
     */
    private void taggingMaterialLotAndSaveHis(MaterialLot materialLot, String stockOutType, String customerName, String poId, String stockTagNote,String address) throws ClientException{
        try {
            materialLot.setReserved54(stockOutType);
            materialLot.setReserved55(customerName);
            materialLot.setReserved56(poId);
            materialLot.setReserved57(stockTagNote);
            materialLot.setVenderAddress(address);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_STOCK_OUT_TAG);
            materialLotHistoryRepository.save(history);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证箱中LOT是否全部标注，且标注信息是否一致，如果一致对箱号标注
     * @param materialLot
     * @param stockOutType
     * @param customerName
     * @param poId
     * @param stockTagNote
     * @throws ClientException
     */
    private void validateMaterilaLotTaggingInfo(MaterialLot materialLot, String stockOutType, String customerName, String poId, String stockTagNote, String address) throws ClientException{
        try {
            List<MaterialLot> materialLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
            boolean tagFlag = true;
            for(MaterialLot mlLot : materialLots){
                if(StringUtils.isNullOrEmpty(mlLot.getReserved54())){
                    tagFlag = false;
                    break;
                }
            }
            if(tagFlag){
                Map<String, List<MaterialLot>> mLotMap =  materialLots.stream().collect(Collectors.groupingBy(mLot -> {
                    StringBuffer key = new StringBuffer();
                    if(StringUtils.isNullOrEmpty(mLot.getReserved54())){
                        key.append(StringUtils.EMPTY);
                    } else {
                        key.append(mLot.getReserved54());
                    }
                    if(StringUtils.isNullOrEmpty(mLot.getReserved56())){
                        key.append(StringUtils.EMPTY);
                    } else {
                        key.append(mLot.getReserved56());
                    }
                    if(StringUtils.isNullOrEmpty(mLot.getVenderAddress())){
                        key.append(StringUtils.EMPTY);
                    } else {
                        key.append(mLot.getVenderAddress());
                    }
                    return key.toString();
                }));
                if (mLotMap != null &&  mLotMap.size() > 1) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_TAG_INFO_IS_NOT_SAME, materialLot.getMaterialLotId());
                }
                taggingMaterialLotAndSaveHis(materialLot, stockOutType, customerName, poId, stockTagNote, address);
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 清除晶圆出货标注信息
     * @param materialLotActions
     * @throws ClientException
     */
    public void waferUnStockOutTagging(List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            List<MaterialLot> materialLotList = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            for(MaterialLot materialLot : materialLotList){
                //还原PO标注数量信息
                String poId = materialLot.getReserved56();
                if(!StringUtils.isNullOrEmpty(poId)){
                    Map<String, List<MaterialLot>> materialNameMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot :: getMaterialName));
                    for(String materialName : materialNameMap.keySet()){
                        GCOutSourcePo outSourcePo = outSourcePoRepository.findByPoIdAndMaterialName(poId, materialName);
                        if(outSourcePo != null){
                            BigDecimal poHandleQty = outSourcePo.getHandledQty().subtract(materialLot.getCurrentSubQty());
                            BigDecimal poUnHandleQty = outSourcePo.getUnHandledQty().add(materialLot.getCurrentSubQty());
                            outSourcePo.setHandledQty(poHandleQty);
                            outSourcePo.setUnHandledQty(poUnHandleQty);
                            outSourcePo = outSourcePoRepository.saveAndFlush(outSourcePo);

                            GCOutSourcePoHis history = (GCOutSourcePoHis) baseService.buildHistoryBean(outSourcePo, GCOutSourcePoHis.TRANS_TYPE_UNSTOCK_OUT_TAG);
                            outSourcePoHisRepository.save(history);
                        }
                    }
                }

                unTaggingMaterialLot(materialLot);
            }

            //清除箱上的标注信息
            Map<String, List<MaterialLot>> packedLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            if(packedLotMap != null && packedLotMap.keySet().size() > 0){
                for(String parentMaterialLotId : packedLotMap.keySet()){
                    MaterialLot materialLot = mmsService.getMLotByMLotId(parentMaterialLotId, true);
                    unTaggingMaterialLot(materialLot);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 清除物料批次的标注信息
     * @param materialLot
     * @throws ClientException
     */
    private void unTaggingMaterialLot(MaterialLot materialLot) throws ClientException{
        try {
            materialLot.setReserved54(StringUtils.EMPTY);
            materialLot.setReserved55(StringUtils.EMPTY);
            materialLot.setReserved56(StringUtils.EMPTY);
            materialLot.setReserved57(StringUtils.EMPTY);
            materialLot.setVenderAddress(StringUtils.EMPTY);
            materialLot.setCustomerId(StringUtils.EMPTY);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_UN_STOCK_OUT_TAG);
            materialLotHistoryRepository.save(history);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证物料批次的供应商是否一致
     * @param materialLotActions
     * @throws ClientException
     */
    public void validationMaterialLotVender(List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            List<MaterialLot> materialLotList = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            Set venderInfo = materialLotList.stream().map(materialLot -> materialLot.getReserved22()).collect(Collectors.toSet());
            if (venderInfo != null &&  venderInfo.size() > 1) {
                throw new ClientParameterException(GcExceptions.MATERIALLOT_VENDER_IS_NOT_SAME, materialLotList.get(0).getReserved22());
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证物料批次的产品号是否一致
     * @param materialLotActions
     * @throws ClientException
     */
    public void validationMLotMaterialName(List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            List<MaterialLot> materialLotList = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            Set productInfo = materialLotList.stream().map(materialLot -> materialLot.getMaterialName()).collect(Collectors.toSet());
            if (productInfo != null &&  productInfo.size() > 1) {
                throw new ClientParameterException(GcExceptions.MATERIALLOT_MATERIAL_NAME_IS_NOT_SAME, materialLotList.get(0).getMaterialName());
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialLot getWltMaterialLotToStockOut(Long tableRrn, String queryLotId) throws ClientException {
        try {
            MaterialLot materialLot = new MaterialLot();
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String _whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();

            StringBuffer clauseBuffer = new StringBuffer(_whereClause);
            clauseBuffer.append(" AND materialLotId = ");
            clauseBuffer.append("'" + queryLotId + "'");
            _whereClause = clauseBuffer.toString();
            List<MaterialLot> materialLotList = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);
            if(CollectionUtils.isEmpty(materialLotList)){
                clauseBuffer = new StringBuffer(nbTable.getWhereClause());
                clauseBuffer.append(" AND lotId = ");
                clauseBuffer.append("'" + queryLotId+ "'");
                _whereClause = clauseBuffer.toString();
                materialLotList = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);
            }
            if(CollectionUtils.isNotEmpty(materialLotList)){
                materialLot = materialLotList.get(0);
            }
            return materialLot;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 获取WLT或者CP的标签打印参数
     * @return
     * @throws ClientException
     */
    public Map<String, String> getWltCpPrintParameter(MaterialLot materialLot) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            parameterMap.put("LOTID", materialLot.getLotId());
            parameterMap.put("DEVICEID", materialLot.getMaterialName());
            parameterMap.put("QTY", materialLot.getCurrentQty().toString());
            parameterMap.put("WAFERGRADE", materialLot.getGrade());
            parameterMap.put("LOCATION", materialLot.getReserved6());
            parameterMap.put("SUBCODE", materialLot.getReserved1());
            List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());

            if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                Integer waferQty = materialLotUnitList.size();
                parameterMap.put("WAFERQTY", waferQty.toString());
                String waferIdList1 = "";
                String waferIdList2 = "";

                for(int j = 0; j <  materialLotUnitList.size() ; j++){
                    String[] waferIdList = materialLotUnitList.get(j).getUnitId().split(StringUtils.SPLIT_CODE);
                    String waferSeq = waferIdList[1] + ",";
                    if(j < 8){
                        waferIdList1 = waferIdList1 + waferSeq;
                    } else {
                        waferIdList2 = waferIdList2 + waferSeq;
                    }
                }
                if(!StringUtils.isNullOrEmpty(waferIdList1)){
                    parameterMap.put("WAFERID1", waferIdList1);
                } else {
                    parameterMap.put("WAFERID1", StringUtils.EMPTY);
                }
                if(!StringUtils.isNullOrEmpty(waferIdList2)){
                    parameterMap.put("WAFERID2", waferIdList2);
                } else {
                    parameterMap.put("WAFERID2", StringUtils.EMPTY);
                }
            }
            return parameterMap;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取装箱检验的物料批次信息
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getMaterialLotByTableRrnAndMLotId(String mLotId, long tableRrn) throws ClientException{
        try {
            List<MaterialLot> materialLotList = Lists.newArrayList();
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();
            StringBuffer clauseBuffer = new StringBuffer(whereClause);
            clauseBuffer.append(" AND parentMaterialLotId = ");
            clauseBuffer.append("'" + mLotId + "'");
            whereClause = clauseBuffer.toString();
            materialLotList = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause, orderBy);

            if(CollectionUtils.isEmpty(materialLotList)){
                clauseBuffer = new StringBuffer(nbTable.getWhereClause());
                clauseBuffer.append(" AND lotId = ");
                clauseBuffer.append("'" + mLotId+ "'");
                clauseBuffer.append(" AND statusCategory = 'Stock' and status = 'In' ");
                whereClause = clauseBuffer.toString();
                materialLotList = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause, orderBy);
            }

            return materialLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取加密之后的二级代码
     * @param subcode
     * @return
     * @throws ClientException
     */
    public String getEncryptionSubCode(String grade, String subcode) throws ClientException{
        try {
            String targetSubcode = StringUtils.EMPTY;
            String endString = StringUtils.EMPTY;
            if(subcode.length() > 4){
                subcode = subcode.substring(0, 4);
            }
            List<NBOwnerReferenceList> encryptionSubcodeList = getReferenceListByName(REFERENCE_NAME_ENCRYPTION_SUBCODE_LIST);
            Map<String, List<NBOwnerReferenceList>> subcodeMap = encryptionSubcodeList.stream().collect(Collectors.groupingBy(NBOwnerReferenceList:: getKey));
            if(subcodeMap.containsKey(grade)){
                if(MaterialLot.GEADE_NA.equals(grade) || MaterialLot.GEADE_DA.equals(grade) ||  MaterialLot.GEADE_EA.equals(grade) ||  MaterialLot.GEADE_AA.equals(grade)){
                    NBOwnerReferenceList referenceList = subcodeMap.get(grade).get(0);
                    targetSubcode = subcode + referenceList.getValue();
                } else {
                    NBOwnerReferenceList referenceList = subcodeMap.get(grade).get(0);
                    targetSubcode = MaterialLot.GRADE_FIRST + subcode + referenceList.getValue();
                }
            } else {
                List<String> randomNumber = getTwoRandomChar();
                if(MaterialLot.GEADE_TA.equals(grade)){
                    targetSubcode = getTargetSubCode(MaterialLot.GRADE_FIXED_CHAR_ZERO, subcode, randomNumber);
                } else if(MaterialLot.GEADE_HA.equals(grade) || MaterialLot.GEADE_HA1.equals(grade)){
                    targetSubcode = getTargetSubCode(MaterialLot.GRADE_FIXED_CHAR_ONE, subcode, randomNumber);
                } else if(MaterialLot.GEADE_SA.equals(grade)){
                    targetSubcode = getTargetSubCode(MaterialLot.GRADE_FIXED_CHAR_TWO, subcode, randomNumber);
                } else if(MaterialLot.GEADE_MA.equals(grade)){
                    targetSubcode = getTargetSubCode(MaterialLot.GRADE_FIXED_CHAR_THREE, subcode, randomNumber);
                } else if(MaterialLot.GEADE_WA.equals(grade)){
                    targetSubcode = getTargetSubCode(MaterialLot.GRADE_FIXED_CHAR_FOUR, subcode, randomNumber);
                } else if(MaterialLot.GEADE_HA2.equals(grade)){
                    for(int i=0; i< randomNumber.size(); i++){
                        endString += randomNumber.get(i);
                    }
                    targetSubcode = MaterialLot.GRADE_FIRST + subcode + endString + MaterialLot.GRADE_FIXED_CHAR_Q;
                } else if(MaterialLot.GEADE_HA3.equals(grade)){
                    for(int i=0; i< randomNumber.size(); i++){
                        endString += randomNumber.get(i);
                    }
                    targetSubcode = MaterialLot.GRADE_FIRST + subcode + endString + MaterialLot.GRADE_FIXED_CHAR_Z;
                } else {
                    targetSubcode = MaterialLot.GRADE_UNDEFINED;
                }
            }
            return targetSubcode;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取加密的等级信息
     * @param fixedChar
     * @param subcode
     * @param randomNumber
     * @return
     * @throws ClientException
     */
    private String getTargetSubCode(String fixedChar, String subcode, List<String> randomNumber) throws ClientException{
        try {
            String targetSubcode = StringUtils.EMPTY;
            String endString = StringUtils.EMPTY;
            randomNumber.add(fixedChar);
            Collections.shuffle(randomNumber);
            for(int i=0; i< randomNumber.size(); i++){
                endString += randomNumber.get(i);
            }
            targetSubcode = MaterialLot.GRADE_FIRST + subcode + endString;
            return targetSubcode;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 从5-9、A-Z中随机获取两个字符串
     * @return
     * @throws ClientException
     */
    private List<String> getTwoRandomChar() throws ClientException{
        try {
            List<String> twoRandomCharList = Lists.newArrayList();
            Random random = new Random();
            String [] numberStrArray = "5,6,7,8,9".split(",");
            String [] letterStrArray = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z".split(",");
            String number = numberStrArray[random.nextInt(numberStrArray.length)];
            String letter = letterStrArray[random.nextInt(letterStrArray.length)];
            twoRandomCharList.add(number);
            twoRandomCharList.add(letter);
            return twoRandomCharList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public GCWorkorderRelation saveWorkorderGradeHoldInfo(GCWorkorderRelation workorderRelation, String transType) throws ClientException{
        try {
            GCWorkorderRelation oldWorkorderRelation = workorderRelationRepository.findByWorkOrderIdAndGrade(workorderRelation.getWorkOrderId(), workorderRelation.getGrade());
            if(NBHis.TRANS_TYPE_CREATE.equals(transType)){
                if(oldWorkorderRelation == null){
                    workorderRelation = workorderRelationRepository.saveAndFlush(workorderRelation);

                    GCWorkorderRelationHis history = (GCWorkorderRelationHis) baseService.buildHistoryBean(workorderRelation, transType);
                    workorderRelationHisRepository.save(history);
                } else {
                    throw new ClientParameterException(GcExceptions.WORKORDER_GRADE_HOLD_INFO_IS_EXIST, workorderRelation.getWorkOrderId());
                }
            } else {
                workorderRelation = workorderRelationRepository.saveAndFlush(workorderRelation);

                GCWorkorderRelationHis history = (GCWorkorderRelationHis) baseService.buildHistoryBean(workorderRelation, transType);
                workorderRelationHisRepository.save(history);
            }

            return  workorderRelation;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void validateMLotAndDocLineByRule(DocumentLine documentLine, MaterialLot materialLot, String ruleName) throws ClientException{
        try {
            List<MLotDocRule> mLotDocLineRule = mLotDocRuleRepository.findByNameAndOrgRrn(ruleName, ThreadLocalContext.getOrgRrn());
            if (CollectionUtils.isEmpty(mLotDocLineRule)) {
                throw new ClientParameterException(GcExceptions.MLOT_DOC_VALIDATE_RULE_IS_NOT_EXIST, ruleName);
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

    private Map<String,List<DocumentLine>> groupDocLineByMLotDocRule(List<DocumentLine> documentLineList, String ruleName) throws ClientException{
        try {
            Map<String,List<DocumentLine>> documentLineMap = Maps.newHashMap();
            List<MLotDocRule> mLotDocLineRule = mLotDocRuleRepository.findByNameAndOrgRrn(ruleName, ThreadLocalContext.getOrgRrn());
            if (CollectionUtils.isEmpty(mLotDocLineRule)) {
                throw new ClientParameterException(GcExceptions.MLOT_DOC_VALIDATE_RULE_IS_NOT_EXIST, ruleName);
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

    private Map<String,List<MaterialLot>> groupMaterialLotByMLotDocRule(List<MaterialLot> materialLots, String ruleId) throws ClientException{
        try {
            Map<String,List<MaterialLot>> materialLotMap = Maps.newHashMap();
            List<MLotDocRule> mLotDocLineRule = mLotDocRuleRepository.findByNameAndOrgRrn(ruleId, ThreadLocalContext.getOrgRrn());
            if (CollectionUtils.isEmpty(mLotDocLineRule)) {
                throw new ClientParameterException(GcExceptions.MLOT_DOC_VALIDATE_RULE_IS_NOT_EXIST, ruleId);
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

    /**
     * FT来料导入
     * @param materialLotUnits
     * @param importType
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> createFTMaterialLotAndGetImportCode(List<MaterialLotUnit> materialLotUnits, String importType) throws ClientException{
        try {
            Map<String, List<MaterialLotUnit>> materialUnitIdMap = materialLotUnits.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getUnitId));
            for(String unitId : materialUnitIdMap.keySet()){
                if(materialUnitIdMap.get(unitId).size() > 1){
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_UNIT_ID_REPEATS, unitId);
                }
                MaterialLot materialLot = mmsService.getMLotByMLotId(unitId);
                if(materialLot != null){
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, unitId);
                }
            }
            for(MaterialLotUnit materialLotUnit : materialLotUnits){
                GCProductSubcode gcProductSubcode = getProductAndSubcodeInfo(materialLotUnit.getMaterialName(), materialLotUnit.getReserved1());
                if(gcProductSubcode == null ){
                    throw new ClientParameterException(GcExceptions.PRODUCT_AND_SUBCODE_IS_NOT_EXIST, materialLotUnit.getMaterialName() + StringUtils.SPLIT_CODE + materialLotUnit.getReserved1());
                }
                materialLotUnit.setLotId(materialLotUnit.getUnitId().toUpperCase());
                materialLotUnit.setMaterialLotId(materialLotUnit.getUnitId().toUpperCase());
                materialLotUnit.setReceiveQty(materialLotUnit.getCurrentQty());
                materialLotUnit.setCurrentSubQty(BigDecimal.ONE);
                materialLotUnit.setReserved6(StringUtils.EMPTY);//来料导入时reserved6不是报税属性，暂时清空
                materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_SENSOR);//晶圆信息不保存产品型号
                materialLotUnit.setReserved18("0");
                materialLotUnit.setReserved30(materialLotUnit.getReserved30().split("\\.")[0]);
                materialLotUnit.setReserved32(materialLotUnit.getCurrentQty().toString());
                materialLotUnit.setReserved49(MaterialLot.IMPORT_SENSOR);
                materialLotUnit.setReserved50(MaterialLot.SENSOR_WAFER_SOURCE);
            }
            materialLotUnits = materialLotUnitService.createFTMLot(materialLotUnits);

            return materialLotUnits;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * FT晶圆接收(unitId与materialLotId一致)
     * @param materialLotUnits
     * @throws ClientException
     */
    public void receiveFTWafer(List<MaterialLotUnit> materialLotUnits) throws ClientException {
        try {
            List<MaterialLot> materialLotList = materialLotUnits.stream().map(materialLotUnit -> mmsService.getMLotByMLotId(materialLotUnit.getMaterialLotId(), true)).collect(Collectors.toList());
            for(MaterialLotUnit materialLotUnit : materialLotUnits){
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotUnit.getMaterialLotId());
                Warehouse warehouse = new Warehouse();
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved13())){
                    warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                }
                if(warehouse == null){
                    throw new ClientParameterException(GcExceptions.WAREHOUSE_CANNOT_EMPTY);
                }
                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
                materialLotAction.setTransQty(materialLot.getCurrentQty());
                materialLotAction.setTransCount(materialLot.getCurrentSubQty());
                mmsService.stockIn(materialLot, materialLotAction);

                materialLotUnit.setState(MaterialLotUnit.STATE_IN);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);
                MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_IN);
                history.setTransQty(materialLotUnit.getCurrentQty());
                materialLotUnitHisRepository.save(history);
            }

            for(MaterialLot materialLot : materialLotList){
                String importType = materialLot.getReserved49();
                if(MaterialLot.IMPORT_CRMA.equals(importType) || MaterialLot.IMPORT_RETURN.equals(importType) || MaterialLot.IMPORT_RMA.equals(importType)){
                    continue;
                }
                String prodCate = MaterialLotUnit.PRODUCT_TYPE_PROD;
                Warehouse warehouse  = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                if(!StringUtils.isNullOrEmpty(materialLot.getProductType())){
                    prodCate = materialLot.getProductType();
                }

                ErpInStock erpInStock = new ErpInStock();
                erpInStock.setProdCate(prodCate);
                erpInStock.setMaterialLot(materialLot);
                if(ErpInStock.WAREHOUSE_ZJ_STOCK.equals(warehouse.getName())){
                    erpInStock.setWarehouse(ErpInStock.ZJ_STOCK);
                } else if(ErpInStock.WAREHOUSE_SH_STOCK.equals(warehouse.getName())){
                    erpInStock.setWarehouse(ErpInStock.SH_STOCK);
                } else if(ErpInStock.WAREHOUSE_HK_STOCK.equals(warehouse.getName())){
                    erpInStock.setWarehouse(ErpInStock.HK_STOCK);
                } else {
                    throw new ClientParameterException(GcExceptions.ERP_WAREHOUSE_CODE_IS_UNDEFINED, warehouse.getName());
                }
                erpInStockRepository.save(erpInStock);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取入库位晶圆信息
     * @param unitId
     * @param tableRrn
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> queryFTMLotByUnitIdAndTableRrn(String unitId, long tableRrn) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnitList = Lists.newArrayList();
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();
            StringBuffer clauseBuffer = new StringBuffer(whereClause);
            clauseBuffer.append(" AND unitId = ");
            clauseBuffer.append("'" + unitId + "'");
            whereClause = clauseBuffer.toString();
            materialLotUnitList = materialLotUnitRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause, orderBy);

            return materialLotUnitList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * FT来料入中转箱
     * @param materialLotUnits
     * @param stockInModels
     * @throws ClientException
     */
    public void stockInFTWafer(List<MaterialLotUnit> materialLotUnits, List<StockInModel> stockInModels) throws ClientException {
        try {
            stockIn(stockInModels);
            List<MaterialLot> materialLots = stockInModels.stream().map(model -> mmsService.getMLotByMLotId(model.getMaterialLotId(), true)).collect(Collectors.toList());
            for(MaterialLot materialLot : materialLots){
                Warehouse warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                saveErpInStock(materialLot, materialLot.getProductType(), warehouse.getName());
            }
            for (MaterialLotUnit materialLotUnit : materialLotUnits){
                materialLotUnit.setReserved8(materialLotUnit.getRelaxBoxId());
                materialLotUnit.setReserved14(materialLotUnit.getStorageId());
                materialLotUnitRepository.save(materialLotUnit);

                MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotHistory.TRANS_TYPE_STOCK_IN);
                materialLotUnitHisRepository.save(materialLotUnitHistory);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存产品箱数量绑定关系
     * @param productNumberRelation
     * @return
     * @throws ClientException
     */
    public GCProductNumberRelation saveProductNumberRelation(GCProductNumberRelation productNumberRelation, String transType) throws ClientException {
        try {
            GCProductNumberRelation oldProductNumberRelation = new GCProductNumberRelation();
            String productId = productNumberRelation.getProductId();
            BigDecimal packageQty = productNumberRelation.getPackageQty();
            BigDecimal boxPackedQty = productNumberRelation.getBoxPackedQty();
            String defaultFlag = productNumberRelation.getDefaultFlag();
            if(GCProductNumberRelation.TRANS_TYPE_CREATE.equals(transType)){
                oldProductNumberRelation = productNumberRelationRepository.findByProductIdAndPackageQtyAndBoxPackedQty(productId, packageQty, boxPackedQty);
                if(oldProductNumberRelation != null){
                    throw new ClientParameterException(GcExceptions.PRODUCT_NUMBER_RELATION_IS_EXIST, productId);
                }
            }
            if(StringUtils.YES.equals(defaultFlag)){
                oldProductNumberRelation = productNumberRelationRepository.findByProductIdAndDefaultFlag(productId, defaultFlag);
                if(oldProductNumberRelation != null){
                    oldProductNumberRelation.setDefaultFlag(StringUtils.NO);
                    productNumberRelationRepository.save(oldProductNumberRelation);

                    GCProductNumberRelationHis productNumberRelationHis = (GCProductNumberRelationHis) baseService.buildHistoryBean(oldProductNumberRelation, GCProductNumberRelation.TRANS_TYPE_UPDATE);
                    productNumberRelationHisRepository.save(productNumberRelationHis);
                }
            }

            productNumberRelation = productNumberRelationRepository.saveAndFlush(productNumberRelation);

            if(GCProductNumberRelation.TRANS_TYPE_CREATE.equals(transType)){
                GCProductNumberRelationHis productNumberRelationHis = (GCProductNumberRelationHis) baseService.buildHistoryBean(productNumberRelation, GCProductNumberRelation.TRANS_TYPE_CREATE);
                productNumberRelationHisRepository.save(productNumberRelationHis);
            } else {
                GCProductNumberRelationHis productNumberRelationHis = (GCProductNumberRelationHis) baseService.buildHistoryBean(productNumberRelation, GCProductNumberRelation.TRANS_TYPE_UPDATE);
                productNumberRelationHisRepository.save(productNumberRelationHis);
            }
            return productNumberRelation;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据产品号获取包装规格
     * @param documentLineRrn
     * @return
     * @throws ClientException
     */
    public List<GCProductNumberRelation> getProductNumberRelationByDocRrn(Long documentLineRrn) throws ClientException{
        try {
            DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLineRrn);
            String productId = documentLine.getMaterialName();
            List<GCProductNumberRelation> gcProductNumberRelationList = productNumberRelationRepository.findByProductId(productId);
            if(CollectionUtils.isEmpty(gcProductNumberRelationList)){
                throw new ClientParameterException(GcExceptions.PRODUCT_NUMBER_RELATION_IS_NOT_EXIST, productId);
            }
            return gcProductNumberRelationList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据包装规则和单据挑选满足条件的物料批次
     * @param documentLineRrn
     * @param materialLotActions
     * @param packageRule
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getMaterialLotByPackageRuleAndDocLine(Long documentLineRrn, List<MaterialLotAction> materialLotActions, String packageRule) throws ClientException{
        try {
            List<MaterialLot> materialLotList = Lists.newArrayList();
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLineRrn);
            String materialNmae = documentLine.getMaterialName();
            BigDecimal boxPackedQty = new BigDecimal(packageRule);
            List<GCProductNumberRelation> productNumberRelations = productNumberRelationRepository.findByProductIdAndBoxPackedQty(materialNmae, boxPackedQty);
            if(CollectionUtils.isNotEmpty(productNumberRelations) && productNumberRelations.size() > 1){
                throw new ClientParameterException(GcExceptions.PRODUCT_NUMBER_RELATION_IS_ERROR, materialNmae);
            }
            GCProductNumberRelation productNumberRelation = productNumberRelations.get(0);
            BigDecimal totalQty = documentLine.getUnReservedQty();//单据备货单未备货数量
            List<MaterialLot> wholeBoxMLots = Lists.newArrayList();//整箱
            List<MaterialLot> zeroBoxMLots = Lists.newArrayList();//零箱
            List<MaterialLot> wholeVboxMLots = Lists.newArrayList();//整包
            List<MaterialLot> zeroVBoxMLots = Lists.newArrayList();//零包
            BigDecimal totalNumber = productNumberRelation.getTotalNumber();//整箱颗数
            BigDecimal packageQty = productNumberRelation.getPackageQty();//整包颗数
            //将物料批次按照箱号做分组
            Map<String, List<MaterialLot>> packedLotMap = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            if(packedLotMap != null && packedLotMap.keySet().size() > 0){
                for(String parentMaterialLotId : packedLotMap.keySet()){
                    MaterialLot materialLot = mmsService.getMLotByMLotId(parentMaterialLotId, true);
                    BigDecimal unreservedQty = materialLot.getCurrentQty().subtract(materialLot.getReservedQty());
                    if(MaterialLot.HOLD_STATE_ON.equals(materialLot.getHoldState())){
                        zeroBoxMLots.add(materialLot);
                    } else if(totalNumber.compareTo(unreservedQty) == 0){
                        wholeBoxMLots.add(materialLot);
                    } else if(totalNumber.compareTo(unreservedQty) > 0){
                        zeroBoxMLots.add(materialLot);
                    } else {
                        throw new ClientParameterException(GcExceptions.MATERIALLOT_PACKAGE_RULE_IS_ERROR, parentMaterialLotId);
                    }
                    materialLots.removeAll(packedLotMap.get(parentMaterialLotId));
                }
            }
            //未装箱的物料批次信息
            if(CollectionUtils.isNotEmpty(materialLots)){
                for(MaterialLot materialLot : materialLots){
                    if(packageQty.compareTo(materialLot.getCurrentQty()) == 0){
                        wholeVboxMLots.add(materialLot);
                    } else if(packageQty.compareTo(materialLot.getCurrentQty()) > 0){
                        zeroVBoxMLots.add(materialLot);
                    } else {
                        throw new ClientParameterException(GcExceptions.MATERIALLOT_PACKAGE_RULE_IS_ERROR, materialLot.getMaterialLotId());
                    }
                }
            }
            //先挑整箱的
            if(CollectionUtils.isNotEmpty(wholeBoxMLots)){
                wholeBoxMLots = wholeBoxMLots.stream().sorted(Comparator.comparing(MaterialLot::getCreated)).collect(Collectors.toList());
            }
            Iterator<MaterialLot> iterator = wholeBoxMLots.iterator();
            while (iterator.hasNext()){
                MaterialLot materialLot = iterator.next();
                if(totalQty.compareTo(materialLot.getCurrentQty()) >= 0){
                    materialLotList.addAll(packedLotMap.get(materialLot.getMaterialLotId()));
                    totalQty = totalQty.subtract(materialLot.getCurrentQty());
                    iterator.remove();
                }
            }
            //再挑未装箱的真空包（整包）
            if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                if(CollectionUtils.isNotEmpty(wholeVboxMLots)){
                    List<MaterialLot> wholeVboxMLotList = wholeVboxMLots.stream().sorted(Comparator.comparing(MaterialLot::getCreated)).collect(Collectors.toList());
                    for(MaterialLot materialLot: wholeVboxMLotList){
                        if(totalQty.compareTo(materialLot.getCurrentQty()) >= 0){
                            materialLotList.add(materialLot);
                            totalQty = totalQty.subtract(materialLot.getCurrentQty());
                        }
                    }
                }
            }
            //已经装箱的零数箱（先装箱的先挑）
            if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                if(CollectionUtils.isNotEmpty(zeroBoxMLots)){
                    List<MaterialLot> zeroBoxMLotList = zeroBoxMLots.stream().sorted(Comparator.comparing(MaterialLot::getCreated)).collect(Collectors.toList());
                    for (MaterialLot materialLot: zeroBoxMLotList){
                        BigDecimal unreservedQty = BigDecimal.ZERO;
                        if(MaterialLot.HOLD_STATE_ON.equals(materialLot.getHoldState())){
                            List<MaterialLot> mLotDetials = packedLotMap.get(materialLot.getMaterialLotId());
                            Long totalUnhandledQty = mLotDetials.stream().collect(Collectors.summingLong(mLot -> mLot.getCurrentQty().longValue()));
                            unreservedQty = new BigDecimal(totalUnhandledQty);
                        } else {
                            unreservedQty = materialLot.getCurrentQty().subtract(materialLot.getReservedQty());
                        }
                        if(totalQty.compareTo(unreservedQty) >= 0){
                            materialLotList.addAll(packedLotMap.get(materialLot.getMaterialLotId()));
                            totalQty = totalQty.subtract(unreservedQty);
                        }
                    }
                }
            }
            //再挑已经装箱的整箱中的真空包
            if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                if(CollectionUtils.isNotEmpty(wholeBoxMLots)){
                    List<MaterialLot> wholeBoxMLotList = wholeBoxMLots.stream().sorted(Comparator.comparing(MaterialLot::getCreated)).collect(Collectors.toList());
                    for(MaterialLot packagedLot : wholeBoxMLotList){
                        List<MaterialLot> packedDetials = packedLotMap.get(packagedLot.getMaterialLotId());
                        for(MaterialLot packedMLot : packedDetials){
                            if(totalQty.compareTo(packedMLot.getCurrentQty()) >= 0){
                                materialLotList.add(packedMLot);
                                totalQty = totalQty.subtract(packedMLot.getCurrentQty());
                            }
                        }
                    }
                }
            }

            //最后挑选未装箱的真空包（只挑零包）、零包需先挑选数量多的，数量相同的按照先进先出的原则挑选
            if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                if(CollectionUtils.isNotEmpty(zeroVBoxMLots)){
                    List<MaterialLot> zeroVBoxMLotList = zeroVBoxMLots.stream().sorted(Comparator.comparing(MaterialLot::getCurrentQty).reversed()).collect(Collectors.toList());
                    Map<BigDecimal, List<MaterialLot>> mLotMap = zeroVBoxMLotList.stream().collect(Collectors.groupingBy(MaterialLot :: getCurrentQty));
                    for(BigDecimal qty : mLotMap.keySet()){
                        List<MaterialLot> zeroMLots = mLotMap.get(qty).stream().sorted(Comparator.comparing(MaterialLot::getCreated)).collect(Collectors.toList());
                        for(MaterialLot zeroVbox : zeroMLots){
                            if(totalQty.compareTo(zeroVbox.getCurrentQty()) >= 0){
                                totalQty = totalQty.subtract(zeroVbox.getCurrentQty());
                                materialLotList.add(zeroVbox);
                            } else {
                                break;
                            }
                        }
                    }

                }
            }

            return materialLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 原料清单导入 保存
     * @param materialLotList
     * @param importType
     * @return
     * @throws ClientException
     */
    @Override
    public String importRawMaterialLotList(List<MaterialLot> materialLotList, String importType) throws  ClientException{
        try {
            Warehouse warehouse = mmsService.getWarehouseByName(WAREHOUSE_ZJ);
            if (warehouse == null) {
                throw new ClientParameterException(MmsException.MM_WAREHOUSE_IS_NOT_EXIST, WAREHOUSE_ZJ);
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd");
            String importCode = generatorMLotsTransId(MaterialLot.GENERATOR_INCOMING_MLOT_IMPORT_CODE_RULE);
            Map<String, List<MaterialLot>> materialLotMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot::getMaterialName));
            for(String materialName : materialLotMap.keySet()){
                RawMaterial rawMaterial = mmsService.getRawMaterialByName(materialName);
                if (rawMaterial == null){
                    throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_IS_NOT_EXIST, materialName);
                }
                String materialType = rawMaterial.getMaterialType() ;
                if (!importType.equals(materialType)){
                    throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_TYPE_NOT_SAME, importType);
                }
                List<MaterialLot> materialLots = materialLotMap.get(materialName);
                for(MaterialLot materialLot : materialLots){
                    MaterialLot oldmaterialLot = mmsService.getMLotByMLotId(materialLot.getMaterialLotId());
                    if(oldmaterialLot != null){
                        throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, materialLot.getMaterialLotId());
                    }

                    materialLot.setMaterial(rawMaterial);
                    materialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
                    materialLot.setStatus(MaterialStatus.STATUS_CREATE);
                    materialLot.setStatusModelRrn(rawMaterial.getStatusModelRrn());
                    materialLot.initialMaterialLot();
                    materialLot.setProductType(StringUtils.EMPTY);
                    materialLot.setReserved13(warehouse.getObjectRrn().toString());
                    materialLot.setReserved48(importCode);
                    materialLot.setReserved49(importType);
                    if(!StringUtils.isNullOrEmpty(materialLot.getMfgDateValue())){
                        String msgDate = formats.format(simpleDateFormat.parse(materialLot.getMfgDateValue()));
                        materialLot.setMfgDate(formats.parse(msgDate));
                    }
                    if(!StringUtils.isNullOrEmpty(materialLot.getExpDateValue())){
                        String expDate = formats.format(simpleDateFormat.parse(materialLot.getExpDateValue()));
                        materialLot.setExpDate(formats.parse(expDate));
                    }
                    if(!StringUtils.isNullOrEmpty(materialLot.getShippingDateValue())){
                        String shippingDate = formats.format(simpleDateFormat.parse(materialLot.getShippingDateValue()));
                        materialLot.setShippingDate(formats.parse(shippingDate));
                    }
                    materialLotRepository.save(materialLot);
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE);
                    materialLotHistoryRepository.save(history);
                }
            }
            return importCode;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 通过导入型号验证是否做产品型号转换，并保存原产品型号（后续接收时需要保存至中间表ETM_IN_STOCK）
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> validateAndChangeMaterialNameByImportType(List<MaterialLotUnit> materialLotUnits, String importType) throws ClientException{
        try {
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnits.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialName));
            if(MaterialLotUnit.SENSOR_CP.equals(importType) || MaterialLotUnit.FAB_SENSOR.equals(importType) || MaterialLotUnit.SENSOR_CP_KLT.equals(importType)
                    || MaterialLotUnit.SENSOR_UNMEASURED.equals(importType) || MaterialLotUnit.FAB_SENSOR_2UNMEASURED.equals(importType)){
                changeMaterialNameByModelCategory(materialLotUnitMap, MaterialLot.IMPORT_SENSOR_CP);
            } else if(MaterialLotUnit.LCD_CP_25UNMEASURED.equals(importType) || MaterialLotUnit.FAB_LCD_PTC.equals(importType) || MaterialLotUnit.FAB_LCD_SILTERRA.equals(importType)
                    || MaterialLotUnit.LCD_CP.equals(importType)){
                changeMaterialNameByModelCategory(materialLotUnitMap, MaterialLot.IMPORT_LCD_CP);
            } else if(MaterialLotUnit.SENSOR_PACK_RETURN.equals(importType) || MaterialLotUnit.SENSOR_PACK_RETURN_COGO.equals(importType) || MaterialLotUnit.SENSOR_TPLCC.equals(importType)){
                for(String materialName : materialLotUnitMap.keySet()){
                    GCProductModelConversion productModelConversion = gcProductModelConversionRepository.findByProductIdAndModelCategory(materialName, MaterialLot.IMPORT_FT);
                    if(productModelConversion != null){
                        String conversionModelId = productModelConversion.getConversionModelId();
                        conversionModelId = conversionModelId.substring(0, conversionModelId.lastIndexOf("-")) + "-3.5";
                        List<MaterialLotUnit> materialLotUnitList = materialLotUnitMap.get(materialName);
                        for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                            materialLotUnit.setSourceProductId(materialName);
                            materialLotUnit.setMaterialName(conversionModelId);
                        }
                    }
                }
            }
            return  materialLotUnits;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 通过产品型号转换表修改来料的晶圆型号
     * @param materialLotUnitMap
     * @param modelCategory
     * @throws ClientException
     */
    private void changeMaterialNameByModelCategory(Map<String,List<MaterialLotUnit>> materialLotUnitMap, String modelCategory) throws ClientException{
        try {
            for(String materialName : materialLotUnitMap.keySet()){
                GCProductModelConversion productModelConversion = gcProductModelConversionRepository.findByProductIdAndModelCategory(materialName, modelCategory);
                if(productModelConversion != null){
                    String conversionModelId = productModelConversion.getConversionModelId();
                    List<MaterialLotUnit> materialLotUnitList = materialLotUnitMap.get(materialName);
                    for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                        materialLotUnit.setMaterialName(conversionModelId);
                        materialLotUnit.setSourceProductId(materialName);
                    }
                }
            }
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * mes记录物料批次历史
     * @param materialLots
     * @param transId
     * @return
     * @throws ClientException
     */
    public String mesSaveMaterialLotHis(List<MaterialLot> materialLots, String transId) throws ClientException {
        String errorMessage = "";
        try {
            List<MaterialLot> scmReportMLotList = Lists.newArrayList();
            for(MaterialLot materialLot : materialLots){
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, transId);
                materialLotHistoryRepository.save(history);

                if(!StringUtils.isNullOrEmpty(materialLot.getLotId()) && (MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(materialLot.getReserved7())
                        || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CLASSIFY_CP.equals(materialLot.getReserved7()))){
                    scmReportMLotList.add(materialLot);
                }
            }

            if(CollectionUtils.isNotEmpty(scmReportMLotList)){
                if(MaterialLot.TRANSTYPE_BIND_WORKORDER.equals(transId)){
                    scmService.sendMaterialStateReport(scmReportMLotList, MaterialLotStateReportRequestBody.ACTION_TYPE_PLAN, ThreadLocalContext.getSessionContext());
                } else if(MaterialLot.TRANSTYPE_UN_BIND_WORKORDER.equals(transId)){
                    scmService.sendMaterialStateReport(scmReportMLotList, MaterialLotStateReportRequestBody.ACTION_TYPE_UN_PLAN, ThreadLocalContext.getSessionContext());
                }
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        return errorMessage;
    }

    public String mesSaveMaterialLotUnitHis(List<MaterialLotUnit> materialLotUnitList, String transId) throws ClientException{
        String errorMessage = "";
        try {
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, transId);
                materialLotUnitHisRepository.save(materialLotUnitHistory);
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        return errorMessage;
    }

    public String mesReceiveRawMaterialAndSaveHis(List<MaterialLot> materialLotList, String transType) throws ClientException{
        String errorMessage = "";
        try {
            for(MaterialLot materialLot : materialLotList){
                materialLot = mmsService.getMLotByMLotId(materialLot.getMaterialLotId());
                materialLot = mmsService.changeMaterialLotState(materialLot,  MaterialEvent.EVENT_MES_RECEIVE, StringUtils.EMPTY);

                MaterialLotHistory materialLotHistory = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, transType);
                materialLotHistoryRepository.save(materialLotHistory);
            }
        } catch (Exception e){
            errorMessage = e.getMessage();
        }
        return errorMessage;
    }

    /**
     * HongKong仓接收物料批次（暂时只做接收入库，不对晶圆做eng验证，不写入中间表）
     * @param materialLotActions
     * @throws ClientException
     */
    public void hongKongMLotReceive(List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            for(MaterialLot materialLot : materialLots){
                Warehouse warehouse = new Warehouse();
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved13())){
                    warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                }
                if(warehouse == null){
                    throw new ClientParameterException(GcExceptions.WAREHOUSE_CANNOT_EMPTY);
                }
                String warehouseName = warehouse.getName();

                materialLotUnitService.receiveMLotWithUnit(materialLot, warehouseName);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取香港仓待出货的真空包信息
     * @param tableRrn
     * @param materialLotId
     * @return
     * @throws ClientException
     */
    public MaterialLot getHKWarehouseStockOutMLot(Long tableRrn, String materialLotId) throws ClientException{
        try {
            MaterialLot materialLot = new MaterialLot();
            List<MaterialLot> materialLots= queryMaterialLotByTableRrnAndMaterialLotId(tableRrn, materialLotId);
            if(CollectionUtils.isNotEmpty(materialLots)){
                materialLot = materialLots.get(0);
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 香港仓出货验证物料批次基础信息
     * @param materialLot
     * @param materialLotActions
     * @return
     * @throws ClientException
     */
    public boolean validationHKStockOutMaterialLot(MaterialLot materialLot,  List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            boolean falg = true;
            falg = validationMaterialLotInfo(materialLot, materialLotActions, falg);
            return falg;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 香港仓依订单出货
     * 只允许箱号出货（真空包不允许）
     * @param documentLine
     * @param materialLotActions
     * @throws ClientException
     */
    public void hongKongWarehouseByOrderStockOut(DocumentLine documentLine, List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            String threeSideTransaction = documentLine.getThreeSideTransaction();
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            List<MaterialLot> unPackMaterialLot = materialLots.stream().filter(materialLot -> StringUtils.isNullOrEmpty(materialLot.getPackageType())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(unPackMaterialLot)){
                throw new ClientParameterException(GcExceptions.MATERIAL_LOT_IS_NOT_PACKED, unPackMaterialLot.get(0).getMaterialLotId());
            }
            Long totalMaterialLotQty = materialLots.stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentSubQty().longValue()));
            if(documentLine.getUnHandledQty().longValue() < totalMaterialLotQty){
                throw new ClientException(GcExceptions.OVER_DOC_QTY);
            }
            documentLine = (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());
            for(MaterialLot materialLot : materialLots){
                validateMLotAndDocLineByRule(documentLine, materialLot, MaterialLot.HKWAREHOUSE_BY_ORDER_STOCK_OUT_RULE_ID);
            }
            //如果单据客户代码是C1001，C2837则对物料批次进行转仓，否则做出货
            if(!StringUtils.isNullOrEmpty(threeSideTransaction) && (DocumentLine.CUSCODE_C1001.equals(threeSideTransaction) || DocumentLine.CUSCODE_C2837.equals(threeSideTransaction))){
                Warehouse warehouse = new Warehouse();
                String location = StringUtils.EMPTY;
                if(DocumentLine.CUSCODE_C1001.equals(threeSideTransaction)){
                    List<Warehouse> warehouseList = warehouseRepository.findByNameAndOrgRrn(WAREHOUSE_SH, ThreadLocalContext.getOrgRrn());
                    warehouse = warehouseList.get(0);
                    location = BONDED_PROPERTITY_SH;
                } else {
                    List<Warehouse> warehouseList = warehouseRepository.findByNameAndOrgRrn(WAREHOUSE_ZJ, ThreadLocalContext.getOrgRrn());
                    warehouse = warehouseList.get(0);
                    location = BONDED_PROPERTITY_ZSH;
                }

                for(MaterialLot materialLot: materialLots){
                    //状态恢复为待接收，仓库信息修改为GCSH或者GCZJ，修改报税属性，清空库位
                    mmsService.changeMaterialLotState(materialLot,  MaterialEvent.EVENT_TRANSFER, StringUtils.EMPTY);
                    updateWarehouseAndSaveHis(materialLot, warehouse, location);

                    List<MaterialLot> packedDetial = materialLotRepository.getPackageDetailLots(materialLot.getObjectRrn());
                    for(MaterialLot packedMLot : packedDetial){
                        updateWarehouseAndSaveHis(packedMLot, warehouse, location);
                    }
                }
            } else {
                for (MaterialLot materialLot : materialLots) {
                    materialLot.setCurrentSubQty(BigDecimal.ZERO);
                    if (StringUtils.isNullOrEmpty(materialLot.getReserved12())) {
                        materialLot.setReserved12(documentLine.getObjectRrn().toString());
                    } else {
                        materialLot.setReserved12(materialLot.getReserved12() + StringUtils.SEMICOLON_CODE + documentLine.getObjectRrn().toString());
                    }
                    changeMaterialLotStatusAndSaveHistory(materialLot);

                    List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                    if(CollectionUtils.isNotEmpty(packageDetailLots)){
                        for (MaterialLot packageLot : packageDetailLots){
                            changeMaterialLotStatusAndSaveHistory(packageLot);
                        }
                    }
                }
            }

            BigDecimal handledQty = new BigDecimal(totalMaterialLotQty);
            BigDecimal unHandleQty =  documentLine.getUnHandledQty().subtract(handledQty);
            documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
            documentLine.setUnHandledQty(unHandleQty);
            documentLine = documentLineRepository.saveAndFlush(documentLine);
            baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_SHIP);

            // 获取到主单据
            OtherStockOutOrder otherStockOutOrder = (OtherStockOutOrder) otherStockOutOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            otherStockOutOrder.setHandledQty(otherStockOutOrder.getHandledQty().add(handledQty));
            otherStockOutOrder.setUnHandledQty(otherStockOutOrder.getUnHandledQty().subtract(handledQty));
            otherStockOutOrder = otherStockOutOrderRepository.saveAndFlush(otherStockOutOrder);
            baseService.saveHistoryEntity(otherStockOutOrder, MaterialLotHistory.TRANS_TYPE_SHIP);

            validateAndUpdateErpSoa(documentLine, handledQty);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改箱号仓库信息并报错历史
     * @param materialLot
     * @param warehouse
     * @param location
     * @throws ClientException
     */
    private void updateWarehouseAndSaveHis(MaterialLot materialLot, Warehouse warehouse, String location) throws ClientException{
        try {
            materialLot.setReserved13(warehouse.getObjectRrn().toString());
            materialLot.setReserved6(location);
            materialLot.setReserved14(StringUtils.EMPTY);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_TRANSFER_WAREHOUSE);
            materialLotHistoryRepository.save(history);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * FT出货，出货单据（ETM_SOA）
     * @param materialLotActions
     * @param documentLineList
     * @throws ClientException
     */
    public void ftStockOut(List<MaterialLotAction> materialLotActions, List<DocumentLine> documentLineList) throws ClientException{
        try {
            documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByMLotDocRule(materialLots, MaterialLot.FT_STOCK_OUT_DOC_VALIDATE_RULE_ID);
            Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMLotDocRule(documentLineList, MaterialLot.FT_STOCK_OUT_DOC_VALIDATE_RULE_ID);
            for (String key : materialLotMap.keySet()) {
                if (!documentLineMap.keySet().contains(key)) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLotMap.get(key).get(0).getMaterialLotId());
                }
                Long totalMaterialLotQty = materialLotMap.get(key).stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentQty().longValue()));
                Long totalUnhandledQty = documentLineMap.get(key).stream().collect(Collectors.summingLong(documentLine -> documentLine.getUnHandledQty().longValue()));
                if (totalMaterialLotQty.compareTo(totalUnhandledQty) > 0) {
                    throw new ClientException(GcExceptions.OVER_DOC_QTY);
                }
                materialLotStockOutByErpSoa(documentLineMap.get(key), materialLotMap.get(key));
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次通过ERP_SOA单据出货
     * @param documentLines
     * @param materialLots
     * @throws ClientException
     */
    private void materialLotStockOutByErpSoa(List<DocumentLine> documentLines, List<MaterialLot> materialLots) throws ClientException{
        try {
            documentLines = vlidateDocMergeAndSortDocumentLinesBySeq(documentLines);
            for(DocumentLine documentLine : documentLines){
                BigDecimal unhandedQty = documentLine.getUnHandledQty();
                Iterator<MaterialLot> iterator = materialLots.iterator();
                while (iterator.hasNext()) {
                    MaterialLot materialLot = iterator.next();
                    BigDecimal currentSubQty = materialLot.getCurrentSubQty();
                    if (unhandedQty.compareTo(currentSubQty) >= 0) {
                        unhandedQty = unhandedQty.subtract(currentSubQty);
                        currentSubQty = BigDecimal.ZERO;
                    } else {
                        currentSubQty = currentSubQty.subtract(unhandedQty);
                        unhandedQty = BigDecimal.ZERO;
                    }
                    materialLot.setCurrentSubQty(currentSubQty);
                    if (materialLot.getCurrentSubQty().compareTo(BigDecimal.ZERO) == 0) {
                        saveDocLineRrnAndChangeStatus(materialLot, documentLine);
                        iterator.remove();
                    }
                    if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }

                BigDecimal handledQty = documentLine.getUnHandledQty().subtract(unhandedQty);
                if(handledQty.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                } else {
                    documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
                    documentLine.setUnHandledQty(unhandedQty);
                    documentLine = documentLineRepository.saveAndFlush(documentLine);
                    baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_SHIP);

                    // 获取到主单据
                    OtherStockOutOrder otherStockOutOrder = (OtherStockOutOrder) otherStockOutOrderRepository.findByObjectRrn(documentLine.getDocRrn());
                    otherStockOutOrder.setHandledQty(otherStockOutOrder.getHandledQty().add(handledQty));
                    otherStockOutOrder.setUnHandledQty(otherStockOutOrder.getUnHandledQty().subtract(handledQty));
                    otherStockOutOrder = otherStockOutOrderRepository.saveAndFlush(otherStockOutOrder);
                    baseService.saveHistoryEntity(otherStockOutOrder, MaterialLotHistory.TRANS_TYPE_SHIP);

                    validateAndUpdateErpSoa(documentLine, handledQty);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 改变包装批次的状态及记录历史
     * @param packageDetailLots
     * @throws ClientException
     */
    private void changPackageDetailLotStatusAndSaveHis(List<MaterialLot> packageDetailLots) throws ClientException{
        try {
            for (MaterialLot packageLot : packageDetailLots){
                changeMaterialLotStatusAndSaveHistory(packageLot);
                List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(packageLot.getMaterialLotId());
                if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                    for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                        materialLotUnit.setState(MaterialLotUnit.STATE_OUT);
                        materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                        MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_STOCK_OUT);
                        materialLotUnitHisRepository.save(materialLotUnitHistory);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证并接收COG来料信息
     * @param documentLineList
     * @param materialLotActionList
     * @throws ClientException
     */
    public void validateAndReceiveCogMLot(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActionList) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActionList.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMLotDocRule(documentLineList, MaterialLot.COG_MLOT_RECEIVE_DOC_VALIDATE_RULE_ID);
            Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByMLotDocRule(materialLots, MaterialLot.COG_MLOT_RECEIVE_DOC_VALIDATE_RULE_ID);
            for (String key : materialLotMap.keySet()) {
                if (!documentLineMap.keySet().contains(key)) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLotMap.get(key).get(0).getMaterialLotId());
                }
                Long totalMLotQty = materialLotMap.get(key).stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentQty().longValue()));
                Long totalDocUnhandledQty = documentLineMap.get(key).stream().collect(Collectors.summingLong(documentLine -> documentLine.getUnHandledQty().longValue()));
                if (totalMLotQty.compareTo(totalDocUnhandledQty) > 0) {
                    throw new ClientException(GcExceptions.OVER_DOC_QTY);
                }
                receiveCogMLot(documentLineMap.get(key), materialLotMap.get(key));
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收COG来料
     * @param documentLines
     * @param materialLots
     * @throws ClientException
     */
    private void receiveCogMLot(List<DocumentLine> documentLines, List<MaterialLot> materialLots) throws ClientException{
        try {
            documentLines = vlidateDocMergeAndSortDocumentLinesBySeq(documentLines);
            for (DocumentLine documentLine: documentLines) {
                BigDecimal unhandedQty = documentLine.getUnHandledQty();
                Iterator<MaterialLot> iterator = materialLots.iterator();

                Map<String, BigDecimal> mLotQty = Maps.newHashMap();
                for(MaterialLot materialLot : materialLots) {
                    mLotQty.put(materialLot.getMaterialLotId(), materialLot.getCurrentQty());
                }

                while (iterator.hasNext()) {
                    MaterialLot materialLot = iterator.next();
                    if (StringUtils.isNullOrEmpty(materialLot.getReserved12())) {
                        materialLot.setReserved12(documentLine.getObjectRrn().toString());
                    } else {
                        materialLot.setReserved12(materialLot.getReserved12() + StringUtils.SEMICOLON_CODE + documentLine.getObjectRrn().toString());
                    }
                    BigDecimal currentQty = materialLot.getCurrentQty();
                    if (unhandedQty.compareTo(currentQty) >= 0) {
                        unhandedQty = unhandedQty.subtract(currentQty);
                        currentQty =  BigDecimal.ZERO;
                    } else {
                        currentQty = currentQty.subtract(unhandedQty);
                        unhandedQty = BigDecimal.ZERO;
                    }
                    materialLot.setCurrentQty(currentQty);
                    if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                        //数量进行还原。不能扣减。
                        materialLot.setCurrentQty(mLotQty.get(materialLot.getMaterialLotId()));
                        materialLotUnitService.receiveMLotWithUnit(materialLot, WAREHOUSE_ZJ);
                        iterator.remove();
                    }
                    if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }
                BigDecimal handledQty = documentLine.getUnHandledQty().subtract(unhandedQty);
                if(handledQty.compareTo(BigDecimal.ZERO) == 0){
                    break;
                } else {
                    documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
                    documentLine.setUnHandledQty(unhandedQty);
                    documentLine = documentLineRepository.saveAndFlush(documentLine);
                    baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_RECEIVE);

                    CogReceiveOrder cogReceiveOrder = (CogReceiveOrder) cogReceiveOrderRepository.findByObjectRrn(documentLine.getDocRrn());
                    cogReceiveOrder.setHandledQty(cogReceiveOrder.getHandledQty().add(handledQty));
                    cogReceiveOrder.setUnHandledQty(cogReceiveOrder.getUnHandledQty().subtract(handledQty));
                    cogReceiveOrder = cogReceiveOrderRepository.saveAndFlush(cogReceiveOrder);
                    baseService.saveHistoryEntity(cogReceiveOrder, MaterialLotHistory.TRANS_TYPE_RECEIVE);

                    validateDocAndUpdateErpSo(documentLine, handledQty);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证单据是否时合单单据，并更新中间表单据数量信息
     * @param documentLine
     * @param handledQty
     */
    private void validateDocAndUpdateErpSo(DocumentLine documentLine, BigDecimal handledQty) throws ClientException{
        try {
            if(StringUtils.isNullOrEmpty(documentLine.getMergeDoc())){
                Optional<ErpSo> erpSoOptional = erpSoRepository.findById(Long.valueOf(documentLine.getReserved1()));
                if (!erpSoOptional.isPresent()) {
                    throw new ClientParameterException(GcExceptions.ERP_RECEIVE_ORDER_IS_NOT_EXIST, documentLine.getReserved1());
                }

                ErpSo erpSo = erpSoOptional.get();
                erpSo.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
                erpSo.setLeftNum(erpSo.getLeftNum().subtract(handledQty));
                if (StringUtils.isNullOrEmpty(erpSo.getDeliveredNum())) {
                    erpSo.setDeliveredNum(handledQty.toPlainString());
                } else {
                    BigDecimal docHandledQty = new BigDecimal(erpSo.getDeliveredNum());
                    docHandledQty = docHandledQty.add(handledQty);
                    erpSo.setDeliveredNum(docHandledQty.toPlainString());
                }
                erpSoRepository.save(erpSo);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存未确认晶圆追踪设置信息
     * @param unConfirmWaferSet
     * @param transType
     * @return
     * @throws ClientException
     */
    public GcUnConfirmWaferSet saveUnConfirmWaferTrackSetInfo(GcUnConfirmWaferSet unConfirmWaferSet, String transType) throws ClientException{
        try {
            unConfirmWaferSet = unConfirmWaferSetRepository.saveAndFlush(unConfirmWaferSet);

            GCUnConfirmWaferSetHis unConfirmWaferSetHis = (GCUnConfirmWaferSetHis) baseService.buildHistoryBean(unConfirmWaferSet, transType);
            unConfirmWaferSetHisRepository.save(unConfirmWaferSetHis);
            return unConfirmWaferSet;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收RMA物料批次
     * @param materialLotActionList
     * @throws ClientException
     */
    public List<MaterialLot> receiveRmaMLot(List<MaterialLotAction> materialLotActionList) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActionList.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            for (MaterialLot materialLot : materialLots){
                Warehouse warehouse = new Warehouse();
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved13())){
                    warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                }
                if(warehouse == null){
                    throw new ClientParameterException(GcExceptions.WAREHOUSE_CANNOT_EMPTY);
                }
                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
                materialLotAction.setTransCount(materialLot.getCurrentSubQty());
                materialLotAction.setTransQty(materialLot.getCurrentQty());
                mmsService.stockIn(materialLot, materialLotAction);

                List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
                for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                    materialLotUnit.setState(MaterialLotUnit.STATE_IN);
                    materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                    MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_IN);
                    materialLotUnitHisRepository.save(history);
                }
            }
            return materialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取RMA真空包打印参数
     * @param materialLots
     * @return
     * @throws ClientException
     */
    public List<Map<String, String>> getRmaLabelPrintParameter(List<MaterialLot> materialLots) throws ClientException{
        try {
             List<Map<String, String>> parameterMapList = Lists.newArrayList();
             for(MaterialLot materialLot : materialLots){
                 Map<String, String> parameterMap = Maps.newHashMap();
                 parameterMap.put("BOXID", materialLot.getMaterialLotId());
                 parameterMap.put("PRODUCTID", materialLot.getMaterialName());
                 parameterMap.put("GRADE", materialLot.getGrade() + StringUtils.PARAMETER_CODE + materialLot.getCurrentQty());
                 parameterMap.put("LOCATION", materialLot.getReserved6());
                 parameterMap.put("SUBCODE", materialLot.getReserved1());
                 parameterMap.put("PASSDIES", materialLot.getReserved34());
                 parameterMap.put("NGDIES", materialLot.getReserved35());

                 parameterMapList.add(parameterMap);
             }
             return parameterMapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COB装箱箱标签参数获取(一箱只有一包)
     * @param materialLotId
     * @return
     * @throws ClientException
     */
    public Map<String, String> getCOBLabelPrintParamater(String materialLotId) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId);
            parameterMap.put("BOXID", materialLotId);
            parameterMap.put("SUBCODE", materialLot.getReserved1());
            parameterMap.put("LOCATION", materialLot.getReserved6());
            parameterMap.put("DEVICEID", materialLot.getMaterialName());
            parameterMap.put("CHIPNUM", materialLot.getCurrentQty().toPlainString());

            List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
            if(CollectionUtils.isNotEmpty(packageDetailLots)){
                //COB箱号，一箱只装一个真空包
                MaterialLot packedLot = packageDetailLots.get(0);
                parameterMap.put("CSTID", packedLot.getLotId());
                parameterMap.put("FRAMEQTY", packedLot.getCurrentSubQty().toPlainString());

                List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(packedLot.getMaterialLotId());

                if(CollectionUtils.isNotEmpty(materialLotUnitList) && materialLotUnitList.size() > 13){
                    throw new ClientParameterException(GcExceptions.MATERIALLOT_WAFER_QTY_MORE_THAN_THIRTEEN, materialLotId);
                }

                int i = 1;
                if (CollectionUtils.isNotEmpty(materialLotUnitList)){
                    for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                        parameterMap.put("FRAMEID" + i, materialLotUnit.getUnitId());
                        parameterMap.put("CHIPQTY" + i, materialLotUnit.getCurrentQty().toPlainString());
                        i++;
                    }
                }

                for (int j = i; j <= 13; j++) {
                    parameterMap.put("FRAMEID" + j, StringUtils.EMPTY);
                    parameterMap.put("CHIPQTY" + j, StringUtils.EMPTY);
                }
            } else {
                throw new ClientParameterException(GcExceptions.MATERIALLOT_PACKED_DETIAL_IS_NULL, materialLotId);
            }
            return parameterMap;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 原材料接收
     * 写入中间表MTE_MATERIAL_IN
     * @param materialLotList
     * @throws ClientException
     */
    public void receiveRawMaterial(List<MaterialLot> materialLotList) throws ClientException{
        try {
            SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd");
            String ddate = formats.format(new Date());
            for(MaterialLot materialLot : materialLotList){
                Warehouse warehouse = new Warehouse();
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved13())){
                    warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                }
                if(warehouse == null){
                    throw new ClientParameterException(GcExceptions.WAREHOUSE_CANNOT_EMPTY);
                }
                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
                materialLotAction.setTransQty(materialLot.getCurrentQty());
                materialLotAction.setTransCount(materialLot.getCurrentSubQty());
                mmsService.stockIn(materialLot, materialLotAction);

                ErpMaterialIn erpMaterialIn = new ErpMaterialIn();
                erpMaterialIn.setMaterialLot(materialLot);
                erpMaterialInRepository.save(erpMaterialIn);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 原材料报废操作
     * @param materialLotList
     * @param reason
     * @param remarks
     * @throws ClientException
     */
    public void scrapRawMaterial(List<MaterialLot> materialLotList, String reason, String remarks) throws ClientException{
        try {
            for(MaterialLot materialLot: materialLotList){
                mmsService.changeMaterialLotState(materialLot,  MaterialEvent.EVENT_SCRAP, StringUtils.EMPTY);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_RAW_SCRAP);
                history.setActionReason(reason);
                history.setActionComment(remarks);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * WLT/CP晶圆无订单发料
     */
    public void waferOutOrderIssue(List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());

            SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd");
            String ddate = formats.format(new Date());
            //写数据到中间表etm_material_out表中，WLA/CP一个lot写入一条，FT的一个晶圆写入一条
            for(MaterialLot materialLot: materialLots){
                String importType = materialLot.getReserved49();
                if(MaterialLot.IMPORT_SENSOR_CP.equals(importType) || MaterialLot.IMPORT_LCD_CP.equals(importType) || MaterialLot.IMPORT_WLA.equals(importType)){
                    ErpMaterialOut erpMaterialOut = new ErpMaterialOut();
                    erpMaterialOut.setCmocode(materialLot.getLotId());
                    erpMaterialOut.setMaterialLot(materialLot);
                    if(materialLot.getCurrentSubQty().compareTo(BigDecimal.ZERO) > 0){
                        erpMaterialOut.setFqty(materialLot.getCurrentSubQty().toString());
                    }
                    if(materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) > 0){
                        erpMaterialOut.setMaterialQty(materialLot.getCurrentQty().toString());
                    }
                    erpMaterialOut.setCmaker(ThreadLocalContext.getUsername());
                    erpMaterialOut.setDmakerdate(ddate);
                    erpMaterialOutRepository.save(erpMaterialOut);
                } else if(MaterialLot.IMPORT_FT.equals(importType) || MaterialLot.IMPORT_SENSOR.equals(importType)){
                    List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
                    for(MaterialLotUnit materialLotUnit: materialLotUnitList){
                        ErpMaterialOut erpMaterialOut = new ErpMaterialOut();
                        erpMaterialOut.setMaterialLot(materialLot);
                        erpMaterialOut.setCmocode(materialLotUnit.getUnitId());
                        erpMaterialOut.setCmaker(ThreadLocalContext.getUsername());
                        erpMaterialOut.setFqty(ErpMaterialOut.DEFAULT_FQTY);
                        if(materialLotUnit.getCurrentQty().compareTo(BigDecimal.ZERO) > 0){
                            erpMaterialOut.setMaterialQty(materialLotUnit.getCurrentQty().toString());
                        }
                        erpMaterialOut.setDmakerdate(ddate);
                        erpMaterialOutRepository.save(erpMaterialOut);
                    }
                 }
            }

            waferIssueWithOutDocument(materialLots);

            //将晶圆信息保存至Mes backendWaferReceive表中
            mesService.saveBackendWaferReceive(materialLots);

            boolean waferIssueToMesPlanLot = SystemPropertyUtils.getWaferIssueToMesPlanLot();
            log.info("wafer issue to mes plan lot flag is " + waferIssueToMesPlanLot);
            if(waferIssueToMesPlanLot){
                log.info("wafer issue to mes plan lot start ");
                mesService.materialLotUnitPlanLot(materialLots, ThreadLocalContext.getSessionContext());
                log.info("wafer issue to mes plan lot end ");
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 原材料发料
     * @param documentLine
     * @param materialLotList
     * @throws ClientException
     */
    public void validateAndRawMaterialIssue(DocumentLine documentLine, List<MaterialLot> materialLotList) throws ClientException{
        try {
            documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());
            for (MaterialLot materialLot : materialLotList) {
                validateMLotAndDocLineByRule(documentLine, materialLot, MaterialLot.RAW_MATERIAL_ISSUE_DOC_VALIDATE_RULE_ID);
            }

            BigDecimal handledQty = BigDecimal.ZERO;
            for (MaterialLot materialLot : materialLotList) {
                handledQty = handledQty.add(materialLot.getCurrentQty());
                materialLot.setCurrentQty(BigDecimal.ZERO);
                if (StringUtils.isNullOrEmpty(materialLot.getReserved12())) {
                    materialLot.setReserved12(documentLine.getObjectRrn().toString());
                } else {
                    materialLot.setReserved12(materialLot.getReserved12() + StringUtils.SEMICOLON_CODE + documentLine.getObjectRrn().toString());
                }
                if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                    mmsService.changeMaterialLotState(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE, StringUtils.EMPTY);
                    materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
                } else {
                    List<MaterialLotInventory> materialLotInvList = mmsService.getMaterialLotInv(materialLot.getObjectRrn());
                    if (CollectionUtils.isNotEmpty(materialLotInvList)) {
                        MaterialLotInventory materialLotInventory = materialLotInvList.get(0);
                        materialLotInventory.setStockQty(materialLot.getCurrentQty());
                        materialLotInventoryRepository.save(materialLotInventory);
                    }
                }

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE);
                materialLotHistoryRepository.save(history);
            }

            BigDecimal unHandleQty =  documentLine.getUnHandledQty().subtract(handledQty);
            if (unHandleQty.compareTo(BigDecimal.ZERO) < 0) {
                throw new ClientParameterException(GcExceptions.OVER_DOC_QTY, documentLine.getDocId());
            }

            documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
            documentLine.setUnHandledQty(unHandleQty);
            documentLine = documentLineRepository.saveAndFlush(documentLine);
            baseService.saveHistoryEntity(documentLine, GCMaterialEvent.EVENT_WAFER_ISSUE);

            MaterialIssueOrder materialIssueOrder = (MaterialIssueOrder) materialIssueOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            materialIssueOrder.setHandledQty(materialIssueOrder.getHandledQty().add(handledQty));
            materialIssueOrder.setUnHandledQty(materialIssueOrder.getUnHandledQty().subtract(handledQty));
            materialIssueOrderRepository.save(materialIssueOrder);
            baseService.saveHistoryEntity(materialIssueOrder, GCMaterialEvent.EVENT_WAFER_ISSUE);

            Optional<ErpMaterialOutaOrder> erpMaterialOutAOrderOptional = erpMaterialOutAOrderRepository.findById(Long.valueOf(documentLine.getReserved1()));
            if(erpMaterialOutAOrderOptional.isPresent()) {
                ErpMaterialOutaOrder erpMOutAOrder = erpMaterialOutAOrderOptional.get();
                erpMOutAOrder.setLeftNum(erpMOutAOrder.getLeftNum().subtract(handledQty));
                erpMOutAOrder.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
                if (StringUtils.isNullOrEmpty(erpMOutAOrder.getDeliveredNum())) {
                    erpMOutAOrder.setDeliveredNum(handledQty.toPlainString());
                } else {
                    BigDecimal docHandledQty = new BigDecimal(erpMOutAOrder.getDeliveredNum());
                    docHandledQty = docHandledQty.add(handledQty);
                    erpMOutAOrder.setDeliveredNum(docHandledQty.toPlainString());
                }
                erpMaterialOutAOrderRepository.save(erpMOutAOrder);
            } else {
                throw new ClientParameterException(GcExceptions.ERP_RAW_MATERIAL_ISSUE_ORDER_IS_NOT_EXIST, documentLine.getReserved1());
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据物料合批规则验证物料批次信息是否一致
     * @param materialLot
     * @param materialLotActions
     * @return
     * @throws ClientException
     */
    public boolean validateMLotByPackageRule(MaterialLot materialLot,  List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            boolean flag = true;
            if (CollectionUtils.isNotEmpty(materialLotActions)) {
                List<MaterialLot> materialLotList = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
                materialLotList.add(materialLot);
                flag = mmsService.validationMLotByMergeRule(MaterialLot.WLT_SHIP_MLOT_MERGE_RULE, materialLotList);
            }
            return flag;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改Lot信息
     * @param materialLot
     * @throws ClientException
     */
    public void updateMaterialLotInfo(MaterialLot materialLot) throws ClientException{
        try {
            materialLot = materialLotRepository.saveAndFlush(materialLot);
            MaterialLotHistory materialLotHistory = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_UPDATE);
            materialLotHistoryRepository.save(materialLotHistory);

            List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
            if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                    materialLotUnit.setReserved4(materialLot.getReserved6());
                    materialLotUnit.setReserved22(materialLot.getReserved22());
                    materialLotUnit.setReserved25(materialLot.getReserved25());
                    materialLotUnit.setReserved1(materialLot.getReserved1());
                    materialLotUnit.setGrade(materialLot.getGrade());
                    materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                    MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotHistory.TRANS_TYPE_UPDATE);
                    materialLotUnitHisRepository.save(materialLotUnitHistory);
                }
            }

        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 单据合并
     * @param documentLines
     * @throws ClientException
     */
    public void valaidateAndMergeErpDocLine(List<DocumentLine> documentLines) throws ClientException {
        try {
            List<Long> seqList = Lists.newArrayList();
            //根据单据验证规则验证单据信息是否满足合批条件
            validationDocMergeRule(MLotDocRuleContext.MERGE_DOC_VALIDATE_RULE_ID, documentLines);

            //将所有的单据合并成一条documentLine单据
            Long totalDocQty = documentLines.stream().collect(Collectors.summingLong(documentLine -> documentLine.getQty().longValue()));
            DocumentLine documentLine = new DocumentLine();
            documentLine.setDocumentLine(documentLines.get(0));
            documentLine.setQty(new BigDecimal(totalDocQty));
            documentLine.setUnHandledQty(documentLine.getQty());
            documentLine.setUnReservedQty(documentLine.getQty());
            documentLine.setMergeDoc(DocumentLine.DOC_MERGE);
            documentLine = documentLineRepository.saveAndFlush(documentLine);

            baseService.saveHistoryEntity(documentLine, DocumentLineHistory.TRANS_TYPE_MERGE_DOC);

            for(DocumentLine docLine : documentLines){
                if(StringUtils.isNullOrEmpty(docLine.getReserved1())){
                    throw new ClientParameterException(GcExceptions.ERP_ORDER_CANNOT_EMPTY, documentLine.getDocId(), "seq" + documentLine.getReserved1());
                }
                seqList.add(Long.parseLong(docLine.getReserved1()));

                documentLineRepository.deleteById(docLine.getObjectRrn());
                baseService.saveHistoryEntity(docLine, DocumentLineHistory.TRANS_TYPE_DELETE);
            }

            //根据单据的类型更新中间表单据的状态
            String docType = documentLines.get(0).getReserved31();
            if(ErpSo.SOURCE_TABLE_NAME.equals(docType)){
                erpSoRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(DocumentLine.SYNC_STATUS_MERGE, DocumentLine.ERROR_MEMO, Document.SYNC_USER_ID, seqList);
            } else if(ErpSoa.SOURCE_TABLE_NAME.equals(docType)){
                erpSoaOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(DocumentLine.SYNC_STATUS_MERGE, DocumentLine.ERROR_MEMO, Document.SYNC_USER_ID, seqList);
            } else if(ErpSob.SOURCE_TABLE_NAME.equals(docType)){
                erpSobOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(DocumentLine.SYNC_STATUS_MERGE, DocumentLine.ERROR_MEMO, Document.SYNC_USER_ID, seqList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 合单规则验证
     * @param ruleName
     * @param documentLines
     * @throws ClientException
     */
    public void validationDocMergeRule(String ruleName, List<DocumentLine> documentLines) throws ClientException{
        try {
            List<MLotDocRule> mLotDocLineRule = mLotDocRuleRepository.findByNameAndOrgRrn(ruleName, ThreadLocalContext.getOrgRrn());
            if (CollectionUtils.isEmpty(mLotDocLineRule)) {
                throw new ClientParameterException(GcExceptions.DOCUMENT_LINE_MERGE_RULE_IS_NOE_EXIST, ruleName);
            }
            MLotDocRuleContext mLotDocRuleContext = new MLotDocRuleContext();
            mLotDocRuleContext.setSourceObject(documentLines.get(0));
            mLotDocRuleContext.setDocumentLineList(documentLines);
            mLotDocRuleContext.setMLotDocRuleLines(mLotDocLineRule.get(0).getLines());
            mLotDocRuleContext.validationDocMerge();
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取RW发料标签打印参数
     * @param materialLotList
     * @return
     */
    public List<Map<String,String>> getRWIssueMaterialLotPrintParameter(List<MaterialLot> materialLotList) throws ClientException{
        try {
            List<Map<String, String>> parameterMapList = Lists.newArrayList();
            SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
            String date = formatter.format(new Date());
            String innerLotInfo = MLotCodePrint.COMPANY_INITIALS_G + date;
            for(MaterialLot materialLot : materialLotList){
                Map<String, String> parameter = Maps.newHashMap();
                List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                parameter.put("CUSTOMER", MLotCodePrint.PRINT_GALAXYCORE);
                parameter.put("WAFERCOUNT", Integer.toString(materialLotUnitList.size()));
                parameter.put("MATERIALNAME", materialLot.getMaterialName());
                parameter.put("LOTID", materialLot.getLotId());
                parameter.put("PRODUCTID", materialLot.getMaterialName());
                parameter.put("INNERLOTID", materialLot.getInnerLotId());
                parameter.put("WAFERQTY", materialLot.getCurrentQty().toString());
                parameter.put("PLANTIME", materialLot.getWorkOrderPlanputTime());
                innerLotInfo = innerLotInfo + materialLot.getInnerLotId();
                parameter.put("INNERLOTINFO", innerLotInfo );

                parameterMapList.add(parameter);
            }
            return parameterMapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取RW产线接收Lot标签打印参数
     * @param materialLot
     * @return
     */
    public Map<String, String> getRwReceiveLotLabelPrintParameter(MaterialLot materialLot) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            String materialName = materialLot.getMaterialName();
            parameterMap.put("PRODUCTID", materialName.substring(0 , materialName.lastIndexOf("-")));
            parameterMap.put("CSTID", materialLot.getLotId());
            parameterMap.put("WAFERQTY", materialLot.getCurrentSubQty().toString());
            parameterMap.put("LOCATION", materialLot.getReserved6());
            parameterMap.put("SUBCODE", materialLot.getReserved1());
            parameterMap.put("LOIID", materialLot.getLotId());
            parameterMap.put("LOTCST", materialLot.getLotCst());
            parameterMap.put("PCODE", materialLot.getPcode());
            parameterMap.put("QTY", materialLot.getCurrentQty().toString());
            List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
            int i = 1;
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                parameterMap.put("frameID" + i, materialLotUnit.getUnitId());
                parameterMap.put("chipQty" + i, materialLotUnit.getCurrentQty().toString());
                i++;
            }
            for (int j = i; j <= 13; j++) {
                parameterMap.put("frameID" + j, StringUtils.EMPTY);
                parameterMap.put("chipQty" + j, StringUtils.EMPTY);
            }
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * 获取RW产线接收Lot标签打印参数
     * @param materialLotRrn
     * @return
     */
    public Map<String, String> getRWBoxPrintParameter(Long materialLotRrn) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            MaterialLot materialLot = mmsService.getMLotByObjectRrn(materialLotRrn);
            parameterMap.put("DeviceID", materialLot.getMaterialName());
            parameterMap.put("BoxID", materialLot.getMaterialLotId());
            parameterMap.put("Qty", materialLot.getCurrentQty().toPlainString());
            parameterMap.put("BP", materialLot.getReserved6());
            if(materialLot.getGrade().contains("A"))
            {
                parameterMap.put("SubCode", "A"+materialLot.getReserved1());
            }else
            {
                parameterMap.put("SubCode", materialLot.getGrade()+materialLot.getReserved1());
            }
            if(StringUtils.isNullOrEmpty(materialLot.getPcode()))
            {
                parameterMap.put("Pcode", StringUtils.EMPTY);
            }else
            {
                parameterMap.put("Pcode", materialLot.getPcode());
            }
            List<MaterialLot> materialLotDetails = materialLotRepository.getByParentMaterialLotId(materialLot.getMaterialLotId());
            if (CollectionUtils.isNotEmpty(materialLotDetails)) {
                MaterialLot materialLotDetail = materialLotDetails.get(0);
                if(StringUtils.isNullOrEmpty(materialLotDetail.getLotCst()))
                {
                    parameterMap.put("LotID", StringUtils.EMPTY);
                }else
                {
                    parameterMap.put("LotID", materialLotDetail.getLotCst());
                }
                parameterMap.put("CSTID", materialLotDetail.getLotId());
                List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLotDetail.getMaterialLotId());
                parameterMap.put("FrameQty", ""+materialLotUnitList.size());
                for(int i=0;i<materialLotUnitList.size();i++)
                {
                    parameterMap.put("FrameID" + i, "" + materialLotUnitList.get(i).getUnitId());
                    parameterMap.put("ChipQty" + i, "" + materialLotUnitList.get(i).getCurrentQty());
                }
                for(int j=materialLotUnitList.size();j < 13;j++)
                {
                    parameterMap.put("FrameID" + j, StringUtils.EMPTY);
                    parameterMap.put("ChipQty" + j, StringUtils.EMPTY);
                }
            }
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * RW批次标注自动挑选(按照先进先出的原则，先挑选装箱的)
     * @param materialLotList
     * @param pickQty
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> rwTagginggAutoPickMLot(List<MaterialLot> materialLotList, BigDecimal pickQty) throws ClientException{
        try {
            List<MaterialLot> pickMLotList = Lists.newArrayList();
            List<MaterialLot> packedMaterialLots = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())).collect(Collectors.toList());
            List<MaterialLot> unpackedMaterialLots = materialLotList.stream().filter(materialLot -> StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(packedMaterialLots)){
                packedMaterialLots = packedMaterialLots.stream().sorted(Comparator.comparing(MaterialLot::getCreated)).collect(Collectors.toList());
                Map<String, List<MaterialLot>> packedMLotMap = packedMaterialLots.stream().collect(Collectors.groupingBy(MaterialLot::getParentMaterialLotId));
                for(String parentMLotId : packedMLotMap.keySet()){
                    List<MaterialLot> materialLots = packedMLotMap.get(parentMLotId);
                    materialLots = materialLots.stream().sorted(Comparator.comparing(MaterialLot::getCreated)).collect(Collectors.toList());
                    for(MaterialLot materialLot : materialLots){
                        if(pickQty.compareTo(materialLot.getCurrentQty()) > 0){
                            pickMLotList.add(materialLot);
                            pickQty = pickQty.subtract(materialLot.getCurrentQty());
                        }
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(unpackedMaterialLots)){
                unpackedMaterialLots = unpackedMaterialLots.stream().sorted(Comparator.comparing(MaterialLot :: getCreated)).collect(Collectors.toList());
                for(MaterialLot materialLot : unpackedMaterialLots){
                    if(pickQty.compareTo(materialLot.getCurrentQty()) >= 0){
                        pickMLotList.add(materialLot);
                        pickQty = pickQty.subtract(materialLot.getCurrentQty());
                    }
                }
            }
            return pickMLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW出货标注
     * @param materialLotList
     * @param customerName
     * @param abbreviation
     * @param remarks
     * @throws ClientException
     */
    public void rwMaterialLotStockOutTag(List<MaterialLot> materialLotList, String customerName, String abbreviation, String remarks) throws ClientException{
        try {
            //验证装箱的Lot客户标识和客户简称是否一致，不一致不能标注
            Map<String, List<MaterialLot>> packedLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            for(String parentMaterialLotId : packedLotMap.keySet()){
                MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(parentMaterialLotId, ThreadLocalContext.getOrgRrn());
                validateMLotTagInfo(materialLot, customerName, abbreviation);

                saveMaterialLotTaggingInfoAndSaveHis(materialLot, customerName, abbreviation, remarks);
            }

            for(MaterialLot materialLot : materialLotList){
                saveMaterialLotTaggingInfoAndSaveHis(materialLot, customerName, abbreviation, remarks);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存标注信息并记录历史
     * @param materialLot
     * @param customerName
     * @param abbreviation
     * @param remarks
     * @throws ClientException
     */
    private void saveMaterialLotTaggingInfoAndSaveHis(MaterialLot materialLot, String customerName, String abbreviation, String remarks) throws ClientException{
        try {
            materialLot.setCustomerId(customerName);
            materialLot.setReserved55(abbreviation);
            materialLot.setReserved57(remarks);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_STOCK_OUT_TAG);
            materialLotHistoryRepository.save(history);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证箱中的Lot是否已经标注，如果已经标注，则验证客户标识与客户简称是否一致
     * @param materialLot
     * @param customerName
     * @param abbreviation
     * @throws ClientException
     */
    private void validateMLotTagInfo(MaterialLot materialLot, String customerName, String abbreviation) throws ClientException{
        try {
            List<MaterialLot> materialLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
            for(MaterialLot mLot : materialLots){
                if(!StringUtils.isNullOrEmpty(mLot.getReserved55()) && !abbreviation.equals(mLot.getReserved55())){
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_CUSTOMER_NAME_IS_NOT_SAME, materialLot.getMaterialLotId());
                }
                if(!StringUtils.isNullOrEmpty(mLot.getCustomerId()) && !customerName.equals(mLot.getCustomerId())){
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_ABBREVIATION_IS_NOT_SAME, materialLot.getMaterialLotId());
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次添加快递单号
     * 暂时将出货单号记录到reserved56栏位（于CP出货标注PO共用一个栏位）
     * @param materialLotList
     * @param shipOrderId
     * @throws ClientException
     */
    public void rwMaterialLotAddShipOrderId(List<MaterialLot> materialLotList, String shipOrderId) throws ClientException {
        try {
            Map<String, List<MaterialLot>> packedLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            for(String parentMLotId : packedLotMap.keySet()){
                MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(parentMLotId, ThreadLocalContext.getOrgRrn());
                List<MaterialLot> materialLots = packageService.getPackageDetailLots(materialLot.getObjectRrn()).stream().filter(mLot -> StringUtils.isNullOrEmpty(mLot.getReserved56())).collect(Collectors.toList());
                if(!shipOrderId.equals(materialLots.get(0).getReserved56())){
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_SHIP_ORDER_ID_IS_NOT_SAME, parentMLotId);
                }

                saveMaterialLotShipOrderIdAndSaveHis(materialLot, shipOrderId);
            }
            for(MaterialLot materialLot : materialLotList){
                saveMaterialLotShipOrderIdAndSaveHis(materialLot, shipOrderId);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW保存物料批次出货单号
     * @param materialLot
     * @param shipOrderId
     * @throws ClientException
     */
    private void saveMaterialLotShipOrderIdAndSaveHis(MaterialLot materialLot, String shipOrderId) throws ClientException{
        try {
            materialLot.setReserved56(shipOrderId);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_ADD_SHIP_ORDER_ID);
            materialLotHistoryRepository.save(history);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW取消出货标注
     * @param materialLots
     * @throws ClientException
     */
    public void rwMaterialLotCancelStockTag(List<MaterialLot> materialLots) throws ClientException {
        try {
            for(MaterialLot materialLot : materialLots){
                unTaggingMaterialLot(materialLot);
            }

            Map<String, List<MaterialLot>> packedLotMap = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            if (packedLotMap != null && packedLotMap.keySet().size() > 0) {
                for (String parentMaterialLotId : packedLotMap.keySet()) {
                    MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(parentMaterialLotId, ThreadLocalContext.getOrgRrn());
                    unTaggingMaterialLot(materialLot);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW出货
     * @param materialLotList
     * @param documentLineList
     * @throws ClientException
     */
    public void rwStockOut(List<MaterialLot> materialLotList, List<DocumentLine> documentLineList) throws ClientException{
        try {
            documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());
            materialLotList = materialLotList.stream().map(materialLot -> mmsService.getMLotByMLotId(materialLot.getMaterialLotId(), true)).collect(Collectors.toList());
            Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByMLotDocRule(materialLotList, MaterialLot.RW_MLOT_STOCK_OUT_DOC_VALIDATE_RULE_ID);
            Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMLotDocRule(documentLineList, MaterialLot.RW_MLOT_STOCK_OUT_DOC_VALIDATE_RULE_ID);
            for (String key : materialLotMap.keySet()) {
                if (!documentLineMap.keySet().contains(key)) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLotMap.get(key).get(0).getMaterialLotId());
                }
                Long totalMaterialLotQty = materialLotMap.get(key).stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentSubQty().longValue()));
                Long totalUnhandledQty = documentLineMap.get(key).stream().collect(Collectors.summingLong(documentLine -> documentLine.getUnHandledQty().longValue()));
                if (totalMaterialLotQty.compareTo(totalUnhandledQty) > 0) {
                    throw new ClientParameterException(GcExceptions.OVER_DOC_QTY, documentLineMap.get(key).get(0).getDocId());
                }
                materialLotStockOutByErpSoa(documentLineMap.get(key), materialLotMap.get(key));
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据二维码信息获取辅料信息
     * 固定供应商Lintec(reserved22)、二维码第一段为供应商信息码（reserved23）
     * @param tapeMaterialCode
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getMaterialLotByTapeMaterialCode(String tapeMaterialCode) throws ClientException {
        try {
            List<MaterialLot> materialLotList = Lists.newArrayList();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd");
            String[] tepaArray = tapeMaterialCode.split(" ");
            if(tepaArray.length < 6 || tepaArray[3].length() < 24 || tepaArray[4].length() < 27 || tepaArray[5].length() < 16){
                throw new ClientParameterException(GcExceptions.TAPA_MATERIAL_CODE_IS_ERROR, tapeMaterialCode);
            }
            String materialName = tepaArray[1] + " " + tepaArray[2];
            Material material = mmsService.getRawMaterialByName(materialName);
            if(material == null || !Material.MATERIAL_TYPE_TAPE.equals(material.getMaterialType())){
                throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, materialName);
            }

            String tapeSize = tepaArray[3].substring(2, 5) + "mm*" + tepaArray[3].substring(13, 16) + "mm*" + tepaArray[3].substring(21, 24) + "s";
            String mfgDate = formats.format(simpleDateFormat.parse(tepaArray[5].substring(0, 8)));
            String expDate = formats.format(simpleDateFormat.parse(tepaArray[5].substring(8, 16)));

            String materialLotIdList = tepaArray[4].substring(17);
            while (materialLotIdList.length() >= 10) {
                MaterialLot materialLot = new MaterialLot();
                String materialLotId = materialLotIdList.substring(0, 10);
                MaterialLot oldMaterialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLotId, ThreadLocalContext.getOrgRrn());
                if(oldMaterialLot != null){
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, materialLotId);
                }
                materialLot.setMaterial(material);
                materialLot.setCurrentQty(BigDecimal.ONE);
                materialLot.setCurrentSubQty(BigDecimal.ONE);
                materialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
                materialLot.setStatus(MaterialStatus.STATUS_CREATE);
                materialLot.setStatusModelRrn(material.getStatusModelRrn());
                materialLot.initialMaterialLot();
                materialLot.setMaterialLotId(materialLotId);
                materialLot.setReserved2(tapeSize);
                materialLot.setReserved22(Material.MATERIAL_SHIPPER_NAME);
                materialLot.setReserved23(tepaArray[0]);
                materialLot.setReserved27(Material.MATERIAL_PO_EMPTY);
                materialLot.setReserved49(Material.MATERIAL_TYPE_TAPE);
                materialLot.setMfgDate(formats.parse(mfgDate));
                materialLot.setExpDate(formats.parse(expDate));
                materialLot.setTapeMaterialCode(tapeMaterialCode);

                materialLotList.add(materialLot);

                materialLotIdList = materialLotIdList.substring(10);
            }
            return materialLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * Tape辅料接收入库
     * @param materialLotList
     * @throws ClientException
     */
    public void receiveTapeMaterial(List<MaterialLot> materialLotList, String tapeSize) throws ClientException{
        try {
            Warehouse warehouse = mmsService.getWarehouseByName(WAREHOUSE_ZJ);
            Map<String, List<MaterialLot>> tapeMaterialMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot :: getTapeMaterialCode));
            for(String tapeCode : tapeMaterialMap.keySet()){
                String lotId = generatorMLotsTransId(MaterialLot.TAPE_MATERIAL_LOT_ID_RULE);
                List<MaterialLot> materialLots = tapeMaterialMap.get(tapeCode);
                for(MaterialLot materialLot : materialLots){
                    materialLot.setLotId(lotId);
                    materialLot.setReserved13(warehouse.getObjectRrn().toString());
                    materialLot.setReserved24(tapeSize);
                    materialLot = materialLotRepository.saveAndFlush(materialLot);

                    // 记录历史
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE);
                    materialLotHistoryRepository.save(history);

                    materialStockAndSaveToErpMaterialLotIn(materialLot, warehouse);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 原材料接收并保存至中间表
     * @param materialLot
     * @param warehouse
     * @throws ClientException
     */
    private void materialStockAndSaveToErpMaterialLotIn(MaterialLot materialLot, Warehouse warehouse) throws ClientException{
        try {
            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
            materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
            materialLotAction.setTransQty(materialLot.getCurrentQty());
            materialLotAction.setTransCount(materialLot.getCurrentSubQty());
            mmsService.stockIn(materialLot, materialLotAction);

            ErpMaterialIn erpMaterialIn = new ErpMaterialIn();
            erpMaterialIn.setMaterialLot(materialLot);
            erpMaterialInRepository.save(erpMaterialIn);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * Blade原材料查询
     * @param bladeMaterialCode
     * @return
     * @throws ClientException
     */
    public MaterialLot getMaterialLotByBladeMaterialCode(String bladeMaterialCode) throws ClientException{
        try {
            MaterialLot materialLot = new MaterialLot();
            Material material = mmsService.getRawMaterialByName(bladeMaterialCode);
            if(material == null || !Material.MATERIAL_TYPE_BLADE.equals(material.getMaterialType())){
                throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, bladeMaterialCode);
            }
            materialLot.setMaterial(material);
            materialLot.setCurrentQty(BigDecimal.ONE);
            materialLot.setCurrentSubQty(BigDecimal.ONE);
            materialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
            materialLot.setStatus(MaterialStatus.STATUS_CREATE);
            materialLot.setStatusModelRrn(material.getStatusModelRrn());
            materialLot.initialMaterialLot();

            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证并获取Blade原材料批次号
     * @param bladeMaterialLotCode
     * @return
     * @throws ClientException
     */
    public String validateAndGetBladeMLotId(String bladeMaterialLotCode) throws ClientException{
        try {
            if(bladeMaterialLotCode.length() < 14){
                throw new ClientParameterException(GcExceptions.BLADE_MATERIAL_CODE_IS_ERROR, bladeMaterialLotCode);
            }
            String materialLotId = bladeMaterialLotCode.substring(bladeMaterialLotCode.length() - 14);
            MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLotId, ThreadLocalContext.getOrgRrn());
            if(materialLot != null){
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, materialLotId);
            }
            return materialLotId;
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收Blade原材料
     * 默认存在浙江仓库
     * @param materialLotList
     * @throws ClientException
     */
    public void receiveBladeMaterial(List<MaterialLot> materialLotList) throws ClientException{
        try {
            Warehouse warehouse = mmsService.getWarehouseByName(WAREHOUSE_ZJ);
            //有效日期增加100年
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.YEAR, 100);

            for(MaterialLot materialLot : materialLotList){
                materialLot.setObjectRrn(null);
                materialLot.setReserved13(warehouse.getObjectRrn().toString());
                materialLot.setReserved22(Material.MATERIAL_DISCO);
                materialLot.setReserved27(Material.MATERIAL_PO_EMPTY);
                materialLot.setReserved49(Material.MATERIAL_TYPE_BLADE);
                materialLot.setMfgDate(new Date());
                materialLot.setExpDate(cal.getTime());
                materialLot = materialLotRepository.saveAndFlush(materialLot);

                // 记录历史
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE);
                materialLotHistoryRepository.save(history);

                materialStockAndSaveToErpMaterialLotIn(materialLot, warehouse);
            }
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW材料备料
     * @param materialLotList
     * @throws ClientException
     */
    public void spareRwMaterial(List<MaterialLot> materialLotList) throws ClientException{
        try {
            for(MaterialLot materialLot: materialLotList){
                materialLot = mmsService.changeMaterialLotState(materialLot,  MaterialEvent.EVENT_MATEREIAL_SPARE, StringUtils.EMPTY);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_MATERIAL_SPARE);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW原材料发料
     * @param materialLotList
     * @throws ClientException
     */
    public void issueRwMaterial(List<MaterialLot> materialLotList) throws ClientException{
        try {
            for(MaterialLot materialLot : materialLotList){
                mmsService.changeMaterialLotState(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE, StringUtils.EMPTY);
                materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

}
