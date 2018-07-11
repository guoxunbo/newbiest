package com.newbiest.base.model;

import com.newbiest.base.factory.ModelFactory;
import com.newbiest.base.utils.HistoryBeanConverter;
import com.newbiest.base.utils.PropertyUtils;
import com.newbiest.base.utils.SessionContext;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.cglib.beans.BeanCopier;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by guoxunbo on 2017/10/5.
 */
@MappedSuperclass
@Data
@NoArgsConstructor
public class NBHis extends NBUpdatable {

    public static final String TRANS_TYPE_CRAETE = "Create";
    public static final String TRANS_TYPE_UPDATE = "Update";
    public static final String TRANS_TYPE_DELETE = "Delete";

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

    public String getHisSeq() {
        return hisSeq;
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
        this.setHisSeq(sc.getTransRrn());
        this.setUpdatedBy(sc.getUsername());
        this.setCreatedBy(sc.getUsername());
    }
}
