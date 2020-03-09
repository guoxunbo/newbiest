package com.newbiest.mms.utils;
import com.google.common.collect.Maps;
import com.newbiest.base.ui.model.NBField;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.PropertyUtils;
import com.newbiest.base.utils.StringUtils;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class CsvUtils {

    private static final Logger log = LoggerFactory.getLogger(CsvUtils.class);

    public static Collection importCsv(Class clazz, Map<String, String> headersMapped, InputStream inputStream, String separtor) throws Exception{
        Collection csvDataList = new ArrayList<>();
        try(CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(inputStream,"GBK")))){
            int num = 0;
            List<String[]> csvReaderCode = csvReader.readAll();
            if(csvReaderCode.size() > 0){
                String[] boxhead = null;
                for (String[] stringCode : csvReaderCode){
                    if (num == 0){
                        boxhead = stringCode;
                        ++num;
                    } else {
                        Object object = clazz.newInstance();
                        for (int i = 0 ; i < stringCode.length ; i++){
                            if (headersMapped.containsKey(boxhead[i])){
                                PropertyUtils.setProperty(object,headersMapped.get(boxhead[i]),stringCode[i].trim() );
                            }
                        }
                        csvDataList.add(object);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
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
