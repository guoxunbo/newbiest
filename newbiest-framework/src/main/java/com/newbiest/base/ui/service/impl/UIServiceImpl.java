package com.newbiest.base.ui.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.ui.exception.UIExceptions;
import com.newbiest.base.ui.model.NBField;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.repository.TableRepository;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.ExcelUtils;
import com.newbiest.base.utils.SessionContext;
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

    /**
     * 页面上点击authority的时候触发
     * @param authorityRrn
     * @return 返回带有所有栏位以及TAB的NBTABLE
     * @throws ClientException
     */
    public NBTable getNBTableByAuthority(Long authorityRrn) throws ClientException {
        try {
            NBAuthority nbAuthority = (NBAuthority) authorityRepository.findByObjectRrn(authorityRrn);
            NBTable nbTable = new NBTable();
            nbTable.setObjectRrn(nbAuthority.getTableRrn());

            nbTable = (NBTable) baseService.findEntity(nbTable);
            return nbTable;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 根据tableRrn获取数据
     * @return
     * @throws ClientException
     */
    public List<? extends NBBase> getDataFromTableRrn(Long tableRrn, SessionContext sc) throws ClientException {
        try {
            NBTable nbTable = (NBTable) tableRepository.findByObjectRrn(tableRrn);
            if (nbTable == null) {
                throw new ClientParameterException(UIExceptions.UI_TABLE_NOT_EXIST, tableRrn);
            }
            if (!nbTable.getView()) {
                List<? extends NBBase> datas = baseService.findAll(nbTable.getModelClass(), sc.getOrgRrn(), nbTable.getInitWhereClause(), nbTable.getOrderByClause());
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
            nbTable = (NBTable) baseService.findEntity(nbTable);
            // 组成MAP 形式为LABEL/LABEL_ZH, name
            Map<String, String> headersMapped;
            if (titleCh) {
                headersMapped =  nbTable.getFields().stream().filter(nbField -> nbField.getExportFlag()).collect(Collectors.toConcurrentMap(NBField :: getLabelZh, NBField :: getName));
            } else {
                headersMapped =  nbTable.getFields().stream().filter(nbField -> nbField.getExportFlag()).collect(Collectors.toConcurrentMap(NBField :: getLabel, NBField :: getName));
            }

            List dataList = (List) ExcelUtils.importExcel(Class.forName(nbTable.getModelClass()), headersMapped, inputStream, datePattern);

            return dataList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

}
