package com.newbiest.gc.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.model.NBQuery;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.repository.QueryRepository;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.service.VersionControlService;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.*;
import com.newbiest.commom.sm.exception.StatusMachineExceptions;
import com.newbiest.common.exception.ContextException;
import com.newbiest.gc.GcExceptions;
import com.newbiest.gc.model.*;
import com.newbiest.gc.repository.*;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.mms.state.model.MaterialEvent;
import com.newbiest.mms.state.model.MaterialStatusModel;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.Transformers;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.nio.cs.ext.MacArabic;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
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

    public static final String REFERENCE_NAME_STOCK_OUT_CHECK_ITEM_LIST = "StockOutCheckItemList";
    public static final String REFERENCE_NAME_PACK_CASE_CHECK_ITEM_LIST = "PackCaseCheckItemList";

    public static final String EVENT_OQC = "OQC";

    public static final String WAREHOUSE_SH = "SH_STOCK";
    public static final String WAREHOUSE_ZJ = "ZJ_STOCK";

    @Autowired
    MesPackedLotRepository mesPackedLotRepository;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialLotHistoryRepository materialLotHistoryRepository;

    @Autowired
    MmsService mmsService;

    @Autowired
    BaseService baseService;

    @Autowired
    UIService uiService;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    ErpSoRepository erpSoRepository;

    @Autowired
    ErpMaterialOutOrderRepository erpMaterialOutOrderRepository;

    @Autowired
    DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    ReTestOrderRepository reTestOrderRepository;

    @Autowired
    DocumentLineRepository documentLineRepository;

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
//                            validationDocLine(documentLine, materialLot);
                            waitForReservedMaterialLots.add(materialLot);
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
//                validationDocLine(documentLine, materialLot);
                BigDecimal currentQty = materialLot.getCurrentQty();
                reservedQty = reservedQty.add(currentQty);
                if (unReservedQty.compareTo(reservedQty) < 0) {
                    throw new ClientParameterException(GcExceptions.RESERVED_OVER_QTY);
                }
                materialLot.setReservedQty(materialLot.getCurrentQty());
                materialLot.setReserved16(documentLine.getObjectRrn().toString());
                materialLot.setReserved17(documentLine.getDocId());
                materialLot.setReserved18(stockNote);
                materialLot = materialLotRepository.saveAndFlush(materialLot);
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_RESERVED);
                materialLotHistoryRepository.save(history);
            }

            documentLine.setUnReservedQty(unReservedQty.subtract(reservedQty));
            BigDecimal lineReservedQty = documentLine.getReservedQty() == null ? BigDecimal.ZERO : documentLine.getReservedQty();
            documentLine.setReservedQty(lineReservedQty.add(reservedQty));
            documentLine = documentLineRepository.saveAndFlush(documentLine);

            DeliveryOrder deliveryOrder = (DeliveryOrder) deliveryOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            BigDecimal docReservedQty = deliveryOrder.getReservedQty() == null ? BigDecimal.ZERO : deliveryOrder.getReservedQty();
            deliveryOrder.setUnReservedQty(deliveryOrder.getUnReservedQty().subtract(reservedQty));
            deliveryOrder.setReservedQty(docReservedQty.add(reservedQty));
            deliveryOrderRepository.save(deliveryOrder);

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
            for (String docLine : docLineReservedMaterialLotMap.keySet()) {
                List<MaterialLot> docLineReservedMaterialLots = docLineReservedMaterialLotMap.get(docLine);
                DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(docLine));
                BigDecimal unReservedQty = BigDecimal.ZERO;
                for (MaterialLot materialLot : docLineReservedMaterialLots) {
                    unReservedQty = unReservedQty.add(materialLot.getReservedQty());
                    materialLot.setReservedQty(BigDecimal.ZERO);
                    materialLot.setReserved16(StringUtils.EMPTY);
                    materialLot.setReserved17(StringUtils.EMPTY);

                    materialLot = materialLotRepository.saveAndFlush(materialLot);
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_UN_RESERVED);
                    materialLotHistoryRepository.save(history);
                }
                documentLine.setReservedQty(documentLine.getReservedQty().subtract(unReservedQty));
                documentLine.setUnReservedQty(documentLine.getUnHandledQty().add(unReservedQty));
                documentLine = documentLineRepository.saveAndFlush(documentLine);

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
                deliveryOrderRepository.save(deliveryOrder);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public List<DeliveryOrder> recordExpressNumber(List<DeliveryOrder> deliveryOrders) throws ClientException {
        List<DeliveryOrder> deliveryOrderList = Lists.newArrayList();
        for (DeliveryOrder deliveryOrder : deliveryOrders) {
            deliveryOrder = deliveryOrderRepository.saveAndFlush(deliveryOrder);
            deliveryOrderList.add(deliveryOrder);
        }
        return deliveryOrderList;
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

    public void asyncErpMaterialOutOrder() throws ClientException {
        try {
            List<ErpMaterialOutOrder> erpMaterialOutOrders = erpMaterialOutOrderRepository.findBySynStatusNotIn(Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_SUCCESS));
            if (CollectionUtils.isNotEmpty(erpMaterialOutOrders)) {
                Map<String, List<ErpMaterialOutOrder>> documentIdMap = erpMaterialOutOrders.stream().collect(Collectors.groupingBy(ErpMaterialOutOrder :: getCcode));
                for (String documentId : documentIdMap.keySet()) {
                    List<ReTestOrder> reTestOrderList = reTestOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    ReTestOrder reTestOrder;
                    if (CollectionUtils.isEmpty(reTestOrderList)) {
                        reTestOrder = new ReTestOrder();
                        reTestOrder.setStatus(Document.STATUS_OPEN);
                    } else {
                        reTestOrder = reTestOrderList.get(0);
                    }
                    reTestOrder.setName(documentId);
                    BigDecimal totalQty = BigDecimal.ZERO;

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpMaterialOutOrder erpMaterialOutOrder : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (reTestOrder.getObjectRrn() != null) {
                                documentLine = documentLineRepository.findByDocRrnAndReserved1(reTestOrder.getObjectRrn(), String.valueOf(erpMaterialOutOrder.getSeq()));
                                if (documentLine != null) {
                                    if (ErpSo.SYNC_STATUS_CHANGED.equals(erpMaterialOutOrder.getSynStatus())) {
                                        if (documentLine != null && documentLine.getHandledQty().compareTo(erpMaterialOutOrder.getIquantity()) < 0) {
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
                                documentLine.setReserved5(erpMaterialOutOrder.getCmaker());
                                documentLine.setReserved6(erpMaterialOutOrder.getChandler());
                                documentLine.setReserved7(erpMaterialOutOrder.getOther1());
                                documentLine.setReserved9(ReTestOrder.CATEGORY_RETEST);
                            }
                            documentLine.setQty(erpMaterialOutOrder.getIquantity());
                            documentLine.setUnHandledQty(erpMaterialOutOrder.getLeftNum());
                            totalQty = totalQty.add(erpMaterialOutOrder.getIquantity());
                            documentLines.add(documentLine);

                            reTestOrder.setOwner(erpMaterialOutOrder.getChandler());
                            erpMaterialOutOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_SUCCESS);
                            erpMaterialOutOrder.setErrorMemo(StringUtils.EMPTY);
                        } catch (Exception e) {
                            // 修改状态为2
                            erpMaterialOutOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpMaterialOutOrder.setErrorMemo(e.getMessage());
                        }
                        erpMaterialOutOrderRepository.save(erpMaterialOutOrder);
                    }
                    reTestOrder.setQty(totalQty);
                    reTestOrder.setUnHandledQty(reTestOrder.getQty().subtract(reTestOrder.getHandledQty()));
                    reTestOrder.setDocumentLines(documentLines);
                    reTestOrderRepository.save(reTestOrder);
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
            Map<String, List<DocumentLine>> documentLineMap = documentLineList.stream().collect(Collectors.groupingBy(documentLine -> {
                StringBuffer key = new StringBuffer();
                key.append(documentLine.getMaterialName());
                key.append(StringUtils.SPLIT_CODE);

                key.append(documentLine.getReserved2());
                key.append(StringUtils.SPLIT_CODE);

                key.append(documentLine.getReserved3());
                key.append(StringUtils.SPLIT_CODE);

                key.append(documentLine.getReserved7());
                key.append(StringUtils.SPLIT_CODE);
                return key.toString();
            }));

            Map<String, List<MaterialLot>> materialLotMap = materialLots.stream().collect(Collectors.groupingBy(materialLot -> {
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
                        mmsService.changeMaterialLotState(materialLot, "ReTest", StringUtils.EMPTY);
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
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, "ReTest");
                    materialLotHistoryRepository.save(history);
                    if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }
                BigDecimal handledQty = documentLine.getHandledQty().add((documentLine.getUnHandledQty().subtract(unhandedQty)));
                documentLine.setHandledQty(handledQty);
                documentLine.setUnHandledQty(unhandedQty);
                documentLineRepository.save(documentLine);

                ReTestOrder reTestOrder = (ReTestOrder) reTestOrderRepository.findByObjectRrn(documentLine.getDocRrn());
                reTestOrder.setHandledQty(reTestOrder.getHandledQty().add(handledQty));
                reTestOrder.setUnHandledQty(reTestOrder.getUnHandledQty().subtract(handledQty));
                reTestOrderRepository.save(reTestOrder);

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
            for (MaterialLot materialLot : materialLots) {
                validationDocLine(documentLine, materialLot);
            }

            BigDecimal handledQty = BigDecimal.ZERO;
            for (MaterialLot materialLot : materialLots) {
                handledQty = handledQty.add(materialLot.getCurrentQty());
                // 变更事件，并清理掉库存
                materialLot.setCurrentQty(BigDecimal.ZERO);
                materialLot.setReserved12(documentLine.getObjectRrn().toString());
                mmsService.changeMaterialLotState(materialLot,  MaterialEvent.EVENT_SHIP, StringUtils.EMPTY);
                materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_SHIP);
                materialLotHistoryRepository.save(history);
            }

            // 验证当前操作数量是否超过待检查数量
            BigDecimal unHandleQty =  documentLine.getUnHandledQty().subtract(handledQty);
            if (unHandleQty.compareTo(BigDecimal.ZERO) < 0) {
                throw new ClientParameterException(GcExceptions.OVER_DOC_QTY);
            }

            documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());
            documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
            documentLine.setUnHandledQty(unHandleQty);
            documentLineRepository.save(documentLine);

            // 获取到主单据
            DeliveryOrder deliveryOrder = (DeliveryOrder) deliveryOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            deliveryOrder.setHandledQty(deliveryOrder.getHandledQty().add(handledQty));
            deliveryOrder.setUnHandledQty(deliveryOrder.getUnHandledQty().subtract(handledQty));
            deliveryOrderRepository.save(deliveryOrder);

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

    public void asyncErpSo() throws ClientException {
        try {
            List<ErpSo> erpSos = erpSoRepository.findByTypeAndSynStatusNotIn(ErpSo.TYPE_SO, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_SUCCESS));
            if (CollectionUtils.isNotEmpty(erpSos)) {
                Map<String, List<ErpSo>> documentIdMap = erpSos.stream().collect(Collectors.groupingBy(ErpSo :: getCcode));
                for (String documentId : documentIdMap.keySet()) {
                    List<DeliveryOrder> deliveryOrderList = deliveryOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    DeliveryOrder deliveryOrder;
                    if (CollectionUtils.isEmpty(deliveryOrderList)) {
                        deliveryOrder = new DeliveryOrder();
                        deliveryOrder.setStatus(Document.STATUS_OPEN);
                    } else {
                        deliveryOrder = deliveryOrderList.get(0);
                    }
                    deliveryOrder.setName(documentId);
                    BigDecimal totalQty = BigDecimal.ZERO;

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpSo erpSo : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (deliveryOrder.getObjectRrn() != null) {
                                documentLine = documentLineRepository.findByDocRrnAndReserved1(deliveryOrder.getObjectRrn(), String.valueOf(erpSo.getSeq()));
                                if (documentLine != null) {
                                   if (ErpSo.SYNC_STATUS_CHANGED.equals(erpSo.getSynStatus())) {
                                       if (documentLine != null && documentLine.getHandledQty().compareTo(erpSo.getIquantity()) < 0) {
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
                                documentLine.setDocId(documentId);
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
                            }
                            documentLine.setQty(erpSo.getIquantity());
                            documentLine.setUnHandledQty(erpSo.getLeftNum());
                            totalQty = totalQty.add(erpSo.getIquantity());
                            documentLines.add(documentLine);

                            // 同一个单据下，所有的客户都是一样的。
                            deliveryOrder.setSupplierName(erpSo.getCusname());
                            deliveryOrder.setOwner(erpSo.getChandler());
                            if (deliveryOrder.getErpCreated() == null) {
                                deliveryOrder.setErpCreated(erpCreatedDate);
                            } else {
                                if (deliveryOrder.getErpCreated().after(erpCreatedDate)) {
                                    deliveryOrder.setErpCreated(erpCreatedDate);
                                }
                            }
                            erpSo.setSynStatus(ErpSo.SYNC_STATUS_SYNC_SUCCESS);
                            erpSo.setErrorMemo(StringUtils.EMPTY);
                        } catch (Exception e) {
                            // 修改状态为2
                            erpSo.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpSo.setErrorMemo(e.getMessage());
                        }
                        erpSoRepository.save(erpSo);
                    }
                    deliveryOrder.setQty(totalQty);
                    deliveryOrder.setUnHandledQty(deliveryOrder.getQty().subtract(deliveryOrder.getHandledQty()));
                    deliveryOrder.setDocumentLines(documentLines);
                    deliveryOrderRepository.save(deliveryOrder);

                    // 保存单据的时候同步下客户
                    if (!StringUtils.isNullOrEmpty(deliveryOrder.getSupplierName())) {
                        Customer customer = customerRepository.getByName(deliveryOrder.getSupplierName());
                        if (customer == null) {
                            customer = new Customer();
                            customer.setName(deliveryOrder.getSupplierName());
                            customer.setDescription(deliveryOrder.getSupplierName());
                            customerRepository.save(customer);
                        }
                    }
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

    public List<NBOwnerReferenceList> getStockOutCheckList() throws ClientException {
        List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(REFERENCE_NAME_STOCK_OUT_CHECK_ITEM_LIST, NBReferenceList.CATEGORY_OWNER);
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
            Map<String, List<MesPackedLot>> packedLotMap = packedLotList.stream().map(packedLot -> mesPackedLotRepository.findByBoxId(packedLot.getBoxId())).collect(Collectors.groupingBy(MesPackedLot :: getProductId));
            packedLotMap.keySet().forEach(productId -> {
                RawMaterial rawMaterial = mmsService.getRawMaterialByName(productId);
                if (rawMaterial == null) {
                    throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, productId);
                }

                List<MesPackedLot> mesPackedLots = packedLotMap.get(productId);
                for (MesPackedLot mesPackedLot : mesPackedLots) {
                    MaterialLotAction materialLotAction = new MaterialLotAction();
                    materialLotAction.setGrade(mesPackedLot.getGrade());
                    materialLotAction.setTransQty(BigDecimal.valueOf(mesPackedLot.getQuantity()));

                    // 工单前2位是SH的入SH仓库，是ZJ的入浙江仓库
                    String warehouseName = WAREHOUSE_SH;
                    String location = mesPackedLot.getWorkorderId().substring(0, 2);
                    if (location.equalsIgnoreCase("ZJ")) {
                        warehouseName = WAREHOUSE_ZJ;
                    }
                    Warehouse warehouse = mmsService.getWarehouseByName(warehouseName);
                    if (warehouse == null) {
                        warehouse = new Warehouse();
                        warehouse.setName(warehouseName);
                        warehouse = warehouseRepository.saveAndFlush(warehouse);
                    }
                    materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
                    MaterialLot materialLot = mmsService.receiveMLot2Warehouse(rawMaterial, mesPackedLot.getBoxId(), materialLotAction);

                    materialLot.setWorkOrderId(mesPackedLot.getWorkorderId());
                    // 预留栏位赋值
                    materialLot.setReserved1(mesPackedLot.getLevelTwoCode());
                    materialLot.setReserved2(mesPackedLot.getWaferId());
                    materialLot.setReserved3(mesPackedLot.getSalesNote());
                    materialLot.setReserved4(mesPackedLot.getTreasuryNote());
                    materialLot.setReserved5(mesPackedLot.getProductionNote());
                    materialLot.setReserved6(mesPackedLot.getBondedProperty());
                    materialLot.setReserved7(mesPackedLot.getProductCategory());
                    materialLot.setReserved13(warehouse.getObjectRrn().toString());
                    materialLotRepository.save(materialLot);

                    // 修改MES成品批次为接收状态
                    mesPackedLot.setPackedStatus(MesPackedLot.PACKED_STATUS_RECEIVED);
                    mesPackedLotRepository.save(mesPackedLot);
                }
            });

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

    public void validationMaterial(MaterialLot materialLotFirst, MaterialLot materialLot) throws ClientException{
        try {
            Assert.assertEquals(materialLotFirst.getMaterialName(), materialLot.getMaterialName());
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "materialName", materialLotFirst.getMaterialName(), materialLot.getMaterialName());
        }

        try {
            Assert.assertEquals(materialLotFirst.getReserved1(), materialLot.getReserved1());
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "secondcode", materialLotFirst.getReserved1(),  materialLot.getReserved1());
        }

        try {
            Assert.assertEquals(materialLotFirst.getGrade(), materialLot.getGrade());
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "grade", materialLotFirst.getGrade(), materialLot.getGrade());
        }
        try {
            Assert.assertEquals(materialLotFirst.getReserved6(), materialLot.getReserved6());
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "other1", materialLotFirst.getReserved6(), materialLot.getReserved6());
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
            String queryName = "GETPRODUCTINFO";
            RawMaterial rawMaterial = new RawMaterial();
            List<Map> materialList = findEntityMapListByQueryName(queryName,null,0,999,"","");
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
                        rawMaterial.setMaterialCategory(Material.TYPE_PRODUCT);
                        rawMaterial.setMaterialType(Material.TYPE_PRODUCT);

                        rawMaterial = mmsService.saveRawMaterial(rawMaterial);

                        List<MaterialStatusModel> statusModels = materialStatusModelRepository.findByNameAndOrgRrn(Material.DEFAULT_STATUS_MODEL, sc.getOrgRrn());
                        if (CollectionUtils.isNotEmpty(statusModels)) {
                            rawMaterial.setStatusModelRrn(statusModels.get(0).getObjectRrn());
                        } else {
                            throw new ClientException(StatusMachineExceptions.STATUS_MODEL_IS_NOT_EXIST);
                        }
                        rawMaterialRepository.save(rawMaterial);
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

}
