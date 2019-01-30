package com.newbiest.common.idgenerator.model;

import com.newbiest.common.idgenerator.utils.GeneratorContext;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 时间类型
 * Created by guoxunbo on 2018/8/3.
 */
@Entity
@DiscriminatorValue(GeneratorRuleLine.DATA_TYPE_DATETIME)
@Data
public class DateRuleLine extends GeneratorRuleLine {

    /**
     * 默认当前日期
     */
    public static final String DATE_TYPE_SYSTEM = "SYSTEM";

    /**
     * 指定日期进行生成
     */
    public static final String DATE_TYPE_SPECIFIC = "Specific";

    public static final String DATE_FORMAT_YYYY = "yyyy";
    public static final String DATE_FORMAT_YY = "yy";
    public static final String DATE_FORMAT_Y = "y";

    public static final String DATE_FORMAT_MM = "MM";
    public static final String DATE_FORMAT_M = "M";
    public static final String DATE_FORMAT_DAY = "dd";
    public static final String DATE_FORMAT_WEEK = "ww";
    public static final String DATE_FORMAT_HOURS = "hh";
    public static final String DATE_FORMAT_MINUTES = "mm";
    public static final String DATE_FORMAT_SECONDS = "ss";

    public static final String DATE_FORMAT_REF_YEAR = "YEAR_CODE";
    public static final String DATE_FORMAT_REF_MONTH = "MONTH_CODE";

    /**
     * 日期类型
     * SYSTEM   : 默认类型,根据Context中日期来生成
     * SPECIFIC : 指定类型,根据传递的固定的日期来生成,用的极少 一般用来补条码
     */
    @Column(name = "DATE_TYPE")
    private String dateType = DATE_TYPE_SYSTEM;

    @Column(name = "SPECIFIC_DATE")
    private Date specificDate;

    @Column(name = "DATE_FORMAT")
    private String dateFormat;

    @Override
    public String generator(GeneratorContext context) throws Exception {
        Date useDate = new Date();
        if (DATE_TYPE_SPECIFIC.equals(dateType)) {
            if (specificDate != null) {
                useDate = specificDate;
            }
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(useDate);

        if (DATE_FORMAT_YYYY.equals(dateFormat) || DATE_FORMAT_YY.equals(dateFormat)
                || DATE_FORMAT_MM.equals(dateFormat) || DATE_FORMAT_M.equals(dateFormat)
                || DATE_FORMAT_WEEK.equals(dateFormat) || DATE_FORMAT_DAY.equals(dateFormat)
                || DATE_FORMAT_HOURS.equals(dateFormat) || DATE_FORMAT_MINUTES.equals(dateFormat)
                || DATE_FORMAT_SECONDS.equals(dateFormat)) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            return String.valueOf(sdf.format(calendar.getTime()));
        } else if (DATE_FORMAT_Y.equals(dateFormat)) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            String year = sdf.format(calendar.getTime());
            return String.valueOf(year.charAt(year.length() - 1));
        } else if (DATE_FORMAT_REF_YEAR.equals(dateFormat)) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYY);
            String year = String.valueOf(sdf.format(calendar.getTime()));
            return getReferenceValue(year, context);
        } else if (DATE_FORMAT_REF_MONTH.equals(dateFormat)) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_MM);
            String month = String.valueOf(sdf.format(calendar.getTime()));
            return getReferenceValue(month, context);
        }
        return GENERATOR_ERROR_CODE;
    }



}
