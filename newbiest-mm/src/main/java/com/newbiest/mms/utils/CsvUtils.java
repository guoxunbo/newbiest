package com.newbiest.mms.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.ui.model.NBField;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.PropertyUtils;
import com.newbiest.base.utils.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Cell;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import liquibase.util.csv.opencsv.CSVReader;
public class CsvUtils {

    private static final Logger log = LoggerFactory.getLogger(CsvUtils.class);
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss:SSS";


    public static Collection importCsv(Class clazz, Map<String, String> headersMapped, InputStream inputStream, String separtor) throws Exception{
        Collection csvDataList = new ArrayList<>();
        List<String> propertyNameList =  new ArrayList<String>();
        BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream,"GBK"));
        try{
            String fieldName = null;
            String[] str = null;
            int num = 0;
            while ((fieldName = bf.readLine()) != null){
                if(!StringUtils.isNullOrEmpty(fieldName)){
                    str = fieldName.split(separtor);
                    if (num == 0){
                        for (int i = 0 ; i< str.length ; i++){
                            propertyNameList.add(str[i].trim());
                        }
                        ++num;
                    }else {
                        Object object = clazz.newInstance();
                        for (int i = 0 ; i < str.length ; i++){
                            if (headersMapped.containsKey(propertyNameList.get(i))){
                                for (int j = 0 ; j < str[i].length() ; j++ ){
                                    str[i] = str[i].replace(" ","");
                                }
                                PropertyUtils.setProperty(object,headersMapped.get(propertyNameList.get(i)),str[i].trim() );
                            }
                        }
                        csvDataList.add(object);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if (bf != null){
                    bf.close();
                }
            }catch (Exception e){
                log.error(e.getMessage(), e);
                throw e;
            }
            try {
                if(inputStream != null){
                    inputStream.close();
                }
            }catch (Exception e){
                log.error(e.getMessage(), e);
                throw e;
            }
        }
        return csvDataList;
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

                    if (field.getRequiredFlag()) {
                        value = value + "*";
                    }
                }
            }

            return headerMap;
        } catch (Exception var7) {
            log.error(var7.getMessage(), var7);
            throw var7;
        }
    }

}
