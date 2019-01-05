package com.newbiest.base.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.ui.model.NBField;
import com.newbiest.base.ui.model.NBTable;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2018/4/2.
 */
public class ExcelUtilsTest {

    @Test
    public void exportExcelByBean() {
        Map<String, String> map = Maps.newLinkedHashMap();
        map.put("name", "name");
        map.put("age", "年龄");
        map.put("birthDay", "出生日期");

        List<People> list = Lists.newArrayList();
        People people = new People();
        people.setName("张三");
        people.setAge(10);
        people.setBirthDay(new Date());
        list.add(people);

        people = new People();
        people.setName("李四");
        people.setAge(11);
        people.setBirthDay(new Date());
        list.add(people);
        try {
            File file = new File("test.xls");
            OutputStream out = new FileOutputStream(file);
            ExcelUtils.exportExcel(map, list, out, "YYYY/MM/dd");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void exportExcelByMap() {
        Map<String, String> map = Maps.newLinkedHashMap();
        map.put("name", "name");
        map.put("age", "年龄");
        map.put("birthDay", "出生日期");

        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("name", "张三");
        dataMap.put("age", 11);
        dataMap.put("birthDay", new Date());
        list.add(dataMap);

        dataMap = Maps.newHashMap();
        dataMap.put("name", "李四");
        dataMap.put("age", 12);
        dataMap.put("birthDay", new Date());
        list.add(dataMap);

        dataMap = Maps.newHashMap();
        dataMap.put("name", "王五");
        dataMap.put("age", 13);
        dataMap.put("birthDay", new Date());
        list.add(dataMap);

        try {
            File file = new File("test2.xls");
            OutputStream out = new FileOutputStream(file);
            ExcelUtils.exportExcel(map, list, out, "YYYY/MM/dd");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void exportExcelByNBTable() {
        try {
            // 模拟数据
            NBTable nbTable = new NBTable();

            List<NBField> nbFields = Lists.newArrayList();
            NBField nbField = new NBField();
            nbField.setName("name");
            nbField.setLabelZh("名字");
            nbField.setSeqNo(10L);
            nbField.setExportFlag(true);
            nbField.setRequiredFlag(true);
            nbFields.add(nbField);

            nbField = new NBField();
            nbField.setName("age");
            nbField.setLabelZh("年龄");
            nbField.setExportFlag(true);
            nbField.setSeqNo(20L);
            nbFields.add(nbField);

            nbField = new NBField();
            nbField.setName("birthDay");
            nbField.setLabelZh("出生日期");
            nbField.setExportFlag(true);
            nbField.setSeqNo(30L);
            nbFields.add(nbField);

            nbField = new NBField();
            nbField.setName("job");
            nbField.setLabelZh("工作");
            nbField.setExportFlag(true);
            nbField.setSeqNo(40L);
            nbFields.add(nbField);

            nbTable.setFields(nbFields);

            File file = new File("test3.xls");
            OutputStream out = new FileOutputStream(file);
            ExcelUtils.exportTemplateByTable(nbTable, "", out);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void importExcelReturnMap() {
        InputStream inputStream = null;
        try {
            File file = new File("test3.xlsx");
            inputStream = new FileInputStream(file);
            List<Map> dataList = ExcelUtils.importExcel(inputStream);
            Assert.assertEquals(2, dataList.size());
            Assert.assertEquals("张三", dataList.get(0).get("名字"));
            Assert.assertEquals("10", dataList.get(0).get("年龄"));
            Assert.assertEquals(null, dataList.get(0).get("工作"));

            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void importExcelReturnObject() {
        InputStream inputStream = null;
        try {
            File file = new File("test3.xlsx");
            inputStream = new FileInputStream(file);

            Map<String, String> headersMapped = Maps.newHashMap();
            headersMapped.put("名字", "name");
            headersMapped.put("年龄", "age");
            headersMapped.put("出生日期", "birthDay");

            List<People> dataList = (List) ExcelUtils.importExcel(People.class, headersMapped, inputStream, "yyyy/MM/dd");


            Assert.assertEquals(2, dataList.size());
            Assert.assertEquals("张三", dataList.get(0).getName());
            Assert.assertEquals(10, dataList.get(0).getAge());
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}