package com.newbiest.mms.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.mms.repository.MaterialLotUnitHisRepository;
import com.newbiest.mms.repository.MaterialLotUnitRepository;
import com.newbiest.mms.service.MaterialLotUnitService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.utils.CollectorsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2020-01-17 12:39
 */
@Service
@Transactional
@Slf4j
public class MaterialLotUnitServiceImpl implements MaterialLotUnitService {

    @Autowired
    MmsService mmsService;

    @Autowired
    MaterialLotUnitRepository materialLotUnitRepository;

    @Autowired
    MaterialLotUnitHisRepository materialLotUnitHisRepository;

    @Autowired
    BaseService baseService;

    @Autowired
    GeneratorService generatorService;

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
            Long wherehouseRrn = warehouse.getObjectRrn();
            if(!StringUtils.isNullOrEmpty(materialLot.getReserved13())){
                wherehouseRrn = Long.parseLong(materialLot.getReserved13());
            }

            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
            materialLotAction.setTargetWarehouseRrn(wherehouseRrn);
            materialLotAction.setTransQty(materialLot.getCurrentQty());
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
            //生成导入编码
            String importCode = "";
            if(!StringUtils.isNullOrEmpty(materialLotUnitList.get(0).getReserved47())){
                importCode = generatorMLotUnitImportCode(MaterialLot.GENERATOR_INCOMING_MLOT_IMPORT_CODE_RULE);
            }
            Map<String, List<MaterialLotUnit>> materialUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialName));
            for (String materialName : materialUnitMap.keySet()) {
                RawMaterial rawMaterial = mmsService.getRawMaterialByName(materialName);
                if (rawMaterial == null) {
                    throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_IS_NOT_EXIST, materialName);
                }
                StatusModel statusModel = mmsService.getMaterialStatusModel(rawMaterial);
                Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialUnitMap.get(materialName).stream().collect(Collectors.groupingBy(MaterialLotUnit :: getMaterialLotId));

                for (String materialLotId : materialLotUnitMap.keySet()) {
                    List<MaterialLotUnit> materialLotUnits = materialLotUnitMap.get(materialLotId);

                    BigDecimal totalQty = materialLotUnits.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLotUnit :: getCurrentQty));
                    Map<String, Object> propsMap = Maps.newHashMap();
                    propsMap.put("category", MaterialLot.CATEGORY_UNIT);
                    propsMap.put("durable", materialLotUnits.get(0).getDurable());
                    propsMap.put("supplier", materialLotUnits.get(0).getSupplier());
                    propsMap.put("shipper", materialLotUnits.get(0).getShipper());
                    propsMap.put("grade", materialLotUnits.get(0).getGrade());

                    propsMap.put("reserved1",materialLotUnits.get(0).getReserved1());
                    propsMap.put("reserved6",materialLotUnits.get(0).getReserved4());
                    propsMap.put("reserved13",materialLotUnits.get(0).getReserved13());
                    propsMap.put("reserved22",materialLotUnits.get(0).getReserved22());
                    propsMap.put("reserved23",materialLotUnits.get(0).getReserved23());
                    propsMap.put("reserved24",materialLotUnits.get(0).getReserved24());
                    propsMap.put("reserved27",materialLotUnits.get(0).getReserved27());
                    propsMap.put("reserved28",materialLotUnits.get(0).getReserved28());
                    propsMap.put("reserved29",materialLotUnits.get(0).getReserved29());
                    propsMap.put("reserved32",materialLotUnits.get(0).getReserved32());
                    propsMap.put("reserved33",materialLotUnits.get(0).getReserved33());
                    propsMap.put("reserved34",materialLotUnits.get(0).getReserved34());
                    propsMap.put("reserved35",materialLotUnits.get(0).getReserved35());
                    propsMap.put("reserved36",materialLotUnits.get(0).getReserved36());
                    propsMap.put("reserved37",materialLotUnits.get(0).getReserved37());
                    propsMap.put("reserved38",materialLotUnits.get(0).getReserved38());
                    propsMap.put("reserved39",materialLotUnits.get(0).getReserved39());
                    propsMap.put("reserved41",materialLotUnits.get(0).getReserved41());
                    propsMap.put("reserved44",materialLotUnits.get(0).getReserved44());
                    propsMap.put("reserved45",materialLotUnits.get(0).getReserved45());
                    propsMap.put("reserved46",materialLotUnits.get(0).getReserved46());
                    propsMap.put("reserved47",materialLotUnits.get(0).getReserved47());
                    propsMap.put("reserved48",importCode);

                    MaterialLot materialLot = mmsService.createMLot(rawMaterial, statusModel,  materialLotId, StringUtils.EMPTY, totalQty, propsMap);
                    for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                        materialLotUnit.setMaterialLotRrn(materialLot.getObjectRrn());
                        materialLotUnit.setMaterialLotId(materialLot.getMaterialLotId());
                        materialLotUnit.setReceiveQty(materialLotUnit.getCurrentQty());
                        materialLotUnit.setReserved18("0");
                        materialLotUnit.setReserved48(importCode);
                        materialLotUnit.setMaterial(rawMaterial);
                        materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);
                        materialLotUnitArrayList.add(materialLotUnit);

                        MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_CREATE);
                        history.setTransQty(materialLotUnit.getReceiveQty());
                        materialLotUnitHisRepository.save(history);
                    }
                }
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

}
