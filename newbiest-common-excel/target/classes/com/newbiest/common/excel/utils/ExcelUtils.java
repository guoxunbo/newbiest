package com.newbiest.common.excel.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.PreConditionalUtils;
import com.newbiest.base.utils.PropertyUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.common.excel.annotation.Export;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 导入导出excel的正常操作 支持从bean直接导出 支持从map上导出Map<propertyName, value>
 * //TODO 处理导入
 * Created by guoxunbo on 2018/4/2.
 */
@Slf4j
public class ExcelUtils {

    /**
     * 默认的日期格式
     */
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss:SSS";


    /**
     * 支持的cell类型 和字段的栏位类型相匹配
     */
    private static Map<Class, CellType[]> validateMap = Maps.newHashMap();

    static {
        validateMap.put(String[].class, new CellType[]{CellType.STRING});
        validateMap.put(Double[].class, new CellType[]{CellType.NUMERIC});
        validateMap.put(String.class, new CellType[]{CellType.STRING});
        validateMap.put(Double.class, new CellType[]{CellType.NUMERIC});
        validateMap.put(Date.class, new CellType[]{CellType.NUMERIC, CellType.STRING});
        validateMap.put(Integer.class, new CellType[]{CellType.NUMERIC});
        validateMap.put(Float.class, new CellType[]{CellType.NUMERIC});
        validateMap.put(Long.class, new CellType[]{CellType.NUMERIC});
        validateMap.put(Boolean.class, new CellType[]{CellType.BOOLEAN});
    }

