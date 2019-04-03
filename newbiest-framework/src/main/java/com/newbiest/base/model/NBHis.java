package com.newbiest.base.model;

import com.newbiest.base.factory.ModelFactory;
import com.newbiest.base.utils.HistoryBeanConverter;
import com.newbiest.base.utils.PropertyUtils;
import com.newbiest.base.utils.SessionContext;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by guoxunbo on 2017/10/5.
 */
@MappedSuperclass
@Data
@NoArgsConstructor
public class NBHis extends NBUpdatable {

    public static final String TRANS_TYPE_CREATE = "Create";
    public static final String TRANS_TYPE_UPDATE = "Update";
    public static final String TRANS_TYPE_DELETE = "Delete";
    public static final String TRANS_TYPE_HOLD = "Hold";
    public static final String TRANS_TYPE_RELEASE = "Release";

    public static final Integer MAX_APPEND_COMMENT_LENGTH = 1000;

    @Column(name="HISTORY_SEQ")
    protected String hisSeq;

    @Column(name="TRANS_TYPE")
    protected String transType;

    @Column(name="ACTION_CODE")
    protected String actionCode;

    @Column(name="ACTION_REASON")
    protected String actionReason;

    @Column(name="ACTION_COMMENT")
    protected String actionComment;

    public NBHis(NBBase base, SessionContext sc) {
        setNbBase(base, sc);
    }

    public static NBHis getHistoryBean(NBBase nbBase) throws Exception{
        ClassLoader historyModelClassLoader = ModelFactory.getHistoryModelClass(nbBase.getClass().getName());
        String historyClassName = ModelFactory.getHistoryModelClassName(nbBase.getClass().getName());
        if (historyModelClassLoader != null) {
            NBHis nbHis = (NBHis) historyModelClassLoader.loadClass(historyClassName).newInstance();
            return nbHis;
        }
        return null;
    }

    public void setNbBase(NBBase base, SessionContext sc) {
        PropertyUtils.copyProperties(base, this, new HistoryBeanConverter());
        this.setObjectRrn(null);
        this.setOrgRrn(base.getOrgRrn());
        this.setHisSeq(sc.getTransRrn());
        this.setUpdatedBy(sc.getUsername());
        this.setCreatedBy(sc.getUsername());
    }
}
