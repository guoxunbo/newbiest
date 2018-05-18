package com.newbiest.base.ui.model;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.utils.StringUtils;
import lombok.Data;

import javax.persistence.*;

/**
 * 页面栏位显示
 * Created by guoxunbo on 2018/2/7.
 */
@Data
@Entity
@Table(name = "NB_FIELD")
public class NBField extends NBBase {
    private static final long serialVersionUID = 2536500351356282982L;

    /**
     * 默认长度
     */
    public static final Long DISPLAY_LENGTH = 32L;

    @Column(name="NAME")
    private String name;

    @Column(name="COLUMN_NAME")
    private String columnName;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="TABLE_RRN")
    private Long tableRrn;

    @Column(name="TAB_RRN")
    private Long tabRrn;

    @Column(name="SEQ_NO")
    private Long seqNo;

    /**
     * 栏位的长度
     */
    @Column(name="DISPLAY_LENGTH")
    private Long displayLength = DISPLAY_LENGTH;

    /**
     * 栏位的显示类型
     * 如text、password、refTable等
     */
    @Column(name="DISPLAY_TYPE")
    private String displayType;

    /**
     * 栏位输入值的类型
     */
    @Column(name="DATA_TYPE")
    private String dataType;

    /**
     * 栏位输入值的命名规则
     * 正则表达式
     */
    @Column(name="NAMING_RULE")
    private String namingRule;

    /**
     * 栏位的关键字
     * 将相同关键字的栏位设为同一个关键组
     * 相同关健组中的栏位不同重复
     */
    @Column(name="KEY_NUMBER")
    private Long keyNumber;

    /**
     * 栏位的最小值
     */
    @Column(name="MIN_VALUE")
    private String minValue;

    /**
     * 栏位的最大值
     */
    @Column(name="MAX_VALUE")
    private String maxValue;

    /**
     * 栏位所对应的参考表
     */
    @Column(name="REF_TABLE_RRN")
    private Long refTableRrn;

    /**
     * 栏位所对应的系统参考值
     */
    @Column(name="REF_LIST_NAME")
    private String refListName;

    /**
     * 栏位所对应的用户参考值
     */
    @Column(name="USER_REF_LIST_NAME")
    private String user_Ref_ListName;

    /**
     * 栏位的参考规则
     */
    @Column(name="REFERENCE_RULE")
    private String referenceRule;

    /**
     * 栏位的默认值
     */
    @Column(name="DEFAULT_VALUE")
    private String defaultValue;

    /**
     * 英文标签
     */
    @Column(name="LABEL")
    private String label;

    /**
     * 中文标签
     */
    @Column(name="LABEL_ZH")
    private String labelZh;

    /**
     * 其它语言标签
     */
    @Column(name="LABEL_RES")
    private String labelRes;

    /**
     * 栏位是否显示
     */
    @Column(name="DISPLAY_FLAG")
    private String displayFlag;

    /**
     * 栏位是否显示在基本信息中
     */
    @Column(name="BASIC_FLAG")
    private String basicFlag;

    /**
     * 栏位是否在表格中显示
     */
    @Column(name="MAIN_FLAG")
    private String mainFlag;

    /**
     * 栏位是否保存数据库
     */
    @Column(name="PERSIST_FLAG")
    private String persistFlag;

    /**
     * 栏位是否是只读
     */
    @Column(name="READONLY_FLAG")
    private String readonlyFlag;

    /**
     * 保存之后是否可编辑
     */
    @Column(name="EDITABLE")
    private String editable;

    /**
     * 是否占用整行
     */
    @Column(name="ALL_LINE")
    private String allLine;

    /**
     * 必输
     */
    @Column(name="REQUIRED_FLAG")
    private String requiredFlag;

    /**
     * 是否自动转换成大写
     */
    @Column(name="UPPER_FLAG")
    private String upperFlag;

    /**
     * 从父对象上取值 父对象必须为对象的field栏位中体现如user.name
     */
    @Column(name = "FROM_PARENT")
    private String fromParent;

    /**
     * 是否是查询栏位
     */
    @Column(name="QUERY_FLAG")
    private String queryFlag;

    /**
     * 是否是导出栏位
     */
    @Column(name="EXPORT_FLAG")
    private String exportFlag;

    @ManyToOne
    @JoinColumn(name = "TABLE_RRN", referencedColumnName = "OBJECT_RRN",
            insertable = false, updatable = false)
    private NBTable table;

    @ManyToOne
    @JoinColumn(name = "TAB_RRN", referencedColumnName = "OBJECT_RRN",
            insertable = false, updatable = false)
    private NBTab tab;

    public Boolean getDisplayFlag() {
        return StringUtils.YES.equalsIgnoreCase(displayFlag);
    }

    public void setDisplayFlag(Boolean displayFlag) {
        this.displayFlag = displayFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getBasicFlag() {
        return StringUtils.YES.equalsIgnoreCase(basicFlag);
    }

    public void setBasicFlag(Boolean basicFlag) {
        this.basicFlag = basicFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getMainFlag() {
        return StringUtils.YES.equalsIgnoreCase(mainFlag);
    }

    public void setMainFlag(Boolean mainFlag) {
        this.mainFlag = mainFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getPersistFlag() {
        return StringUtils.YES.equalsIgnoreCase(persistFlag);
    }

    public void setPersistFlag(Boolean persistFlag) {
        this.persistFlag = persistFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getReadonlyFlag() {
        return StringUtils.YES.equalsIgnoreCase(readonlyFlag);
    }

    public void setReadonlyFlag(Boolean readonlyFlag) {
        this.readonlyFlag = readonlyFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getEditable() {
        return StringUtils.YES.equalsIgnoreCase(editable);
    }

    public void setEditable(Boolean editable) {
        this.editable = editable ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getAllLine() {
        return StringUtils.YES.equalsIgnoreCase(allLine);
    }

    public void setAllLine(Boolean allLine) {
        this.allLine = allLine ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getRequiredFlag() {
        return StringUtils.YES.equalsIgnoreCase(requiredFlag);
    }

    public void setRequiredFlag(Boolean requiredFlag) {
        this.requiredFlag = requiredFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getUpperFlag() {
        return StringUtils.YES.equalsIgnoreCase(upperFlag);
    }

    public void setUpperFlag(Boolean upperFlag) {
        this.upperFlag = upperFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getFromParent() {
        return StringUtils.YES.equalsIgnoreCase(fromParent);
    }

    public void setFromParent(Boolean fromParent) {
        this.fromParent = fromParent ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getQueryFlag() {
        return StringUtils.YES.equalsIgnoreCase(queryFlag);
    }

    public void setQueryFlag(Boolean queryFlag) {
        this.queryFlag = queryFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getExportFlag() {
        return StringUtils.YES.equalsIgnoreCase(exportFlag);
    }

    public void setExportFlag(Boolean exportFlag) {
        this.exportFlag = exportFlag ? StringUtils.YES : StringUtils.NO;
    }
}
