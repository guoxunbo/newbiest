package com.newbiest.gc.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.common.exception.ContextException;
import com.newbiest.gc.GcExceptions;
import com.newbiest.gc.model.*;
import com.newbiest.gc.repository.ErpSoRepository;
import com.newbiest.gc.repository.ErpSoaOrderRepository;
import com.newbiest.gc.repository.MLotDocRuleRepository;
import com.newbiest.gc.repository.OtherStockOutOrderRepository;
import com.newbiest.gc.service.GcService;
import com.newbiest.gc.service.ThreeSideShipService;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.MaterialLotUnitService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.mms.state.model.MaterialEvent;
import com.newbiest.mms.state.model.MaterialStatus;
import com.newbiest.mms.state.model.MaterialStatusCategory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 三方销售Service
 * @author luoguozhang
 * @date 2022/5/12
 */
@Service
@Transactional
@Slf4j
@Data
public class ThreeSideShipServiceImpl implements ThreeSideShipService {

    @Autowired
    MmsService mmsService;

    @Autowired
    GcService gcService;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialLotHistoryRepository materialLotHistoryRepository;

    @Autowired
    MaterialLotUnitRepository materialLotUnitRepository;

    @Autowired
    MaterialLotUnitHisRepository materialLotUnitHisRepository;

    @Autowired
    PackageService packageService;

    @Autowired
    BaseService baseService;

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    MaterialLotUnitService materialLotUnitService;

    @Autowired
    MaterialLotInventoryRepository materialLotInventoryRepository;

    @Autowired
    DocumentLineRepository documentLineRepository;

    @Autowired
    OtherStockOutOrderRepository otherStockOutOrderRepository;

    @Autowired
    ErpSoaOrderRepository erpSoaOrderRepository;

    @Autowired
    MLotDocRuleRepository mLotDocRuleRepository;

    @Autowired
    DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    ErpSoRepository erpSoRepository;

    public static final String WAREHOUSE_SH = "SH_STOCK";
    public static final String WAREHOUSE_ZJ = "ZJ_STOCK";
    public static final String WAREHOUSE_HK = "HK_STOCK";

    public static final String BONDED_PROPERTITY_SH = "SH";
    public static final String BONDED_PROPERTITY_ZSH = "ZSH";
    public static final String BONDED_PROPERTITY_HK = "HK";


