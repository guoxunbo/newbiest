package com.newbiest.mms.utils;

import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.ui.model.NBField;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.PropertyUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.exception.MmsException;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CsvUtils {

    private static final Logger log = LoggerFactory.getLogger(CsvUtils.class);

    public static Collection importCsv(NBTable nbTable, Class clazz, Map<String, String> headersMapped, InputStream inputStream, String separtor) throws Exception{
        Collection csvDataList = new ArrayList<>();
        try(CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(inputStream,"GBK")))){
            int num = 0;
            List<NBField> fields = nbTable.getFields();
            Map<String, NBField> fieldMap = fields.stream().collect(Collectors.toMap(NBField :: getLabelZh, Function.identity()));
            List<String[]> csvReaderCode = csvReader.readAll();
            if(csvReaderCode.size() > 0){
                String[] boxhead = null;
                for (String[] stringCode : csvReaderCode){
                    if (num == 0){
                        boxhead = stringCode;
                        ++num;
                    } else {
                        if(stringCodeIsEmpty(stringCode)){
                            Object object = clazz.newInstance();
                            for (int i = 0 ; i < stringCode.length ; i++){
                                if(i < boxhead.length && !StringUtils.isNullOrEmpty(boxhead[i])){
                                    if (headersMapped.containsKey(boxhead[i])){
                                        NBField nbField = fieldMap.get(boxhead[i]);
                                        if(nbField.getRequiredFlag() && StringUtils.isNullOrEmpty(stringCode[i].trim())){
                                            throw new ClientParameterException(MmsException.MM_IMPORT_FILE_CONTAINS_EMPTY_DATA, boxhead[i]);
                                        } else {
                                            PropertyUtils.setProperty(object,headersMapped.get(boxhead[i]),stringCode[i].trim() );
                                        }
                                    }
                                }
                            }
                            csvDataList.add(object);
                        }
                    }
                }
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
        return csvDataList;
    }

    /**
     * 验证是否为空行
     * @param stringCode
     * @return
     */
    private static boolean stringCodeIsEmpty(String[] stringCode) throws ClientException{
        try {
            boolean flag = false;
            for(String code : stringCode){
                if(!StringUtils.isNullOrEmpty(code.trim())){
                    flag = true;
                    break;
                }
            }
            return flag;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }


    public static Map<String, String> buildHeaderByTable(NBTable nbTable, String language) throws Exception {
        try {
            Map<String, String> headerMap = Maps.newLinkedHashMap();
            List<NBField> fields = nbTable.getFields();
            if (StringUtils.isNullOrEmpty(language)) {
                language = "Chinese";
            }

            if (CollectionUtils.isNotEmpty(fields)) {
                fields = (List)fields.stream().filter((fieldx) -> {
                    return fieldx.getMainFlag();
                }).sorted(Comparator.comparing(NBField::getSeqNo)).collect(Collectors.toList());

                NBField field;
                String value;
                for(Iterator var4 = fields.iterator(); var4.hasNext(); headerMap.put(field.getName(), value)) {
                    field = (NBField)var4.next();
                    value = field.getLabelZh();
                    if ("English".equals(language)) {
                        value = field.getLabel();
                    }
                }
            }

            return headerMap;
        } catch (Exception var7) {
            log.error(var7.getMessage(), var7);
            throw var7;
        }
    }

    /**
     * 通过表单字段信息验证文件是否和导入型号匹配
     * @param inputStream
     * @param headersMapped
     */
    public static void validateImportFile( Map<String, String> headersMapped, InputStream inputStream, NBTable nbTable) throws ClientException {
        try(CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(inputStream,"GBK")))){
            List<String[]> csvReaderCode = csvReader.readAll();
            List<String> fieldNameList = Arrays.asList(csvReaderCode.get(0));
            List<String> nbFieldNameList = new ArrayList<>();
            for(String headerName : headersMapped.keySet()){
                nbFieldNameList.add(headerName);
            }
            nbFieldNameList.removeAll(fieldNameList);
            if(nbFieldNameList != null){
                for(String headerName : nbFieldNameList){
                    for(NBField nbField :nbTable.getFields() ){
                        if(nbField.getLabelZh().equals(headerName) && nbField.getRequiredFlag()){
                            throw new ClientParameterException(MmsException.MM_IMPORT_FILE_AND_TYPE_IS_NOT_SAME, headerName);
                        }
                    }
                }
            }
            //if(nbFieldNameList != null && nbFieldNameList.size() > 0){
            //    throw new ClientParameterException(MmsException.MM_IMPORT_FILE_AND_TYPE_IS_NOT_SAME);
            //}
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

}
