package com.newbiest.vanchip.repository;

import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.vanchip.model.MaterialModelConversion;

import java.util.List;

public interface MaterialModelConversionRepository extends IRepository<MaterialModelConversion, String> {

    List<MaterialModelConversion> findByMaterialCategoryAndMaterialTypeAndMaterialClassify(String materialCategory, String materialType, String materialClassify);

    List<MaterialModelConversion> findByMaterialCategoryAndMaterialType(String materialCategory, String materialType);

    List<MaterialModelConversion> findByMaterialCategoryAndMaterialClassify(String materialCategory, String materialClassify);
}
