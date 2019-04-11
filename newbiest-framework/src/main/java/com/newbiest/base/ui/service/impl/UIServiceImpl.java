package com.newbiest.base.ui.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.ui.exception.UIExceptions;
import com.newbiest.base.ui.model.*;
import com.newbiest.base.ui.repository.OwnerReferenceListRepository;
import com.newbiest.base.ui.repository.ReferenceTableRepository;
import com.newbiest.base.ui.repository.SystemReferenceListRepository;
import com.newbiest.base.ui.repository.TableRepository;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.ExcelUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.security.model.NBAuthority;
import com.newbiest.security.repository.AuthorityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2018/4/18.
 */
@Component
@Slf4j
public class UIServiceImpl implements UIService {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private BaseService baseService;

    @Autowired
    private OwnerReferenceListRepository ownerReferenceListRepository;

    @Autowired
    private SystemReferenceListRepository systemReferenceListRepository;

    @Autowired
    private ReferenceTableRepository referenceTableRepository;

    /**
     * 根据菜单获取动态表
     * @param authorityRrn
     * @return 返回带有所有栏位以及TAB的NBTable
     * @throws ClientException
     */
    public NBTable getNBTableByAuthority(Long authorityRrn) throws ClientException {
        try {
            NBAuthority nbAuthority = (NBAuthority) authorityRepository.findByObjectRrn(authorityRrn);
            return getDeepNBTable(nbAuthority.getTableRrn());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    @Override
    public NBTable getNBTableByName(String tableName, long orgRrn) throws ClientException {
        List<NBTable> tables = (List<NBTable>) tableRepository.findByNameAndOrgRrn(tableName, orgRrn);

        if (CollectionUtils.isNotEmpty(tables)) {
            return tables.get(0);
        }
        throw new ClientParameterException(UIExceptions.UI_TABLE_NOT_EXIST, tableName);
    }

    /**
     * 根据主键获取动态表
     * @param tableRrn 主键
     * @return 返回带有所有栏位以及TAB的NBTable
     * @throws ClientException
     */
    public NBTable getDeepNBTable(Long tableRrn) throws ClientException {
        try {
            NBTable nbTable = new NBTable();
            nbTable.setObjectRrn(tableRrn);

            nbTable = (NBTable) baseService.findEntity(nbTable, true);
            return nbTable;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 根据tableRrn获取数据 覆盖Table上的whereClause和orderBy
     * @param tableRrn NBTable的主键
     * @param whereClause 查询条件
     * @param orderBy 排序条件
     * @return
     * @throws ClientException
     */
    public List<? extends NBBase> getDataFromTableRrn(Long tableRrn, String whereClause, String orderBy, SessionContext sc) throws ClientException {
        try {
            NBTable nbTable = (NBTable) tableRepository.findByObjectRrn(tableRrn);
            // 没传递查询条件 则默认使用InitWhereClause进行查询
            if (StringUtils.isNullOrEmpty(whereClause)) {
                whereClause = nbTable.getInitWhereClause();
            } else {
                if (!StringUtils.isNullOrEmpty(nbTable.getWhereClause())) {
                    StringBuffer clauseBuffer = new StringBuffer(whereClause);
                    clauseBuffer.append(" AND ");
                    clauseBuffer.append(nbTable.getWhereClause());
                    whereClause = clauseBuffer.toString();
                }
            }

            if (!nbTable.getView()) {
                List<? extends NBBase> datas = baseService.findAll(nbTable.getModelClass(), whereClause, orderBy, sc.getOrgRrn());
                return datas;
            } else {
                //TODO 暂时只支持实体查询不支持直接SQL查询
                throw new ClientException(UIExceptions.UI_TABLE_NON_SUPPORT_VIEW);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 获取栏位参考值
     * @param referenceName 参考名称
     * @param category 类型 系统栏位参考值还是用户栏位参考值
     * @return
     * @throws ClientException
     */
    public List<? extends NBReferenceList> getReferenceList(String referenceName, String category, SessionContext sc) throws ClientException {
        try {
            if (NBReferenceList.CATEGORY_SYSTEM.equals(category)) {
                return getSystemReferenceList(referenceName);
            } else if (NBReferenceList.CATEGORY_OWNER.equals(category)) {
                return getOwnerReferenceList(referenceName, sc);
            } else {
                throw new ClientParameterException(UIExceptions.UI_REF_LIST_NONSUPPORT_CATEGORY, category);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 获取系统栏位参考值
     * @param referenceName 参考名称
     * @return
     * @throws ClientException
     */
    public List<NBSystemReferenceList> getSystemReferenceList(String referenceName) throws ClientException {
        try {
            return systemReferenceListRepository.findByReferenceName(referenceName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 根据区域获取用户栏位参考值
     * @param referenceName 参考名称
     * @return
     * @throws ClientException
     */
    public List<NBOwnerReferenceList> getOwnerReferenceList(String referenceName, SessionContext sc) throws ClientException {
        try {
            return ownerReferenceListRepository.findByReferenceNameAndOrgRrn(referenceName, sc.getOrgRrn());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public NBReferenceTable getReferenceTableByName(String name, SessionContext sc) throws ClientException {
        try {
            List<NBReferenceTable> referenceTables = (List<NBReferenceTable>) referenceTableRepository.findByNameAndOrgRrn(name, sc.getOrgRrn());
            if (CollectionUtils.isNotEmpty(referenceTables)) {
                return referenceTables.get(0);
            } else {
                throw new ClientParameterException(UIExceptions.UI_REF_TABLE_NOT_EXIST, name);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 导入数据
     * @param tableRrn table主键
     * @param titleCh 标题是否是中文/必须是field的LABEL_ZH
     * @param inputStream 文件输入源
     */
    public List importData(Long tableRrn, InputStream inputStream, boolean titleCh) throws ClientException {
        try {
            return importData(tableRrn, titleCh, inputStream, ExcelUtils.DEFAULT_DATE_PATTERN);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 导入数据
     * @param tableRrn table主键
     * @param titleCh 标题是否是中文必须是field的LABEL_ZH
     * @param inputStream 文件输入源
     * @param datePattern 日期格式 默认为yyyy-MM-dd HH:mm:ss:SSS
     */
    public List importData(Long tableRrn, boolean titleCh, InputStream inputStream, String datePattern) throws ClientException {
        try {
            NBTable nbTable = new NBTable();
            nbTable.setObjectRrn(tableRrn);
            nbTable = (NBTable) baseService.findEntity(nbTable, true);
            // 组成MAP 形式为LABEL/LABEL_ZH, name
            Map<String, String> headersMapped;
            if (titleCh) {
                headersMapped =  nbTable.getFields().stream().filter(nbField -> nbField.getMainFlag()).collect(Collectors.toConcurrentMap(NBField :: getLabelZh, NBField :: getName));
            } else {
                headersMapped =  nbTable.getFields().stream().filter(nbField -> nbField.getMainFlag()).collect(Collectors.toConcurrentMap(NBField :: getLabel, NBField :: getName));
            }

            List dataList = (List) ExcelUtils.importExcel(Class.forName(nbTable.getModelClass()), headersMapped, inputStream, datePattern);

            return dataList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

}
