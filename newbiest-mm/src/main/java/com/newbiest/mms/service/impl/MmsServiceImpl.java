package com.newbiest.mms.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.annotation.BaseJpaFilter;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.model.NBVersionControlHis;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.service.VersionControlService;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.*;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.commom.sm.service.StatusMachineService;
import com.newbiest.common.exception.ContextException;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.context.model.MergeRuleContext;
import com.newbiest.mms.MmsPropertyUtils;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.state.model.MaterialEvent;
import com.newbiest.mms.state.model.MaterialStatus;
import com.newbiest.mms.state.model.MaterialStatusModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2019/2/13.
 */
@Service
@Slf4j
@Transactional
@BaseJpaFilter
public class MmsServiceImpl implements MmsService {

    @Autowired
    BaseService baseService;

    @Autowired
    VersionControlService versionControlService;

    @Autowired
    RawMaterialRepository rawMaterialRepository;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialStatusModelRepository materialStatusModelRepository;

    @Autowired
    MaterialLotInventoryRepository materialLotInventoryRepository;

    @Autowired
    StatusMachineService statusMachineService;

    @Autowired
    GeneratorService generatorService;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    MaterialLotMergeRuleRepository materialLotMergeRuleRepository;

    @Autowired
    IQCCheckSheetRepository iqcCheckSheetRepository;

    @Autowired
    CheckSheetLineRepository checkSheetLineRepository;

    @Autowired
    MLotCheckSheetRepository mLotCheckSheetRepository;

    @Autowired
    MLotCheckSheetLineRepository mLotCheckSheetLineRepository;

    @Autowired
    MaterialLotHoldRepository materialLotHoldRepository;

