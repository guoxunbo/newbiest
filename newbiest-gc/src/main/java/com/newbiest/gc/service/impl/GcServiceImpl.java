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
import com.newbiest.gc.service.GcService;
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
import com.newbiest.mms.state.model.MaterialStatusCategory;
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
    public static final String TRANS_TYPE_UPDATE_LOCAYTION = "UpdateLocation";
    public static final String TRANS_TYPE_HOLD = "Hold";
    public static final String TRANS_TYPE_RELEASE = "Release";

    public static final String REFERENCE_NAME_STOCK_OUT_CHECK_ITEM_LIST = "StockOutCheckItemList";
    public static final String REFERENCE_NAME_WLTSTOCK_OUT_CHECK_ITEM_LIST = "WltStockOutCheckItemList";
    public static final String REFERENCE_NAME_PACK_CASE_CHECK_ITEM_LIST = "PackCaseCheckItemList";
    public static final String REFERENCE_NAME_WLTPACK_CASE_CHECK_ITEM_LIST = "WltPackCaseCheckItemList";

    public static final String EVENT_OQC = "OQC";

    public static final String WAREHOUSE_SH = "SH_STOCK";
    public static final String WAREHOUSE_ZJ = "ZJ_STOCK";
    public static final String WAREHOUSE_HK = "HK_STOCK";

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
    DocumentLineRepository documentLineRepository;

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
    PackagedLotDetailRepository packagedLotDetailRepository;

    @Autowired
    GCLcdCogDetialRepository gcLcdCogDetialRepository;

    @Autowired
    GCLcdCogDetialHisRepository gcLcdCogDetialHisRepository;

    @Autowired
    ErpInStockRepository erpInStockRepository;

    @Autowired
    ErpMoaRepository erpMoaRepository;

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
                            validationDocLine(documentLine, materialLot);
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
            DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLineRrn);
            BigDecimal unReservedQty = documentLine.getUnReservedQty();
            BigDecimal reservedQty = BigDecimal.ZERO;
            for (MaterialLot materialLot : materialLots) {
                if (!StringUtils.isNullOrEmpty(materialLot.getReserved16())) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_RESERVED_BY_ANOTHER);
                }
                validationDocLine(documentLine, materialLot);
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
                materialLot.setShipper(documentLine.getReserved8());
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
                parentMLot.setShipper(documentLine.getReserved8());
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
     * 获取到可以入库的批次
     *  当前只验证了物料批次是否是完结
     * @param materialLotId
     * @return
     */
    public MaterialLot getWaitStockInStorageMaterialLot(String materialLotId) throws ClientException {
        try {
            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId, true);
            materialLot.isFinish();
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取到可以入库的批次
     *  当前只验证了物料批次是否是完结
     * @param lotId
     * @return
     */
    public MaterialLot getWaitStockInStorageWaferByLotId(String lotId) throws ClientException {
        try {
            MaterialLot materialLot = materialLotRepository.findByLotIdAndReserved7NotIn(lotId, MaterialLotUnit.PRODUCT_CATEGORY_WLT);
            if (materialLot == null) {
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
                        }
                    }

                    List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                    if (CollectionUtils.isNotEmpty(materialLotUnitList)) {
                        for (MaterialLotUnit materialLotUnit : materialLotUnitList) {
                            MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotHistory.TRANS_TYPE_CHECK);
                            materialLotUnitHistory.setTransQty(materialLotUnit.getCurrentQty());
                            materialLotUnitHisRepository.save(materialLotUnitHistory);

                            checkHistory = new CheckHistory();
                            checkHistory.setMaterialLotUnit(materialLotUnit);
                            checkHistory.setTransQty(materialLotUnit.getCurrentQty());
                            checkHistory.setTransType(MaterialLotHistory.TRANS_TYPE_CHECK);
                            checkHistory.setObjectRrn(null);
                            checkHistory.setHisSeq(ThreadLocalContext.getTransRrn());
                            checkHistoryRepository.save(checkHistory);
                        }
                    }
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
     * Wafer发料时同时同步wafer发料单和其他发料单
     * @throws ClientException
     */
    public void asyncWaferIssueOrderAndOtherIssueOrder() throws ClientException {
        asyncWaferIssueOrder();
        asyncOtherIssueOrder();
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
                    WaferIssueOrder waferIssueOrder;
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
                    BigDecimal totalQty = BigDecimal.ZERO;

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpMaterialOutOrder erpMaterialOutOrder : documentIdList) {
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
                            documentLine.setQty(erpMaterialOutOrder.getIquantity());
                            documentLine.setUnHandledQty(erpMaterialOutOrder.getLeftNum());
                            totalQty = totalQty.add(erpMaterialOutOrder.getIquantity());
                            documentLines.add(documentLine);

                            waferIssueOrder.setOwner(erpMaterialOutOrder.getChandler());
                            waferIssueOrder.setReserved32(erpMaterialOutOrder.getCreateSeq());
                            asyncSuccessSeqList.add(erpMaterialOutOrder.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpMaterialOutOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpMaterialOutOrder.setErrorMemo(e.getMessage());
                            erpMaterialOutOrderRepository.save(erpMaterialOutOrder);
                        }
                    }
                    waferIssueOrder.setQty(totalQty);
                    waferIssueOrder.setUnHandledQty(waferIssueOrder.getQty().subtract(waferIssueOrder.getHandledQty()));
                    waferIssueOrder.setReserved31(ErpMaterialOutOrder.SOURCE_TABLE_NAME);
                    waferIssueOrder = (WaferIssueOrder) baseService.saveEntity(waferIssueOrder);

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
                    erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, asyncSuccessSeqList);
                }
                if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                    erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoBySeq(ErpMaterialOutaOrder.SYNC_STATUS_SYNC_ERROR,
                            ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, asyncDuplicateSeqList);
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
                    ReTestOrder reTestOrder;
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
                    BigDecimal totalQty = BigDecimal.ZERO;

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpMaterialOutOrder erpMaterialOutOrder : documentIdList) {
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
                                documentLine.setReserved9(ReTestOrder.CATEGORY_RETEST);
                                documentLine.setReserved31(ErpMaterialOutOrder.SOURCE_TABLE_NAME);

                            }
                            documentLine.setQty(erpMaterialOutOrder.getIquantity());
                            documentLine.setUnHandledQty(erpMaterialOutOrder.getLeftNum());
                            totalQty = totalQty.add(erpMaterialOutOrder.getIquantity());
                            documentLines.add(documentLine);

                            reTestOrder.setOwner(erpMaterialOutOrder.getChandler());
                            reTestOrder.setReserved32(erpMaterialOutOrder.getCreateSeq());
                            asyncSuccessSeqList.add(erpMaterialOutOrder.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpMaterialOutOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpMaterialOutOrder.setErrorMemo(e.getMessage());
                            erpMaterialOutOrderRepository.save(erpMaterialOutOrder);
                        }
                    }
                    reTestOrder.setQty(totalQty);
                    reTestOrder.setUnHandledQty(reTestOrder.getQty().subtract(reTestOrder.getHandledQty()));
                    reTestOrder.setReserved31(ErpMaterialOutOrder.SOURCE_TABLE_NAME);
                    reTestOrder = (ReTestOrder) baseService.saveEntity(reTestOrder);

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
                    erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, asyncSuccessSeqList);
                }
                if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                    erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoBySeq(ErpSo.SYNC_STATUS_SYNC_ERROR,
                            ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, asyncDuplicateSeqList);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 晶圆根据匹配规则自动匹配单据
     * @param documentLineList
     * @param materialLotActions
     * @throws ClientException
     */
    public void validationAndReceiveWafer(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());
            Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMaterialAndSecondCodeAndGradeAndBondProp(documentLineList);
            Map<String, List<MaterialLotAction>> materialLotActionMap = materialLotActions.stream().collect(Collectors.groupingBy(MaterialLotAction:: getMaterialLotId));
            List<MaterialLot> materialLots = new ArrayList<>();
            for(String materialLotId : materialLotActionMap.keySet()){
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId, true);
                materialLots.add(materialLot);
            }
            Map<String, List<MaterialLot>> materialLotMap = groupWaferByMaterialAndSecondCodeAndGradeAndBondProp(materialLots);

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

    private Map<String,List<MaterialLot>> groupWaferByMaterialAndSecondCodeAndGradeAndBondProp(List<MaterialLot> materialLots) {
        return  materialLots.stream().collect(Collectors.groupingBy(materialLot -> {
            StringBuffer key = new StringBuffer();
            key.append(materialLot.getMaterialName());
            key.append(StringUtils.SPLIT_CODE);

            String materialSecondCode = StringUtils.EMPTY;
            if(!StringUtils.isNullOrEmpty(materialLot.getReserved49()) && MaterialLot.IMPORT_COB.equals(materialLot.getReserved49())){
                materialSecondCode = materialLot.getReserved1() + materialLot.getGrade();
            } else {
                materialSecondCode = materialLot.getReserved1();
            }
            key.append(materialSecondCode);
            key.append(StringUtils.SPLIT_CODE);

            key.append(materialLot.getGrade());
            key.append(StringUtils.SPLIT_CODE);

            key.append(materialLot.getReserved6());
            key.append(StringUtils.SPLIT_CODE);
            return key.toString();
        }));
    }


    public void validationAndWaferIssue(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            List<MaterialLot> materialLots = new ArrayList<>();
            Map<String, List<MaterialLotAction>> materialLotActionMap = materialLotActions.stream().collect(Collectors.groupingBy(MaterialLotAction:: getMaterialLotId));
            for(String materialLotId : materialLotActionMap.keySet()){
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId, true);
                materialLots.add(materialLot);
            }

            boolean waferIssueWithDocFlag = SystemPropertyUtils.getWaferIssueWithDocFlag();
            if (waferIssueWithDocFlag) {
                documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());
                Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMaterialAndSecondCodeAndGradeAndBondProp(documentLineList);
                Map<String, List<MaterialLot>> materialLotMap = groupWaferByMaterialAndSecondCodeAndGradeAndBondProp(materialLots);

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
    public void waferIssue(List<DocumentLine> documentLines, List<MaterialLot> materialLots) throws ClientException{
        try {
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
                    if (MaterialLot.IMPORT_LCD_CP.equals(importType) || MaterialLot.IMPORT_SENSOR_CP.equals(importType) || MaterialLot.IMPORT_WLA.equals(importType)) {
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
                    materialLotHistoryRepository.save(history);
                    if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }
                BigDecimal handledQty = (documentLine.getUnHandledQty().subtract(unhandedQty));
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
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void changeMLotUnitStateAndSaveMesWaferBackendWaferReceive(MaterialLot materialLot) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnits = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
            String waferType = null;
            if(MaterialLot.IMPORT_WLT.equals(materialLot.getReserved7())){
                waferType = MesWaferReceive.DEFAULT_WAFER_TYPE;
            }
            for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                materialLotUnit.setState(MaterialLotUnit.STATE_ISSUE);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, GCMaterialEvent.EVENT_WAFER_ISSUE);
                history.setTransQty(materialLotUnit.getCurrentQty());
                materialLotUnitHisRepository.save(history);

                //发料成功，将晶圆信息保存到MES的BACKEND_WAFER_RECEIVE表中
                saveMesBackendWaferReceive(materialLotUnit,waferType);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void saveMesBackendWaferReceive(MaterialLotUnit materialLotUnit,String waferType) throws ClientException{
        try {
            MesWaferReceive waferReceive = new MesWaferReceive();
            waferReceive.setMaterialLotUnit(materialLotUnit);
            Warehouse warehouse = new Warehouse();
            if(!StringUtils.isNullOrEmpty(materialLotUnit.getReserved13())){
                warehouse = warehouseRepository.getOne(Long.parseLong(materialLotUnit.getReserved13()));
            }
            waferReceive.setCstId(materialLotUnit.getLotId());
            waferReceive.setStockId(warehouse.getName());
            if(!StringUtils.isNullOrEmpty(waferType)){
                waferReceive.setWaferType(MesWaferReceive.DEFAULT_WAFER_TYPE);
            }
            waferReceive = mesWaferReceiveRepository.saveAndFlush(waferReceive);

            //mes的晶圆历史表中记录晶圆发料历史
            MesWaferReceiveHis mesWaferReceiveHis = new MesWaferReceiveHis();
            mesWaferReceiveHis.setTransType(MesWaferReceiveHis.TRNAS_TYPE_ISSUE);
            PropertyUtils.copyProperties(waferReceive, mesWaferReceiveHis, new HistoryBeanConverter());
            mesWaferReceiveHis.setObjectRrn(null);
            mesWaferReceiveHisRepository.save(mesWaferReceiveHis);

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
            for (DocumentLine documentLine: documentLines) {

                BigDecimal unhandedQty = documentLine.getUnHandledQty();
                Map<String, BigDecimal> mLotQty = Maps.newHashMap();
                for(MaterialLot materialLot : materialLots){
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
                            materialLotUnitService.receiveMLotWithUnit(materialLot, WAREHOUSE_ZJ);
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
                            materialLotUnitService.receiveMLotWithUnit(materialLot, WAREHOUSE_ZJ);
                            iterator.remove();
                        }
                    }
                    if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }
                BigDecimal handledQty = documentLine.getUnHandledQty().subtract(unhandedQty);
                documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
                documentLine.setUnHandledQty(unhandedQty);
                documentLine = documentLineRepository.saveAndFlush(documentLine);
                baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_RECEIVE);

                ReceiveOrder receiveOrder = (ReceiveOrder) receiveOrderRepository.findByObjectRrn(documentLine.getDocRrn());
                receiveOrder.setHandledQty(receiveOrder.getHandledQty().add(handledQty));
                receiveOrder.setUnHandledQty(receiveOrder.getUnHandledQty().subtract(handledQty));
                receiveOrder = receiveOrderRepository.saveAndFlush(receiveOrder);
                baseService.saveHistoryEntity(receiveOrder, MaterialLotHistory.TRANS_TYPE_RECEIVE);

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
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 单据按照 物料名称+二级代码+等级+保税属性分类
     * @param documentLineList
     * @return
     */
    public Map<String, List<DocumentLine>> groupDocLineByMaterialAndSecondCodeAndGradeAndBondProp(List<DocumentLine> documentLineList) {
        return documentLineList.stream().collect(Collectors.groupingBy(documentLine -> {
            StringBuffer key = new StringBuffer();
            key.append(documentLine.getMaterialName());
            key.append(StringUtils.SPLIT_CODE);
            // 二级代码
            key.append(documentLine.getReserved2());
            key.append(StringUtils.SPLIT_CODE);

            //等级
            key.append(documentLine.getReserved3());
            key.append(StringUtils.SPLIT_CODE);

            key.append(documentLine.getReserved7());
            key.append(StringUtils.SPLIT_CODE);
            return key.toString();
        }));
    }

    /**
     * 物料批次按照 物料名称+二级代码+等级+保税属性分类
     * @param materialLots
     * @return
     */
    public Map<String, List<MaterialLot>> groupMaterialLotByMaterialAndSecondCodeAndGradeAndBondProp(List<MaterialLot> materialLots) {
        return  materialLots.stream().collect(Collectors.groupingBy(materialLot -> {
            StringBuffer key = new StringBuffer();
            key.append(materialLot.getMaterialName());
            key.append(StringUtils.SPLIT_CODE);

            String materialSecondCode = materialLot.getReserved1() + materialLot.getGrade();
            key.append(materialSecondCode);
            key.append(StringUtils.SPLIT_CODE);

            key.append(materialLot.getGrade());
            key.append(StringUtils.SPLIT_CODE);

            key.append(materialLot.getReserved6());
            key.append(StringUtils.SPLIT_CODE);
            return key.toString();
        }));
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
            Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMaterialAndSecondCodeAndGradeAndBondProp(documentLineList);
            Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByMaterialAndSecondCodeAndGradeAndBondProp(materialLots);

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
                BigDecimal handledQty = documentLine.getHandledQty().add((documentLine.getUnHandledQty().subtract(unhandedQty)));
                documentLine.setHandledQty(handledQty);
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
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void validationStockDocLine(DocumentLine documentLine, MaterialLot materialLot) throws ClientException{
        validationDocLine(documentLine,materialLot);
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
                validationDocLine(documentLine, materialLot);
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
            documentLine = documentLineRepository.saveAndFlush(documentLine);
            baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_SHIP);

            // 获取到主单据
            DeliveryOrder deliveryOrder = (DeliveryOrder) deliveryOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            deliveryOrder.setHandledQty(deliveryOrder.getHandledQty().add(handledQty));
            deliveryOrder.setUnHandledQty(deliveryOrder.getUnHandledQty().subtract(handledQty));
            deliveryOrder = deliveryOrderRepository.saveAndFlush(deliveryOrder);
            baseService.saveHistoryEntity(deliveryOrder, MaterialLotHistory.TRANS_TYPE_SHIP);

            Optional<ErpSo> erpSoOptional = erpSoRepository.findById(Long.valueOf(documentLine.getReserved1()));
            if (!erpSoOptional.isPresent()) {
                throw new ClientParameterException(GcExceptions.ERP_SO_IS_NOT_EXIST, documentLine.getReserved1());
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
            List<ErpSo> erpSos = erpSoRepository.findByTypeAndSynStatusNotIn(ErpSo.TYPE_TV, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS));
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
                    ReceiveOrder receiveOrder;
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
                        receiveOrder.setReserved31(ErpMaterialOutOrder.SOURCE_TABLE_NAME);
                    } else {
                        receiveOrder = receiveOrderList.get(0);
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
                    BigDecimal totalQty = BigDecimal.ZERO;

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpSo erpSo : documentIdList) {
                        try {
                            DocumentLine documentLine = null;
                            if (receiveOrder.getObjectRrn() != null) {
                                documentLine = documentLineRepository.findByDocRrnAndReserved1(receiveOrder.getObjectRrn(), String.valueOf(erpSo.getSeq()));
                                if (documentLine != null ) {
                                    if (ErpSo.SYNC_STATUS_CHANGED.equals(erpSo.getSynStatus())) {
                                        if (documentLine != null && documentLine.getHandledQty().compareTo(erpSo.getIquantity()) > 0) {
                                            throw new ClientException("gc.order_handled_qty_gt_qty");
                                        }
                                    }
                                }
                            }

                            Date erpCreatedDate = DateUtils.parseDate(erpSo.getDdate());
                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            if (documentLine == null) {
                                documentLine = new DocumentLine();
                                Material material = mmsService.getRawMaterialByName(erpSo.getCinvcode());
                                if (material == null) {
                                    throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, erpSo.getCinvcode());
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
                                documentLine.setReserved9(Document.CATEGORY_RECEIVE);

                                documentLine.setReserved10(erpSo.getGCode());
                                documentLine.setReserved11(erpSo.getGName());
                                documentLine.setReserved12(erpSo.getOther8());
                                documentLine.setReserved15(erpSo.getOther18());
                                documentLine.setReserved17(erpSo.getOther3());
                                documentLine.setReserved20(erpSo.getOther9());
                                documentLine.setReserved21(erpSo.getOther10());
                                documentLine.setReserved27(erpSo.getOther7());
                                documentLine.setReserved28(erpSo.getOther4());
                                documentLine.setDocType(erpSo.getCvouchtype());
                                documentLine.setDocName(erpSo.getCvouchname());
                                documentLine.setDocBusType(erpSo.getCbustype());
                                documentLine.setDocSource(erpSo.getCsource());
                                documentLine.setWarehouseCode(erpSo.getCwhcode());
                                documentLine.setWarehouseName(erpSo.getCwhname());
                                documentLine.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                            }
                            documentLine.setQty(erpSo.getIquantity());
                            documentLine.setUnHandledQty(erpSo.getLeftNum());
                            documentLine.setReservedQty(BigDecimal.ZERO);
                            documentLine.setUnReservedQty(erpSo.getIquantity());
                            totalQty = totalQty.add(erpSo.getIquantity());
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
                            erpSo.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpSo.setErrorMemo(e.getMessage());
                            erpSoRepository.save(erpSo);
                        }
                    }
                    receiveOrder.setQty(totalQty);
                    receiveOrder.setUnHandledQty(receiveOrder.getQty().subtract(receiveOrder.getHandledQty()));
                    receiveOrder.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                    receiveOrder = (ReceiveOrder) baseService.saveEntity(receiveOrder);

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(receiveOrder);
                        baseService.saveEntity(documentLine);
                    }
                    // 保存单据的时候同步下客户
                    if (!StringUtils.isNullOrEmpty(receiveOrder.getSupplierName())) {
                        savaCustomer(receiveOrder.getSupplierName());
                    }
                }

                if (CollectionUtils.isNotEmpty(asyncSuccessSeqList)) {
                    erpSoRepository.updateSynStatusAndErrorMemoBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, asyncSuccessSeqList);
                }
                if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                    erpSoRepository.updateSynStatusAndErrorMemoBySeq(ErpSo.SYNC_STATUS_SYNC_ERROR,
                            ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, asyncDuplicateSeqList);
                }
            }
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
            List<ErpSo> erpSos = erpSoRepository.findByTypeAndSynStatusNotIn(ErpSo.TYPE_SO, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS));
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
                    DeliveryOrder deliveryOrder;
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
                    BigDecimal totalQty = BigDecimal.ZERO;

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpSo erpSo : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (deliveryOrder.getObjectRrn() != null) {
                                documentLine = documentLineRepository.findByDocRrnAndReserved1(deliveryOrder.getObjectRrn(), String.valueOf(erpSo.getSeq()));
                                if (documentLine != null) {
                                    if (ErpSo.SYNC_STATUS_CHANGED.equals(erpSo.getSynStatus())) {
                                        if (documentLine != null && documentLine.getHandledQty().compareTo(erpSo.getIquantity()) > 0) {
                                            throw new ClientException("gc.order_handled_qty_gt_qty");
                                        }
                                    }
                                }
                            }

                            Date erpCreatedDate = DateUtils.parseDate(erpSo.getDdate());
                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            if (documentLine == null) {
                                documentLine = new DocumentLine();
                                Material material = mmsService.getRawMaterialByName(erpSo.getCinvcode());
                                if (material == null) {
                                    throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, erpSo.getCinvcode());
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
                                documentLine.setReserved9(DeliveryOrder.CATEGORY_DELIVERY);

                                documentLine.setReserved10(erpSo.getGCode());
                                documentLine.setReserved11(erpSo.getGName());
                                documentLine.setReserved12(erpSo.getOther8());
                                documentLine.setReserved15(erpSo.getOther18());
                                documentLine.setReserved17(erpSo.getOther3());
                                documentLine.setReserved20(erpSo.getOther9());
                                documentLine.setReserved21(erpSo.getOther10());
                                documentLine.setReserved27(erpSo.getOther7());
                                documentLine.setReserved28(erpSo.getOther4());

                                documentLine.setDocType(erpSo.getCvouchtype());
                                documentLine.setDocName(erpSo.getCvouchname());
                                documentLine.setDocBusType(erpSo.getCbustype());
                                documentLine.setDocSource(erpSo.getCsource());
                                documentLine.setWarehouseCode(erpSo.getCwhcode());
                                documentLine.setWarehouseName(erpSo.getCwhname());
                                documentLine.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                            }
                            documentLine.setQty(erpSo.getIquantity());
                            documentLine.setUnHandledQty(erpSo.getLeftNum());
                            documentLine.setUnReservedQty(erpSo.getIquantity());
                            totalQty = totalQty.add(erpSo.getIquantity());
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
                            erpSo.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpSo.setErrorMemo(e.getMessage());
                            erpSoRepository.save(erpSo);
                        }
                    }
                    deliveryOrder.setQty(totalQty);
                    deliveryOrder.setUnHandledQty(deliveryOrder.getQty().subtract(deliveryOrder.getHandledQty()));
                    deliveryOrder.setUnReservedQty(totalQty);

                    deliveryOrder.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                    deliveryOrder = (DeliveryOrder) baseService.saveEntity(deliveryOrder);

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(deliveryOrder);
                        baseService.saveEntity(documentLine);
                    }

                    // 保存单据的时候同步下客户
                    if (!StringUtils.isNullOrEmpty(deliveryOrder.getSupplierName())) {
                        savaCustomer(deliveryOrder.getSupplierName());
                    }
                }

                if (CollectionUtils.isNotEmpty(asyncSuccessSeqList)) {
                    erpSoRepository.updateSynStatusAndErrorMemoBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, asyncSuccessSeqList);
                }
                if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                    erpSoRepository.updateSynStatusAndErrorMemoBySeq(ErpSo.SYNC_STATUS_SYNC_ERROR,
                            ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, asyncDuplicateSeqList);
                }
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
     * @param doWltReceiveFlag
     */
    public void receiveFinishGood(List<MesPackedLot> packedLotList,boolean doWltReceiveFlag) throws ClientException {
        try {
            Map<String, List<MesPackedLot>> packedLotMap = packedLotList.stream().map(packedLot -> mesPackedLotRepository.findByBoxId(packedLot.getBoxId())).collect(Collectors.groupingBy(MesPackedLot :: getProductId));
            Map<String, Warehouse> warehouseMap = Maps.newHashMap();

            List<ErpMo> erpMos = Lists.newArrayList();
            List<ErpMoa> erpMoaList = Lists.newArrayList();
            //MesPackedLotRelation mesPackedLotRelation ;

            for (String productId : packedLotMap.keySet()) {
                List<MesPackedLot> mesPackedLotList = packedLotMap.get(productId);
                RawMaterial rawMaterial = mmsService.getRawMaterialByName(productId);
                if (rawMaterial == null) {
                    throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, productId);
                }
                //mesPackedLotRelation = mesPackedLotRelationRepository.findByPackedLotRrn(mesPackedLotList.get(0).getPackedLotRrn());

                List<MesPackedLot> mesPackedLots = packedLotMap.get(productId);
                List<MaterialLotAction> materialLotActions = Lists.newArrayList();
                for (MesPackedLot mesPackedLot : mesPackedLots) {
                    //由于wlt的业务在之前会更改PackedLotRrn，所以在mesPackedLot中传递数据
                    /*
                    if(!MesPackedLot.PRODUCT_CATEGORY_COM.equals(mesPackedLot.getProductCategory())){
                        if(doWltReceiveFlag){
                            if(mesPackedLot.getHaveBindMaterialData()){
                                mesPackedLotRelation = new MesPackedLotRelation();
                                mesPackedLotRelation.setMaterialBonded(mesPackedLot.getMaterialBonded());
                                mesPackedLotRelation.setMaterialCode(mesPackedLot.getMaterialCode());
                                mesPackedLotRelation.setMaterialQty(mesPackedLot.getMaterialQty());
                                mesPackedLotRelation.setMaterialGrade(mesPackedLot.getMaterialGrade());
                                mesPackedLotRelation.setMaterialVersion(mesPackedLot.getMaterialVersion());
                            }else{
                                //当前Relation查不到数据并且没有从mesPackedLot中传递数据时提示
                                throw new ClientException(GcExceptions.CORRESPONDING_RAW_MATERIAL_INFO_IS_EMPTY);
                            }
                        }else{
                            if(mesPackedLotRelation == null){
                                throw new ClientException(GcExceptions.CORRESPONDING_RAW_MATERIAL_INFO_IS_EMPTY);
                            }
                        }
                    }*/

                    MaterialLotAction materialLotAction = new MaterialLotAction();
                    materialLotAction.setMaterialLotId(mesPackedLot.getBoxId());
                    materialLotAction.setGrade(mesPackedLot.getGrade());
                    materialLotAction.setTransQty(BigDecimal.valueOf(mesPackedLot.getQuantity()));

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
                    otherReceiveProps.put("reserved7", mesPackedLot.getProductCategory());
                    otherReceiveProps.put("reserved13", warehouse.getObjectRrn().toString());
                    otherReceiveProps.put("workOrderId", mesPackedLot.getWorkorderId());
                    otherReceiveProps.put("reserved21", mesPackedLot.getErpProductId());
                    otherReceiveProps.put("lotId", mesPackedLot.getCstId());
                    if(mesPackedLot.getWaferQty() != null){
                        BigDecimal waferQty = new BigDecimal(mesPackedLot.getWaferQty().toString());
                        materialLotAction.setTransCount(waferQty);
                    }
                    materialLotAction.setPropsMap(otherReceiveProps);

                    materialLotActions.add(materialLotAction);

                    String productCateGory = mesPackedLot.getProductCategory();

                    if(MesPackedLot.PRODUCT_CATEGORY_FT.equals(productCateGory)){
                        // ERP_MOA插入数据
                        ErpMoa erpMoa = new ErpMoa();
                        erpMoa.setFQty(mesPackedLot.getQuantity());
                        erpMoa.setWarehouseCode(warehouseName);
                        erpMoa.setMesPackedLot(mesPackedLot);
                        //从MM_PACKED_LOT_RELATION表中获取物料型号、物料数据等相关数据
                        erpMoa.setCMemo("EMPTY");
                        erpMoa.setMaterialBonded("EMPTY");
                        erpMoa.setMaterialCode("EMPTY");
                        erpMoa.setMaterialQty(0);
                        erpMoa.setMaterialGrade("EMPTY");
                        erpMoa.setMaterialVersion("EMPTY");
                        /*
                        erpMoa.setMaterialBonded(mesPackedLotRelation.getMaterialBonded());
                        erpMoa.setMaterialCode(mesPackedLotRelation.getMaterialCode());
                        erpMoa.setMaterialQty(mesPackedLotRelation.getMaterialQty());
                        erpMoa.setMaterialGrade(mesPackedLotRelation.getMaterialGrade());
                        erpMoa.setMaterialVersion(mesPackedLotRelation.getMaterialVersion());*/

                        erpMoaList.add(erpMoa);
                    } else if(MesPackedLot.PRODUCT_CATEGORY_CP.equals(productCateGory) || MesPackedLot.PRODUCT_CATEGORY_WLT.equals(productCateGory)){
                        // ERP_MOA插入数据
                        ErpMoa erpMoa = new ErpMoa();
                        erpMoa.setFQty(mesPackedLot.getWaferQty());
                        erpMoa.setWarehouseCode(warehouseName);
                        erpMoa.setMesPackedLot(mesPackedLot);

                        //从MM_PACKED_LOT_RELATION表中获取物料型号、物料数据等相关数据
                        erpMoa.setCMemo("EMPTY");
                        erpMoa.setMaterialBonded("EMPTY");
                        erpMoa.setMaterialCode("EMPTY");
                        erpMoa.setMaterialQty(0);
                        erpMoa.setMaterialGrade("EMPTY");
                        erpMoa.setMaterialVersion("EMPTY");
                        /*
                        erpMoa.setMaterialBonded(mesPackedLotRelation.getMaterialBonded());
                        erpMoa.setMaterialCode(mesPackedLotRelation.getMaterialCode());
                        erpMoa.setMaterialQty(mesPackedLot.getWaferQty());
                        erpMoa.setMaterialGrade(mesPackedLotRelation.getMaterialGrade());
                        erpMoa.setMaterialVersion(mesPackedLotRelation.getMaterialVersion());*/

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
                mmsService.receiveMLotList2Warehouse(rawMaterial, materialLotActions);
            };

            mesPackedLotRepository.updatePackedStatusByPackedLotRrnList(MesPackedLot.PACKED_STATUS_RECEIVED, packedLotList.stream().map(MesPackedLot :: getPackedLotRrn).collect(Collectors.toList()));
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

            //GC只会全部包装。故此处，直接用ParentMaterialLotId做包装号。
            Map<String, List<MaterialLot>> packedLotMap = materialLots.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true))
                    .collect(Collectors.groupingBy(MaterialLot::getParentMaterialLotId));
            for (String packageMLotId : packedLotMap.keySet())  {
                MaterialLot parentMLot = mmsService.getMLotByMLotId(packageMLotId, true);
                judgeMaterialLot(parentMLot, judgeGrade, judgeCode, ngCheckList);

                for (MaterialLot packagedMLot : packedLotMap.get(packageMLotId)) {
                    judgeMaterialLot(packagedMLot, judgeGrade, judgeCode, ngCheckList);
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
            queryMesProductOrWaferTypeInfoByQueryName(Material.QUERY_PRODUCTINFO, Material.TYPE_PRODUCT);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科同步MES的晶圆型号、描述、单位
     */
    public void asyncMesWaferType() throws ClientException{
        try {
            queryMesProductOrWaferTypeInfoByQueryName(Material.QUERY_WAFERTYPEINFO, Material.TYPE_WAFER);
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
            RawMaterial rawMaterial = new RawMaterial();
            List<Map> productSubcodeList = findEntityMapListByQueryName(Material.QUERY_PRODUCT_SUBCODE,null,0,999,"","");
            if(CollectionUtils.isNotEmpty(productSubcodeList)){
                for(Map<String, String> m : productSubcodeList){
                    String productId = m.get("MODEL_ID");
                    String subcode = m.get("SUB_CODE");
                    rawMaterial = mmsService.getRawMaterialByName(productId);
                    if(rawMaterial != null){
                        GCProductSubcode productSubcode = gcProductSubcodeSetRepository.findByProductId(productId);
                        if(productSubcode == null){
                            productSubcode = new GCProductSubcode();
                            productSubcode.setProductId(productId);
                            productSubcode.setSubcode(subcode);
                            gcProductSubcodeSetRepository.saveAndFlush(productSubcode);
                        } else {
                            GCProductSubcode oldProductSubcode = gcProductSubcodeSetRepository.findByProductIdAndSubcode(productId, subcode);
                            if(oldProductSubcode == null){
                                productSubcode.setSubcode(subcode);
                                gcProductSubcodeSetRepository.saveAndFlush(productSubcode);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void queryMesProductOrWaferTypeInfoByQueryName(String queryName, String materialType) {
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            RawMaterial rawMaterial = new RawMaterial();
            List<Map> materialList = findEntityMapListByQueryName(queryName,null,0,999,"","");
            if(CollectionUtils.isNotEmpty(materialList)){
                for (Map<String, String> m :materialList)  {
                    String productId = m.get("INSTANCE_ID");
                    String productDesc = m.get("INSTANCE_DESC");
                    String storeUom = m.get("STORE_UOM");
                    if("PRODUCT".equals(m.get("OBJECT"))){
                        materialType = Material.TYPE_PRODUCT;
                    } else if("WAFER".equals(m.get("OBJECT"))){
                        materialType = Material.TYPE_WAFER;
                    }
                    Integer packageTotalQty = 0;
                    if(m.get("BOX_STANDARD_QTY") != null && m.get("PACKAGE_STANDARD_QTY") != null){
                        Integer boxStandardQty = Integer.parseInt(String.valueOf((Object)m.get("BOX_STANDARD_QTY")));
                        Integer packageStandardQty = Integer.parseInt(String.valueOf((Object)m.get("PACKAGE_STANDARD_QTY")));
                        packageTotalQty = boxStandardQty * packageStandardQty;
                    }

                    rawMaterial = mmsService.getRawMaterialByName(productId);
                    if(rawMaterial == null){
                        rawMaterial = new RawMaterial();
                        rawMaterial.setName(productId);
                        rawMaterial.setDescription(productDesc);
                        rawMaterial.setStoreUom(storeUom);
                        rawMaterial.setMaterialCategory(materialType);
                        rawMaterial.setMaterialType(materialType);
                        rawMaterial.setReserved1(packageTotalQty.toString());
                        rawMaterial = mmsService.saveRawMaterial(rawMaterial);

                        List<MaterialStatusModel> statusModels = materialStatusModelRepository.findByNameAndOrgRrn(Material.DEFAULT_STATUS_MODEL, sc.getOrgRrn());
                        if (CollectionUtils.isNotEmpty(statusModels)) {
                            rawMaterial.setStatusModelRrn(statusModels.get(0).getObjectRrn());
                        } else {
                            throw new ClientException(StatusMachineExceptions.STATUS_MODEL_IS_NOT_EXIST);
                        }
                        rawMaterialRepository.save(rawMaterial);
                    } else {
                        rawMaterial.setMaterialCategory(materialType);
                        rawMaterial.setMaterialType(materialType);
                        rawMaterial.setReserved1(packageTotalQty.toString());
                        rawMaterialRepository.saveAndFlush(rawMaterial);
                    }
                }
            }
        } catch (Exception e) {
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
    public MaterialLot getWaitWeightMaterialLot(String materialLotId) throws ClientException {
        try {
            MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId, true);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void materialLotWeight(List<WeightModel> weightModels) throws ClientException {
        try {
            ThreadLocalContext.getSessionContext().buildTransInfo();
            Map<String, WeightModel> weightModelMap = weightModels.stream().collect(Collectors.toMap(WeightModel :: getMaterialLotId, Function.identity()));
            List<MaterialLot> materialLots = weightModels.stream().map(model -> mmsService.getMLotByMLotId(model.getMaterialLotId(), true)).collect(Collectors.toList());

            //验证物料批次是否是多箱称重
            String transId = "";
            WeightModel boxsWeightModel = weightModelMap.get(materialLots.get(0).getMaterialLotId());
            if(!StringUtils.isNullOrEmpty(boxsWeightModel.getBoxsWeightFlag())){
                transId = generatorMLotsTransId(MaterialLot.GENERATOR_MATERIAL_LOT_WEIGHT_RULE);
            }
            //称重记录
            for (MaterialLot materialLot : materialLots) {
                WeightModel weightModel = weightModelMap.get(materialLot.getMaterialLotId());
                String weight = weightModel.getWeight();
                materialLot.setReserved19(weight);
                materialLot.setReserved20(transId);
                materialLotRepository.save(materialLot);
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
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_RESERVED_INFO_IS_NOT_SAME);
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
            if (CollectionUtils.isNotEmpty(validatedMLotActions)) {
                MaterialLot validatedMLot = mmsService.getMLotByMLotId(validatedMLotActions.get(0).getMaterialLotId(), true);
                try {
                    Assert.assertEquals(waitValidationLot.getMaterialName(), validatedMLot.getMaterialName());
                } catch (AssertionError e) {
                    falg = false;
                }

                try {
                    Assert.assertEquals(waitValidationLot.getReserved1(), validatedMLot.getReserved1());
                } catch (AssertionError e) {
                    falg = false;
                }

                try {
                    Assert.assertEquals(waitValidationLot.getGrade(), validatedMLot.getGrade());
                } catch (AssertionError e) {
                    falg = false;
                }
                try {
                    Assert.assertEquals(waitValidationLot.getReserved6(), validatedMLot.getReserved6());
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
                    RawMaterial rawMaterial = mmsService.getRawMaterialByName(materialLots.get(0).getMaterialName());
                    if (rawMaterial == null) {
                        throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, parentMaterialLot.getMaterialName());
                    }
                    StatusModel statusModel = mmsService.getMaterialStatusModel(rawMaterial);

                    parentMaterialLot.setMaterial(rawMaterial);
                    Long totalMaterialLotQty = materialLotMap.get(parentMaterialLotId).stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentQty().longValue()));
                    parentMaterialLot.setMaterialLotId(parentMaterialLotId);
                    parentMaterialLot.setLotId(parentMaterialLotId);
                    parentMaterialLot.setCurrentQty(BigDecimal.valueOf(totalMaterialLotQty));
                    parentMaterialLot.setMaterialLot(materialLots.get(0));
                    parentMaterialLot.initialMaterialLot();
                    parentMaterialLot.setStatusModelRrn(statusModel.getObjectRrn());
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
                        materialLot.setMaterial(rawMaterial);
                        materialLot.initialMaterialLot();
                        materialLot.setStatusModelRrn(statusModel.getObjectRrn());
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
                Map<String, List<MaterialLot>> materialLotMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot:: getMaterialLotId));
                for(String materialLotId : materialLotMap.keySet()){
                    List<MaterialLot> materialLots = materialLotMap.get(materialLotId);
                    RawMaterial rawMaterial = mmsService.getRawMaterialByName(materialLots.get(0).getMaterialName());
                    if (rawMaterial == null) {
                        throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, materialLots.get(0).getMaterialName());
                    }
                    StatusModel statusModel = mmsService.getMaterialStatusModel(rawMaterial);

                    for(MaterialLot materialLot : materialLots){
                        materialLot.setMaterial(rawMaterial);
                        materialLot.setReserved48(importCode);
                        materialLot.initialMaterialLot();
                        materialLot.setStatusModelRrn(statusModel.getObjectRrn());
                        materialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
                        materialLot.setStatus(MaterialStatus.STATUS_CREATE);
                        materialLot.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_RMA);
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
                        materialLot = materialLotRepository.saveAndFlush(materialLot);

                        MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE);
                        materialLotHistoryRepository.save(history);
                    }
                }

            }
            return importCode;
        } catch (Exception e) {
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
                materialLotUnit.setReserved50("1");
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
            //按照箱号分组
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialLotId));
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                materialLotUnitRepository.delete(materialLotUnit);

                MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_DELETE);
                history.setTransQty(materialLotUnit.getCurrentQty());
                history.setActionComment(deleteNote);
                materialLotUnitHisRepository.save(history);
            }

            for(String materialLotId : materialLotUnitMap.keySet()){
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId);
                materialLotRepository.delete(materialLot);

                // 记录历史
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_DELETE);
                history.setTransQty(materialLot.getCurrentQty());
                history.setActionComment(deleteNote);
                materialLotHistoryRepository.save(history);
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收WLT的完成品
     * @param packedLotList
     */
    public void receiveWltFinishGood(List<MesPackedLot> packedLotList) throws ClientException {
        try {
            Map<String, List<MesPackedLot>> packedLotMap = packedLotList.stream().map(packedLot -> mesPackedLotRepository.findByBoxId(packedLot.getBoxId())).collect(Collectors.groupingBy(MesPackedLot :: getCstId));
            List<MesPackedLot> mesPackedLots = Lists.newArrayList();
            for(String cstId : packedLotMap.keySet()){
                List<MesPackedLot> mesPackedLotList = packedLotMap.get(cstId);
                Long totalQuantity = mesPackedLotList.stream().collect(Collectors.summingLong(mesPackedLot -> mesPackedLot.getQuantity().longValue()));
                RawMaterial rawMaterial = mmsService.getRawMaterialByName(mesPackedLotList.get(0).getProductId());
                if (rawMaterial == null) {
                    throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_IS_NOT_EXIST, mesPackedLotList.get(0).getProductId());
                }

                //MesPackedLotRelation mesPackedLotRelation = mesPackedLotRelationRepository.findByPackedLotRrn(mesPackedLotList.get(0).getPackedLotRrn());
                MesPackedLot mesPackedLot = new MesPackedLot();
                PropertyUtils.copyProperties(mesPackedLotList.get(0), mesPackedLot, new HistoryBeanConverter());
                String mLotId = mmsService.generatorMLotId(rawMaterial);
                mesPackedLot.setBoxId(mLotId);
                mesPackedLot.setPackedLotRrn(null);
                mesPackedLot.setWaferId("");
                mesPackedLot.setQuantity(totalQuantity.intValue());
                mesPackedLot.setWaferQty(mesPackedLotList.size());
                mesPackedLot = mesPackedLotRepository.saveAndFlush(mesPackedLot);
                /*
                if(mesPackedLotRelation != null){
                    mesPackedLot.setMaterialBonded(mesPackedLotRelation.getMaterialBonded());
                    mesPackedLot.setMaterialCode(mesPackedLotRelation.getMaterialCode());
                    mesPackedLot.setMaterialQty(mesPackedLotRelation.getMaterialQty());
                    mesPackedLot.setMaterialGrade(mesPackedLotRelation.getMaterialGrade());
                    mesPackedLot.setMaterialVersion(mesPackedLotRelation.getMaterialVersion());
                    mesPackedLot.setHaveBindMaterialData(true);
                }*/
                mesPackedLots.add(mesPackedLot);
            }
            receiveFinishGood(mesPackedLots,true);

            for(MesPackedLot mesPackedLot : mesPackedLots){
                String cstId = mesPackedLot.getCstId();
                List<MesPackedLot> mesPackedLotList = packedLotMap.get(cstId);
                MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(mesPackedLot.getBoxId(), ThreadLocalContext.getOrgRrn());
                RawMaterial rawMaterial = mmsService.getRawMaterialByName(materialLot.getMaterialName());
                for(MesPackedLot packedLot : mesPackedLotList){
                    MaterialLotUnit materialLotUnit = new MaterialLotUnit();
                    materialLotUnit.setUnitId(packedLot.getWaferId());
                    materialLotUnit.setMaterialLotId(materialLot.getMaterialLotId());
                    materialLotUnit.setMaterialLotRrn(materialLot.getObjectRrn());
                    materialLotUnit.setLotId(cstId);
                    materialLotUnit.setMaterial(rawMaterial);
                    materialLotUnit.setState(MaterialLotUnit.STATE_IN);
                    materialLotUnit.setGrade(packedLot.getGrade());
                    materialLotUnit.setWorkOrderId(packedLot.getWorkorderId());
                    materialLotUnit.setCurrentQty(BigDecimal.valueOf(packedLot.getQuantity()));
                    materialLotUnit.setCurrentSubQty(BigDecimal.ONE);
                    materialLotUnit.setReceiveQty(BigDecimal.valueOf(packedLot.getQuantity()));
                    materialLotUnit.setReserved1(packedLot.getLevelTwoCode());
                    materialLotUnit.setReserved3(String.valueOf(mesPackedLotList.size()));
                    materialLotUnit.setReserved4(packedLot.getBondedProperty());
                    materialLotUnit.setReserved13(materialLot.getReserved13());
                    materialLotUnit.setReserved18("0");
                    materialLotUnit.setReserved38(packedLot.getWaferMark());
                    materialLotUnit =  materialLotUnitRepository.saveAndFlush(materialLotUnit);

                    MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_CREATE);
                    history.setTransQty(materialLotUnit.getReceiveQty());
                    materialLotUnitHisRepository.save(history);
                }
            }
            mesPackedLotRepository.updatePackedStatusByPackedLotRrnList(MesPackedLot.PACKED_STATUS_RECEIVED, packedLotList.stream().map(MesPackedLot :: getPackedLotRrn).collect(Collectors.toList()));
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
            RawMaterial rawMaterial = mmsService.getRawMaterialByName(productSubcode.getProductId());
            if (rawMaterial == null) {
                throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_IS_NOT_EXIST, productSubcode.getProductId());
            }
            GCProductSubcode oldProductSubcode = getProductAndSubcodeInfo(productSubcode.getProductId(),productSubcode.getSubcode());
            if(oldProductSubcode != null){
                throw new ClientParameterException(GcExceptions.PRODUCT_AND_SUBCODE_IS_EXIST);
            }
            productSubcode = gcProductSubcodeSetRepository.saveAndFlush(productSubcode);
            return productSubcode;
        } catch (Exception e){
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
    public void validateMLotUnitProductAndBondedProperty(List<MaterialLotUnit> materialLotUnitList) throws ClientException{
        try {
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getLotId));
            for (String lotId : materialLotUnitMap.keySet()){
                MaterialLotUnit materialLotUnit = materialLotUnitMap.get(lotId).get(0);
                GCProductSubcode gcProductSubcode = getProductAndSubcodeInfo(materialLotUnit.getMaterialName(), materialLotUnit.getReserved1());
                if(gcProductSubcode == null ){
                    throw new ClientParameterException(GcExceptions.PRODUCT_AND_SUBCODE_IS_NOT_EXIST);
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

                        MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, TRANS_TYPE_UPDATE_LOCAYTION);
                        materialLotUnitHistory.setActionComment(remarks);
                        materialLotUnitHistory.setTransQty(materialLotUnit.getCurrentQty());
                        materialLotUnitHisRepository.save(materialLotUnitHistory);
                    }
                }
                // 记录历史
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_UPDATE_LOCAYTION);
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
            for (MaterialLot materialLot : materialLotList){
                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setTransQty(materialLot.getCurrentQty());
                materialLotAction.setActionComment(remarks);
                mmsService.holdMaterialLot(materialLot,materialLotAction);
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
            for (MaterialLot materialLot : materialLotList){
                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setTransQty(materialLot.getCurrentQty());
                materialLotAction.setActionComment(remarks);
                mmsService.releaseMaterialLot(materialLot,materialLotAction);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public List<MaterialLot> validationAndGetWaitIssueWafer(List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            List<MaterialLot> materialLotList = new ArrayList<>();
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            for(MaterialLot materialLot: materialLots){
                List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitList.stream().filter(materialLotUnit -> !StringUtils.isNullOrEmpty(materialLotUnit.getWorkOrderId()))
                        .collect(Collectors.groupingBy(MaterialLotUnit :: getWorkOrderId));
                if(materialLotUnitMap != null && materialLotUnitMap.size() > 0){
                    materialLotList.add(materialLot);
                }
            }
            return materialLotList;
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

            //获取当前日期，时间格式yyMMdd
            SimpleDateFormat formatter = new SimpleDateFormat(MaterialLot.PRINT_DATE_PATTERN);
            String date = formatter.format(new Date());

            //从产品上获取真空包的标准数量，用于区分真空包属于零包还是散包
            RawMaterial rawMaterial = mmsService.getRawMaterialByName(materialLot.getMaterialName());
            BigDecimal packageTotalQty = new BigDecimal(rawMaterial.getReserved1());

            String dateAndNumber = StringUtils.EMPTY;
            String twoDCode = StringUtils.EMPTY;
            String boxSeq = StringUtils.EMPTY;
            String printSeq = StringUtils.EMPTY;
            String flow = StringUtils.EMPTY;

            if(StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                Map<String, String> parameterMap = Maps.newHashMap();
                printSeq = generatorMLotsTransId(MaterialLot.GENERATOR_QRCODE_LABEL_PRINT_SEQ_RULE);
                flow = printSeq + StringUtils.UNDERLINE_CODE  + printSeq;
                parameterMap.put("VENDER", MaterialLot.GC_CODE);
                parameterMap.put("MATERIALCODE", "weizhi");
                dateAndNumber = date + StringUtils.UNDERLINE_CODE + StringUtil.leftPad(materialLot.getCurrentQty().toString() , 6 , "0") + StringUtils.UNDERLINE_CODE;
                parameterMap.put("DATEANDNUMBER", dateAndNumber);
                if(packageTotalQty.compareTo(materialLot.getCurrentQty()) > 0 ){
                    boxSeq = "VL" + materialLot.getMaterialLotId().substring(materialLot.getMaterialLotId().length() - 3);
                } else {
                    boxSeq = "VZ" + materialLot.getMaterialLotId().substring(materialLot.getMaterialLotId().length() - 3);
                }
                twoDCode = MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE + "weizhi" + StringUtils.UNDERLINE_CODE + dateAndNumber + StringUtils.UNDERLINE_CODE + flow;
                parameterMap.put("FLOW", flow);
                parameterMap.put("BOXSEQ", boxSeq);
                parameterMap.put("TWODCODE", twoDCode);
                parameterMap.put("printCount", "1");

                parameterMapList.add(parameterMap);
            } else {
                //如果勾选打印箱中真空包标签信息，需要按照整包和零包进行分组，再按照是否打印真空包flag组装Map
                if(MaterialLot.PRINT_CHECK.equals(printVboxLabelFlag)){
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
                    if( CollectionUtils.isNotEmpty(fullPackageMLotList)){
                        parameterMapList = getQRCodeLabelPrintParmByVboxStandardQty(parameterMapList ,fullPackageMLotList, date, "VZ");
                    }
                    if( CollectionUtils.isNotEmpty(zeroPackageMLotList)){
                        parameterMapList = getQRCodeLabelPrintParmByVboxStandardQty(parameterMapList ,zeroPackageMLotList, date, "VL");
                    }
                } else {
                    Map<String, String> parameterMap = Maps.newHashMap();
                    printSeq = generatorMLotsTransId(MaterialLot.GENERATOR_QRCODE_LABEL_PRINT_SEQ_RULE);
                    flow = printSeq + StringUtils.UNDERLINE_CODE  + printSeq;
                    dateAndNumber = date + StringUtils.UNDERLINE_CODE + StringUtil.leftPad(materialLot.getCurrentQty().toString() , 6 , "0");
                    twoDCode = MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE + "weizhi" + StringUtils.UNDERLINE_CODE + dateAndNumber + StringUtils.UNDERLINE_CODE + flow;
                    parameterMap.put("VENDER", MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE);
                    parameterMap.put("MATERIALCODE", "weizhi" + StringUtils.UNDERLINE_CODE);
                    parameterMap.put("DATEANDNUMBER", dateAndNumber + StringUtils.UNDERLINE_CODE);
                    parameterMap.put("FLOW", flow);
                    parameterMap.put("BOXSEQ", "BL");
                    parameterMap.put("TWODCODE", twoDCode);
                    parameterMapList.add(parameterMap);
                }
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
    private List<Map<String, String>> getQRCodeLabelPrintParmByVboxStandardQty(List<Map<String, String>> parameterMapList, List<MaterialLot> materialLotList, String date, String boxStart)throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            String dateAndNumber = StringUtils.EMPTY;
            String printSeq = StringUtils.EMPTY;
            String flow = StringUtils.EMPTY;
            String boxSeq = StringUtils.EMPTY;
            String twoDCode = StringUtils.EMPTY;

            Long fullPackageTotalQty = materialLotList.stream().collect(Collectors.summingLong(mesPackedLot -> mesPackedLot.getCurrentQty().longValue()));
            String  startPrintSeq = "";
            for(MaterialLot fullMlot : materialLotList){
                parameterMap = Maps.newHashMap();
                printSeq = generatorMLotsTransId(MaterialLot.GENERATOR_QRCODE_LABEL_PRINT_SEQ_RULE);
                if(StringUtils.isNullOrEmpty(startPrintSeq)){
                    startPrintSeq = printSeq;
                }
                flow = printSeq + StringUtils.UNDERLINE_CODE + printSeq;
                dateAndNumber = date + StringUtils.UNDERLINE_CODE + StringUtil.leftPad(fullMlot.getCurrentQty().toString() , 6 , "0");
                boxSeq = boxStart + fullMlot.getMaterialLotId().substring(fullMlot.getMaterialLotId().length() - 3);
                twoDCode = MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE + "weizhi" + StringUtils.UNDERLINE_CODE + dateAndNumber + StringUtils.UNDERLINE_CODE + flow;
                parameterMap.put("VENDER", MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE);
                parameterMap.put("MATERIALCODE", "weizhi" + StringUtils.UNDERLINE_CODE);
                parameterMap.put("DATEANDNUMBER", dateAndNumber + StringUtils.UNDERLINE_CODE);
                parameterMap.put("FLOW", flow);
                parameterMap.put("BOXSEQ", boxSeq);
                parameterMap.put("TWODCODE", twoDCode);
                parameterMap.put("printCount", "1");
                parameterMapList.add(parameterMap);
            }
            //获取箱标签信息
            parameterMap = Maps.newHashMap();
            dateAndNumber = date + StringUtils.UNDERLINE_CODE + StringUtil.leftPad( fullPackageTotalQty.toString(), 6 , "0");
            flow = startPrintSeq + StringUtils.UNDERLINE_CODE + printSeq;
            twoDCode = MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE + "weizhi" + StringUtils.UNDERLINE_CODE + dateAndNumber + StringUtils.UNDERLINE_CODE + flow;
            parameterMap.put("VENDER", MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE);
            parameterMap.put("MATERIALCODE", "weizhi" + StringUtils.UNDERLINE_CODE);
            parameterMap.put("DATEANDNUMBER", dateAndNumber + StringUtils.UNDERLINE_CODE);
            parameterMap.put("FLOW", flow);
            parameterMap.put("BOXSEQ", "BL");
            parameterMap.put("TWODCODE", twoDCode);
            parameterMap.put("printCount", "2");
            parameterMapList.add(parameterMap);

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
            Map<String, String> parameterMap = Maps.newHashMap();
            SimpleDateFormat formatter = new SimpleDateFormat(MaterialLot.PRINT_DATE_PATTERN);
            String date = formatter.format(new Date());
            String printSeq = generatorMLotsTransId(MaterialLot.GENERATOR_QRCODE_LABEL_PRINT_SEQ_RULE);
            String flow = printSeq + StringUtils.UNDERLINE_CODE  + printSeq;
            String dateAndNumber = date + StringUtils.UNDERLINE_CODE + StringUtil.leftPad(materialLot.getCurrentQty().toString() , 6 , "0");
            String twoDCode = MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE + "weizhi" + StringUtils.UNDERLINE_CODE + dateAndNumber + StringUtils.UNDERLINE_CODE + flow;
            parameterMap.put("VENDER", MaterialLot.GC_CODE + StringUtils.UNDERLINE_CODE);
            parameterMap.put("MATERIALCODE", "weizhi" + StringUtils.UNDERLINE_CODE);
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

    public List<MaterialLotUnit> validateAndSetWaferSource(String importType, String checkFourCodeFlag, List<MaterialLotUnit> materialLotUnitList) throws ClientException{
        try {
            //按照载具号分组，相同载具号的产品型号、晶圆数量必须一致(暂时只对WLA未测（-2.5）模板做特殊验证处理)
            String waferSource = StringUtils.EMPTY;
            Map<String, List<MaterialLotUnit>> mLotUnitMap = new HashMap<>();
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getLotId));
            if(MaterialLotUnit.WLA_UNMEASURED.equals(importType)){
                Pattern pattern = Pattern.compile("^[_][a-zA-Z0-9]{4}$");
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
                    materialLotUnit.setReserved50("5");
                    materialLotUnit.setReserved49(MaterialLot.IMPORT_WLA);
                    Matcher matcher = pattern.matcher(materialLotUnit.getReserved38());
                    if(!matcher.find()){
                        throw new ClientParameterException(GcExceptions.MATERIAL_LOT_FOUR_CODE_ERROR, materialLotUnit.getLotId());
                    }
                }
            } else if(MaterialLotUnit.FAB_SENSOR.equals(importType) || MaterialLotUnit.FAB_SENSOR_2UNMEASURED.equals(importType) || MaterialLotUnit.SENSOR_CP_KLT.equals(importType)
                    || MaterialLotUnit.SENSOR_CP.equals(importType) || MaterialLotUnit.SENSOR_UNMEASURED.equals(importType)){
                //根据页面是否勾选四位码检测flag，验证来料信息四位码是否符合，不符合不让导入
                if("check".equals(checkFourCodeFlag)){
                    validateMaterialLotUnitFourCode(materialLotUnitList);
                }
                //验证同一个载具号的晶圆型号是否一致
                for (String lotId : materialLotUnitMap.keySet()) {
                    mLotUnitMap = materialLotUnitMap.get(lotId).stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialName));
                    if(mLotUnitMap.size() > 1){
                        throw new ClientParameterException(GcExceptions.MATERIALNAME_IS_NOT_SAME, lotId);
                    }
                    for(String materialName : mLotUnitMap.keySet()){
                        String materialNameQty = materialName.split("-")[1];
                        //通过晶圆型号末尾的数字获取不同的Wafer Source
                        if(materialNameQty.equals("1") || materialNameQty.equals("2")){
                            waferSource = "1";
                        } else if(materialNameQty.equals("2.1")) {
                            waferSource = "2";
                        } else {
                            throw new ClientParameterException(GcExceptions.MATERIALNAME_IS_ERROR, materialName);
                        }
                        List<MaterialLotUnit> materialLotUnits = mLotUnitMap.get(materialName);
                        for(MaterialLotUnit materialLotUnit : materialLotUnits){
                            materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_CP);
                            materialLotUnit.setReserved50(waferSource);
                            materialLotUnit.setReserved49(MaterialLot.IMPORT_SENSOR_CP);
                        }
                    }
                }
            }  else if(MaterialLotUnit.FAB_LCD_PTC.equals(importType) || MaterialLotUnit.FAB_LCD_SILTERRA.equals(importType)
                    || MaterialLotUnit.LCD_CP_25UNMEASURED.equals(importType) || MaterialLotUnit.LCD_CP_25UNMEASURED.equals(importType)){
                for (String lotId : materialLotUnitMap.keySet()) {
                    mLotUnitMap = materialLotUnitMap.get(lotId).stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialName));
                    if(mLotUnitMap.size() > 1){
                        throw new ClientParameterException(GcExceptions.MATERIALNAME_IS_NOT_SAME, lotId);
                    }
                }
                for(String materialName : mLotUnitMap.keySet()){
                    String materialNameQty = materialName.split("-")[1];
                    if(materialNameQty.equals("1") || materialNameQty.equals("2.5")){
                        waferSource = "3";
                    } else if(materialNameQty.equals("2.6")) {
                        waferSource = "4";
                    } else {
                        throw new ClientParameterException(GcExceptions.MATERIALNAME_IS_ERROR, materialName);
                    }
                    List<MaterialLotUnit> materialLotUnits = mLotUnitMap.get(materialName);
                    for(MaterialLotUnit materialLotUnit : materialLotUnits){
                        materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_CP);
                        materialLotUnit.setReserved50(waferSource);
                        materialLotUnit.setReserved49(MaterialLot.IMPORT_LCD_CP);
                    }
                }
            } else if(MaterialLotUnit.SENSOR_PACK_RETURN_COGO.equals(importType) || MaterialLotUnit.SENSOR_PACK_RETURN.equals(importType)
                    || MaterialLotUnit.SENSOR_TPLCC.equals(importType)){
                for (MaterialLotUnit materialLotUnit : materialLotUnitList) {
                    materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_SENSOR);
                    materialLotUnit.setReserved50("9");
                    materialLotUnit.setReserved49(MaterialLot.IMPORT_SENSOR);
                }
            } else {
                for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                    if(MaterialLotUnit.WLT_PACK_RETURN.equals(importType)){
                        materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_WLT);
                        materialLotUnit.setReserved50("7");
                        materialLotUnit.setReserved49(MaterialLot.IMPORT_WLT);
                    } else if(MaterialLotUnit.COB_FINISH_PRODUCT.equals(importType)){
                        materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_COB);
                        materialLotUnit.setReserved50("16");
                        materialLotUnit.setReserved49(MaterialLot.IMPORT_COB);
                    } else if(MaterialLotUnit.SOC_FINISH_PRODUCT.equals(importType)){
                        materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_SOC);
                        materialLotUnit.setReserved50("18");
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
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            for(MaterialLot materialLot : materialLots){
                Warehouse warehouse = new Warehouse();
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved13())){
                    warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                }
                if(warehouse == null){
                    throw new ClientParameterException(GcExceptions.WAREHOUSE_CANNOT_EMPTY);
                }
                String warehosueName = warehouse.getName();
                materialLotUnitService.receiveMLotWithUnit(materialLot, warehosueName);
                ErpInStock erpInStock = new ErpInStock();
                if(StringUtils.isNullOrEmpty(materialLot.getProductType())){
                    erpInStock.setProdCate(MaterialLot.PRODUCT_TYPE);
                } else {
                    erpInStock.setProdCate(materialLot.getProductType());
                }
                erpInStock.setMaterialLot(materialLot);
                if(ErpInStock.WAREHOUSE_ZJ_STOCK.equals(warehosueName)){
                    erpInStock.setWarehouse(ErpInStock.ZJ_STOCK);
                } else if(ErpInStock.WAREHOUSE_SH_STOCK.equals(warehosueName)){
                    erpInStock.setWarehouse(ErpInStock.SH_STOCK);
                } else if(ErpInStock.WAREHOUSE_HK_STOCK.equals(warehosueName)){
                    erpInStock.setWarehouse(ErpInStock.HK_STOCK);
                } else {
                    throw new ClientParameterException(GcExceptions.ERP_WAREHOUSE_CODE_IS_UNDEFINED, warehosueName);
                }
                erpInStockRepository.save(erpInStock);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取物料编码标签打印参数
     * @param materialLotList
     * @param printType
     * @return
     */
    public List<Map<String, String>> getMlotCodePrintParameter(List<MaterialLot> materialLotList, String printType) throws ClientException {
        try {
            ThreadLocalContext.getSessionContext().buildTransInfo();
            List<Map<String, String>> parameterMapList = Lists.newArrayList();
            //获取当前日期，时间格式yyyy-MM-dd
            SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
            Calendar calendar = Calendar.getInstance();
            String date = formatter.format(new Date());

            if(MLotCodePrint.GENERAL_MLOT_LABEL.equals(printType)){
                for(MaterialLot materialLot : materialLotList){
                    Map<String, String> parameterMap = Maps.newHashMap();
                    Warehouse warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                    long documentLineRrn = Long.parseLong(materialLot.getReserved16());
                    DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLineRrn);
                    String productType = StringUtils.EMPTY;
                    String materialName = materialLot.getMaterialName();
                    String [] materialNameArray = materialName.split(StringUtils.SPLIT_CODE);
                    if(materialNameArray.length >= 3){
                        productType = materialName.substring(0, materialName.indexOf(StringUtils.SPLIT_CODE,materialName.indexOf(StringUtils.SPLIT_CODE)+1 ));
                    } else {
                        productType = materialName;
                    }
                    parameterMap.put("CUSTOMER", documentLine.getReserved8());
                    //TODO 物料编码暂时无数据来源  后续补上
                    parameterMap.put("MLOTCODE", "1111111111111");
                    if(warehouse.getName().equals(WAREHOUSE_HK)){
                        parameterMap.put("SUPPLIER", MLotCodePrint.HK_SUPPLIER);
                    } else {
                        parameterMap.put("SUPPLIER", MLotCodePrint.SH_SUPPLIER);
                    }
                    parameterMap.put("CURRENTQTY", materialLot.getCurrentQty().toString());
                    parameterMap.put("ORDERID", documentLine.getDocId());
                    parameterMap.put("OUTDATE", date);
                    parameterMap.put("DELIVERYPLACE", MLotCodePrint.DELIVERY_PLACE);
                    parameterMap.put("PRODUCTTYPE", productType);
                    parameterMap.put("MLOTID", materialLot.getMaterialLotId());

                    if(StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                        parameterMap.put("LABEL", MLotCodePrint.VBOX_LABEL);
                        parameterMap.put("printCount", "1");
                    } else {
                        parameterMap.put("LABEL", MLotCodePrint.BOX_LABEL);
                        parameterMap.put("printCount", "2");
                    }
                    parameterMap.put("portId", MLotCodePrint.GENERAL_MLOT_PORTID);
                    parameterMapList.add(parameterMap);
                }
            } else if (MLotCodePrint.OPHELION_MLOT_LABEL.equals(printType)){
                for(MaterialLot materialLot : materialLotList){
                    Map<String, String> parameterMap = Maps.newHashMap();
                    String startDate = formatter.format(materialLot.getReceiveDate());
                    calendar.setTime(materialLot.getReceiveDate());
                    calendar.add(Calendar.YEAR, +1);
                    String endDate = formatter.format(calendar.getTime());
                    String [] endDateStrArray = endDate.split(StringUtils.SPLIT_CODE);

                    parameterMap.put("SUPPLIERCODE", MLotCodePrint.SUPPLIER_CODE);
                    parameterMap.put("ORDERID", materialLot.getReserved17());
                    parameterMap.put("MATERIALCODE", "1111111111");
                    parameterMap.put("CURRENTQTY", materialLot.getCurrentQty().toString());
                    parameterMap.put("MLOTID", materialLot.getMaterialLotId());
                    parameterMap.put("STARTDATE", startDate);
                    if(endDateStrArray[1].equals("02") && endDateStrArray[2].equals("29")){
                        endDate = endDateStrArray[0] + StringUtils.SPLIT_CODE + endDateStrArray[1] + StringUtils.SPLIT_CODE + "28";
                        parameterMap.put("ENDDATE", endDate);
                    } else {
                        parameterMap.put("ENDDATE", endDate);
                    }
                    parameterMap.put("PRINTDATE", date);
                    parameterMap.put("QC", MLotCodePrint.QC);

                    formatter = new SimpleDateFormat(MLotCodePrint.DATE_PATTERN);
                    String effectiveDate = formatter.format(materialLot.getReceiveDate());//有效日期
                    String expirationDate = formatter.format(calendar.getTime());//失效日期
                    if(expirationDate.endsWith("0229")){
                        expirationDate = expirationDate.substring(0,2) + "0228";
                    }
                    String code = MLotCodePrint.SUPPLIER_CODE + "|"  + "1111" + "|" + materialLot.getMaterialLotId() + "|"
                            + materialLot.getCurrentSubQty().toString() + "|"  + effectiveDate + "|" + expirationDate;
                    parameterMap.put("CODE", code);

                    if(StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                        parameterMap.put("printCount", "1");
                    } else {
                        parameterMap.put("printCount", "2");
                    }
                    parameterMap.put("portId", MLotCodePrint.OPHELION_MLOT_PORTID);
                    parameterMapList.add(parameterMap);
                }
            }

            return parameterMapList;
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
                //判断即将同步的数据中是否有同ccode不同createseq
                for (String documentId : documentIdMap.keySet()) {
                    List<ErpMaterialOutaOrder> documentIdList = documentIdMap.get(documentId);
                    //把即将同步的同Ccode数据按createseq分组
                    Map<String, List<ErpMaterialOutaOrder>> sameCreateSeqOrder = documentIdList.stream().filter(erpMaterialOutaOrder -> !StringUtils.isNullOrEmpty(erpMaterialOutaOrder.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpMaterialOutaOrder :: getCreateSeq));
                    //由于取消值为WaferIssueA的CATEGORY，所以用WaferIssueOrder替代OtherIssueOrder
                    List<WaferIssueOrder> otherIssueOrderList = waferIssueOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    WaferIssueOrder otherIssueOrder;
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

                    BigDecimal totalQty = BigDecimal.ZERO;

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpMaterialOutaOrder erpMaterialOutaOrder : documentIdList) {
                        try {
                            DocumentLine documentLine = null;
                            if (otherIssueOrder.getObjectRrn() != null) {
                                documentLine = documentLineRepository.findByDocRrnAndReserved1(otherIssueOrder.getObjectRrn(), String.valueOf(erpMaterialOutaOrder.getSeq()));
                                if (documentLine != null) {
                                    if (ErpMaterialOutaOrder.SYNC_STATUS_CHANGED.equals(erpMaterialOutaOrder.getSynStatus())) {
                                        if (documentLine != null && documentLine.getHandledQty().compareTo(erpMaterialOutaOrder.getIquantity()) > 0) {
                                            throw new ClientException("gc.order_handled_qty_gt_qty");
                                        }
                                    }
                                }
                            }
                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            if (documentLine == null) {
                                documentLine = new DocumentLine();
                                Material material = mmsService.getRawMaterialByName(erpMaterialOutaOrder.getCinvcode());
                                if (material == null) {
                                    throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, erpMaterialOutaOrder.getCinvcode());
                                }
                                documentLine.setDocId(documentId);
                                documentLine.setErpCreated(DateUtils.parseDate(erpMaterialOutaOrder.getDdate()));
                                documentLine.setMaterialRrn(material.getObjectRrn());
                                documentLine.setMaterialName(material.getName());
                                documentLine.setReserved1(String.valueOf(erpMaterialOutaOrder.getSeq()));
                                documentLine.setReserved2(erpMaterialOutaOrder.getSecondcode());
                                documentLine.setReserved3(erpMaterialOutaOrder.getGrade());
                                documentLine.setReserved5(erpMaterialOutaOrder.getCmaker());
                                documentLine.setReserved6(erpMaterialOutaOrder.getChandler());
                                documentLine.setReserved7(erpMaterialOutaOrder.getOther1());
                                documentLine.setReserved9(ErpMaterialOutaOrder.CATEGORY_WAFER_ISSUEA);
                                documentLine.setReserved31(ErpMaterialOutaOrder.SOURCE_TABLE_NAME);
                            }
                            documentLine.setQty(erpMaterialOutaOrder.getIquantity());
                            documentLine.setUnHandledQty(erpMaterialOutaOrder.getLeftNum());
                            totalQty = totalQty.add(erpMaterialOutaOrder.getIquantity());
                            documentLines.add(documentLine);

                            otherIssueOrder.setOwner(erpMaterialOutaOrder.getChandler());
                            otherIssueOrder.setReserved32(erpMaterialOutaOrder.getCreateSeq());
                            asyncSuccessSeqList.add(erpMaterialOutaOrder.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpMaterialOutaOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpMaterialOutaOrder.setErrorMemo(e.getMessage());
                            erpMaterialOutAOrderRepository.save(erpMaterialOutaOrder);
                        }
                    }
                    otherIssueOrder.setQty(totalQty);
                    otherIssueOrder.setUnHandledQty(otherIssueOrder.getQty().subtract(otherIssueOrder.getHandledQty()));

                    otherIssueOrder.setReserved31(ErpMaterialOutaOrder.SOURCE_TABLE_NAME);
                    otherIssueOrder = (WaferIssueOrder) baseService.saveEntity(otherIssueOrder);

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(otherIssueOrder);
                        baseService.saveEntity(documentLine);
                    }
                }
                if (CollectionUtils.isNotEmpty(asyncSuccessSeqList)) {
                    erpMaterialOutAOrderRepository.updateSynStatusAndErrorMemoBySeq(ErpMaterialOutaOrder.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, asyncSuccessSeqList);
                }
                if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                    erpMaterialOutAOrderRepository.updateSynStatusAndErrorMemoBySeq(ErpMaterialOutaOrder.SYNC_STATUS_SYNC_ERROR,
                            ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, asyncDuplicateSeqList);
                }
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
            List<ErpSoa> erpSos = erpSoaOrderRepository.findBySynStatusNotIn(Lists.newArrayList(ErpSoa.SYNC_STATUS_OPERATION, ErpSoa.SYNC_STATUS_SYNC_SUCCESS));
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
                    OtherStockOutOrder otherStockOutOrder;
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
                    BigDecimal totalQty = BigDecimal.ZERO;

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpSoa erpSoa : documentIdList) {
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
                                Material material = mmsService.getRawMaterialByName(erpSoa.getInvcode());
                                if (material == null) {
                                    throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, erpSoa.getInvcode());
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
                            }
                            documentLine.setQty(erpSoa.getQuantity());
                            documentLine.setUnHandledQty(erpSoa.getQuantity());
                            documentLine.setUnReservedQty(erpSoa.getQuantity());
                            totalQty = totalQty.add(erpSoa.getQuantity());
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
                            erpSoa.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpSoa.setErrorMemo(e.getMessage());
                            erpSoaOrderRepository.save(erpSoa);
                        }
                    }
                    otherStockOutOrder.setQty(totalQty);
                    otherStockOutOrder.setUnHandledQty(otherStockOutOrder.getQty().subtract(otherStockOutOrder.getHandledQty()));
                    otherStockOutOrder.setUnReservedQty(totalQty);
                    otherStockOutOrder = (OtherStockOutOrder) baseService.saveEntity(otherStockOutOrder);

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
                    erpSoaOrderRepository.updateSynStatusAndErrorMemoBySeq(ErpSoa.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, asyncSuccessSeqList);
                }
                if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                    erpSoaOrderRepository.updateSynStatusAndErrorMemoBySeq(ErpSoa.SYNC_STATUS_SYNC_ERROR,
                            ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, asyncDuplicateSeqList);
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
            List<ErpSob> erpSobs = erpSobOrderRepository.findBySynStatusNotIn(Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_SUCCESS));
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
                    OtherShipOrder otherShipOrder;
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
                    BigDecimal totalQty = BigDecimal.ZERO;

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpSob erpSob : documentIdList) {
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
                                Material material = mmsService.getRawMaterialByName(erpSob.getCinvcode());
                                if (material == null) {
                                    throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, erpSob.getCinvcode());
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

                                documentLine.setDocType(erpSob.getCvouchtype());
                                documentLine.setDocName(erpSob.getCvouchname());
                                documentLine.setDocBusType(erpSob.getCbustype());
                                documentLine.setDocSource(erpSob.getCsource());
                                documentLine.setWarehouseCode(erpSob.getCwhcode());
                                documentLine.setWarehouseName(erpSob.getCwhname());
                            }
                            documentLine.setQty(erpSob.getIquantity());
                            documentLine.setUnHandledQty(erpSob.getLeftNum());
                            documentLine.setUnReservedQty(erpSob.getIquantity());
                            totalQty = totalQty.add(erpSob.getIquantity());
                            documentLines.add(documentLine);

                            // 同一个单据下，所有的客户都是一样的。
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
                            erpSob.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpSob.setErrorMemo(e.getMessage());
                            erpSobOrderRepository.save(erpSob);
                        }
                    }
                    otherShipOrder.setQty(totalQty);
                    otherShipOrder.setUnHandledQty(otherShipOrder.getQty().subtract(otherShipOrder.getHandledQty()));
                    otherShipOrder.setUnReservedQty(totalQty);
                    otherShipOrder = (OtherShipOrder) baseService.saveEntity(otherShipOrder);

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
                    erpSobOrderRepository.updateSynStatusAndErrorMemoBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, asyncSuccessSeqList);
                }
                if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                    erpSobOrderRepository.updateSynStatusAndErrorMemoBySeq(ErpSo.SYNC_STATUS_SYNC_ERROR,
                            ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, asyncDuplicateSeqList);
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

}
