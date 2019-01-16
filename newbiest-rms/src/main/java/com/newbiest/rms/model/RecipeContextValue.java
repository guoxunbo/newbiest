package com.newbiest.rms.model;

import com.newbiest.context.model.ContextValue;

/**
 * 产品工步关联Recipe
 * 当前不支持1个产品下有2个重名的工步
 * Created by guoxunbo on 2019/1/14.
 */
public class RecipeContextValue extends ContextValue {

    public static final String CONTEXT_NAME_PRODUCT_RECIPE = "ProductRecipeContext";

    public static final String CONTEXT_FIELD_STEP_NAME = "StepName";
    public static final String CONTEXT_FIELD_PART_NAME = "PartName";

    private void setPartName(String partName) {
        this.setFieldValue1(partName);
    }

    private void setStepName(String stepName) {
        this.setFieldValue2(stepName);
    }

    /**
     * 设置产品+工步对应的Recipe(PPId: process parameter id)
     */
    private void setRecipeName(String recipeName) {
        this.setResultValue1(recipeName);
    }

}
