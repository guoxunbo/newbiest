package com.newbiest.rtm.analyse;

import com.newbiest.base.exception.ClientException;
import com.newbiest.rtm.model.AnalyseResult;

import java.io.InputStream;
import java.util.List;

/**
 * Created by guoxunbo on 2019/5/27.
 */
public interface IAnalyse {

    List<AnalyseResult> analyse(AnalyseContext analyseContext) throws ClientException;

}
