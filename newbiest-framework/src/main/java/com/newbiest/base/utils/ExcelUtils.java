package com.newbiest.base.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.ui.model.NBField;
import com.newbiest.base.ui.model.NBTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 导入导出excel的正常操作 支持从bean直接导出 支持从map上导出Map<propertyName, value>
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
                            if (StringUtils.isNullOrEmpty(pattern)) {
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
        if (cell == null || (cell.getCellTypeEnum() == CellType.STRING && StringUtils.isNullOrEmpty(cell.getStringCellValue()))) {
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
            if (!StringUtils.isNullOrEmpty(value)) {
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
            exportExcel(headers, data, outputStream, StringUtils.EMPTY);
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
            write2Sheet(sheet, headers, data, pattern);
            workbook.write(outputStream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }

    }

    /**
     * 根据nbtable上的栏位的是否导出标志来导出模板
     * @param nbTable 动态表
     * @param language 语言
     * @return
     * @throws Exception
     */
    public static void exportByTable(NBTable nbTable, Collection data, String language, OutputStream out) throws Exception {
        Map<String, String> headerMap = buildHeaderByTable(nbTable, language);
        exportExcel(headerMap, data, out);
    }

    /**
     * 根据nbtable上的栏位的是否导出标志来生成表头
     * @param nbTable 动态表
     * @param language 语言
     * @return
     * @throws Exception
     */
    public static Map<String, String> buildHeaderByTable(NBTable nbTable, String language) throws Exception {
        try {
            Map<String, String> headerMap = Maps.newLinkedHashMap();
            List<NBField> fields = nbTable.getFields();
            if (StringUtils.isNullOrEmpty(language)) {
                language = NBBase.LANGUAGE_CHINESE;
            }
            if (CollectionUtils.isNotEmpty(fields)) {
                fields = fields.stream().filter(field -> field.getMainFlag()).sorted(Comparator.comparing(NBField :: getSeqNo)).collect(Collectors.toList());
                for (NBField field : fields) {
                    String value = field.getLabelZh();
                    if (NBBase.LANGUAGE_ENGLISH.equals(language)) {
                        value = field.getLabel();
                    }
                    if (field.getRequiredFlag()) {
                        value += "*";
                    }
                    headerMap.put(field.getName(), value);
                }
            }
            return headerMap;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 表头加粗 如果包含了*号则进行标红
     * @param sheet
     * @param text
     * @return
     */
    public static HSSFCellStyle buildHeaderHssfCellStyle(HSSFSheet sheet, String text) {
        HSSFCellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        HSSFFont hssfFont = sheet.getWorkbook().createFont();
        hssfFont.setBold(true);
        if (text.indexOf('*') != -1) {
            hssfFont.setColor(HSSFFont.COLOR_RED);
        }
        headerStyle.setFont(hssfFont);
        return headerStyle;
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
            if (StringUtils.isNullOrEmpty(pattern)) {
                pattern = DEFAULT_DATE_PATTERN;
            }

            int index = 0;
            // 创建列头
            HSSFRow hssfRow = sheet.createRow(index);
            // 记录当前栏位在什么位置
            Map<String, Integer> headerIndexMap = Maps.newHashMap();
            int cellNum = 0;
            for (String propertyName : headers.keySet()) {
                HSSFCell cell = hssfRow.createCell(cellNum);
                String header = headers.get(propertyName);
                HSSFRichTextString text = new HSSFRichTextString(header);
                cell.setCellValue(text);
                cell.setCellStyle(buildHeaderHssfCellStyle(sheet, header));
                headerIndexMap.put(propertyName, cellNum);
                cellNum++;
            }
            index++;
            if (CollectionUtils.isNotEmpty(data)) {
                // 填充数据 从标题行下一行开始
                for(Object object : data) {
                    hssfRow = sheet.createRow(index);
                    for (String propertyName : headers.keySet()) {
                        Object value = PropertyUtils.getProperty(object, propertyName);
                        cellNum = headerIndexMap.get(propertyName);
                        HSSFCell cell = hssfRow.createCell(cellNum);
                        setCellValue(cell, value, pattern);
                    }
                    index++;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    private static void setCellValue(HSSFCell cell, Object value, String pattern) throws Exception{
        try {
            if (value == null) {
                value = StringUtils.EMPTY;
            }
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

}
