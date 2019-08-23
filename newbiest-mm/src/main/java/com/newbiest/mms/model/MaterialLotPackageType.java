package com.newbiest.mms.model;

import com.newbiest.base.dto.Action;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 物料包装规则定义
 * Created by guoxunbo on 2019/4/2.
 */
@Entity
@DiscriminatorValue(PackageType.CLASS_MATERIAL_LOT)
public class MaterialLotPackageType extends PackageType {

    @Override
    public void validationPacking(List<? extends NBUpdatable> packageChildren) {
        List<MaterialLot> materialLots = (List<MaterialLot>) packageChildren;
        //1. 验证批次是否已经被包装
        Optional<MaterialLot> packagedMaterial = materialLots.stream().filter(materialLot -> materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0).findFirst();
        if (packagedMaterial.isPresent()) {
            throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_ALREADY_PACKED, packagedMaterial.get().getMaterialLotId());
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
    public BigDecimal getPackedQty(List<? extends Action> actions) {
        List<MaterialLotAction> materialLotActions = (List<MaterialLotAction>) actions;
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

}
