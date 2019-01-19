package com.newbiest.base.utils;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 属性栏位工具类
 * Created by guoxunbo on 2017/10/7.
 */
public class PropertyUtils extends org.apache.commons.beanutils.PropertyUtils {

    private static Logger logger = LoggerFactory.getLogger(PropertyUtils.class);

    public static void copyProperties(Object sourceBean, Object targetBean) {
        copyProperties(sourceBean, targetBean, null);
    }

    /**
     * 属性复制
     * @param sourceBean
     * @param targetBean
     * @param converter
     */
    public static void copyProperties(Object sourceBean, Object targetBean, Converter converter) {
        BeanCopier beanCopier;
        if (converter != null) {
            beanCopier = BeanCopier.create(sourceBean.getClass(), targetBean.getClass(), true);
        } else {
            beanCopier = BeanCopier.create(sourceBean.getClass(), targetBean.getClass(), false);
        }
        beanCopier.copy(sourceBean, targetBean, converter);
    }

    public static void setProperty(Object sourceBean, String propertyName, Object value) {
        try {
            if (!StringUtils.isNullOrEmpty(propertyName)) {
                org.apache.commons.beanutils.PropertyUtils.setSimpleProperty(sourceBean, propertyName, value);
            }
        } catch (IllegalArgumentException e) {
            try {
                // 参数类型不匹配的时候
                Class clazz = org.apache.commons.beanutils.PropertyUtils.getPropertyType(sourceBean, propertyName);
                if (value instanceof Boolean) {
                    if (String.class.getName().equalsIgnoreCase(clazz.getName())) {
                        if ((Boolean)value) {
                            org.apache.commons.beanutils.PropertyUtils.setSimpleProperty(sourceBean, propertyName, "Y");
                        } else {
                            org.apache.commons.beanutils.PropertyUtils.setSimpleProperty(sourceBean, propertyName, "N");
                        }
                    }
                } else {
                    if (Date.class.getName().equalsIgnoreCase(clazz.getName())) {
                        if (value instanceof String) {
                            org.apache.commons.beanutils.PropertyUtils.setSimpleProperty(sourceBean, propertyName, DateUtils.parseDateTime((String) value));
                        } else if (value instanceof Date) {
                            org.apache.commons.beanutils.PropertyUtils.setSimpleProperty(sourceBean, propertyName, value);
                        }
                    } else if (Long.class.getName().equalsIgnoreCase(clazz.getName())) {
                        if (value instanceof String) {
                            org.apache.commons.beanutils.PropertyUtils.setSimpleProperty(sourceBean, propertyName, Long.parseLong((String) value));
                        }
                    } else if (Double.class.getName().equalsIgnoreCase(clazz.getName())) {
                        if (value instanceof String) {
                            org.apache.commons.beanutils.PropertyUtils.setSimpleProperty(sourceBean, propertyName, Double.parseDouble((String) value));
                        }
                    } else if (BigDecimal.class.getName().equalsIgnoreCase(clazz.getName())) {
                        if (value instanceof String) {
                            org.apache.commons.beanutils.PropertyUtils.setSimpleProperty(sourceBean, propertyName, new BigDecimal((String) value));
                        }
                    } else if (Integer.class.getName().equalsIgnoreCase(clazz.getName())) {
                        if (value instanceof String) {
                            org.apache.commons.beanutils.PropertyUtils.setSimpleProperty(sourceBean, propertyName, Integer.parseInt((String) value));
                        }
                    } else if (int.class.getName().equalsIgnoreCase(clazz.getName())) {
                        if (value instanceof String) {
                            org.apache.commons.beanutils.PropertyUtils.setSimpleProperty(sourceBean, propertyName, Integer.parseInt((String) value));
                        } else if (value instanceof Double) {
                            org.apache.commons.beanutils.PropertyUtils.setSimpleProperty(sourceBean, propertyName, ((Double) value).intValue());

                        }
                    }
                }
            } catch (Exception e1) {
                logger.error("PropertyUtil : setProperty " + "name=" + propertyName , e1);
            }
        } catch (Exception e) {
            logger.error("PropertyUtil : setProperty " + "name=" + propertyName , e);
        }
    }

    public static Object getProperty(Object sourceObject, String propertyName) {
       try {
            return org.apache.commons.beanutils.PropertyUtils.getProperty(sourceObject, propertyName);
       } catch (Exception e) {
           logger.error("PropertyUtil : getProperty " + "name=" + propertyName , e);
       }
       return null;
    }
}
