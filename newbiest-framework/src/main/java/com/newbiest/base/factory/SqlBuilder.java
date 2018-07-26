package com.newbiest.base.factory;
import com.google.common.collect.ImmutableMap;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.PreConditionalUtils;
import com.newbiest.base.utils.SqlUtils;
import com.newbiest.security.model.NBOrg;

/**
 * Created by guoxunbo on 2018/7/4.
 */
public class SqlBuilder {

    public StringBuffer sqlBuffer = new StringBuffer();

    public SqlBuilder selectWithBasedCondition(Class clazz, long orgRrn) throws Exception {
        String entityName = clazz.getSimpleName();
        sqlBuffer.append("SELECT ");
        sqlBuffer.append(entityName);
        sqlBuffer.append(" FROM ");
        sqlBuffer.append(entityName);
        sqlBuffer.append(" ");
        sqlBuffer.append(entityName);
        this.where();
        if (NBBase.class.isAssignableFrom(clazz)) {
            this.and();
            this.buildBaseConditon();
        }

        return this;
    }

    public SqlBuilder where() throws Exception{
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" 1 = 1 ");
        return this;
    }

    /**
     * 栏位是key 值是value进行SQL拼接
     * @param immutableMap
     * @return
     */
    public SqlBuilder mapFieldValue(ImmutableMap<String, Object> immutableMap) throws Exception{
        PreConditionalUtils.checkNotNull(immutableMap, null);
        for (String field : immutableMap.keySet()) {
            this.and();
            sqlBuffer.append(field);
            sqlBuffer.append(" = ");
            sqlBuffer.append(SqlUtils.getValueByType(immutableMap.get(field)));
        }
        return this;
    }

    public SqlBuilder and() throws Exception{
        sqlBuffer.append(" AND ");
        return this;
    }

    public SqlBuilder buildBaseConditon() throws Exception{
        sqlBuffer.append(NBBase.BASE_CONDITION);
        return this;
    }

    public SqlBuilder delete(Class clazz) throws Exception {
        sqlBuffer.append(" DELETE FROM ");
        sqlBuffer.append(clazz.getSimpleName());
        where();
        return this;
    }

    public String buildString() throws Exception{
        return sqlBuffer.toString();
    }

    public StringBuffer build() throws Exception{
        return sqlBuffer;
    }
}