    /**
     * 导入excel返回对象 当前仅支持导入一个单sheet
     * @param clazz 导入对象 当为空的时候默认导出 List<Map<列头, 值>
     * @param headersMapped 列头映射如：Map<名称, name> 当列头和属性名不一致的时候的映射关系
     * @param inputStream 导入的来源
     * @param pattern 日期格式 当有date类型的时候 导出到excel中是以如何格式进行显示 默认为："YYYY-MM-dd HH:mm:ss:SSS"
     * @return
     * @throws Exception
     */
    public static Collection importExcel(Class clazz, Map<String, String> headersMapped, InputStream inputStream, String pattern) throws Exception {
        try {
            PreConditionalUtils.checkNotNull(inputStream, "Excel Input");
            if (clazz == null || clazz == Map.class) {
                return importExcel(inputStream);
            }
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.rowIterator();

            List dataList = Lists.newArrayList();
            // 取出headers行 将列头转换成 -> map<属性名, 位置>
            Map<String, Integer> headerIndexMap = Maps.newHashMap();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                // 如果是第一行 则是列头
                if (row.getRowNum() == 0) {
                    int cellNumber = 0;
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        String value = cell.getStringCellValue();
                        // 如果不需要转换 则直接放值
                        if (headersMapped == null || headersMapped.size() == 0) {
                            headerIndexMap.put(value, cellNumber);
                        } else {
                            if (headersMapped.containsKey(value)) {
                                // 如name, 1
                                headerIndexMap.put(headersMapped.get(value), cellNumber);
                            }
                        }
                        cellNumber++;
                    }
                    continue;
                }
                // 当遇到空行则跳过
                boolean nullRowFlag = checkCurrentRowIsNull(row);
                if (nullRowFlag) {
                    continue;
                }
                Object object = clazz.newInstance();
                Field[] fields = clazz.getDeclaredFields();
                for (String propertyName : headerIndexMap.keySet()) {
                    // 如果fileds中没有这个属性栏位 则不导入
                    boolean fieldsContainsProperty = false;
                    for (Field field : fields) {
                        if (field.getName().equalsIgnoreCase(propertyName)) {
                            fieldsContainsProperty = true;
                            break;
                        }
                    }
                    if (fieldsContainsProperty) {
                        Cell cell = row.getCell(headerIndexMap.get(propertyName));
                        Object value = getCellValue(cell);
                        // 日期类型单独处理
                        if (org.apache.commons.beanutils.PropertyUtils.getPropertyType(object, propertyName) == Date.class) {
                            if (StringUtils.isEmpty(pattern)) {
                                pattern = DEFAULT_DATE_PATTERN;
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                            value = sdf.parse((String) value);
                        }

                        PropertyUtils.setProperty(object, propertyName, value);
                    }
                }

                dataList.add(object);
            }

            return dataList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 取得Cell的值
     * @param cell
     * @return
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null || (cell.getCellTypeEnum() == CellType.STRING && StringUtils.isEmpty(cell.getStringCellValue()))) {
            return StringUtils.EMPTY;
        }
        CellType cellType = cell.getCellTypeEnum();
        if (cellType == CellType.BOOLEAN){
            return cell.getBooleanCellValue();
        } else if (cellType == CellType.ERROR) {
            return cell.getErrorCellValue();
        } else if (cellType == CellType.FORMULA) {
            try {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            } catch (IllegalStateException e) {
                return cell.getRichStringCellValue();
            }
        } else if (cellType == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else {
                return cell.getNumericCellValue();
            }
        } else if(cellType == CellType.STRING) {
            return cell.getStringCellValue();
        }
        return null;
    }

    /**
     * 检查当前行是否是空行
     */
    private static boolean checkCurrentRowIsNull(Row row) throws Exception {
        // 当遇到空行则跳过
        boolean nullRowFlag = true;
        while (row.cellIterator().hasNext()) {
            Cell cell = row.cellIterator().next();
            String value = cell.getStringCellValue();
            if (!StringUtils.isEmpty(value)) {
                nullRowFlag = false;
                break;
            }
            if (nullRowFlag && log.isWarnEnabled()) {
                log.warn("The rowNumber [ " + row.getRowNum() + "]'s data is null!");
            }
        }
        return nullRowFlag;
    }

    /**
     * 导入excel返回List<Map> 当前仅支持导入一个单sheet
     * @param inputStream 导入的来源
     * @return List<Map<列头, 值>
     * @throws Exception
     */
    public static List<Map> importExcel(InputStream inputStream) throws Exception {
        try {
            PreConditionalUtils.checkNotNull(inputStream, "Excel Input");
            Workbook workbook = WorkbookFactory.create(inputStream);

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.rowIterator();

            List<Map> dataList = Lists.newArrayList();
            // 取出headers行 MAP<列头名, 属性名> -> map<属性名, 位置>
            Map<String, Integer> headerMap = Maps.newHashMap();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                // 如果是第一行 则是列头
                if (row.getRowNum() == 0) {
                    int cellNumber = 0;
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        String value = cell.getStringCellValue();
                        headerMap.put(value, cellNumber);
                        cellNumber++;
                    }
                    continue;
                }
                // 当遇到空行则跳过
                boolean nullRowFlag = checkCurrentRowIsNull(row);
                if (nullRowFlag) {
                    continue;
                }
                Map<String, Object> dataMap = Maps.newHashMap();
                for (String key : headerMap.keySet()) {
                    int index = headerMap.get(key);
                    Cell cell = row.getCell(index);
                    if (cell == null) {
                        dataMap.put(key, null);
                    } else {
                        // 当是Map的时候，所有类型都改成String传递
                        cell.setCellType(CellType.STRING);
                        dataMap.put(key, cell.getStringCellValue());
                    }
                }
                dataList.add(dataMap);
            }
            return dataList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 将符合一定条件的数据导出到EXCEL中 仅支持单个Sheet
     *
     * @param headers 列头格式为<字段名, 列名> 如<name, 名字>
     * @param data 需要导出到EXCEL中的对象
     * @param outputStream 输出对象
     */
    public static void exportExcel(Map<String, String> headers, Collection data, OutputStream outputStream) throws Exception {
        try {
            exportExcel(headers, data, outputStream, null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }


    /**
     * 将符合一定条件的数据导出到EXCEL中 仅支持单个Sheet
     *
     * @param headers 列头格式为<字段名, 列名> 如<name, 名字>
     * @param data 需要导出到EXCEL中的对象
     * @param outputStream 输出对象
     * @param pattern 日期格式 当有date类型的时候 导出到excel中是以如何格式进行显示 默认为："YYYY-MM-dd HH:mm:ss:SSS"
     */
    public static void exportExcel(Map<String, String> headers, Collection data, OutputStream outputStream, String pattern) throws Exception {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();

            if (CollectionUtils.isNotEmpty(data)) {
                write2Sheet(sheet, headers, data, pattern);
            }
            workbook.write(outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }

    }

    /**
     * 将数据写入Sheet
     * @param sheet sheet
     * @param headers 列头 列头格式为<字段名, 列名> 如<name, 名字>
     * @param data 需要导出到EXCEL中的对象
     * @param pattern 时间格式 日期格式 当有date类型的时候 导出到excel中是以如何格式进行显示 默认为："YYYY-MM-dd HH:mm:ss:SSS"
     */
    public static void write2Sheet(HSSFSheet sheet, Map<String, String> headers, Collection data, String pattern) throws Exception {
        try {
            PreConditionalUtils.checkNotNull(headers, "Excel Headers");
            PreConditionalUtils.checkNotNull(data, "Excel Data");
            if (StringUtils.isEmpty(pattern)) {
                pattern = DEFAULT_DATE_PATTERN;
            }

            int index = 0;
            // 创建列头
            HSSFRow hssfRow = sheet.createRow(index);
            // 记录当前栏位在什么位置
            Map<String, Integer> headerIndexMap = Maps.newHashMap();
            int cellNum = 0;
            // 列头的Style 加粗
            HSSFCellStyle headerStyle = sheet.getWorkbook().createCellStyle();
            HSSFFont hssfFont = sheet.getWorkbook().createFont();
            hssfFont.setBold(true);
            headerStyle.setFont(hssfFont);

            for (String key : headers.keySet()) {
                HSSFCell cell = hssfRow.createCell(cellNum);
                HSSFRichTextString text = new HSSFRichTextString(headers.get(key));
                cell.setCellValue(text);
                cell.setCellStyle(headerStyle);
                headerIndexMap.put(key, cellNum);
                cellNum++;
            }

            index++;
            // 填充数据 从标题行下一行开始
            for(Object object : data) {
                hssfRow = sheet.createRow(index);
                // 从map中获取列位置进行填充
                List<SortingField> sortingFields = getSortingFields(object, headerIndexMap);
                for (SortingField sortingField : sortingFields) {
                    cellNum = headerIndexMap.get(sortingField.getName());
                    HSSFCell cell = hssfRow.createCell(cellNum);
                    setCellValue(cell, sortingField.getValue(), pattern);
                }
                index++;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    private static void setCellValue(HSSFCell cell, Object value, String pattern) throws Exception{
        try {
            PreConditionalUtils.checkNotNull(value, "Cell Value");
            String textValue = null;

            if (value instanceof Integer) {
                cell.setCellValue((Integer)value);
            } else if (value instanceof Float) {
                cell.setCellValue((Float)value);
            } else if (value instanceof Double) {
                cell.setCellValue((Double) value);
            } else if (value instanceof Long) {
                cell.setCellValue((Long)value);
            } else if (value instanceof Boolean) {
                cell.setCellValue((Boolean) value);
            } else if (value instanceof Date) {
                // 特殊处理时间类型
                Date date = (Date) value;
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                textValue = sdf.format(date);
            } else {
                // 其他类型都当做字符串处理
                // TODO 此处处理默认值的问题
                textValue = value.toString();
            }
            if (textValue != null) {
                HSSFRichTextString richString = new HSSFRichTextString(textValue);
                cell.setCellValue(richString);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 将对象转换成SortingFiled
     * @param object 当前支持Map<String, value>以及普通的带有Export注解的JAVABean
     * @param headerIndexMap
     * @return
     * @throws Exception
     */
    private static List<SortingField> getSortingFields(Object object, Map<String, Integer> headerIndexMap) throws Exception {
        try {
            List<SortingField> sortingFields = Lists.newArrayList();

            if (object instanceof Map) {
                for (Object key : ((Map) object).keySet()) {
                    if (!headerIndexMap.containsKey(key)) {
                        if (log.isWarnEnabled()) {
                            log.warn("key [" + key.toString() + "] is not in headerIndexMap");
                        }
                        continue;
                    }
                    SortingField sortingField = new SortingField((String) key,headerIndexMap.get(key), ((Map) object).get(key));
                    sortingFields.add(sortingField);
                }
            } else {
                // 如果是从bean中导出的
                Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    Export export = field.getAnnotation(Export.class);
                    // 没有export注解 默认为不导出
                    if (export == null) {
                        continue;
                    }
                    String fieldName = field.getName();
                    if (headerIndexMap.containsKey(fieldName)) {
                        SortingField sortingField = new SortingField(fieldName, headerIndexMap.get(fieldName), PropertyUtils.getProperty(object, fieldName));
                        sortingFields.add(sortingField);
                    } else {
                        if (log.isWarnEnabled()) {
                            log.warn("The field is not in headerIndexMap : [" + fieldName + "]");
                        }
                    }
                }
            }
            return sortingFields;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

}
