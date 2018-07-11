package com.newbiest.rms.service;

import com.google.common.collect.Lists;
import com.newbiest.base.dao.BaseDao;
import com.newbiest.rms.model.AbstractRecipeEquipment;
import com.newbiest.rms.model.RecipeEquipment;
import com.newbiest.rms.model.RecipeEquipmentParameter;
import com.newbiest.rms.model.RecipeEquipmentProgram;
import com.newbiest.rms.repository.AbstractRecipeEquipmentRepository;
import com.newbiest.rms.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by guoxunbo on 2018/7/5.
 */
public class RmsServiceTest extends BaseTest {

    @Autowired
    RmsService rmsService;

    @Autowired
    AbstractRecipeEquipmentRepository abstractRecipeEquipmentRepository;

    @Test
    public void saveSingleRecipeEquipmentTest() {

        RecipeEquipment recipeEquipment = new RecipeEquipment();
        recipeEquipment.setRecipeName("TestRecipe");
        recipeEquipment.setEquipmentId("aa");
        recipeEquipment.setGoldenFlag(false);

        List<RecipeEquipmentParameter> parameters = Lists.newArrayList();
        RecipeEquipmentParameter parameter = new RecipeEquipmentParameter();
        parameter.setDefaultValue("1");
        parameter.setCompareFlag(true);
        parameter.setParameterName("test");
        parameter.setParameterDesc("test Parameter");
        parameters.add(parameter);

        recipeEquipment.setRecipeEquipmentParameters(parameters);

        rmsService.saveRecipeEquipment(recipeEquipment, sessionContext);

        RecipeEquipmentProgram program = new RecipeEquipmentProgram();
        program.setRecipeName("TestRecipe");
        program.setEquipmentId("aa");
        program.setGoldenFlag(false);
        program.setProgramName("test");
        program.setFileCheckType("tat");
        program.setRecipeEquipmentParameters(parameters);

        rmsService.saveRecipeEquipment(program, sessionContext);

    }

    @Test
    public void updateSingleRecipeEquipmentTest() {

        AbstractRecipeEquipment recipeEquipment = abstractRecipeEquipmentRepository.getByObjectRrn(1L);
        List<RecipeEquipmentParameter> parameters = Lists.newArrayList();
        RecipeEquipmentParameter parameter = new RecipeEquipmentParameter();
        parameter.setDefaultValue("2");
        parameter.setCompareFlag(true);
        parameter.setParameterName("test");
        parameter.setParameterDesc("test Parameter");
        parameters.add(parameter);

        parameter = new RecipeEquipmentParameter();
        parameter.setDefaultValue("3");
        parameter.setCompareFlag(true);
        parameter.setParameterName("test3");
        parameter.setParameterDesc("test3 Parameter");
        parameters.add(parameter);

        recipeEquipment.setRecipeEquipmentParameters(parameters);

        rmsService.saveRecipeEquipment(recipeEquipment, sessionContext);

    }

    @Test
    public void statusChangeTest() {
        AbstractRecipeEquipment recipeEquipment = abstractRecipeEquipmentRepository.getByObjectRrn(1L);
        recipeEquipment = rmsService.frozenRecipeEquipment(recipeEquipment, sessionContext);

        recipeEquipment = rmsService.activeRecipeEquipment(recipeEquipment, false, false, sessionContext);

        recipeEquipment = rmsService.inActiveRecipeEquipment(recipeEquipment, false, sessionContext);

        rmsService.unFrozenRecipeEquipment(recipeEquipment, sessionContext);
    }

    @Test
    public void holdStateChangeTest() {
        AbstractRecipeEquipment recipeEquipment = abstractRecipeEquipmentRepository.getByObjectRrn(1L);
        rmsService.holdRecipeEquipment(recipeEquipment, "hold", "1", "1", sessionContext);

        recipeEquipment = abstractRecipeEquipmentRepository.getByObjectRrn(1L);
        rmsService.releaseRecipeEquipment(recipeEquipment, "release", "2", "2", sessionContext);
    }

    @Test
    public void setGoldenTest() {
        AbstractRecipeEquipment recipeEquipment = abstractRecipeEquipmentRepository.getByObjectRrn(1L);
        if (!AbstractRecipeEquipment.STATUS_ACTIVE.equals(recipeEquipment.getStatus())) {
            recipeEquipment = rmsService.activeRecipeEquipment(recipeEquipment, false, false, sessionContext);
        }
        rmsService.setGoldenRecipe(recipeEquipment, sessionContext);
    }

    @Test
    public void unSetGoldenTest() {
        AbstractRecipeEquipment recipeEquipment = abstractRecipeEquipmentRepository.getByObjectRrn(1L);
        if (!AbstractRecipeEquipment.STATUS_ACTIVE.equals(recipeEquipment.getStatus())) {
            recipeEquipment = rmsService.activeRecipeEquipment(recipeEquipment, false, false, sessionContext);
        }
        rmsService.unSetGoldenRecipe(recipeEquipment, "aa", sessionContext);
    }

    @Test
    public void test() {
        AbstractRecipeEquipment abstractRecipeEquipment = abstractRecipeEquipmentRepository.getByObjectRrn(1L);
        System.out.println(abstractRecipeEquipment.getRecipeEquipmentParameters().size());
    }

    @Test
    public void deleteRecipeEquipmentTest() {
        rmsService.deleteRecipeEquipment(1L, sessionContext);
    }

}