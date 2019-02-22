package com.newbiest.base.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.service.VersionControlService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.SessionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by guoxunbo on 2019/2/21.
 */
@Service
@Slf4j
@Transactional
public class VersionControlServiceImpl implements VersionControlService {

    @Autowired
    BaseService baseService;

    /**
     * 失效 数据从正式环境下线
     * @param nbVersionControl
     * @param sc
     * @return
     * @throws ClientException
     */
    public NBVersionControl inactive(NBVersionControl nbVersionControl, SessionContext sc) throws ClientException {
        try {
            if (!nbVersionControl.getStatus().equals(NBVersionControl.STATUS_ACTIVE)) {
                throw new ClientException(NewbiestException.COMMON_STATUS_IS_NOT_ALLOW);
            }
            sc.buildTransInfo();
            NBHis nbHis = NBHis.getHistoryBean(nbVersionControl);

            IRepository modelRepsitory = baseService.getRepositoryByClassName(nbVersionControl.getClass().getName());
            IRepository historyRepository = null;
            if (nbHis != null) {
                historyRepository = baseService.getRepositoryByClassName(nbHis.getClass().getName());
            }

            // 只能改变状态
            NBVersionControl oldData = (NBVersionControl) modelRepsitory.findByObjectRrn(nbVersionControl.getObjectRrn());
            oldData.setStatus(NBVersionControl.STATUS_INACTIVE);
            oldData.setUpdatedBy(sc.getUsername());
            if (nbHis != null) {
                nbHis.setTransType(NBHis.TRANS_TYPE_INACTIVE);
                nbHis.setNbBase(nbVersionControl, sc);
                historyRepository.save(nbHis);
            }
            modelRepsitory.saveAndFlush(oldData);
            return nbVersionControl;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 冻结 冻结状态数据不可更改
     *  只有失效或者解冻状态是可以转到frozen状态
     * @param nbVersionControl
     * @param sc
     * @return
     * @throws ClientException
     */
    public NBVersionControl frozen(NBVersionControl nbVersionControl, SessionContext sc) throws ClientException {
        try {
            if (!nbVersionControl.getStatus().equals(NBVersionControl.STATUS_UNFROZEN) || !nbVersionControl.getStatus().equals(NBVersionControl.STATUS_INACTIVE)) {
                throw new ClientException(NewbiestException.COMMON_STATUS_IS_NOT_ALLOW);
            }
            sc.buildTransInfo();
            NBHis nbHis = NBHis.getHistoryBean(nbVersionControl);

            IRepository modelRepsitory = baseService.getRepositoryByClassName(nbVersionControl.getClass().getName());
            IRepository historyRepository = null;
            if (nbHis != null) {
                historyRepository = baseService.getRepositoryByClassName(nbHis.getClass().getName());
            }

            // 只能改变状态
            NBVersionControl oldData = (NBVersionControl) modelRepsitory.findByObjectRrn(nbVersionControl.getObjectRrn());
            oldData.setStatus(NBVersionControl.STATUS_FROZEN);
            oldData.setUpdatedBy(sc.getUsername());
            if (nbHis != null) {
                nbHis.setTransType(NBHis.TRANS_TYPE_FROZEN);
                nbHis.setNbBase(nbVersionControl, sc);
                historyRepository.save(nbHis);
            }
            modelRepsitory.saveAndFlush(oldData);
            return nbVersionControl;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 解冻 唯一一个修改数据的状态
     * 只有冻结状态才可以进行解冻
     * @param nbVersionControl
     * @param sc
     * @return
     * @throws ClientException
     */
    public NBVersionControl unFrozen(NBVersionControl nbVersionControl, SessionContext sc) throws ClientException {
        try {
            if (!nbVersionControl.getStatus().equals(NBVersionControl.STATUS_UNFROZEN)) {
                throw new ClientException(NewbiestException.COMMON_STATUS_IS_NOT_ALLOW);
            }
            sc.buildTransInfo();
            NBHis nbHis = NBHis.getHistoryBean(nbVersionControl);

            IRepository modelRepsitory = baseService.getRepositoryByClassName(nbVersionControl.getClass().getName());
            IRepository historyRepository = null;
            if (nbHis != null) {
                historyRepository = baseService.getRepositoryByClassName(nbHis.getClass().getName());
            }

            // 只能改变状态
            NBVersionControl oldData = (NBVersionControl) modelRepsitory.findByObjectRrn(nbVersionControl.getObjectRrn());
            oldData.setStatus(NBVersionControl.STATUS_UNFROZEN);
            oldData.setUpdatedBy(sc.getUsername());
            if (nbHis != null) {
                nbHis.setTransType(NBHis.TRANS_TYPE_UNFROZEN);
                nbHis.setNbBase(nbVersionControl, sc);
                historyRepository.save(nbHis);
            }
            modelRepsitory.saveAndFlush(oldData);
            return nbVersionControl;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 激活 表示数据可以进行正式投入使用
     * 相同名称只会有一个版本被激活，其他版本会自动失效
     * @param nbVersionControl
     * @param sc
     * @return
     * @throws ClientException
     */
    public NBVersionControl active(NBVersionControl nbVersionControl, SessionContext sc) throws ClientException {
        try {
            if (!nbVersionControl.getStatus().equals(NBVersionControl.STATUS_FROZEN) || !nbVersionControl.getStatus().equals(NBVersionControl.STATUS_INACTIVE)) {
                throw new ClientException(NewbiestException.COMMON_STATUS_IS_NOT_ALLOW);
            }
            sc.buildTransInfo();
            NBHis nbHis = NBHis.getHistoryBean(nbVersionControl);

            IRepository modelRepsitory = baseService.getRepositoryByClassName(nbVersionControl.getClass().getName());
            IRepository historyRepository = null;
            if (nbHis != null) {
                historyRepository = baseService.getRepositoryByClassName(nbHis.getClass().getName());
            }

            // 获取激活版本的数据将其设成Inactive
            NBVersionControl activeObject = getActiveObject(nbVersionControl, sc);
            if (activeObject != null) {
                inactive(activeObject, sc);
                modelRepsitory.save(activeObject);
            }

            // 只能改变状态
            NBVersionControl oldData = (NBVersionControl) modelRepsitory.findByObjectRrn(nbVersionControl.getObjectRrn());
            oldData.setStatus(NBVersionControl.STATUS_ACTIVE);
            oldData.setUpdatedBy(sc.getUsername());
            if (nbHis != null) {
                nbHis.setTransType(NBHis.TRANS_TYPE_ACTIVE);
                nbHis.setNbBase(nbVersionControl, sc);
                historyRepository.save(nbHis);
            }
            modelRepsitory.saveAndFlush(oldData);
            return nbVersionControl;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据versionControl的名称获取激活版本
     * 相同名称只能允许一个版本激活存在
     * @return
     * @throws ClientException
     */
    public NBVersionControl getActiveObject(NBVersionControl nbVersionControl, SessionContext sc) throws ClientException {
        try {
            List<NBVersionControl> versions = (List<NBVersionControl>) baseService.findAll(nbVersionControl.getClass().getName(), 0, 1,
                                                                                            "name = '" + nbVersionControl.getName() + "' And status ='" + NBVersionControl.STATUS_ACTIVE+ "'", "", sc.getOrgRrn());
            if (CollectionUtils.isNotEmpty(versions)) {
                return versions.get(0);
            }
            return null;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存versionControl相关的
     * 会涉及到自动升级版本的动作
     * @param nbVersionControl
     * @param sc
     * @return
     * @throws ClientException
     */
    public NBVersionControl save(NBVersionControl nbVersionControl, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            NBHis nbHis = NBHis.getHistoryBean(nbVersionControl);

            IRepository modelRepsitory = baseService.getRepositoryByClassName(nbVersionControl.getClass().getName());
            IRepository historyRepository = null;
            if (nbHis != null) {
                historyRepository = baseService.getRepositoryByClassName(nbHis.getClass().getName());
            }

            if (nbVersionControl.getObjectRrn() == null) {
                nbVersionControl.setOrgRrn(sc.getOrgRrn());
                nbVersionControl.setCreatedBy(sc.getUsername());
                nbVersionControl.setStatus(NBVersionControl.STATUS_UNFROZEN);
                Long version = getNextVersion(nbVersionControl, sc);
                nbVersionControl.setVersion(version);

                modelRepsitory.save(nbVersionControl);
                if (nbHis != null) {
                    nbHis.setTransType(NBHis.TRANS_TYPE_CREATE);
                    nbHis.setNbBase(nbVersionControl, sc);
                    historyRepository.save(nbHis);
                }
            } else {
                if (!NBVersionControl.STATUS_UNFROZEN.equals(nbVersionControl.getStatus())) {
                    throw new ClientException(NewbiestException.COMMON_STATUS_IS_NOT_ALLOW);
                }
                NBVersionControl oldData = (NBVersionControl) modelRepsitory.findByObjectRrn(nbVersionControl.getObjectRrn());
                // 不可改变状态
                nbVersionControl.setStatus(oldData.getStatus());
                nbVersionControl.setUpdatedBy(sc.getUsername());
                nbVersionControl = (NBVersionControl) modelRepsitory.saveAndFlush(nbVersionControl);

                if (nbHis != null) {
                    nbHis.setTransType(NBHis.TRANS_TYPE_UPDATE);
                    nbHis.setNbBase(nbVersionControl, sc);
                    historyRepository.save(nbHis);
                }
            }
            return nbVersionControl;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 返回根据versionControl的name栏位查找的下一个版本号
     * 如果以前存在相同名称的话，则返回下一个版本号(即最高版本+1) 不存在则返回1
     * @param nbVersionControl
     * @param sc
     * @return
     * @throws ClientException
     */
    public Long getNextVersion(NBVersionControl nbVersionControl, SessionContext sc) throws ClientException {
        try {
            List<NBVersionControl> versions = (List<NBVersionControl>) baseService.findAll(nbVersionControl.getClass().getName(), 0, 1, "name = '" + nbVersionControl.getName() + "'", "", sc.getOrgRrn());
            if (CollectionUtils.isNotEmpty(versions)) {
                return versions.get(0).getVersion() + 1;
            }
            return 1L;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
}
