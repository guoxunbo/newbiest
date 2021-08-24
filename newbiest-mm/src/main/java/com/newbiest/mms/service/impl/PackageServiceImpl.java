package com.newbiest.mms.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
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
import com.newbiest.mms.utils.CollectorsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2019/4/1.
 */
@Service
@Slf4j
@Transactional
public class PackageServiceImpl implements PackageService{

    @Autowired
    MaterialLotPackageTypeRepository materialLotPackageTypeRepository;

    @Autowired
    MaterialLotHistoryRepository materialLotHistoryRepository;

    @Autowired
    PackagedLotDetailRepository packagedLotDetailRepository;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialLotUnitRepository materialLotUnitRepository;

    @Autowired
    MaterialLotUnitHisRepository materialLotUnitHisRepository;

    @Autowired
    GeneratorService generatorService;

    @Autowired
    MmsService mmsService;

    @Autowired
    BaseService baseService;

    @Autowired
    MaterialLotInventoryRepository materialLotInventoryRepository;

    @Autowired
    DocumentLineRepository documentLineRepository;

    @Autowired
    DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    MaterialLotUnitService materialLotUnitService;

    public MaterialLotPackageType getMaterialPackageTypeByName(String name) throws ClientException{
        List<MaterialLotPackageType> packageTypes = materialLotPackageTypeRepository.findByNameAndOrgRrn(name, ThreadLocalContext.getOrgRrn());
        if (CollectionUtils.isNotEmpty(packageTypes)) {
            return packageTypes.get(0);
        } else {
            throw new ClientParameterException(MmsException.MM_PACKAGE_TYPE_IS_NOT_EXIST, name);
        }
    }

