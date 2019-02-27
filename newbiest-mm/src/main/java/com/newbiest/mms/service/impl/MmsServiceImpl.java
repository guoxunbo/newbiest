package com.newbiest.mms.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.model.NBVersionControlHis;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.service.VersionControlService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.commom.sm.exception.StatusMachineExceptions;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.commom.sm.service.StatusMachineService;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotHistory;
import com.newbiest.mms.model.RawMaterial;
import com.newbiest.mms.repository.MaterialLotHistoryRepository;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.mms.repository.MaterialStatusModelRepository;
import com.newbiest.mms.repository.RawMaterialRepository;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.state.model.MaterialStatusModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by guoxunbo on 2019/2/13.
 */
@Service
@Slf4j
@Transactional
public class MmsServiceImpl implements MmsService {

    @Autowired
    BaseService baseService;

    @Autowired
    VersionControlService versionControlService;

    @Autowired
    RawMaterialRepository rawMaterialRepository;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialLotHistoryRepository materialLotHistoryRepository;

    @Autowired
    MaterialStatusModelRepository materialStatusModelRepository;

    @Autowired
    StatusMachineService statusMachineService;

    @Autowired
    GeneratorService generatorService;

    /**
     * 根据名称获取源物料。
     *  源物料不区分版本。故此处只会有1个
     * @param name 名称
     * @param sc
     * @return
     * @throws ClientException
     */
    public RawMaterial getRawMaterialByName(String name, SessionContext sc) throws ClientException {
        try {
            List<RawMaterial> rawMaterialList = (List<RawMaterial>) rawMaterialRepository.findByNameAndOrgRrn(name, sc.getOrgRrn());
            if (CollectionUtils.isNotEmpty(rawMaterialList)) {
                return rawMaterialList.get(0);
            }
            return null;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存源物料。此处和versionControlService的save不同点在于
     * 1. 保存的时候直接激活，故物料只会是一个版本
     * 2. 激活状态允许修改数据
     * @param rawMaterial
     * @param sc
     * @return
     * @throws ClientException
     */
    public RawMaterial saveRawMaterial(RawMaterial rawMaterial, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            NBHis nbHis = NBHis.getHistoryBean(rawMaterial);

            IRepository modelRepsitory = baseService.getRepositoryByClassName(rawMaterial.getClass().getName());
            IRepository historyRepository = null;
            if (nbHis != null) {
                historyRepository = baseService.getRepositoryByClassName(nbHis.getClass().getName());
            }

            if (rawMaterial.getObjectRrn() == null) {
                rawMaterial.setOrgRrn(sc.getOrgRrn());
                rawMaterial.setCreatedBy(sc.getUsername());
                rawMaterial.setUpdatedBy(sc.getUsername());
                rawMaterial.setStatus(NBVersionControl.STATUS_ACTIVE);
                Long version = versionControlService.getNextVersion(rawMaterial, sc);
                rawMaterial.setVersion(version);

                rawMaterial = (RawMaterial) modelRepsitory.saveAndFlush(rawMaterial);
                if (nbHis != null) {
                    nbHis.setTransType(NBVersionControlHis.TRANS_TYPE_CREATE_AND_ACTIVE);
                    nbHis.setNbBase(rawMaterial, sc);
                    historyRepository.save(nbHis);
                }
            } else {
                NBVersionControl oldData = (NBVersionControl) modelRepsitory.findByObjectRrn(rawMaterial.getObjectRrn());
                // 不可改变状态
                rawMaterial.setStatus(oldData.getStatus());
                rawMaterial.setUpdatedBy(sc.getUsername());
                rawMaterial = (RawMaterial) modelRepsitory.saveAndFlush(rawMaterial);

                if (nbHis != null) {
                    nbHis.setTransType(NBHis.TRANS_TYPE_UPDATE);
                    nbHis.setNbBase(rawMaterial, sc);
                    historyRepository.save(nbHis);
                }
            }
            return rawMaterial;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收物料批次并入库指定物料批次号
     * 如果没有指定入的仓库，则直接入到物料上绑定的仓库
     * @param rawMaterial 原物料
     * @param mLotId 物料批次号
     * @param qty 数量
     * @param sc
     * @return
     */
    public MaterialLot receiveMLot2Warehouse(RawMaterial rawMaterial, String mLotId, BigDecimal qty, SessionContext sc) {
        try {
            MaterialLot materialLot = createMLot(rawMaterial, mLotId, qty, sc);


            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收物料批次
     * @param rawMaterial 原物料
     * @param mLotId 物料批次号
     * @param qty 数量
     * @param sc
     * @return
     */
    public MaterialLot receiveMLot(RawMaterial rawMaterial, String mLotId, BigDecimal qty, SessionContext sc) {
        try {
            MaterialLot materialLot = createMLot(rawMaterial, mLotId, qty, sc);

            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收物料批次并入库
     * 如果没有指定入的仓库，则直接入到物料上绑定的仓库
     * @param rawMaterial
     * @param sc
     * @return
     */
    public MaterialLot receiveMLot2Warehouse(RawMaterial rawMaterial, BigDecimal qty, SessionContext sc) {
        try {
            return receiveMLot2Warehouse(rawMaterial, StringUtils.EMPTY, qty, sc);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialLot getMLotByMLotId(String mLotId, SessionContext sc) throws ClientException{
        try {
            return materialLotRepository.findByMaterialLotIdAndOrgRrn(mLotId, sc.getOrgRrn());
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 创建物料批次
     * @param rawMaterial 源物料
     * @param  mLotId 物料批次号。当为空的时候，按照设定的物料批次号生成规则进行生成
     * @return
     * @throws ClientException
     */
    public MaterialLot createMLot(RawMaterial rawMaterial, String mLotId, BigDecimal transQty, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            if (StringUtils.isNullOrEmpty(mLotId)) {
                mLotId = generatorMLotId(rawMaterial, sc);
            }
            MaterialLot materialLot = getMLotByMLotId(mLotId, sc);
            if (materialLot != null) {
                throw new ClientException(MmsException.MM_MATERIAL_LOT_IS_EXIST);
            }
            materialLot = new MaterialLot();
            materialLot.setActiveFlag(true);
            materialLot.setOrgRrn(sc.getOrgRrn());
            materialLot.setCreatedBy(sc.getUsername());
            materialLot.setUpdatedBy(sc.getUsername());
            materialLot.setMaterialLotId(mLotId);

            if (rawMaterial.getStatusModelRrn() == null) {
                List<MaterialStatusModel> statusModels = (List<MaterialStatusModel>) materialStatusModelRepository.findByNameAndOrgRrn(Material.DEFAULT_STATUS_MODEL, sc.getOrgRrn());
                if (CollectionUtils.isNotEmpty(statusModels)) {
                    rawMaterial.setStatusModelRrn(statusModels.get(0).getObjectRrn());
                } else {
                    throw new ClientException(StatusMachineExceptions.STATUS_MODEL_IS_NOT_EXIST);
                }
            }
            materialLot.setStatusModelRrn(rawMaterial.getStatusModelRrn());

            StatusModel statusModel = statusMachineService.getStatusModelByObjectRrn(rawMaterial.getStatusModelRrn());
            materialLot.setStatusCategory(statusModel.getInitialStateCategory());
            materialLot.setStatus(statusModel.getInitialState());
            materialLot.setCurrentQty(transQty);
            materialLot.setMaterialRrn(rawMaterial.getObjectRrn());
            materialLot.setMaterialVersion(rawMaterial.getVersion());
            materialLot.setMaterialDesc(rawMaterial.getDescription());
            materialLot.setMaterialType(rawMaterial.getMaterialType());
            materialLot.setStoreUom(rawMaterial.getStoreUom());

            materialLot = materialLotRepository.saveAndFlush(materialLot);

            // 记录历史
            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE, sc);
            history.setTransQty(materialLot.getCurrentQty());
            materialLotHistoryRepository.save(history);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据ID生成规则设置的生成物料批次号
     * @return
     * @throws ClientException
     */
    public String generatorMLotId(RawMaterial rawMaterial, SessionContext sc) throws ClientException{
        try {
            GeneratorContext generatorContext = new GeneratorContext();
            generatorContext.setRuleName(MaterialLot.GENERATOR_MATERIAL_LOT_ID_RULE);
            String id = generatorService.generatorId(sc.getOrgRrn(), generatorContext);
            return id;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据StateMachine修改物料批次的状态
     * @param mLot 物料批次
     * @param eventId 触发的事件 不可为空
     * @param targetState 目标状态。因为一个事件可能有多个目标状态可强行指定转到具体的状态。不指定则以event上优先级最高的来当状态
     * @param sc
     * @return
     * @throws ClientException
     */
    public MaterialLot changeMaterialLotState(MaterialLot mLot, String eventId, String targetState, SessionContext sc) throws ClientException {
        try {
            mLot.setPreStatus(mLot.getStatus());

            
            mLot = materialLotRepository.saveAndFlush(mLot);
            return mLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
}
