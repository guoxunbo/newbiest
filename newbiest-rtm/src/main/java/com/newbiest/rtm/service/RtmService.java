package com.newbiest.rtm.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.rtm.analyse.AnalyseContext;

/**
 * Created by guoxunbo on 2019/5/27.
 */
public interface RtmService {

    void analyseFile(AnalyseContext analyseContext) throws ClientException;
}