    /**
     * 获取包装详细信息
     * @return
     */
    public List<MaterialLot> getPackageDetailLots(Long packagedLotRrn) throws ClientException{
        try {
            return materialLotRepository.getPackageDetailLots(packagedLotRrn);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 追加包装
     *   当前不卡控被追加包装批次的状态
     * @param packedMaterialLot 被追加的包装批次
     * @param materialLotActions 包装批次动作
     * @return
     */
    public MaterialLot appendPacking(MaterialLot packedMaterialLot, List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();

            packedMaterialLot = mmsService.getMLotByMLotId(packedMaterialLot.getMaterialLotId(), true);
            packedMaterialLot.isFinish();

            // 取第一个的materialAction作为所有者的actionCode
            MaterialLotAction firstMaterialAction = materialLotActions.get(0);
            List<MaterialLot> allMaterialLot = Lists.newArrayList();

            List<MaterialLot> waitToAddPackingMLots = materialLotActions.stream().map(action -> mmsService.getMLotByMLotId(action.getMaterialLotId())).collect(Collectors.toList());
            BigDecimal totalWaitToAddReservedQty = waitToAddPackingMLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getReservedQty));

            allMaterialLot.addAll(waitToAddPackingMLots);
            // 取到包装规则
            MaterialLotPackageType materialLotPackageType = getMaterialPackageTypeByName(packedMaterialLot.getPackageType());

            // 将包装的和以前包装的放在一起进行包装规则验证
            List<MaterialLot> packageDetailLots = getPackageDetailLots(packedMaterialLot.getObjectRrn());

            List<MaterialLotAction> allMaterialAction = Lists.newArrayList(materialLotActions);
            if (CollectionUtils.isNotEmpty(packageDetailLots)) {
                allMaterialLot.addAll(packageDetailLots);
                //TODO 此处因为此版本中项目要求不扣减批次数量。所有直接使用批次上的CurrentQty。后续版本需要使用detail上的数据
                for (MaterialLot packageDetailLot : packageDetailLots) {
                    //TODO 此处为GC客制化 追加包装之后 清空装箱检验相关栏位 后续公共版本需要清除此代码
                    packageDetailLot.setReserved9(StringUtils.EMPTY);
                    packageDetailLot.setReserved10(StringUtils.EMPTY);
                    materialLotRepository.save(packageDetailLot);

                    MaterialLotAction packedMLotAction = new MaterialLotAction();
                    packedMLotAction.setMaterialLotId(packageDetailLot.getMaterialLotId());
                    packedMLotAction.setTransQty(packageDetailLot.getCurrentQty());
                    allMaterialAction.add(packedMLotAction);
                }
            }

            materialLotPackageType.validationAppendPacking(waitToAddPackingMLots, allMaterialAction);
            if (!StringUtils.isNullOrEmpty(materialLotPackageType.getMergeRule())) {
                mmsService.validationMergeRule(materialLotPackageType.getMergeRule(), allMaterialLot);
            }

            //TODO 此处为GC客制化 追加包装之后 清空装箱检验相关栏位并将状态到USE-WAIT 后续公共版本需要清除此代码
            packedMaterialLot.setStatusCategory(MaterialStatusCategory.STATUS_CATEGORY_USE);
            packedMaterialLot.setStatus(MaterialStatus.STATUS_WAIT);
            packedMaterialLot.setReserved9(StringUtils.EMPTY);
            packedMaterialLot.setReserved10(StringUtils.EMPTY);

            packedMaterialLot.setCurrentQty(materialLotPackageType.getPackedQty(allMaterialAction));

            packedMaterialLot.setReservedQty(packedMaterialLot.getReservedQty().add(totalWaitToAddReservedQty));
            packedMaterialLot = materialLotRepository.saveAndFlush(packedMaterialLot);
//            // 记录创建历史
            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(packedMaterialLot, MaterialLotHistory.TRANS_TYPE_ADDITIONAL_PACKAGE);
            history.buildByMaterialLotAction(firstMaterialAction);
            history.setActionCode(firstMaterialAction.getActionCode());
            history.setActionReason(firstMaterialAction.getActionReason());
            materialLotHistoryRepository.save(history);

            packageMaterialLots(packedMaterialLot, waitToAddPackingMLots, materialLotActions, false, true);
            return packedMaterialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 拆包装
     *
     * @param materialLotActions 需要被拆出来的物料批次
     * @throws ClientException
     * @return 被拆包的主批次
     */
    public List<MaterialLot> unPack(List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            List<MaterialLot> unPackedMainMaterialLots = Lists.newArrayList();
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            //因为当前仅支持全部包装。故此处，直接用ParentMaterialLotId做包装号。
            Map<String, List<MaterialLot>> packedLotMap = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true))
                    .collect(Collectors.groupingBy(MaterialLot::getParentMaterialLotId));
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
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            // 取到包装规则
            MaterialLotPackageType materialLotPackageType = getMaterialPackageTypeByName(packedMaterialLot.getPackageType());
            BigDecimal packedQty = materialLotPackageType.getPackedQty(materialLotActions);

