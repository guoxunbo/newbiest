package com.newbiest.base.ui.model;

import com.newbiest.base.model.NBBase;
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

    /**
     * 栏位是否显示
     */
    public static final int STYLE_DISPLAY = 1 << 0;

    /**
     * 栏位是否显示在基本信息中
     */
    public static final int STYLE_BASIC = 1 << 1;

    /**
     * 栏位是否在表格中显示
     */
    public static final int STYLE_MAIN = 1 << 2;

    /**
     * 栏位是否保存数据库
     */
    public static final int STYLE_PERSIST = 1 << 3;

    /**
     * 栏位是否是只读
     */
    public static final int STYLE_READONLY = 1 << 4;

    /**
     * 保存之后是否可编辑
     */
    public static final int STYLE_EDITABLE = 1 << 5;

    /**
     * 是否占用整行
     */
    public static final int STYLE_LINE = 1 << 6;

    /**
     * 必输
     */
    public static final int STYLE_REQUIRED = 1 << 7;

    /**
     * 是否自动转换成大写
     */
    public static final int STYLE_UPPER = 1 << 8;

    /**
     * 从父对象上取值 父对象必须为对象的field栏位中体现如user.name
     */
    public static final int STYLE_FROM_PARENT = 1 << 9;

    /**
     * 是否是查询栏位
     */
    public static final int STYLE_QUERY = 1 << 10;

    /**
     * 是否是导出栏位
     */
    public static final int STYLE_EXPORT = 1 << 11;

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
     * 栏位格式 默认选中显示和保存到数据库
     */
    @Column
    private Integer style = STYLE_DISPLAY | STYLE_PERSIST;

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
    private String label_zh;

    /**
     * 其它语言标签
     */
    @Column(name="LABEL_RES")
    private String label_res;


    @ManyToOne
    @JoinColumn(name = "TABLE_RRN", referencedColumnName = "OBJECT_RRN",
            insertable = false, updatable = false)
    private NBTable table;

    @ManyToOne
    @JoinColumn(name = "TAB_RRN", referencedColumnName = "OBJECT_RRN",
            insertable = false, updatable = false)
    private NBTab tab;

    public Boolean getDisplay() {
        return (style & STYLE_DISPLAY) == STYLE_DISPLAY;
    }

    public void setDisplay(Boolean display) {
        if (display) {
            style |= STYLE_DISPLAY;
        } else {
            style &= ~STYLE_DISPLAY;
        }
    }

    public Boolean getBasic() {
        return (style & STYLE_BASIC) == STYLE_BASIC;
    }

    public void getBasic(Boolean basic) {
        if (basic) {
            style |= STYLE_BASIC;
        } else {
            style &= ~STYLE_BASIC;
        }
    }

    public Boolean getMain() {
        return (style & STYLE_MAIN) == STYLE_MAIN;
    }

    public void setMain(Boolean main) {
        if (main) {
            style |= STYLE_MAIN;
        } else {
            style &= ~STYLE_MAIN;
        }
    }

    public Boolean getPersist() {
        return (style & STYLE_PERSIST) == STYLE_PERSIST;
    }

    public void setPersist(Boolean persist) {
        if (persist) {
            style |= STYLE_PERSIST;
        } else {
            style &= ~STYLE_PERSIST;
        }
    }

    public Boolean getReadonly() {
        return (style & STYLE_READONLY) == STYLE_READONLY;
    }

    public void setReadonly(Boolean readonly) {
        if (readonly) {
            style |= STYLE_READONLY;
        } else {
            style &= ~STYLE_READONLY;
        }
    }

    public Boolean getEditable() {
        return (style & STYLE_EDITABLE) == STYLE_EDITABLE;
    }

    public void setEditable(Boolean editable) {
        if (editable) {
            style |= STYLE_EDITABLE;
        } else {
            style &= ~STYLE_EDITABLE;
        }
    }

    public Boolean getLine() {
        return (style & STYLE_LINE) == STYLE_LINE;
    }

    public void setLine(Boolean line) {
        if (line) {
            style |= STYLE_LINE;
        } else {
            style &= ~STYLE_LINE;
        }
    }

    public Boolean getRequired() {
        return (style & STYLE_REQUIRED) == STYLE_REQUIRED;
    }

    public void setRequired(Boolean required) {
        if (required) {
            style |= STYLE_REQUIRED;
        } else {
            style &= ~STYLE_REQUIRED;
        }
    }

    public Boolean getUpper() {
        return (style & STYLE_UPPER) == STYLE_UPPER;
    }

    public void setUpper(Boolean upper) {
        if (upper) {
            style |= STYLE_UPPER;
        } else {
            style &= ~STYLE_UPPER;
        }
    }

    public Boolean getFromParent() {
        return (style & STYLE_FROM_PARENT) == STYLE_FROM_PARENT;
    }

    public void setFromParent(Boolean fromParent) {
        if (fromParent) {
            style |= STYLE_FROM_PARENT;
        } else {
            style &= ~STYLE_FROM_PARENT;
        }
    }

    public Boolean getQuery() {
        return (style & STYLE_QUERY) == STYLE_QUERY;
    }

    public void setQuery(Boolean query) {
        if (query) {
            style |= STYLE_QUERY;
        } else {
            style &= ~STYLE_QUERY;
        }
    }

    public Boolean getExport() {
        return (style & STYLE_EXPORT) == STYLE_EXPORT;
    }

    public void setExport(Boolean export) {
        if (export) {
            style |= STYLE_EXPORT;
        } else {
            style &= ~STYLE_EXPORT;
        }
    }

}
