package com.newbiest.common.idgenerator.model;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.common.idgenerator.exception.GeneratorExceptions;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.List;

/**
 * 生成序列号
 * 从NB_SEQUENCE表中获得下一个序列号
 * Created by guoxunbo on 2018/8/3.
 */
@Entity
@DiscriminatorValue(GeneratorRuleLine.DATA_TYPE_SEQUENCE)
@Data
@Slf4j
public class SequenceRuleLine extends GeneratorRuleLine{

    /**
     * 序列号完全由数字组成(1,2,3,4,...)
     */
    public static final String SEQUENCE_TYPE_DIGITS = "Digits";

    /**
     *  序列号由数字和字母混合组成(1,2,3,4,...,A,B,C,D,...Z) 36进制即0-9 A-Z
     *  到达一定进制则开始进位比如01-ZZ，01...0Z...10...ZZ
     */
    public static final String SEQUENCE_TYPE_RADIX = "Radix";

    /**
     * 递增
     */
    public static final String SEQUENCE_DIRECTION_UP = "Up";

    /**
     * 递减
     */
    public static final String SEQUENCE_DIRECTION_DOWN = "Down";

    public static final String EXCLUDE_TYPE_ALL = "All";

    public static final String EXCLUDE_TYPE_INCLUDE = "Include";

    @Column(name="SEQUENCE_TYPE")
    private String sequenceType = SEQUENCE_TYPE_DIGITS;

    @Column(name="SEQUENCE_DIRECTION")
    private String sequenceDirection = SEQUENCE_DIRECTION_UP;

    @Column(name="EXCLUDE")
    private String exclude;

    @Column(name="EXCLUDE_TYPE")
    private String excludeType = EXCLUDE_TYPE_ALL;

    @Column(name="MIN")
    private String min;

    @Column(name="MAX")
    private String max;

    @Transient
    private List<String> excludeString;

    @Override
    public String generator(GeneratorContext context) throws Exception {
        // 组成sequenceName
        String sequenceName = buildSequenceName(context);

        //获得允许的最大、最小值
        String currentSeq = GENERATOR_ERROR_CODE;
        int minValue = translate(min);
        int maxValue = translate(max);
        int currentInt = getNextSequence(context, 1, sequenceName, minValue).get(0);
        setExclude(exclude);
        if (currentInt > maxValue) {
            return currentSeq;
        }
        if (CollectionUtils.isNotEmpty(excludeString)) {
            do {
                currentSeq = translate(currentInt, context);
                if (isExclude(currentSeq)) {
                    //如果不符合过滤规则，则再生成一个seq
                    currentInt = getNextSequence(context, 1, sequenceName, minValue).get(0);
                }
            } while (isExclude(currentSeq));
        } else {
            currentSeq = translate(currentInt, context);
        }
        // 没设置最大值时。超过了位数限制抛出异常
        if (currentSeq.length() > length.intValue()) {
            throw new ClientException(GeneratorExceptions.COM_GENERATOR_ID_MORE_THAN_SIZE);
        }
        return currentSeq;
    }

