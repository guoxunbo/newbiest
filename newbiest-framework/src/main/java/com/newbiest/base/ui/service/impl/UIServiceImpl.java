package com.newbiest.base.ui.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.repository.TableRepository;
import com.newbiest.base.ui.model.NBField;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.ExcelUtils;
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

    /**
     * 页面上点击authority的时候触发
     * @param authorityRrn
     * @return 返回带有所有栏位以及TAB的NBTABLE
     * @throws ClientException
     */
    public NBTable getNBTableByAuthority(Long authorityRrn) throws ClientException {
        try {
            NBAuthority nbAuthority = authorityRepository.getByObjectRrn(authorityRrn);
            if (nbAuthority == null) {
//                throw new ClientException(SecurityException.AUTHORITY_IS_NULL);
            }
            return tableRepository.getDeepTable(nbAuthority.getTableRrn());
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
            NBTable nbTable = tableRepository.getDeepTable(tableRrn);
            // 组成MAP 形式为LABEL/LABEL_ZH, name
            Map<String, String> headersMapped = null;
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
