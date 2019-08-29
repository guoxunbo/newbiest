package com.newbiest.mms.gc.model.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.gc.model.MesPackedLot;
import com.newbiest.mms.gc.model.StockOutCheck;
import com.newbiest.mms.gc.model.service.GcService;
import com.newbiest.mms.gc.repository.MesPackedLotRepository;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotHistory;
import com.newbiest.mms.model.RawMaterial;
import com.newbiest.mms.repository.MaterialLotHistoryRepository;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.mms.service.MmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.newbiest.mms.exception.MmsException.*;

/**
 * Created by guoxunbo on 2019-08-21 12:41
 */
@Service
@Slf4j
@Transactional
public class GcServiceImpl implements GcService {

    public static final String TRANS_TYPE_BIND_RELAY_BOX = "BindRelayBox";
    public static final String TRANS_TYPE_UNBIND_RELAY_BOX = "UnbindRelayBox";
    public static final String TRANS_TYPE_JUDGE = "Judge";
    public static final String TRANS_TYPE_STOCK_OUT_CHECK = "StockOutCheck";

    public static final String REFERENCE_NAME_STOCK_OUT_CHECK_ITEM_LIST = "StockOutCheckItemList";

    public static final String EVENT_OQC = "OQC";

    @Autowired
    MesPackedLotRepository mesPackedLotRepository;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialLotHistoryRepository materialLotHistoryRepository;

    @Autowired
    MmsService mmsService;

    @Autowired
    BaseService baseService;

    @Autowired
    UIService uiService;

    /**
     * 出货前检查。
     *  直接以检查结果做状态
     * @param materialLot
     * @param stockOutCheckList
     * @return
     */
    public MaterialLot stockOutCheck(MaterialLot materialLot, List<StockOutCheck> stockOutCheckList) throws ClientException {
        try {
            String checkResult = StockOutCheck.RESULT_OK;
            Optional optional = stockOutCheckList.stream().filter(stockOutCheck -> StockOutCheck.RESULT_NG.equals(stockOutCheck.getResult())).findFirst();
            if (optional.isPresent()) {
                checkResult = StockOutCheck.RESULT_NG;
            }
            materialLot = mmsService.changeMaterialLotState(materialLot, EVENT_OQC, checkResult);
            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_STOCK_OUT_CHECK);
            materialLotHistoryRepository.save(history);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public List<StockOutCheck> getStockOutCheckList() throws ClientException {
        List<StockOutCheck> stockOutChecks = Lists.newArrayList();
        List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(REFERENCE_NAME_STOCK_OUT_CHECK_ITEM_LIST, NBReferenceList.CATEGORY_OWNER);
        if (CollectionUtils.isNotEmpty(nbReferenceList)) {
            for (NBOwnerReferenceList nbOwnerReference : nbReferenceList) {
                StockOutCheck stockOutCheck = new StockOutCheck();
                stockOutCheck.setName(nbOwnerReference.getValue());
                stockOutCheck.setResult(StockOutCheck.RESULT_OK);
                stockOutChecks.add(stockOutCheck);
            }

        }
        return stockOutChecks;
    }

    /**
     * 接收MES的完成品
     * @param packedLotList
     */
    public void receiveFinishGood(List<MesPackedLot> packedLotList) throws ClientException {
        try {
            Map<String, List<MesPackedLot>> packedLotMap = packedLotList.stream().map(packedLot -> mesPackedLotRepository.findByBoxId(packedLot.getBoxId())).collect(Collectors.groupingBy(MesPackedLot :: getProductId));
            packedLotMap.keySet().forEach(productId -> {
                RawMaterial rawMaterial = mmsService.getRawMaterialByName(productId);
                if (rawMaterial == null) {
                    throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, productId);
                }

                List<MesPackedLot> mesPackedLots = packedLotMap.get(productId);
                for (MesPackedLot mesPackedLot : mesPackedLots) {
                    MaterialLotAction materialLotAction = new MaterialLotAction();
                    materialLotAction.setGrade(mesPackedLot.getGrade());
                    materialLotAction.setTransQty(BigDecimal.valueOf(mesPackedLot.getQuantity()));
                    MaterialLot materialLot = mmsService.receiveMLot2Warehouse(rawMaterial, mesPackedLot.getBoxId(), materialLotAction);

                    materialLot.setWorkOrderId(mesPackedLot.getWorkorderId());
                    // 预留栏位赋值
                    materialLot.setReserved1(mesPackedLot.getLevelTwoCode());
                    materialLot.setReserved2(mesPackedLot.getWaferId());
                    materialLot.setReserved3(mesPackedLot.getSalesNote());
                    materialLot.setReserved4(mesPackedLot.getTreasuryNote());
                    materialLot.setReserved5(mesPackedLot.getProductionNote());
                    materialLot.setReserved6(mesPackedLot.getBondedProperty());
                    materialLot.setReserved7(mesPackedLot.getProductCategory());
                    materialLotRepository.save(materialLot);

                    // 修改MES成品批次为接收状态
                    mesPackedLot.setPackedStatus(MesPackedLot.PACKED_STATUS_RECEIVED);
                    mesPackedLotRepository.save(mesPackedLot);
                }
            });

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次绑定中转箱
     * @throws ClientException
     */
    public void bindRelaxBox(List<MaterialLot> materialLots, String relaxBoxId) throws ClientException{
        try {
            materialLots.forEach(materialLot -> {
                materialLot.setReserved8(relaxBoxId);
                materialLotRepository.save(materialLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_BIND_RELAY_BOX);
                materialLotHistoryRepository.save(history);
            });
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }

    }

    /**
     * 物料批次取消绑定中转箱
     * @throws ClientException
     */
    public void unbindRelaxBox(List<MaterialLot> materialLots) throws ClientException{
        try {
            materialLots.forEach(materialLot -> {
                materialLot.setReserved8(StringUtils.EMPTY);
                materialLotRepository.save(materialLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_UNBIND_RELAY_BOX);
                materialLotHistoryRepository.save(history);
            });
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }

    }

    /**
     * 对包装后的物料批次进行判等
     * @throws ClientException
     */
    public void judgePackedMaterialLot(List<MaterialLot> materialLots, String judgeGrade, String judgeCode) throws ClientException{
        try {
            materialLots.forEach(materialLot -> {
                materialLot = mmsService.getMLotByMLotId(materialLot.getMaterialLotId(), true);
                if (!StringUtils.isNullOrEmpty(materialLot.getPackageType())) {
                    materialLot.setReserved9(judgeGrade);
                    materialLot.setReserved10(judgeCode);
                    materialLotRepository.save(materialLot);
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_JUDGE);
                    materialLotHistoryRepository.save(history);
                }
            });
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }

    }

}
