package com.newbiest.mms.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.mms.SystemPropertyUtils;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.MaterialLotHistoryRepository;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.mms.repository.MaterialLotUnitHisRepository;
import com.newbiest.mms.repository.MaterialLotUnitRepository;
import com.newbiest.mms.service.MaterialLotUnitService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.thread.ImportMLotThread;
import com.newbiest.mms.thread.ImportMLotThreadResult;
import com.newbiest.msg.ResponseHeader;
import freemarker.template.utility.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2020-01-17 12:39
 */
@Service
@Transactional
@Slf4j
public class MaterialLotUnitServiceImpl implements MaterialLotUnitService {

    private static final Integer DEFAULT_IMPORT_MLOT_POOL_SIZE = 30;

    @Autowired
    MmsService mmsService;

    @Autowired
    MaterialLotUnitRepository materialLotUnitRepository;

    @Autowired
    MaterialLotUnitHisRepository materialLotUnitHisRepository;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialLotHistoryRepository materialLotHistoryRepository;

    @Autowired
    BaseService baseService;

    @Autowired
    GeneratorService generatorService;

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        Integer importMLotPoolSize = SystemPropertyUtils.getImportMLotPoolSize();
        if (importMLotPoolSize == null) {
            log.warn("System property import mlot pool size is not set. so use default pool size");
            importMLotPoolSize = DEFAULT_IMPORT_MLOT_POOL_SIZE;
        }
        executorService = Executors.newFixedThreadPool(importMLotPoolSize);
    }

    public List<MaterialLotUnit> getUnitsByMaterialLotId(String materialLotId) throws ClientException{
        return materialLotUnitRepository.findByMaterialLotId(materialLotId);
    }

    /**
     * 创建之后只做接收动作
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> receiveMLotWithUnit(MaterialLot materialLot, String warehouseName) throws ClientException {
        try {
            List<MaterialLotUnit> materialLotUnitList = Lists.newArrayList();
            Warehouse warehouse = mmsService.getWarehouseByName(warehouseName);
            if (warehouse == null) {
                throw new ClientParameterException(MmsException.MM_WAREHOUSE_IS_NOT_EXIST, warehouseName);
            }
            List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
            for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                materialLotUnit.setState(MaterialLotUnit.STATE_IN);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_IN);
                history.setTransQty(materialLotUnit.getCurrentQty());
                materialLotUnitHisRepository.save(history);
                materialLotUnitList.add(materialLotUnit);
            }
            Long warehouseRrn = warehouse.getObjectRrn();
            if(!StringUtils.isNullOrEmpty(materialLot.getReserved13())){
                warehouseRrn = Long.parseLong(materialLot.getReserved13());
            }

            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
            materialLotAction.setTargetWarehouseRrn(warehouseRrn);
            materialLotAction.setTransQty(materialLot.getCurrentQty());
            materialLotAction.setTransCount(materialLot.getCurrentSubQty());
            mmsService.stockIn(materialLot, materialLotAction);
            return materialLotUnitList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 生成物料批次以及物料批次对应的单元
     * @param materialLotUnitList
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> createMLot(List<MaterialLotUnit> materialLotUnitList) throws ClientException {
        try {
            List<MaterialLotUnit> materialLotUnitArrayList = new ArrayList<>();
            Map<String, List<MaterialLotUnit>> materialUnitIdMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getUnitId));
            for(String unitId : materialUnitIdMap.keySet()){
                if(materialUnitIdMap.get(unitId).size() > 1){
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_UNIT_ID_REPEATS, unitId);
                }
            }
            //生成导入编码
            String importCode = "";
            if(StringUtils.isNullOrEmpty(materialLotUnitList.get(0).getReserved48())){
                importCode = generatorMLotUnitImportCode(MaterialLot.GENERATOR_INCOMING_MLOT_IMPORT_CODE_RULE);
            } else {
                importCode = materialLotUnitList.get(0).getReserved48();
            }
            Map<String, List<MaterialLotUnit>> materialUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialName));
            List<Future<ImportMLotThreadResult>> importCallBackList = Lists.newArrayList();
            for (String materialName : materialUnitMap.keySet()) {
                Material material = mmsService.getRawMaterialByName(materialName);
                if (material == null) {
                    RawMaterial rawMaterial = new RawMaterial();
                    rawMaterial.setName(materialName);
                    material = mmsService.createRawMaterial(rawMaterial);
                }
                StatusModel statusModel = mmsService.getMaterialStatusModel(material);
                Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialUnitMap.get(materialName).stream().collect(Collectors.groupingBy(MaterialLotUnit :: getLotId));
                for(String lotId : materialLotUnitMap.keySet()){
                    MaterialLot materialLotInfo = materialLotRepository.findByLotIdAndReserved7NotIn(lotId, MaterialLotUnit.PRODUCT_CATEGORY_WLT);
                    if(materialLotInfo != null){
                        throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, lotId);
                    }
                }
                for (String lotId : materialLotUnitMap.keySet()) {
                    List<MaterialLotUnit> materialLotUnits = materialLotUnitMap.get(lotId);

                    // 导入进行多线程处理 进行并行处理
                    ImportMLotThread importMLotThread = new ImportMLotThread();
                    importMLotThread.setMaterialLotRepository(materialLotRepository);
                    importMLotThread.setMmsService(mmsService);
                    importMLotThread.setBaseService(baseService);
                    importMLotThread.setMaterialLotUnitRepository(materialLotUnitRepository);
                    importMLotThread.setMaterialLotUnitHisRepository(materialLotUnitHisRepository);
                    importMLotThread.setSessionContext(ThreadLocalContext.getSessionContext());

                    importMLotThread.setLotId(lotId);
                    importMLotThread.setImportCode(importCode);
                    importMLotThread.setMaterial(material);
                    importMLotThread.setStatusModel(statusModel);
                    importMLotThread.setMaterialLotUnits(materialLotUnits);

                    Future<ImportMLotThreadResult> importCallBack = executorService.submit(importMLotThread);
                    importCallBackList.add(importCallBack);
                }
            }

            // 最大等待返回次数
            int maxWaitCount = 300;
            boolean result = true;
            for (Future<ImportMLotThreadResult> importCallBack : importCallBackList) {
                if (!result || maxWaitCount <= 0) {
                    log.warn("There has some import error. please see log get more details.");
                    break;
                }
                while (true) {
                    if (importCallBack.isDone()) {
                        ImportMLotThreadResult importResult = importCallBack.get();
                        if (ResponseHeader.RESULT_SUCCESS.equals(importResult.getResult())) {
                            materialLotUnitArrayList.addAll(importResult.getMaterialLotUnits());
                        } else {
                            result = false;
                        }
                        break;
                    } else {
                        // 如果没做好，等待100ms,防止系统将CPU用光
                        Thread.sleep(50);
                        maxWaitCount--;
                        if (maxWaitCount == 0) {
                            log.info("====================================" + maxWaitCount);
                            break;
                        }
                    }
                }
            }

            if(!result){
                //停止线程
                for(Future<ImportMLotThreadResult> importCallBack : importCallBackList){
                    if(!importCallBack.isDone()){
                        importCallBack.cancel(true);
                    }
                }
                //删除导入数据
                Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitArrayList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialLotId));
                for(String materialLotId : materialLotUnitMap.keySet()){
                    materialLotUnitRepository.deleteByMaterialLotId(materialLotId);
                    materialLotRepository.deleteByMaterialLotId(materialLotId);
                    materialLotUnitHisRepository.deleteByMaterialLotId(materialLotId);
                    materialLotHistoryRepository.deleteByMaterialLotId(materialLotId);
                }
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST);
            }
            return materialLotUnitArrayList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 晶圆导入生成导入编码
     * @param ruleId
     * @return
     */
    private String generatorMLotUnitImportCode(String ruleId) throws ClientException{
        try {
            GeneratorContext generatorContext = new GeneratorContext();
            generatorContext.setRuleName(ruleId);
            String importCode = generatorService.generatorId(ThreadLocalContext.getOrgRrn(), generatorContext);
            return importCode;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * MES晶圆退仓库时验证晶圆信息是否已经存在，如果存在则修改状态
     * 晶圆换箱号则修改原箱号中的晶圆状态
     * @param materialLotUnitList
     */
    public String validateAndCreateMLotUnit(List<MaterialLotUnit> materialLotUnitList) throws ClientException{
        String errorMessage = "";
        try {
            Warehouse warehouse;
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialLotId));
            for(String materialLotId : materialLotUnitMap.keySet()){
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId);
                List<MaterialLotUnit> materialLotUnitInfo = materialLotUnitMap.get(materialLotId);

                for(MaterialLotUnit materialLotUnit : materialLotUnitInfo){
                    String warehouseName = materialLotUnit.getReserved13();
                    if(!StringUtils.isNullOrEmpty(warehouseName)){
                        warehouse = mmsService.getWarehouseByName(warehouseName);
                        materialLotUnit.setReserved13(warehouse.getObjectRrn().toString());
                    }
                }
                if(materialLot != null){
                    for(MaterialLotUnit materialLotUnit : materialLotUnitInfo){
                        materialLotUnitRepository.updateMLotUnitByUnitIdAndMLotId(materialLotUnit.getUnitId(), materialLotUnit.getMaterialLotId(), MaterialLotUnit.STATE_CREATE);
                    }
                    materialLot.setCurrentQty(materialLot.getReceiveQty());
                    materialLot.setStatusCategory(MaterialLotUnit.STATE_CREATE);
                    materialLot.setStatus(MaterialLotUnit.STATE_CREATE);
                    materialLot.setPreStatus("");
                    materialLot.setPreStatusCategory("");
                    materialLotRepository.saveAndFlush(materialLot);
                } else{
                    //修改unit表中存在且已发料的晶圆状态
                    for(MaterialLotUnit materialLotUnit : materialLotUnitInfo){
                        List<MaterialLotUnit> issuedMLotUnitInfo = materialLotUnitRepository.findByUnitIdAndState(materialLotUnit.getUnitId(), MaterialLotUnit.STATE_ISSUE);
                        for(MaterialLotUnit issuedMLotUnit : issuedMLotUnitInfo){
                            issuedMLotUnit.setState(MaterialLotUnit.STATE_SCRAP);
                            materialLotUnitRepository.saveAndFlush(issuedMLotUnit);
                        }
                    }
                    //重新导入退仓库的晶圆
                    createMLot(materialLotUnitInfo);
                }
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        return errorMessage;
    }

    /**
     * WLT导入更具FabLotId和第一片waferId获取载具号
     * @param materialLotUnitList
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> getMaterialLotUnitByFabLotAndWaferId(List<MaterialLotUnit> materialLotUnitList, String importType) throws ClientException {
        try {
            List<MaterialLotUnit> materialLotUnits = Lists.newArrayList();
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = Maps.newHashMap();

            if(importType.equals(MaterialLotUnit.WLA_UNMEASURED)){
                materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getDurable));
            } else {
                materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getReserved30));
            }
            for(String fabLotId : materialLotUnitMap.keySet()){
                List<MaterialLotUnit> mLotUnitList = materialLotUnitMap.get(fabLotId);
                Integer minWaferId = 0;
                for (MaterialLotUnit materialLotUnit : mLotUnitList) {
                    if(minWaferId == 0 || minWaferId > Integer.parseInt(materialLotUnit.getReserved31())){
                        minWaferId = Integer.parseInt(materialLotUnit.getReserved31());
                    }
                }
                String waferId = minWaferId+"";
                waferId = StringUtil.leftPad(waferId , 2 , "0");
                String lotId = fabLotId.split("\\.")[0] +"."+ waferId;
                for(MaterialLotUnit materialLotUnit : mLotUnitList){
                    materialLotUnit.setLotId(lotId);
                    materialLotUnit.setReserved30(fabLotId.split("\\.")[0]);
                    materialLotUnits.add(materialLotUnit);
                }
            }
            return materialLotUnits;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取出货标注的物料批次的晶圆信息
     * @param materialLotActions
     * @return materialLotUnitList
     * @throws ClientException
     */
    public List<MaterialLotUnit> queryStockOutTagMLotUnits(List<MaterialLotAction> materialLotActions) throws ClientException{
        try{
            List<MaterialLotUnit> materialLotUnitList = Lists.newArrayList();
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            for(MaterialLot materialLot : materialLots){
                List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                materialLotUnitList.addAll(materialLotUnits);
            }
            return materialLotUnitList;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }
}
