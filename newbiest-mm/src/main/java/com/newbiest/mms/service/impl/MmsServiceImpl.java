package com.newbiest.mms.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.model.NBVersionControlHis;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.service.VersionControlService;
import com.newbiest.base.ui.repository.TableRepository;
import com.newbiest.base.utils.*;
import com.newbiest.commom.sm.exception.StatusMachineExceptions;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.commom.sm.service.StatusMachineService;
import com.newbiest.common.exception.ContextException;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.context.model.MergeRuleContext;
import com.newbiest.mms.SystemPropertyUtils;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.MaterialLotUnitService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.state.model.MaterialEvent;
import com.newbiest.mms.state.model.MaterialStatusModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2019/2/13.
 */
@Service
@Slf4j
@Transactional
public class MmsServiceImpl implements MmsService {

    public static final String USER_SCM = "SCM";

    @Autowired
    BaseService baseService;

    @Autowired
    VersionControlService versionControlService;

    @Autowired
    RawMaterialRepository rawMaterialRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialLotHistoryRepository materialLotHistoryRepository;

    @Autowired
    MaterialStatusModelRepository materialStatusModelRepository;

    @Autowired
    MaterialLotInventoryRepository materialLotInventoryRepository;

    @Autowired
    MaterialLotUnitRepository materialLotUnitRepository;

    @Autowired
    MaterialLotUnitHisRepository materialLotUnitHisRepository;

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
    MaterialLotUnitService materialLotUnitService;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PartsRepository partsRepository;

    @Autowired
    MaterialNameInfoRepository materialNameInfoRepository;

    @Autowired
    MaterialLotHoldInfoRepository materialLotHoldInfoRepository;

    @Autowired
    WaferHoldRelationRepository waferHoldRelationRepository;

    @Autowired
    WaferHoldRelationHisRepository waferHoldRelationHisRepository;

    @Autowired
    FutureHoldConfigRepository futureHoldConfigRepository;

    @Autowired
    FutureHoldConfigHisRepository futureHoldConfigHisRepository;

