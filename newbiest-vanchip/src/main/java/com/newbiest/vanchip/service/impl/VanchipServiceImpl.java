package com.newbiest.vanchip.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.CollectorsUtils;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.PropertyUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.Document;
import com.newbiest.mms.model.IncomingOrder;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.RawMaterial;
import com.newbiest.mms.repository.IncomingOrderRepository;
import com.newbiest.mms.service.DocumentService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.state.model.MaterialStatusModel;
import com.newbiest.vanchip.service.VanChipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.newbiest.vanchip.exception.VanchipExceptions.*;

/**
 * @author guoxunbo
 * @date 12/24/20 2:22 PM
 */
@Slf4j
@Component
@Transactional
public class VanchipServiceImpl implements VanChipService {

    public static final String BIND_WO = "BindWo";
    public static final String UNBIND_WO = "UnbindWo";

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

    public void bindMesOrder(List<String> materialLotIdList, String workOrderId) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotIdList.stream().map(materialLotId -> {
                return mmsService.getMLotByMLotId(materialLotId, true);
            }).collect(Collectors.toList());
            Optional<MaterialLot> bindedMLot = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getWorkOrderId())).findFirst();
            if (bindedMLot.isPresent()) {
                throw new ClientParameterException(MLOT_BINDED_WORKORDER, bindedMLot.get().getMaterialLotId());
            }
            for (MaterialLot materialLot : materialLots) {
                materialLot.setWorkOrderId(workOrderId);
                baseService.saveEntity(materialLot, BIND_WO);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void unbindMesOrder(List<String> materialLotIdList) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotIdList.stream().map(materialLotId -> {
                return mmsService.getMLotByMLotId(materialLotId, true);
            }).collect(Collectors.toList());
            for (MaterialLot materialLot : materialLots) {
                materialLot.setWorkOrderId(StringUtils.EMPTY);
                baseService.saveEntity(materialLot, UNBIND_WO);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 来料导入
     * @param materialLots
     */
    public void importIncomingOrder(List<MaterialLot> materialLots) throws ClientException {
        try {

            BigDecimal totalQty = materialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentQty));

            //来料单创建即审核通过
            String documentId = generateIncomingDocId();
            IncomingOrder incomingOrder = new IncomingOrder();
            incomingOrder.setName(documentId);
            incomingOrder.setDescription(documentId);
            incomingOrder.setQty(totalQty);
            incomingOrder.setUnHandledQty(totalQty);
            incomingOrder.setStatus(Document.STATUS_APPROVE);
            incomingOrder.setApproveTime(DateUtils.now());
            incomingOrder.setApproveUser(ThreadLocalContext.getUsername());
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
