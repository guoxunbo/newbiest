package com.newbiest.mms.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.mms.model.MaterialLotUnitHistory;
import com.newbiest.mms.model.RawMaterial;
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

    /**
     * 生成物料批次以及物料批次对应的单元
     * @param materialLotUnitList
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> createMLot(List<MaterialLotUnit> materialLotUnitList) throws ClientException {
        try {
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
                    //TODO 如有有对预留栏位赋值的，此处进行赋值
                    Map<String, Object> propsMap = Maps.newHashMap();
                    propsMap.put("category", MaterialLot.CATEGORY_UNIT);
                    // TODO 对应的载具号
                    propsMap.put("durable", "");

                    MaterialLot materialLot = mmsService.createMLot(rawMaterial, statusModel,  materialLotId, StringUtils.EMPTY, totalQty, propsMap);
                    for (MaterialLotUnit materialLotUnit : materialLotUnitList) {
                        materialLotUnit.setMaterialLotRrn(materialLot.getObjectRrn());
                        materialLotUnit.setMaterialLotId(materialLot.getMaterialLotId());
                        materialLotUnit.setReceiveQty(materialLot.getCurrentQty());

                        materialLotUnit.setMaterialRrn(rawMaterial.getObjectRrn());
                        materialLotUnit.setMaterialName(rawMaterial.getName());
                        materialLotUnit.setMaterialDesc(rawMaterial.getDescription());
                        materialLotUnit.setMaterialVersion(rawMaterial.getVersion());
                        materialLotUnit.setMaterialCategory(rawMaterial.getMaterialCategory());
                        materialLotUnit.setMaterialType(rawMaterial.getMaterialType());
                        materialLotUnit.setStoreUom(rawMaterial.getStoreUom());
                        materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);
                        materialLotUnits.add(materialLotUnit);

                        MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE);
                        history.setTransQty(materialLot.getCurrentQty());
                        materialLotUnitHisRepository.save(history);
                    }
                }
            }
            return materialLotUnitList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }



}