    /**
     * 根据名称获取源物料。
     *  源物料不区分版本。故此处只会有1个
     * @param name 名称
     * @return
     * @throws ClientException
     */
    public RawMaterial getRawMaterialByName(String name) throws ClientException {
        try {
            List<RawMaterial> rawMaterialList = rawMaterialRepository.findByNameAndOrgRrn(name, ThreadLocalContext.getOrgRrn());
            if (CollectionUtils.isNotEmpty(rawMaterialList)) {
                return rawMaterialList.get(0);
            }
            return null;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据名称产品信息
     * @param name 名称
     * @return
     * @throws ClientException
     */
    public Product getProductByName(String name) throws ClientException {
        try {
            List<Product> productList = productRepository.findByNameAndOrgRrn(name, ThreadLocalContext.getOrgRrn());
            if (CollectionUtils.isNotEmpty(productList)) {
                return productList.get(0);
            }
            return null;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据名称查询备件信息
     * @param name 名称
     * @return
     * @throws ClientException
     */
    public Parts getPartsByName(String name) throws ClientException {
        try {
            List<Parts> partsList = partsRepository.findByNameAndOrgRrn(name, ThreadLocalContext.getOrgRrn());
            if (CollectionUtils.isNotEmpty(partsList)) {
                return partsList.get(0);
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
     * @return
     * @throws ClientException
     */
    public RawMaterial saveRawMaterial(RawMaterial rawMaterial) throws ClientException {
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            NBHis nbHis = NBHis.getHistoryBean(rawMaterial);

            IRepository modelRepository = baseService.getRepositoryByClassName(rawMaterial.getClass().getName());
            IRepository historyRepository = null;
            if (nbHis != null) {
                historyRepository = baseService.getRepositoryByClassName(nbHis.getClass().getName());
            }

            if (rawMaterial.getObjectRrn() == null) {
                rawMaterial.setActiveTime(new Date());
                rawMaterial.setActiveUser(sc.getUsername());
                rawMaterial.setStatus(NBVersionControl.STATUS_ACTIVE);
                Long version = versionControlService.getNextVersion(rawMaterial);
                rawMaterial.setVersion(version);

                rawMaterial = (RawMaterial) modelRepository.saveAndFlush(rawMaterial);
                if (nbHis != null) {
                    nbHis.setTransType(NBVersionControlHis.TRANS_TYPE_CREATE_AND_ACTIVE);
                    nbHis.setNbBase(rawMaterial);
                    historyRepository.save(nbHis);
                }
                if(Material.TYPE_WAFER.equals(rawMaterial.getMaterialCategory())){
                    saveMaterialName(rawMaterial.getName());
                }
            } else {
                NBVersionControl oldData = (NBVersionControl) modelRepository.findByObjectRrn(rawMaterial.getObjectRrn());
                // 不可改变状态
                rawMaterial.setStatus(oldData.getStatus());
                rawMaterial = (RawMaterial) modelRepository.saveAndFlush(rawMaterial);

                if (nbHis != null) {
                    nbHis.setTransType(NBHis.TRANS_TYPE_UPDATE);
                    nbHis.setNbBase(rawMaterial);
                    historyRepository.save(nbHis);
                }
            }
            return rawMaterial;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public Material createRawMaterial(RawMaterial rawMaterial) throws ClientException{
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            if(StringUtils.isNullOrEmpty(rawMaterial.getMaterialCategory())){
                rawMaterial.setMaterialCategory(Material.TYPE_WAFER);
            }
            if(StringUtils.isNullOrEmpty(rawMaterial.getMaterialType())){
                rawMaterial.setMaterialType(Material.TYPE_WAFER);
            }
            rawMaterial = this.saveRawMaterial(rawMaterial);

            List<MaterialStatusModel> statusModels = materialStatusModelRepository.findByNameAndOrgRrn(Material.DEFAULT_STATUS_MODEL, sc.getOrgRrn());
            if (CollectionUtils.isNotEmpty(statusModels)) {
                rawMaterial.setStatusModelRrn(statusModels.get(0).getObjectRrn());
            } else {
                throw new ClientException(StatusMachineExceptions.STATUS_MODEL_IS_NOT_EXIST);
            }
            Material material = rawMaterialRepository.save(rawMaterial);
            if(Material.TYPE_WAFER.equals(rawMaterial.getMaterialCategory())){
                saveMaterialName(rawMaterial.getName());
            }
            return material;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存产品信息
     * @param product
     * @return
     * @throws ClientException
     */
    public Product saveProduct(Product product) throws ClientException {
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            NBHis nbHis = NBHis.getHistoryBean(product);

            IRepository modelRepository = baseService.getRepositoryByClassName(product.getClass().getName());
            IRepository historyRepository = null;
            if (nbHis != null) {
                historyRepository = baseService.getRepositoryByClassName(nbHis.getClass().getName());
            }

            if (product.getObjectRrn() == null) {
                product.setActiveTime(new Date());
                product.setActiveUser(sc.getUsername());
                product.setStatus(NBVersionControl.STATUS_ACTIVE);
                Long version = versionControlService.getNextVersion(product);
                product.setVersion(version);

                product = (Product) modelRepository.saveAndFlush(product);
                if (nbHis != null) {
                    nbHis.setTransType(NBVersionControlHis.TRANS_TYPE_CREATE_AND_ACTIVE);
                    nbHis.setNbBase(product);
                    historyRepository.save(nbHis);
                }
                saveMaterialName(product.getName());
            } else {
                NBVersionControl oldData = (NBVersionControl) modelRepository.findByObjectRrn(product.getObjectRrn());
                // 不可改变状态
                product.setStatus(oldData.getStatus());
                product = (Product) modelRepository.saveAndFlush(product);

                if (nbHis != null) {
                    nbHis.setTransType(NBHis.TRANS_TYPE_UPDATE);
                    nbHis.setNbBase(product);
                    historyRepository.save(nbHis);
                }
            }
            return product;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存备件信息
     * @param parts
     * @return
     * @throws ClientException
     */
    public Parts saveParts(Parts parts) throws ClientException {
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            NBHis nbHis = NBHis.getHistoryBean(parts);

            IRepository modelRepository = baseService.getRepositoryByClassName(parts.getClass().getName());
            IRepository historyRepository = null;
            if (nbHis != null) {
                historyRepository = baseService.getRepositoryByClassName(nbHis.getClass().getName());
            }

            if (parts.getObjectRrn() == null) {
                parts.setActiveTime(new Date());
                parts.setActiveUser(sc.getUsername());
                parts.setStatus(NBVersionControl.STATUS_ACTIVE);
                Long version = versionControlService.getNextVersion(parts);
                parts.setVersion(version);

                parts = (Parts) modelRepository.saveAndFlush(parts);
                if (nbHis != null) {
                    nbHis.setTransType(NBVersionControlHis.TRANS_TYPE_CREATE_AND_ACTIVE);
                    nbHis.setNbBase(parts);
                    historyRepository.save(nbHis);
                }
            } else {
                NBVersionControl oldData = (NBVersionControl) modelRepository.findByObjectRrn(parts.getObjectRrn());
                // 不可改变状态
                parts.setStatus(oldData.getStatus());
                parts = (Parts) modelRepository.saveAndFlush(parts);

                if (nbHis != null) {
                    nbHis.setTransType(NBHis.TRANS_TYPE_UPDATE);
                    nbHis.setNbBase(parts);
                    historyRepository.save(nbHis);
                }
            }
            return parts;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialNameInfo saveMaterialName(String materialName) throws ClientException{
        try {
            MaterialNameInfo materialNameInfo = new MaterialNameInfo();
            List<MaterialNameInfo> materialNameInfoList = materialNameInfoRepository.findByNameAndOrgRrn(materialName, ThreadLocalContext.getOrgRrn());
            if(CollectionUtils.isEmpty(materialNameInfoList)){
                materialNameInfo.setName(materialName);
                materialNameInfo = materialNameInfoRepository.saveAndFlush(materialNameInfo);
            }
            return materialNameInfo;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * 批量创建批次
     * @param rawMaterial
     * @param materialLotImportActions
     * @return
     */
    public List<MaterialLot> createMaterialLotList(RawMaterial rawMaterial, List<MaterialLotAction> materialLotImportActions) throws ClientException {
        try {
            List<MaterialLot> materialLots = Lists.newArrayList();
            StatusModel statusModel = getMaterialStatusModel(rawMaterial);
            for (MaterialLotAction materialLotImportAction : materialLotImportActions) {
                materialLotImportAction.setTransCount(BigDecimal.ZERO);
                MaterialLot materialLot = createMLot(rawMaterial, statusModel, materialLotImportAction);
                materialLots.add(materialLot);
            }
            return materialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收物料批次并入库指定物料批次号
     * 如果没有指定入的仓库，则直接入到物料上绑定的仓库
     * @param  material 物料号
     * @param materialLotActions 操作物料批次的动作包括了操作数量以及原因
     * @return
     */
    public List<MaterialLot> receiveMLotList2Warehouse(Material material, List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            List<MaterialLot> materialLots = Lists.newArrayList();
            Map<String, MaterialLotAction> materialLotActionMap = materialLotActions.stream().collect(Collectors.toMap(MaterialLotAction :: getMaterialLotId, Function.identity()));
            List<MaterialLot> materialLotList = receiveMLots(material, materialLotActions);

            for (MaterialLot materialLot : materialLotList) {
                MaterialLotAction materialLotAction = materialLotActionMap.get(materialLot.getMaterialLotId());
                if (materialLotAction.getTargetWarehouseRrn() == null) {
                    materialLotAction.setTargetWarehouseRrn(material.getWarehouseRrn());
                }
                materialLot = stockIn(materialLot, materialLotAction);
                materialLots.add(materialLot);
            }
            return materialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收物料批次并入库指定物料批次号
     * 如果没有指定入的仓库，则直接入到物料上绑定的仓库
     * @param material 原物料/产品号
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
            materialLot = stockIn(materialLot, materialLotAction);
            return materialLot;
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
    public MaterialLotInventory getMaterialLotInv(long mLotRrn, long warehouseRrn, long storageRrn) throws ClientException {
        return materialLotInventoryRepository.findByMaterialLotRrnAndWarehouseRrnAndStorageRrn(mLotRrn, warehouseRrn, storageRrn);
    }

    /**
     * 根据物料批次号获取物料库存
     * @param mLotRrn
     * @return
     * @throws ClientException
     */
    public List<MaterialLotInventory> getMaterialLotInv(long mLotRrn) throws ClientException {
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
                targetStorage = (Storage) storageRepository.findByObjectRrn(materialLotAction.getTargetStorageRrn());
            } else if (!StringUtils.isNullOrEmpty(materialLotAction.getTargetStorageId())) {
                targetStorage = getStorageByWarehouseRrnAndName(warehouse, materialLotAction.getTargetStorageId());
                if (targetStorage == null ) {
                    if (SystemPropertyUtils.getAutoCreateStorageFlag()) {
                        targetStorage = new Storage();
                        targetStorage.setName(materialLotAction.getTargetStorageId());
                        targetStorage.setDescription(StringUtils.SYSTEM_CREATE);
                        targetStorage.setWarehouseRrn(warehouse.getObjectRrn());
                        targetStorage = storageRepository.saveAndFlush(targetStorage);
                    } else {
                        throw new ClientParameterException(MmsException.MM_STORAGE_IS_NOT_EXIST, materialLotAction.getTargetStorageId());

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
                targetStorage = (Storage) storageRepository.findByObjectRrn(materialLotAction.getFromStorageRrn());
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
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();

            PreConditionalUtils.checkNotNull(materialLotAction.getTargetWarehouseRrn(), "TargetWarehouseRrn");
            Warehouse targetWarehouse = (Warehouse) warehouseRepository.findByObjectRrn(materialLotAction.getTargetWarehouseRrn());
            Storage targetStorage = getTargetStorageByMaterialLotAction(materialLotAction, targetWarehouse);

            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(), targetWarehouse.getObjectRrn(), targetStorage.getObjectRrn());
            if (materialLotInventory != null) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_NOT_SUPPORT_MULTI_INVENTORY, materialLot.getMaterialLotId());
            }
            materialLot.setCurrentSubQty(materialLotAction.getTransCount());
            // 变更物料库存并改变物料批次状态
            saveMaterialLotInventory(materialLot, targetWarehouse, targetStorage, materialLotAction.getTransQty());

            changeMaterialLotState(materialLot, eventId, StringUtils.EMPTY);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_STOCK_IN);
            history.buildByMaterialLotAction(materialLotAction);
            if(!StringUtils.isNullOrEmpty(materialLotAction.getWorkOrderId())){
                history.setWorkOrderId(materialLotAction.getWorkOrderId());
            }
            history.setTargetWarehouseId(targetWarehouse.getName());
            history.setTargetStorageId(targetStorage.getName());
            materialLotHistoryRepository.save(history);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialLot getMLotByObjectRrn(long materialLotRrn) throws ClientException{
        return (MaterialLot) materialLotRepository.findByObjectRrn(materialLotRrn);
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
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();

            PreConditionalUtils.checkNotNull(materialLotAction.getFromWarehouseRrn(), StringUtils.EMPTY);
            materialLot.validateMLotHold();

            Warehouse fromWarehouse = (Warehouse) warehouseRepository.findByObjectRrn(materialLotAction.getFromWarehouseRrn());
            Storage fromStorage = getFromStorageByMaterialLotAction(materialLotAction, fromWarehouse);
            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(), fromWarehouse.getObjectRrn(), fromStorage.getObjectRrn());
            if (materialLotInventory == null) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_NOT_IN_INVENTORY, materialLot.getMaterialLotId());
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

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_CHECK);
            history.buildByMaterialLotAction(materialLotAction);
            history.setTransWarehouseId(fromWarehouse.getName());
            history.setTransStorageId(fromStorage.getName());
            materialLotHistoryRepository.save(history);
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
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();

            PreConditionalUtils.checkNotNull(materialLotAction.getFromWarehouseRrn(), StringUtils.EMPTY);
            materialLot.validateMLotHold();

            Warehouse fromWarehouse = (Warehouse) warehouseRepository.findByObjectRrn(materialLotAction.getFromWarehouseRrn());
            Storage fromStorage = getFromStorageByMaterialLotAction(materialLotAction, fromWarehouse);
            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(), fromWarehouse.getObjectRrn(), fromStorage.getObjectRrn());
            if (materialLotInventory == null) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_NOT_IN_INVENTORY, materialLot.getMaterialLotId());
            }
            materialLot = getMLotByObjectRrn(materialLot.getObjectRrn());

            if (materialLot.getCurrentQty().compareTo(materialLotAction.getTransQty()) != 0) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_MUST_STOCK_OUT_ALL, materialLot.getMaterialLotId());
            }
            // 变更物料库存并改变物料批次状态
            saveMaterialLotInventory(materialLot, fromWarehouse, fromStorage, materialLotAction.getTransQty().negate());
            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_STOCK_OUT, StringUtils.EMPTY);

            //修改批次数量
            materialLot.setCurrentQty(materialLot.getCurrentQty().subtract(materialLotAction.getTransQty()));
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_STOCK_OUT);
            history.buildByMaterialLotAction(materialLotAction);
            history.setTransWarehouseId(fromWarehouse.getName());
            history.setTransStorageId(fromStorage.getName());
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
     * @return
     * @throws ClientException
     */
    public MaterialLot pick(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException {
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            PreConditionalUtils.checkNotNull(materialLotAction.getFromWarehouseRrn(), StringUtils.EMPTY);
            materialLot.validateMLotHold();

            Warehouse fromWarehouse = (Warehouse) warehouseRepository.findByObjectRrn(materialLotAction.getFromWarehouseRrn());
            Storage fromStorage = getFromStorageByMaterialLotAction(materialLotAction, fromWarehouse);

            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(), fromWarehouse.getObjectRrn(), fromStorage.getObjectRrn());
            if (materialLotInventory == null) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_NOT_IN_INVENTORY, materialLot.getMaterialLotId());
            }
            materialLot = getMLotByObjectRrn(materialLot.getObjectRrn());

            if (materialLot.getCurrentQty().compareTo(materialLotAction.getTransQty()) != 0) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_MUST_PICK_ALL, materialLot.getMaterialLotId());
            }
            // 变更物料库存并改变物料批次状态
            saveMaterialLotInventory(materialLot, fromWarehouse, fromStorage, materialLotAction.getTransQty().negate());
            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_PICK, StringUtils.EMPTY);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_PICK);
            history.buildByMaterialLotAction(materialLotAction);
            history.setTransWarehouseId(fromWarehouse.getName());
            history.setTransStorageId(fromStorage.getName());
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
     * @return
     * @throws ClientException
     */
    public MaterialLotInventory transfer(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException {
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            PreConditionalUtils.checkNotNull(materialLotAction.getFromWarehouseRrn(), StringUtils.EMPTY);
            PreConditionalUtils.checkNotNull(materialLotAction.getTargetWarehouseRrn(), StringUtils.EMPTY);


            Warehouse fromWarehouse = (Warehouse) warehouseRepository.findByObjectRrn(materialLotAction.getFromWarehouseRrn());
            Storage fromStorage = getFromStorageByMaterialLotAction(materialLotAction, fromWarehouse);

            Warehouse targetWarehouse = (Warehouse) warehouseRepository.findByObjectRrn(materialLotAction.getTargetWarehouseRrn());
            Storage targetStorage = getTargetStorageByMaterialLotAction(materialLotAction, targetWarehouse);

//            if (materialLotAction.getFromWarehouseRrn().equals(materialLotAction.getTargetWarehouseRrn()) && fromStorage.getObjectRrn().equals(targetStorage.getObjectRrn())) {
//                throw new ClientException(MmsException.MM_MATERIAL_LOT_TRANSFER_MUST_DIFFERENT_STORAGE);
//            }

            MaterialLotInventory materialLotInventory = getMaterialLotInv(materialLot.getObjectRrn(), fromWarehouse.getObjectRrn(), fromStorage.getObjectRrn());
            if (materialLotInventory == null) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_NOT_IN_INVENTORY, materialLot.getMaterialLotId());
            }
            materialLot = getMLotByObjectRrn(materialLot.getObjectRrn());

            if (materialLot.getCurrentQty().compareTo(materialLotAction.getTransQty()) != 0) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_MUST_TRANSFER_ALL, materialLot.getMaterialLotId());
            }
            materialLotInventory.setWarehouse(targetWarehouse).setStorage(targetStorage);
            materialLotInventoryRepository.saveAndFlush(materialLotInventory);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_INSTROAGE);
            history.buildByMaterialLotAction(materialLotAction);
            history.setTransWarehouseId(fromWarehouse.getName());
            history.setTransStorageId(fromStorage.getName());
            history.setTargetWarehouseId(targetWarehouse.getName());
            history.setTargetStorageId(targetStorage.getName());
            materialLotHistoryRepository.save(history);
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
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();

            materialLot = getMLotByObjectRrn(materialLot.getObjectRrn());

            BigDecimal currentQty = materialLot.getCurrentQty().subtract(materialLotAction.getTransQty());
            if (currentQty.compareTo(BigDecimal.ZERO) < 0) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_QTY_CANT_LESS_THEN_ZERO, materialLot.getMaterialLotId());
            }

            // 当批次在库存中，无法进行消耗/反消耗 只能进行盘点
            List<MaterialLotInventory> materialLotInventories = getMaterialLotInv(materialLot.getObjectRrn());
            if (CollectionUtils.isNotEmpty(materialLotInventories)) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IN_INVENTORY, materialLot.getMaterialLotId());
            }

            String eventId = MaterialEvent.EVENT_CONSUME;
            if (currentQty.compareTo(BigDecimal.ZERO) == 0) {
                eventId = MaterialEvent.EVENT_USE_UP;
            }
            materialLot = changeMaterialLotState(materialLot, eventId, StringUtils.EMPTY);

            materialLot.setCurrentQty(currentQty);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_CONSUME);
            history.buildByMaterialLotAction(materialLotAction);
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
    public MaterialLot holdMaterialLot(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException{
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            materialLot = getMLotByObjectRrn(materialLot.getObjectRrn());
            materialLot.validateMLotHold();

            //GC 物料批次只会hold一次。多重Hold只会记录历史，并不会产生多重Hold记录
            materialLot.setHoldState(MaterialLot.HOLD_STATE_ON);
            materialLot.setHoldReason(materialLotAction.getActionReason());
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHoldInfo materialLotHoldInfo = new MaterialLotHoldInfo();
            materialLotHoldInfo.setMaterialLot(materialLot);
            materialLotHoldInfo.setMaterialLotAction(materialLotAction);
            materialLotHoldInfoRepository.save(materialLotHoldInfo);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_HOLD);
            history.buildByMaterialLotAction(materialLotAction);
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
    public MaterialLot releaseMaterialLot(MaterialLot materialLot, MaterialLotAction materialLotAction) throws ClientException{
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            materialLot = getMLotByObjectRrn(materialLot.getObjectRrn());
            materialLot.setHoldState(MaterialLot.HOLD_STATE_OFF);
            materialLot.setHoldReason(StringUtils.EMPTY);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            List<MaterialLotHoldInfo> materialLotHoldInfos = materialLotHoldInfoRepository.findByMaterialLotId(materialLot.getMaterialLotId());
            if (CollectionUtils.isNotEmpty(materialLotHoldInfos)) {
                // GC 暂时只有一重Hold。故直接从Hold用户上判断即可
                MaterialLotHoldInfo materialLotHoldInfo = materialLotHoldInfos.get(0);
                // 其他人不能解除SCM的Hold SCM不能解除其他的Hold
                if (USER_SCM.equals(ThreadLocalContext.getUsername())) {
                    if (!USER_SCM.equals(materialLotHoldInfo.getCreatedBy())) {
                        throw new ClientException(MmsException.MATERIAL_LOT_IS_HOLD_BY_OTHERS);
                    }
                } else {
                    if (USER_SCM.equals(materialLotHoldInfo.getCreatedBy())) {
                        throw new ClientException(MmsException.MATERIAL_LOT_IS_HOLD_BY_SCM);
                    }
                }
                materialLotHoldInfoRepository.delete(materialLotHoldInfo);
            }

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_RELEASE);
            history.buildByMaterialLotAction(materialLotAction);
            materialLotHistoryRepository.save(history);
            return materialLot;
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
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_STOCK_QTY_CANOT_LESS_THEN_ZERO, materialLotInventory.getMaterialLotId());
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

    public List<MaterialLot> receiveMLots(Material material, List<MaterialLotAction> materialLotActionList) {
        try {
            List<MaterialLot> materialLots = Lists.newArrayList();
            StatusModel statusModel = getMaterialStatusModel(material);

            for (MaterialLotAction materialLotAction : materialLotActionList) {
                MaterialLot materialLot = createMLot(material, statusModel, materialLotAction);
                materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_RECEIVE, StringUtils.EMPTY);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_RECEIVE);
                history.buildByMaterialLotAction(materialLotAction);
                if(!StringUtils.isNullOrEmpty(materialLotAction.getWorkOrderId())){
                    history.setWorkOrderId(materialLotAction.getWorkOrderId());
                }
                history.setSourceModelId(materialLotAction.getSourceModelId());
                materialLotHistoryRepository.save(history);
                materialLots.add(materialLot);
            }
            return materialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public StatusModel getMaterialStatusModel(Material material) throws ClientException{
        try {
            StatusModel statusModel = null;
            if (material.getStatusModelRrn() == null) {
                List<MaterialStatusModel> statusModels = materialStatusModelRepository.findByNameAndOrgRrn(Material.DEFAULT_STATUS_MODEL, ThreadLocalContext.getOrgRrn());
                if (CollectionUtils.isNotEmpty(statusModels)) {
                    statusModel = statusModels.get(0);
                    material.setStatusModelRrn(statusModel.getObjectRrn());
                } else {
                    throw new ClientException(StatusMachineExceptions.STATUS_MODEL_IS_NOT_EXIST);
                }
            }
            material = materialRepository.saveAndFlush(material);
            if (statusModel == null) {
                statusModel = statusMachineService.getStatusModelByObjectRrn(material.getStatusModelRrn());
            }
            return statusModel;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
    /**
     * 接收物料批次
     * @param material 原物料/产品号
     * @param mLotId 物料批次号
     * @param materialLotAction 操作物料批次的动作包括了操作数量以及原因
     * @return
     */
    public MaterialLot receiveMLot(Material material, String mLotId, MaterialLotAction materialLotAction) {
        try {
            StatusModel statusModel = getMaterialStatusModel(material);
            materialLotAction.setMaterialLotId(mLotId);
            MaterialLot materialLot = createMLot(material, statusModel, materialLotAction);
            materialLot = changeMaterialLotState(materialLot, MaterialEvent.EVENT_RECEIVE, StringUtils.EMPTY);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_RECEIVE);
            history.buildByMaterialLotAction(materialLotAction);
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
     * @return
     */
    public MaterialLot receiveMLot2Warehouse(RawMaterial rawMaterial, MaterialLotAction lotAction) {
        try {
            return receiveMLot2Warehouse(rawMaterial, StringUtils.EMPTY, lotAction);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialLot getMLotByMLotId(String mLotId, boolean throwExceptionFlag) throws ClientException{
        try {
            MaterialLot materialLot =  materialLotRepository.findByMaterialLotIdAndOrgRrn(mLotId, ThreadLocalContext.getOrgRrn());
            if (materialLot == null && throwExceptionFlag) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, mLotId);
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    public MaterialLot getMLotByMLotIdAndBindWorkOrderId(String mLotId, boolean throwExceptionFlag) throws ClientException{
        try {
            MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(mLotId, ThreadLocalContext.getOrgRrn());

            List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(mLotId);
            if(materialLotUnitList != null && materialLotUnitList.size() > 0){
                for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                    if(!StringUtils.isNullOrEmpty(materialLotUnit.getWorkOrderId())){
                        materialLot.setWorkOrderId(materialLotUnit.getWorkOrderId());
                        break;
                    }
                }
            }

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
     * @param material 源物料/产品号
     *  mLotId 物料批次号。当为空的时候，按照设定的物料批次号生成规则进行生成
     * @return
     * @throws ClientException
     */
    public MaterialLot createMLot(Material material, StatusModel statusModel, MaterialLotAction materialLotAction) throws ClientException {
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            String mLotId = materialLotAction.getMaterialLotId();
            String grade = materialLotAction.getGrade();
            BigDecimal transQty = materialLotAction.getTransQty();
            Map<String, Object>  propsMap = materialLotAction.getPropsMap();
            BigDecimal currentSubQty = materialLotAction.getTransCount();
            String workOrderId = materialLotAction.getWorkOrderId();
            if (StringUtils.isNullOrEmpty(mLotId)) {
                mLotId = generatorMLotId(material);
            }
            MaterialLot materialLot = getMLotByMLotId(mLotId);
            if (materialLot != null) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, mLotId);
            }
            materialLot = new MaterialLot();
            materialLot.setMaterialLotId(mLotId);
            materialLot.setGrade(grade);

            materialLot.setStatusModelRrn(material.getStatusModelRrn());
            materialLot.setStatusCategory(statusModel.getInitialStateCategory());
            materialLot.setStatus(statusModel.getInitialState());

            materialLot.setReceiveQty(transQty);
            materialLot.setReceiveDate(new Date());
            materialLot.setCurrentQty(transQty);
            materialLot.setCurrentSubQty(currentSubQty);

            materialLot.setMaterialRrn(material.getObjectRrn());
            materialLot.setMaterialName(material.getName());
            materialLot.setMaterialDesc(material.getDescription());
            materialLot.setMaterialVersion(material.getVersion());
            materialLot.setMaterialCategory(material.getMaterialCategory());
            materialLot.setMaterialType(material.getMaterialType());
            materialLot.setStoreUom(material.getStoreUom());
            materialLot.setEffectiveLife(material.getEffectiveLife());
            materialLot.setEffectiveUnit(material.getEffectiveUnit());
            materialLot.setWarningLife(material.getWarningLife());
            materialLot.setReserved58(material.getSpareSpecs());
            materialLot.setReserved59(material.getSpareModel());
            materialLot.setReserved60(material.getSparePartsLine());

            if (propsMap != null && propsMap.size() > 0) {
                for (String propName : propsMap.keySet()) {
                    PropertyUtils.setProperty(materialLot, propName, propsMap.get(propName));
                }
            }
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            // 记录历史
            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE);
            history.setTransQty(materialLot.getCurrentQty());
            if(!StringUtils.isNullOrEmpty(workOrderId)){
                history.setWorkOrderId(workOrderId);
            }
            history.setCreated(materialLot.getCreated());
            materialLotHistoryRepository.save(history);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 晶圆接收入库操作
     * @param materialLot
     * @param transType
     * @throws ClientException
     */
    public void stockInMaterialLotUnitAndSaveHis(MaterialLot materialLot, String transType) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
            for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                materialLotUnit.setState(MaterialLotUnit.STATE_IN);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_IN);
                materialLotUnitHisRepository.save(history);
                log.info("received materialLotUnit is " + materialLotUnit);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据LotId验证物料批次是否需要预约Hold操作
     * 此处做预约Hold传的值都是已经拼接好的LotId包含“.”
     * 预约hold通过匹配 接收来源、接收类型、以及lotId来验证是否预约HOLD操作，并删除该HOLD信息，记录历史
     * @param receiveType 接收来源
     * @param importType 导入型号
     * @param lotId
     * @throws ClientException
     */
    public void validateFutureHoldByReceiveTypeAndProductAreaAndLotId(String receiveType, String importType, String lotId) throws ClientException{
        try {
            log.info("future Hold lotId is " + lotId);
            FutureHoldConfig futureHoldConfigSet = futureHoldConfigRepository.findByReceiveTypeAndProductAreaAndLotId(receiveType, importType, lotId);
            if(futureHoldConfigSet != null){
                log.info("future HoldConfigSet is " + futureHoldConfigSet);
                MaterialLot materialLot = materialLotRepository.findByLotIdAndReserved49AndStatusCategoryNotIn(futureHoldConfigSet.getLotId(), importType, MaterialLot.STATUS_FIN);
                if(materialLot != null && MaterialLot.HOLD_STATE_OFF.equals(materialLot.getHoldState())){
                    MaterialLotAction materialLotAction = new MaterialLotAction();
                    materialLotAction.setActionReason(futureHoldConfigSet.getHoldReason());
                    holdMaterialLot(materialLot, materialLotAction);
                }
/*                futureHoldConfigRepository.delete(futureHoldConfigSet);
                FutureHoldConfigHis history = (FutureHoldConfigHis) baseService.buildHistoryBean(futureHoldConfigSet, FutureHoldConfigHis.HOLD_DELETE);
                futureHoldConfigHisRepository.save(history);*/
            } else {
                String fabLotId = lotId.split("\\.")[0];
                FutureHoldConfig gcFutureHoldConfig = futureHoldConfigRepository.findByReceiveTypeAndProductAreaAndLotId(receiveType, importType, fabLotId);
                if (gcFutureHoldConfig != null){
                    List<MaterialLot> materialLotList = materialLotRepository.findByLotIdLikeAndReserved49AndStatusCategoryNotIn(lotId + "%", importType, MaterialLot.STATUS_FIN);
                    log.info("Hold materialLotList is " + materialLotList);
                    for(MaterialLot materialLot : materialLotList){
                        if(MaterialLot.HOLD_STATE_OFF.equals(materialLot.getHoldState())) {
                            MaterialLotAction materialLotAction = new MaterialLotAction();
                            materialLotAction.setActionReason(gcFutureHoldConfig.getHoldReason());
                            holdMaterialLot(materialLot, materialLotAction);
                        }
                    }
                    /*futureHoldConfigRepository.delete(gcFutureHoldConfig);
                    FutureHoldConfigHis history = (FutureHoldConfigHis) baseService.buildHistoryBean(gcFutureHoldConfig, FutureHoldConfigHis.HOLD_DELETE);
                    futureHoldConfigHisRepository.save(history);*/
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据 waferId(unitId) 和 materialLot 验证物料批次是否需要预约Hold操作
     * @param waferId
     * @param materialLot
     * @throws ClientException
     */
    @Override
    public void validateFutureHoldByWaferId(String waferId, MaterialLot materialLot) throws ClientException {
        try {
            String holdType = WaferHoldRelation.HOLD_TYPE_WLA;
            if(MaterialLot.IMPORT_LCD_CP.equals(materialLot.getReserved49()) || MaterialLot.IMPORT_SENSOR_CP.equals(materialLot.getReserved49())){
                holdType = WaferHoldRelation.HOLD_TYPE_SCM;
            }
            WaferHoldRelation waferHoldRelation = waferHoldRelationRepository.findByWaferIdAndType(waferId, holdType);
            materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLot.getMaterialLotId(), ThreadLocalContext.getOrgRrn());
            if (waferHoldRelation != null) {
                if(MaterialLot.HOLD_STATE_OFF.equals(materialLot.getHoldState())){
                    MaterialLotAction materialLotAction = new MaterialLotAction();
                    materialLotAction.setActionReason(waferHoldRelation.getHoldReason());
                    holdMaterialLot(materialLot, materialLotAction);
                }

                waferHoldRelationRepository.delete(waferHoldRelation);
                WaferHoldRelationHis history = (WaferHoldRelationHis) baseService.buildHistoryBean(waferHoldRelation, WaferHoldRelationHis.HOLD_DELETE);
                waferHoldRelationHisRepository.save(history);
            }
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
            String id = generatorService.generatorId(ThreadLocalContext.getOrgRrn(), generatorContext);
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
            List<MaterialLotMergeRule> mergeRule = materialLotMergeRuleRepository.findByNameAndOrgRrn(ruleName, ThreadLocalContext.getOrgRrn());
            if (CollectionUtils.isEmpty(mergeRule)) {
                throw new ClientParameterException(ContextException.MERGE_RULE_IS_NOT_EXIST, ruleName);
            }
            MergeRuleContext mergeRuleContext = new MergeRuleContext();
            mergeRuleContext.setBaseObject(materialLots.get(0));
            mergeRuleContext.setCompareObjects(materialLots);
            mergeRuleContext.setMergeRuleLines(mergeRule.get(0).getLines());
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
        List<Warehouse> warehouses = warehouseRepository.findByNameAndOrgRrn(name, ThreadLocalContext.getOrgRrn());
        if (CollectionUtils.isNotEmpty(warehouses)) {
            return warehouses.get(0);
        }
        return null;
    }


    /**
     * 验证物料批次规则信息是否一致，不一致返回false
     */
    public boolean validationMLotByMergeRule(String ruleName, List<MaterialLot> materialLots) throws ClientException{
        try {
            List<MaterialLotMergeRule> mLotValidateRule = materialLotMergeRuleRepository.findByNameAndOrgRrn(ruleName, ThreadLocalContext.getOrgRrn());
            if (CollectionUtils.isEmpty(mLotValidateRule)) {
                throw new ClientParameterException(ContextException.MLOT_VALIDATE_RULE_IS_NOT_EXIST, ruleName);
            }
            MergeRuleContext mergeRuleContext = new MergeRuleContext();
            mergeRuleContext.setBaseObject(materialLots.get(0));
            mergeRuleContext.setCompareObjects(materialLots);
            mergeRuleContext.setMergeRuleLines(mLotValidateRule.get(0).getLines());
            return  mergeRuleContext.validateMLot();
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
}
