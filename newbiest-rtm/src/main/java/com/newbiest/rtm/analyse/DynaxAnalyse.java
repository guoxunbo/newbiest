package com.newbiest.rtm.analyse;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.rtm.model.AnalyseResult;
import com.newbiest.rtm.model.DynaxAnalyseResult;
import com.newbiest.rtm.model.DynaxAnalyseResultDetail;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;

/**
 * 能讯的文件解析
 *  格式参考资源文件 D1H38140C1_D1923232_25Feb2019_1032_Site0.txt
 * Created by guoxunbo on 2019/5/27.
 */
@Slf4j
public class DynaxAnalyse implements IAnalyse {

    public static final String SPLIT_BATCH_PART_FLAG = "************************************************************************************************************";

    public static final String SPLIT_STEP_FLAG = "============================================================================================================";
    public static final String SPLIT_STEP_RESULT_FLAG = "------------------------------------------------------------------------------------------------------------";
    public static final String SPLIT_STEP_RESULT_HEADER = "Test # | Result | Test Name                          | Low Limit    | Value        | High Limit   | Units   ";


    public static final String SITE_NUBER_PREFIX = "Site Number:";
    public static final String BATCH_NUBER_PREFIX = "Batch Number:";
    public static final String PART_NUBER_PREFIX = "Part Number:";
    public static final String STEP_NAME_PREFIX = "Step Name:";

    /*
     * 根据文件流解析成结果
     *      fileName为文件名：D1H38140C1_D1923232_25Feb2019_1032_Site0.txt
     *                      其中D1H38140C1是产品名，D1923232是批次号
     * @param analyseContext
     * @throws ClientException
     */
    @Override
    public List<AnalyseResult> analyse(AnalyseContext analyseContext) throws ClientException {
        try {
            // 文件名D1H38140C1_D1923232_25Feb2019_1032_Site0.txt
            String[] fileNameContent = analyseContext.getFileName().split("_");

            InputStream inputStream = analyseContext.getInputStream();
            List<String> contents = readLines(inputStream, true);

            List<AnalyseResult> dynaxAnalyseResults = Lists.newArrayList();

            DynaxAnalyseResult result = null;
            List<DynaxAnalyseResultDetail> details = Lists.newArrayList();
            String stepName = StringUtils.EMPTY;
            boolean startStepFlag = false;

            for (String content : contents) {
                if (StringUtils.isNullOrEmpty(content)) {
                    continue;
                }
                if (SPLIT_BATCH_PART_FLAG.equals(content)) {
                    if (result != null) {
                        result.setDetails(details);
                        dynaxAnalyseResults.add(result);
                    }
                    result = new DynaxAnalyseResult();
                    result.setFileName(analyseContext.getFileName());
                    result.setPartName(fileNameContent[0]);
                    result.setLotId(fileNameContent[1]);
                    details = Lists.newArrayList();
                }
                if (content.startsWith(SITE_NUBER_PREFIX)) {
                    result.setSiteNumber(content.substring(SITE_NUBER_PREFIX.length()).trim());
                } else if (content.startsWith(BATCH_NUBER_PREFIX)) {
                    result.setBatchNumber(content.substring(BATCH_NUBER_PREFIX.length()).trim());
                } else if (content.startsWith(PART_NUBER_PREFIX)) {
                    result.setPartNumber(content.substring(PART_NUBER_PREFIX.length()).trim());
                }
                if (SPLIT_STEP_FLAG.equals(content)) {
                    if (!startStepFlag) {
                        startStepFlag = true;
                        stepName = StringUtils.EMPTY;
                    } else {
                        startStepFlag = false;
                    }
                    continue;
                }
                if (startStepFlag) {
                    if (content.startsWith(STEP_NAME_PREFIX)) {
                        stepName = content.substring(STEP_NAME_PREFIX.length()).trim();
                    } else if (SPLIT_STEP_RESULT_FLAG.equals(content) || SPLIT_STEP_RESULT_HEADER.equals(content)) {
                        continue;
                    } else {
                        DynaxAnalyseResultDetail detail = buildDetail(stepName, content);
                        details.add(detail);
                    }
                }
            }
            result.setDetails(details);
            dynaxAnalyseResults.add(result);
            return dynaxAnalyseResults;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }


    private DynaxAnalyseResultDetail buildDetail(String stepName, String content) throws ClientException{
        DynaxAnalyseResultDetail detail = new DynaxAnalyseResultDetail();
        detail.setStepName(stepName);
        String[] contents = content.split("\\|");
        detail.setTest(contents[0].trim());
        detail.setResult(contents[1].trim());
        detail.setTestName(contents[2].trim());
        detail.setLowerLimit(contents[3].trim());
        detail.setCurrentValue(contents[4].trim());
        detail.setUpperLimit(contents[5].trim());
        detail.setUnits(contents[6].trim());
        return detail;
    }

    /**
     *
     * @param inputStream
     * @param closeStream
     * @return
     */
    private List<String> readLines(InputStream inputStream, boolean closeStream) {
        try {
            List<String> lines = Lists.newArrayList();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                String readLine = reader.readLine();
                if (readLine != null) {
                    lines.add(readLine);
                } else {
                    break;
                }
            }
            reader.close();
            return lines;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        } finally {
            if (closeStream) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
    }
}
