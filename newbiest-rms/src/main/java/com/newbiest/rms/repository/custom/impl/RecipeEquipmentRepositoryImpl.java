package com.newbiest.rms.repository.custom.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.sql.SQLBuilder;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.DefaultStatusMachine;
import com.newbiest.base.utils.SqlUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.rms.exception.RmsException;
import com.newbiest.rms.model.RecipeEquipment;
import com.newbiest.rms.model.Equipment;
import com.newbiest.rms.repository.EquipmentRepository;
import com.newbiest.rms.repository.custom.RecipeEquipmentRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

/**
 * Created by guoxunbo on 2018/7/4.
 */
@Slf4j
public class RecipeEquipmentRepositoryImpl implements RecipeEquipmentRepositoryCustom {

    @Autowired
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EquipmentRepository equipmentRepository;

    /**
     * 根据recipeName + equipmentId + equipmentType + pattern进行查找RecipeEquipment
     * @param orgRrn
     * @param recipeName
     * @param equipmentId
     * @param equipmentType
     * @param pattern
     * @return
     * @throws ClientException
     */
    public List<RecipeEquipment> getRecipeEquipment(long orgRrn, String recipeName, String equipmentId, String equipmentType, String pattern) throws ClientException {
        try {
            StringBuffer sqlBuffer = SQLBuilder.newInstance().selectEntity(RecipeEquipment.class)
                    .mapFieldValue(ImmutableMap.of("recipeName", recipeName, "pattern", pattern))
                    .build();
            sqlBuffer.append(" AND ");
            if (!StringUtils.isNullOrEmpty(equipmentId)) {
                sqlBuffer.append(" equipmentId = " + SqlUtils.getValueByType(equipmentId));
            } else {
                sqlBuffer.append(" equipmentType = " + SqlUtils.getValueByType(equipmentType));
            }
            sqlBuffer.append(" ORDER BY ");
            sqlBuffer.append(" version desc");

            Query query = em.createQuery(sqlBuffer.toString());

            query.setParameter("orgRrn", orgRrn);
            List<RecipeEquipment> list = query.getResultList();
            if (CollectionUtils.isNotEmpty(list)) {
                return list;
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public RecipeEquipment getGoldenRecipe(long orgRrn, String eqpType, String recipeName, String status, String pattern, boolean bodyFlag) throws ClientException {
        try {
            StringBuffer sqlBuffer = SQLBuilder.newInstance().selectEntity(RecipeEquipment.class)
                    .mapFieldValue(ImmutableMap.of("goldenFlag", "Y"))
                    .build();
            sqlBuffer.append(" AND equipmentType = :equipmentType");
            sqlBuffer.append(" AND recipeName = :recipeName");
            sqlBuffer.append(" AND pattern = :pattern");
            if (!StringUtils.isNullOrEmpty(status)) {
                sqlBuffer.append(" AND status = :status");
            }
            Query query = em.createQuery(sqlBuffer.toString());
            query.setParameter("orgRrn", orgRrn);
            query.setParameter("equipmentType", eqpType);
            query.setParameter("recipeName", recipeName);
            query.setParameter("pattern", pattern);
            if (!StringUtils.isNullOrEmpty(status)) {
                query.setParameter("status", status);
            }
            List<RecipeEquipment> goldenRecipeList = query.getResultList();
            if (goldenRecipeList != null && goldenRecipeList.size() > 0) {
                if (goldenRecipeList.size() > 1) {
                    throw new ClientException(RmsException.EQP_GOLDEN_RECIPE_IS_MULTI);
                } else {
                    RecipeEquipment RecipeEquipment =  goldenRecipeList.get(0);
                    if (bodyFlag) {
                        RecipeEquipment.getRecipeEquipmentParameters().size();
                    }
                    return RecipeEquipment;
                }
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 根据主键查找，查询相应的Parameter一起返回
     * @param objectRrn 主键
     * @return
     * @throws ClientException
     */
    public RecipeEquipment getDeepRecipeEquipment(long objectRrn) throws ClientException{
        try {
            EntityGraph graph = em.createEntityGraph(RecipeEquipment.class);
            graph.addSubgraph("recipeEquipmentParameters");
            Map<String, Object> props = Maps.newHashMap();
            props.put(NBBase.LAZY_FETCH_PROP, graph);

            RecipeEquipment RecipeEquipment = em.find(RecipeEquipment.class, objectRrn, props);
            return RecipeEquipment;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public RecipeEquipment getActiveRecipeEquipment(long orgRrn, String recipeName, String equipmentId, String pattern, boolean bodyFlag) throws ClientException {
        try {
            Equipment equipment = equipmentRepository.getByEquipmentId(equipmentId);
            if (equipment == null) {
                throw new ClientException(RmsException.EQP_IS_NOT_EXIST);
            }

            StringBuffer sqlBuffer = SQLBuilder.newInstance().selectEntity(RecipeEquipment.class)
                    .mapFieldValue(ImmutableMap.of("status", DefaultStatusMachine.STATUS_ACTIVE))
                    .build();
            sqlBuffer.append(" AND recipeName = :recipeName");
            sqlBuffer.append(" AND equipmentId = :equipmentId");
            sqlBuffer.append(" AND pattern = :pattern");
            Query query = em.createQuery(sqlBuffer.toString());
            query.setParameter("orgRrn", orgRrn);
            query.setParameter("recipeName", recipeName);
            query.setParameter("equipmentId", equipmentId);
            if (StringUtils.isNullOrEmpty(pattern)) {
                query.setParameter("pattern", RecipeEquipment.PATTERN_NORMAL);
            } else {
                query.setParameter("pattern", pattern);
            }
            List<RecipeEquipment> activeRecipeEquipments = query.getResultList();
            if (activeRecipeEquipments != null && activeRecipeEquipments.size() > 0) {
                if (bodyFlag) {
                    RecipeEquipment activeRecipeEquipment = activeRecipeEquipments.get(0);
                    activeRecipeEquipment.getRecipeEquipmentParameters().size();
                    return activeRecipeEquipment;
                }
                return activeRecipeEquipments.get(0);
            } else {
//                // 如果没找到，则去GoldenRecipe上去找
                if (!StringUtils.isNullOrEmpty(equipment.getEquipmentType())) {
                    RecipeEquipment RecipeEquipment = getGoldenRecipe(orgRrn, equipment.getEquipmentType(), recipeName, DefaultStatusMachine.STATUS_ACTIVE, pattern, bodyFlag);
                    return RecipeEquipment;
                }
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }
}
