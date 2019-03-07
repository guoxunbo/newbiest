package com.newbiest.base.service.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.model.*;
import com.newbiest.base.repository.MessageRepository;
import com.newbiest.base.repository.OrgRepository;
import com.newbiest.base.repository.QueryRepository;
import com.newbiest.base.repository.RelationRepository;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.ui.model.*;
import com.newbiest.base.utils.*;
import com.newbiest.main.NewbiestConfiguration;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.model.NBOrg;
import com.newbiest.security.model.NBUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by guoxunbo on 2018/6/6.
 */
@Service
@Slf4j
@Transactional
@DependsOn("applicationContextProvider")
public class BaseServiceImpl implements BaseService  {

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private List<IRepository> repositories;

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private NewbiestConfiguration newbiestConfiguration;

    @PostConstruct
    public void init() {
        loadMessages();
    }


    /**
     * 加载所有国际化
     * @throws ClientException
     */
    private void loadMessages() throws ClientException {
        List<NBMessage> nbMessages = (List<NBMessage>) messageRepository.findAll(NBOrg.GLOBAL_ORG_RRN);
        NBMessage.putAll(nbMessages);
    }

    /**
     * 根据名称获取区域
     * @param name 名称
     * @return
     * @throws ClientException
     */
    public NBOrg findOrgByName(String name) throws ClientException {
        List<NBOrg> orgs = (List<NBOrg>) orgRepository.findByNameAndOrgRrn(name, NBOrg.GLOBAL_ORG_RRN);
        return orgs.get(0);
    }

    /**
     * 根据主键获取区域
     * @param objectRrn 主键
     * @return
     * @throws ClientException
     */
    public NBOrg findOrgByObjectRrn(Long objectRrn) throws ClientException {
        return (NBOrg) orgRepository.findByObjectRrn(objectRrn);
    }