            packedMaterialLot.setCurrentQty(packedMaterialLot.getCurrentQty().subtract(packedQty));
            if(!StringUtils.isNullOrEmpty(packedMaterialLot.getReserved16())){
                packedMaterialLot.setReservedQty(packedMaterialLot.getReservedQty().subtract(packedQty));
            }
            if(packedMaterialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == -1){
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_CURRENT_QTY_LESS_THAN_ZERO, packedMaterialLot.getMaterialLotId());
            } else if (packedMaterialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                packedMaterialLot.setReservedQty(BigDecimal.ZERO);
                packedMaterialLot.clearReservedInfo();
                packedMaterialLot = mmsService.changeMaterialLotState(packedMaterialLot, MaterialEvent.EVENT_UN_PACKAGE, StringUtils.EMPTY);
            } else {
                //TODO 此处为GC客制化 拆包 清空装箱检验相关栏位并将状态到USE-WAIT 后续公共版本需要清除此代码
                packedMaterialLot.setStatusCategory(MaterialStatusCategory.STATUS_CATEGORY_USE);
                packedMaterialLot.setStatus(MaterialStatus.STATUS_WAIT);
                packedMaterialLot.setReserved9(StringUtils.EMPTY);
                packedMaterialLot.setReserved10(StringUtils.EMPTY);
            }

            // 扣减库存 箱批次只会存在一个位置上
            List<MaterialLotInventory> materialLotInventories = mmsService.getMaterialLotInv(packedMaterialLot.getObjectRrn());
            if (CollectionUtils.isNotEmpty(materialLotInventories)) {
                MaterialLotInventory materialLotInventory = materialLotInventories.get(0);
                mmsService.saveMaterialLotInventory(materialLotInventory, packedQty.negate());
            }

            Map<String, MaterialLotAction> materialLotActionMap = materialLotActions.stream().collect(Collectors.toMap(MaterialLotAction :: getMaterialLotId, Function.identity()));

            Map<String, PackagedLotDetail> packagedLotDetails = packagedLotDetailRepository.findByPackagedLotRrn(packedMaterialLot.getObjectRrn()).stream().collect(Collectors.toMap(PackagedLotDetail :: getMaterialLotId, Function.identity()));

            for (MaterialLot waitToUnPackageMLot : waitToUnPackageMLots) {
                MaterialLotAction materialLotAction = materialLotActionMap.get(waitToUnPackageMLot.getMaterialLotId());

                // 更新packageDetail数量
                PackagedLotDetail packagedLotDetail = packagedLotDetails.get(waitToUnPackageMLot.getMaterialLotId());
                packagedLotDetail.setQty(packagedLotDetail.getQty().subtract(materialLotAction.getTransQty()));
                if (packagedLotDetail.getQty().compareTo(BigDecimal.ZERO) == 0) {
                    packagedLotDetailRepository.deleteById(packagedLotDetail.getObjectRrn());
                } else {
                    packagedLotDetailRepository.save(packagedLotDetail);
                }
                // 根据JVM参数来判断是否要直接还原被包装的批次数量
                if (SystemPropertyUtils.getUnpackRecoveryLotFlag()) {
                    waitToUnPackageMLot.setParentMaterialLotRrn(null);
                    waitToUnPackageMLot.setParentMaterialLotId(StringUtils.EMPTY);
                    //TODO 此处为GC客制化 拆包 清空装箱检验相关栏位 后续公共版本需要清除此代码
                    waitToUnPackageMLot.setReserved9(StringUtils.EMPTY);
                    waitToUnPackageMLot.setReserved10(StringUtils.EMPTY);
                    waitToUnPackageMLot.restoreStatus();
                    materialLotRepository.save(waitToUnPackageMLot);

                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(waitToUnPackageMLot, MaterialLotHistory.TRANS_TYPE_UN_PACKAGE);
                    history.buildByMaterialLotAction(materialLotAction);

                    if (MaterialStatusCategory.STATUS_CATEGORY_STOCK.equals(waitToUnPackageMLot.getStatusCategory()) && MaterialStatus.STATUS_IN.equals(waitToUnPackageMLot.getStatus())) {
                        // 找到最后一笔包装数据
                        MaterialLotHistory materialLotHistory = materialLotHistoryRepository.findTopByMaterialLotIdAndTransTypeOrderByCreatedDesc(waitToUnPackageMLot.getMaterialLotId(), MaterialLotHistory.TRANS_TYPE_PACKAGE);
                        if (materialLotHistory != null) {
                            Warehouse warehouse = mmsService.getWarehouseByName(materialLotHistory.getTransWarehouseId());
                            Storage storage = mmsService.getStorageByWarehouseRrnAndName(warehouse, materialLotHistory.getTransStorageId());
                            // 恢复库存数据
                            MaterialLotInventory materialLotInventory = new MaterialLotInventory();
                            materialLotInventory.setMaterialLot(waitToUnPackageMLot).setWarehouse(warehouse).setStorage(storage);
                            mmsService.saveMaterialLotInventory(materialLotInventory, waitToUnPackageMLot.getCurrentQty());

                            history.setTargetWarehouseId(warehouse.getName());
                            history.setTransStorageId(storage.getName());
                        }
                    }
                    materialLotHistoryRepository.save(history);

                }
                //拆包时将晶圆的状态恢复至In状态
                List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(waitToUnPackageMLot.getMaterialLotId());
                if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                    for(MaterialLotUnit materialLotUnit: materialLotUnitList){
                        materialLotUnit.setState(MaterialLotUnit.STATE_IN);
                        materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                        MaterialLotUnitHistory unitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotHistory.TRANS_TYPE_UN_PACKAGE);
                        materialLotUnitHisRepository.save(unitHistory);
                    }
                }
            }

            //拆箱结束，如果没有全部拆完，修改箱号上的备货标记
            List<MaterialLot> packedMaterialLots = materialLotRepository.getPackageDetailLots(packedMaterialLot.getObjectRrn());
            if(CollectionUtils.isNotEmpty(packedMaterialLots)){
                packedMaterialLot.setReserved16(packedMaterialLots.get(0).getReserved16());
                packedMaterialLot.setReserved17(packedMaterialLots.get(0).getReserved17());
                packedMaterialLot.setReserved18(packedMaterialLots.get(0).getReserved18());

                //如果箱号被Hold,箱中被Hold的真空包全部被拆除，Release箱号
                if(MaterialLot.HOLD_STATE_ON.equals(packedMaterialLot.getHoldState())){
                    List<MaterialLot> holdMLotList = packedMaterialLots.stream().filter(materialLot -> MaterialLot.HOLD_STATE_ON.equals(materialLot.getHoldState())).collect(Collectors.toList());
                    if(CollectionUtils.isEmpty(holdMLotList)){
                        packedMaterialLot.setHoldState(MaterialLot.HOLD_STATE_OFF);
                        packedMaterialLot.setHoldReason(StringUtils.EMPTY);
                    }
                }
            }

            packedMaterialLot = materialLotRepository.saveAndFlush(packedMaterialLot);
            MaterialLotHistory unPackagedHistory = (MaterialLotHistory) baseService.buildHistoryBean(packedMaterialLot, MaterialLotHistory.TRANS_TYPE_UN_PACKAGE);
            materialLotHistoryRepository.save(unPackagedHistory);

            return packedMaterialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

