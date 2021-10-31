package com.newbiest.mms.model;

import com.newbiest.base.dto.Action;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.state.model.MaterialStatusCategory;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 物料包装规则定义
 * Created by guoxunbo on 2019/4/2.
 */
@Entity
@DiscriminatorValue(PackageType.CLASS_MATERIAL_LOT)
@Slf4j
public class MaterialLotPackageType extends PackageType {

    /**
     *最终客户 荣耀
     */
    public static final String FINAL_CUSTOMER_RY = "RY" ;

    /**
     *最终客户 小米
     */
    public static final String FINAL_CUSTOMER_XM = "XM" ;

    @Override
    public void validationPacking(List<? extends NBUpdatable> packageChildren) {
        List<MaterialLot> materialLots = (List<MaterialLot>) packageChildren;
        //1. 验证批次是否已经被包装
        Optional<MaterialLot> packagedMaterial = materialLots.stream().filter(materialLot -> MaterialStatusCategory.STATUS_CATEGORY_FIN.equals(materialLot.getStatusCategory())).findFirst();
        if (packagedMaterial.isPresent()) {
            throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_ALREADY_FIN, packagedMaterial.get().getMaterialLotId());
        }
        //2. 验证是否超过包装的最大数量限制
        if (beforePackCountType.equals(COUNT_TYPE_BY_LOT)) {
            BigDecimal lotSize = new BigDecimal(materialLots.size());
            if (maxQty.compareTo(lotSize) < 0) {
                throw new ClientException(MmsException.MM_PACKAGE_OVER_MAX_QTY);
            }
        } else if (beforePackCountType.equals(COUNT_TYPE_BY_LOT_QTY)) {
            int totalQty = materialLots.stream().collect(Collectors.summingInt(materialLot -> materialLot.getCurrentQty().intValue()));
            if (maxQty.compareTo(new BigDecimal(totalQty)) < 0) {
                throw new ClientException(MmsException.MM_PACKAGE_OVER_MAX_QTY);
            }
        }

    }

    @Override
    public void validationAppendPacking(List<? extends NBUpdatable> waitToAppendChildren, List<? extends Action> actions) {
        List<MaterialLot> waitToAppendMaterialLots = (List<MaterialLot>) waitToAppendChildren;
        List<MaterialLotAction> materialLotActions = (List<MaterialLotAction>) actions;

        //1. 验证批次是否已经被包装
        Optional<MaterialLot> packagedMaterial = waitToAppendMaterialLots.stream().filter(materialLot -> MaterialStatusCategory.STATUS_CATEGORY_FIN.equals(materialLot.getStatusCategory())).findFirst();
        if (packagedMaterial.isPresent()) {
            throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_ALREADY_FIN, packagedMaterial.get().getMaterialLotId());
        }

        //2. 验证是否超过包装的最大数量限制
        if (beforePackCountType.equals(COUNT_TYPE_BY_LOT)) {
            BigDecimal lotSize = new BigDecimal(materialLotActions.size());
            if (maxQty.compareTo(lotSize) < 0) {
                throw new ClientException(MmsException.MM_PACKAGE_OVER_MAX_QTY);
            }
        } else if (beforePackCountType.equals(COUNT_TYPE_BY_LOT_QTY)) {
            int totalQty = materialLotActions.stream().collect(Collectors.summingInt(action -> action.getTransQty().intValue()));
            if (maxQty.compareTo(new BigDecimal(totalQty)) < 0) {
                throw new ClientException(MmsException.MM_PACKAGE_OVER_MAX_QTY);
            }
        }
    }

    @Override
    public BigDecimal getPackedQty(List<? extends Action> actions) {
        List<MaterialLotAction> materialLotActions = (List<MaterialLotAction>) actions;
        if  (log.isInfoEnabled()) {
            log.info("Get PackedQty. PackageTypeInfo is [" + this.toString() + "]" );
        }
        if (packedCountType.equals(COUNT_TYPE_ONE)) {
            return BigDecimal.ONE;
        } else if (packedCountType.equals(COUNT_TYPE_BY_LOT)) {
            return new BigDecimal(materialLotActions.size());
        } else if (packedCountType.equals(COUNT_TYPE_BY_LOT_QTY)) {
            int totalQty = materialLotActions.stream().collect(Collectors.summingInt(materialLot -> materialLot.getTransQty().intValue()));
            return new BigDecimal(totalQty);
        }
        return BigDecimal.ZERO;
    }

    /**
     * vanChip 客制化验证
     * bin3 + 最终客户 => RY 验证DC
     * bin3 + 最终客户 => XM 验证CONTROL LOT
     */
    public void validationCustomizationPackageRule(String finalCustomer, String grade, List<MaterialLotUnit> materialLotUnits){
        if (StringUtils.isNullOrEmpty(finalCustomer) || StringUtils.isNullOrEmpty(grade)) {
            return;
        }
        if (FINAL_CUSTOMER_XM.equals(finalCustomer) && "PASS_BIN3".equals(grade)){
            this.validationControlLot(materialLotUnits);
        }else if (FINAL_CUSTOMER_RY.equals(finalCustomer) && "PASS_BIN3".equals(grade)){
            this.validationDC(materialLotUnits);
        }
    }

    /**
     * 验证control Lot规则
     */
    public void validationControlLot(List<MaterialLotUnit> materialLotUnits){
        if (StringUtils.isNullOrEmpty(reserved1) || CollectionUtils.isEmpty(materialLotUnits)){
            return;
        }
        Integer maxControlLotQty = Integer.valueOf(reserved1);
        Set<String> controlLotSet = materialLotUnits.stream().map(unit -> unit.getReserved4()).collect(Collectors.toSet());
        if (controlLotSet.size() > maxControlLotQty){
            throw new ClientParameterException(MmsException.MM_PACKAGE_OVER_MAX_CONTROL_LOT_QTY, maxControlLotQty);
        }

    }

    /**
     * 验证DC规则
     */
    public void validationDC(List<MaterialLotUnit> materialLotUnits){
        if (StringUtils.isNullOrEmpty(reserved2) || CollectionUtils.isEmpty(materialLotUnits)){
            return;
        }
        Integer maxDCQty = Integer.valueOf(reserved2);
        Set<String> dcSet = materialLotUnits.stream().map(unit -> unit.getReserved2()).collect(Collectors.toSet());
        if (dcSet.size() > maxDCQty){
            throw new ClientParameterException(MmsException.MM_PACKAGE_OVER_MAX_DC_QTY, maxDCQty);
        }
    }


}