    public boolean isExclude(String seq) {
        if (EXCLUDE_TYPE_ALL.equals(excludeType)) {
            if (CollectionUtils.isNotEmpty(excludeString)) {
                if (excludeString.contains(seq)) {
                    return true;
                }
            }
        } else {
            if (CollectionUtils.isNotEmpty(excludeString)) {
                for (String exclude : excludeString) {
                    if (seq.indexOf(exclude) != -1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 根据用户栏位参考值定义的进制符进行进位转换
     * @param currentSeq
     * @param context
     * @return
     */
    public String translate(int currentSeq, GeneratorContext context) {
        String currentString;
        if (StringUtils.isNullOrEmpty(referenceName)) {
            currentString = translate(currentSeq);
        } else {
            List<NBOwnerReferenceList> formatCodes = (List<NBOwnerReferenceList>) context.getUiService().getReferenceList(referenceName, NBReferenceList.CATEGORY_OWNER, SessionContext.buildSessionContext(orgRrn));
            if (CollectionUtils.isNotEmpty(formatCodes)) {
                char[] digits = new char[formatCodes.size()];
                for (int i = 0; i < formatCodes.size(); i++) {
                    digits[i] = formatCodes.get(i).getValue().charAt(0);
                }
                String digit = toCustomNumericString(currentSeq, digits.length, digits);
                currentString = StringUtils.padStart(digit, length.intValue(), '0');
            } else {
                // 如果没找到。默认返回数字
                String format = "%0" + length + "d";
                currentString = String.format(format, currentSeq);
            }
        }
        return currentString;
    }

    /**
     *
     * @param decimal 十进制数
     * @param radix 待转换的目的进制位数
     * @param digits 转换进制的字符集
     * @return
     */
    public String toCustomNumericString(int decimal, int radix, char[] digits) {
        long num;
        if (decimal < 0) {
            num = ((long) 2 * 0x7fffffff) + decimal + 2;
        } else {
            num = decimal;
        }
        // 只支持进制数的最大数，超出转换范围则报错，例：4进制的最大十进制数为255，超过255则报错
        int maxBuf = radix + 1;
        char[] buf = new char[maxBuf];
        int charPos = maxBuf;
        while ((num / radix) > 0) {
            buf[--charPos] = digits[(int) (num % radix)];
            num /= radix;
        }
        buf[--charPos] = digits[(int) (num % radix)];
        return new String(buf, charPos, (maxBuf - charPos));
    }

    /**
     * 对字符串进行翻译
     * 将字符串转换为对应的数字
     */
    public int translate(String currentSeq) {
        int currentInt = 0;
        if (SEQUENCE_TYPE_DIGITS.equals(sequenceType)) {
            //如果是DIGITS类型,则直接翻译
            currentInt = Integer.parseInt(currentSeq);
        } else if (SEQUENCE_TYPE_RADIX.equals(sequenceType)) {
            //如果是进制类型
            char[] cs = currentSeq.toCharArray();
            for(int i = 0; i < cs.length; i++) {
                int digits;
                if (Character.isDigit(cs[i])) {
                    digits = Integer.parseInt(String.valueOf(cs[i]));
                } else {
                    digits = cs[i] - 64 + 9;
                }
                //为36进制(10位数字+26位字母)
                currentInt += digits * Math.pow(36, cs.length - 1 - i);
            }
        }
        return currentInt;
    }

    /**
     * 对数字进行翻译
     * 将数字转换为对应的字符串
     */
    public String translate(int currentSeq) {
        String currentString = "";
        if (SEQUENCE_TYPE_DIGITS.equals(sequenceType)) {
            String format = "%0" + length + "d";
            currentString = String.format(format, currentSeq);
        } else if (SEQUENCE_TYPE_RADIX.equals(sequenceType)) {
            while (currentSeq > 0) {
                String letter;
                int mod = currentSeq % 36;
                if (mod > 9) {
                    letter = String.valueOf((char)('A' + (mod - 10)));
                } else {
                    letter = String.valueOf(mod);
                }
                currentString = letter + currentString;
                currentSeq = currentSeq / 36;
            }
            currentString = getCurrentString(currentString);
        }
        return currentString;
    }

    /**
     * 生成NBSequence的名称
     *  context.objectType + "-" + idPrefix + "_"
     *  如 MLot-BO3_
     * @param context ID生成规则上下文
     * @return
     * @throws Exception
     */
    private String buildSequenceName(GeneratorContext context) throws Exception{
        StringBuffer sequenceName = new StringBuffer(StringUtils.EMPTY);
        if(context != null && context.getObjectType() != null) {
            sequenceName.append(context.getObjectType() + StringUtils.SPLIT_CODE);
        }
        sequenceName.append(context.getIdPrefix() + StringUtils.UNDERLINE_CODE);
        return sequenceName.toString();
    }

    /**
     *
     * @param context
     * @param count
     * @param sequenceName
     * @param minValue
     * @return
     */
    private List<Integer> getNextSequence(GeneratorContext context, int count, String sequenceName, int minValue) throws ClientException{
        try {
            return context.getGeneratorService().getNextSequenceValue(orgRrn, objectRrn, sequenceName, count, minValue, context.isNewTransFlag());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
        if(exclude != null){
            if (EXCLUDE_TYPE_ALL.equals(excludeType)) {
                parserExcludeAll(exclude);
            } else {
                parserExcludeInclude(exclude);
            }
        }
    }

    /**
     * 只要包含了这个字符就过滤。比如设置9，那9会过滤，19则不会
     * @param exclude 过滤字符
     */
    private void parserExcludeAll(String exclude) {
        if (exclude != null && exclude.trim().length() > 0) {
            String[] excludes = exclude.split(EXCLUDE_SPLIT_CODE);
            excludeString = Lists.newArrayList();
            for (String currentString : excludes ) {
                if (SEQUENCE_TYPE_DIGITS.equals(sequenceType)) {
                    currentString = StringUtils.padStart(currentString, length.intValue(), '0');
                } else if (SEQUENCE_TYPE_RADIX.equals(sequenceType)) {
                    currentString = getCurrentString(currentString);
                } else {
                    if (currentString.length() < length) {
                        int i = (int)(length - currentString.length());
                        while (i > 0) {
                            currentString = "A" + currentString;
                            i--;
                        }
                    }
                }
                excludeString.add(currentString);
            }
        }
    }

    private String getCurrentString(String currentString) {
        if (currentString.length() < length) {
            int i = (int)(length - currentString.length());
            while (i > 0) {
                currentString = "0" + currentString;
                i--;
            }
        }
        return currentString;
    }

    /**
     * 只要包含了这个字符就过滤。比如设置9，那0019也会被过滤
     * @param exclude 过滤字符
     */
    private void parserExcludeInclude(String exclude) {
        if (exclude != null && exclude.trim().length() > 0) {
            String[] excludes = exclude.split(EXCLUDE_SPLIT_CODE);
            excludeString = Lists.newArrayList();
            for (String currentString : excludes ) {
                excludeString.add(currentString);
            }
        }
    }

}