//    /**
//     * 还原箱中所有备份的真空包标识并还原出库单中的备货以及未备货数量
//     * @param materialLots
//     */
//    public void restoreMaterialLotAndDocLineReservedSignAndQty(List<MaterialLot> materialLots) throws ClientException{
//        try {
//             Map<Long, BigDecimal> docUnReservedQtyMap = Maps.newHashMap();
//             for (MaterialLot materialLot : materialLots) {
//                BigDecimal unReservedQty = BigDecimal.ZERO;
//                if(!StringUtils.isNullOrEmpty(materialLot.getReserved16())){
//                    unReservedQty = unReservedQty.add(materialLot.getReservedQty());
//                    DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(materialLot.getReserved16()));
//                    documentLine.setReservedQty(documentLine.getReservedQty().subtract(unReservedQty));
//                    documentLine.setUnReservedQty(documentLine.getUnReservedQty().add(unReservedQty));
//                    documentLine = documentLineRepository.saveAndFlush(documentLine);
//
//                    DeliveryOrder deliveryOrder = (DeliveryOrder) deliveryOrderRepository.findByObjectRrn(documentLine.getDocRrn());
//                    deliveryOrder.setUnReservedQty(deliveryOrder.getUnReservedQty().add(unReservedQty));
//                    deliveryOrder.setReservedQty(deliveryOrder.getReservedQty().subtract(unReservedQty));
//                    deliveryOrderRepository.save(deliveryOrder);
//                }
//                materialLot.setReserved16(StringUtils.EMPTY);
//                materialLot.setReserved17(StringUtils.EMPTY);
//                materialLot.setReserved18(StringUtils.EMPTY);
//                materialLotRepository.save(materialLot);
//            }
//
//        } catch (Exception e) {
//            throw ExceptionManager.handleException(e, log);
//        }
//    }

    /**
     * 根据packageType验证包装规则
     * @param materialLots
     * @param materialLotPackageType 包装规则
     */
    public void validationPackageRule(List<MaterialLot> materialLots, MaterialLotPackageType materialLotPackageType) throws ClientException{
        try {
            materialLotPackageType.validationPacking(materialLots);
            if (!StringUtils.isNullOrEmpty(materialLotPackageType.getMergeRule())) {
                mmsService.validationMergeRule(materialLotPackageType.getMergeRule(), materialLots);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialLot packageMLots(List<MaterialLotAction> materialLotActions, String packageType) throws ClientException{
        return packageMLots(materialLotActions, StringUtils.EMPTY, packageType);
    }

    /**
     * 对物料批次进行包装
     * @return
     */
    public MaterialLot packageMLots(List<MaterialLotAction> materialLotActions, String packedMaterialLotId, String packageType) throws ClientException{
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            // 取第一个的materialAction作为所有者的actionCode
            MaterialLotAction firstMaterialAction = materialLotActions.get(0);

            List<MaterialLot> materialLots = materialLotActions.stream().map(action -> mmsService.getMLotByMLotId(action.getMaterialLotId())).collect(Collectors.toList());

            //COB装箱只允许装一个lot，一个lot不允许超过13片
            if(MaterialLot.COB_PACKCASE.equals(packageType)){
                List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLots.get(0).getMaterialLotId());
                if (materialLotUnitList.size() > MaterialLot.COB_UNIT_SIZE){
                    throw new  ClientParameterException(MmsException.MM_MATERIAL_LOT_UNIT_SIZE_MORE_THAN_THIRTEEN, materialLots.get(0).getMaterialLotId());
                }
            }

            //格科要求装过箱的真空包也可以再次装箱，将等待装箱的真空包按照箱号分组，先进行拆箱操作
            materialLots = getWaitPackMaterialLots(materialLots);

            MaterialLotPackageType materialLotPackageType = getMaterialPackageTypeByName(packageType);
            validationPackageRule(materialLots, materialLotPackageType);

            MaterialLot packedMaterialLot = (MaterialLot) materialLots.get(0).clone();
            if (StringUtils.isNullOrEmpty(packedMaterialLotId)) {
                packedMaterialLotId = generatorPackageMLotId(packedMaterialLot, materialLotPackageType);
            }
            if (mmsService.getMLotByMLotId(packedMaterialLotId) != null) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, packedMaterialLotId);
            }
            packedMaterialLot.setMaterialLotId(packedMaterialLotId);
            packedMaterialLot.setCurrentQty(materialLotPackageType.getPackedQty(materialLotActions));
            packedMaterialLot.setReceiveQty(packedMaterialLot.getCurrentQty());
            packedMaterialLot.buildPackageMaterialLot(packageType);
            packedMaterialLot.setMaterialType(StringUtils.isNullOrEmpty(materialLotPackageType.getTargetMaterialType()) ? packedMaterialLot.getMaterialType() : materialLotPackageType.getTargetMaterialType());

            BigDecimal totalCurrentSubQty = BigDecimal.ZERO;
            for(MaterialLot materialLot : materialLots){
                if(materialLot.getCurrentSubQty() != null){
                    totalCurrentSubQty = totalCurrentSubQty.add(materialLot.getCurrentSubQty());
                }
            }
            if(totalCurrentSubQty.compareTo(BigDecimal.ZERO) > 0){
                packedMaterialLot.setCurrentSubQty(totalCurrentSubQty);
            }

            //GC要求包装的时候，也要进行包装数量的记录到箱子上以及相应的包装栏位也要有
            if (!StringUtils.isNullOrEmpty(materialLots.get(0).getReserved16())) {
                BigDecimal totalReservedQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getReservedQty));
                packedMaterialLot.setReservedQty(totalReservedQty);
            }
            packedMaterialLot = materialLotRepository.saveAndFlush(packedMaterialLot);

            // 记录创建历史
            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(packedMaterialLot, MaterialLotHistory.TRANS_TYPE_CREATE_PACKAGE);
            history.buildByMaterialLotAction(firstMaterialAction);
            history.setCreated(packedMaterialLot.getCreated());
            history.setReceiveDate(packedMaterialLot.getReceiveDate());
            materialLotHistoryRepository.save(history);

            packageMaterialLots(packedMaterialLot, materialLots, materialLotActions, false, true);
            return packedMaterialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科要求装过箱的真空包也可以再次装箱
     * @return
     */
    public List<MaterialLot> getWaitPackMaterialLots(List<MaterialLot> materialLots) throws ClientException {
        try {
            List<MaterialLot> waitPackMaterialLots = materialLots.stream().filter(materialLot -> StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())).collect(Collectors.toList());
            List<MaterialLot> waitUnPackMaterialLots = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())).collect(Collectors.toList());
            //将已经包装的真空包按照箱号分组拆包
            if(CollectionUtils.isNotEmpty(waitUnPackMaterialLots)){
                Map<String, List<MaterialLot>> waitUnPackMaterialLotMap = waitUnPackMaterialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
                for(String boxId : waitUnPackMaterialLotMap.keySet()){
                    List<MaterialLotAction> materialLotActions = Lists.newArrayList();
                    //箱中待拆箱的真空包
                    List<MaterialLot> materialLotList = waitUnPackMaterialLotMap.get(boxId);
                    MaterialLot packagedLot = mmsService.getMLotByMLotId(materialLotList.get(0).getParentMaterialLotId(), true);
                    for (MaterialLot materialLot : materialLotList){
                        MaterialLotAction materialLotAction = new MaterialLotAction();
                        materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                        materialLotAction.setTransQty(materialLot.getCurrentQty());
                        materialLotActions.add(materialLotAction);
                    }
                    unPack(packagedLot, materialLotList, materialLotActions);

                    //拆包之后的真空包添加到待装箱真空包列表中
                    List<MaterialLot> materialLotInfo = materialLotList.stream().map(MaterialLot -> mmsService.getMLotByMLotId(MaterialLot.getMaterialLotId(), true)).collect(Collectors.toList());
                    waitPackMaterialLots.addAll(materialLotInfo);
                }
            }
            return waitPackMaterialLots;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 批次包装
     * @param packedMaterialLot
     * @param waitToPackingLot
     * @param materialLotActions
     * @param subtractQtyFlag 是否扣减被包装批次的数量 后续版本去除此参数 根据物料属性自己判断
     * @param updateParentMLotFlag 是否更新包装批次的信息为被包装信息的parent相关栏位 后续版本去除此参数 根据物料属性自己判断
     */
    private void packageMaterialLots(MaterialLot packedMaterialLot, List<MaterialLot> waitToPackingLot, List<MaterialLotAction> materialLotActions,
                                        boolean subtractQtyFlag, boolean updateParentMLotFlag) throws ClientException {
        // 对物料批次做package事件处理 扣减物料批次数量
        for (MaterialLot materialLot : waitToPackingLot) {
//            materialLot.clearPackedMaterialLot();

            String materialLotId = materialLot.getMaterialLotId();
            MaterialLotAction materialLotAction = materialLotActions.stream().filter(action -> materialLotId.equals(action.getMaterialLotId())).findFirst().get();

            BigDecimal currentQty = materialLot.getCurrentQty();

            if (subtractQtyFlag) {
                materialLot.setCurrentQty(materialLot.getCurrentQty().subtract(materialLotAction.getTransQty()));
            }
            if (currentQty.compareTo(materialLotAction.getTransQty()) == 0) {
                if (updateParentMLotFlag) {
                    materialLot.setParentMaterialLotId(packedMaterialLot.getMaterialLotId());
                    materialLot.setParentMaterialLotRrn(packedMaterialLot.getObjectRrn());
                }
                materialLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_PACKAGE, MaterialStatus.STATUS_PACKED);
            }

            // 记录包装详情
            PackagedLotDetail packagedLotDetail = packagedLotDetailRepository.findByPackagedLotRrnAndMaterialLotRrn(packedMaterialLot.getObjectRrn(), materialLot.getObjectRrn());
            if (packagedLotDetail == null) {
                packagedLotDetail = new PackagedLotDetail();
                packagedLotDetail.setPackagedLotRrn(packedMaterialLot.getObjectRrn());
                packagedLotDetail.setPackagedLotId(packedMaterialLot.getMaterialLotId());
                packagedLotDetail.setMaterialLotRrn(materialLot.getObjectRrn());
                packagedLotDetail.setMaterialLotId(materialLot.getMaterialLotId());
                packagedLotDetail.setLotId(materialLot.getLotId());
            }
            packagedLotDetail.setQty(packagedLotDetail.getQty().add(materialLotAction.getTransQty()));
            packagedLotDetailRepository.save(packagedLotDetail);

            // 记录历史
            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_PACKAGE);

            // 如果批次在库存中，则直接消耗库存数量 支持一个批次在多个仓库中 故这里直接取第一个库存位置消耗
            // 不考虑subtractQtyFlag因素，都消耗库存
            List<MaterialLotInventory> materialLotInventories = mmsService.getMaterialLotInv(materialLot.getObjectRrn());
            if (CollectionUtils.isNotEmpty(materialLotInventories)) {
                MaterialLotInventory materialLotInventory = materialLotInventories.get(0);
                mmsService.saveMaterialLotInventory(materialLotInventory, materialLotAction.getTransQty().negate());

                history.setTransWarehouseId(materialLotInventory.getWarehouseId());
                history.setTransStorageId(materialLotInventory.getStorageId());
                history.setTransStorageType(materialLotInventory.getStorageType());
            }

            history.setActionCode(materialLotAction.getActionCode());
            history.setActionReason(materialLotAction.getActionReason());
            history.setActionComment(materialLotAction.getActionComment());
            materialLotHistoryRepository.save(history);

            List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
            if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                for (MaterialLotUnit materialLotUnit: materialLotUnitList){
                    //RW(COB)的装箱晶圆将箱号记录到lotId栏位，LotId信息记录到Durable栏位上
                    if(MaterialLot.RW_WAFER_SOURCE.equals(materialLot.getReserved50())){
                        materialLotUnit.setLotId(materialLot.getParentMaterialLotId());
                        materialLotUnit.setDurable(materialLot.getLotId());
                    }
                    materialLotUnit.setState(MaterialLotUnit.STATE_PACKAGE);
                    materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                    MaterialLotUnitHistory unitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotHistory.TRANS_TYPE_PACKAGE);
                    unitHistory.setCreated(materialLotUnit.getCreated());
                    materialLotUnitHisRepository.save(unitHistory);
                }
            }
        }

    }

    /**
     * 生成打包之后的物料批次号
     * @return
     * @throws ClientException
     */
    public String generatorPackageMLotId(MaterialLot packageMaterialLot, MaterialLotPackageType packageType) throws ClientException{
        try {
            GeneratorContext generatorContext = new GeneratorContext();
            generatorContext.setObject(packageMaterialLot);
            generatorContext.setRuleName(packageType.getPackIdRule());
            return generatorService.generatorId(ThreadLocalContext.getOrgRrn(), generatorContext);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
}
