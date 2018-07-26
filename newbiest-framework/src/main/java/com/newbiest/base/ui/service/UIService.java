package com.newbiest.base.ui.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.utils.SessionContext;

import java.util.List;

/**
 * 获取数据生成UI页面的操作
 * Created by guoxunbo on 2018/4/18.
 */
public interface UIService {

    NBTable getNBTableByAuthority(Long authorityRrn) throws ClientException;

    List<? extends NBBase> getDataFromTableRrn(Long tableRrn, SessionContext sc) throws ClientException;

}
