package com.newbiest.mms.model;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.StringUtils;
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
        Optional<MaterialLot> subMaterialLot = materialLots.stream().filter(materialLot -> materialLot.getPackedFlag()).findFirst();
        if (subMaterialLot.isPresent()) {
            throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_ALREADY_PACKED, subMaterialLot.get().getMaterialLotId());
        }
        //2. 验证所有的批次类型是否一致
        if (!StringUtils.isNullOrEmpty(sourceMaterialType)) {
            Optional optional = materialLots.stream().filter(materialLot -> !sourceMaterialType.equals(materialLot.getMaterialType())).findFirst();
            if (optional.isPresent()) {
                throw new ClientException(MmsException.MM_PACKAGE_MATERIAL_TYPE_IS_NOT_THE_SAME);
            }
        }
        //3. 验证是否超过包装的最大数量限制
        if (maxQtyCountType.equals(COUNT_TYPE_BY_LOT)) {
            BigDecimal lotSize = new BigDecimal(materialLots.size());
            if (lotSize.compareTo(maxQty) < 0) {
                throw new ClientException(MmsException.MM_PACKAGE_OVER_MAX_QTY);
            }
        } else if (maxQtyCountType.equals(COUNT_TYPE_BY_LOT_QTY)) {
            int totalQty = materialLots.stream().collect(Collectors.summingInt(materialLot -> materialLot.getCurrentQty().intValue()));
            if (new BigDecimal(totalQty).compareTo(maxQty) < 0) {
                throw new ClientException(MmsException.MM_PACKAGE_OVER_MAX_QTY);
            }
        }

    }

    @Override
    public BigDecimal getPackedQty(List<? extends NBUpdatable> packageChildren) {
        List<MaterialLot> materialLots = (List<MaterialLot>) packageChildren;
        if (packedCountType.equals(COUNT_TYPE_ONE)) {
            return BigDecimal.ONE;
        } else if (packedCountType.equals(COUNT_TYPE_BY_LOT)) {
            return new BigDecimal(materialLots.size());
        } else if (packedCountType.equals(COUNT_TYPE_BY_LOT_QTY)) {
            int totalQty = materialLots.stream().collect(Collectors.summingInt(materialLot -> materialLot.getCurrentQty().intValue()));
            return new BigDecimal(totalQty);
        }
        return BigDecimal.ZERO;
    }

}