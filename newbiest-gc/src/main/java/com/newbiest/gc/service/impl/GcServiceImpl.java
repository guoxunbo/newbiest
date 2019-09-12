package com.newbiest.gc.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.*;
import com.newbiest.common.exception.ContextException;
import com.newbiest.gc.GcExceptions;
import com.newbiest.gc.model.*;
import com.newbiest.gc.repository.*;
import com.newbiest.gc.service.GcService;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.state.model.MaterialEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
     * 物料批次根据重测单进行重测，更新单据数据以及，更改ERP的中间表数据
     *  documentLine 产品型号 materialName，二级代码 reserved2，等级 reserved3 一致
     *  materialLot 产品型号 materialName，二级代码 reserved1，等级grade 一致
     */
    public void reTest(DocumentLine documentLine, List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            for (MaterialLot materialLot : materialLots) {
                try {
                    Assert.assertEquals(documentLine.getMaterialName(), materialLot.getMaterialName());
                    Assert.assertEquals(documentLine.getReserved2(), materialLot.getReserved1());
                    Assert.assertEquals(documentLine.getReserved3(), materialLot.getGrade());
                } catch (AssertionError e) {
                    throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, materialLot.getMaterialLotId());
                }
            }
            BigDecimal handledQty = BigDecimal.ZERO;

            for (MaterialLot materialLot : materialLots) {
                handledQty = handledQty.add(materialLot.getCurrentQty());

                // 变更事件，并清理掉库存
                materialLot.setCurrentQty(BigDecimal.ZERO);
                mmsService.changeMaterialLotState(materialLot, "ReTest", StringUtils.EMPTY);
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, "ReTest");
                materialLotHistoryRepository.save(history);

                materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
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
            ReTestOrder reTestOrder = (ReTestOrder) reTestOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            reTestOrder.setHandledQty(reTestOrder.getHandledQty().add(handledQty));
            reTestOrder.setUnHandledQty(reTestOrder.getUnHandledQty().subtract(handledQty));
            reTestOrderRepository.save(reTestOrder);

            Optional<ErpMaterialOutOrder> erpMaterialOutOrderOptional = erpMaterialOutOrderRepository.findById(Long.valueOf(documentLine.getReserved1()));
            if (!erpMaterialOutOrderOptional.isPresent()) {
                throw new ClientParameterException(GcExceptions.ERP_RETEST_ORDER_IS_NOT_EXIST, documentLine.getReserved1());
            }

            //TODO 待更新完成数量 确定栏位
            ErpMaterialOutOrder erpMaterialOutOrder = erpMaterialOutOrderOptional.get();
            erpMaterialOutOrder.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
            erpMaterialOutOrder.setLeftNum(erpMaterialOutOrder.getLeftNum().subtract(handledQty));
            erpMaterialOutOrderRepository.save(erpMaterialOutOrder);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
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
                try {
                    Assert.assertEquals(documentLine.getMaterialName(), materialLot.getMaterialName());
                    Assert.assertEquals(documentLine.getReserved2(), materialLot.getReserved1());
                    Assert.assertEquals(documentLine.getReserved3(), materialLot.getGrade());
                    Assert.assertEquals(documentLine.getReserved7(), materialLot.getReserved6());
                } catch (AssertionError e) {
                    throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, materialLot.getMaterialLotId());
                }
            }

            BigDecimal handledQty = BigDecimal.ZERO;
            for (MaterialLot materialLot : materialLots) {
                handledQty = handledQty.add(materialLot.getCurrentQty());

                // 变更事件，并清理掉库存
                materialLot.setCurrentQty(BigDecimal.ZERO);
                mmsService.changeMaterialLotState(materialLot,  MaterialEvent.EVENT_STOCK_OUT, StringUtils.EMPTY);
                materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_STOCK_OUT);
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
            if (StringUtils.isNullOrEmpty(erpSo.getOther3())) {
                erpSo.setOther3(handledQty.toPlainString());
            } else {
                BigDecimal docHandledQty = new BigDecimal(erpSo.getOther3());
                docHandledQty = docHandledQty.add(handledQty);
                erpSo.setOther3(docHandledQty.toPlainString());
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
                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            if (documentLine == null) {
                                documentLine = new DocumentLine();
                                Material material = mmsService.getRawMaterialByName(erpSo.getCinvcode());
                                if (material == null) {
                                    throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, erpSo.getCinvcode());
                                }
                                documentLine.setDocId(documentId);
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
                materialLot = mmsService.changeMaterialLotState(materialLot, EVENT_OQC, checkResult);
//                GC要求只记录NG的判定历史即可
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

    public List<StockOutCheck> getStockOutCheckList() throws ClientException {
        List<StockOutCheck> stockOutChecks = Lists.newArrayList();
        List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(REFERENCE_NAME_STOCK_OUT_CHECK_ITEM_LIST, NBReferenceList.CATEGORY_OWNER);
        if (CollectionUtils.isNotEmpty(nbReferenceList)) {
            for (NBOwnerReferenceList nbOwnerReference : nbReferenceList) {
                StockOutCheck stockOutCheck = new StockOutCheck();
                stockOutCheck.setName(nbOwnerReference.getValue());
                stockOutChecks.add(stockOutCheck);
            }
        }
        return stockOutChecks;
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

    public void judgeMaterialLot(MaterialLot materialLot, String judgeGrade, String judgeCode) {
        materialLot.setReserved9(judgeGrade);
        materialLot.setReserved10(judgeCode);
        materialLotRepository.save(materialLot);
        MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_JUDGE);
        materialLotHistoryRepository.save(history);
    }

    /**
     * 对包装后的物料批次进行判等
     * @throws ClientException
     */
    public void judgePackedMaterialLot(List<MaterialLot> materialLots, String judgeGrade, String judgeCode) throws ClientException{
        try {
            //GC只会全部包装。故此处，直接用ParentMaterialLotId做包装号。
            Map<String, List<MaterialLot>> packedLotMap = materialLots.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true))
                    .collect(Collectors.groupingBy(MaterialLot::getParentMaterialLotId));
            for (String packageMLotId : packedLotMap.keySet())  {
                MaterialLot parentMLot = mmsService.getMLotByMLotId(packageMLotId, true);
                judgeMaterialLot(parentMLot, judgeGrade, judgeCode);
                for (MaterialLot packagedMLot : packedLotMap.get(packageMLotId)) {
                    judgeMaterialLot(packagedMLot, judgeGrade, judgeCode);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }

    }

}
