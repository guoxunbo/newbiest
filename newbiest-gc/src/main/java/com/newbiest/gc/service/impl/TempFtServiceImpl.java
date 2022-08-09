package com.newbiest.gc.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.gc.GcExceptions;
import com.newbiest.gc.scm.dto.TempFtModel;
import com.newbiest.gc.scm.dto.TempFtVboxModel;
import com.newbiest.gc.service.GcService;
import com.newbiest.gc.service.TempFtService;
import com.newbiest.gc.thread.*;
import com.newbiest.mms.SystemPropertyUtils;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.msg.ResponseHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 格科临时使用service。
 *  老系统FT数据转到新系统
 * @author luoguozhang
 * @date 2022/2/11
 */
@Deprecated
@Service
@Transactional
@Slf4j
public class TempFtServiceImpl implements TempFtService {

    private static final Integer DEFAULT_IMPORT_MLOT_POOL_SIZE = 30;

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
    GeneratorService generatorService;

    @Autowired
    PackagedLotDetailRepository packagedLotDetailRepository;

    @Autowired
    MaterialLotInventoryRepository materialLotInventoryRepository;

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        Integer importMLotPoolSize = SystemPropertyUtils.getImportMLotPoolSize();
        if (importMLotPoolSize == null) {
            importMLotPoolSize = DEFAULT_IMPORT_MLOT_POOL_SIZE;
        }
        executorService = Executors.newFixedThreadPool(importMLotPoolSize);
    }

    /**
     * 转换老系统的FT数据
     * @throws ClientException
     */
    public String transferFtData(List<TempFtModel> tempCpModelList, String fileName) throws ClientException {
        try {
            String messageInfo = StringUtils.EMPTY;
            String importCode = generatorMLotUnitImportCode(MaterialLot.GENERATOR_INCOMING_MLOT_IMPORT_CODE_RULE);
            //区分真空包、COB、wafer处理,wafersource为21的则为COB类型数据，lotId为空的则为真空包，不为空则为wafer
            List<TempFtModel> cobLotList = tempCpModelList.stream().filter(tempFtModel -> TempFtModel.WAFER_SOURCE_21.equals(tempFtModel.getWaferSource())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(cobLotList)){
                if(cobLotList.size() != tempCpModelList.size()){
                    throw new ClientException(GcExceptions.COB_AND_OTHER_WAFER_CANNOT_TOGETHER_IMPORT);
                } else {
                    messageInfo = importCob(cobLotList, importCode, fileName, messageInfo);
                }
            } else {
                List<TempFtModel> vboxList = tempCpModelList.stream().filter(tempFtModel -> tempFtModel.getLotId() == null || StringUtils.isNullOrEmpty(tempFtModel.getLotId().trim())).collect(Collectors.toList());
                List<TempFtModel> lotUnitList = tempCpModelList.stream().filter(tempFtModel -> tempFtModel.getLotId() != null && !StringUtils.isNullOrEmpty(tempFtModel.getLotId().trim())).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(vboxList) && CollectionUtils.isNotEmpty(lotUnitList)){
                    throw new ClientException(GcExceptions.VBOX_AND_WAFER_CANNOT_TOGETHER_IMPORT);
                }
                if(CollectionUtils.isNotEmpty(vboxList)){
                    messageInfo = importVbox(vboxList, importCode, fileName, messageInfo);
                }
                if(CollectionUtils.isNotEmpty(lotUnitList)){
                    messageInfo = importLotInfo(lotUnitList, importCode, fileName, messageInfo);
                }
            }
            return messageInfo;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COB晶圆导入
     * @param cobLotList
     * @param importCode
     * @param fileName
     * @param messageInfo
     * @return
     * @throws ClientException
     */
    private String importCob(List<TempFtModel> cobLotList, String importCode, String fileName, String messageInfo) throws ClientException{
        try {
            Map<String, Storage> storageMap = getStorageMapInfo(cobLotList);
            Map<String, List<TempFtModel>> waferSourceMap = cobLotList.stream().collect(Collectors.groupingBy(TempFtModel :: getWaferSource));
            Map<String, Material> materialMap = getMaterialMapInfo(waferSourceMap);
            List<String> cstIdList = Lists.newArrayList();
            List<String> boxIdList = Lists.newArrayList();
            List<Future<FTImportMLotThreadResult>> cobImportCallBackList = Lists.newArrayList();

            Map<String, List<TempFtModel>> productFtModelMap = cobLotList.stream().collect(Collectors.groupingBy(TempFtModel :: getProductId));
            for(String productId : productFtModelMap.keySet()) {
                List<TempFtModel> tempFtModelList = productFtModelMap.get(productId);
                Material material = materialMap.get(productId);
                Map<String, List<TempFtModel>> lotCstMap = tempFtModelList.stream().collect(Collectors.groupingBy(TempFtModel :: getCstId));
                for(String cstId : lotCstMap.keySet()){
                    List<TempFtModel> lotTempCpModels = lotCstMap.get(cstId);
                    TempFtModel firstTempFtModel = lotTempCpModels.get(0);
                    Warehouse warehouse = getWareHoseByStockId(firstTempFtModel.getStockId().trim());
                    String pointId = firstTempFtModel.getPointId();
                    Storage storage = null;
                    if(StringUtils.isNullOrEmpty(pointId)){
                        storage = mmsService.getDefaultStorage(warehouse);
                    } else {
                        storage = storageMap.get(pointId.trim() + firstTempFtModel.getStockId().trim());
                    }
                    String shipper = StringUtils.EMPTY;
                    if(firstTempFtModel.getDataValue27() != null && !StringUtils.isNullOrEmpty(firstTempFtModel.getDataValue27().trim())){
                        shipper = firstTempFtModel.getDataValue27().split("_ _")[0];
                    }
                    String parentMaterialLotId = firstTempFtModel.getBoxId();
                    boxIdList.add(parentMaterialLotId);
                    cstIdList.add(cstId);
                    String productType = firstTempFtModel.getDataValue14() == null ? "" : firstTempFtModel.getDataValue14();
                    String productCategory = MaterialLotUnit.PRODUCT_CATEGORY_RMA.equals(productType) ? MaterialLotUnit.PRODUCT_CLASSIFY_RMA : MaterialLotUnit.PRODUCT_CATEGORY_FT_COB;
                    Date createHisDate = getDate(new Date());

                    FTImportCobMLotUnitThread ftImportCobMLotUnitThread = new FTImportCobMLotUnitThread();
                    ftImportCobMLotUnitThread.setMaterialLotRepository(materialLotRepository);
                    ftImportCobMLotUnitThread.setMaterialLotHistoryRepository(materialLotHistoryRepository);
                    ftImportCobMLotUnitThread.setMaterialLotUnitRepository(materialLotUnitRepository);
                    ftImportCobMLotUnitThread.setMaterialLotUnitHisRepository(materialLotUnitHisRepository);
                    ftImportCobMLotUnitThread.setMmsService(mmsService);
                    ftImportCobMLotUnitThread.setBaseService(baseService);
                    ftImportCobMLotUnitThread.setPackageService(packageService);
                    ftImportCobMLotUnitThread.setSessionContext(ThreadLocalContext.getSessionContext());

                    ftImportCobMLotUnitThread.setProductCategory(productCategory);
                    ftImportCobMLotUnitThread.setParentMaterialLotId(parentMaterialLotId);
                    ftImportCobMLotUnitThread.setImportType(MaterialLot.IMPORT_COB);
                    ftImportCobMLotUnitThread.setTargetWaferSource(MaterialLot.RW_WAFER_SOURCE);
                    ftImportCobMLotUnitThread.setFileName(fileName);
                    ftImportCobMLotUnitThread.setDurable(cstId);
                    ftImportCobMLotUnitThread.setFirstTempFtModel(firstTempFtModel);
                    ftImportCobMLotUnitThread.setImportCode(importCode);
                    ftImportCobMLotUnitThread.setMaterial(material);
                    ftImportCobMLotUnitThread.setWarehouse(warehouse);
                    ftImportCobMLotUnitThread.setStorage(storage);
                    ftImportCobMLotUnitThread.setShipper(shipper);
                    ftImportCobMLotUnitThread.setCreateHisDate(createHisDate);
                    ftImportCobMLotUnitThread.setTempFtModelList(lotTempCpModels);
                    Future<FTImportMLotThreadResult> importCallBack = executorService.submit(ftImportCobMLotUnitThread);
                    cobImportCallBackList.add(importCallBack);
                }
            }

            int maxWaitCount = 1000;
            String resultMessage = StringUtils.EMPTY;
            for (Future<FTImportMLotThreadResult> cobImportCallBack : cobImportCallBackList) {
                if (!StringUtils.isNullOrEmpty(resultMessage) || maxWaitCount <= 0) {
                    log.info("Cob import error." + resultMessage);
                    break;
                }
                while (true) {
                    if (cobImportCallBack.isDone()) {
                        FTImportMLotThreadResult importResult = cobImportCallBack.get();
                        if (!ResponseHeader.RESULT_SUCCESS.equals(importResult.getResult())) {
                            resultMessage = importResult.getResultMessage();
                        }
                        break;
                    } else {
                        Thread.sleep(300);
                        maxWaitCount--;
                        if (maxWaitCount == 0) {
                            resultMessage = MmsException.MM_MATERIAL_LOT_IMPORT_TIME_OUT;
                            break;
                        }
                    }
                }
            }
            if (!StringUtils.isNullOrEmpty(resultMessage)) {
                for (Future<FTImportMLotThreadResult> importCallBack : cobImportCallBackList) {
                    if (!importCallBack.isDone()) {
                        importCallBack.cancel(true);
                    }
                }
                deleteImportMaterialLotUnit(importCode, cstIdList, boxIdList);
                messageInfo = resultMessage;
            }
            return messageInfo;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * Lot类型数据导入
     * @param lotUnitList
     * @param importCode
     * @param fileName
     * @param messageInfo
     * @return
     * @throws ClientException
     */
    private String importLotInfo(List<TempFtModel> lotUnitList, String importCode, String fileName, String messageInfo) throws ClientException{
        try {
            Map<String, Storage> storageMap = getStorageMapInfo(lotUnitList);
            Map<String, List<TempFtModel>> waferSourceMap = lotUnitList.stream().collect(Collectors.groupingBy(TempFtModel :: getWaferSource));
            Map<String, Material> materialMap = getMaterialMapInfo(waferSourceMap);
            List<String> materialLotIdList = Lists.newArrayList();
            List<Future<FTImportMLotThreadResult>> waferImportCallBackList = Lists.newArrayList();
            for (String waferSource : waferSourceMap.keySet()) {
                List<TempFtModel> tempFtModels = waferSourceMap.get(waferSource);
                Map<String, List<TempFtModel>> productFtModelMap = tempFtModels.stream().collect(Collectors.groupingBy(TempFtModel :: getProductId));
                for(String productId : productFtModelMap.keySet()) {
                    List<TempFtModel> tempFtModelList = productFtModelMap.get(productId);
                    Material material = materialMap.get(productId);
                    Map<String, List<TempFtModel>> lotUnitMap = tempFtModelList.stream().collect(Collectors.groupingBy(TempFtModel :: getLotId));
                    for(String lotId : lotUnitMap.keySet()){
                        List<TempFtModel> lotTempCpModels = lotUnitMap.get(lotId);
                        TempFtModel firstTempFtModel = lotTempCpModels.get(0);
                        Warehouse warehouse = getWareHoseByStockId(firstTempFtModel.getStockId().trim());
                        String pointId = firstTempFtModel.getPointId();
                        Storage storage = null;
                        if(StringUtils.isNullOrEmpty(pointId)){
                            storage = mmsService.getDefaultStorage(warehouse);
                        } else {
                            storage = storageMap.get(pointId.trim() + firstTempFtModel.getStockId().trim());
                        }
                        Map<String, Object> propMap = Maps.newConcurrentMap();
                        getImportTypeAndReserved7AndWaferSourceBySourceWaferSource(propMap, waferSource, firstTempFtModel.getDataValue14() == null ? "" : firstTempFtModel.getDataValue14());
                        String productCategory = (String) propMap.get("reserved7");
                        String importType = (String) propMap.get("reserved49");
                        String targetWaferSource = (String) propMap.get("reserved50");
                        Date createHisDate = getDate(new Date());

                        FTImportMLotUnitThread ftImportMLotUnitThread = new FTImportMLotUnitThread();
                        ftImportMLotUnitThread.setMaterialLotRepository(materialLotRepository);
                        ftImportMLotUnitThread.setMaterialLotHistoryRepository(materialLotHistoryRepository);
                        ftImportMLotUnitThread.setMaterialLotUnitRepository(materialLotUnitRepository);
                        ftImportMLotUnitThread.setMaterialLotUnitHisRepository(materialLotUnitHisRepository);
                        ftImportMLotUnitThread.setMmsService(mmsService);
                        ftImportMLotUnitThread.setBaseService(baseService);
                        ftImportMLotUnitThread.setPackageService(packageService);
                        ftImportMLotUnitThread.setSessionContext(ThreadLocalContext.getSessionContext());

                        ftImportMLotUnitThread.setProductCategory(productCategory);
                        ftImportMLotUnitThread.setImportType(importType);
                        ftImportMLotUnitThread.setTargetWaferSource(targetWaferSource);
                        ftImportMLotUnitThread.setFileName(fileName);
                        ftImportMLotUnitThread.setLotId(lotId);
                        ftImportMLotUnitThread.setFirstTempFtModel(firstTempFtModel);
                        ftImportMLotUnitThread.setImportCode(importCode);
                        ftImportMLotUnitThread.setMaterial(material);
                        ftImportMLotUnitThread.setWarehouse(warehouse);
                        ftImportMLotUnitThread.setStorage(storage);
                        ftImportMLotUnitThread.setCreateHisDate(createHisDate);
                        ftImportMLotUnitThread.setTempFtModelList(lotTempCpModels);

                        Future<FTImportMLotThreadResult> importCallBack = executorService.submit(ftImportMLotUnitThread);
                        waferImportCallBackList.add(importCallBack);
                    }
                }
            }

            int maxWaitCount = 1200;
            String resultMessage = StringUtils.EMPTY;
            for (Future<FTImportMLotThreadResult> waferImportCallBack : waferImportCallBackList) {
                if (!StringUtils.isNullOrEmpty(resultMessage) || maxWaitCount <= 0) {
                    log.info("There has import error." + resultMessage);
                    break;
                }
                while (true) {
                    if (waferImportCallBack.isDone()) {
                        FTImportMLotThreadResult importResult = waferImportCallBack.get();
                        if (!ResponseHeader.RESULT_SUCCESS.equals(importResult.getResult())) {
                            resultMessage = importResult.getResultMessage();
                        } else {
                            materialLotIdList.addAll(importResult.getMaterialLotIdList());
                        }
                        break;
                    } else {
                        Thread.sleep(200);
                        maxWaitCount--;
                        if (maxWaitCount == 0) {
                            resultMessage = MmsException.MM_MATERIAL_LOT_IMPORT_TIME_OUT;
                            break;
                        }
                    }
                }
            }
            if (!StringUtils.isNullOrEmpty(resultMessage)) {
                for (Future<FTImportMLotThreadResult> importCallBack : waferImportCallBackList) {
                    if (!importCallBack.isDone()) {
                        importCallBack.cancel(true);
                    }
                }
                deleteImportMaterialLotUnit(importCode, materialLotIdList, Lists.newArrayList());
                messageInfo = resultMessage;
            }
            return messageInfo;
        } catch (Exception e) {
          throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 真空包类型数据导入
     * @param vboxList
     * @param messageInfo
     * @throws ClientException
     */
    private String importVbox(List<TempFtModel> vboxList, String importCode, String fileName, String messageInfo) throws ClientException{
        try {
            //先区分是否装箱，未装箱的直接处理，装箱的多线程处理
            List<TempFtModel> unPackedackMLotList = vboxList.stream().filter(tempFtModel -> tempFtModel.getBoxId() == null || StringUtils.isNullOrEmpty(tempFtModel.getBoxId()) ||
                    (!tempFtModel.getBoxId().startsWith(TempFtModel.BOX_START_B) && !tempFtModel.getBoxId().startsWith(TempFtModel.BOX_START_SBB)
                            && !tempFtModel.getBoxId().startsWith(TempFtModel.BOX_START_LB) && !tempFtModel.getBoxId().startsWith(TempFtModel.BOX_START_BZZSH))).collect(Collectors.toList());

            //处理装箱的真空包
            List<TempFtModel> boxedTempFtModelList = vboxList.stream().filter(tempFtModel -> !StringUtils.isNullOrEmpty(tempFtModel.getBoxId()) &&
                    (tempFtModel.getBoxId().startsWith(TempFtModel.BOX_START_B) || tempFtModel.getBoxId().startsWith(TempFtModel.BOX_START_SBB)
                            || tempFtModel.getBoxId().startsWith(TempFtModel.BOX_START_LB) || tempFtModel.getBoxId().startsWith(TempFtModel.BOX_START_BZZSH))).collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(unPackedackMLotList) && CollectionUtils.isNotEmpty(boxedTempFtModelList)){
                throw new ClientException(GcExceptions.VBOX_AND_BBOX_CANNOT_TOGETHER_IMPORT);
            }

            List<Future<FTImportMLotThreadResult>> importCallBackList = Lists.newArrayList();
            List<String> bboxIdList = Lists.newArrayList();
            List<String> vboxIdList = Lists.newArrayList();
            if(CollectionUtils.isNotEmpty(unPackedackMLotList)){
                //先获取库位信息，不存在新建
                Map<String, Storage> storageMap = getStorageMapInfo(unPackedackMLotList);
                Map<String, List<TempFtModel>> waferSourceMap = unPackedackMLotList.stream().collect(Collectors.groupingBy(TempFtModel :: getWaferSource));
                Map<String, Material> materialMap = getMaterialMapInfo(waferSourceMap);
                for(String waferSource : waferSourceMap.keySet()){
                    List<TempFtModel> tempFtModels = waferSourceMap.get(waferSource);
                    Map<String, List<TempFtModel>> productFtModelMap = tempFtModels.stream().collect(Collectors.groupingBy(TempFtModel :: getProductId));
                    for(String productId : productFtModelMap.keySet()){
                        List<TempFtModel> tempFtModelList = productFtModelMap.get(productId);
                        Material material = materialMap.get(productId);
                        for (TempFtModel tempFtModel : tempFtModelList) {
                            Warehouse warehouse = getWareHoseByStockId(tempFtModel.getStockId().trim());
                            String pointId = tempFtModel.getPointId();
                            Storage storage = null;
                            if(StringUtils.isNullOrEmpty(pointId)){
                                storage = mmsService.getDefaultStorage(warehouse);
                            } else {
                                storage = storageMap.get(pointId.trim() + tempFtModel.getStockId().trim());
                            }
                            Map<String, Object> propMap = Maps.newConcurrentMap();
                            String productType = tempFtModel.getDataValue14();
                            getImportTypeAndReserved7AndWaferSourceBySourceWaferSource(propMap, waferSource, productType == null ? "" : productType);
                            String productCategory = (String) propMap.get("reserved7");
                            String importType = (String) propMap.get("reserved49");
                            String targetWaferSource = (String) propMap.get("reserved50");
                            Date createHisDate = getDate(new Date());

                            FTImportVBoxThread ftImportVBoxThread = new FTImportVBoxThread();
                            ftImportVBoxThread.setMaterialLotRepository(materialLotRepository);
                            ftImportVBoxThread.setMaterialLotHistoryRepository(materialLotHistoryRepository);
                            ftImportVBoxThread.setMmsService(mmsService);
                            ftImportVBoxThread.setBaseService(baseService);
                            ftImportVBoxThread.setPackageService(packageService);
                            ftImportVBoxThread.setSessionContext(ThreadLocalContext.getSessionContext());

                            ftImportVBoxThread.setProductCategory(productCategory);
                            ftImportVBoxThread.setImportType(importType);
                            ftImportVBoxThread.setTargetWaferSource(targetWaferSource);
                            ftImportVBoxThread.setFileName(fileName);
                            ftImportVBoxThread.setImportCode(importCode);
                            ftImportVBoxThread.setMaterial(material);
                            ftImportVBoxThread.setWarehouse(warehouse);
                            ftImportVBoxThread.setStorage(storage);
                            ftImportVBoxThread.setCreateHisDate(createHisDate);
                            ftImportVBoxThread.setTempFtModel(tempFtModel);
                            vboxIdList.add(tempFtModel.getWaferId());

                            Future<FTImportMLotThreadResult> importCallBack = executorService.submit(ftImportVBoxThread);
                            importCallBackList.add(importCallBack);
                        }
                    }
                }
            } else if(CollectionUtils.isNotEmpty(boxedTempFtModelList)){
                Map<String, Storage> storageMap = getStorageMapInfo(boxedTempFtModelList);
                Map<String, List<TempFtModel>> waferSourceMap = boxedTempFtModelList.stream().collect(Collectors.groupingBy(TempFtModel :: getWaferSource));
                Map<String, Material> materialMap = getMaterialMapInfo(waferSourceMap);
                for (String waferSource : waferSourceMap.keySet()) {
                    List<TempFtModel> tempFtModels = waferSourceMap.get(waferSource);
                    Map<String, List<TempFtModel>> boxedTempFtModelMap = tempFtModels.stream().collect(Collectors.groupingBy(TempFtModel::getBoxId));
                    for (String parentMaterialLotId : boxedTempFtModelMap.keySet()) {
                        bboxIdList.add(parentMaterialLotId);
                        List<TempFtModel> boxInfoList = boxedTempFtModelMap.get(parentMaterialLotId);
                        Material material = materialMap.get(boxInfoList.get(0).getProductId());
                        Warehouse warehouse = getWareHoseByStockId(boxInfoList.get(0).getStockId().trim());
                        String pointId = boxInfoList.get(0).getPointId();
                        Storage storage = null;
                        if(StringUtils.isNullOrEmpty(pointId)){
                            storage = mmsService.getDefaultStorage(warehouse);
                        } else {
                            storage = storageMap.get(pointId.trim() + boxInfoList.get(0).getStockId().trim());
                        }
                        String packageType = MaterialLot.DFT_PACKAGE_TYPE;
                        if (parentMaterialLotId.startsWith(TempFtModel.BOX_START_LB)) {
                            packageType = MaterialLot.LCD_PACKCASE;
                        }

                        Map<String, Object> propMap = Maps.newConcurrentMap();
                        String productType = boxInfoList.get(0).getDataValue14();
                        getImportTypeAndReserved7AndWaferSourceBySourceWaferSource(propMap, waferSource, productType == null ? "" : productType);
                        String productCategory = (String) propMap.get("reserved7");
                        String importType = (String) propMap.get("reserved49");
                        String targetWaferSource = (String) propMap.get("reserved50");
                        Date createHisDate = getDate(new Date());

                        FTImportMLotThread ftImportMLotThread = new FTImportMLotThread();
                        ftImportMLotThread.setMaterialLotRepository(materialLotRepository);
                        ftImportMLotThread.setMaterialLotHistoryRepository(materialLotHistoryRepository);
                        ftImportMLotThread.setMmsService(mmsService);
                        ftImportMLotThread.setBaseService(baseService);
                        ftImportMLotThread.setPackageService(packageService);
                        ftImportMLotThread.setSessionContext(ThreadLocalContext.getSessionContext());

                        ftImportMLotThread.setParentMaterialLotId(parentMaterialLotId);
                        ftImportMLotThread.setProductCategory(productCategory);
                        ftImportMLotThread.setImportType(importType);
                        ftImportMLotThread.setTargetWaferSource(targetWaferSource);
                        ftImportMLotThread.setPackageType(packageType);
                        ftImportMLotThread.setFileName(fileName);
                        ftImportMLotThread.setImportCode(importCode);
                        ftImportMLotThread.setMaterial(material);
                        ftImportMLotThread.setWarehouse(warehouse);
                        ftImportMLotThread.setStorage(storage);
                        ftImportMLotThread.setCreateHisDate(createHisDate);
                        ftImportMLotThread.setTempFtModelList(boxInfoList);

                        Future<FTImportMLotThreadResult> importCallBack = executorService.submit(ftImportMLotThread);
                        importCallBackList.add(importCallBack);
                    }
                }
            }

            int maxWaitCount = 1000;// 最大等待返回次数 300*100最长30S
            String resultMessage = StringUtils.EMPTY;
            for (Future<FTImportMLotThreadResult> ftVboxImportCallBack : importCallBackList) {
                if (!StringUtils.isNullOrEmpty(resultMessage) || maxWaitCount <= 0) {
                    log.warn("There has some import error. please see log get more details.");
                    log.info("There has import error." + resultMessage);
                    break;
                }
                while (true) {
                    if (ftVboxImportCallBack.isDone()) {
                        FTImportMLotThreadResult importResult = ftVboxImportCallBack.get();
                        if (!ResponseHeader.RESULT_SUCCESS.equals(importResult.getResult())) {
                            resultMessage = importResult.getResultMessage();
                        }
                        break;
                    } else {
                        Thread.sleep(200);
                        maxWaitCount--;
                        if (maxWaitCount == 0) {
                            resultMessage = MmsException.MM_MATERIAL_LOT_IMPORT_TIME_OUT;
                            break;
                        }
                    }
                }
            }
            //停止线程
            if (!StringUtils.isNullOrEmpty(resultMessage)) {
                for (Future<FTImportMLotThreadResult> importCallBack : importCallBackList) {
                    if (!importCallBack.isDone()) {
                        importCallBack.cancel(true);
                    }
                }
                deleteImportMaterialLot(importCode, bboxIdList, vboxIdList);
                messageInfo = resultMessage;
            }
            return messageInfo;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取产品信息
     * @param waferSourceMap
     * @return
     * @throws ClientException
     */
    private Map<String,Material> getMaterialMapInfo(Map<String,List<TempFtModel>> waferSourceMap) throws ClientException{
        try {
            Map<String, Material> materialMap = Maps.newHashMap();
            for(String waferSource : waferSourceMap.keySet()){
                List<TempFtModel> tempFtModels = waferSourceMap.get(waferSource);
                Map<String, List<TempFtModel>> productFtModelMap = tempFtModels.stream().collect(Collectors.groupingBy(TempFtModel :: getProductId));
                for(String productId : productFtModelMap.keySet()){
                    Material material = validateAndGetMaterial(waferSource, productId.trim());
                    materialMap.put(productId, material);
                }
            }
            return materialMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取库位信息
     * @param unPackedackMLotList
     * @return
     * @throws ClientException
     */
    private Map<String,Storage> getStorageMapInfo(List<TempFtModel> unPackedackMLotList) throws ClientException{
        try {
            Map<String, Storage> storageMap = Maps.newHashMap();
            Map<String, List<TempFtModel>> warehouseMap = unPackedackMLotList.stream().collect(Collectors.groupingBy(TempFtModel :: getStockId));
            for(String warehouseId : warehouseMap.keySet()){
                List<TempFtModel> tempFtModels = warehouseMap.get(warehouseId);
                Warehouse warehouse = getWareHoseByStockId(warehouseId.trim());
                List<TempFtModel> pointIdList = tempFtModels.stream().filter(tempFtModel -> tempFtModel.getPointId() != null || !StringUtils.isNullOrEmpty(tempFtModel.getPointId().trim())).collect(Collectors.toList());
                Map<String, List<TempFtModel>> pointIdMap = pointIdList.stream().collect(Collectors.groupingBy(TempFtModel :: getPointId));
                for(String storageId : pointIdMap.keySet()){
                    Storage storage = getStorageByPointId(storageId.trim(), warehouse);
                    String key = storageId.trim() + warehouseId.trim();
                    if(!storageMap.containsKey(key)){
                        storageMap.put(key, storage);
                    }
                }
            }
            return storageMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 查询库位信息
     * @param pointId
     * @param warehouse
     * @return
     * @throws ClientException
     */
    private Storage getStorageByPointId(String pointId, Warehouse warehouse) throws ClientException{
        try {
            Storage storage = null;
            if(pointId != null && !StringUtils.isNullOrEmpty(pointId)){
                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setTargetStorageId(pointId);
                storage = mmsService.getStorageByWarehouseRrnAndName(warehouse, pointId);
                if (storage == null ) {
                    storage = new Storage();
                    storage.setName(materialLotAction.getTargetStorageId());
                    storage.setDescription(StringUtils.SYSTEM_CREATE);
                    storage.setWarehouseRrn(warehouse.getObjectRrn());
                    storage = storageRepository.saveAndFlush(storage);
                }
            } else {
                storage = mmsService.getDefaultStorage(warehouse);
            }
            return storage;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 删除晶圆信息
     * @param importCode
     * @param materialLotIdList
     * @throws ClientException
     */
    private void deleteImportMaterialLotUnit(String importCode, List<String> materialLotIdList, List<String> bboxIdList) throws ClientException{
        try {
            materialLotRepository.deleteByImportType(importCode);
            materialLotHistoryRepository.deleteByImportCode(importCode);
            materialLotUnitRepository.deleteByImportCode(importCode);
            materialLotUnitHisRepository.deleteByImportCode(importCode);
            if(CollectionUtils.isNotEmpty(materialLotIdList)){
                materialLotInventoryRepository.deleteByMaterialLotIdIn(materialLotIdList);
            }
            if(CollectionUtils.isNotEmpty(bboxIdList)){
                packagedLotDetailRepository.deleteByPackagedLotIdIn(bboxIdList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 删除导入的所有数据
     * @param importCode
     * @param bboxIdList
     * @param vboxIdList
     * @throws ClientException
     */
    private void deleteImportMaterialLot(String importCode, List<String> bboxIdList, List<String> vboxIdList) throws ClientException{
        try {
            materialLotRepository.deleteByImportType(importCode);
            materialLotHistoryRepository.deleteByImportCode(importCode);
            //删除包装信息
            if(CollectionUtils.isNotEmpty(bboxIdList)){
                packagedLotDetailRepository.deleteByPackagedLotIdIn(bboxIdList);
            }
            //删除真空包库存
            if(CollectionUtils.isNotEmpty(vboxIdList)){
                materialLotInventoryRepository.deleteByMaterialLotIdIn(vboxIdList);
            }
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
            return generatorService.generatorId(ThreadLocalContext.getOrgRrn(), generatorContext);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取三分钟后的时间
     * @param created
     * @return
     * @throws ClientException
     */
    private Date getDate(Date created) throws ClientException{
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(created);
            calendar.add(Calendar.MINUTE, 3);
            return calendar.getTime();
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据仓库名称获取仓库信息
     * @param stockId
     * @return
     * @throws ClientException
     */
    private Warehouse getWareHoseByStockId(String stockId) throws ClientException{
        try {
            Warehouse warehouse = mmsService.getWarehouseByName(stockId);
            if (warehouse == null) {
                throw new ClientParameterException(MmsException.MM_WAREHOUSE_IS_NOT_EXIST, stockId);
            }
            return warehouse;
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据waferSource获取产品号或者晶圆型号
     * @param waferSource
     * @param productId
     * @return
     * @throws ClientException
     */
    private Material validateAndGetMaterial(String waferSource, String productId) throws ClientException{
        try {
            Material material = new Material();
            if(TempFtModel.WAFER_SOURCE_LIST_4.contains(waferSource) || TempFtModel.WAFER_SOURCE_100.equals(waferSource)){
                if(TempFtModel.WAFER_SOURCE_100.equals(waferSource)){
                    productId += "-4.7";
                } else{
                    productId += "-4";
                }
                material = mmsService.getProductByName(productId);
                if (material == null) {
                    material = gcService.saveProductAndSetStatusModelRrn(productId);
                }
            } else if(TempFtModel.WAFER_SOURCE_LIST_35.contains(waferSource)){
                productId += "-3.5";
                material = mmsService.getRawMaterialByName(productId);
                if (material == null) {
                    RawMaterial rawMaterial = new RawMaterial();
                    rawMaterial.setName(productId);
                    material =  mmsService.createRawMaterial(rawMaterial);
                }
            }
            return  material;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据老系统WaferSouce获取导入型号和waferSource
     * @param propMap
     * @param waferSource
     * @param productType
     * @throws ClientException
     */
    private void getImportTypeAndReserved7AndWaferSourceBySourceWaferSource(Map<String,Object> propMap, String waferSource, String productType) throws ClientException{
        try {
            if(TempFtModel.WAFER_SOURCE_1.equals(waferSource) || TempFtModel.WAFER_SOURCE_3.equals(waferSource)){
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CATEGORY_RMA.equals(productType) ? MaterialLotUnit.PRODUCT_CLASSIFY_RMA :  MaterialLotUnit.PRODUCT_CLASSIFY_SENSOR);//SENSOR0
                propMap.put("reserved49", MaterialLot.IMPORT_SENSOR);//SENSOR
                propMap.put("reserved50", MaterialLot.SENSOR_WAFER_SOURCE);//9
            } else if(TempFtModel.WAFER_SOURCE_2.equals(waferSource)) {
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CATEGORY_RMA.equals(productType) ? MaterialLotUnit.PRODUCT_CLASSIFY_RMA : MaterialLotUnit.PRODUCT_CATEGORY_FT);//FT
                propMap.put("reserved49", MaterialLotUnit.PRODUCT_CATEGORY_FT);//FT
                propMap.put("reserved50", MaterialLot.FT_WAFER_SOURCE);//10
            } else if(TempFtModel.WAFER_SOURCE_11.equals(waferSource) || TempFtModel.WAFER_SOURCE_12.equals(waferSource)){
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CATEGORY_RMA.equals(productType) ? MaterialLotUnit.PRODUCT_CLASSIFY_RMA : MaterialLotUnit.PRODUCT_CLASSIFY_COG);//COG0
                propMap.put("reserved49", MaterialLot.IMPORT_COG);//COG
                propMap.put("reserved50", MaterialLot.COG_WAFER_SOURCE);//17
            } else if(TempFtModel.WAFER_SOURCE_21.equals(waferSource)){
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CATEGORY_RMA.equals(productType) ? MaterialLotUnit.PRODUCT_CLASSIFY_RMA : MaterialLotUnit.PRODUCT_CATEGORY_FT_COB);//COB
                propMap.put("reserved49", MaterialLot.IMPORT_COB);//COB
                propMap.put("reserved50", MaterialLot.RW_WAFER_SOURCE);//20
            } else if(TempFtModel.WAFER_SOURCE_31.equals(waferSource)){
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CATEGORY_RMA.equals(productType) ? MaterialLotUnit.PRODUCT_CLASSIFY_RMA : MaterialLotUnit.PRODUCT_CATEGORY_RW);//RW
                propMap.put("reserved49", MaterialLot.IMPORT_SENSOR_CP);//SENSOR_CP
                propMap.put("reserved50", MaterialLot.RW_WAFER_SOURCE);//20
            } else if(TempFtModel.WAFER_SOURCE_32.equals(waferSource)){
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CATEGORY_RMA.equals(productType) ? MaterialLotUnit.PRODUCT_CLASSIFY_RMA : MaterialLotUnit.PRODUCT_CLASSIFY_RMA);//RMA
                propMap.put("reserved49", MaterialLot.IMPORT_CRMA);//CRMA
                propMap.put("reserved50", MaterialLot.RMA_WAFER_SOURCE);//11
            } else if(TempFtModel.WAFER_SOURCE_33.equals(waferSource) || TempFtModel.WAFER_SOURCE_34.equals(waferSource)){
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CATEGORY_RMA.equals(productType) ? MaterialLotUnit.PRODUCT_CLASSIFY_RMA : MaterialLotUnit.PRODUCT_CLASSIFY_WLT);//WLT0
                propMap.put("reserved49", MaterialLot.IMPORT_WLT);//WLT
                propMap.put("reserved50", MaterialLot.WLT_PACK_RETURN_WAFER_SOURCE);//7
            } else if(TempFtModel.WAFER_SOURCE_39.equals(waferSource)){
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CATEGORY_RMA.equals(productType) ? MaterialLotUnit.PRODUCT_CLASSIFY_RMA : MaterialLotUnit.PRODUCT_CLASSIFY_SOC);//SOC0
                propMap.put("reserved49", MaterialLot.IMPORT_SOC);//SOC
                propMap.put("reserved50", MaterialLot.SOC_WAFER_SOURCE);//18
            } else if(TempFtModel.WAFER_SOURCE_100.equals(waferSource)){
                propMap.put("reserved7", MaterialLotUnit.PRODUCT_CATEGORY_RMA.equals(productType) ? MaterialLotUnit.PRODUCT_CLASSIFY_RMA : MaterialLot.PRODUCT_CATEGORY);//COM
                propMap.put("reserved49", MaterialLot.PRODUCT_CATEGORY);//COM
                propMap.put("reserved50", MaterialLot.COM_WAFER_SOURCE);//19
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收FT老系统真空包
     * @param tempFtVboxModelList
     * @throws ClientException
     */
    public void receiveFtOldSystemVbox(List<TempFtVboxModel> tempFtVboxModelList) throws ClientException{
        try {
            Map<String, List<TempFtVboxModel>> materialNameMap = tempFtVboxModelList.stream().collect(Collectors.groupingBy(TempFtVboxModel :: getProductId));
            for(String materialName : materialNameMap.keySet()){
                List<TempFtVboxModel> tempFtVboxModels = materialNameMap.get(materialName);

                Material material = mmsService.getProductByName(materialName);
                if (material == null) {
                    material = gcService.saveProductAndSetStatusModelRrn(materialName);
                }

                Map<String, List<TempFtVboxModel>> stockIdMap = tempFtVboxModels.stream().collect(Collectors.groupingBy(TempFtVboxModel :: getWarehouseName));
                for(String warehouseName : stockIdMap.keySet()){
                    Warehouse warehouse = getWareHoseByStockId(warehouseName);
                    List<TempFtVboxModel> ftVboxModels = stockIdMap.get(warehouseName);
                    for(TempFtVboxModel tempFtVboxModel : ftVboxModels){
                        MaterialLotAction materialLotAction = new MaterialLotAction();
                        materialLotAction.setTransQty(new BigDecimal(tempFtVboxModel.getQty()));
                        materialLotAction.setGrade(tempFtVboxModel.getGrade());
                        materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
                        if(StringUtils.isNullOrEmpty(tempFtVboxModel.getStorageId())){
                            Storage targetStorage = new Storage();
                            targetStorage.setName(materialLotAction.getTargetStorageId());
                            targetStorage.setDescription(StringUtils.SYSTEM_CREATE);
                            targetStorage.setWarehouseRrn(warehouse.getObjectRrn());
                            storageRepository.saveAndFlush(targetStorage);

                            materialLotAction.setTargetStorageId(tempFtVboxModel.getStorageId());
                        }

                        Map<String, Object> propMap = Maps.newConcurrentMap();
                        propMap.put("reserved1", tempFtVboxModel.getSecondCode() == null ? "": tempFtVboxModel.getSecondCode().trim());
                        propMap.put("reserved3", tempFtVboxModel.getSaleNote() == null ? "": tempFtVboxModel.getSaleNote().trim());
                        propMap.put("reserved4", tempFtVboxModel.getTreasuryNote() == null ? "": tempFtVboxModel.getTreasuryNote().trim());
                        propMap.put("reserved6", tempFtVboxModel.getLocation() == null ? "": tempFtVboxModel.getLocation().trim());
                        propMap.put("reserved7", MaterialLotUnit.PRODUCT_CATEGORY_FT);//FT
                        propMap.put("reserved13", warehouse.getObjectRrn().toString());
                        propMap.put("reserved14", tempFtVboxModel.getStorageId() == null ? "": tempFtVboxModel.getStorageId().trim());
                        propMap.put("reserved22", tempFtVboxModel.getSubName() == null ? "": tempFtVboxModel.getSubName().trim());
                        propMap.put("reserved46", tempFtVboxModel.getWo() == null ? "": tempFtVboxModel.getWo().trim());
                        propMap.put("reserved49", MaterialLotUnit.PRODUCT_CATEGORY_FT);//FT
                        propMap.put("reserved50", MaterialLot.FT_WAFER_SOURCE);//10
                        propMap.put("sourceProductId", tempFtVboxModel.getSoureProductId() == null ? "": tempFtVboxModel.getSoureProductId().trim());

                        materialLotAction.setPropsMap(propMap);
                        mmsService.receiveMLot2Warehouse(material, tempFtVboxModel.getBoxId(), materialLotAction);
                    }
                }
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
}