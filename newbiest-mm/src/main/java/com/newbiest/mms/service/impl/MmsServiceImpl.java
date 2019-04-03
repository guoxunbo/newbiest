package com.newbiest.mms.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.model.NBVersionControlHis;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.service.VersionControlService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.PreConditionalUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.commom.sm.exception.StatusMachineExceptions;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.commom.sm.service.StatusMachineService;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.state.model.MaterialEvent;
import com.newbiest.mms.state.model.MaterialStatusModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by guoxunbo on 2019/2/13.
 */
@Service
@Slf4j
@Transactional
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
    MaterialLotHistoryRepository materialLotHistoryRepository;

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

    /**
     * 根据名称获取源物料。
     *  源物料不区分版本。故此处只会有1个
     * @param name 名称
     * @param sc
     * @return
     * @throws ClientException
     */
    public RawMaterial getRawMaterialByName(String name, SessionContext sc) throws ClientException {
        try {
            List<RawMaterial> rawMaterialList = (List<RawMaterial>) rawMaterialRepository.findByNameAndOrgRrn(name, sc.getOrgRrn());
            if (CollectionUtils.isNotEmpty(rawMaterialList)) {
                return rawMaterialList.get(0);
            }
            return null;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存源物料。此处和versionControlService的save不同点在于
     * 1. 保存的时候直接激活，故物料只会是一个版本
     * 2. 激活状态允许修改数据
     * @param rawMaterial
     * @param sc
     * @return
     * @throws ClientException
     */
    public RawMaterial saveRawMaterial(RawMaterial rawMaterial, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            NBHis nbHis = NBHis.getHistoryBean(rawMaterial);

            IRepository modelRepository = baseService.getRepositoryByClassName(rawMaterial.getClass().getName());
            IRepository historyRepository = null;
            if (nbHis != null) {
                historyRepository = baseService.getRepositoryByClassName(nbHis.getClass().getName());
            }

            if (rawMaterial.getObjectRrn() == null) {
                rawMaterial.setOrgRrn(sc.getOrgRrn());
                rawMaterial.setCreatedBy(sc.getUsername());
                rawMaterial.setUpdatedBy(sc.getUsername());
                rawMaterial.setActiveTime(new Date());
                rawMaterial.setActiveUser(sc.getUsername());
                rawMaterial.setStatus(NBVersionControl.STATUS_ACTIVE);
                Long version = versionControlService.getNextVersion(rawMaterial, sc);
                rawMaterial.setVersion(version);

                rawMaterial = (RawMaterial) modelRepository.saveAndFlush(rawMaterial);
                if (nbHis != null) {
                    nbHis.setTransType(NBVersionControlHis.TRANS_TYPE_CREATE_AND_ACTIVE);
                    nbHis.setNbBase(rawMaterial, sc);
                    historyRepository.save(nbHis);
                }
            } else {
                NBVersionControl oldData = (NBVersionControl) modelRepository.findByObjectRrn(rawMaterial.getObjectRrn());
                // 不可改变状态
                rawMaterial.setStatus(oldData.getStatus());
                rawMaterial.setUpdatedBy(sc.getUsername());
                rawMaterial = (RawMaterial) modelRepository.saveAndFlush(rawMaterial);

                if (nbHis != null) {
                    nbHis.setTransType(NBHis.TRANS_TYPE_UPDATE);
                    nbHis.setNbBase(rawMaterial, sc);
                    historyRepository.save(nbHis);
                }
            }
            return rawMaterial;
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
     * @param sc
     * @return
     */
    public MaterialLot receiveMLot2Warehouse(RawMaterial rawMaterial, String mLotId, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException {
        try {
            MaterialLot materialLot = receiveMLot(rawMaterial, mLotId, materialLotAction, sc);

            if (materialLotAction.getTargetWarehouseRrn() == null) {
                materialLotAction.setTargetWarehouseRrn(rawMaterial.getWarehouseRrn());
                materialLot = stockIn(materialLot, materialLotAction, sc);
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次入库并执行StockIn事件 只会修改库存数量 并不会修改物料批次的数量
     * @param materialLot 物料批次
     * @param materialLotAction 动作需要包含目标仓库以及数量
     *
     * @param sc
     * @return
     */
    public MaterialLot stockIn(MaterialLot materialLot, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException {
        return stockIn(materialLot, MaterialEvent.EVENT_STOCK_IN, materialLotAction, sc);
    }

    /**
     * 根据物料号以及仓库 获取物料批次库存
     * 因为当前不考虑storage.故一个批次可以在多个仓库中，但是一个仓库中只能存在一个相同批次号的物料批次
     * @param mLotRrn 物料号
     * @param warehouseRrn 仓库
     */
    public MaterialLotInventory getMaterialLotInv(long mLotRrn, long warehouseRrn) throws ClientException {
        return materialLotInventoryRepository.findByMaterialLotRrnAndWarehouseRrn(mLotRrn, warehouseRrn);
    }

    public List<MaterialLotInventory> getMaterialLotInv(long mLotRrn) throws ClientException {
        return materialLotInventoryRepository.findByMaterialLotRrn(mLotRrn);
    }

    /**
     * 物料批次入库 只会修改库存数量 并不会修改物料批次的数量
     * @param materialLot 物料批次
     * @param eventId 事件号
     * @param materialLotAction 动作需要包含目标仓库以及数量
     *
     * @param sc
     * @return
     */
    public MaterialLot stockIn(MaterialLot materialLot, String eventId, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException {
        try {
            PreConditionalUtils.checkNotNull(materialLotAction.getTargetWarehouseRrn(), StringUtils.EMPTY);
            materialLot.validateMLotHold();

            Warehouse targetWarehouse = (Warehouse) warehouseRepository.findByObjectRrn(materialLotAction.getTargetWarehouseRrn());
            sc.buildTransInfo();
            // 一个批次可以在多个仓库中，但是一个仓库中只能存在一个相同批次号的物料批次
            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(),targetWarehouse.getObjectRrn());
            if (materialLotInventory != null && !materialLotInventory.getWarehouseRrn().equals(materialLotAction.getTargetWarehouseRrn())) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_NOT_SUPPORT_MULTI_INVENTORY);
            }
            // 变更物料库存并改变物料批次状态
            saveMaterialLotInventory(materialLot, targetWarehouse, materialLotAction.getTransQty(), sc);
            changeMaterialLotState(materialLot, eventId, StringUtils.EMPTY, sc);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_STOCK_IN, sc);
            history.setTransQty(materialLotAction.getTransQty());
            history.setTargetWarehouseId(targetWarehouse.getName());
            history.setActionCode(materialLotAction.getActionCode());
            history.setActionReason(materialLotAction.getActionReason());
            history.setActionComment(materialLotAction.getActionComment());
            materialLotHistoryRepository.save(history);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 盘点 物料批次数量，物料批次库存 以盘点数量为准
     *  支持从有盘无 不支持从无盘有，从无盘有当前必须用入库功能
     * @param materialLot
     * @param materialLotAction
     * @param sc
     * @return
     */
    public MaterialLot checkMaterialInventory(MaterialLot materialLot, MaterialLotAction materialLotAction, SessionContext sc) {
        try {
            sc.buildTransInfo();

            PreConditionalUtils.checkNotNull(materialLotAction.getFromWarehouseRrn(), StringUtils.EMPTY);
            materialLot.validateMLotHold();

            Warehouse fromWarehouse = (Warehouse) warehouseRepository.findByObjectRrn(materialLotAction.getFromWarehouseRrn());
            // 一个批次可以在多个仓库中，但是一个仓库中只能存在一个相同批次号的物料批次
            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(), fromWarehouse.getObjectRrn());
            if (materialLotInventory == null) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_NOT_IN_INVENTORY);
            }
            materialLot = (MaterialLot) materialLotRepository.findByObjectRrn(materialLot.getObjectRrn());
            // 如果盘点为0 则表示物料批次被用完，则触发出库事件
            String eventId = MaterialEvent.EVENT_CHECK;
            if (materialLotAction.getTransQty().compareTo(BigDecimal.ZERO) == 0) {
                eventId = MaterialEvent.EVENT_STOCK_OUT;
            }
            materialLot = changeMaterialLotState(materialLot, eventId, StringUtils.EMPTY, sc);
            // 盘点的时候 物料批次数量，物料批次库存 以盘点数量为准
            materialLotInventory.setStockQty(materialLotAction.getTransQty());
            if (materialLotInventory.getStockQty().compareTo(BigDecimal.ZERO) == 0) {
                materialLotInventoryRepository.delete(materialLotInventory);
            } else {
                materialLotInventoryRepository.save(materialLotInventory);
            }

            materialLot.setCurrentQty(materialLotAction.getTransQty());
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_CHECK, sc);
            history.setTransQty(materialLotAction.getTransQty());
            history.setTransWarehouseId(fromWarehouse.getName());
            history.setActionCode(materialLotAction.getActionCode());
            history.setActionReason(materialLotAction.getActionReason());
            history.setActionComment(materialLotAction.getActionComment());
            materialLotHistoryRepository.save(history);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次出库。扣减物料库存数量以及物料的当前数量
     * @param materialLot 物料批次
     * @param materialLotAction 动作需要包含来源仓库以及数量
     * @param sc
     * @return
     * @throws ClientException
     */
    public MaterialLot stockOut(MaterialLot materialLot, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();

            PreConditionalUtils.checkNotNull(materialLotAction.getFromWarehouseRrn(), StringUtils.EMPTY);
            materialLot.validateMLotHold();

            Warehouse fromWarehouse = (Warehouse) warehouseRepository.findByObjectRrn(materialLotAction.getFromWarehouseRrn());
            // 一个批次可以在多个仓库中，但是一个仓库中只能存在一个相同批次号的物料批次
            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(), fromWarehouse.getObjectRrn());
            if (materialLotInventory == null) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_NOT_IN_INVENTORY);
            }
            materialLot = (MaterialLot) materialLotRepository.findByObjectRrn(materialLot.getObjectRrn());

            if (materialLot.getCurrentQty().compareTo(materialLotAction.getTransQty()) != 0) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_MUST_STOCK_OUT_ALL);
            }
            // 变更物料库存并改变物料批次状态
            saveMaterialLotInventory(materialLot, fromWarehouse, materialLotAction.getTransQty().negate(), sc);
            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_STOCK_OUT, StringUtils.EMPTY, sc);

            //修改批次数量
            materialLot.setCurrentQty(materialLot.getCurrentQty().subtract(materialLotAction.getTransQty()));
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_STOCK_OUT, sc);
            history.setTransQty(materialLotAction.getTransQty());
            history.setTransWarehouseId(fromWarehouse.getName());
            history.setActionCode(materialLotAction.getActionCode());
            history.setActionReason(materialLotAction.getActionReason());
            history.setActionComment(materialLotAction.getActionComment());
            materialLotHistoryRepository.save(history);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 领料出库 和出库的不同就在于不会扣减物料的当前数量只会扣减库存
     * @param materialLot 物料批次
     * @param materialLotAction 动作需要包含来源仓库以及数量
     * @param sc
     * @return
     * @throws ClientException
     */
    public MaterialLot pick(MaterialLot materialLot, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            PreConditionalUtils.checkNotNull(materialLotAction.getFromWarehouseRrn(), StringUtils.EMPTY);
            materialLot.validateMLotHold();

            Warehouse fromWarehouse = (Warehouse) warehouseRepository.findByObjectRrn(materialLotAction.getFromWarehouseRrn());
            // 一个批次可以在多个仓库中，但是一个仓库中只能存在一个相同批次号的物料批次
            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(), fromWarehouse.getObjectRrn());
            if (materialLotInventory == null) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_NOT_IN_INVENTORY);
            }
            materialLot = (MaterialLot) materialLotRepository.findByObjectRrn(materialLot.getObjectRrn());

            if (materialLot.getCurrentQty().compareTo(materialLotAction.getTransQty()) != 0) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_MUST_PICK_ALL);
            }
            // 变更物料库存并改变物料批次状态
            saveMaterialLotInventory(materialLot, fromWarehouse, materialLotAction.getTransQty().negate(), sc);
            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_PICK, StringUtils.EMPTY, sc);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_PICK, sc);
            history.setTransQty(materialLotAction.getTransQty());
            history.setTransWarehouseId(fromWarehouse.getName());
            history.setActionCode(materialLotAction.getActionCode());
            history.setActionReason(materialLotAction.getActionReason());
            history.setActionComment(materialLotAction.getActionComment());
            materialLotHistoryRepository.save(history);
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
     * @param sc
     * @return
     * @throws ClientException
     */
    public MaterialLot transfer(MaterialLot materialLot, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            PreConditionalUtils.checkNotNull(materialLotAction.getFromWarehouseRrn(), StringUtils.EMPTY);
            PreConditionalUtils.checkNotNull(materialLotAction.getTargetWarehouseRrn(), StringUtils.EMPTY);

            materialLot.validateMLotHold();

            //当前没考虑库位，则同一个物料批次在转库的时候无法转移到自己本身
            if (materialLotAction.getFromWarehouseRrn() == materialLotAction.getTargetWarehouseRrn()) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_TRANSFER_MUST_DIFFERENT_WAREHOUSE);
            }

            Warehouse fromWarehouse = (Warehouse) warehouseRepository.findByObjectRrn(materialLotAction.getFromWarehouseRrn());
            Warehouse targetWarehouse = (Warehouse) warehouseRepository.findByObjectRrn(materialLotAction.getTargetWarehouseRrn());

            // 一个批次可以在多个仓库中，但是一个仓库中只能存在一个相同批次号的物料批次
            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(), fromWarehouse.getObjectRrn());
            if (materialLotInventory == null) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_NOT_IN_INVENTORY);
            }
            materialLot = (MaterialLot) materialLotRepository.findByObjectRrn(materialLot.getObjectRrn());

            if (materialLot.getCurrentQty().compareTo(materialLotAction.getTransQty()) != 0) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_MUST_TRANSFER_ALL);
            }
            materialLotInventory.setWarehouse(targetWarehouse);
            materialLotInventoryRepository.save(materialLotInventory);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_TRANSFER, sc);
            history.setTransQty(materialLotAction.getTransQty());
            history.setTransWarehouseId(fromWarehouse.getName());
            history.setTargetWarehouseId(targetWarehouse.getName());
            history.setActionCode(materialLotAction.getActionCode());
            history.setActionReason(materialLotAction.getActionReason());
            history.setActionComment(materialLotAction.getActionComment());
            materialLotHistoryRepository.save(history);
            return materialLot;
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
    public MaterialLot consumeMLot(MaterialLot materialLot, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();

            materialLot = (MaterialLot) materialLotRepository.findByObjectRrn(materialLot.getObjectRrn());

            BigDecimal currentQty = materialLot.getCurrentQty().subtract(materialLotAction.getTransQty());
            if (currentQty.compareTo(materialLotAction.getTransQty()) < 0) {
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
            materialLot = changeMaterialLotState(materialLot, eventId, StringUtils.EMPTY, sc);

            materialLot.setCurrentQty(currentQty);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_CONSUME, sc);
            history.setTransQty(materialLotAction.getTransQty());
            history.setActionCode(materialLotAction.getActionCode());
            history.setActionReason(materialLotAction.getActionReason());
            history.setActionComment(materialLotAction.getActionComment());
            materialLotHistoryRepository.save(history);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 暂停物料批次
     * @param materialLot
     * @param materialLotAction
     * @return
     */
    public MaterialLot holdMaterialLot(MaterialLot materialLot, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException{
        try {
            sc.buildTransInfo();
            materialLot = (MaterialLot) materialLotRepository.findByObjectRrn(materialLot.getObjectRrn());
            materialLot.validateMLotHold();

            // 物料批次只会hold一次。多重Hold只会记录历史，并不会产生多重Hold记录
            materialLot.setHoldState(MaterialLot.HOLD_STATE_ON);
            materialLot.setUpdatedBy(sc.getUsername());
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_HOLD, sc);
            history.setTransQty(materialLot.getCurrentQty());
            history.setActionCode(materialLotAction.getActionCode());
            history.setActionReason(materialLotAction.getActionReason());
            history.setActionComment(materialLotAction.getActionComment());
            materialLotHistoryRepository.save(history);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 释放物料批次
     * @param materialLot
     * @param materialLotAction
     * @return
     */
    public MaterialLot releaseMaterialLot(MaterialLot materialLot, MaterialLotAction materialLotAction, SessionContext sc) throws ClientException{
        try {
            sc.buildTransInfo();
            materialLot = (MaterialLot) materialLotRepository.findByObjectRrn(materialLot.getObjectRrn());
            materialLot.setHoldState(MaterialLot.HOLD_STATE_OFF);
            materialLot.setUpdatedBy(sc.getUsername());
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_RELEASE, sc);
            history.setTransQty(materialLot.getCurrentQty());
            history.setActionCode(materialLotAction.getActionCode());
            history.setActionReason(materialLotAction.getActionReason());
            history.setActionComment(materialLotAction.getActionComment());
            materialLotHistoryRepository.save(history);
            return materialLot;
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
    private void saveMaterialLotInventory(MaterialLot materialLot, Warehouse warehouse, BigDecimal transQty, SessionContext sc) throws ClientException {
        try {
            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(), warehouse.getObjectRrn());
            if (materialLotInventory == null) {
                materialLotInventory = new MaterialLotInventory();
                materialLotInventory.setMaterialLot(materialLot).setWarehouse(warehouse);
                //TODO storage相关
                materialLotInventory.setOrgRrn(sc.getOrgRrn());
                materialLotInventory.setCreatedBy(sc.getUsername());
                materialLotInventory.setUpdatedBy(sc.getUsername());
            }
            materialLotInventory.setStockQty(materialLotInventory.getStockQty().add(transQty));
            if (materialLotInventory.getStockQty().compareTo(BigDecimal.ZERO) < 0) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_STOCK_QTY_CANOT_LESS_THEN_ZERO);
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
     * 接收物料批次
     * @param rawMaterial 原物料
     * @param mLotId 物料批次号
     * @param materialLotAction 操作物料批次的动作包括了操作数量以及原因
     * @param sc
     * @return
     */
    public MaterialLot receiveMLot(RawMaterial rawMaterial, String mLotId, MaterialLotAction materialLotAction, SessionContext sc) {
        try {
            MaterialLot materialLot = createMLot(rawMaterial, mLotId, materialLotAction.getGrade(), materialLotAction.getTransQty(), sc);
            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_RECEIVE, StringUtils.EMPTY, sc);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_RECEIVE, sc);
            history.setTransQty(materialLot.getCurrentQty());
            history.setActionCode(materialLotAction.getActionCode());
            history.setActionReason(materialLotAction.getActionReason());
            history.setActionComment(materialLotAction.getActionComment());
            materialLotHistoryRepository.save(history);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收物料批次并入库
     * 如果没有指定入的仓库，则直接入到物料上绑定的仓库
     * @param rawMaterial
     * @param sc
     * @return
     */
    public MaterialLot receiveMLot2Warehouse(RawMaterial rawMaterial, MaterialLotAction lotAction, SessionContext sc) {
        try {
            return receiveMLot2Warehouse(rawMaterial, StringUtils.EMPTY, lotAction, sc);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialLot getMLotByMLotId(String mLotId, SessionContext sc) throws ClientException{
        try {
            return materialLotRepository.findByMaterialLotIdAndOrgRrn(mLotId, sc.getOrgRrn());
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 创建物料批次
     * @param rawMaterial 源物料
     * @param  mLotId 物料批次号。当为空的时候，按照设定的物料批次号生成规则进行生成
     * @return
     * @throws ClientException
     */
    public MaterialLot createMLot(RawMaterial rawMaterial, String mLotId, String grade, BigDecimal transQty, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            if (StringUtils.isNullOrEmpty(mLotId)) {
                mLotId = generatorMLotId(rawMaterial, sc);
            }
            MaterialLot materialLot = getMLotByMLotId(mLotId, sc);
            if (materialLot != null) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_IS_EXIST);
            }
            materialLot = new MaterialLot();
            materialLot.setOrgRrn(sc.getOrgRrn());
            materialLot.setCreatedBy(sc.getUsername());
            materialLot.setUpdatedBy(sc.getUsername());
            materialLot.setMaterialLotId(mLotId);
            materialLot.setGrade(grade);
            if (rawMaterial.getStatusModelRrn() == null) {
                List<MaterialStatusModel> statusModels = (List<MaterialStatusModel>) materialStatusModelRepository.findByNameAndOrgRrn(Material.DEFAULT_STATUS_MODEL, sc.getOrgRrn());
                if (CollectionUtils.isNotEmpty(statusModels)) {
                    rawMaterial.setStatusModelRrn(statusModels.get(0).getObjectRrn());
                } else {
                    throw new ClientException(StatusMachineExceptions.STATUS_MODEL_IS_NOT_EXIST);
                }
            }
            materialLot.setStatusModelRrn(rawMaterial.getStatusModelRrn());

            StatusModel statusModel = statusMachineService.getStatusModelByObjectRrn(rawMaterial.getStatusModelRrn());
            materialLot.setStatusCategory(statusModel.getInitialStateCategory());
            materialLot.setStatus(statusModel.getInitialState());

            materialLot.setReceiveQty(transQty);
            materialLot.setReceiveDate(new Date());
            materialLot.setCurrentQty(transQty);

            materialLot.setMaterialRrn(rawMaterial.getObjectRrn());
            materialLot.setMaterialName(rawMaterial.getName());
            materialLot.setMaterialDesc(rawMaterial.getDescription());
            materialLot.setMaterialVersion(rawMaterial.getVersion());
            materialLot.setMaterialCategory(rawMaterial.getMaterialCategory());
            materialLot.setMaterialType(rawMaterial.getMaterialType());
            materialLot.setStoreUom(rawMaterial.getStoreUom());
            materialLot.setEffectiveLife(rawMaterial.getEffectiveLife());
            materialLot.setEffectiveUnit(rawMaterial.getEffectiveUnit());
            materialLot.setWarningLife(rawMaterial.getWarningLife());

            materialLot = materialLotRepository.saveAndFlush(materialLot);

            // 记录历史
            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE, sc);
            history.setTransQty(materialLot.getCurrentQty());
            materialLotHistoryRepository.save(history);
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
    public String generatorMLotId(RawMaterial rawMaterial, SessionContext sc) throws ClientException{
        try {
            GeneratorContext generatorContext = new GeneratorContext();
            generatorContext.setRuleName(MaterialLot.GENERATOR_MATERIAL_LOT_ID_RULE);
            String id = generatorService.generatorId(sc.getOrgRrn(), generatorContext);
            return id;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据StateMachine修改物料批次的状态
     * @param mLot 物料批次
     * @param eventId 触发的事件 不可为空
     * @param targetStatus 目标状态。因为一个事件可能有多个目标状态可强行指定转到具体的状态。不指定则以event上优先级最高的来当状态
     * @param sc
     * @return
     * @throws ClientException
     */
    public MaterialLot changeMaterialLotState(MaterialLot mLot, String eventId, String targetStatus, SessionContext sc) throws ClientException {
        try {
            mLot = (MaterialLot) statusMachineService.triggerEvent(mLot, eventId, targetStatus, sc);
            mLot = materialLotRepository.saveAndFlush(mLot);
            return mLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


}
