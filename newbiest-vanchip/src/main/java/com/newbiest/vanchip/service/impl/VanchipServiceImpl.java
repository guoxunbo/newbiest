package com.newbiest.vanchip.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.PropertyUtils;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.IncomingOrderRepository;
import com.newbiest.mms.service.DocumentService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.state.model.MaterialStatusModel;
import com.newbiest.vanchip.service.VanChipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author guoxunbo
 * @date 12/24/20 2:22 PM
 */
@Slf4j
@Component
@Transactional
public class VanchipServiceImpl implements VanChipService {

    @Autowired
    BaseService baseService;

    @Autowired
    MmsService mmsService;

    @Autowired
    DocumentService documentService;

    @Autowired
    GeneratorService generatorService;

    @Autowired
    IncomingOrderRepository incomingOrderRepository;


    /**
     * 来料导入
     * @param materialLots
     */
    public void importIncomingOrder(List<MaterialLot> materialLots) throws ClientException {
        try {
            String documentId = generateIncomingDocId();
            IncomingOrder incomingOrder = new IncomingOrder();
            incomingOrder.setName(documentId);
            incomingOrder.setDescription(documentId);
            incomingOrder = (IncomingOrder) baseService.saveEntity(incomingOrder);

            List<MaterialLot> documentMaterialLots = Lists.newArrayList();

            Map<String, List<MaterialLot>> materialMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getMaterialName));
            final IncomingOrder _incomingOrder = incomingOrder;
            materialMap.keySet().forEach(materialName -> {
                RawMaterial rawMaterial = mmsService.getRawMaterialByName(materialName);
                if (rawMaterial == null) {
                    throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_IS_NOT_EXIST, materialName);
                }
                MaterialStatusModel materialStatusModel = mmsService.getStatusModelByRrn(rawMaterial.getStatusModelRrn());
                List<MaterialLot> materialLotList = materialMap.get(materialName);

                for (MaterialLot materialLot : materialLotList) {
                    Map<String, Object> propMap = PropertyUtils.convertObj2Map(materialLot);
                    propMap.put("incomingDocRrn", _incomingOrder.getObjectRrn());
                    propMap.put("incomingDocId", _incomingOrder.getName());
                    MaterialLot mLot = mmsService.createMLot(rawMaterial, materialStatusModel, materialLot.getMaterialLotId(), materialLot.getCurrentQty(), materialLot.getCurrentSubQty(), propMap);
                    documentMaterialLots.add(mLot);
                }
            });




        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);

        }
    }

    private String generateIncomingDocId() throws ClientException {
        GeneratorContext generatorContext = new GeneratorContext();
        generatorContext.setRuleName(IncomingOrder.GENERATOR_INCOMING_ORDER_ID_RULE);
        return generatorService.generatorId(generatorContext);
    }


}
