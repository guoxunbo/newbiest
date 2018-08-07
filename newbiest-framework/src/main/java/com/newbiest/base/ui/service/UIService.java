package com.newbiest.base.ui.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.ui.model.NBReferenceTable;
import com.newbiest.base.ui.model.NBSystemReferenceList;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.utils.SessionContext;

import java.util.List;

/**
 * 获取数据生成UI页面的操作
 * Created by guoxunbo on 2018/4/18.
 */
public interface UIService {

    // table相关
    NBTable getNBTable(Long tableRrn) throws ClientException;
    NBTable getNBTableByAuthority(Long authorityRrn) throws ClientException;
    List<? extends NBBase> getDataFromTableRrn(Long tableRrn, SessionContext sc) throws ClientException;
    List<? extends NBBase> getDataFromTableRrn(Long tableRrn, String whereClause, String orderBy, SessionContext sc) throws ClientException;

    // ReferenceList相关
    List<? extends NBReferenceList> getReferenceList(String referenceName, String category, SessionContext sc) throws ClientException;

    // ReferenceTable相关
    NBReferenceTable getReferenceTableByName(String name, SessionContext sc) throws ClientException;

}