    /**
     * 查找Class相应的区域下的所有数据
     * @param fullClassName class全名 如com.newbiest.ui.model.NBTable
     * @param orgRrn 区域号
     * @return
     * @throws ClientException
     */
    public List<? extends NBBase> findAll(String fullClassName, long orgRrn) throws ClientException {
        try {
            return findAll(fullClassName, StringUtils.EMPTY, StringUtils.EMPTY, orgRrn);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 查找Class相应的区域下的所有数据
     * @param fullClassName class全名 如com.newbiest.ui.model.NBTable
     * @param orgRrn 区域号
     * @param whereClause where语句 如name = 'Test'
     * @param orderBy 排序语句 如 name desc
     * @return
     * @throws ClientException
     */
    public List<? extends NBBase> findAll(String fullClassName, String whereClause, String orderBy, long orgRrn) throws ClientException {
        try {
            return findAll(fullClassName, 0, newbiestConfiguration.getQueryMaxCount(), whereClause, orderBy, orgRrn);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 查找Class相应的区域下的所有数据
     * @param fullClassName class全名 如com.newbiest.ui.model.NBTable
     * @param orgRrn 区域号
     * @param firstResult 起始数据下标
     * @param maxResult 返回最大数据量
     * @param whereClause where语句 如name = 'Test'
     * @param orderBy 排序语句 如 name desc
     * @return
     * @throws ClientException
     */
    public List<? extends NBBase> findAll(String fullClassName, int firstResult, int maxResult, String whereClause, String orderBy, long orgRrn) throws ClientException {
        try {
            IRepository repository = getRepositoryByClassName(fullClassName);
            List<NBBase> nbBases = repository.findAll(orgRrn, firstResult, maxResult, whereClause, orderBy);
            return nbBases;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 保存对象集合
     * @param nbBaseList
     * @param sc
     * @return
     * @throws ClientException
     */
    public List<? extends NBBase> saveEntity(List<? extends NBBase> nbBaseList, SessionContext sc) throws ClientException {
        try {
            List<NBBase> data = Lists.newArrayList();

            if (CollectionUtils.isNotEmpty(nbBaseList)) {
                for (NBBase nbBase : nbBaseList) {
                    data.add(saveEntity(nbBase, sc));
                }
            }
            return data;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }



    /**
     * 保存对象
     * 更新时候 不会更新关联的对象 只会更新自己本身的属性
     * @param nbBase 对象
     * @param sc
     * @return
     * @throws ClientException
     */
    public NBBase saveEntity(NBBase nbBase, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();

            IRepository modelRepsitory = getRepositoryByClassName(nbBase.getClass().getName());
            if (nbBase.getObjectRrn() != null) {
                if (nbBase instanceof NBUpdatable) {
                    ((NBUpdatable) nbBase).setUpdatedBy(sc.getUsername());
                }
                nbBase = (NBBase) modelRepsitory.saveAndFlush(nbBase);
                saveHistoryEntity(nbBase, NBHis.TRANS_TYPE_UPDATE, sc);
            } else {
                nbBase.setOrgRrn(sc.getOrgRrn());
                if (nbBase instanceof NBTable || nbBase instanceof NBTab || nbBase instanceof NBField
                        || nbBase instanceof NBReferenceTable || nbBase instanceof NBSystemReferenceName
                        || nbBase instanceof NBMessage || nbBase instanceof NBUser || nbBase instanceof NBAuthority) {
                    nbBase.setOrgRrn(NBOrg.GLOBAL_ORG_RRN);
                }
                if (nbBase instanceof NBUpdatable) {
                    ((NBUpdatable) nbBase).setCreatedBy(sc.getUsername());
                }
                nbBase = (NBBase) modelRepsitory.saveAndFlush(nbBase);
                saveHistoryEntity(nbBase, NBHis.TRANS_TYPE_CREATE, sc);
            }
            return nbBase;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 保存历史
     * @param nbBase 实体
     * @param transType 事务类型
     * @param sc
     * @throws ClientException
     */
    public void saveHistoryEntity(NBBase nbBase, String transType, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            NBHis nbHis = buildHistoryBean(nbBase, transType, sc);
            IRepository historyRepository = null;
            if (nbHis != null) {
                historyRepository = getRepositoryByClassName(nbHis.getClass().getName());
                historyRepository.save(nbHis);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 返回历史对象
     *  此时的历史对象不具备action code等信息
     * @param nbBase
     * @param sc
     * @return
     * @throws ClientException
     */
    public NBHis buildHistoryBean(NBBase nbBase, String transType, SessionContext sc) throws ClientException {
        try {
            NBHis nbHis = NBHis.getHistoryBean(nbBase);
            if (nbHis != null) {
                nbHis.setTransType(transType);
                nbHis.setNbBase(nbBase, sc);
            }
            return nbHis;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 删除实体
     * @param nbBase 包含了主键的实体
     * @param sc
     * @throws ClientException
     */
    public void delete(NBBase nbBase, SessionContext sc) throws ClientException {
        this.delete(nbBase, false, sc);
    }

    /**
     *
     * @param nbBase
     * @param sc
     * @throws ClientException
     */
    public void delete(NBBase nbBase, boolean deleleRelationFlag, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();

            NBHis nbHis = NBHis.getHistoryBean(nbBase);
            IRepository modelRepsitory = getRepositoryByClassName(nbBase.getClass().getName());

            nbBase = modelRepsitory.findByObjectRrn(nbBase.getObjectRrn());
            if (nbHis != null) {
                IRepository historyRepository = getRepositoryByClassName(nbHis.getClass().getName());
                if (nbHis != null) {
                    nbHis.setTransType(NBHis.TRANS_TYPE_DELETE);
                    nbHis.setNbBase(nbBase, sc);
                    historyRepository.save(nbHis);
                }
            }
            deleteRelationObject(nbBase, deleleRelationFlag);

            modelRepsitory.delete(nbBase);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 删除关联对象
     * @param nbBase
     * @param deleteFlag
     * @throws ClientException
     */
    public void deleteRelationObject(NBBase nbBase, boolean deleteFlag) throws ClientException {
        try {
            List<NBRelation> relations = relationRepository.findBySource(nbBase.getClass().getName());
            if (CollectionUtils.isNotEmpty(relations)) {
                nbBase = getRepositoryByClassName(nbBase.getClass().getName()).findByObjectRrn(nbBase.getObjectRrn());
                for (NBRelation relation : relations) {
                    List relationObjects = Lists.newArrayList();
                    if (NBRelation.RELATION_TYPE_CLASS.equals(relation.getRelationType())) {
                        IRepository relationObjectRepository = getRepositoryByClassName(relation.getTarget());
                        if (deleteFlag) {
                            relationObjectRepository.delete(relation.getWhereClause(nbBase));
                        }
                    } else if (NBRelation.RELATION_TYPE_SQL.equals(relation.getRelationType())) {
                        //TODO 暂时不支持SQL删除关联关系
                    } else {
                        throw new ClientParameterException(NewbiestException.COMMON_NONSUPPORT_RELATION_TYPE, relation.getRelationType());
                    }

                    if (CollectionUtils.isNotEmpty(relationObjects)) {
                        throw new ClientParameterException(NewbiestException.COMMON_RELATION_OBJECT_IS_EXIST, relation.getTarget());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 根据ObjectRrn取得相应的实体 并根据deepFlag加载出所有的懒加载对象
     * @param nbBase 实体必带objectRrn
     * @param deepFlag 是否加载懒加载对象
     * @throws ClientException
     */
    public NBBase findEntity(NBBase nbBase, boolean deepFlag) throws ClientException{
        try {
            IRepository modelRepository = getRepositoryByClassName(nbBase.getClass().getName());
            nbBase = modelRepository.getEntityManager().find(nbBase.getClass(), nbBase.getObjectRrn());
            if (deepFlag) {
                PropertyDescriptor[] descriptors = org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors(nbBase);
                if (descriptors != null && descriptors.length > 0) {
                    for (PropertyDescriptor descriptor : descriptors) {
                        Class clazz = descriptor.getPropertyType();
                        if (List.class.isAssignableFrom(clazz)) {
                            List list = (List) PropertyUtils.getProperty(nbBase, descriptor.getName());
                            if (list != null) {
                                list.size();
                            }
                        }
                    }
                }
            }
            return nbBase;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 根据NBquery中定义的SQL语句进行以返回Map的形式查询返回
     * @param queryName NBQuery名称
     * @param paramMap WhereClause的参数值
     * @param firstResult 起始
     * @param maxResult 最大返回数据
     * @param whereClause 查询条件
     * @param orderByClause 排序条件
     * @return
     * @throws ClientException
     */
    @Override
    public List<Map> findEntityMapListByQueryName(String queryName, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause, SessionContext sc) throws ClientException {
        try {
            NBQuery nbQuery = queryRepository.findByName(queryName);
            if (nbQuery == null) {
                throw new ClientParameterException(NewbiestException.COMMON_QUERY_IS_NOT_EXIST, queryName);
            }
            return findEntityMapListByQueryText(nbQuery.getQueryText(), paramMap, firstResult, maxResult, whereClause, orderByClause);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 根据原生态SQL语句进行以返回Map的形式查询返回
     * @param queryText 原生态SQL语句
     * @param paramMap WhereClause的参数值
     * @param firstResult 起始
     * @param maxResult 最大返回数据
     * @param whereClause 查询条件
     * @param orderByClause 排序条件
     * @return
     * @throws ClientException
     */
    public List<Map> findEntityMapListByQueryText(String queryText, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause) throws ClientException{
        try {
            return queryRepository.findEntityMapListByQueryText(queryText, paramMap, firstResult, maxResult, whereClause, orderByClause);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 根据class名称找到相应的repository
     * @param fullClassName
     * @return
     * @throws ClientException
     */
    public IRepository getRepositoryByClassName(String fullClassName) throws ClientException{
        try {
            Optional<IRepository> optional = repositories.stream().filter(repository -> repository.support(fullClassName)).findFirst();
            if (optional.isPresent()) {
               return optional.get();
            } else {
                throw new ClientParameterException(NewbiestException.COMMON_REPOSITORY_IS_NOT_EXIST, fullClassName);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }


}
