package com.newbiest.rtm.service.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.rtm.analyse.AnalyseContext;
import com.newbiest.rtm.analyse.DynaxAnalyse;
import com.newbiest.rtm.analyse.IAnalyse;
import com.newbiest.rtm.model.AnalyseResult;
import com.newbiest.rtm.model.DynaxAnalyseResult;
import com.newbiest.rtm.model.DynaxAnalyseResultDetail;
import com.newbiest.rtm.repository.DynaxAnalyseResultDetailRepository;
import com.newbiest.rtm.repository.DynaxAnalyseResultRepository;
import com.newbiest.rtm.service.RtmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by guoxunbo on 2019/5/27.
 */
@Component
@Transactional
@Slf4j
public class RtmServiceImpl implements RtmService {

    @Autowired
    DynaxAnalyseResultRepository dynaxAnalyseResultRepository;

    @Autowired
    DynaxAnalyseResultDetailRepository dynaxAnalyseResultDetailRepository;

    @Autowired
    BaseService baseService;

    /**
     * 分析文件并产生结果保存
     */
    public void analyseFile(AnalyseContext analyseContext, SessionContext sc) throws ClientException{
        try {
            IAnalyse analyser = analyseContext.match();
            log.info("Start to analyse");
            List<AnalyseResult> analyseResultList = analyser.analyse(analyseContext);
            log.info("Analysed. start to delete exist data");
            if (analyser instanceof DynaxAnalyse) {
                deleteDynaxAnalyseResultByFileName(analyseContext.getFileName());
            }
            log.info("deleted start to save data");
            analyseResultList.parallelStream().forEach(result -> {
                baseService.saveEntity(result, sc);
            });
            log.info("saved");
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据文件名称删除已经解析过的结果
     */
    public void deleteDynaxAnalyseResultByFileName(String fileName) {
        try {
            List<DynaxAnalyseResult> fileResults = dynaxAnalyseResultRepository.getByFileName(fileName);
            if (CollectionUtils.isNotEmpty(fileResults)) {
                for (DynaxAnalyseResult result : fileResults) {
                    dynaxAnalyseResultDetailRepository.deleteByResultRrn(result.getObjectRrn());
                }
            }
            dynaxAnalyseResultRepository.deleteByFileName(fileName);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

}
