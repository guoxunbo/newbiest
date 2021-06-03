package com.newbiest.vanchip.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 邮件信息
 */
@Entity
@Table(name="VC_MATERIAL_MODEL_CONVERSION")
@Data
public class MaterialModelConversion extends NBBase {

    /**
     * 一级分类
     */
    @Column(name = "MATERIAL_CATEGORY")
    private String materialCategory ;

    /**
     *二级分类
     */
    @Column(name = "MATERIAL_TYPE")
    private String materialType;

    /**
     * 三级分类
     */
    @Column(name = "MATERIAL_CLASSIFY")
    private String materialClassify ;

    /**
     *转换一级分类
     */
    @Column(name = "CONVERSION_MATERIAL_CATEGORY")
    private String conversionMaterialCategory;

    /**
     * 转换二级分类
     */
    @Column(name = "CONVERSION_MATERIAL_TYPE")
    private String conversionMaterialType ;

    /**
     *转换三级分类
     */
    @Column(name = "CONVERSION_MATERIAL_CLASSIFY")
    private String conversionMaterialClassify;

    /**
     *描述
     */
    @Column(name = "DESCRIPTION")
    private String description;

    /**
     *SAP/WMS
     */
    @Column(name = "MODEL_TYPE")
    private String modelType;
}