    /**
     * 根据名称获取源物料。
     *  源物料不区分版本。故此处只会有1个
     * @param name 名称
     * @return
     * @throws ClientException
     */
    public RawMaterial getRawMaterialByName(String name) throws ClientException {
        try {
            return rawMaterialRepository.findOneByName(name);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存源物料。此处和versionControlService的save不同点在于
     * 1. 保存的时候直接激活，故物料只会是一个版本
     * 2. 激活状态允许修改数据
     * @param rawMaterial
     * @return
     * @throws ClientException
     */
    public RawMaterial saveRawMaterial(RawMaterial rawMaterial) throws ClientException {
        try {
            if (rawMaterial.getObjectRrn() == null) {
                rawMaterial.setActiveTime(new Date());
                rawMaterial.setActiveUser(ThreadLocalContext.getUsername());
                rawMaterial.setStatus(DefaultStatusMachine.STATUS_ACTIVE);
                Long version = versionControlService.getNextVersion(rawMaterial);
                rawMaterial.setVersion(version);

                baseService.saveEntity(rawMaterial, NBVersionControlHis.TRANS_TYPE_CREATE_AND_ACTIVE);
            } else {
                NBVersionControl oldData = rawMaterialRepository.findByObjectRrn(rawMaterial.getObjectRrn());
                // 不可改变状态
                rawMaterial.setStatus(oldData.getStatus());
                baseService.saveEntity(rawMaterial);
            }
            return rawMaterial;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public List<MaterialLot> stockIn(List<MaterialLot> materialLots, List<MaterialLotAction> materialLotActionList) throws ClientException {
        try {
            List<MaterialLot> materialLotList = Lists.newArrayList();
            materialLots.forEach(materialLot -> {
                MaterialLotAction materialLotAction = materialLotActionList.stream().filter(action -> action.getMaterialLotId().equals(materialLot.getMaterialLotId())).findFirst().get();
                MaterialLot stockInMaterialLot = stockIn(materialLot, materialLotAction);
                materialLotList.add(stockInMaterialLot);
            });

            return materialLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 发料
     *  此处只能全部发料 不存在发一部分的数量
     * @param materialLot 物料批次
     * @return
     * @throws ClientException
     */
    public MaterialLot issue(MaterialLot materialLot) throws ClientException {
        try {
            materialLot.setCurrentQty(BigDecimal.ZERO);
            materialLot.setCurrentSubQty(BigDecimal.ZERO);
            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_ISSUE, StringUtils.EMPTY);

            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_ISSUE);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 退料
     *  此处只能全部发料 不存在发一部分的数量
     * @param materialLot 物料批次
     * @return
     * @throws ClientException
     */
    public MaterialLot returnMLot(MaterialLot materialLot) throws ClientException {
        try {
            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_RECEIVE, MaterialStatus.STATUS_RECEIVE);
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_RETURN);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次分批
     * @param parentMaterialLotId 母批的物料批次号
     * @param materialLotAction 动作包含数量原因等
     * @return
     * @throws ClientException
     */
    public MaterialLot splitMLot(String parentMaterialLotId, MaterialLotAction materialLotAction) throws ClientException {
        try {
            MaterialLot materialLot = getMLotByMLotId(parentMaterialLotId, true);
            BigDecimal splitQty = materialLotAction.getTransQty();
            if (materialLot.getCurrentQty().compareTo(splitQty) <= 0){
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_QTY_CANT_LESS_THEN_ZERO);
            }
            materialLot.setCurrentQty(materialLot.getCurrentQty().subtract(splitQty));
            materialLot.setReceiveQty(materialLot.getReceiveQty().subtract(splitQty));
            baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_SPLIT, materialLotAction);

            String subMLotId = generatorSubMLotId(MaterialLot.GENERATOR_SUB_MATERIAL_LOT_ID_RULE, materialLot);
            MaterialLot subMaterialLot =  (MaterialLot) materialLot.clone();
            subMaterialLot.setMaterialLotId(subMLotId);
            subMaterialLot.setCurrentQty(splitQty);
            subMaterialLot.setReceiveQty(splitQty);
            subMaterialLot.setParentMaterialLot(materialLot);
            baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_SPLIT_CREATE, materialLotAction);
            return subMaterialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次入库并执行StockIn事件 只会修改库存数量 并不会修改物料批次的数量
     * @param materialLot 物料批次
     * @param materialLotAction 动作需要包含目标仓库以及数量
     * @return
     */
    public MaterialLot stockIn(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException {
        return stockIn(materialLot, MaterialEvent.EVENT_STOCK_IN, materialLotAction);
    }

    /**
     * 根据物料号以及仓库 获取物料批次库存
     * @param mLotRrn 物料号
     * @param warehouseRrn 仓库
     * @param storageRrn 库位
     */
    public MaterialLotInventory getMaterialLotInv(String mLotRrn, String warehouseRrn, String storageRrn) throws ClientException {
        return materialLotInventoryRepository.findByMaterialLotRrnAndWarehouseRrnAndStorageRrn(mLotRrn, warehouseRrn, storageRrn);
    }

    /**
     * 根据物料批次号获取物料库存
     * @param mLotRrn
     * @return
     * @throws ClientException
     */
    public List<MaterialLotInventory> getMaterialLotInv(String mLotRrn) throws ClientException {
        return materialLotInventoryRepository.findByMaterialLotRrn(mLotRrn);
    }

    public Storage getStorageByWarehouseRrnAndName(Warehouse warehouse, String storageId) throws ClientException {
        return storageRepository.findByWarehouseRrnAndName(warehouse.getObjectRrn(), storageId);
    }

    /**
     * 获取默认的库位。这个库位可以挂在任意仓库下面
     *  如果系统中没有默认库位则直接创建一个
     */
    private Storage getDefaultStorage(Warehouse warehouse) throws ClientException{
        try {
            Storage storage = getStorageByWarehouseRrnAndName(warehouse, Storage.DEFAULT_STORAGE_NAME);
            if (storage == null) {
                storage = new Storage();
                storage.setWarehouseRrn(warehouse.getObjectRrn());
                storage.setName(Storage.DEFAULT_STORAGE_NAME);
                storage.setDescription(StringUtils.SYSTEM_CREATE);
                storage = storageRepository.saveAndFlush(storage);
            }
            return storage;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * 根据Action上目标库位进行返回库位信息，如果没有指定库位则返回默认库位
     * @param materialLotAction
     * @param warehouse
     * @return
     */
    private Storage getTargetStorageByMaterialLotAction(MaterialLotAction materialLotAction, @NotNull  Warehouse warehouse) {
        try {
            Storage targetStorage;
            if (materialLotAction.getTargetStorageRrn() != null) {
                targetStorage = storageRepository.findByObjectRrn(materialLotAction.getTargetStorageRrn());
            } else if (!StringUtils.isNullOrEmpty(materialLotAction.getTargetStorageId())) {
                targetStorage = getStorageByWarehouseRrnAndName(warehouse, materialLotAction.getTargetStorageId());
                if (targetStorage == null ) {
                    if (MmsPropertyUtils.getAutoCreateStorageFlag()) {
                        targetStorage = new Storage();
                        targetStorage.setName(materialLotAction.getTargetStorageId());
                        targetStorage.setDescription(StringUtils.SYSTEM_CREATE);
                        targetStorage.setWarehouseRrn(warehouse.getObjectRrn());
                        targetStorage = storageRepository.saveAndFlush(targetStorage);
                    } else {
                        throw new ClientParameterException(MmsException.MM_STORAGE_IS_NOT_EXIST);

                    }
                }
            } else {
                targetStorage = getDefaultStorage(warehouse);
                materialLotAction.setTargetStorageRrn(targetStorage.getObjectRrn());
            }
            return targetStorage;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据Action上目标库位进行返回库位信息，如果没有指定库位则返回默认库位
     * @param materialLotAction
     * @return
     */
    private Storage getFromStorageByMaterialLotAction(MaterialLotAction materialLotAction, Warehouse warehouse) {
        try {
            Storage targetStorage = null;
            if (materialLotAction.getFromStorageRrn() != null) {
                targetStorage = storageRepository.findByObjectRrn(materialLotAction.getFromStorageRrn());
            } else {
                targetStorage = getDefaultStorage(warehouse);
                materialLotAction.setFromStorageRrn(targetStorage.getObjectRrn());
            }
            return targetStorage;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次入库 只会修改库存数量 并不会修改物料批次的数量
     * @param materialLot 物料批次
     * @param eventId 事件号
     * @param materialLotAction 动作需要包含目标仓库以及数量
     * @return
     */
    private MaterialLot stockIn(MaterialLot materialLot, String eventId, MaterialLotAction materialLotAction) throws ClientException {
        try {
            PreConditionalUtils.checkNotNull(materialLotAction.getTargetWarehouseRrn(), "TargetWarehouseRrn");
            materialLot.validateMLotHold();
            Warehouse targetWarehouse = warehouseRepository.findByObjectRrn(materialLotAction.getTargetWarehouseRrn());
            Storage targetStorage = getTargetStorageByMaterialLotAction(materialLotAction, targetWarehouse);

            // 变更物料库存并改变物料批次状态
            saveMaterialLotInventory(materialLot, targetWarehouse, targetStorage, materialLotAction.getTransQty());

            // 记录下批次最后一次入库的仓库
            materialLot.setLastWarehouseRrn(targetWarehouse.getObjectRrn());
            materialLot.setLastWarehouseId(targetWarehouse.getName());
            materialLot.setLastStorageRrn(targetStorage.getObjectRrn());
            materialLot.setLastStorageId(targetStorage.getName());
            changeMaterialLotState(materialLot, eventId, StringUtils.EMPTY);

            materialLotAction.setTargetWarehouseId(targetWarehouse.getName());
            materialLotAction.setTargetStorageId(targetStorage.getName());
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_STOCK_IN, materialLotAction);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialLot getMLotByObjectRrn(String materialLotRrn) throws ClientException{
        return materialLotRepository.findByObjectRrn(materialLotRrn);
    }
    
    /**
     * 盘点 物料批次数量，物料批次库存 以盘点数量为准
     *  支持从有盘无 不支持从无盘有，从无盘有当前必须用入库功能
     * @param materialLot
     * @param materialLotAction
     * @return
     */
    public MaterialLotInventory checkMaterialInventory(MaterialLot materialLot, MaterialLotAction materialLotAction) {
        try {
            PreConditionalUtils.checkNotNull(materialLotAction.getFromWarehouseRrn(), StringUtils.EMPTY);
            materialLot.validateMLotHold();

            Warehouse fromWarehouse = warehouseRepository.findByObjectRrn(materialLotAction.getFromWarehouseRrn());
            Storage fromStorage = getFromStorageByMaterialLotAction(materialLotAction, fromWarehouse);
            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(), fromWarehouse.getObjectRrn(), fromStorage.getObjectRrn());
            if (materialLotInventory == null) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_NOT_IN_INVENTORY);
            }
            materialLot = getMLotByObjectRrn(materialLot.getObjectRrn());
            // 如果盘点为0 则表示物料批次被用完，则触发出库事件
            String eventId = MaterialEvent.EVENT_CHECK;
            if (materialLotAction.getTransQty().compareTo(BigDecimal.ZERO) == 0) {
                eventId = MaterialEvent.EVENT_STOCK_OUT;
            }
            materialLot = changeMaterialLotState(materialLot, eventId, StringUtils.EMPTY);
            // 盘点的时候 物料批次数量，物料批次库存 以盘点数量为准
            materialLotInventory.setStockQty(materialLotAction.getTransQty());
            if (materialLotInventory.getStockQty().compareTo(BigDecimal.ZERO) == 0) {
                materialLotInventoryRepository.delete(materialLotInventory);
                materialLotInventory = null;
            } else {
                materialLotInventory = materialLotInventoryRepository.saveAndFlush(materialLotInventory);
            }

            materialLot.setCurrentQty(materialLotAction.getTransQty());
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            materialLotAction.setFromWarehouseId(fromWarehouse.getName());
            materialLotAction.setFromStorageId(fromStorage.getName());
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_CHECK, materialLotAction);
            return materialLotInventory;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次出货。扣减物料批次数量
     * @param materialLot 物料批次
     * @param materialLotAction 动作需要包含来源仓库以及数量
     * @return
     * @throws ClientException
     */
    public MaterialLot stockOut(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException {
        try {
            PreConditionalUtils.checkNotNull(materialLotAction.getFromWarehouseRrn(), StringUtils.EMPTY);
            materialLot.validateMLotHold();

            Warehouse fromWarehouse = warehouseRepository.findByObjectRrn(materialLotAction.getFromWarehouseRrn());
            Storage fromStorage = getFromStorageByMaterialLotAction(materialLotAction, fromWarehouse);
            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(), fromWarehouse.getObjectRrn(), fromStorage.getObjectRrn());
            if (materialLotInventory == null) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_NOT_IN_INVENTORY);
            }
            materialLot = getMLotByObjectRrn(materialLot.getObjectRrn());

            if (materialLot.getCurrentQty().compareTo(materialLotAction.getTransQty()) != 0) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_MUST_STOCK_OUT_ALL);
            }
            // 变更物料库存并改变物料批次状态
            saveMaterialLotInventory(materialLot, fromWarehouse, fromStorage, materialLotAction.getTransQty().negate());
            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_STOCK_OUT, StringUtils.EMPTY);

            //修改批次数量
            materialLot.setCurrentQty(materialLot.getCurrentQty().subtract(materialLotAction.getTransQty()));
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            materialLotAction.setFromWarehouseId(fromWarehouse.getName());
            materialLotAction.setFromStorageId(fromStorage.getName());
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_STOCK_OUT, materialLotAction);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 领料出库 和出库的不同就在于不会扣减物料的当前数量只会扣减库存
     * @param materialLot 物料批次
     * @param materialLotAction 动作需要包含来源仓库以及数量
     * @return
     * @throws ClientException
     */
    public MaterialLot pick(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException {
        try {
            PreConditionalUtils.checkNotNull(materialLotAction.getFromWarehouseRrn(), StringUtils.EMPTY);
            materialLot.validateMLotHold();

            Warehouse fromWarehouse = warehouseRepository.findByObjectRrn(materialLotAction.getFromWarehouseRrn());
            Storage fromStorage = getFromStorageByMaterialLotAction(materialLotAction, fromWarehouse);

            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(), fromWarehouse.getObjectRrn(), fromStorage.getObjectRrn());
            if (materialLotInventory == null) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_NOT_IN_INVENTORY);
            }
            materialLot = getMLotByObjectRrn(materialLot.getObjectRrn());

            if (materialLot.getCurrentQty().compareTo(materialLotAction.getTransQty()) != 0) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_MUST_PICK_ALL);
            }
            // 变更物料库存并改变物料批次状态
            saveMaterialLotInventory(materialLot, fromWarehouse, fromStorage, materialLotAction.getTransQty().negate());
            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_PICK, StringUtils.EMPTY);

            materialLotAction.setFromWarehouseId(fromWarehouse.getName());
            materialLotAction.setFromStorageId(fromStorage.getName());
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_PICK, materialLotAction);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次转移。从A仓库转移到B仓库
     * 当前没有通过事件。即没卡控何种状态可以做transfer。
     * @param materialLot
     * @param materialLotAction
     * @return
     * @throws ClientException
     */
    public MaterialLotInventory transfer(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException {
        try {
            PreConditionalUtils.checkNotNull(materialLotAction.getFromWarehouseRrn(), StringUtils.EMPTY);
            PreConditionalUtils.checkNotNull(materialLotAction.getTargetWarehouseRrn(), StringUtils.EMPTY);

            materialLot.validateMLotHold();

            Warehouse fromWarehouse = warehouseRepository.findByObjectRrn(materialLotAction.getFromWarehouseRrn());
            Storage fromStorage = getFromStorageByMaterialLotAction(materialLotAction, fromWarehouse);

            Warehouse targetWarehouse = warehouseRepository.findByObjectRrn(materialLotAction.getTargetWarehouseRrn());
            Storage targetStorage = getTargetStorageByMaterialLotAction(materialLotAction, targetWarehouse);

            if (materialLotAction.getFromWarehouseRrn().equals(materialLotAction.getTargetWarehouseRrn()) && fromStorage.getObjectRrn().equals(targetStorage.getObjectRrn())) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_TRANSFER_MUST_DIFFERENT_STORAGE);
            }

            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(), fromWarehouse.getObjectRrn(), fromStorage.getObjectRrn());
            if (materialLotInventory == null) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_NOT_IN_INVENTORY);
            }
            materialLot = getMLotByObjectRrn(materialLot.getObjectRrn());

            if (materialLot.getCurrentQty().compareTo(materialLotAction.getTransQty()) != 0) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_MUST_TRANSFER_ALL);
            }
            materialLotInventory.setWarehouse(targetWarehouse).setStorage(targetStorage);
            materialLotInventoryRepository.saveAndFlush(materialLotInventory);

            materialLotAction.setFromWarehouseId(fromWarehouse.getName());
            materialLotAction.setFromStorageId(fromStorage.getName());
            materialLotAction.setTargetWarehouseId(targetWarehouse.getName());
            materialLotAction.setTargetStorageId(targetStorage.getName());
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_TRANSFER, materialLotAction);
            return materialLotInventory;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 消耗批次物料
     * 允许传入数量为负数，负数表示反消耗
     * @param materialLot 物料批次
     * @param materialLotAction 动作
     */
    public MaterialLot consumeMLot(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException {
        try {
            materialLot = getMLotByObjectRrn(materialLot.getObjectRrn());

            BigDecimal currentQty = materialLot.getCurrentQty().subtract(materialLotAction.getTransQty());
            if (currentQty.compareTo(BigDecimal.ZERO) < 0) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_QTY_CANT_LESS_THEN_ZERO);
            }

            // 当批次在库存中，无法进行消耗/反消耗 只能进行盘点
            List<MaterialLotInventory> materialLotInventories = getMaterialLotInv(materialLot.getObjectRrn());
            if (CollectionUtils.isNotEmpty(materialLotInventories)) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_IN_INVENTORY);
            }

            String eventId = MaterialEvent.EVENT_CONSUME;
            if (currentQty.compareTo(BigDecimal.ZERO) == 0) {
                eventId = MaterialEvent.EVENT_USE_UP;
            }
            materialLot = changeMaterialLotState(materialLot, eventId, StringUtils.EMPTY);

            materialLot.setCurrentQty(currentQty);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_CONSUME, materialLotAction);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 批量hold。
     *  可以一个批次同时hold多次，也可以多个批次同时hold
     * @param materialLotActions
     * @return
     * @throws ClientException
     */
    public void holdMaterialLot(List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            Map<String, List<MaterialLotAction>> materialLotHoldMap = materialLotActions.stream().collect(Collectors.groupingBy(MaterialLotAction :: getMaterialLotId));
            for (String materialLotId : materialLotHoldMap.keySet()) {
                holdMaterialLot(materialLotId, materialLotHoldMap.get(materialLotId));
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 暂停物料批次
     *  支持多重Hold
     * @param materialLotId 物料批次号
     * @param materialLotActions hold原因
     * @return
     */
    public MaterialLot holdMaterialLot(String materialLotId, List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            MaterialLot materialLot = getMLotByMLotId(materialLotId, true);

            if (MaterialLot.HOLD_STATE_OFF.equals(materialLot.getHoldState())) {
                materialLot.setHoldState(MaterialLot.HOLD_STATE_ON);
                materialLot = materialLotRepository.saveAndFlush(materialLot);
            }

            for (MaterialLotAction materialLotAction : materialLotActions) {
                MaterialLotHold materialLotHold = new MaterialLotHold();
                materialLotHold.setMaterialLot(materialLot).setAction(materialLotAction);

                String actionPassword = materialLotAction.getActionPassword();
                if (!StringUtils.isNullOrEmpty(actionPassword)) {
                    materialLotHold.setActionPassword(EncryptionUtils.md5Hex(actionPassword));
                }
                materialLotHoldRepository.save(materialLotHold);

                baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_HOLD, materialLotAction);
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 批量Release。
     *  可以批量释放批次的HOLD。批量释放的原因是一样的
     * @param materialLotHolds 物料批次的HOLD信息
     * @param releaseLotAction 释放物料批次动作
     * @return
     * @throws ClientException
     */
    public void releaseMaterialLot(List<MaterialLotHold>  materialLotHolds, MaterialLotAction releaseLotAction) throws ClientException{
        try {
            Map<String, List<MaterialLotHold>> materialLotHoldMap = materialLotHolds.stream().collect(Collectors.groupingBy(MaterialLotHold :: getMaterialLotId));
            for (String materialLotId : materialLotHoldMap.keySet()) {
                releaseMaterialLot(materialLotId, materialLotHoldMap.get(materialLotId), releaseLotAction);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 释放物料批次
     *  支持一个原因码解锁多个Hold
     * @param materialLotId 物料批次号
     * @param materialLotHolds 物料批次的Hold
     * @param releaseLotAction 释放物料批次动作
     * @return
     */
    public void releaseMaterialLot(String materialLotId, List<MaterialLotHold>  materialLotHolds, MaterialLotAction releaseLotAction) throws ClientException{
        try {
            MaterialLot materialLot = getMLotByMLotId(materialLotId, true);

            for (MaterialLotHold materialLotHold : materialLotHolds) {

                if (!materialLot.getMaterialLotId().equals(materialLotHold.getMaterialLotId())) {
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_HOLD_IS_NOT_EXIST, materialLotId);
                }
                String actionPassword = materialLotHold.getActionPassword();
                if (!StringUtils.isNullOrEmpty(actionPassword) && !actionPassword.equals(EncryptionUtils.md5Hex(releaseLotAction.getActionPassword()))) {
                    throw new ClientException(MmsException.MM_MATERIAL_LOT_HOLD_PASSWORD_IS_ERROR);
                }
            }
            materialLotHoldRepository.deleteInBatch(materialLotHolds);

            List<MaterialLotHold> remainingMLotHolds = materialLotHoldRepository.findByMaterialLotId(materialLotId);
            if (CollectionUtils.isEmpty(remainingMLotHolds)) {
                materialLot.setHoldState(MaterialLot.HOLD_STATE_OFF);
                materialLot = materialLotRepository.saveAndFlush(materialLot);
            }
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_RELEASE, releaseLotAction);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 更新物料库存
     * @throws ClientException
     */
    public void saveMaterialLotInventory(MaterialLotInventory materialLotInventory, BigDecimal transQty) throws ClientException {
        try {
            materialLotInventory.setStockQty(materialLotInventory.getStockQty().add(transQty));
            if (materialLotInventory.getStockQty().compareTo(BigDecimal.ZERO) < 0) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_STOCK_QTY_CANT_LESS_THEN_ZERO);
            } else if (materialLotInventory.getStockQty().compareTo(BigDecimal.ZERO) == 0) {
                if (materialLotInventory.getObjectRrn() != null) {
                    materialLotInventoryRepository.delete(materialLotInventory);
                }
            } else {
                materialLotInventoryRepository.save(materialLotInventory);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 更新物料批次的库存数量
     * @param materialLot 物料批次
     * @param warehouse 仓库
     * @param transQty 数量
     * @throws ClientException
     */
    private void saveMaterialLotInventory(MaterialLot materialLot, Warehouse warehouse, Storage storage, BigDecimal transQty) throws ClientException {
        try {
            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(), warehouse.getObjectRrn(), storage.getObjectRrn());
            if (materialLotInventory == null) {
                materialLotInventory = new MaterialLotInventory();
                materialLotInventory.setMaterialLot(materialLot).setWarehouse(warehouse);
                if (storage != null) {
                    materialLotInventory.setStorage(storage);
                }

            }
            saveMaterialLotInventory(materialLotInventory, transQty);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收物料批次并入库指定物料批次号
     * 如果没有指定入的仓库，则直接入到物料上绑定的仓库
     * @param rawMaterial 原物料
     * @param mLotId 物料批次号
     * @param materialLotAction 操作物料批次的动作包括了操作数量以及原因
     * @return
     */
    public MaterialLot receiveMLot2Warehouse(RawMaterial rawMaterial, String mLotId, MaterialLotAction materialLotAction) throws ClientException {
        try {
            MaterialLot materialLot = receiveMLot(rawMaterial, mLotId, materialLotAction);
            if (materialLotAction.getTargetWarehouseRrn() == null) {
                materialLotAction.setTargetWarehouseRrn(rawMaterial.getWarehouseRrn());
            }
            if (materialLotAction.getTargetWarehouseRrn() != null) {
                materialLot = stockIn(materialLot, materialLotAction);
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialStatusModel getStatusModelByRrn(String statusModelRrn) throws ClientException {
        try {
            return materialStatusModelRepository.findByObjectRrn(statusModelRrn);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * iqc配置
     * @param materialLotAction
     * @throws ClientException
     */
    public MLotCheckSheet iqc(MaterialLotAction materialLotAction) throws ClientException {
        try {
            return judgeByCheckSheet(materialLotAction, MaterialEvent.EVENT_IQC);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 判定
     * @param materialLotAction
     * @throws ClientException
     */
    public MLotCheckSheet judgeByCheckSheet(MaterialLotAction materialLotAction, String eventId) throws ClientException {
        try {
            String materialLotId = materialLotAction.getMaterialLotId();
            MaterialLot materialLot = getMLotByMLotId(materialLotId);

            changeMaterialLotState(materialLot, eventId, materialLotAction.getActionCode());

            MLotCheckSheet mLotCheckSheet = mLotCheckSheetRepository.findByMaterialLotIdAndStatus(materialLotId, MLotCheckSheet.STATUS_OPEN);
            mLotCheckSheet.setCheckResult(materialLotAction.getActionCode());
            mLotCheckSheet.setStatus(MLotCheckSheet.STATUS_CLOSE);
            mLotCheckSheet.setRemark2(materialLotAction.getActionComment());
            mLotCheckSheet = mLotCheckSheetRepository.saveAndFlush(mLotCheckSheet);
            return mLotCheckSheet;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 触发IQC
     * @param materialLot
     * @throws ClientException
     */
    public void triggerIqc(Material material, MaterialLot materialLot) throws ClientException{
        try {
            IqcCheckSheet iqcCheckSheet = iqcCheckSheetRepository.findByObjectRrn(material.getIqcSheetRrn());

            MLotCheckSheet mLotCheckSheet = new MLotCheckSheet();
            mLotCheckSheet.setMaterialLotId(materialLot.getMaterialLotId());
            mLotCheckSheet.setSheetName(iqcCheckSheet.getName());
            mLotCheckSheet.setSheetDesc(iqcCheckSheet.getDescription());
            mLotCheckSheet.setSheetCategory(iqcCheckSheet.getCategory());
            mLotCheckSheet = mLotCheckSheetRepository.save(mLotCheckSheet);

            List<CheckSheetLine> checkSheetLines = checkSheetLineRepository.findByCheckSheetRrn(material.getIqcSheetRrn());
            for (CheckSheetLine checkSheetLine : checkSheetLines) {
                MLotCheckSheetLine mLotCheckSheetLine = new MLotCheckSheetLine();
                mLotCheckSheetLine.setMLotCheckSheetRrn(mLotCheckSheet.getObjectRrn());
                mLotCheckSheetLine.setSheetName(iqcCheckSheet.getName());
                mLotCheckSheetLine.setSheetDesc(iqcCheckSheet.getDescription());

                mLotCheckSheetLine.setName(checkSheetLine.getName());
                mLotCheckSheetLine.setDescription(checkSheetLine.getDescription());
                mLotCheckSheetLineRepository.save(mLotCheckSheetLine);
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收物料批次
     *  事先会先导入物料批次，此时只是做接收
     * @param material 物料
     * @param materialLotList 物料批次
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> receiveMLot(Material material, List<MaterialLot> materialLotList) throws ClientException {
        try {
            String materialName = material.getName();
            if (material instanceof RawMaterial) {
                material = rawMaterialRepository.findOneByName(materialName);
            }
            if (material == null) {
                throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_IS_NOT_EXIST, materialName);
            }
            for (MaterialLot materialLot : materialLotList) {
                String materialLotId = materialLot.getMaterialLotId();
                materialLot = getMLotByMLotId(materialLotId);
                if (materialLot == null) {
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, materialLotId);
                }
                String targetStatus = MaterialStatus.STATUS_RECEIVE;
                if (material.getIqcSheetRrn() != null) {
                    targetStatus = MaterialStatus.STATUS_IQC;
                    // IQC检查
                    triggerIqc(material, materialLot);
                }
                materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_RECEIVE, targetStatus);

                baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_RECEIVE);
            }
            return materialLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收物料批次 根据状态模型决定是否允许重复接收
     * @param material 原物料
     * @param mLotId 物料批次号
     * @param materialLotAction 操作物料批次的动作包括了操作数量以及原因
     * @return
     */
    @Deprecated
    public MaterialLot receiveMLot(Material material, String mLotId, MaterialLotAction materialLotAction) {
        try {
            MaterialLot materialLot = null;

            String statusModelRrn = material.getStatusModelRrn();
            StatusModel statusModel;
            if (statusModelRrn == null) {
                statusModel = materialStatusModelRepository.findOneByName(Material.DEFAULT_STATUS_MODEL);
            } else {
                statusModel = materialStatusModelRepository.findByObjectRrn(material.getStatusModelRrn());
            }
            if (!StringUtils.isNullOrEmpty(mLotId)) {
                materialLot = getMLotByMLotId(mLotId);
                if (materialLot != null) {
                    try {
                        Assert.assertEquals(material.getName(), materialLot.getMaterialName());
                    } catch (AssertionError e) {
                        throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_IS_NOT_SAME, material.getName(), materialLot.getMaterialName());
                    }
                    materialLot.setGrade(materialLotAction.getGrade());
                    materialLot.setReceiveQty(materialLot.getReceiveQty().add(materialLotAction.getTransQty()));
                    materialLot.setCurrentQty(materialLot.getCurrentQty().add(materialLotAction.getTransQty()));
                }
            }

            if (materialLot == null) {
                materialLot = createMLot(material, statusModel, mLotId, materialLotAction.getTransQty(), BigDecimal.ZERO, Maps.newHashMap());
            }
            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_RECEIVE, StringUtils.EMPTY);

            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_RECEIVE, materialLotAction);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialLot getMLotByMLotId(String mLotId, boolean throwExceptionFlag) throws ClientException{
        try {
            MaterialLot materialLot =  materialLotRepository.findByMaterialLotId(mLotId);
            if (materialLot == null && throwExceptionFlag) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, mLotId);
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialLot getMLotByMLotId(String mLotId) throws ClientException{
        try {
            return getMLotByMLotId(mLotId, false);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 创建物料批次
     * @param material 物料或者产品
     * @param  mLotId 物料批次号。当为空的时候，按照设定的物料批次号生成规则进行生成
     * @return
     * @throws ClientException
     */
    public MaterialLot createMLot(Material material, StatusModel statusModel, String mLotId, BigDecimal transQty, BigDecimal transSubQty,
                                  Map<String, Object> propsMap) throws ClientException {
        try {
            if (StringUtils.isNullOrEmpty(mLotId)) {
                mLotId = generatorMLotId(material);
            }
            MaterialLot materialLot = getMLotByMLotId(mLotId);
            if (materialLot != null) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_IS_EXIST);
            }
            materialLot = new MaterialLot();
            materialLot.setMaterialLotId(mLotId);

            materialLot.setStatusModelRrn(statusModel.getObjectRrn());
            materialLot.setStatusCategory(statusModel.getInitialStateCategory());
            materialLot.setStatus(statusModel.getInitialState());

            materialLot.setReceiveDate(new Date());
            materialLot.setMaterial(material);

            if (propsMap != null && propsMap.size() > 0) {
                for (String propName : propsMap.keySet()) {
                    Object propValue = propsMap.get(propName);
                    if (propValue == null || StringUtils.isNullOrEmpty(propValue.toString())) {
                        continue;
                    }
                    PropertyUtils.setProperty(materialLot, propName, propsMap.get(propName));
                }
            }
            materialLot.setReceiveQty(transQty);
            materialLot.setCurrentQty(transQty);
            materialLot.setCurrentSubQty(transSubQty);

            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setTransQty(transQty);
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_CREATE, materialLotAction);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据ID生成规则设置的生成物料批次号
     * @return
     * @throws ClientException
     */
    public String generatorMLotId(Material material) throws ClientException{
        try {
            GeneratorContext generatorContext = new GeneratorContext();
            generatorContext.setRuleName(MaterialLot.GENERATOR_MATERIAL_LOT_ID_RULE);
            generatorContext.setObject(material);
            String id = generatorService.generatorId(generatorContext);
            return id;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public String generatorSubMLotId(String ruleName, MaterialLot materialLot) throws ClientException{
        try {
            GeneratorContext generatorContext = new GeneratorContext();
            generatorContext.setObject(materialLot);
            generatorContext.setRuleName(ruleName);
            String id = generatorService.generatorId(generatorContext);
            return id;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证合批规则
     */
    public void validationMergeRule(String ruleName, List<MaterialLot> materialLots) throws ClientException{
        try {
            MaterialLotMergeRule mergeRule = materialLotMergeRuleRepository.findOneByName(ruleName);
            if (mergeRule == null) {
                throw new ClientParameterException(ContextException.MERGE_RULE_IS_NOT_EXIST, ruleName);
            }
            MergeRuleContext mergeRuleContext = new MergeRuleContext();
            mergeRuleContext.setBaseObject(materialLots.get(0));
            mergeRuleContext.setCompareObjects(materialLots);
            mergeRuleContext.setMergeRuleLines(mergeRule.getLines());
            mergeRuleContext.validation();
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据StateMachine修改物料批次的状态
     * @param mLot 物料批次
     * @param eventId 触发的事件 不可为空
     * @param targetStatus 目标状态。因为一个事件可能有多个目标状态可强行指定转到具体的状态。不指定则以event上优先级最高的来当状态
     * @return
     * @throws ClientException
     */
    public MaterialLot changeMaterialLotState(MaterialLot mLot, String eventId, String targetStatus) throws ClientException {
        try {
            mLot = (MaterialLot) statusMachineService.triggerEvent(mLot, eventId, targetStatus);
            mLot = materialLotRepository.saveAndFlush(mLot);
            return mLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public Warehouse getWarehouseByName(String name) throws ClientException {
        return warehouseRepository.findOneByName(name);
    }

    public List<MaterialLot> getMLotByIncomingDocId(String incomingDocId) throws ClientException {
        return materialLotRepository.findByIncomingDocId(incomingDocId);
    }

}