    /**
     * COM 销售出
     * @param documentLineList
     * @param materialLotActions
     * @throws ClientException
     */
    @Override
    public void comSaleShip(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            documentLineList = documentLineList.stream().map(documentLine -> documentLineRepository.getOne(documentLine.getObjectRrn())).collect(Collectors.toList());
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            Set treasuryNoteInfo = materialLots.stream().map(materialLot -> materialLot.getReserved4()).collect(Collectors.toSet());
            if (treasuryNoteInfo != null &&  treasuryNoteInfo.size() > 1) {
                throw new ClientParameterException(GcExceptions.MATERIAL_LOT_TREASURY_INFO_IS_NOT_SAME);
            }
            validationStockMLotReservedDocLineByRuleId(documentLineList, materialLots, MaterialLot.MLOT_SHIP_DOC_VALIDATE_RULE_ID);
            Map<String, List<MaterialLot>> mlotDocMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getReserved16));
            for(String docLineRrn : mlotDocMap.keySet()){
                List<MaterialLot> materialLotList = mlotDocMap.get(docLineRrn);
                DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(docLineRrn));
                if(!DocumentLine.CUSCODE_LIST.contains(documentLine.getThreeSideTransaction()) || !DocumentLine.CBUS_TYPE.equals(documentLine.getDocBusType())){
                    gcService.comStockOut(documentLine, materialLotList);
                } else {
                    comDoNomerStockOut(documentLine, materialLotList, StringUtils.EMPTY);
                    materialLotThreeSideShip(documentLine, materialLotList, "COM");
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COM销售出货
     * @param documentLine
     * @param materialLotList
     * @param bondedProperty
     * @throws ClientException
     */
    private void comDoNomerStockOut(DocumentLine documentLine, List<MaterialLot> materialLotList, String bondedProperty) throws ClientException{
        try {
            BigDecimal handledQty = BigDecimal.ZERO;
            for (MaterialLot materialLot : materialLotList) {
                handledQty = handledQty.add(materialLot.getCurrentQty());
                materialLot.setReserved12(documentLine.getObjectRrn().toString());
                materialLot.setCurrentQty(BigDecimal.ZERO);
                if(!StringUtils.isNullOrEmpty(bondedProperty)){
                    materialLot.setReserved6(bondedProperty);
                }
                changeMaterialLotStatusAndSaveHistory(materialLot);

                List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                if(CollectionUtils.isNotEmpty(packageDetailLots)){
                    for (MaterialLot packageLot : packageDetailLots){
                        if(StringUtils.isNullOrEmpty(bondedProperty)){
                            packageLot.setReserved6(bondedProperty);
                        }
                        changeMaterialLotStatusAndSaveHistory(packageLot);
                    }
                }
            }
            validateComShipDocAndUpdateErpSo(documentLine, handledQty);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * FT、RW销售出
     * @param documentLineList
     * @param materialLotActions
     * @throws ClientException
     */
    public void ftRwMLotSaleShip(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String ruleId) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());

            gcService.validateCobMaterialLotDocInfo(materialLots);
            validationStockMLotReservedDocLineByRuleId(documentLineList, materialLots, ruleId);
            Map<String, List<MaterialLot>> mlotDocMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getReserved16));
            for(String docLineRrn : mlotDocMap.keySet()){
                List<MaterialLot> materialLotList = mlotDocMap.get(docLineRrn);
                DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(docLineRrn));
                String stCode = documentLine.getDocName();
                if(!DocumentLine.CUSCODE_LIST.contains(documentLine.getThreeSideTransaction()) || !DocumentLine.STCODE_LIST.contains(documentLine.getDocName())){
                    gcService.ftShipByDocLie(documentLine, materialLotList);
                } else {
                    if(DocumentLine.STCODE_LIST.contains(stCode)){//根据三方销售码做不同处理
                        ftRwDoNomerStockOut(documentLine, materialLotList, StringUtils.EMPTY);
                        materialLotThreeSideShip(documentLine, materialLotList, StringUtils.EMPTY);
                    } else if(DocumentLine.STCODE_60.equals(stCode)){//修改保税属性为SH，正常出货
                        ftRwDoNomerStockOut(documentLine, materialLotList, BONDED_PROPERTITY_SH);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * FT、COB、RW正常出货
     * @param documentLine
     * @param materialLotList
     * @param bondedProperty
     * @throws ClientException
     */
    private void ftRwDoNomerStockOut(DocumentLine documentLine, List<MaterialLot> materialLotList, String bondedProperty) throws ClientException{
        try {
            BigDecimal handledQty = BigDecimal.ZERO;
            for (MaterialLot materialLot : materialLotList) {
                handledQty = handledQty.add(materialLot.getCurrentQty());
                materialLot.setReserved12(documentLine.getObjectRrn().toString());
                materialLot.setCurrentQty(BigDecimal.ZERO);
                if(!StringUtils.isNullOrEmpty(bondedProperty)){
                    materialLot.setReserved6(bondedProperty);
                }
                if(MaterialLot.RW_WAFER_SOURCE.equals(materialLot.getReserved50())){
                    materialLot.clearCobReservedDocInfo();
                }
                changeMaterialLotStatusAndSaveHistory(materialLot);

                List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                if(CollectionUtils.isNotEmpty(packageDetailLots)){
                    for (MaterialLot packageLot : packageDetailLots){
                        packageLot.setReserved6(materialLot.getReserved6());
                        changeMaterialLotStatusAndSaveHistory(packageLot);
                    }
                }
            }
            updateDocQyAndErpSoaSynStatusAndQty(documentLine, handledQty);
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证装箱的真空包备货单号必须一致
     * 验证发货单据是否存在，是否满足出货匹配规则
     * @param docLineList
     * @param mLotList
     * @throws ClientException
     */
    public void validationStockMLotReservedDocLineByRuleId(List<DocumentLine> docLineList, List<MaterialLot> mLotList, String ruleName) throws ClientException{
        try {
            Map<String, List<MaterialLot>> materialLotDocMap = mLotList.stream().collect(Collectors.groupingBy(MaterialLot :: getReserved16));
            for(String docLineRrn : materialLotDocMap.keySet()){
                List<MaterialLot> materialLots = materialLotDocMap.get(docLineRrn);
                Long totalUnhandledQty = materialLots.stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentQty().longValue()));
                DocumentLine docLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(docLineRrn));
                if(!docLineList.contains(docLine)){
                    throw new ClientParameterException(GcExceptions.MATERIALLOT_RESERVED_DOCID_IS_NOT_SAME, docLine.getDocId());
                }
                if(docLine.getUnHandledQty().compareTo(new BigDecimal(totalUnhandledQty)) < 0){
                    throw new ClientParameterException(GcExceptions.OVER_DOC_QTY, docLine.getDocId());
                }
                for (MaterialLot mLot : materialLots) {
                    String reservedRrn = mLot.getReserved16();
                    //验证物料批次出货规则
                    validateMLotAndDocLineByRule(docLine, mLot, ruleName);
                    if(!StringUtils.isNullOrEmpty(mLot.getPackageType()) && !MaterialLot.RW_WAFER_SOURCE.equals(mLot.getReserved50())){
                        //COB箱号不需要验证备货单号
                        List<MaterialLot> packedLotList = materialLotRepository.getPackageDetailLots(mLot.getObjectRrn());
                        for (MaterialLot packedMLot : packedLotList) {
                            if(!reservedRrn.equals(packedMLot.getReserved16())){
                                throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "reservedDocRrn", mLot.getReserved16(), packedMLot.getReserved16());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * WLT CP三方销售出或者销售出
     * 销售出需要校验
     * @param documentLine
     * @param materialLotActions
     * @param checkSubCode
     * @throws ClientException
     */
    @Override
    public void wltCpMLotSaleShip(DocumentLine documentLine, List<MaterialLotAction> materialLotActions, String checkSubCode) throws ClientException {
        try {
            String stCode = documentLine.getDocName();
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            valideteWltDocLineAndMLotSaleShipInfo(documentLine, materialLots, checkSubCode);
            if(StringUtils.isNullOrEmpty(stCode) || !DocumentLine.ALL_STCODE_LIST.contains(stCode) || !DocumentLine.CUSCODE_LIST.contains(documentLine.getThreeSideTransaction())){//做普通销售出
                wltCpSaleShipOut(documentLine, materialLots, StringUtils.EMPTY);
            } else if(DocumentLine.STCODE_LIST.contains(stCode)){//根据三方销售码做不同处理
                wltCpSaleShipOut(documentLine, materialLots, StringUtils.EMPTY);
                materialLotThreeSideShip(documentLine, materialLots, "WLT");
            } else if(DocumentLine.STCODE_60.equals(stCode)){//修改保税属性为SH，正常出货
                wltCpSaleShipOut(documentLine, materialLots, BONDED_PROPERTITY_SH);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * WLT、CP普通销售出货
     * @param documentLine
     * @param location
     * @param materialLots
     * @throws ClientException
     */
    private void wltCpSaleShipOut(DocumentLine documentLine, List<MaterialLot> materialLots, String location) throws ClientException{
        try {
            BigDecimal unhandedQty = documentLine.getUnHandledQty();
            for(MaterialLot materialLot : materialLots){
                if(!StringUtils.isNullOrEmpty(location)){
                    materialLot.setReserved6(location);
                }
                if(MaterialLot.STOCKOUT_TYPE_35.equals(materialLot.getReserved54()) || materialLot.getMaterialName().endsWith(MaterialLot.STOCKOUT_TYPE_4)){
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
                    }
                }
            }
            BigDecimal handledQty = documentLine.getUnHandledQty().subtract(unhandedQty);
            updateDocQyAndErpSoaSynStatusAndQty(documentLine, handledQty);
        } catch (Exception e){
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
            documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());
            documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
            documentLine.setUnHandledQty(documentLine.getUnHandledQty().subtract(handledQty));
            documentLine = documentLineRepository.saveAndFlush(documentLine);
            baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_SHIP);

            OtherStockOutOrder otherShipOrder = (OtherStockOutOrder) otherStockOutOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            otherShipOrder.setUnHandledQty(otherShipOrder.getUnHandledQty().subtract(handledQty));
            otherShipOrder.setHandledQty(otherShipOrder.getHandledQty().add(handledQty));
            otherShipOrder = otherStockOutOrderRepository.saveAndFlush(otherShipOrder);
            baseService.saveHistoryEntity(otherShipOrder, MaterialLotHistory.TRANS_TYPE_SHIP);

            if(StringUtils.isNullOrEmpty(documentLine.getMergeDoc())){
                Optional<ErpSoa> erpSoaOptional = erpSoaOrderRepository.findById(Long.valueOf(documentLine.getReserved1()));
                if (!erpSoaOptional.isPresent()) {
                    throw new ClientParameterException(GcExceptions.ERP_SOA_IS_NOT_EXIST, documentLine.getReserved1());
                }
                ErpSoa erpSoaOrder = erpSoaOptional.get();
                erpSoaOrder.setLeftNum(erpSoaOrder.getLeftNum().subtract(handledQty));
                erpSoaOrder.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
                if (!StringUtils.isNullOrEmpty(erpSoaOrder.getDeliveredNum())) {
                    BigDecimal docHandledQty = new BigDecimal(erpSoaOrder.getDeliveredNum());
                    docHandledQty = docHandledQty.add(handledQty);
                    erpSoaOrder.setDeliveredNum(docHandledQty.toPlainString());
                } else {
                    erpSoaOrder.setDeliveredNum(handledQty.toPlainString());
                }
                erpSoaOrderRepository.save(erpSoaOrder);
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
    private void validateComShipDocAndUpdateErpSo(DocumentLine documentLine, BigDecimal handledQty) throws ClientException{
        try {
            // 验证当前操作数量是否超过待检查数量
            documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());
            documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
            documentLine.setUnHandledQty(documentLine.getUnHandledQty().subtract(handledQty));
            documentLine = documentLineRepository.saveAndFlush(documentLine);
            baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_SHIP);

            // 获取到主单据
            DeliveryOrder deliveryOrder = (DeliveryOrder) deliveryOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            deliveryOrder.setHandledQty(deliveryOrder.getHandledQty().add(handledQty));
            deliveryOrder.setUnHandledQty(deliveryOrder.getUnHandledQty().subtract(handledQty));
            deliveryOrder = deliveryOrderRepository.saveAndFlush(deliveryOrder);
            baseService.saveHistoryEntity(deliveryOrder, MaterialLotHistory.TRANS_TYPE_SHIP);

            if(StringUtils.isNullOrEmpty(documentLine.getMergeDoc())){
                Optional<ErpSo> erpSoOptional = erpSoRepository.findById(Long.valueOf(documentLine.getReserved1()));
                if (!erpSoOptional.isPresent()) {
                    throw new ClientParameterException(GcExceptions.ERP_RECEIVE_ORDER_IS_NOT_EXIST, documentLine.getReserved1());
                }

                ErpSo erpSo = erpSoOptional.get();
                erpSo.setLeftNum(erpSo.getLeftNum().subtract(handledQty));
                erpSo.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
                if (!StringUtils.isNullOrEmpty(erpSo.getDeliveredNum())) {
                    BigDecimal docLineHandledQty = new BigDecimal(erpSo.getDeliveredNum());
                    docLineHandledQty = docLineHandledQty.add(handledQty);
                    erpSo.setDeliveredNum(docLineHandledQty.toPlainString());
                } else {
                    erpSo.setDeliveredNum(handledQty.toPlainString());
                }
                erpSoRepository.save(erpSo);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * WLT、CP销售出
     * @param documentLine
     * @param materialLots
     * @param checkSubCode
     * @throws ClientException
     */
    private void valideteWltDocLineAndMLotSaleShipInfo(DocumentLine documentLine, List<MaterialLot> materialLots, String checkSubCode) throws ClientException{
        try {
            Long unHandleQty = documentLine.getUnHandledQty().longValue();
            String docSaleShipInfo = getSaleShipDocInfo(documentLine, checkSubCode);
            BigDecimal totalQty = BigDecimal.ZERO;
            for(MaterialLot materialLot : materialLots){
                String mLotShipInfo = getMLotSaleShipInfo(materialLot, checkSubCode);
                if(!docSaleShipInfo.equals(mLotShipInfo)){
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLot.getMaterialLotId());
                }
                if(materialLot.getMaterialName().endsWith(MaterialLot.STOCKOUT_TYPE_35) || materialLot.getMaterialName().endsWith(MaterialLot.STOCKOUT_TYPE_4)){
                    totalQty = totalQty.add(materialLot.getCurrentQty());
                } else {
                    totalQty = totalQty.add(materialLot.getCurrentSubQty());
                }
            }
            Long totalMaterialLotQty = totalQty.longValue();
            if (totalMaterialLotQty.compareTo(unHandleQty) > 0) {
                throw new ClientException(GcExceptions.OVER_DOC_QTY);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证物料批次和单据批次信息
     * @param materialLot
     * @param checkSubCode
     * @return
     * @throws ClientException
     */
    private String getMLotSaleShipInfo(MaterialLot materialLot, String checkSubCode) throws ClientException{
        try {
            StringBuffer key = new StringBuffer();
            String materialName = StringUtils.EMPTY;
            if(!StringUtils.isNullOrEmpty(materialLot.getReserved7()) && MaterialLotUnit.PRODUCT_CATEGORY_WLT.equals(materialLot.getReserved7())){
                materialName = materialLot.getMaterialName();
            } else {
                materialName = materialLot.getMaterialName().substring(0, materialLot.getMaterialName().lastIndexOf("-")) + materialLot.getReserved54();
            }
            key.append(materialName + StringUtils.SPLIT_CODE);
            if(!StringUtils.isNullOrEmpty(checkSubCode)){
                key.append(materialLot.getReserved1() + StringUtils.SPLIT_CODE);
            }
            key.append(materialLot.getReserved6() + StringUtils.SPLIT_CODE);
//            if(StringUtils.isNullOrEmpty(materialLot.getReserved55())){
//                key.append(materialLot.getReserved55() + StringUtils.SPLIT_CODE);
//            } else{
//                key.append(materialLot.getReserved55().toUpperCase() + StringUtils.SPLIT_CODE);
//            }
            return key.toString();
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取销售单批次信息
     * @param documentLine
     * @param checkSubCode
     * @return
     * @throws ClientException
     */
    private String getSaleShipDocInfo(DocumentLine documentLine, String checkSubCode) throws ClientException{
        try {
            StringBuffer docShipInfo = new StringBuffer();
            docShipInfo.append(documentLine.getMaterialName() + StringUtils.SPLIT_CODE);
            if(!StringUtils.isNullOrEmpty(checkSubCode)){
                docShipInfo.append(documentLine.getReserved2() + StringUtils.SPLIT_CODE);
            }
            docShipInfo.append(documentLine.getReserved7() + StringUtils.SPLIT_CODE);
//            if(StringUtils.isNullOrEmpty(documentLine.getReserved8())){
//                docShipInfo.append(documentLine.getReserved8() + StringUtils.SPLIT_CODE);
//            } else {
//                docShipInfo.append(documentLine.getReserved8().toUpperCase() + StringUtils.SPLIT_CODE);
//            }
            return docShipInfo.toString();
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次三方销售
     * @param documentLine
     * @param materialLots
     * @throws ClientException
     */
    private void materialLotThreeSideShip(DocumentLine documentLine, List<MaterialLot> materialLots, String lineType) throws ClientException{
        try {
            String threeSideCode = documentLine.getThreeSideTransaction();
            String place = documentLine.getReserved34();//soa:other7
            if(DocumentLine.SH_ZJ_CUSCODE_LIST.contains(threeSideCode)){
                //先做出货，再修改仓库:“SH_STOCK”保税属性:“SH” 清除备货信息 变:Create 记录创建历史
                String warehouseRrn = MaterialLot.SH_WAREHOUSE;
                String bondedProperty = MaterialLot.LOCATION_SH;
                if(DocumentLine.CUSCODE_C2837.equals(threeSideCode) || DocumentLine.CUSCODE_C9009.equals(threeSideCode)){
                    //先做出货，再修改仓库:“ZJ_STOCK”保税属性:“ZSH” 清除备货信息 变:Create 记录创建历史
                    warehouseRrn = MaterialLot.ZJ_WAREHOUSE;
                    bondedProperty = MaterialLot.BONDED_PROPERTY_ZSH;
                }
                materialLotThreeSaleShipByWarehouse(materialLots, warehouseRrn, bondedProperty, lineType);
            } else if(DocumentLine.CUSCODE_C001.equals(threeSideCode)){
                //先做出货，再修改仓库:“ZJ_STOCK”保税属性:“ZSH” 清除备货信息 变:Create 记录创建历史
                if(!StringUtils.isNullOrEmpty(documentLine.getReserved13()) && documentLine.getReserved13().contains(DocumentLine.MEMO) && !lineType.equals("COM")){
                    materialLotThreeSaleShipByWarehouse(materialLots, MaterialLot.BS_WAREHOUSE, MaterialLot.BONDED_PROPERTY_HK, lineType);
                } else if(DocumentLine.PLACR_GALAXYCORE.equals(place)){
                    materialLotThreeSaleShipByWarehouse(materialLots, MaterialLot.HK_WAREHOUSE, MaterialLot.BONDED_PROPERTY_HK, lineType);
                } else {
                    //正常出货，然后修改为HK_STOCK，新增香港仓：Create stockIn Ship 记录
                    galaxyCoreThreeSideAndSaveHis(materialLots, MaterialLot.HK_WAREHOUSE, MaterialLot.BONDED_PROPERTY_HK);
                }
            } else if(DocumentLine.CUSCODE_C003263.equals(threeSideCode)){
                if(DocumentLine.PLACR_GALAXYCORE.equals(place)){
                    //正常出货，然后修改为IC_STOCK，HK Create
                    materialLotThreeSaleShipByWarehouse(materialLots, MaterialLot.IC_WAREHOUSE, MaterialLot.BONDED_PROPERTY_HK, lineType);
                } else {
                    //正常出货，然后修改为IC_STOCK，新增香港仓：Create stockIn Ship 记录
                    galaxyCoreThreeSideAndSaveHis(materialLots, MaterialLot.IC_WAREHOUSE, MaterialLot.BONDED_PROPERTY_HK);
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科微三方销售出货及历史记录
     * @param materialLots
     * @param warehouseRrn
     * @param location
     * @throws ClientException
     */
    private void galaxyCoreThreeSideAndSaveHis(List<MaterialLot> materialLots, String warehouseRrn, String location) throws ClientException{
        try {
            for(MaterialLot materialLot : materialLots){
                materialLot.setReserved13(warehouseRrn);
                materialLot.setReserved6(location);
                materialLot = materialLotRepository.saveAndFlush(materialLot);

                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                    List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                    for(MaterialLot packedLot : packageDetailLots){
                        packedLot.setReserved13(warehouseRrn);
                        packedLot.setReserved6(location);
                        packedLot = materialLotRepository.saveAndFlush(packedLot);

                        updateMaterialLotUnitWarehouseInfo(packedLot);
                    }
                } else {
                    updateMaterialLotUnitWarehouseInfo(materialLot);
                }

                //1、记录香港仓Create记录
                createResetMLotAndUnitHis(materialLot, MaterialLotHistory.TRANS_TYPE_AUTO_CREATE_ONE);

                //2、记录香港仓In记录
                createResetMLotAndUnitHis(materialLot, MaterialLotHistory.TRANS_TYPE_AUTO_IN);

                //3、记录香港仓Out记录
                createResetMLotAndUnitHis(materialLot, MaterialLotHistory.TRANS_TYPE_AUTO_OUT);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * 修改晶圆仓库信息
     * @param materialLot
     * @throws ClientException
     */
    private void updateMaterialLotUnitWarehouseInfo(MaterialLot materialLot) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnits = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
            if(CollectionUtils.isNotEmpty(materialLotUnits)){
                for(MaterialLotUnit materialLotUnit : materialLotUnits){
                    materialLotUnit.setReserved4(materialLot.getReserved6());
                    materialLotUnit.setReserved13(materialLot.getReserved13());
                    materialLotUnitRepository.saveAndFlush(materialLotUnit);
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 创建物料批次三方操作历史
     * @param materialLot
     * @throws ClientException
     */
    private void createResetMLotAndUnitHis(MaterialLot materialLot, String transType) throws ClientException{
        try {
            if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                MaterialLotHistory materialLotHistory = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, transType);
                if(MaterialLotHistory.TRANS_TYPE_AUTO_CREATE.equals(transType)){
                    materialLotHistory.setStatusCategory(MaterialStatus.STATUS_CREATE);
                    materialLotHistory.setStatus(MaterialStatus.STATUS_CREATE);
                    materialLotHistory.setPreStatusCategory(null);
                    materialLotHistory.setPreStatus(null);
                } else if(MaterialLotHistory.TRANS_TYPE_AUTO_IN.equals(transType)){
                    materialLotHistory.setStatusCategory(MaterialStatusCategory.STATUS_CATEGORY_USE);
                    materialLotHistory.setStatus(MaterialStatus.STATUS_WAIT);
                    materialLotHistory.setPreStatusCategory(MaterialStatus.STATUS_CREATE);
                    materialLotHistory.setPreStatus(MaterialStatus.STATUS_CREATE);
                }
                materialLotHistory.setCreated(getDate(new Date(), 10));
                materialLotHistoryRepository.save(materialLotHistory);
                List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                for(MaterialLot packedLot : packageDetailLots){
                    saveMLotAndUnitThreeSideHis(packedLot, transType, materialLotHistory.getCreated());
                }
            } else {
                saveMLotAndUnitThreeSideHis(materialLot, transType, getDate(new Date(), 10));
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 记录物料批次历史
     * @param materialLot
     * @param transType
     * @param created
     */
    private void saveMLotAndUnitThreeSideHis(MaterialLot materialLot, String transType, Date created) throws ClientException{
        try {
            MaterialLotHistory materialLotHistory = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, transType);
            if(MaterialLotHistory.TRANS_TYPE_AUTO_CREATE.equals(transType)){
                materialLotHistory.setStatusCategory(MaterialStatus.STATUS_CREATE);
                materialLotHistory.setStatus(MaterialStatus.STATUS_CREATE);
                materialLotHistory.setPreStatusCategory(null);
                materialLotHistory.setPreStatus(null);
            } else if(MaterialLotHistory.TRANS_TYPE_AUTO_IN.equals(transType)){
                materialLotHistory.setStatusCategory(MaterialStatus.STATUS_STOCK);
                materialLotHistory.setStatus(MaterialStatus.STATUS_IN);
                materialLotHistory.setPreStatusCategory(MaterialStatus.STATUS_CREATE);
                materialLotHistory.setPreStatus(MaterialStatus.STATUS_CREATE);
            }
            materialLotHistory.setCreated(created);
            materialLotHistoryRepository.save(materialLotHistory);

            List<MaterialLotUnit> materialLotUnits = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
            if(CollectionUtils.isNotEmpty(materialLotUnits)){
                for(MaterialLotUnit materialLotUnit : materialLotUnits){
                    MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, transType);
                    materialLotUnitHistory.setState(MaterialStatus.STATUS_CREATE);
                    materialLotUnitHistory.setCreated(created);
                    materialLotUnitHisRepository.save(materialLotUnitHistory);
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 出货并且修改仓库信息，恢复待接收状态
     * @param materialLots
     * @param warehouseRrn
     * @param bondedProperty
     * @param lineType
     * @throws ClientException
     */
    private void materialLotThreeSaleShipByWarehouse(List<MaterialLot> materialLots, String warehouseRrn, String bondedProperty, String lineType) throws ClientException{
        try {
            for(MaterialLot materialLot : materialLots){
                materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLot.getMaterialLotId(), ThreadLocalContext.getOrgRrn());
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                    List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                    for(MaterialLot packedLot : packageDetailLots){
                        reNewMLotAndUpdateWarehouse(packedLot, warehouseRrn, bondedProperty, lineType);
                    }
                    Long totalSubQty = packageDetailLots.stream().collect(Collectors.summingLong(mLot -> mLot.getCurrentSubQty() == null ? 0 : mLot.getCurrentSubQty().longValue()));
                    materialLot.setCurrentSubQty(new BigDecimal(totalSubQty));
                    reNewMLotAndUpdateWarehouse(materialLot, warehouseRrn, bondedProperty, lineType);
                } else {
                    reNewMLotAndUpdateWarehouse(materialLot, warehouseRrn, bondedProperty, lineType);
                }
            }
        }  catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 恢复待接收状态，清除备货等信息，修改仓库信息
     * @param materialLot
     * @param warehouseRrn
     * @param bondedProperty
     */
    private void reNewMLotAndUpdateWarehouse(MaterialLot materialLot, String warehouseRrn, String bondedProperty, String lineType) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
            if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                materialLot.setCurrentSubQty(new BigDecimal(materialLotUnitList.size()));
            }
            if(lineType.equals("WLT")){
                materialLot.restoreStatus();
                materialLot.setReserved12(null);
            } else {
                materialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
                materialLot.setStatus(MaterialStatus.STATUS_CREATE);
                materialLot.setPreStatusCategory(null);
                materialLot.setPreStatus(null);
                materialLot.resetMLotInfo();
            }
            materialLot.setCurrentQty(materialLot.getReceiveQty());
            materialLot.setReserved13(warehouseRrn);
            materialLot.setReserved6(bondedProperty);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_AUTO_CREATE);
            history.setCreated(getDate(new Date(), 10));
            materialLotHistoryRepository.save(history);

            if(lineType.equals("WLT")){
                MaterialLotHistory materialLotHistory = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_AUTO_IN);
                materialLotHistory.setCreated(getDate(new Date(), 10));
                materialLotHistoryRepository.save(materialLotHistory);
            }

            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                materialLotUnit.setWorkOrderId(null);
                materialLotUnit.setWorkOrderPlanputTime(null);
                if(lineType.equals("WLT")){
                    if(StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())){
                        materialLotUnit.setState(MaterialStatus.STATUS_IN);
                    } else {
                        materialLotUnit.setState(MaterialStatus.STATUS_PACKAGE);
                    }
                } else {
                    materialLotUnit.setState(MaterialStatus.STATUS_CREATE);
                }
                materialLotUnit.setReserved4(bondedProperty);
                materialLotUnit.setReserved13(warehouseRrn);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotHistory.TRANS_TYPE_AUTO_CREATE);
                materialLotUnitHistory.setCreated(getDate(new Date(), 10));
                materialLotUnitHisRepository.save(materialLotUnitHistory);

                if(lineType.equals("WLT")){
                    MaterialLotUnitHistory mUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotHistory.TRANS_TYPE_AUTO_IN);
                    mUnitHistory.setCreated(getDate(new Date(), 10));
                    materialLotUnitHisRepository.save(mUnitHistory);
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 记录单号至物料批次上，并且修改物料批次状态，记录历史
     * @param mLot
     * @param docLine
     */
    private void saveDocLineRrnAndChangeStatus(MaterialLot mLot, DocumentLine docLine) throws ClientException{
        try {
            mLot.setReserved12(docLine.getObjectRrn().toString());
            changeMaterialLotStatusAndSaveHistory(mLot);
            if(!StringUtils.isNullOrEmpty(mLot.getParentMaterialLotId())){
                changPackaedDetailLotStatusAndSaveHis(mLot);
            } else {
                changMaterialLotUnitLocationAndDoShip(mLot, mLot.getReserved6());
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 改变包装批次的状态及记录历史
     * @param parentMLot
     * @throws ClientException
     */
    private void changPackaedDetailLotStatusAndSaveHis(MaterialLot parentMLot) throws ClientException{
        try {
            List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(parentMLot.getObjectRrn());
            for (MaterialLot packageLot : packageDetailLots){
                packageLot.setReserved6(parentMLot.getReserved6());
                changeMaterialLotStatusAndSaveHistory(packageLot);
                changMaterialLotUnitLocationAndDoShip(packageLot, parentMLot.getReserved6());
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改晶圆报税属性并做出货操作
     * @param packageLot
     * @throws ClientException
     */
    private void changMaterialLotUnitLocationAndDoShip(MaterialLot packageLot, String location) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(packageLot.getMaterialLotId());
            if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                    materialLotUnit.setReserved4(location);
                    materialLotUnit.setState(MaterialLotUnit.STATE_OUT);
                    materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                    MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_STOCK_OUT);
                    materialLotUnitHisRepository.save(materialLotUnitHistory);
                }
            }
        } catch (Exception e){
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
     * 获取当前时间几秒后的时间
     * @param created
     * @return
     * @throws ClientException
     */
    private Date getDate(Date created, Integer count) throws ClientException{
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(created);
            calendar.add(Calendar.SECOND, count);
            return calendar.getTime();
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次单据验证
     * @param docLine
     * @param materialLot
     * @param ruleName
     * @throws ClientException
     */
    private void validateMLotAndDocLineByRule(DocumentLine docLine, MaterialLot materialLot, String ruleName) throws ClientException{
        try {
            List<MLotDocRule> mLotDocLineRule = mLotDocRuleRepository.findByNameAndOrgRrn(ruleName, ThreadLocalContext.getOrgRrn());
            if (CollectionUtils.isEmpty(mLotDocLineRule)) {
                throw new ClientParameterException(GcExceptions.MLOT_DOC_VALIDATE_RULE_IS_NOT_EXIST, ruleName);
            }
            MLotDocRuleContext materialLotDocRuleContext = new MLotDocRuleContext();
            materialLotDocRuleContext.setTargetObject(docLine);
            materialLotDocRuleContext.setSourceObject(materialLot);
            materialLotDocRuleContext.setMLotDocRuleLines(mLotDocLineRule.get(0).getLines());
            materialLotDocRuleContext.validation();
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

}