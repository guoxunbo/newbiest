package com.newbiest.mms.service.impl;

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
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.commom.sm.service.StatusMachineService;
import com.newbiest.common.exception.ContextException;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.context.model.MergeRuleContext;
import com.newbiest.mms.MmsPropertyUtils;
import com.newbiest.mms.application.event.HoldMLotApplicationEvent;
import com.newbiest.mms.application.event.SplitMLotApplicationEvent;
import com.newbiest.mms.application.event.StockInApplicationEvent;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PrintService;
import com.newbiest.mms.state.model.MaterialEvent;
import com.newbiest.mms.state.model.MaterialStatus;
import com.newbiest.mms.state.model.MaterialStatusCategory;
import com.newbiest.mms.state.model.MaterialStatusModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PrintService printService;

    @Autowired
    LabMaterialRepository labMaterialRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    PartsRepository partsRepository;

    @Autowired
    ApplicationContext applicationContext;

    /**
     * 根据名称获取源物料。
     * 源物料不区分版本。故此处只会有1个
     *
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
     *
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

                rawMaterial = (RawMaterial) baseService.saveEntity(rawMaterial, NBVersionControlHis.TRANS_TYPE_CREATE_AND_ACTIVE);
            } else {
                NBVersionControl oldData = rawMaterialRepository.findByObjectRrn(rawMaterial.getObjectRrn());
                // 不可改变状态
                rawMaterial.setStatus(oldData.getStatus());
                rawMaterial = (RawMaterial) baseService.saveEntity(rawMaterial);
            }
            return rawMaterial;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public RawMaterial saveRawMaterial(RawMaterial rawMaterial, String warehouseName, String iqcSheetName) throws ClientException {
        try {
            if (!StringUtils.isNullOrEmpty(warehouseName)) {
                Warehouse warehouse = getWarehouseByName(warehouseName, true);
                rawMaterial.setWarehouseRrn(warehouse.getObjectRrn());
            }

            if (!StringUtils.isNullOrEmpty(iqcSheetName)) {
                IqcCheckSheet iqcCheckSheet = getIqcSheetByName(iqcSheetName, true);
                rawMaterial.setIqcSheetRrn(iqcCheckSheet.getObjectRrn());
            }
            return saveRawMaterial(rawMaterial);
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

            applicationContext.publishEvent(new StockInApplicationEvent(this, materialLotList, materialLotActionList));
            return materialLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 发料
     * 此处只能全部发料 不存在发一部分的数量
     *
     * @param materialLot 物料批次
     * @return
     * @throws ClientException
     */
    public MaterialLot issue(MaterialLot materialLot) throws ClientException {
        try {
            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setTransQty(materialLot.getCurrentQty());

            materialLot.setCurrentQty(BigDecimal.ZERO);
            materialLot.setCurrentSubQty(BigDecimal.ZERO);
            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_ISSUE, StringUtils.EMPTY);

            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_ISSUE, materialLotAction);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 退料
     * 此处只能全部发料 不存在发一部分的数量
     *
     * @param materialLot 物料批次
     * @return
     * @throws ClientException
     */
    public MaterialLot returnMLot(MaterialLot materialLot) throws ClientException {
        try {
            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setTransQty(materialLot.getCurrentQty());

            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_RECEIVE, MaterialStatus.STATUS_RECEIVE);
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_RETURN, materialLotAction);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 等待退料
     *
     * @param materialLot
     * @return
     * @throws ClientException
     */
    public MaterialLot waitReturnMLot(MaterialLot materialLot) throws ClientException {
        try {
            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setTransQty(materialLot.getCurrentQty());

            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_WAIT_RETURN, StringUtils.EMPTY);
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_WAIT_RETURN, materialLotAction);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 退料 退回厂家
     * @param materialLot reservedQty退料数量
     * @return
     * @throws ClientException
     */
    public MaterialLot returnMaterialLot(MaterialLot materialLot) throws ClientException {
        try {
            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setTransQty(materialLot.getReservedQty());

            if (materialLot.getCurrentQty().compareTo(materialLot.getReservedQty()) == 0) {
                //全部退料 批次结束
                materialLot.setCurrentQty(BigDecimal.ZERO);
                materialLot.setReservedQty(BigDecimal.ZERO);
                materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_RETURN, MaterialStatus.STATUS_RETURN);
                baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_RETURN, materialLotAction);
            } else {
                //数量改变
                BigDecimal returnQty = materialLot.getReservedQty();
                materialLot.setCurrentQty(materialLot.getCurrentQty().subtract(returnQty));
                materialLot.setReservedQty(BigDecimal.ZERO);
                materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_RETURN, MaterialStatus.STATUS_WAIT);

                materialLotAction.setActionReason(materialLot.getReturnReason());
                baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_RETURN, materialLotAction);
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 报废
     * @param materialLot
     * @return
     * @throws ClientException
     */
    public MaterialLot scrapMLot(MaterialLot materialLot) throws ClientException {
        try {
            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setTransQty(materialLot.getCurrentQty());

            materialLot.setCurrentQty(BigDecimal.ZERO);
            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_SCRAP, StringUtils.EMPTY);

            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_SCRAP, materialLotAction);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据标准数量将物料批次分批
     * @param parentMaterialLotId
     * @param standardQty
     * @return
     * @throws ClientException
     */
    @Override
    public List<MaterialLot> splitStandardMLot(String parentMaterialLotId, BigDecimal standardQty) throws ClientException {
        try {
            List<MaterialLot> waitPrintMaterialLots = Lists.newArrayList();
            List<MaterialLot> subMaterialLots = Lists.newArrayList();
            MaterialLot parentMaterialLot = getMLotByMLotId(parentMaterialLotId, true);
            if (parentMaterialLot.getCurrentQty().compareTo(standardQty) <= 0) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_QTY_CANT_LESS_THEN_ZERO);
            }
            BigDecimal currentQty = parentMaterialLot.getCurrentQty();
            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setTransQty(standardQty);
            while (currentQty.compareTo(standardQty) > 0) {
                MaterialLot subMaterialLot = splitMLot(parentMaterialLotId, materialLotAction);
                subMaterialLots.add(subMaterialLot);
                waitPrintMaterialLots.add(subMaterialLot);
                currentQty = currentQty.subtract(standardQty);
            }
            // 如果没有分完，则需要重新打标签
            if (currentQty.compareTo(BigDecimal.ZERO) > 0) {
                parentMaterialLot = getMLotByMLotId(parentMaterialLotId, true);
                //printMLot(parentMaterialLot);
                waitPrintMaterialLots.add(parentMaterialLot);
            }

            printMLotList(waitPrintMaterialLots);
            return subMaterialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 分批并且打印
     * @param parentMaterialLotId
     * @param materialLotAction
     * @return
     * @throws ClientException
     */
    public MaterialLot splitAndPrintMLot(String parentMaterialLotId, MaterialLotAction materialLotAction) throws ClientException {
        try {
            List<MaterialLot> waitPrintMaterialLots = Lists.newArrayList();

            MaterialLot subMaterialLot = splitMLot(parentMaterialLotId, materialLotAction, MaterialLot.GENERATOR_SUB_MATERIAL_LOT_ID_RULE);
            MaterialLot parentMaterialLot = getMLotByMLotId(parentMaterialLotId);

            waitPrintMaterialLots.add(subMaterialLot);
            waitPrintMaterialLots.add(parentMaterialLot);

            printMLotList(waitPrintMaterialLots);
            return subMaterialLot;
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
            MaterialLot subMaterialLot = splitMLot(parentMaterialLotId, materialLotAction, MaterialLot.GENERATOR_SUB_MATERIAL_LOT_ID_RULE);
            return subMaterialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialLot splitMLot(String parentMaterialLotId, MaterialLotAction materialLotAction, String generatorSubMLotIdRule) throws ClientException {
        try {
            MaterialLot parentMaterialLot = getMLotByMLotId(parentMaterialLotId, true);
            parentMaterialLot.validateMLotHold();
            BigDecimal splitQty = materialLotAction.getTransQty();
            if (parentMaterialLot.getCurrentQty().compareTo(splitQty) <= 0) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_QTY_CANT_LESS_THEN_ZERO);
            }
            parentMaterialLot.setCurrentQty(parentMaterialLot.getCurrentQty().subtract(splitQty));
            parentMaterialLot.setReceiveQty(parentMaterialLot.getReceiveQty().subtract(splitQty));
            if (parentMaterialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                parentMaterialLot.setStatusCategory(MaterialStatusCategory.STATUS_CATEGORY_FIN);
                parentMaterialLot.setStatus(MaterialStatus.STATUS_SPLIT);
            }
            if (parentMaterialLot.getIncomingQty() != null) {
                parentMaterialLot.setIncomingQty(parentMaterialLot.getIncomingQty().subtract(splitQty));
            }

            baseService.saveEntity(parentMaterialLot, MaterialLotHistory.TRANS_TYPE_SPLIT, materialLotAction);

            String subMLotId = generatorSubMLotId(generatorSubMLotIdRule, parentMaterialLot);
            MaterialLot subMaterialLot = (MaterialLot) parentMaterialLot.clone();
            subMaterialLot.setMaterialLotId(subMLotId);
            subMaterialLot.setCurrentQty(splitQty);
            subMaterialLot.setReceiveQty(splitQty);
            if (parentMaterialLot.getIncomingQty() != null) {
                subMaterialLot.setIncomingQty(splitQty);
            }
            subMaterialLot.setParentMaterialLot(parentMaterialLot);
            subMaterialLot = (MaterialLot) baseService.saveEntity(subMaterialLot, MaterialLotHistory.TRANS_TYPE_SPLIT_CREATE, materialLotAction);

            //IQC IQC状态 触发iqc检查
            if (MaterialStatusCategory.STATUS_CATEGORY_IQC.equals(parentMaterialLot.getStatusCategory()) && MaterialStatus.STATUS_IQC.equals(parentMaterialLot.getStatus())) {
                Material material = getMaterialByName(subMaterialLot.getMaterialName(), false);
                if (material.getIqcSheetRrn() != null) {
                    // IQC检查
                    triggerIqc(material, subMaterialLot);
                }
            }

            //Stock In状态 更新库存
            if (MaterialStatusCategory.STATUS_CATEGORY_STOCK.equals(parentMaterialLot.getStatusCategory()) && (MaterialStatus.STATUS_IN.equals(parentMaterialLot.getStatus()))){
                MaterialLotInventory parentMLotInventory = materialLotInventoryRepository.findByMaterialLotId(parentMaterialLotId);
                if (parentMLotInventory != null) {
                    saveMaterialLotInventory(parentMLotInventory, splitQty.negate());

                    MaterialLotInventory subMLotInventory = (MaterialLotInventory) parentMLotInventory.clone();
                    subMLotInventory.setMaterialLot(subMaterialLot);
                    subMLotInventory.setStockQty(BigDecimal.ZERO);
                    saveMaterialLotInventory(subMLotInventory, splitQty);
                }

                //在库分批增强
                applicationContext.publishEvent(new SplitMLotApplicationEvent(this, subMaterialLot));

            }

            //printMLot(subMaterialLot);
            return subMaterialLot;
        }catch (Exception e){
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
        materialLot = stockIn(materialLot, MaterialEvent.EVENT_STOCK_IN, materialLotAction);
        return materialLot;
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
    private Storage getTargetStorageByMaterialLotAction(MaterialLotAction materialLotAction, @NotNull Warehouse warehouse){
        try {
            Storage targetStorage;
            if (materialLotAction.getTargetStorageRrn() != null) {
                targetStorage = storageRepository.findByObjectRrn(materialLotAction.getTargetStorageRrn());
            } else if (!StringUtils.isNullOrEmpty(materialLotAction.getTargetStorageId())) {
                targetStorage = getStorageByWarehouseRrnAndName(warehouse, materialLotAction.getTargetStorageId());
                if (targetStorage == null) {
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
     * @param materialLot       物料批次
     * @param eventId           事件号
     * @param materialLotAction 动作需要包含目标仓库以及数量
     * @return
     */
    private MaterialLot stockIn(MaterialLot materialLot, String eventId, MaterialLotAction materialLotAction) throws ClientException {
        try {
            PreConditionalUtils.checkNotNull(materialLotAction.getTargetWarehouseRrn(), "TargetWarehouseRrn");
            Warehouse targetWarehouse = warehouseRepository.findByObjectRrn(materialLotAction.getTargetWarehouseRrn());
            Storage targetStorage = getTargetStorageByMaterialLotAction(materialLotAction, targetWarehouse);

            //VanChip客制化 HOLD仓库类型的不验证物料是否HoLd
            if (!Warehouse.WAREHOUSE_TYPE_HOLD.equals(targetWarehouse.getWarehouseType())) {
                materialLot.validateMLotHold();
            }
            //验证目标仓库是 物料默认仓库或 hold仓库类型
            validatTargetWarehouse(materialLot.getMaterialLotId(), targetWarehouse);

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

    public MaterialLot getMLotByObjectRrn(String materialLotRrn) throws ClientException {
        return materialLotRepository.findByObjectRrn(materialLotRrn);
    }

    /**
     * 盘点 记录盘点数量,不更新库存
     * @param materialLot
     * @param materialLotAction
     * @return
     */
    public MaterialLotInventory checkMaterialInventory(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException{
        try {
            PreConditionalUtils.checkNotNull(materialLotAction.getFromWarehouseRrn(), StringUtils.EMPTY);

            Warehouse fromWarehouse = warehouseRepository.findByObjectRrn(materialLotAction.getFromWarehouseRrn());
            Storage fromStorage = getFromStorageByMaterialLotAction(materialLotAction, fromWarehouse);
            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(), fromWarehouse.getObjectRrn(), fromStorage.getObjectRrn());
            if (materialLotInventory == null) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_NOT_IN_INVENTORY);
            }

            materialLot.setCheckQty(materialLotAction.getTransQty());
            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_CHECK, StringUtils.EMPTY);

            materialLotAction.setFromWarehouseId(fromWarehouse.getName());
            materialLotAction.setFromStorageId(fromStorage.getName());
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_CHECK, materialLotAction);
            return materialLotInventory;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 盘点 物料批次数量，物料批次库存 以盘点数量为准
     * 支持从有盘无 不支持从无盘有，从无盘有当前必须用入库功能
     * @param materialLot
     * @param materialLotAction
     * @return
     */
    public MaterialLotInventory recheckMaterialInventory(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException{
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
            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_RECHECK, materialLotAction);
            return materialLotInventory;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次出货。扣减物料批次数量
     * @param materialLot       物料批次
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

            //VanChip客制化 物料hold也能转库，故此不验证。
            //materialLot.validateMLotHold();

            Warehouse fromWarehouse = warehouseRepository.findByObjectRrn(materialLotAction.getFromWarehouseRrn());
            Storage fromStorage = getFromStorageByMaterialLotAction(materialLotAction, fromWarehouse);

            Warehouse targetWarehouse = warehouseRepository.findByObjectRrn(materialLotAction.getTargetWarehouseRrn());
            Storage targetStorage = getTargetStorageByMaterialLotAction(materialLotAction, targetWarehouse);

            //VanChip客制化 物料只能从hold类型仓和物料默认仓库之间转换 验证目标仓库是物料默认仓或hold仓库类型
            validatTargetWarehouse(materialLot.getMaterialLotId(), targetWarehouse);

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

            materialLot.setLastWarehouseRrn(targetWarehouse.getObjectRrn());
            materialLot.setLastWarehouseId(targetWarehouse.getName());
            materialLot.setLastStorageRrn(targetStorage.getObjectRrn());
            materialLot.setLastStorageId(targetStorage.getName());
            baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_TRANSFER, materialLotAction);
            return materialLotInventory;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 消耗批次物料
     * 允许传入数量为负数，负数表示反消耗
     * @param materialLot       物料批次
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
     * 可以一个批次同时hold多次，也可以多个批次同时hold
     * @param materialLotActions
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> holdMaterialLot(List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            Map<String, List<MaterialLotAction>> materialLotHoldMap = materialLotActions.stream().collect(Collectors.groupingBy(MaterialLotAction::getMaterialLotId));
            List<MaterialLot> materialLots = Lists.newArrayList();
            for (String materialLotId : materialLotHoldMap.keySet()) {
                MaterialLot materialLot = holdMaterialLot(materialLotId, materialLotHoldMap.get(materialLotId));
                materialLots.add(materialLot);
            }
            return materialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 暂停物料批次
     * 支持多重Hold
     * @param materialLotId      物料批次号
     * @param materialLotActions hold原因
     * @return
     */
    public MaterialLot holdMaterialLot(String materialLotId, List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            MaterialLot materialLot = getMLotByMLotId(materialLotId, true);

            if (MaterialLot.HOLD_STATE_OFF.equals(materialLot.getHoldState())) {
                materialLot.setHoldState(MaterialLot.HOLD_STATE_ON);
                materialLot = materialLotRepository.saveAndFlush(materialLot);
            }

            //Vanchip Reel成品需要增强处理
            if (Material.MATERIAL_CATEGORY_PRODUCT.equals(materialLot.getMaterialCategory()) && StringUtils.NO.equals(materialLot.getInferiorProductsFlag()) && !StringUtils.YES.equals(materialLot.getCategory())){
                applicationContext.publishEvent(new HoldMLotApplicationEvent(this, materialLot, materialLotActions));
                return materialLot;
            }

            for (MaterialLotAction materialLotAction : materialLotActions) {
                MaterialLotHold materialLotHold = new MaterialLotHold();
                materialLotHold.setMaterialLot(materialLot).setAction(materialLotAction);

                String actionPassword = materialLotAction.getActionPassword();
                if (!StringUtils.isNullOrEmpty(actionPassword)) {
                    materialLotHold.setActionPassword(EncryptionUtils.md5Hex(actionPassword));
                }
                saveMaterialLotHold(materialLotHold);

                baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_HOLD, materialLotAction);
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void saveMaterialLotHold(MaterialLotHold materialLotHold) throws ClientException {
        try {
            materialLotHoldRepository.save(materialLotHold);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 批量Release。
     * 可以批量释放批次的HOLD。批量释放的原因是一样的
     * @param materialLotHolds 物料批次的HOLD信息
     * @param releaseLotAction 释放物料批次动作
     * @return
     * @throws ClientException
     */
    public void releaseMaterialLot(List<MaterialLotHold> materialLotHolds, MaterialLotAction releaseLotAction) throws ClientException {
        try {
            Map<String, List<MaterialLotHold>> materialLotHoldMap = materialLotHolds.stream().collect(Collectors.groupingBy(MaterialLotHold::getMaterialLotId));
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
    public void releaseMaterialLot(String materialLotId, List<MaterialLotHold> materialLotHolds, MaterialLotAction releaseLotAction) throws ClientException {
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
     * @param material       原物料/备件
     * @param mLotId 物料批次号
     * @param materialLotAction 操作物料批次的动作包括了操作数量以及原因
     * @return
     */
    public MaterialLot receiveMLot2Warehouse(Material material, String mLotId, MaterialLotAction materialLotAction) throws ClientException {
        try {
            MaterialLot materialLot = receiveMLot(material, mLotId, materialLotAction);
            if (materialLotAction.getTargetWarehouseRrn() == null) {
                materialLotAction.setTargetWarehouseRrn(material.getWarehouseRrn());
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

    public MaterialStatusModel getStatusModelByName(String statusModelName, boolean throwExceptionFlag) throws ClientException {
        try {
            MaterialStatusModel statusModel = materialStatusModelRepository.findOneByName(statusModelName);
            if (statusModel == null && throwExceptionFlag) {
                throw new ClientException(StatusMachineExceptions.STATUS_MODEL_IS_NOT_EXIST);
            }
            return statusModel;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * iqc检验
     * @param materialLotAction
     * @param urlRemark 文件链接
     * @throws ClientException
     */
    public MLotCheckSheet iqc(MaterialLotAction materialLotAction, String urlRemark, List<MLotCheckSheetLine> mLotCheckSheetLines) throws ClientException {
        try {
            MaterialLot materialLot = getMLotByMLotId(materialLotAction.getMaterialLotId(), true);
            String checkResult = materialLotAction.getActionCode();
            List<MaterialLot> waitPrintMaterialLots = Lists.newArrayList();

            //iqc丢料数量
            BigDecimal missingQty = materialLotAction.getTransQty();
            if (missingQty != null){
                MaterialLotAction action = new MaterialLotAction();
                action.setTransQty(missingQty);
                materialLot = missingMLot(materialLot, action);

                waitPrintMaterialLots.add(materialLot);
            }

            //iqc不合格数量
            BigDecimal ngQty = materialLotAction.getReservedQty();
            if (ngQty != null){
                materialLotAction.setTransQty(ngQty);
                MaterialLot ngSubMaterialLot = splitMLot(materialLot.getMaterialLotId(), materialLotAction, MaterialLot.GENERATOR_NG_SUB_MATERIAL_LOT_ID_RULE);
                waitPrintMaterialLots.add(ngSubMaterialLot);
                waitPrintMaterialLots.add(materialLot);//主批两次打印
                waitPrintMaterialLots.add(materialLot);

                ngSubMaterialLot.setIqcQty(ngSubMaterialLot.getCurrentQty());
                materialLotRepository.save(ngSubMaterialLot);

                materialLotAction.setMaterialLotId(ngSubMaterialLot.getMaterialLotId());
                materialLotAction.setActionCode(MaterialStatus.STATUS_NG);
                judgeByCheckSheet(materialLotAction, MaterialEvent.EVENT_IQC, urlRemark, mLotCheckSheetLines);

                holdByQcNG(materialLotAction.getMaterialLotId(), MaterialLotHold.IQC_HOLD);
            }

            materialLot.setIqcQty(materialLot.getCurrentQty());
            materialLotRepository.save(materialLot);

            materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
            materialLotAction.setTransQty(materialLot.getCurrentQty());
            materialLotAction.setActionCode(checkResult);

            MLotCheckSheet mLotCheckSheet = judgeByCheckSheet(materialLotAction, MaterialEvent.EVENT_IQC, urlRemark, mLotCheckSheetLines);

            if (MaterialStatus.STATUS_NG.equals(materialLotAction.getActionCode())) {
                holdByQcNG(materialLotAction.getMaterialLotId(), MaterialLotHold.IQC_HOLD);
            }

            //打印标签
            if (CollectionUtils.isEmpty(waitPrintMaterialLots)) {
                printMLotList(waitPrintMaterialLots);
            }
            return mLotCheckSheet;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 丢料
     * @param materialLot
     */
    public MaterialLot missingMLot(MaterialLot materialLot, MaterialLotAction materialLotAction){
        materialLot.setCurrentQty(materialLot.getCurrentQty().subtract(materialLotAction.getTransQty()));
        materialLot.setMissingQty(materialLotAction.getTransQty());
        return (MaterialLot)baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_MISSING_MLOT, materialLotAction);
    }

    /**
     * oqc
     * @param materialLotAction
     * @return
     * @throws ClientException
     */
    public MLotCheckSheet oqc(MaterialLotAction materialLotAction) throws ClientException {
        try {
            MLotCheckSheet mLotCheckSheet = judgeByCheckSheet(materialLotAction, MaterialEvent.EVENT_OQC, StringUtils.EMPTY, null);

            if (MaterialStatus.STATUS_NG.equals(materialLotAction.getActionCode())) {
                holdByQcNG(materialLotAction.getMaterialLotId(), MaterialLotHold.OQC_HOLD);
            }
            return mLotCheckSheet;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void holdByQcNG(String materialLotId, String qcNgHold) throws ClientException {
        try {
            List<MaterialLotAction> materialLotActions = Lists.newArrayList();
            MaterialLotAction materialLotAction = new MaterialLotAction();

            materialLotAction.setActionCode(qcNgHold);
            materialLotActions.add(materialLotAction);
            holdMaterialLot(materialLotId, materialLotActions);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 判定
     * @param materialLotAction
     * @throws ClientException
     */
    public MLotCheckSheet judgeByCheckSheet(MaterialLotAction materialLotAction, String eventId, String urlRemark, List<MLotCheckSheetLine> mLotCheckSheetLines) throws ClientException {
        try {
            String materialLotId = materialLotAction.getMaterialLotId();
            MaterialLot materialLot = getMLotByMLotId(materialLotId);

            changeMaterialLotState(materialLot, eventId, materialLotAction.getActionCode());

            MLotCheckSheet mLotCheckSheet = mLotCheckSheetRepository.findByMaterialLotIdAndStatus(materialLotId, MLotCheckSheet.STATUS_OPEN);
            mLotCheckSheet.setCheckResult(materialLotAction.getActionCode());

            if (CheckSheet.CATEGORY_OQC.equals(mLotCheckSheet.getSheetCategory())){
                if (MaterialStatus.STATUS_OK.equals(materialLotAction.getActionCode())){
                    mLotCheckSheet.setStatus(MLotCheckSheet.STATUS_CLOSE);
                }
            } else if (CheckSheet.CATEGORY_IQC.equals(mLotCheckSheet.getSheetCategory())){
                //iqc 需审核
                mLotCheckSheet.setStatus(MLotCheckSheet.STATUS_IN_APPROVAL);
            }
            if (!StringUtils.isNullOrEmpty(urlRemark)){
                mLotCheckSheet.setRemark1(urlRemark);
            }

            mLotCheckSheet.setRemark2(materialLotAction.getActionComment());
            mLotCheckSheet = mLotCheckSheetRepository.saveAndFlush(mLotCheckSheet);

            List<MLotCheckSheetLine> checkSheetLines = mLotCheckSheetLineRepository.findByMLotCheckSheetRrn(mLotCheckSheet.getObjectRrn());
            if (CollectionUtils.isEmpty(mLotCheckSheetLines)) {
                //如果详情CheckResult是空,根据批次检查结果来判定小的检查项
                for (MLotCheckSheetLine checkSheetLine:checkSheetLines){
                    if (checkSheetLine.getCheckResult() == null){
                        checkSheetLine.setCheckResult(materialLotAction.getActionCode());
                        mLotCheckSheetLineRepository.saveAndFlush(checkSheetLine);
                    }
                }
            }else {
                for (MLotCheckSheetLine checkSheetLine:checkSheetLines){
                    Optional<MLotCheckSheetLine> mLotCheckSheetLineOptional = mLotCheckSheetLines.stream().filter(mLotCheckSheetLine -> mLotCheckSheetLine.getName().equals(checkSheetLine.getName())).findFirst();
                    if (mLotCheckSheetLineOptional.isPresent() && StringUtils.isNullOrEmpty(checkSheetLine.getCheckResult())) {
                        MLotCheckSheetLine firstMLotCheckSheetLine = mLotCheckSheetLineOptional.get();

                        checkSheetLine.setActionComment(firstMLotCheckSheetLine.getActionComment());
                        checkSheetLine.setSamplingRemark(firstMLotCheckSheetLine.getSamplingRemark());
                        checkSheetLine.setSamplingScheme(firstMLotCheckSheetLine.getSamplingScheme());
                        checkSheetLine.setCheckResult(firstMLotCheckSheetLine.getCheckResult());
                        mLotCheckSheetLineRepository.saveAndFlush(checkSheetLine);
                    }
                }
            }

            materialLotAction.setTransQty(materialLot.getCurrentQty());
            //物料批次记录历史
            if (CheckSheet.CATEGORY_IQC.equals(mLotCheckSheet.getSheetCategory())) {

                baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_IQC, materialLotAction);
            } else if (CheckSheet.CATEGORY_OQC.equals(mLotCheckSheet.getSheetCategory())) {

                baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_OQC, materialLotAction);
            }
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
    public void triggerIqc(Material material, MaterialLot materialLot) throws ClientException {
        try {
            IqcCheckSheet iqcCheckSheet = iqcCheckSheetRepository.findByObjectRrn(material.getIqcSheetRrn());

            MLotCheckSheet mLotCheckSheet = new MLotCheckSheet();
            mLotCheckSheet.setMaterialLotId(materialLot.getMaterialLotId());
            mLotCheckSheet.setSheetName(iqcCheckSheet.getName());
            mLotCheckSheet.setSheetDesc(iqcCheckSheet.getDescription());
            mLotCheckSheet.setSheetCategory(iqcCheckSheet.getCategory());
            mLotCheckSheet.setMaterialName(material.getName());
            mLotCheckSheet.setMaterialDesc(material.getDescription());
            mLotCheckSheet.setReserved1(materialLot.getReserved3());
            mLotCheckSheet = mLotCheckSheetRepository.save(mLotCheckSheet);

            List<CheckSheetLine> checkSheetLines = checkSheetLineRepository.findByCheckSheetRrn(material.getIqcSheetRrn());
            for (CheckSheetLine checkSheetLine : checkSheetLines) {
                MLotCheckSheetLine mLotCheckSheetLine = new MLotCheckSheetLine();
                mLotCheckSheetLine.setMLotCheckSheetRrn(mLotCheckSheet.getObjectRrn());
                mLotCheckSheetLine.setSheetName(iqcCheckSheet.getName());
                mLotCheckSheetLine.setSheetDesc(iqcCheckSheet.getDescription());

                mLotCheckSheetLine.setName(checkSheetLine.getName());
                mLotCheckSheetLine.setDescription(checkSheetLine.getDescription());
                mLotCheckSheetLine.setSamplingScheme(checkSheetLine.getSamplingScheme());
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
            material = getMaterialByName(material.getName(), true);
            for (MaterialLot materialLot : materialLotList) {
                BigDecimal receiveQty = materialLot.getCurrentQty();

                String materialLotId = materialLot.getMaterialLotId();
                materialLot = getMLotByMLotId(materialLotId);
                if (materialLot == null) {
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, materialLotId);
                }
                BigDecimal incomingQty = materialLot.getIncomingQty();
                if (receiveQty.compareTo(incomingQty) > 0) {
                    throw new ClientParameterException(MmsException.MM_RECEIVE_QTY_OVER_INCOMING_QTY, receiveQty);
                }

                String targetStatus = MaterialStatus.STATUS_RECEIVE;
                if (material.getIqcSheetRrn() != null) {
                    targetStatus = MaterialStatus.STATUS_IQC;
                    // IQC检查
                    triggerIqc(material, materialLot);
                }
                materialLot.setReceiveQty(receiveQty);
                materialLot.setCurrentQty(receiveQty);
                materialLot.setReceiveDate(DateUtils.now());
                if (materialLot.getProductionDate() == null){
                    materialLot.setProductionDate(DateUtils.now());
                }
                Date expireDate = calculateTargetDate(materialLot.getProductionDate(), material.getEffectiveLife(), material.getEffectiveUnit());
                materialLot.setExpireDate(expireDate);
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
            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_RECEIVE, MaterialStatus.STATUS_RECEIVE);

            baseService.saveHistoryEntity(materialLot, MaterialLotHistory.TRANS_TYPE_RECEIVE, materialLotAction);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialLot getMLotByMLotId(String mLotId, boolean throwExceptionFlag) throws ClientException {
        try {
            MaterialLot materialLot = materialLotRepository.findByMaterialLotId(mLotId);
            if (materialLot == null && throwExceptionFlag) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, mLotId);
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialLot getMLotByMLotId(String mLotId) throws ClientException {
        try {
            return getMLotByMLotId(mLotId, false);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 创建物料批次
     * @param material 物料或者产品
     * @param mLotId 物料批次号。当为空的时候，按照设定的物料批次号生成规则进行生成
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
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, mLotId);
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
    public String generatorMLotId(Material material) throws ClientException {
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

    public String generatorSubMLotId(String ruleName, MaterialLot materialLot) throws ClientException {
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
    public void validationMergeRule(String ruleName, List<MaterialLot> materialLots) throws ClientException {
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

    public Warehouse getWarehouseByName(String warehouseName, boolean throwExceptionFlag) throws ClientException {
        try {
            Warehouse warehouse = warehouseRepository.findOneByName(warehouseName);
            if (warehouse == null && throwExceptionFlag) {
                throw new ClientParameterException(MmsException.MM_WAREHOUSE_IS_NOT_EXIST, warehouseName);
            }
            return warehouse;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public IqcCheckSheet getIqcSheetByName(String iqcSheetName, boolean throwExceptionFlag) throws ClientException {
        try {
            IqcCheckSheet iqcCheckSheet = iqcCheckSheetRepository.findOneByName(iqcSheetName);
            if (iqcCheckSheet == null && throwExceptionFlag) {
                throw new ClientParameterException(MmsException.MM_IQC_IS_NOT_EXIST, iqcSheetName);
            }
            return iqcCheckSheet;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public List<MaterialLot> getMLotByIncomingDocId (String incomingDocId) throws ClientException {
        return materialLotRepository.findByIncomingDocId(incomingDocId);
    }

    /**
     * 保存成品型号
     * @param product
     * @return
     * @throws ClientException
     */
    public Product saveProduct (Product product) throws ClientException {
        try {
            if (product.getObjectRrn() == null) {
                Product productByName = productRepository.findOneByName(product.getName());
                if (productByName != null) {
                    throw new ClientParameterException(MmsException.MM_PRODUCT_IS_EXIST, product.getName());
                }
                product.setActiveTime(new Date());
                product.setActiveUser(ThreadLocalContext.getUsername());
                product.setStatus(DefaultStatusMachine.STATUS_ACTIVE);
                Long version = versionControlService.getNextVersion(product);
                product.setVersion(version);
                product = (Product) baseService.saveEntity(product, NBVersionControlHis.TRANS_TYPE_CREATE_AND_ACTIVE);
            } else {
                NBVersionControl oldData = productRepository.findByObjectRrn(product.getObjectRrn());
                product.setStatus(oldData.getStatus());
                product = (Product) baseService.saveEntity(product);
            }
            return product;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public Product getProductByName (String name) throws ClientException {
        try {
            return productRepository.findOneByName(name);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public Product saveProduct (Product product, String warehouseName, String iqcSheetName) throws ClientException {
        try {
            if (!StringUtils.isNullOrEmpty(warehouseName)) {
                Warehouse warehouse = getWarehouseByName(warehouseName, true);
                product.setWarehouseRrn(warehouse.getObjectRrn());
            }
            if (!StringUtils.isNullOrEmpty(iqcSheetName)) {
                IqcCheckSheet iqcCheckSheet = getIqcSheetByName(iqcSheetName, true);
                product.setIqcSheetRrn(iqcCheckSheet.getObjectRrn());
            }
            return saveProduct(product);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public Parts saveParts(Parts parts) throws ClientException {
        try {
            if (parts.getObjectRrn() == null) {
                Parts partsMaterial = partsRepository.findOneByName(parts.getName());
                if (partsMaterial != null) {
                    throw new ClientParameterException(MmsException.MM_PARTS_IS_EXIST, parts.getName());
                }
                parts.setActiveTime(new Date());
                parts.setActiveUser(ThreadLocalContext.getUsername());
                parts.setStatus(DefaultStatusMachine.STATUS_ACTIVE);
                Long version = versionControlService.getNextVersion(parts);
                parts.setVersion(version);

                parts = (Parts) baseService.saveEntity(parts, NBVersionControlHis.TRANS_TYPE_CREATE_AND_ACTIVE);
            } else {
                NBVersionControl oldData = partsRepository.findByObjectRrn(parts.getObjectRrn());
                // 不可改变状态
                parts.setStatus(oldData.getStatus());
                parts = (Parts) baseService.saveEntity(parts);
            }
            return parts;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public Parts saveParts(Parts parts, String warehouseName) throws ClientException {
        try {
            if (!StringUtils.isNullOrEmpty(warehouseName)) {
                Warehouse warehouse = getWarehouseByName(warehouseName, true);
                parts.setWarehouseRrn(warehouse.getObjectRrn());
            }
            return saveParts(parts);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public Parts getPartsByName(String name, boolean throwExceptionFlag) throws ClientException {
        try {
            Parts parts = partsRepository.findOneByName(name);
            if (parts == null && throwExceptionFlag){
                throw new ClientParameterException(MmsException.MM_PARTS_IS_NOT_EXIST, name);
            }
            return parts;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * VanChip 客制化
     * 目标仓库只能是 hold仓库类型 或 物料默认仓库
     * @throws ClientException
     */
    public void validatTargetWarehouse (String materialLotId, Warehouse targetWarehouse) throws ClientException {
        try {
            MaterialLot materialLot = getMLotByMLotId(materialLotId, true);
            Material material = materialRepository.findOneByName(materialLot.getMaterialName());
            if (Warehouse.WAREHOUSE_TYPE_HOLD.equals(targetWarehouse.getWarehouseType())) {
                if (!MaterialLot.HOLD_STATE_ON.equals(materialLot.getHoldState())) {
                    throw new ClientParameterException(MmsException.MM_TARGET_WAREHOUSE_IS_NOT_HOLD_WAREHOUSE, targetWarehouse.getName());
                }
                return;
            }else if(Warehouse.WAREHOUSE_TYPE_REJECTS.equals(targetWarehouse.getWarehouseType())){
                if (!StringUtils.YES.equals(materialLot.getInferiorProductsFlag())) {
                    throw new ClientParameterException(MmsException.MM_TARGET_WAREHOUSE_IS_NOT_APPOINT_WAREHOUSE, targetWarehouse.getName());
                }
                return;
            }else if (Warehouse.WAREHOUSE_TYPE_PARTS.equals(targetWarehouse.getWarehouseType())){
                if (!Material.MATERIAL_CATEGORY_PARTS.equals(materialLot.getMaterialCategory())) {
                    throw new ClientParameterException(MmsException.MM_TARGET_WAREHOUSE_IS_NOT_APPOINT_WAREHOUSE, targetWarehouse.getName());
                }
                return;
            }
            //RMA只能入待测品库
            if (!StringUtils.isNullOrEmpty(materialLot.getRmaFlag())){
                if (!Warehouse.WAREHOUSE_TYPE_RMA.equals(targetWarehouse.getWarehouseType())){
                    throw new ClientParameterException(MmsException.MM_TARGET_WAREHOUSE_IS_NOT_APPOINT_WAREHOUSE, targetWarehouse.getName());
                }
                return;
            }
            if ("R".equals(materialLot.getInferiorProductsFlag())){
                if (!Warehouse.WAREHOUSE_TYPE_RA.equals(targetWarehouse.getWarehouseType())){
                    throw new ClientParameterException(MmsException.MM_TARGET_WAREHOUSE_IS_NOT_APPOINT_WAREHOUSE, targetWarehouse.getName());
                }
                return;
            }

            if (!targetWarehouse.getName().equals(material.getWarehouseName())) {
                throw new ClientParameterException(MmsException.MM_TARGET_WAREHOUSE_IS_NOT_DEFAULT_WAREHOUSE, material.getWarehouseName());
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 计算到期日期
     * @param time 时间
     * @param duration 时长 一位小数的double
     * @param timeUnit 时间单位
     * @return
     */
    public Date calculateTargetDate (Date time, Double duration, String timeUnit) throws ClientException{
        try {
            if (time == null || duration == null || timeUnit == null){
                return null;
            }

            timeUnit = timeUnit.replace(StringUtils.LEFT_BRACKETS, StringUtils.EMPTY);
            timeUnit = timeUnit.replace(StringUtils.RIGHT_BRACKETS, StringUtils.EMPTY);
            if (timeUnit.equalsIgnoreCase(ChronoUnit.YEARS.name())){
                duration = duration * 365;
                timeUnit = ChronoUnit.DAYS.name();
            }
            Date targetDate = DateUtils.plus(time, duration.intValue(), timeUnit);

            return targetDate;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获得物料库存数量
     * @param materials
     * @return
     */
    public List<Material> getMaterialStockQty(List<Material> materials) throws ClientException{
        try {
            List<Material> materialList = Lists.newArrayList();
            List<MaterialLotInventory> materialLotInventorys = materialLotInventoryRepository.findAll();
            for (Material material :materials){
                List<MaterialLotInventory> mLotInventorys = materialLotInventorys.stream().filter(materialLotInventory -> materialLotInventory.getMaterialName().equals(material.getName())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(mLotInventorys)){
                    BigDecimal materialStockQty = mLotInventorys.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLotInventory::getStockQty));
                    material.setMaterialStockQty(materialStockQty);

                    materialList.add(material);
                }
            }
            return  materialList;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public Material getMaterialByName(String name, boolean throwExceptionFlag) throws ClientException{
        try {
            Material material = materialRepository.findOneByName(name);
            if (material == null && throwExceptionFlag){
                throw new ClientParameterException(MmsException.MM_MATERIAL_IS_NOT_EXIST, name);
            }
            return material;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void printMLotList(List<MaterialLot> materialLots) throws ClientException{
        try {
            printService.printMLotList(materialLots);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void printMLot(MaterialLot materialLot) throws ClientException{
        try {
            printMLot(materialLot, new MaterialLotAction(), false);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void printMLot(MaterialLot materialLot, MaterialLotAction materialLotAction, Boolean validationPrintFlag) throws ClientException{
        try {
            materialLot = validationPrintAndAddPrintCount(materialLot, materialLotAction, validationPrintFlag);
            printService.printMLot(materialLot);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialLot validationPrintAndAddPrintCount(MaterialLot materialLot, MaterialLotAction materialLotAction, Boolean validationPrintFlag) throws ClientException{
        try {
            if (validationPrintFlag){
                if (materialLot.getPrintCount().compareTo(BigDecimal.ZERO) > 0){
                    throw new ClientException(MmsException.MM_PERMISSION_NOT_ALLOWED);
                }
            }
            materialLot.setPrintCount(materialLot.getPrintCount().add(BigDecimal.ONE));
            baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_PRINT, materialLotAction);
            return materialLot;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

}
